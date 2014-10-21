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
package org.catrobat.catroid.uitest.bluetooth;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.widget.ListView;

import org.catrobat.catroid.bluetooth.BTConnectDeviceActivity;
import org.catrobat.catroid.bluetooth.BTDeviceConnector;
import org.catrobat.catroid.bluetooth.BTDeviceFactory;
import org.catrobat.catroid.bluetooth.BTDeviceService;
import org.catrobat.catroid.bluetooth.BluetoothConnection;
import org.catrobat.catroid.bluetooth.EmptyActivity;
import org.catrobat.catroid.common.CatrobatService;
import org.catrobat.catroid.common.ServiceProvider;
import org.catrobat.catroid.test.utils.TestUtils;
import org.catrobat.catroid.uitest.annotation.Device;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class BluetoothConnectorTest extends BaseActivityInstrumentationTestCase<EmptyActivity> {

	public BluetoothConnectorTest() {
		super(EmptyActivity.class);
	}

	// needed for testdevices
	// Bluetooth server is running with a name that starts with 'kitty'
	// e.g. kittyroid-0, kittyslave-0
	private static final String PAIRED_BLUETOOTH_SERVER_DEVICE_NAME = "kitty";
//	private static final String PAIRED_BLUETOOTH_SERVER_DEVICE_NAME = "ASUS";

	// needed for testdevices
	// unavailable device is paired with a name that starts with 'SWEET'
	// e.g. SWEETHEART

//	private static final String PAIRED_UNAVAILABLE_DEVICE_NAME = "SWEET";
//	private static final String PAIRED_UNAVAILABLE_DEVICE_MAC = "00:23:4D:F5:A6:18";

	private static final UUID COMMON_BT_TEST_UUID = UUID.fromString("fd2835bb-9d80-41e0-9721-5372b90342da");

	public static final Class<BluetoothTestService> TEST_SERVICE  = BluetoothTestService.class;


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
	public void testBluetoothConnector() throws IOException {

		final int requestCode = 11;

		EmptyActivity emptyActivity = getActivity();
		BTConnectDeviceActivity.setDeviceFactory(new BTDeviceTestFactory());

		BTDeviceConnector connector = ServiceProvider.getService(CatrobatService.BLUETOOTH_DEVICE_CONNECTOR);
		connector.connectDevice(TEST_SERVICE, emptyActivity, requestCode);

		solo.waitForActivity(BTConnectDeviceActivity.class);
		solo.sleep(2000);

		ListView deviceList = solo.getCurrentViews(ListView.class).get(0);
		String connectedDeviceName = null;
		for (int i = 0; i < deviceList.getCount(); i++) {
			String deviceName = (String) deviceList.getItemAtPosition(i);
			if (deviceName.startsWith(PAIRED_BLUETOOTH_SERVER_DEVICE_NAME)) {
				connectedDeviceName = deviceName;
				break;
			}
		}

		solo.clickOnText(connectedDeviceName);

		solo.sleep(20000); //yes, has to be that long! waiting for auto connection timeout!

		Instrumentation.ActivityResult result = getActivity().getActivityResult(requestCode);
		assertEquals("Result should be OK", Activity.RESULT_OK, result.getResultCode());

		BluetoothTestService service = ServiceProvider.getService(TEST_SERVICE);

		assertNotNull("Service already registered, should not be null here.", service);
		service.connect();

		byte[] expectedMessage = new byte[] {1,2,3};

		service.sendTestMessage(expectedMessage);
		solo.sleep(2000);
		byte[] receivedMessage = service.receiveTestMessage();
		assertMessageEquals(expectedMessage, receivedMessage);

	}

	private void assertMessageEquals(byte[] expected, byte[] actual) {

		assertEquals("Bluetooth message is not equal, because of different message length.", expected.length, actual.length);

		for (int i = 0; i < expected.length; i++) {
			assertEquals("Bluetooth message is not equal, byte " + i + " is different", expected[i], actual[i]);
		}
	}

	private class BluetoothTestService implements BTDeviceService {

		private boolean isConnected = false;
		BluetoothConnection connection;

		private InputStream inStream;
		private OutputStream outStream;

		public boolean isConnected() {
			return isConnected;
		}

		@Override
		public String getName() {
			return "BT Test Service";
		}

		@Override
		public Class<? extends BTDeviceService> getServiceType() {
			return BluetoothTestService.class;
		}

		@Override
		public void setConnection(BluetoothConnection connection) {
			this.connection = connection;
		}

		public void connect() throws IOException{
			inStream = connection.getBluetoothSocket().getInputStream();
			outStream = connection.getBluetoothSocket().getOutputStream();

			isConnected = true;
		}

		@Override
		public void disconnect() {
			connection.disconnect();
			isConnected = false;
		}

		@Override
		public UUID getBluetoothDeviceUUID() {
			return COMMON_BT_TEST_UUID;
		}

		public void sendTestMessage(byte[] message) throws IOException {

			outStream.write(message.length);
			outStream.write(message);
			outStream.flush();
		}

		public byte[] receiveTestMessage() throws IOException {
			byte[] messageLengthBuffer = new byte[1];

			inStream.read(messageLengthBuffer, 0, 1);
			int expectedMessageLength = messageLengthBuffer[0];

			byte[] payload = new byte[expectedMessageLength];

			inStream.read(payload, 0, expectedMessageLength);

			return payload;
		}

		@Override
		public void initialise() {

		}

		@Override
		public void start() {

		}

		@Override
		public void pause() {

		}

		@Override
		public void destroy() {

		}
	}

	private class BTDeviceTestFactory implements BTDeviceFactory {

		@Override
		public <T extends BTDeviceService> BTDeviceService create(Class<T> service, Context context) {
			return new BluetoothTestService();
		}
	}
}
