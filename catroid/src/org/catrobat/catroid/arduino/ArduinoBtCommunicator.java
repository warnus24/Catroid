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
package org.catrobat.catroid.arduino;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.res.Resources;
import android.os.Handler;
import android.util.Log;

import org.catrobat.catroid.R;
import org.catrobat.catroid.bluetooth.BTConnectable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.UUID;

/**
 * @author Adrian Schnedlitz
 * 
 */

public class ArduinoBtCommunicator extends ArduinoCommunicator {

	private static final UUID SERIAL_PORT_SERVICE_CLASS_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fc");
	//	private static final byte START_OF_FILE = (byte) 83; //Ascii table "S"
	//	private static final byte END_OF_FILE = (byte) 88; //Ascii table "X"

	private static final byte BOF = (byte) 126; //Ascii table "~"

	private BluetoothAdapter btAdapter;
	private BluetoothSocket btSocket = null;
	private OutputStream outputStream = null;
	private InputStream inputStream = null;

	private String macAddress;
	private BTConnectable myOwner;
	private static boolean debugOutput = true;

	public ArduinoBtCommunicator(BTConnectable myOwner, Handler uiHandler, BluetoothAdapter btAdapter,
			Resources resources) {
		super(uiHandler, resources);

		this.myOwner = myOwner;
		this.btAdapter = btAdapter;
	}

	public void setMACAddress(String macAddress) {
		this.macAddress = macAddress;
	}

	@Override
	public void run() {

		try {
			createConnection();
		} catch (IOException e) {
		}

		while (connected) {
			try {
				receiveMessage();
			} catch (IOException e) {
				Log.d("ArduinoBtComm", "IOException in run:receiveMessage occured: " + e.toString());
				if (connected == true) {
					sendState(STATE_CONNECTERROR);
					connected = false;
				}
			} catch (Exception e) {
				Log.d("ArduinoBtComm", "Exception in run:receiveMessage occured: " + e.toString());
				if (connected == true) {
					sendState(STATE_CONNECTERROR);
					connected = false;
				}
			}
		}
	}

	@Override
	public void createConnection() throws IOException {
		try {
			BluetoothSocket btSocketTemporary;
			BluetoothDevice btDevice = null;
			btDevice = btAdapter.getRemoteDevice(macAddress);
			if (btDevice == null) {
				if (uiHandler == null) {
					throw new IOException();
				} else {
					sendToast(resources.getString(R.string.no_paired_nxt));
					sendState(STATE_CONNECTERROR);
					return;
				}
			}

			btSocketTemporary = btDevice.createRfcommSocketToServiceRecord(SERIAL_PORT_SERVICE_CLASS_UUID);

			try {
				btSocketTemporary.connect();

			} catch (IOException e) {
				if (myOwner.isPairing()) {
					if (uiHandler != null) {
						sendToast(resources.getString(R.string.pairing_message));
						sendState(STATE_CONNECTERROR_PAIRING);
					} else {
						throw e;
					}
					return;
				}

				//try another method for connection, this should work on the HTC desire, credits to Michael Biermann
				try {

					Method mMethod = btDevice.getClass().getMethod("createRfcommSocket", new Class[] { int.class });
					btSocketTemporary = (BluetoothSocket) mMethod.invoke(btDevice, Integer.valueOf(1));
					btSocketTemporary.connect();
				} catch (Exception e1) {
					if (uiHandler == null) {
						throw new IOException();
					} else {
						sendState(STATE_CONNECTERROR);
					}
					return;
				}
			}
			btSocket = btSocketTemporary;
			inputStream = btSocket.getInputStream();
			outputStream = btSocket.getOutputStream();
			connected = true;
		} catch (IOException e) {
			if (uiHandler == null) {
				throw e;
			} else {
				if (myOwner.isPairing()) {
					sendToast(resources.getString(R.string.pairing_message));
				}
				sendState(STATE_CONNECTERROR);
				return;
			}
		}
		// everything was OK
		if (uiHandler != null) {
			sendState(STATE_CONNECTED);
		}
	}

	@Override
	public void destroyConnection() throws IOException {

		Log.d("ArduinoBtComm", "destroyArduinoConnection");

		try {
			if (btSocket != null) {
				connected = false;
				btSocket.close();
				btSocket = null;
			}

			inputStream = null;
			outputStream = null;

		} catch (IOException e) {
			if (uiHandler == null) {
				throw e;
			} else {
				sendToast(resources.getString(R.string.problem_at_closing));
			}
		}
	}

	@Override
	public void stopSensors() {
		pauseArduinoBoard();
	}

	/**
	 * Sends a message on the opened OutputStream
	 * 
	 * @param message
	 *            , the message as a byte array
	 */
	@Override
	public void sendMessage(byte[] message) throws IOException {

		try {
			if (outputStream == null) {
				throw new IOException();
			}
			outputStream.write(message, 0, message.length);
			outputStream.flush();
		} catch (Exception e) {
			Log.d("ArduinoBtComm", "ERROR: Exception occured in sendMessage " + e.getMessage());
		}
	}

	/**
	 * Receives a message on the opened InputStream
	 * 
	 * @return the message
	 */
	@Override
	public byte[] receiveMessage() throws IOException, Exception {

		if (inputStream == null) {
			throw new IOException(" Software caused connection abort ");
		}

		@SuppressWarnings("unused")
		int read = 0;
		byte[] buf = new byte[1];

		do {
			checkIfDataIsAvailable(1);
			read = inputStream.read(buf);
		} while (buf[0] != BOF);

		byte[] length = new byte[1];
		//checkIfDataIsAvailable(1);
		read = inputStream.read(length);

		byte[] buffer = new byte[length[0] - 1];
		//checkIfDataIsAvailable(length[0] - 1);
		read = inputStream.read(buffer);

		switch (buffer[0]) {
			case 'D':
				sensors.setArduinoDigitalSensor(buffer[3]);

				if (debugOutput == true) {
					Log.d("ArduinoBtComm", "sensor packet found");
					Log.d("ArduinoBtComm", "receiveMessage: Value=" + buffer[3]);
				}
				break;
			case 'A':
				sensors.setArduinoAnalogSensor(buffer[3]);

				if (debugOutput == true) {
					Log.d("ArduinoBtComm", "sensor packet found");
					Log.d("ArduinoBtComm", "receiveMessage: Value=" + buffer[3]);
				}
				break;
			default:
				Log.d("ArduinoBtComm", "Unknown Command! id = " + buffer[0]);
				break;
		}
		return buffer;
	}

	public void checkIfDataIsAvailable(int neededBytes) throws IOException {
		int available = 0;
		long timeStart = System.currentTimeMillis();
		long timePast;

		while (true) {
			if (inputStream == null) {
				throw new IOException(" Software caused connection abort ");
			}
			available = inputStream.available();
			if (available >= neededBytes) {
				break;
			}
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			// here you can optionally check elapsed time, and time out
			timePast = System.currentTimeMillis();
			if ((timePast - timeStart) > 16000) {
				Log.d("Arduino-Timeout", "TIMEOUT for receive message occured");
				throw new IOException(" Software caused connection abort because of timeout");
			}
		}
	}
}
