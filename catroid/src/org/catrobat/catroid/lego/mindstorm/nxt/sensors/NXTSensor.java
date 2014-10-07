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
package org.catrobat.catroid.lego.mindstorm.nxt.sensors;


import android.util.Log;

import org.catrobat.catroid.lego.mindstorm.Mindstorm;
import org.catrobat.catroid.lego.mindstorm.MindstormConnection;
import org.catrobat.catroid.lego.mindstorm.MindstormException;
import org.catrobat.catroid.lego.mindstorm.MindstormSensor;
import org.catrobat.catroid.lego.mindstorm.nxt.*;

public abstract class NXTSensor implements MindstormSensor {

	protected final int port;
	protected final NXTSensorType sensorType;
	protected final NXTSensorMode sensorMode;
	protected final int updateInterval = 250;

	protected final MindstormConnection connection;

	protected boolean hasInit;

	protected int lastValidValue = 0;

	public static final String TAG = NXTSensor.class.getSimpleName();

	public NXTSensor(int port, NXTSensorType sensorType, NXTSensorMode sensorMode, MindstormConnection connection) {
		this.port = port;
		this.sensorType = sensorType;
		this.sensorMode = sensorMode;

		this.connection = connection;
	}

	protected void updateTypeAndMode() {//(NXTSensorType NXTSensorType, NXTSensorMode NXTSensorMode){

//		this.sensorType = NXTSensorType;
//		this.sensorMode = NXTSensorMode;

		Command command = new Command(CommandType.DIRECT_COMMAND, CommandByte.SET_INPUT_MODE, true);
		command.append((byte) port);
		command.append(sensorType.getByte());
		command.append(sensorMode.getByte());

		NXTReply reply = new NXTReply(connection.sendAndReceive(command));
		NXTError.checkForError(reply, 3);
	}

	protected int getScaledValue()
	{
		return getSensorReadings().Scaled;
	}

	protected int getRawValue()
	{
		return getSensorReadings().Raw;
	}

	protected int getNormalizedValue()
	{
		return getSensorReadings().Normalized;
	}

	public SensorReadings getSensorReadings()
	{
		if (!hasInit) {
			initialize();
		}

		SensorReadings sensorReadings = new SensorReadings();
		Command command = new Command(CommandType.DIRECT_COMMAND, CommandByte.GET_INPUT_VALUES, true);
		command.append((byte) port);
		NXTReply reply = new NXTReply(connection.sendAndReceive(command));
		NXTError.checkForError(reply, 16);

		sensorReadings.Raw = reply.getShort(8);
		sensorReadings.Normalized = reply.getShort(10);
		sensorReadings.Scaled = reply.getShort(12);
		return sensorReadings;
	}

	protected void initialize()
	{
		if (connection != null && connection.isConnected()) {
			updateTypeAndMode();
			try {
				Thread.sleep(100);
				resetScaledValue();
				Thread.sleep(100);
				updateTypeAndMode();
				hasInit = true;
			} catch (InterruptedException e) {
				hasInit = false;
			}
		} else {
			hasInit = false;
		}
	}

	protected void resetScaledValue() {
		Command command = new Command(CommandType.DIRECT_COMMAND, CommandByte.RESET_INPUT_SCALED_VALUE, false);
		command.append((byte)port);
		connection.send(command);
	}

	private static class SensorReadings {
		public int Raw;
		public int Normalized;
		public int Scaled;
	}

	@Override
	public int getUpdateInterval() {
		return updateInterval;
	}

	@Override
	public void updateLastSensorValue() {
		try {
			lastValidValue = getValue();
		}
		catch (MindstormException e) {
			Log.e(TAG, e.getMessage());
		}
	}

	@Override
	public int getLastSensorValue() {
		return lastValidValue;
	}

	@Override
	public String getName() {
		return String.format("%s_%s_%d", TAG, sensorType.name(), port);
	}

	@Override
	public int getConnectedPort() {
		return port;
	}
}
