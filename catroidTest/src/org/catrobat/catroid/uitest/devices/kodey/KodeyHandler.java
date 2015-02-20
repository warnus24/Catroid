/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
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

package org.catrobat.catroid.uitest.devices.kodey;


import org.catrobat.catroid.test.utils.BluetoothConnectionWrapper;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class KodeyHandler implements BluetoothConnectionWrapper.BTClientHandler {

	@Override
	public void handle(InputStream inStream, OutputStream outStream) throws IOException {
		byte[] messageBuffer = new byte[3];

		while (inStream.read(messageBuffer, 0, 3) != -1) {
			handleClientMessage(messageBuffer, outStream);
		}
	}

	public static final int PIN_SPEAKER_OUT = 147;

	public static final int PIN_RED_1 = 148;
	public static final int PIN_GREEN_1 = 149;
	public static final int PIN_BLUE_1 = 150;
	public static final int PIN_RED_2 = 151;
	public static final int PIN_GREEN_2 = 152;
	public static final int PIN_BLUE_2 = 153;

	public static final int MOVE_LEFT_MOTOR_BACKWORD = 154;
	public static final int MOVE_LEFT_MOTOR_FORWARD = 155;
	public static final int MOVE_RIGHT_MOTOR_FORWARD = 156;
	public static final int MOVE_RIGHT_MOTOR_BACKWORD = 157;

	private void handleClientMessage(byte[] message, OutputStream outStream) throws IOException {



//		outStream.write(getMessageLength(responseMessage));
//		outStream.write(responseMessage);
//		outStream.flush();
	}
}
