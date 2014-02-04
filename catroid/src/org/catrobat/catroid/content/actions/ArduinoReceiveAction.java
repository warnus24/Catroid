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
package org.catrobat.catroid.content.actions;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import com.badlogic.gdx.scenes.scene2d.Action;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * @author Adrian
 * 
 */
public class ArduinoReceiveAction extends Action {

	private int pinNumber;
	private Boolean pinValue;
	public static int ERROR_OK = 0;

	//TODO change this
	private static String MACaddr = "00:07:80:49:8B:61"; //MAC address of the Arduino BT-board
	public static UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

	private static BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	private static BluetoothDevice bluetoothDevice = bluetoothAdapter.getRemoteDevice(MACaddr);;
	private static BluetoothSocket bluetoothSocket = null;
	private static BluetoothSocket tmpSocket = null;
	private static InputStream bluetoothInputStream = null;

	//Needed to init BT at the first call
	private static Boolean isBluetoothinitialized = false;

	public int getPinNumber() {
		return pinNumber;
	}

	public boolean getPinValue() {
		return pinValue;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.badlogic.gdx.scenes.scene2d.Action#act(float)
	 */
	@Override
	public boolean act(float delta) {
		// TODO Auto-generated method stub
		if (!isBluetoothinitialized) {
			this.initBluetoothConnection();
		} else {
			//TODO send stuff
		}
		return false;
	}

	public static int initBluetoothConnection() {
		if (bluetoothAdapter == null) {
			ERROR_OK = -1;
			return ERROR_OK;
		}

		//check if the Arduino Board is on the bonded devices list
		if (bluetoothDevice == null) {
			ERROR_OK = -2;
			return ERROR_OK;
		}

		//enable the Bluetooth adapter
		bluetoothAdapter.enable();
		if (!bluetoothAdapter.isEnabled()) {
			ERROR_OK = -3;
		}

		//create an outgoing Bluetooth Socket
		try {
			tmpSocket = bluetoothDevice.createRfcommSocketToServiceRecord(myUUID);
			ERROR_OK = 1;
		} catch (IOException e) {
			return -4;
		}

		bluetoothSocket = tmpSocket;
		bluetoothAdapter.cancelDiscovery();

		isBluetoothinitialized = true;
		return ERROR_OK;
	}

	//for testing only
	//I checked the Manifest-file, Bluetooth-permissions are available!
	public static void turnOnBluetooth() {
		bluetoothAdapter.enable();
		if (!bluetoothAdapter.isEnabled()) {
			ERROR_OK = -3;
		}
	}

	//

	public static BluetoothSocket getBluetoothSocket() {
		return bluetoothSocket;
	}

	public static int receiveDataViaBluetoothSocket(BluetoothSocket inputBluetoothSocket) {

		try {
			inputBluetoothSocket.connect();
		} catch (IOException e) {
			//			try {
			//				inputBluetoothSocket.close();
			//			} catch (IOException e1) {
			//				return -5;
			//			}
		}

		try {
			bluetoothInputStream = inputBluetoothSocket.getInputStream();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//receive data here via inputStream
		int inputMessage = 0;
		try {
			inputMessage = bluetoothInputStream.read();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			inputBluetoothSocket.close();
		} catch (IOException e1) {
			return -5;
		}

		return inputMessage;
	}

}
