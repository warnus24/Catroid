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

package org.catrobat.catroid.test.arduino;

import android.test.AndroidTestCase;

import org.catrobat.catroid.arduino.ArduinoConnection;
import org.catrobat.catroid.arduino.ArduinoConnectionImpl;
import org.catrobat.catroid.test.utils.Reflection;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;

public class ArduinoConnectionTest extends AndroidTestCase {

	public void testSend() {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();

		ArduinoConnectionImpl connection = new ArduinoConnectionImpl(null);
		Reflection.setPrivateField(connection, "arduinoOutputStream", outStream);

		byte[] message = {1,3,'H'};

		connection.send(message);
	}

	public void testSendAndReceive() {

		byte[] inputBuffer = new byte[] {'~', 4, 'D', 1, 3, 'H'};
		ByteArrayInputStream inStream = new ByteArrayInputStream(inputBuffer);
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();

		ArduinoConnectionImpl connection = new ArduinoConnectionImpl(null);
		Reflection.setPrivateField(connection, "arduinoOutputStream", outStream);
		Reflection.setPrivateField(connection, "arduinoInputStream", new DataInputStream(inStream));

		byte[] message = {1,3,'H'};
		//set pin 13 high
		connection.send(message);

		message[2] = 'D';

		byte[] receivedBytes = connection.sendAndReceive(message);

		byte[] sentBytes = outStream.toByteArray();


		assertEquals("Wrong message length. Before there should be a header with 2 bytes defining the message length",
				message.length, sentBytes.length);

		for (int i = 0; i < message.length; i++) {
			assertEquals("Byte " + i + " is different", message[i], sentBytes[i]);
		}

		assertEquals("Wrong message length. Before there should be a header with 2 bytes defining the message length",
				inputBuffer.length, receivedBytes.length);

		for (int i = 0; i < receivedBytes.length; i++) {
			assertEquals("Byte " + i + " is different", inputBuffer[i], receivedBytes[i]);
		}
	}
}
