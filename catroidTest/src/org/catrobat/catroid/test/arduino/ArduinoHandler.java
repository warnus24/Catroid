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

import org.catrobat.catroid.test.utils.BluetoothConnectionWrapper;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ArduinoHandler implements BluetoothConnectionWrapper.BTClientHandler {

	protected byte[] createResponseFromClientRequest(byte[] message) {
		byte[] reply;
		byte commandLowerPinNumber = message[0];
		byte commandHigherPinNumber = message[1];
		byte commandByte = message[2];

		if ((commandByte != 'D') || (commandByte != 'A')) {
			return null;
		}

		if (commandByte == 'D') {

			reply = new byte[] {'~', 4, 'D', 1, 3, 'H'};

		} else if (commandByte == 'A') {

			reply = new byte[] {'~', 7, 'A', 0, 0, 0, 0, 'H'};

		} else {
			reply = null;
		}

		return reply;
	}

	@Override
	public void handle(InputStream inStream, OutputStream outStream) throws IOException {
		byte[] messageLengthBuffer = new byte[2];

		while (inStream.read(messageLengthBuffer, 0, 2) != -1) {
			int expectedMessageLength = ((messageLengthBuffer[0] & 0xFF) | (messageLengthBuffer[1] & 0xFF) << 8);
			handleClientMessage(expectedMessageLength, new DataInputStream(inStream), outStream);
		}
	}

	private void handleClientMessage(int expectedMessageLength, DataInputStream inStream, OutputStream outStream) throws IOException {

		byte[] requestMessage = new byte[expectedMessageLength];

		inStream.readFully(requestMessage, 0, expectedMessageLength);

		byte[] responseMessage = createResponseFromClientRequest(requestMessage);

		if (responseMessage == null) {
			return;
		}

		outStream.write(getMessageLength(responseMessage));
		outStream.write(responseMessage);
		outStream.flush();
	}

	private byte[] getMessageLength(byte[] message) {

		byte[] messageLength = {
				(byte) (message.length & 0x00FF),
				(byte) ((message.length & 0xFF00) >> 8)
		};

		return messageLength;
	}
}
