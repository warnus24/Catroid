/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2014 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.test.utils;


import android.util.Log;

import junit.framework.Assert;

import org.catrobat.catroid.bluetooth.BluetoothConnection;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.UUID;

public class BluetoothConnectionWrapper extends BluetoothConnection {

	private static final String TAG = BluetoothConnectionWrapper.class.getSimpleName();

	private boolean executeLocal;

	private OutputStream wrappedOutputStream;
	private InputStream wrappedInputStream;

	private ClientHandlerThread clientHandlerThread;

	private Queue<byte[]> sentMessages = new LinkedList<byte[]>();
	private Queue<byte[]> receivedMessages = new LinkedList<byte[]>();

	public BluetoothConnectionWrapper() {
		this(null, null, true);
	}

	public BluetoothConnectionWrapper(String macAddress, UUID uuid, boolean executLocal) {
		super(macAddress, uuid);
		this.executeLocal = executLocal;

		if (executLocal == true) {

			wrappedInputStream = new WrappedInputStream(new InputStream() {
				@Override
				public int read() throws IOException {
					return 0;
				}
			});

			wrappedOutputStream = new WrappedOutputStream(new OutputStream() {
				@Override
				public void write(int i) throws IOException {
				}
			});
		}
	}

	public BluetoothConnectionWrapper(BTClientHandler handler) {
		this(null, null, handler);
	}

	public BluetoothConnectionWrapper(String macAddress, UUID uuid, BTClientHandler handler) {
		super(macAddress, uuid);
		this.executeLocal = true;

		PipedInputStream serverInputStreamFromClientsOutputStream = new PipedInputStream();
		PipedOutputStream serverOutputStreamToClientsInputStream = new PipedOutputStream();

		PipedInputStream pipedInputStreamForClient = new PipedInputStream();
		PipedOutputStream pipedOutputStreamForClient = new PipedOutputStream();

		try {
			serverInputStreamFromClientsOutputStream.connect(pipedOutputStreamForClient);
			serverOutputStreamToClientsInputStream.connect(pipedInputStreamForClient);

			wrappedInputStream = new WrappedInputStream(pipedInputStreamForClient);
			wrappedOutputStream = new WrappedOutputStream(pipedOutputStreamForClient);

			clientHandlerThread = new ClientHandlerThread(handler, serverInputStreamFromClientsOutputStream, serverOutputStreamToClientsInputStream);

		} catch (IOException e) {
			Assert.fail("Error with ConnectionWrapper Stream pipes.");
		}
	}



	public void startClientHandlerThread() {
		if (clientHandlerThread != null) {
			clientHandlerThread.start();
		}
	}

	public void stopClientHandlerThread() {
		if (clientHandlerThread != null) {
			clientHandlerThread.stopHandler();
		}
	}



	public byte[] getNextSentMessage() {
		return getNextSentMessage(0, 0);
	}

	public byte[] getNextSentMessage(int messageOffset){
		return getNextSentMessage(messageOffset, 0);
	}

	public byte[] getNextSentMessage(int messageOffset, int messageByteOffset) {
		return getNextMessage(sentMessages, messageOffset, messageByteOffset);
	}



	public ArrayList<byte[]> getSentMessages() {
		return getSentMessages(0, true);
	}

	public ArrayList<byte[]> getSentMessages(int messageByteOffset, boolean clearMessageQueue) {
		return getMessages(sentMessages, messageByteOffset, clearMessageQueue);
	}



	public byte[] getNextReceivedMessage() {
		return getNextReceivedMessage(0, 0);
	}

	public byte[] getNextReceivedMessage(int messageOffset) {
		return getNextReceivedMessage(messageOffset, 0);
	}

	public byte[] getNextReceivedMessage(int messageOffset, int messageByteOffset) {
		return getNextMessage(receivedMessages, messageOffset, messageByteOffset);
	}



	public ArrayList<byte[]> getReceivedMessages() {
		return getReceivedMessages(0, true);
	}

	public ArrayList<byte[]> getReceivedMessages(int messageByteOffset, boolean clearMessageQueue) {
		return getMessages(receivedMessages, messageByteOffset, clearMessageQueue);
	}



	private static byte[] getNextMessage(Queue<byte[]> messages, int messageOffset, int messageByteOffset) {
		for (int i = 0; i < messageOffset; i++) {
			messages.poll();
		}
		return getSubArray(messages.poll(), messageByteOffset);
	}

	private static ArrayList<byte[]> getMessages(Queue<byte[]> messages, int messageByteOffset, boolean clearMessageQueue) {

		ArrayList<byte[]> m = new ArrayList<byte[]>();

		for (byte[] message : messages) {
			m.add(getSubArray(message, messageByteOffset));
		}

		if (clearMessageQueue) {
			messages.clear();
		}

		return m;
	}



	@Override
	public InputStream getInputStream() throws IOException {

		if (wrappedInputStream == null) {
			wrappedInputStream = new WrappedInputStream(super.getInputStream());
		}

		return wrappedInputStream;
	}

	@Override
	public OutputStream getOutputStream() throws IOException {

		if (wrappedOutputStream == null) {
			wrappedOutputStream = new WrappedOutputStream(super.getOutputStream());
		}

		return wrappedOutputStream;
	}

	@Override
	public State connect() {

		if (executeLocal) {
			return State.CONNECTED;
		}

		return super.connect();
	}

	@Override
	public void disconnect() {
		if (executeLocal) {
			try {
				wrappedOutputStream.close();
				wrappedInputStream.close();
			}
			catch (IOException e) {
				Log.e(TAG, "Error on disconnect while closing streams");
			}
			stopClientHandlerThread();
			return;
		}

		super.disconnect();
	}

	private final class WrappedInputStream extends InputStream {

		public InputStream inputStream;

		private WrappedInputStream(InputStream inputStream) {
			this.inputStream = inputStream;
		}

		@Override
		public int read() throws IOException {
			int readByte = inputStream.read();
			receivedMessages.add(intToByteArray(readByte));

			return readByte;
		}

		@Override
		public int read(byte[] buffer) throws IOException {
			int numOfReadBytes = inputStream.read(buffer);
			receivedMessages.add(buffer);
			return numOfReadBytes;
		}

		@Override
		public int read(byte[] buffer, int byteOffset, int byteCount) throws IOException {
			int numOfReadBytes = inputStream.read(buffer, byteOffset, byteCount);
			receivedMessages.add(getSubArray(buffer, byteOffset, byteCount));
			return numOfReadBytes;
		}

		@Override
		public void close() throws IOException {
			super.close();
			inputStream.close();
		}
	}

	private final class WrappedOutputStream extends OutputStream {

		private OutputStream outputStream;

		public WrappedOutputStream(OutputStream outputStream) {
			this.outputStream = outputStream;
		}

		@Override
		public void write(byte[] buffer) throws IOException {
			outputStream.write(buffer);
			sentMessages.add(buffer);
		}

		@Override
		public void write(byte[] buffer, int offset, int count) throws IOException {
			outputStream.write(buffer, offset, count);
			sentMessages.add(getSubArray(buffer, offset, count));
		}

		@Override
		public void write(int i) throws IOException {
			outputStream.write(i);
			sentMessages.add(intToByteArray(i));
		}

		@Override
		public void close() throws IOException {
			super.close();
			outputStream.close();
		}
	}

	private static byte[] intToByteArray(int i) {
		return ByteBuffer.allocate(4).putInt(i).array();
	}

	private static byte[] getSubArray(byte[] buffer, int offset) {
		if (buffer == null) {
			return null;
		}

		return Arrays.copyOfRange(buffer, offset, buffer.length);
	}

	private static byte[] getSubArray(byte[] buffer, int offset, int count) {
		if (buffer == null) {
			return null;
		}

		Assert.assertTrue("count can't be negative", count >= 0);
		Assert.assertTrue("wrong offset or count", buffer.length - offset >= count);

		return Arrays.copyOfRange(buffer, offset, offset + count);
	}

	public static interface BTClientHandler {
		public abstract void handle(InputStream inStream, OutputStream outStream) throws IOException;
	}

	public static class ClientHandlerThread extends Thread {
		public static final String TAG = BTClientHandler.class.getSimpleName();

		private BTClientHandler handler;
		private InputStream inStream;
		private OutputStream outStream;

		public ClientHandlerThread(BTClientHandler handler, InputStream inStream, OutputStream outStream) {
			this.handler = handler;
			this.inStream = inStream;
			this.outStream = outStream;
		}

		@Override
		public void run() {
			try {
				handler.handle(inStream, outStream);
			} catch (IOException e) {
				Log.e(TAG, "stream closed.");
			}
		}

		public void stopHandler() {
			try {
				outStream.close();
				inStream.close();
			} catch (IOException e) {
				Log.e(TAG, "error while closing the stream");
			}

		}

	}
}
