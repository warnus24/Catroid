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
package org.catrobat.catroid.arduino;

import android.content.Context;
import android.util.Log;

import org.catrobat.catroid.bluetooth.BTDeviceService;
import org.catrobat.catroid.bluetooth.BluetoothConnection;
import org.catrobat.catroid.formulaeditor.Sensors;
import org.catrobat.catroid.arduino.ArduinoConnectionImpl;
import org.catrobat.catroid.arduino.ArduinoException;
import org.catrobat.catroid.arduino.ArduinoSensor;
import org.catrobat.catroid.arduino.ArduinoCommand;
import org.catrobat.catroid.arduino.ArduinoCommandByte;
import org.catrobat.catroid.arduino.ArduinoCommandType;
import org.catrobat.catroid.arduino.Arduino;
import org.catrobat.catroid.arduino.Send;
import org.catrobat.catroid.arduino.Receive;
import org.catrobat.catroid.arduino.ArduinoSensorService;

import java.util.UUID;

public class ArduinoImpl implements Arduino, ArduinoSensorService.OnSensorChangedListener {

	private static final UUID ARDUINO_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	private static final String TAG = ArduinoImpl.class.getSimpleName();

	protected ArduinoConnection arduinoConnection;
	protected Context context;

	private boolean isInitialized = false;

	private ArduinoSendData motorA;

	private ArduinoSensor sensor1;
	private ArduinoSensor sensor2;

	private ArduinoSensorService sensorService;

	public ArduinoImpl(Context applicationContext) {
		this.context = applicationContext;
	}

	@Override
	public String getName() {
		return "ARDUINO";
	}

	@Override
	public Class<? extends BTDeviceService> getServiceType() {
		return BTDeviceService.ARDUINO;
	}

	@Override
	public void setConnection(BluetoothConnection btConnection) {
		this.arduinoConnection = new ArduinoConnectionImpl(btConnection);
	}

	@Override
	public UUID getBluetoothDeviceUUID() {
		return ARDUINO_UUID;
	}

	@Override
	public void disconnect() {
		if (arduinoConnection.isConnected()) {
			this.stopAllMovements();
			sensorService.destroy();
			arduinoConnection.disconnect();
		}
	}


	@Override
	public ArduinoSendData getMotorA() {
		return motorA;
	}


	@Override
	public void stopAllMovements() {
		motorA.stop();
	}

	@Override
	public synchronized int getSensorValue(Sensors sensor) {

		switch (sensor) {
			case ARDUINOANALOG:
				if (getSensor1() == null) {
					return 0;
				}
				return getSensor1().getLastSensorValue();
			case ARDUINODIGITAL:
				if (getSensor2() == null) {
					return 0;
				}
				return getSensor2().getLastSensorValue();
		}

		return -1;
	}

	@Override
	public ArduinoSensor getSensor1() {
		return sensor1;
	}

	@Override
	public ArduinoSensor getSensor2() {
		return sensor2;
	}

	@Override
	public void onSensorChanged() {
		assignSensorsToPorts();
	}

	private ArduinoSensorService getSensorService() {
		if (sensorService == null) {
			sensorService = new ArduinoSensorService(context, arduinoConnection);
			sensorService.registerOnSensorChangedListener(this);
		}

		return sensorService;
	}

	@Override
	public void initialise() {

		if (isInitialized) {
			return;
		}

		arduinoConnection.init();

		motorA = new ArduinoSendData(0, arduinoConnection);

		assignSensorsToPorts();

		isInitialized = true;
	}

	private synchronized void assignSensorsToPorts() {
		ArduinoSensorService sensorService = getSensorService();

		sensor1 = sensorService.createSensor1();
		sensor2 = sensorService.createSensor2();
	}

	@Override
	public void start() {
		sensorService.resumeSensorUpdate();
	}

	@Override
	public void pause() {
		stopAllMovements();
		sensorService.pauseSensorUpdate();
	}

	@Override
	public void destroy() {
	}
}
