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
import org.catrobat.catroid.lego.mindstorm.MindstormConnection;

import java.io.IOException;
import java.util.UUID;

public class LegoNXTImpl implements LegoNXT {

	private static final UUID SERIAL_PORT_SERVICE_CLASS_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	private static final String TAG = LegoNXTImpl.class.getSimpleName();

	private MindstormConnection connection;
	private Context context;

	private NXTMotor motorA;
	private NXTMotor motorB;
	private NXTMotor motorC;

	public LegoNXTImpl(Context context) {
		this.context = context;
	}

	public void init() {
		connection.init();

		motorA = new NXTMotor(0, connection);
		motorB = new NXTMotor(1, connection);
		motorC = new NXTMotor(2, connection);
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
		this.connection = new MindstormConnection(btConnection);
		init();
	}

	@Override
	public UUID getBluetoothDeviceUUID() {
		return SERIAL_PORT_SERVICE_CLASS_UUID;
	}

	@Override
	public void disconnect() {

		if (connection.isConnected()) {
			this.stopAllMovements();
		}

		try {
			connection.disconnect();

		} catch (IOException ioException) {
			Log.e(TAG, Log.getStackTraceString(ioException));
		}
	}

	@Override
	public void playTone(int frequencyInHz, int durationInMs) {
		Command command = new Command(CommandType.DIRECT_COMMAND, CommandByte.PLAY_TONE, false);

		command.append((byte)(frequencyInHz & 0x00FF));
		command.append((byte)((frequencyInHz & 0xFF00) >> 8));
		command.append((byte) (durationInMs & 0x00FF));
		command.append((byte) ((durationInMs & 0xFF00) >> 8));
		connection.send(command);
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

}
