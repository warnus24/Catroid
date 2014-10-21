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
package org.catrobat.catroid.lego.mindstorm.nxt;

import android.content.Context;
import android.util.Log;

import org.catrobat.catroid.bluetooth.BTDeviceService;
import org.catrobat.catroid.bluetooth.BluetoothConnection;
import org.catrobat.catroid.formulaeditor.Sensors;
import org.catrobat.catroid.lego.mindstorm.MindstormConnection;
import org.catrobat.catroid.lego.mindstorm.MindstormConnectionImpl;
import org.catrobat.catroid.lego.mindstorm.MindstormException;
import org.catrobat.catroid.lego.mindstorm.MindstormSensor;
import org.catrobat.catroid.lego.mindstorm.nxt.sensors.NXTSensor;
import org.catrobat.catroid.lego.mindstorm.nxt.sensors.NXTSensorService;

import java.util.UUID;

public class LegoNXTImpl implements LegoNXT, NXTSensorService.OnSensorChangedListener {

	private static final UUID LEGO_NXT_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	private static final String TAG = LegoNXTImpl.class.getSimpleName();

	private MindstormConnection mindstormConnection;
	private Context context;

	private NXTMotor motorA;
	private NXTMotor motorB;
	private NXTMotor motorC;

	private NXTSensor sensor1;
	private NXTSensor sensor2;
	private NXTSensor sensor3;
	private NXTSensor sensor4;

	private NXTSensorService sensorService;

	public LegoNXTImpl(Context applicationContext) {
		this.context = applicationContext;
	}

	@Override
	public String getName() {
		return "NXT";
	}

	@Override
	public Class<? extends BTDeviceService> getServiceType() {
		return BTDeviceService.LEGO_NXT;
	}

	@Override
	public void setConnection(BluetoothConnection btConnection) {
		this.mindstormConnection = new MindstormConnectionImpl(btConnection);
	}

	@Override
	public UUID getBluetoothDeviceUUID() {
		return LEGO_NXT_UUID;
	}

	@Override
	public void disconnect() {
		if (mindstormConnection.isConnected()) {
			this.stopAllMovements();
			sensorService.destroy();
			mindstormConnection.disconnect();
		}
	}

	@Override
	public void playTone(int frequencyInHz, int durationInMs) {

		if (durationInMs <= 0) {
			return;
		}

		if (frequencyInHz > 14000) {
			frequencyInHz = 14000;
		}
		else if (frequencyInHz < 200) {
			frequencyInHz = 200;
		}

		Command command = new Command(CommandType.DIRECT_COMMAND, CommandByte.PLAY_TONE, false);
		command.append((byte)(frequencyInHz & 0x00FF));
		command.append((byte)((frequencyInHz & 0xFF00) >> 8));
		command.append((byte) (durationInMs & 0x00FF));
		command.append((byte) ((durationInMs & 0xFF00) >> 8));

		try {
			mindstormConnection.send(command);
		}
		catch (MindstormException e) {
			Log.e(TAG, e.getMessage());
		}
	}

	@Override
	public NXTMotor getMotorA() {
		return motorA;
	}

	@Override
	public NXTMotor getMotorB() {
		return motorB;
	}

	@Override
	public NXTMotor getMotorC() {
		return motorC;
	}

	@Override
	public void stopAllMovements() {
		motorA.stop();
		motorB.stop();
		motorC.stop();
	}

	@Override
	public synchronized int getSensorValue(Sensors sensor) {

		switch (sensor) {
			case NXT_SENSOR_1:
				return getSensor1().getLastSensorValue();
			case NXT_SENSOR_2:
				return getSensor2().getLastSensorValue();
			case NXT_SENSOR_3:
				return getSensor3().getLastSensorValue();
			case NXT_SENSOR_4:
				return getSensor4().getLastSensorValue();
		}

		return -1;
	}

	@Override
	public MindstormSensor getSensor1() {
		return sensor1;
	}

	@Override
	public MindstormSensor getSensor2() {
		return sensor2;
	}

	@Override
	public MindstormSensor getSensor3() {
		return sensor3;
	}

	@Override
	public MindstormSensor getSensor4() {
		return sensor4;
	}

	@Override
	public void onSensorChanged() {
		assignSensorsToPorts();
	}

	private NXTSensorService getSensorService() {
		if (sensorService == null) {
			sensorService = new NXTSensorService(context, mindstormConnection);
			sensorService.registerOnSensorChangedListener(this);
		}

		return sensorService;
	}

	@Override
	public void initialise() {
		mindstormConnection.init();

		motorA = new NXTMotor(0, mindstormConnection);
		motorB = new NXTMotor(1, mindstormConnection);
		motorC = new NXTMotor(2, mindstormConnection);

		assignSensorsToPorts();
	}

	private synchronized void assignSensorsToPorts() {
		NXTSensorService sensorService = getSensorService();

		sensor1 = sensorService.createSensor1();
		sensor2 = sensorService.createSensor2();
		sensor3 = sensorService.createSensor3();
		sensor4 = sensorService.createSensor4();
	}

	@Override
	public void start() {
		sensorService.resumeSensorUpdate();
	}

	@Override
	public void pause() {
		sensorService.pauseSensorUpdate();
	}

	@Override
	public void destroy() {
		disconnect();
	}
}
