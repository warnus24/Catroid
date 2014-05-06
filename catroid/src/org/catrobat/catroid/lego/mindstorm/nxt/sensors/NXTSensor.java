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
package org.catrobat.catroid.lego.mindstorm.nxt.sensors;


import org.catrobat.catroid.lego.mindstorm.MindstormConnection;
import org.catrobat.catroid.lego.mindstorm.MindstormSensor;
import org.catrobat.catroid.lego.mindstorm.nxt.*;

public abstract class NXTSensor implements MindstormSensor {

	protected int port;
	protected NXTSensorType sensorType;
	protected NXTSensorMode sensorMode;

	protected MindstormConnection connection;

	protected boolean hasInit;

	public NXTSensor(int port, NXTSensorType sensorType, NXTSensorMode sensorMode, MindstormConnection connection) {
		this.port = port;
		this.sensorType = sensorType;
		this.sensorMode = sensorMode;

		this.connection = connection;
	}

	protected void updateTypeAndMode(NXTSensorType NXTSensorType, NXTSensorMode NXTSensorMode){

		this.sensorType = NXTSensorType;
		this.sensorMode = NXTSensorMode;

		Command command = new Command(CommandType.DIRECT_COMMAND, CommandByte.SET_INPUT_MODE, true);
		command.append((byte) port);
		command.append(NXTSensorType.getByte());
		command.append(NXTSensorMode.getByte());
		connection.send(command);
		NXTReply reply = new NXTReply(connection.receive());
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
		connection.send(command);
		NXTReply reply = new NXTReply(connection.receive());
		NXTError.checkForError(reply, 16);

		sensorReadings.Raw = reply.getShort(8);
		sensorReadings.Normalized = reply.getShort(10);
		sensorReadings.Scaled = reply.getShort(12);
		return sensorReadings;
	}

	protected void initialize()
	{
		if (connection != null && connection.isConnected()) {
			updateTypeAndMode(sensorType, sensorMode);
			try {
				Thread.sleep(100);
				resetScaledValue();
				Thread.sleep(100);
				updateTypeAndMode(sensorType, sensorMode);
				hasInit = true;
			} catch (InterruptedException e) {
				hasInit = false;
				e.printStackTrace();
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
}
