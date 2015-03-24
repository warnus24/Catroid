/*
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2015 The Catrobat Team
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
package org.catrobat.catroid.uitest.devices.albert;

import android.test.AndroidTestCase;
import org.catrobat.catroid.common.bluetooth.ConnectionDataLogger;
import org.catrobat.catroid.common.bluetooth.models.AlbertModel;
import org.catrobat.catroid.devices.albert.Albert;
import org.catrobat.catroid.devices.albert.AlbertImpl;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class RobotAlbertTest extends AndroidTestCase {

	public RobotAlbertTest() {

	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		UiTestUtils.prepareStageForTest();
	}

	public void testFrontLed(){
		Albert albert = new AlbertImpl();
		ConnectionDataLogger logger= ConnectionDataLogger.createLocalConnectionLogger();
		albert.setConnection(logger.getConnectionProxy());
		albert.setBodyLed(150);
		byte[] send = logger.getNextSentMessage();
		assertEquals("front led test fail", 150, send[19]);
	}

	public void testSensorLeft(){
		AlbertModel albertModel = new AlbertModel();
		ConnectionDataLogger logger = ConnectionDataLogger.createLocalConnectionLoggerWithDeviceModel(albertModel);
		byte[] receive = logger.getNextReceivedMessage();

	}
//	// This test requires the AlbertTestServer to be running
//	@Device
//	public void testAlbertFunctionality() {
//		Log.d("TestRobotAlbert", "initialized BTDummyClient");
//
//		BTDummyClient dummy = new BTDummyClient();
//		dummy.initializeAndConnectToServer(BTDummyClient.SERVERDUMMYROBOTALBERT);
//
//		createTestproject(projectName);
//
//		//enable albert bricks, if disabled at start
//		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
//		if (!preferences.getBoolean("setting_robot_albert_bricks", false)) {
//			Log.d("RobotAlbertTest", "enabling albert bricks");
//			solo.clickOnMenuItem(solo.getString(R.string.settings));
//			solo.clickOnText(solo.getString(R.string.preference_title_enable_robot_albert_bricks));
//			solo.goBack();
//		}
//
//		solo.clickOnText(solo.getString(R.string.main_menu_continue));
//		solo.sleep(500);
//		solo.clickOnText(spriteName);
//		solo.sleep(500);
//		solo.clickOnText(solo.getString(R.string.scripts));
//		solo.sleep(1000);
//
//		solo.clickOnText("0.0");
//		solo.sleep(1000);
//		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_sensors));
//		solo.sleep(1000);
//		solo.waitForText(getActivity().getString(R.string.formula_editor_sensor_albert_robot_distance_left));
//		solo.clickOnText(getActivity().getString(R.string.formula_editor_sensor_albert_robot_distance_left));
//		solo.sleep(1000);
//		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_ok));
//
//		BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//		assertTrue("Bluetooth not supported on device", bluetoothAdapter != null);
//		if (!bluetoothAdapter.isEnabled()) {
//			bluetoothAdapter.enable();
//			solo.sleep(5000);
//		}
//
//		UiTestUtils.clickOnBottomBar(solo, R.id.button_play);
//		solo.sleep(2500);
//
//		ListView deviceList = solo.getCurrentViews(ListView.class).get(0);
//		String connectedDeviceName = null;
//		for (int i = 0; i < deviceList.getCount(); i++) {
//			String deviceName = (String) deviceList.getItemAtPosition(i);
//			if (deviceName.startsWith(PAIRED_BLUETOOTH_SERVER_DEVICE_NAME)) {
//				connectedDeviceName = deviceName;
//				break;
//			}
//		}
//		Log.d("Robot Albert Test", "connectedDeviceName=" + connectedDeviceName + "  deviceList.getItemAtPosition(0)"
//				+ deviceList.getItemAtPosition(0));
//		solo.clickOnText(connectedDeviceName);
//
//		solo.sleep(6000);
//		solo.assertCurrentActivity("Not in stage - connection to bluetooth-device failed", StageActivity.class);
//
//		double distanceLeft = (Double)userVariablesContainer.getUserVariable("p1", sprite).getValue();
//
//		solo.clickOnScreen(ScreenValues.SCREEN_WIDTH / 2, ScreenValues.SCREEN_HEIGHT / 2);
//		solo.sleep(5000);
//
//		ByteArrayBuffer receivedBufferOld = dummy.getReceivedFeedback();
//		ByteArrayBuffer receivedBuffer = removeSensorCommands(receivedBufferOld);
//
//		boolean ok = Arrays.equals(sendCommands.toByteArray(), receivedBuffer.toByteArray());
//
//		int lenRec = receivedBuffer.length();
//		int lenSent1 = sendCommands.length();
//
//		Log.d("TestRobotAlbert",
//				"lenRec=" + lenRec + "\nlenSent1=" + lenSent1 + "\nlenWithSensor=" + receivedBufferOld.length());
//		assertTrue("messages reveived and sent are not equal", ok == true);
//
//		Log.d("TestRobotAlbert", "till now everthing is fine. Check for Sensordata...");
//		//BluetoothServer always sends a distance of 50.0
//		//if for whatever reason the previous attempt to read the current distance value
//		//failed, check again but this time read it directly from SensorData-class
//		if (distanceLeft != 50.0) {
//			distanceLeft = SensorData.getInstance().getValueOfLeftDistanceSensor();
//		}
//		Log.d("TestRobotAlbert", "Sensordata is OK (distanceLeft = " + distanceLeft + ")");
//		assertEquals("Variable has the wrong value after stage", 50.0, distanceLeft);
//
//		solo.sleep(500);
//		solo.goBack();
//		solo.sleep(100);
//		solo.goBack();
//		solo.sleep(100);
//		solo.goBack();
//		solo.sleep(100);
//		solo.goBack();
//	}
//
//	private void createTestproject(String projectName) {
//
//		Sprite firstSprite = new Sprite(spriteName);
//		Script startScript = new StartScript();
//		Script whenScript = new WhenScript();
//		SetLookBrick setLookBrick = new SetLookBrick();
//		sprite = firstSprite;
//
//		RobotAlbertMotorActionBrick legoMotorActionBrick = new RobotAlbertMotorActionBrick(
//				RobotAlbertMotorActionBrick.Motor.Both, 100);
//		ControlCommands commands = new ControlCommands();
//		commands.setSpeedOfLeftMotor(100);
//		commands.setSpeedOfRightMotor(100);
//		byte[] command = commands.getCommandMessage();
//		int commandLength = command.length;
//		sendCommands.append(command, 0, commandLength);
//
//		RobotAlbertFrontLedBrick robotAlbertFrontLedBrick = new RobotAlbertFrontLedBrick( new Formula(1));
//		commands.setFrontLed(1);
//		command = commands.getCommandMessage();
//		commandLength = command.length;
//		sendCommands.append(command, 0, commandLength);
//
//		RobotAlbertBuzzerBrick robotAlbertBuzzerBrick = new RobotAlbertBuzzerBrick( new Formula(50));
//		commands.setBuzzer(50);
//		command = commands.getCommandMessage();
//		commandLength = command.length;
//		sendCommands.append(command, 0, commandLength);
//
//		RobotAlbertRgbLedEyeActionBrick robotAlbertRgbLedEyeActionBrick = new RobotAlbertRgbLedEyeActionBrick(
//				RobotAlbertRgbLedEyeActionBrick.Eye.Both, new Formula(255), new Formula(255), new Formula(
//				255));
//		commands.setLeftEye(255, 255, 255);
//		commands.setRightEye(255, 255, 255);
//		command = commands.getCommandMessage();
//		commandLength = command.length;
//		sendCommands.append(command, 0, commandLength);
//
//		RobotAlbertBodyLedBrick robotAlbertBodyLedBrick = new RobotAlbertBodyLedBrick( new Formula(255));
//		commands.setBodyLed(255);
//		command = commands.getCommandMessage();
//		commandLength = command.length;
//		sendCommands.append(command, 0, commandLength);
//
//		SetVariableBrick setVariableBrick = new SetVariableBrick( 0.0);
//
//		whenScript.addBrick(legoMotorActionBrick);
//		whenScript.addBrick(robotAlbertFrontLedBrick);
//		whenScript.addBrick(robotAlbertBuzzerBrick);
//		whenScript.addBrick(robotAlbertRgbLedEyeActionBrick);
//		whenScript.addBrick(setVariableBrick);
//		whenScript.addBrick(robotAlbertBodyLedBrick);
//
//		startScript.addBrick(setLookBrick);
//		firstSprite.addScript(startScript);
//		firstSprite.addScript(whenScript);
//
//		ArrayList<Sprite> spriteList = new ArrayList<Sprite>();
//		spriteList.add(firstSprite);
//		Project project = UiTestUtils.createProject(projectName, spriteList, getActivity());
//		userVariablesContainer = project.getUserVariables();
//		userVariablesContainer.addProjectUserVariable("p1");
//		userVariablesContainer.addSpriteUserVariable("sprite_var1");
//
//		setVariableBrick = new SetVariableBrick( 0.0);
//
//		String imageName = "image";
//		File image = UiTestUtils.saveFileToProject(projectName, imageName, IMAGE_FILE_ID, getInstrumentation()
//				.getContext(), UiTestUtils.FileTypes.IMAGE);
//
//		LookData lookData = new LookData();
//		lookData.setLookFilename(image.getName());
//		lookData.setLookName(imageName);
//		setLookBrick.setLook(lookData);
//		firstSprite.getLookDataList().add(lookData);
//
//		StorageHandler.getInstance().saveProject(project);
//
//	}
//
//	private ByteArrayBuffer removeSensorCommands(ByteArrayBuffer buffer) {
//		int i;
//		int length = buffer.length();
//
//		ByteArrayBuffer array = new ByteArrayBuffer(0);
//
//		for (i = 0; i < length; i++) {
//			boolean found = false;
//
//			if (i < length - 51 && buffer.toByteArray()[i] == (byte) 0xAA && buffer.toByteArray()[i + 1] == (byte) 0x55
//					&& buffer.toByteArray()[i + 2] == (byte) 52 && buffer.toByteArray()[i + 50] == (byte) 0x0D
//					&& buffer.toByteArray()[i + 51] == (byte) 0x0A) {
//				i = i + 51;
//				found = true;
//			}
//
//			if (found == false) {
//				array.append(buffer.toByteArray()[i]);
//			}
//		}
//		return array;
//	}
}
