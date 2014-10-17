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
package org.catrobat.catroid.test.bluetooth;

import android.bluetooth.BluetoothSocket;
import android.test.AndroidTestCase;
import android.util.Log;

import org.catrobat.catroid.bluetooth.BluetoothConnection;
import org.catrobat.catroid.test.utils.TestUtils;
import org.catrobat.catroid.uitest.annotation.Device;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class BluetoothConnectionTest extends AndroidTestCase {

	private static final String SERVER_MAC_ADDRESS = "5C:51:4F:7A:DF:EA"; //"00:1A:7D:DA:71:05";
	private static final UUID TEST_SERVER_UUID = UUID.fromString("fd2835bb-9d80-41e0-9721-5372b90342da");

	public static final byte SERVER_CMD_REPLY_SENT_MESSAGE = 0;

	public static final String TAG = BluetoothConnectionTest.class.getSimpleName();


	@Override
	protected void setUp() throws Exception {
		super.setUp();

		TestUtils.enableBluetooth();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();

		TestUtils.disableBluetooth();
	}

	@Device
	public void testSendAndReceiveMessage() throws IOException {

		TestUtils.enableBluetooth();

		BluetoothConnection connection = new BluetoothConnection(SERVER_MAC_ADDRESS, TEST_SERVER_UUID);
		BluetoothConnection.State connectionState = connection.connect();

		assertEquals("connecting to bluetooth device failed.", BluetoothConnection.State.CONNECTED, connectionState);

		BluetoothSocket btSocket = connection.getBluetoothSocket();
		OutputStream btOutStream = btSocket.getOutputStream();
		InputStream btInStream = btSocket.getInputStream();

		final byte[] testMessage = {SERVER_CMD_REPLY_SENT_MESSAGE, 1, 2, 4};

		btOutStream.write(testMessage.length);
		btOutStream.write(testMessage);

		int expectedLength = btInStream.read();
		Log.d(TAG, "expected message length: " + expectedLength);
		byte [] returnedMessage = new byte[expectedLength];
		btInStream.read(returnedMessage, 0, expectedLength);

		assertMessageEquals(testMessage, returnedMessage);

		connection.disconnect();
	}

	private void assertMessageEquals(byte[] expected, byte[] actual) {

		assertEquals("Bluetooth message is not equal, because of different message length.", expected.length, actual.length);

		for (int i = 0; i < expected.length; i++) {
			assertEquals("Bluetooth message is not equal, byte " + i + " is different", expected[i], actual[i]);
		}
	}
}