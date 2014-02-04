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
import java.io.OutputStream;
import java.util.UUID;

/**
 * @author manuelzoderer, Adrian Schnedlitz
 * 
 */
public class ArduinoSendAction extends Action {

	private char pinNumberHigherByte, pinNumberLowerByte;
	private char pinValue;

	public static int ERROR_OK = 0;

	//Needed to init BT at the first call
	private static Boolean isBluetoothinitialized = false;

	//TODO change this
	private static String MACaddr = "00:07:80:49:8B:61"; //MAC address of the Arduino BT-board
	public static UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

	private static BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	private static BluetoothDevice bluetoothDevice = bluetoothAdapter.getRemoteDevice(MACaddr);;
	private static BluetoothSocket bluetoothSocket = null;
	private static BluetoothSocket tmpSocket = null;
	private static OutputStream bluetoothOutputStream = null;

	public void setPinNumberHigherByte(char pinNumberHigherByte) {
		this.pinNumberHigherByte = pinNumberHigherByte;
	}

	public void setPinNumberLowerByte(char pinNumberLowerByte) {
		this.pinNumberLowerByte = pinNumberLowerByte;
	}

	public void setPinValue(Character pinValue) {
		this.pinValue = pinValue;
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

	public void setDigitalPin(int pin, int value) {

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

		//		try {
		//			tmpSocket = bluetoothDevice.createRfcommSocketToServiceRecord(myUUID);
		//			ERROR_OK = 1;
		//		} catch (IOException e) {
		//
		//		}
		//
		//		bluetoothSocket = tmpSocket; //nullpointer
		//		bluetoothAdapter.cancelDiscovery();
		//
		//		isBluetoothinitialized = true;
	}

	public static void turnOffBluetooth() {
		bluetoothAdapter.disable();
		if (bluetoothAdapter.isEnabled()) {
			ERROR_OK = -3;
		}
	}

	//

	public static BluetoothSocket getBluetoothSocket() {
		return bluetoothSocket;
	}

	public static int sendDataViaBluetoothSocket(BluetoothSocket outputBluetoothSocket, char pinValue,
			char pinNumberLowerByte, char pinNumberHigherByte) {

		try {
			outputBluetoothSocket.connect();
			ERROR_OK = 2;
		} catch (IOException e) {
			//			try {
			//				outputBluetoothSocket.close();
			//			} catch (IOException e1) {
			//				return -5;
			//			}
		}

		try {
			bluetoothOutputStream = outputBluetoothSocket.getOutputStream();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//send data here via outputStream

		try {
			bluetoothOutputStream.write(pinNumberLowerByte);
			bluetoothOutputStream.write(pinNumberHigherByte);
			bluetoothOutputStream.write(pinValue);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			outputBluetoothSocket.close();
		} catch (IOException e1) {
			return -5;
		}

		return ERROR_OK;
	}

}
