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

public class ArduinoSendAction extends Action {

	private char pinNumberHigherByte, pinNumberLowerByte;
	private char pinValue;

	//Needed to init BT at the first call
	private static Boolean isBluetoothinitialized = false;

	//TODO change this
	//private static String MACaddr = "00:07:80:49:8B:61"; //MAC address of the Arduino BT-board
	private static String MACaddr = "";
	public static UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

	private static BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	//private static BluetoothDevice bluetoothDevice = bluetoothAdapter.getRemoteDevice(MACaddr);
	private static BluetoothDevice bluetoothDevice = null;
	private static BluetoothSocket bluetoothSocket = null;
	private static BluetoothSocket tmpSocket = null;
	private static OutputStream bluetoothOutputStream = null;

	public static BluetoothSocket getBluetoothSocket() {
		return bluetoothSocket;
	}

	public void setPinNumberHigherByte(char pinNumberHigherByte) {
		this.pinNumberHigherByte = pinNumberHigherByte;
	}

	public void setPinNumberLowerByte(char pinNumberLowerByte) {
		this.pinNumberLowerByte = pinNumberLowerByte;
	}

	public void setPinValue(Character pinValue) {
		this.pinValue = pinValue;
	}

	@Override
	public boolean act(float delta) {
		if (!isBluetoothinitialized) {
			//			this.initBluetoothConnection();
		}
		return false;
	}

	public void setDigitalPin(int pin, int value) {
	}

	public static void initBluetoothConnection(String MACadress) {

		setBluetoothDevice(bluetoothAdapter.getRemoteDevice(MACadress));

		try {
			tmpSocket = bluetoothDevice.createRfcommSocketToServiceRecord(myUUID);
		} catch (IOException e) {
			e.printStackTrace();
		}

		bluetoothSocket = tmpSocket;
		isBluetoothinitialized = true;
	}

	/**
	 * @return the bluetoothDevice
	 */
	public static BluetoothDevice getBluetoothDevice() {
		return bluetoothDevice;
	}

	/**
	 * @param bluetoothDevice
	 *            the bluetoothDevice to set
	 */
	public static void setBluetoothDevice(BluetoothDevice bluetoothDevice) {
		ArduinoSendAction.bluetoothDevice = bluetoothDevice;
	}

	public static void turnOffBluetooth() {
		bluetoothAdapter.disable();
	}

	public static void sendDataViaBluetoothSocket(BluetoothSocket outputBluetoothSocket, char pinValue,
			char pinNumberLowerByte, char pinNumberHigherByte) {
		try {
			outputBluetoothSocket.connect();
			bluetoothOutputStream = outputBluetoothSocket.getOutputStream();
			bluetoothOutputStream.write(pinNumberLowerByte);
			bluetoothOutputStream.write(pinNumberHigherByte);
			bluetoothOutputStream.write(pinValue);
			outputBluetoothSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}