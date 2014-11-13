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

package org.catrobat.catroid.test.mindstorms.nxt;

import android.test.AndroidTestCase;

import org.catrobat.catroid.lego.mindstorm.MindstormCommand;
import org.catrobat.catroid.lego.mindstorm.nxt.CommandByte;
import org.catrobat.catroid.lego.mindstorm.nxt.sensors.NXTI2CUltraSonicSensor;
import org.catrobat.catroid.lego.mindstorm.nxt.sensors.NXTLightSensor;
import org.catrobat.catroid.lego.mindstorm.nxt.sensors.NXTSensor;
import org.catrobat.catroid.lego.mindstorm.nxt.sensors.NXTSensorMode;
import org.catrobat.catroid.lego.mindstorm.nxt.sensors.NXTSensorType;
import org.catrobat.catroid.lego.mindstorm.nxt.sensors.NXTSoundSensor;
import org.catrobat.catroid.lego.mindstorm.nxt.sensors.NXTTouchSensor;

public class SensorTests extends AndroidTestCase {

	private static final byte DIRECT_COMMAND_WITHOUT_REPLY = (byte) 0x80;
	private static final byte DIRECT_COMMAND_WITH_REPLY = (byte) 0x00;
	private static final byte PORT_NR_0 = 0;
	private static final byte PORT_NR_1 = 1;
	private static final byte PORT_NR_2 = 2;
	private static final byte PORT_NR_3 = 3;

	private static final byte ULTRASONIC_ADDRESS = 0x02;
	private static final byte SENSOR_REGISTER_RESULT1 = 0x42;

	public void testSetSensorModeTouch() {
		MindstormTestConnection connection = new MindstormTestConnection();
		connection.init();
		NXTSensor sensor = new NXTTouchSensor(PORT_NR_0, connection);

		sensor.getValue();

		MindstormCommand command = connection.getNextSentCommand();
		assertNotNull("No command", command);
		byte[] rawCommand = command.getRawCommand();

		assertEquals("Incorrect Header", DIRECT_COMMAND_WITH_REPLY, rawCommand[0]);
		assertEquals("Wrong CommandByte", CommandByte.SET_INPUT_MODE.getByte(), rawCommand[1]);
		assertEquals("Wrong Port", PORT_NR_0, rawCommand[2]);
		assertEquals("Wrong sensor type", NXTSensorType.TOUCH.getByte(), rawCommand[3]);
		assertEquals("Wrong sensor mode", NXTSensorMode.BOOL.getByte(), rawCommand[4]);
		assertEquals("Wrong command length", 5, rawCommand.length);
	}

	public void testSetSensorModeSound() {
		MindstormTestConnection connection = new MindstormTestConnection();
		connection.init();
		NXTSensor sensor = new NXTSoundSensor(PORT_NR_1, connection);

		sensor.getValue();

		MindstormCommand command = connection.getNextSentCommand();
		assertNotNull("No command", command);
		byte[] rawCommand = command.getRawCommand();

		assertEquals("Incorrect Header", DIRECT_COMMAND_WITH_REPLY, rawCommand[0]);
		assertEquals("Wrong CommandByte", CommandByte.SET_INPUT_MODE.getByte(), rawCommand[1]);
		assertEquals("Wrong port", PORT_NR_1, rawCommand[2]);
		assertEquals("Wrong sensor Type", NXTSensorType.SOUND_DBA.getByte(), rawCommand[3]);
		assertEquals("Wrong sensor mode", NXTSensorMode.Percent.getByte(), rawCommand[4]);
		assertEquals("Wrong command length", 5, rawCommand.length);
	}

	public void testSetSensorModeLight() {
		MindstormTestConnection connection = new MindstormTestConnection();
		connection.init();
		NXTSensor sensor = new NXTLightSensor(PORT_NR_2, connection);

		sensor.getValue();

		MindstormCommand command = connection.getNextSentCommand();
		assertNotNull("No command", command);
		byte[] rawCommand = command.getRawCommand();

		assertEquals("Incorrect Header", DIRECT_COMMAND_WITH_REPLY, rawCommand[0]);
		assertEquals("Wrong CommandByte", CommandByte.SET_INPUT_MODE.getByte(), rawCommand[1]);
		assertEquals("Wrong port", PORT_NR_2, rawCommand[2]);
		assertEquals("Wrong sensor type", NXTSensorType.LIGHT_INACTIVE.getByte(), rawCommand[3]);
		assertEquals("Wrong sensor mode", NXTSensorMode.Percent.getByte(), rawCommand[4]);
		assertEquals("Wrong command length", 5, rawCommand.length);
	}

	public void testGetSimpleSensorValue() {
		MindstormTestConnection connection = new MindstormTestConnection();
		connection.init();
		NXTSensor sensor = new NXTTouchSensor(PORT_NR_0, connection);

		sensor.getValue();
		MindstormCommand command = null;
		MindstormCommand firstCommand = connection.getNextSentCommand();
		while(firstCommand != null) {
			command = firstCommand;
			firstCommand = connection.getNextSentCommand();
		}

		assertNotNull("No command", command);
		byte[] rawCommand = command.getRawCommand();

		assertEquals("Incorrect Header", DIRECT_COMMAND_WITH_REPLY, rawCommand[0]);
		assertEquals("Wrong Command Byte", CommandByte.GET_INPUT_VALUES.getByte(), rawCommand[1]);
		assertEquals("Wrong port", PORT_NR_0, rawCommand[2]);
		assertEquals("Wrong command length", 3, rawCommand.length);
	}

	public void testGetSimpleSensorValueFullCommunication() {
		MindstormTestConnection connection = new MindstormTestConnection();
		connection.init();
		NXTSensor sensor = new NXTTouchSensor(PORT_NR_0, connection);

		sensor.getValue();

		MindstormCommand command = connection.getNextSentCommand();
		assertNotNull("No command", command);
		byte[] rawCommand = command.getRawCommand();

		assertEquals("Incorrect Header", DIRECT_COMMAND_WITH_REPLY, rawCommand[0]);
		assertEquals("Wrong CommandByte", CommandByte.SET_INPUT_MODE.getByte(), rawCommand[1]);
		assertEquals("Wrong Port", PORT_NR_0, rawCommand[2]);
		assertEquals("Wrong sensor type", NXTSensorType.TOUCH.getByte(), rawCommand[3]);
		assertEquals("Wrong sensor mode", NXTSensorMode.BOOL.getByte(), rawCommand[4]);
		assertEquals("Wrong command length", 5, rawCommand.length);

		command = connection.getNextSentCommand();
		assertNotNull("No command2", command);
		rawCommand = command.getRawCommand();

		assertEquals("Incorrect Header2", DIRECT_COMMAND_WITHOUT_REPLY, rawCommand[0]);
		assertEquals("Wrong CommandByte2", CommandByte.RESET_INPUT_SCALED_VALUE.getByte(), rawCommand[1]);
		assertEquals("Wrong Port2", PORT_NR_0, rawCommand[2]);
		assertEquals("Wrong command length2", 3, rawCommand.length);

		command = connection.getNextSentCommand();
		assertNotNull("No command3", command);
		rawCommand = command.getRawCommand();

		assertEquals("Incorrect Header3", DIRECT_COMMAND_WITH_REPLY, rawCommand[0]);
		assertEquals("Wrong CommandByte3", CommandByte.SET_INPUT_MODE.getByte(), rawCommand[1]);
		assertEquals("Wrong Port3", PORT_NR_0, rawCommand[2]);
		assertEquals("Wrong sensor type3", NXTSensorType.TOUCH.getByte(), rawCommand[3]);
		assertEquals("Wrong sensor mode3", NXTSensorMode.BOOL.getByte(), rawCommand[4]);
		assertEquals("Wrong command length3", 5, rawCommand.length);

		command = connection.getNextSentCommand();
		assertNotNull("No command4", command);
		rawCommand = command.getRawCommand();

		assertEquals("Incorrect Header4", DIRECT_COMMAND_WITH_REPLY, rawCommand[0]);
		assertEquals("Wrong Command Byte4", CommandByte.GET_INPUT_VALUES.getByte(), rawCommand[1]);
		assertEquals("Wrong port4", PORT_NR_0, rawCommand[2]);
		assertEquals("Wrong command length4", 3, rawCommand.length);
	}

	public void testSetSensorModeUltraSonic() {
		MindstormTestConnection connection = new MindstormTestConnection();
		connection.init();
		NXTI2CUltraSonicSensor sensor = new NXTI2CUltraSonicSensor(connection);

		sensor.getValue();

		MindstormCommand command = connection.getNextSentCommand();
		assertNotNull("No command", command);
		byte[] rawCommand = command.getRawCommand();

		assertEquals("Incorrect Header", DIRECT_COMMAND_WITH_REPLY, rawCommand[0]);
		assertEquals("Wrong command byte", CommandByte.SET_INPUT_MODE.getByte(), rawCommand[1]);
		assertEquals("Wrong port", PORT_NR_3, rawCommand[2]);
		assertEquals("Wrong sensor type", NXTSensorType.LOW_SPEED_9V.getByte(), rawCommand[3]);
		assertEquals("Wrong sensor mode", NXTSensorMode.RAW.getByte(), rawCommand[4]);
		assertEquals("Wrong command length", 5, rawCommand.length);
	}

	public void testGetI2CSensorValueLSReadOnly() {
		MindstormTestConnection connection = new MindstormTestConnection();
		connection.init();
		NXTI2CUltraSonicSensor sensor = new NXTI2CUltraSonicSensor(connection);

		sensor.getValue();
		MindstormCommand command = null;
		MindstormCommand firstCommand = connection.getNextSentCommand();
		while(firstCommand != null) {
			command = firstCommand;
			firstCommand = connection.getNextSentCommand();
		}

		assertNotNull("No command", command);
		byte[] rawCommand = command.getRawCommand();

		assertEquals("Incorrect Header", DIRECT_COMMAND_WITH_REPLY, rawCommand[0]);
		assertEquals("Wrong command byte", CommandByte.LS_READ.getByte(), rawCommand[1]);
		assertEquals("Wrong port", PORT_NR_3, rawCommand[2]);
		assertEquals("Wrong command length", 3, rawCommand.length);
	}

	public void testGetI2CSensorValue() {
		MindstormTestConnection connection = new MindstormTestConnection();
		connection.init();
		NXTI2CUltraSonicSensor sensor = new NXTI2CUltraSonicSensor(connection);

		sensor.getValue();
		MindstormCommand command = connection.getNextSentCommand();
		assertNotNull("No command1a", command);
		byte[] rawCommand = command.getRawCommand();
		while( (rawCommand[1] != CommandByte.LS_WRITE.getByte()) && (command != null) ) {
			command = connection.getNextSentCommand();
			assertNotNull("No command1b", command);
			rawCommand = command.getRawCommand();
		}
		command = connection.getNextSentCommand();
		assertNotNull("No command1c", command);
		rawCommand = command.getRawCommand();
		while( (rawCommand[1] != CommandByte.LS_WRITE.getByte()) && (command != null) ) {
			command = connection.getNextSentCommand();
			assertNotNull("No command1d", command);
			rawCommand = command.getRawCommand();
		}

		assertEquals("Incorrect Header", DIRECT_COMMAND_WITHOUT_REPLY, rawCommand[0]);
		assertEquals("Wrong command byte", CommandByte.LS_WRITE.getByte(), rawCommand[1]);
		assertEquals("Wrong port", PORT_NR_3, rawCommand[2]);
		assertEquals("Wrong Tx data length", 2, rawCommand[3]);
		assertEquals("Wrong Rx data length", 1, rawCommand[4]);
		assertEquals("Wrong Tx address", ULTRASONIC_ADDRESS, rawCommand[5]);
		assertEquals("Wrong Tx register", SENSOR_REGISTER_RESULT1, rawCommand[6]);
		assertEquals("Wrong command length", 7, rawCommand.length);

		command = connection.getNextSentCommand();
		assertNotNull("No command2", command);
		rawCommand = command.getRawCommand();

		assertEquals("Incorrect Header2", DIRECT_COMMAND_WITH_REPLY, rawCommand[0]);
		assertEquals("Wrong command byte2", CommandByte.LS_GET_STATUS.getByte(), rawCommand[1]);
		assertEquals("Wrong port2", PORT_NR_3, rawCommand[2]);
		assertEquals("Wrong command length2", 3, rawCommand.length);

		command = connection.getNextSentCommand();
		assertNotNull("No command3a", command);
		rawCommand = command.getRawCommand();
		while(rawCommand[1] == CommandByte.LS_GET_STATUS.getByte()) {
			command = connection.getNextSentCommand();
			assertNotNull("No command3b", command);
			rawCommand = command.getRawCommand();
		}

		assertEquals("Incorrect Header3", DIRECT_COMMAND_WITH_REPLY, rawCommand[0]);
		assertEquals("Wrong command byte3", CommandByte.LS_READ.getByte(), rawCommand[1]);
		assertEquals("Wrong port3", PORT_NR_3, rawCommand[2]);
		assertEquals("Wrong command length3", 3, rawCommand.length);
	}

	public void testGetI2CSensorValueFullCommunication() {
		MindstormTestConnection connection = new MindstormTestConnection();
		connection.init();
		NXTI2CUltraSonicSensor sensor = new NXTI2CUltraSonicSensor(connection);

		sensor.getValue();

		MindstormCommand command = connection.getNextSentCommand();
		assertNotNull("No command", command);
		byte[] rawCommand = command.getRawCommand();

		assertEquals("Incorrect Header", DIRECT_COMMAND_WITH_REPLY, rawCommand[0]);
		assertEquals("Wrong command byte", CommandByte.SET_INPUT_MODE.getByte(), rawCommand[1]);
		assertEquals("Wrong port", PORT_NR_3, rawCommand[2]);
		assertEquals("Wrong sensor type", NXTSensorType.LOW_SPEED_9V.getByte(), rawCommand[3]);
		assertEquals("Wrong sensor mode", NXTSensorMode.RAW.getByte(), rawCommand[4]);
		assertEquals("Wrong command length", 5, rawCommand.length);

		command = connection.getNextSentCommand();
		assertNotNull("No command2", command);
		rawCommand = command.getRawCommand();

		assertEquals("Incorrect Header2", DIRECT_COMMAND_WITHOUT_REPLY, rawCommand[0]);
		assertEquals("Wrong CommandByte2", CommandByte.RESET_INPUT_SCALED_VALUE.getByte(), rawCommand[1]);
		assertEquals("Wrong Port2", PORT_NR_3, rawCommand[2]);
		assertEquals("Wrong command length2", 3, rawCommand.length);

		command = connection.getNextSentCommand();
		assertNotNull("No command3", command);
		rawCommand = command.getRawCommand();

		assertEquals("Incorrect Header3", DIRECT_COMMAND_WITH_REPLY, rawCommand[0]);
		assertEquals("Wrong command byte3", CommandByte.SET_INPUT_MODE.getByte(), rawCommand[1]);
		assertEquals("Wrong port3", PORT_NR_3, rawCommand[2]);
		assertEquals("Wrong sensor type3", NXTSensorType.LOW_SPEED_9V.getByte(), rawCommand[3]);
		assertEquals("Wrong sensor mode3", NXTSensorMode.RAW.getByte(), rawCommand[4]);
		assertEquals("Wrong command length3", 5, rawCommand.length);

		command = connection.getNextSentCommand();
		assertNotNull("No command4", command);
		rawCommand = command.getRawCommand();

		assertEquals("Incorrect Header4", DIRECT_COMMAND_WITHOUT_REPLY, rawCommand[0]);
		assertEquals("Wrong command byte4", CommandByte.LS_WRITE.getByte(), rawCommand[1]);
		assertEquals("Wrong port4", PORT_NR_3, rawCommand[2]);
		assertEquals("Wrong Tx data length4", 2, rawCommand[3]);
		assertEquals("Wrong Rx data length4", 1, rawCommand[4]);
		assertEquals("Wrong Tx address4", ULTRASONIC_ADDRESS, rawCommand[5]);
		assertEquals("Wrong Tx register4", 0x00, rawCommand[6]);
		assertEquals("Wrong command length4", 7, rawCommand.length);

		command = connection.getNextSentCommand();
		assertNotNull("No command5", command);
		rawCommand = command.getRawCommand();

		assertEquals("Incorrect Header5", DIRECT_COMMAND_WITH_REPLY, rawCommand[0]);
		assertEquals("Wrong command byte5", CommandByte.LS_GET_STATUS.getByte(), rawCommand[1]);
		assertEquals("Wrong port5", PORT_NR_3, rawCommand[2]);
		assertEquals("Wrong command length5", 3, rawCommand.length);

		command = connection.getNextSentCommand();
		assertNotNull("No command6a", command);
		rawCommand = command.getRawCommand();
		while(rawCommand[1] == CommandByte.LS_GET_STATUS.getByte()) {
			command = connection.getNextSentCommand();
			assertNotNull("No command6b", command);
			rawCommand = command.getRawCommand();
		}

		assertEquals("Incorrect Header6", DIRECT_COMMAND_WITH_REPLY, rawCommand[0]);
		assertEquals("Wrong command byte6", CommandByte.LS_READ.getByte(), rawCommand[1]);
		assertEquals("Wrong port6", PORT_NR_3, rawCommand[2]);
		assertEquals("Wrong command length6", 3, rawCommand.length);

		command = connection.getNextSentCommand();
		assertNotNull("No command7", command);
		rawCommand = command.getRawCommand();

		assertEquals("Incorrect Header7", DIRECT_COMMAND_WITHOUT_REPLY, rawCommand[0]);
		assertEquals("Wrong command byte7", CommandByte.LS_WRITE.getByte(), rawCommand[1]);
		assertEquals("Wrong port7", PORT_NR_3, rawCommand[2]);
		assertEquals("Wrong Tx data length7", 2, rawCommand[3]);
		assertEquals("Wrong Rx data length7", 1, rawCommand[4]);
		assertEquals("Wrong Tx address7", ULTRASONIC_ADDRESS, rawCommand[5]);
		assertEquals("Wrong Tx register7", SENSOR_REGISTER_RESULT1, rawCommand[6]);
		assertEquals("Wrong command length7", 7, rawCommand.length);

		command = connection.getNextSentCommand();
		assertNotNull("No command8", command);
		rawCommand = command.getRawCommand();

		assertEquals("Incorrect Header8", DIRECT_COMMAND_WITH_REPLY, rawCommand[0]);
		assertEquals("Wrong command byte8", CommandByte.LS_GET_STATUS.getByte(), rawCommand[1]);
		assertEquals("Wrong port8", PORT_NR_3, rawCommand[2]);
		assertEquals("Wrong command length8", 3, rawCommand.length);

		command = connection.getNextSentCommand();
		assertNotNull("No command9a", command);
		rawCommand = command.getRawCommand();
		while(rawCommand[1] == CommandByte.LS_GET_STATUS.getByte()) {
			command = connection.getNextSentCommand();
			assertNotNull("No command9b", command);
			rawCommand = command.getRawCommand();
		}

		assertEquals("Incorrect Header9", DIRECT_COMMAND_WITH_REPLY, rawCommand[0]);
		assertEquals("Wrong command byte9", CommandByte.LS_READ.getByte(), rawCommand[1]);
		assertEquals("Wrong port9", PORT_NR_3, rawCommand[2]);
		assertEquals("Wrong command length9", 3, rawCommand.length);
	}
}