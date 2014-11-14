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
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.UUID;

public class BluetoothConnectionWrapper extends BluetoothConnection {


	private boolean executeLocal;

	private PipedOutputStream wrappedOutputStream;
	private PipedInputStream wrappedInputStream;

	private ClientHandlerThread clientHandlerThread;

	private Queue<byte[]> sentMessages = new LinkedList<byte[]>();
	private Queue<byte[]> receivedMessages = new LinkedList<byte[]>();

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

	public BluetoothConnectionWrapper(String macAddress, UUID uuid, BTClientHandler handler) {
		super(macAddress, uuid);
		this.executeLocal = true;

		PipedInputStream serverInputStreamFromClientsOutputStream = new PipedInputStream();
		PipedOutputStream serverOutputStreamToClientsInputStream = new PipedOutputStream();

		wrappedInputStream = new WrappedInputStream();
		wrappedOutputStream = new WrappedOutputStream();

		try {
			serverInputStreamFromClientsOutputStream.connect(wrappedOutputStream);
			serverOutputStreamToClientsInputStream.connect(wrappedInputStream);

		} catch (IOException e) {
			e.printStackTrace();
		}

		clientHandlerThread = new ClientHandlerThread(handler, serverInputStreamFromClientsOutputStream, serverOutputStreamToClientsInputStream);
		clientHandlerThread.start();
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
		return getNextSentMessage(0);
	}

	public byte[] getNextSentMessage(int messageOffset){
		for (int i = 0; i < messageOffset; i++) {
			this.sentMessages.poll();
		}
			return this.sentMessages.poll();
	}

	public ArrayList<byte[]> getSentMessages() {
		return getSentMessages(0, true);
	}

	public ArrayList<byte[]> getSentMessages(int messageByteOffset, boolean clearMessageQueue) {

		ArrayList<byte[]> messages = new ArrayList<byte[]>();

		if (messageByteOffset != 0) {
			for (byte[] sentCommand : sentMessages) {
				messages.add(getSubArray(sentCommand, messageByteOffset));
			}
		}
		else {
			for (byte[] sentCommand : sentMessages) {
				messages.add(sentCommand);
			}
		}

		if (clearMessageQueue) {
			sentMessages.clear();
		}

		return messages;
	}

	public byte[] getNextReceivedMessage() {
		return getNextReceivedMessage(0);
	}

	public byte[] getNextReceivedMessage(int messageOffset) {
		for (int i = 0; i < messageOffset; i++) {
			this.receivedMessages.poll();
		}
		return this.receivedMessages.poll();
	}

	public ArrayList<byte[]> getReceivedMessages() {
		return getReceivedMessages(true);
	}

	public ArrayList<byte[]> getReceivedMessages(boolean clearMessageQueue) {

		ArrayList<byte[]> messages = new ArrayList<byte[]>();

		for (byte[] receivedCommand : receivedMessages) {
			messages.add(receivedCommand);
		}

		if (clearMessageQueue) {
			receivedMessages.clear();
		}

		return messages;
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

	private class WrappedInputStream extends PipedInputStream {

		public InputStream inputStream;

		private WrappedInputStream(InputStream inputStream) {
			this.inputStream = inputStream;
		}

		public WrappedInputStream() {
			this.inputStream = this;
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
			int numOfReadBytes = super.read(buffer, byteOffset, byteCount);
			receivedMessages.add(getSubArray(buffer, byteOffset, byteCount));

			return numOfReadBytes;
		}
	}

	private class WrappedOutputStream extends PipedOutputStream {

		private OutputStream outputStream;

		public WrappedOutputStream(OutputStream outputStream) {
			this.outputStream = outputStream;
		}

		public WrappedOutputStream() {
			this.outputStream = this;
		}

		@Override
		public synchronized void write(byte[] buffer) throws IOException {
			outputStream.write(buffer);
			sentMessages.add(buffer);
		}

		@Override
		public synchronized void write(byte[] buffer, int offset, int count) throws IOException {
			outputStream.write(buffer, offset, count);
			sentMessages.add(getSubArray(buffer, offset, count));
		}

		@Override
		public synchronized void write(int i) throws IOException {
			outputStream.write(i);
			sentMessages.add(intToByteArray(i));
		}
	}

	private static final int INT_SIZE = Integer.SIZE / Byte.SIZE;
	private static byte[] intToByteArray(int i) {
		byte[] buffer = new byte[INT_SIZE];
		for (int j = 0; j < INT_SIZE; j++) {
			buffer[j] = (byte)(0xFF & (i >> j * Byte.SIZE));
		}

		return buffer;
	}

	private static byte[] getSubArray(byte[] buffer, int offset) {
		return getSubArray(buffer, offset, buffer.length - offset);
	}

	private static byte[] getSubArray(byte[] buffer, int offset, int count) {
		Assert.assertTrue("count can't be negativ", count >= 0);
		Assert.assertTrue("wrong offset or count", buffer.length - offset >= count);

		byte[] subArray = new byte[count];

		for (int i = 0; i < count; i++) {
			subArray[i] = buffer[i + offset];
		}

		return subArray;
	}

	public static interface BTClientHandler {
		public abstract void handle(InputStream inStream, OutputStream outStream) throws IOException;
	}

	public static class ClientHandlerThread extends Thread {
		public static final String TAG = CommonBluetoothTestClientHandler.class.getSimpleName();

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
				Log.e(TAG, "stream closed");
			}
		}

		public void stopHandler() {
			try {
				inStream.close();
				outStream.close();
			} catch (IOException e) {
				Log.e(TAG, "error while closing the stream");
			}

		}

	}

	public static class CommonBluetoothTestClientHandler implements BTClientHandler {

		@Override
		public void handle(InputStream inStream, OutputStream outStream) throws IOException {
			byte[] messageLengthBuffer = new byte[1];

			while (inStream.read(messageLengthBuffer, 0, 1) != -1) {
				int expectedMessageLength = messageLengthBuffer[0];
				handleClientMessage(expectedMessageLength, inStream, outStream);
			}
		}

		private void handleClientMessage(int expectedMessageLength, InputStream inStream, OutputStream outStream) throws IOException {

			byte[] payload = new byte[expectedMessageLength];

			inStream.read(payload, 0, expectedMessageLength);

			byte[] testResult = payload;

			outStream.write(testResult.length);
			outStream.write(testResult);
			outStream.flush();
		}

	}
}
