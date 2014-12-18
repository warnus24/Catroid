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


import android.util.Log;

import org.catrobat.catroid.arduino.ArduinoConnection;
import org.catrobat.catroid.arduino.ArduinoException;
import org.catrobat.catroid.arduino.ArduinoSensor;
import org.catrobat.catroid.arduino.Command;
import org.catrobat.catroid.arduino.ArduinoCommandByte;
import org.catrobat.catroid.arduino.ArduinoCommandType;
import org.catrobat.catroid.lego.mindstorm.nxt.NXTError;
import org.catrobat.catroid.arduino.ArduinoReply;
import org.catrobat.catroid.arduino.ArduinoSensorMode;
import org.catrobat.catroid.arduino.ArduinoSensorType;

import java.util.Locale;

public abstract class ArduinoSensor implements Receive {

	protected final int port;
	protected final ArduinoSensorType sensorType;
	protected final ArduinoSensorMode sensorMode;
	protected final int updateInterval = 250;

	protected final ArduinoConnection connection;

	protected boolean hasInit;

	protected int lastValidValue = 0;

	public static final String TAG = ArduinoSensor.class.getSimpleName();

	public ArduinoSensor(int port, ArduinoSensorType sensorType, ArduinoSensorMode sensorMode, ArduinoConnection connection) {
		this.port = port;
		this.sensorType = sensorType;
		this.sensorMode = sensorMode;

		this.connection = connection;
	}

	protected void updateTypeAndMode() {//(NXTSensorType NXTSensorType, NXTSensorMode NXTSensorMode){

//		this.sensorType = NXTSensorType;
//		this.sensorMode = NXTSensorMode;

		Command command = new Command(ArduinoCommandType.DIRECT_COMMAND, ArduinoCommandByte.SET_INPUT_MODE, true);
		command.append((byte) port);
		command.append(sensorType.getByte());
		command.append(sensorMode.getByte());

		ArduinoReply reply = new ArduinoReply(connection.sendAndReceive(command));
		ArduinoError.checkForError(reply, 3);
	}

	protected int getScaledValue()
	{
		return getSensorReadings().scaled;
	}

	protected int getRawValue()
	{
		return getSensorReadings().raw;
	}

	protected int getNormalizedValue()
	{
		return getSensorReadings().normalized;
	}

	public SensorReadings getSensorReadings()
	{
		if (!hasInit) {
			initialize();
		}

		SensorReadings sensorReadings = new SensorReadings();
		Command command = new Command(ArduinoCommandType.DIRECT_COMMAND, ArduinoCommandByte.GET_INPUT_VALUES, true);
		command.append((byte) port);
		ArduinoReply reply = new ArduinoReply(connection.sendAndReceive(command));
		ArduinoError.checkForError(reply, 16);

		sensorReadings.raw = reply.getShort(8);
		sensorReadings.normalized = reply.getShort(10);
		sensorReadings.scaled = reply.getShort(12);
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
		Command command = new Command(ArduinoCommandType.DIRECT_COMMAND, ArduinoCommandByte.RESET_INPUT_SCALED_VALUE, false);
		command.append((byte)port);
		connection.send(command);
	}

	private static class SensorReadings {
		public int raw;
		public int normalized;
		public int scaled;
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
		catch (ArduinoException e) {
			Log.e(TAG, e.getMessage());
		}
	}

	@Override
	public int getLastSensorValue() {
		return lastValidValue;
	}

	@Override
	public String getName() {
		return String.format(Locale.getDefault(), "%s_%s_%d", TAG, sensorType.name(), port);
	}

	@Override
	public int getConnectedPort() {
		return port;
	}
}
