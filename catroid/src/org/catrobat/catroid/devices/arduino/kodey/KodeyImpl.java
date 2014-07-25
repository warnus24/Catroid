/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
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

package org.catrobat.catroid.devices.arduino.kodey;

import android.util.Log;

import org.catrobat.catroid.bluetooth.BTDeviceService;
import org.catrobat.catroid.bluetooth.BluetoothConnection;
import org.catrobat.catroid.common.CatrobatService;
import org.catrobat.catroid.devices.arduino.common.firmata.Firmata;
import org.catrobat.catroid.devices.arduino.common.firmata.message.DigitalMessage;
import org.catrobat.catroid.devices.arduino.common.serial.ISerial;
import org.catrobat.catroid.devices.arduino.common.serial.SerialException;
import org.catrobat.catroid.devices.arduino.common.serial.StreamingSerialAdapter;
import org.catrobat.catroid.formulaeditor.Sensors;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class KodeyImpl implements Kodey {

	public static final UUID KODEY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	public static final String TAG = KodeyImpl.class.getSimpleName();

	private boolean isInitialized = false;
	private BluetoothConnection connection = null;
	private Firmata firmata = null;

	@Override
	public String getName() {
		return "Kodey";
	}

	@Override
	public Class<? extends BTDeviceService> getServiceType() {
		return CatrobatService.KODEY;
	}

	@Override
	public void setConnection(BluetoothConnection connection) {
		this.connection = connection;
	}

	@Override
	public void disconnect() {
		stopSerial();
		connection.disconnect();
	}

	@Override
	public UUID getBluetoothDeviceUUID() {
		return KODEY_UUID;
	}

	@Override
	public void initialise() {

		if (isInitialized) {
			return;
		}

		try {
			InputStream inStream = connection.getInputStream();
			OutputStream outStream = connection.getOutputStream();

			ISerial serial = new StreamingSerialAdapter(inStream, outStream);
			firmata = new Firmata(serial);


			isInitialized = true;

		} catch (IOException e) {
			Log.e(TAG, "Error trying to get stream connection to kodey");
		}
	}

	@Override
	public void start() {
		try {
			if (firmata != null) {
				firmata.getSerial().start();
			}
		} catch (SerialException e) {
			Log.e(TAG, "Failed to start Kodey");
		}
	}

	@Override
	public void pause() {
		stopAllMovements();
	}

	@Override
	public void destroy() {

	}

	@Override
	public void playTone(int selected_tone, int duration) {
		// TODO: library shifts only 7 bits instead of 8 --> max value for least and most significant byte ist 127
		sendDigitalMessage(PIN_SPEAKER_OUT, (selected_tone & 0x00FF) | ( (duration & 0x00FF)  << 7));
	}

	@Override
	public void move(int motor, int speed) {
		sendDigitalMessage(motor, speed);
	}

	@Override
	public void stopLeft() {
		sendDigitalMessage(LEFT_MOTOR_FORWARD, 0);
	}

	@Override
	public void stopRight() {
		sendDigitalMessage(RIGHT_MOTOR_FORWARD, 0);
	}

	@Override
	public void stopAllMovements() {
		stopLeft();
		stopRight();
	}

	@Override
	public void setRGBLightColor(int eye, int red, int green, int blue) {
		switch (eye) {
			case 0:
				sendDigitalMessage(PIN_RED_1, red);
				sendDigitalMessage(PIN_BLUE_1, green);
				sendDigitalMessage(PIN_GREEN_1, blue);
			case 1:
				sendDigitalMessage(PIN_RED_2, red);
				sendDigitalMessage(PIN_BLUE_2, green);
				sendDigitalMessage(PIN_GREEN_2, blue);
			case 2:
				sendDigitalMessage(PIN_RED_1, red);
				sendDigitalMessage(PIN_BLUE_1, green);
				sendDigitalMessage(PIN_GREEN_1, blue);

				sendDigitalMessage(PIN_RED_2, red);
				sendDigitalMessage(PIN_BLUE_2, green);
				sendDigitalMessage(PIN_GREEN_2, blue);
		}
	}

	@Override
	public int getSensorValue(Sensors sensor) {
		return 0;
	}

	@Override
	public KodeySensor getSensor1() {
		return null;
	}

	@Override
	public KodeySensor getSensor2() {
		return null;
	}

	@Override
	public KodeySensor getSensor3() {
		return null;
	}

	@Override
	public KodeySensor getSensor4() {
		return null;
	}

	@Override
	public KodeySensor getSensor5() {
		return null;
	}

	@Override
	public KodeySensor getSensor6() {
		return null;
	}

	private void stopSerial() {
		try {
			if (firmata != null) {
				firmata.getSerial().stop();
			}
		} catch (SerialException e) {
			Log.e(TAG, "Failed to stop Kodey");
		}
	}

	private void sendDigitalMessage(int port, int value)  {
		try {
			firmata.send(new DigitalMessage(port, value));
		} catch (SerialException e) {
			Log.e(TAG, "Error sending digital Message!");
		}
	}

}
