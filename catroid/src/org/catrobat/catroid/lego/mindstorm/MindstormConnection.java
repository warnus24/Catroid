/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.lego.mindstorm;

import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import org.catrobat.catroid.bluetooth.BluetoothConnection;
import org.catrobat.catroid.lego.mindstorm.nxt.NXTError;
import org.catrobat.catroid.lego.mindstorm.nxt.NXTException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class MindstormConnection {

	private static final UUID SERIAL_PORT_SERVICE_CLASS_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	public static final int DISPLAY_TOAST = 1000;

	private Handler receiveHandler;

	public MindstormConnection(Handler receiveHandler) {
		this.receiveHandler = receiveHandler;
	}

	public void connect(String macAddress) throws IOException{
		BluetoothConnection bluetoothConnection = new BluetoothConnection(macAddress,
				SERIAL_PORT_SERVICE_CLASS_UUID);
		BluetoothConnection.State state = bluetoothConnection.connect();

		switch (state) {
			case CONNECTED:
				break;
			case ERROR_NOT_BONDED:
			case ERROR_STILL_BONDING:
				sendToast("No paired NXT found");
			default:
				sendState(BluetoothConnection.State.NOT_CONNECTED);
				throw new IOException("Bluetooth connecting error " + state.name());
		}

		bluetoothSocket = bluetoothConnection.getBluetoothSocket();
		nxtInputStream = bluetoothSocket.getInputStream();
		nxtOutputStream = bluetoothSocket.getOutputStream();

		isConnected = true;

		sendState(BluetoothConnection.State.CONNECTED);
	}

	private BluetoothSocket bluetoothSocket = null;
	private OutputStream nxtOutputStream = null;
	private InputStream nxtInputStream = null;

	private boolean isConnected = false;

	public boolean isConnected() {
		return isConnected;
	}



	public void disconnect() throws IOException{

		isConnected = false;

		if (bluetoothSocket != null) {
			bluetoothSocket.close();
			bluetoothSocket = null;
		}

		nxtInputStream = null;
		nxtOutputStream = null;
	}

	synchronized public byte[] sendAndReceive(MindstormCommand command) {
		send(command);
		return receive();
	}

	public void send(MindstormCommand command) {
		try {
			int messageLength = command.getLength();
			byte[] message = command.getRawCommand();
			byte[] data = new byte[command.getLength() + 2];
			data[0] = (byte)(messageLength & 0x00FF);
			data[1] = (byte)((messageLength & 0xFF00) >> 8);

			System.arraycopy(message, 0, data, 2, messageLength);

            synchronized (nxtOutputStream) {
                nxtOutputStream.write(data, 0, messageLength + 2);
                nxtOutputStream.flush();
            }

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private byte[] receive() {
		byte[] data = new byte[2];
		byte[] payload;
		int expectedLength = 0;
		int replyLength = 0;
		try{
			expectedLength = 2;
            synchronized (nxtInputStream) {
                replyLength = nxtInputStream.read(data, 0, 2);
                expectedLength = ((data[0] & 0xFF) | (data[1] & 0xFF) << 8);
                payload = new byte[expectedLength];
                replyLength = 0;
                replyLength = nxtInputStream.read(payload, 0, expectedLength);
            }
		}
		catch (IOException e){
			if( replyLength == 0){
				throw new NXTException("No Reply");
			}
			else if(replyLength != expectedLength){
				throw new MindstormException("Wrong Number of Bytes");
			}
			throw new MindstormException("Read Error");
		}

		return payload;
	}

	protected void sendState(BluetoothConnection.State message) {
		Bundle myBundle = new Bundle();
		myBundle.putInt("message", message.ordinal());
		sendBundle(myBundle);
	}

	protected void sendToast(String toastText) {
		Bundle myBundle = new Bundle();
		myBundle.putInt("message", DISPLAY_TOAST);
		myBundle.putString("toastText", toastText);
		sendBundle(myBundle);
	}

	protected void sendBundle(Bundle bundle) {
		if (receiveHandler != null) {
			Message msg = new Message();
			msg.setData(bundle);
			receiveHandler.sendMessage(msg);
		}
	}
}
