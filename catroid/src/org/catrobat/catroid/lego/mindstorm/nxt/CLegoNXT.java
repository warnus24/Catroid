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
package org.catrobat.catroid.lego.mindstorm.nxt;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import org.catrobat.catroid.lego.mindstorm.Mindstorm;
import org.catrobat.catroid.lego.mindstorm.MindstormConnection;
import org.catrobat.catroid.lego.mindstorm.MindstormSensor;
import org.catrobat.catroid.lego.mindstorm.MindstormServiceProvider;
import org.catrobat.catroid.lego.mindstorm.nxt.sensors.NXTSensor;

import java.io.IOException;

public class CLegoNXT implements LegoNXT, NXTSensorService.OnSensorChangedListener {

	private static final String TAG = CLegoNXT.class.getSimpleName();

	private MindstormConnection connection;
	private Handler receiveHandler;
	private Context context;

	private MotorNXT motorA;
	private MotorNXT motorB;
	private MotorNXT motorC;

	private NXTSensor sensor1;
	private NXTSensor sensor2;
	private NXTSensor sensor3;
	private NXTSensor sensor4;

	public CLegoNXT(Handler receiveHandler, Context context) {
		this.receiveHandler = receiveHandler;
		this.context = context;
	}

	@Override
	public void connect(String macAddress) {

		if (connection != null) {
			try {
				connection.disconnect();
			} catch (IOException ioException) {
				Log.e(TAG, Log.getStackTraceString(ioException));
			}
		}

        setConnection();

		try {
			connection.connect(macAddress);
            MindstormServiceProvider.register(this, LegoNXT.class);

			motorA = new MotorNXT(0, connection);
			motorB = new MotorNXT(1, connection);
			motorC = new MotorNXT(2, connection);

			assignSensorsToPorts();

		} catch (IOException ioException) {
			Log.e(TAG, Log.getStackTraceString(ioException));
		}
	}

    private void setConnection() {
        connection = new MindstormConnection(receiveHandler);
    }

	private void assignSensorsToPorts() {
		NXTSensorService sensorService = getSensorService();

		sensor1 = sensorService.createSensor1();
		sensor2 = sensorService.createSensor2();
		sensor3 = sensorService.createSensor3();
		sensor4 = sensorService.createSensor4();
	}

	private NXTSensorService getSensorService() {
		NXTSensorService sensorService = MindstormServiceProvider.resolve(NXTSensorService.class);
		if (sensorService == null) {
			sensorService = new NXTSensorService(context, connection);
			MindstormServiceProvider.register(sensorService);
			sensorService.registerOnSensorChangedListener(this);
		}

		return sensorService;
	}

	@Override
	public void disconnect() {

        NXTSensorService sensorService = MindstormServiceProvider.resolve(NXTSensorService.class);
        if (sensorService != null) {
            sensorService.destory();
        }

		if (connection.isConnected()) {
			this.stopAllMovements();
		}

		try {
			connection.disconnect();

		} catch (IOException ioException) {
			Log.e(TAG, Log.getStackTraceString(ioException));
//			sendToast(resources.getString(R.string.problem_at_closing));
//			Log.e(TAG, Log.getStackTraceString(ioException));
		}
        MindstormServiceProvider.unregister(LegoNXT.class);
	}

	@Override
	public void playTone(int frequencyInHz, int durationInMs) {
		Command command = new Command(CommandType.DIRECT_COMMAND, CommandByte.PLAY_TONE, false);

		command.append((byte)(frequencyInHz & 0x00FF));
		command.append((byte)((frequencyInHz & 0xFF00) >> 8));
		command.append((byte)(durationInMs & 0x00FF));
		command.append((byte)((durationInMs & 0xFF00) >> 8));
		connection.send(command);
	}

	@Override
	public MotorNXT getMotorA() {
		return motorA;
	}

	@Override
	public MotorNXT getMotorB() {
		return motorB;
	}

	@Override
	public MotorNXT getMotorC() {
		return motorC;
	}

	@Override
	public void stopAllMovements() {
		motorA.stop();
		motorB.stop();
		motorC.stop();
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
}
