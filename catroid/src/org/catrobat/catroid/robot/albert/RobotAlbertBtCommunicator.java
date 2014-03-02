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
 *    
 *    This file incorporates work covered by the following copyright and  
 *    permission notice: 
 *    
 *		   	Copyright 2010 Guenther Hoelzl, Shawn Brown
 *
 *		   	This file is part of MINDdroid.
 *
 * 		  	MINDdroid is free software: you can redistribute it and/or modify
 * 		  	it under the terms of the GNU Affero General Public License as
 * 		  	published by the Free Software Foundation, either version 3 of the
 *   		License, or (at your option) any later version.
 *
 *   		MINDdroid is distributed in the hope that it will be useful,
 *   		but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   		MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   		GNU Affero General Public License for more details.
 *
 *   		You should have received a copy of the GNU Affero General Public License
 *   		along with MINDdroid.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.robot.albert;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.res.Resources;
import android.os.Handler;
import android.util.Log;

import org.catrobat.catroid.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

//This code is based on the nxt-implementation
public class RobotAlbertBtCommunicator extends RobotAlbertCommunicator {

	private static final UUID SERIAL_PORT_SERVICE_CLASS_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fc");
	private static final String TAG = RobotAlbertBtCommunicator.class.getSimpleName();
	private static final String NOTHING_ON_STREAM_ERROR_STRING = "Nothing on Stream, even tough it was 'available'";

	private static final byte PACKET_HEADER_1 = (byte) 0xAA;
	private static final byte PACKET_HEADER_2 = 0x55;
	private static final byte PACKET_TAIL_1 = 0x0D;
	private static final byte PACKET_TAIL_2 = 0x0A;
	private static final byte COMMAND_SENSOR = 0x06;
	private static final byte COMMAND_EXTERNAL = 0x20;
	private static final int STREAM_ERROR = -1;

	private static boolean debugOutput = false;

	private BluetoothAdapter btAdapter;
	private BluetoothSocket btSocket;
	private OutputStream outputStream;
	private InputStream inputStream;
	private String macAddress;
	private RobotAlbert myOwner;

	public RobotAlbertBtCommunicator(RobotAlbert myOwner, Handler uiHandler, BluetoothAdapter btAdapter,
			Resources resources) {
		super(uiHandler, resources);

		this.myOwner = myOwner;
		this.btAdapter = btAdapter;
	}

	public void setMACAddress(String mMACaddress) {
		this.macAddress = mMACaddress;
	}

	@Override
	public void run() {
		try {
			createConnection();
		} catch (IOException ioException) {
			Log.e(TAG, "IOException in run:receiveMessage occurred: ", ioException);
		}

		while (connected) {
			try {
				receiveMessage();
			} catch (IOException ioException) {
				Log.e(TAG, "IOException in run:receiveMessage occurred: ", ioException);
				if (connected) {
					sendState(STATE_CONNECT_ERROR);
					connected = false;
				}
			}
		}
	}

	@Override
	public void createConnection() throws IOException {
	
		BluetoothSocket btSocketTemporary;
		BluetoothDevice btDevice;
		btDevice = btAdapter.getRemoteDevice(macAddress);
		btSocketTemporary = btDevice.createRfcommSocketToServiceRecord(SERIAL_PORT_SERVICE_CLASS_UUID);

		try {
			btSocketTemporary.connect();
		} catch (IOException ioException) {
			//try another method for connection, this should work on the HTC desire, credits to Michael Biermann
			try {
				Method mMethod = btDevice.getClass().getMethod("createRfcommSocket", new Class[] {
						int.class });
				btSocketTemporary = (BluetoothSocket) mMethod.invoke(btDevice, 1);
				btSocketTemporary.connect();
			} catch (NoSuchMethodException noSuchMethodException) {
				throw new IOException();
			} catch (InvocationTargetException invocationTargetException) {
				throw new IOException();
			} catch (IllegalAccessException illegalAccessException) {
				throw new IOException();
			}
		}
		btSocket = btSocketTemporary;
		inputStream = btSocket.getInputStream();
		outputStream = btSocket.getOutputStream();
		connected = true;
		// everything was OK
		if (uiHandler != null) {
			sendState(STATE_CONNECTED);
		}
	}

	@Override
	public void destroyConnection() throws IOException {
		Log.d(TAG, "destroyRobotAlbertConnection");
		if (connected) {
			stopAllMovement();
		}

		try {
			if (btSocket != null) {
				connected = false;
				btSocket.close();
				btSocket = null;
			}
			inputStream = null;
			outputStream = null;
		} catch (IOException ioException) {
			if (uiHandler == null) {
				throw ioException;
			} else {
				sendToast(resources.getString(R.string.problem_at_closing));
			}
		}
	}

	@Override
	public void stopAllMovement() {
		myHandler.removeMessages(0);
		myHandler.removeMessages(1);
		myHandler.removeMessages(2);
		resetRobotAlbert();
	}

	/**
	 * Sends a message on the opened OutputStream
	 * 
	 * @param message
	 *            , the message as a byte array
	 */
	@Override
	public void sendMessage(byte[] message) throws IOException {
			if (outputStream == null) {
				throw new IOException("Output Stream was null");
			}
			outputStream.write(message, 0, message.length);
			outputStream.flush();
	}

	/**
	 * Receives a message on the opened InputStream
	 * 
	 * @return the message
	 */
	@Override
	public byte[] receiveMessage() throws IOException {
		if (inputStream == null) {
			throw new IOException(" Software caused connection abort ");
		}

		int read;
		byte[] buf = new byte[1];

		int count = 0;
		do {
			do {
				checkIfDataIsAvailable(1);
				read = inputStream.read(buf);
				if (read == STREAM_ERROR) {
					Log.e(TAG, NOTHING_ON_STREAM_ERROR_STRING);
					return null;
				}

				count++;
				if (count > 400) {
					return null;
				}
			} while (buf[0] != PACKET_HEADER_1);

			checkIfDataIsAvailable(1);
			read = inputStream.read(buf);
			if (read == STREAM_ERROR) {
				Log.e(TAG, NOTHING_ON_STREAM_ERROR_STRING);
				return null;
			}
		} while (buf[0] != PACKET_HEADER_2);

		byte[] length = new byte[1];
		checkIfDataIsAvailable(1);
		read = inputStream.read(length);
		if (read == STREAM_ERROR) {
			Log.e(TAG, NOTHING_ON_STREAM_ERROR_STRING);
			return null;
		}

		byte[] buffer = new byte[length[0] - 1];
		checkIfDataIsAvailable(length[0] - 1);
		read = inputStream.read(buffer);
		if (read == STREAM_ERROR) {
			Log.e(TAG, NOTHING_ON_STREAM_ERROR_STRING);
			return null;
		}

		if (buffer[length[0] - 3] != PACKET_TAIL_1 || buffer[length[0] - 2] != PACKET_TAIL_2) {
			Log.e(TAG, "ERROR: Packet tail not found!");
			return null;
		}

		switch (buffer[0]) {
			case COMMAND_SENSOR:

				int leftDistance = (buffer[11] + buffer[13] + buffer[15] + buffer[17]) / 4;
				int rightDistance = (buffer[10] + buffer[12] + buffer[14] + buffer[16]) / 4;

				if (leftDistance > 25 || rightDistance > 25) {
					int divisor1 = 0;
					int divisor2 = 0;
					for (int i = 11; i < 19; i += 2) {
						if (buffer[i] != 0) {
							divisor1++;
						}
						if (buffer[i + 1] != 0) {
							divisor2++;
						}
					}
					if (divisor1 == 0) {
						divisor1 = 1;
					}
					if (divisor2 == 0) {
						divisor2 = 1;
					}
					leftDistance = (buffer[11] + buffer[13] + buffer[15] + buffer[17]) / divisor2;
					rightDistance = (buffer[10] + buffer[12] + buffer[14] + buffer[16]) / divisor1;
				}

				sensors.setValueOfLeftDistanceSensor(leftDistance);
				sensors.setValueOfRightDistanceSensor(rightDistance);

				if (debugOutput) {
					Log.d(TAG, "sensor packet found");
					Log.d(TAG, "receiveMessage:  leftDistance=" + leftDistance);
					Log.d(TAG, "receiveMessage: rightDistance=" + rightDistance);
				}

				break;
			case COMMAND_EXTERNAL:
				Log.d(TAG, "External Packet received!");
				break;

			default:
				Log.d(TAG, "Unknown Command! id = " + buffer[0]);
				break;
		}
		return buffer;
	}

	private void checkIfDataIsAvailable(int neededBytes) throws IOException {
		int available;
		long timeStart = System.currentTimeMillis();
		long timePast;

		while (true) {
			if (inputStream == null) {
				Log.e(TAG, "Stream was null");
				throw new IOException(" Software caused connection abort ");
			}
			available = inputStream.available();
			if (available >= neededBytes) {
				break;
			}
			try {
				Thread.sleep(1);
			} catch (InterruptedException interruptedException) {
				Log.e(TAG, "Thread interrupted", interruptedException);
			}
			// here you can optionally check elapsed time, and time out
			timePast = System.currentTimeMillis();
			if ((timePast - timeStart) > 16000) {
				Log.e(TAG, "TIMEOUT for receive message occurred");
				throw new IOException(" Software caused connection abort because of timeout");
			}
		}
	}
}
