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

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

import org.catrobat.catroid.arduino.Arduino;

import java.io.IOException;
import java.io.OutputStream;

public class ArduinoSendAction extends TemporalAction {

	private static int pinNumberHigherByte, pinNumberLowerByte;
	private static int pinValue;
	private static BluetoothDevice bluetoothDevice = null;
	private static BluetoothSocket bluetoothSocket = null;
	private static OutputStream bluetoothOutputStream = null;

	public static BluetoothSocket getBluetoothSocket() {
		return bluetoothSocket;
	}

	public static void setBluetoothSocket(BluetoothSocket newbluetoothSocket) {
		bluetoothSocket = newbluetoothSocket;
	}

	public static int getPinNumberHigherByte() {
		return pinNumberHigherByte;
	}

	public static int getPinNumberLowerByte() {
		return pinNumberLowerByte;
	}

	public static int getPinValue() {
		return pinValue;
	}

	public static void setPinNumberHigherByte(int newpinNumberHigherByte) {
		pinNumberHigherByte = newpinNumberHigherByte;
	}

	public static void setPinNumberLowerByte(int newpinNumberLowerByte) {
		pinNumberLowerByte = newpinNumberLowerByte;
	}

	public static void setPinValue(int newpinValue) {
		pinValue = newpinValue;
	}

	public static BluetoothDevice getBluetoothDevice() {
		return bluetoothDevice;
	}

	public static void setBluetoothDevice(BluetoothDevice bluetoothDevice) {
		ArduinoSendAction.bluetoothDevice = bluetoothDevice;
	}

	public static void sendDataViaBluetoothSocket(BluetoothSocket outputBluetoothSocket, int pinValue,
			int pinNumberLowerByte, int pinNumberHigherByte) {
		try {
			bluetoothOutputStream = outputBluetoothSocket.getOutputStream();
			bluetoothOutputStream.write(pinNumberLowerByte);
			bluetoothOutputStream.write(pinNumberHigherByte);
			bluetoothOutputStream.write(pinValue);
			bluetoothOutputStream.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void update(float percent) {

		Log.d("Arduino", "BT Message " + pinNumberLowerByte + "" + pinNumberHigherByte + "" + pinValue
				+ "---------------");

		//		Arduino.sendArduinoDigitalPinMessage(ArduinoSendAction.getPinNumberLowerByte(),
		//				ArduinoSendAction.getPinNumberHigherByte(), ArduinoSendAction.getPinValue());
	}

	@Override
	public boolean act(float delta) {
		//		ArduinoSendAction.sendDataViaBluetoothSocket(ArduinoSendAction.getBluetoothSocket(),
		//				ArduinoSendAction.getPinValue(), ArduinoSendAction.getPinNumberLowerByte(),
		//				ArduinoSendAction.getPinNumberHigherByte());

		Arduino.sendArduinoDigitalPinMessage(ArduinoSendAction.getPinNumberLowerByte(),
				ArduinoSendAction.getPinNumberHigherByte(), ArduinoSendAction.getPinValue());
		return true;
	}
}