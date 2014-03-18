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
import android.util.Log;

import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

public class ArduinoSendAction extends TemporalAction {

	private static char pinNumberHigherByte, pinNumberLowerByte;
	private static char pinValue;
	private static String MACadress = "00:07:80:49:8B:61";
	public static UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

	private static BluetoothAdapter bluetoothAdapter = null;
	private static BluetoothDevice bluetoothDevice = null;
	private static BluetoothSocket bluetoothSocket = null;
	private static BluetoothSocket tmpSocket = null;
	private static OutputStream bluetoothOutputStream = null;

	public static BluetoothSocket getBluetoothSocket() {
		return bluetoothSocket;
	}

	public static void setBluetoothSocket(BluetoothSocket newbluetoothSocket) {
		bluetoothSocket = newbluetoothSocket;
	}

	public static char getPinNumberHigherByte() {
		return pinNumberHigherByte;
	}

	public static char getPinNumberLowerByte() {
		return pinNumberLowerByte;
	}

	public static char getPinValue() {
		return pinValue;
	}

	public static void setPinNumberHigherByte(char newpinNumberHigherByte) {
		pinNumberHigherByte = newpinNumberHigherByte;
	}

	public static void setPinNumberLowerByte(char newpinNumberLowerByte) {
		pinNumberLowerByte = newpinNumberLowerByte;
	}

	public static void setPinValue(Character newpinValue) {
		pinValue = newpinValue;
	}

	@Override
	public boolean act(float delta) {
		return false;
	}

	public static void initBluetoothConnection() {
		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		setBluetoothDevice(bluetoothAdapter.getRemoteDevice(MACadress));

		try {
			tmpSocket = bluetoothDevice.createRfcommSocketToServiceRecord(myUUID);
			//			tmpSocket.connect();
		} catch (IOException e) {
			e.printStackTrace();
		}

		bluetoothSocket = tmpSocket;
	}

	public static BluetoothDevice getBluetoothDevice() {
		return bluetoothDevice;
	}

	public static void setBluetoothDevice(BluetoothDevice bluetoothDevice) {
		ArduinoSendAction.bluetoothDevice = bluetoothDevice;
	}

	public static void turnOnBluetooth() {
		if (!bluetoothAdapter.isEnabled()) {
			bluetoothAdapter.enable();
		}
	}

	public static void turnOffBluetooth() {
		bluetoothAdapter.disable();
	}

	public static void sendDataViaBluetoothSocket(BluetoothSocket outputBluetoothSocket, char pinValue,
			char pinNumberLowerByte, char pinNumberHigherByte) {
		try {
			//			outputBluetoothSocket.connect();
			bluetoothOutputStream = outputBluetoothSocket.getOutputStream();
			bluetoothOutputStream.write(pinNumberLowerByte);
			bluetoothOutputStream.write(pinNumberHigherByte);
			bluetoothOutputStream.write(pinValue);
			bluetoothOutputStream.flush();
			Log.d("Arduino", "BT Message " + pinNumberLowerByte + "" + pinNumberHigherByte + "" + pinValue
					+ "---------------");
			//			outputBluetoothSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void update(float percent) {

		Log.d("Arduino", "BT Message " + pinNumberLowerByte + "" + pinNumberHigherByte + "" + pinValue
				+ "---------------");

		ArduinoSendAction
				.sendDataViaBluetoothSocket(bluetoothSocket, pinValue, pinNumberLowerByte, pinNumberHigherByte);
	}

}