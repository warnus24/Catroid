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

package org.catrobat.catroid.test.devices.albert;


import android.test.AndroidTestCase;

import org.catrobat.catroid.common.bluetooth.ConnectionDataLogger;
import org.catrobat.catroid.devices.albert.Albert;
import org.catrobat.catroid.devices.albert.AlbertImpl;

public class RobotAlbertReceiveCommandsTest extends AndroidTestCase {

	public void testLeftDistance(){
		Albert albert = new AlbertImpl();
		ConnectionDataLogger logger= ConnectionDataLogger.createLocalConnectionLogger();
		albert.setConnection(logger.getConnectionProxy());
		albert.setFrontLed(1);
		byte[] send = logger.getNextSentMessage();
		checkSendCommand(1,send,17);
	}

	private void checkSendCommand(int target, byte[] send, int... items){
		assertEquals("Error: Albert test HEADER1 not found!",(byte) 0xAA, send[0] );
		assertEquals("Error: Albert test HEADER2 not found!",(byte) 0x55, send[1] );
		assertEquals("Error: Albert test send command length false!", 22, send.length );
		assertEquals("Error: Albert test TAIL1 not found!",(byte) 0x0D, send[20] );
		assertEquals("Error: Albert test TAIL1 not found!",(byte) 0x0A, send[21] );
		for (int item : items) {
			assertEquals("Error: Albert test wrong value send!",(byte) target, send[item]);
		}
	}

}
