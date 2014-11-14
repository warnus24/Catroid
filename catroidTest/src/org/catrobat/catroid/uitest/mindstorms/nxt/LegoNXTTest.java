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
package org.catrobat.catroid.uitest.mindstorms.nxt;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.ListView;

import org.catrobat.catroid.R;
import org.catrobat.catroid.bluetooth.BTConnectDeviceActivity;
import org.catrobat.catroid.bluetooth.BTDeviceFactory;
import org.catrobat.catroid.bluetooth.BTDeviceService;
import org.catrobat.catroid.bluetooth.BluetoothConnection;
import org.catrobat.catroid.common.CatrobatService;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.common.ScreenValues;
import org.catrobat.catroid.common.ServiceProvider;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.WhenScript;
import org.catrobat.catroid.content.bricks.LegoNxtMotorActionBrick;
import org.catrobat.catroid.content.bricks.LegoNxtMotorStopBrick;
import org.catrobat.catroid.content.bricks.LegoNxtMotorTurnAngleBrick;
import org.catrobat.catroid.content.bricks.LegoNxtPlayToneBrick;
import org.catrobat.catroid.content.bricks.SetLookBrick;
import org.catrobat.catroid.content.bricks.WaitBrick;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.lego.mindstorm.nxt.LegoNXT;
import org.catrobat.catroid.lego.mindstorm.nxt.LegoNXTImpl;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.test.mindstorms.nxt.MindstormTestConnection;
import org.catrobat.catroid.test.utils.BluetoothConnectionWrapper;
import org.catrobat.catroid.test.utils.TestUtils;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.ProjectActivity;
import org.catrobat.catroid.uitest.annotation.Device;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.Reflection;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

public class LegoNXTTest extends BaseActivityInstrumentationTestCase<MainMenuActivity> {
	private static final int IMAGE_FILE_ID = org.catrobat.catroid.test.R.raw.icon;
	private static final int MOTOR_ACTION = 0;
	private static final int MOTOR_STOP = 1;
	private static final int MOTOR_TURN = 2;
	private static final int PLAY_TONE = 3;

	// needed for testdevices
	// Bluetooth server is running with a name that starts with 'kitty'
	// e.g. kittyroid-0, kittyslave-0
	private static final String PAIRED_BLUETOOTH_SERVER_DEVICE_NAME = "NXT";

	private final String projectName = UiTestUtils.PROJECTNAME1;
	private final String spriteName = "testSprite";

	ArrayList<int[]> commands = new ArrayList<int[]>();

	BluetoothConnectionWrapper wrappedConnection;

	public LegoNXTTest() {
		super(MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		UiTestUtils.prepareStageForTest();

		Context applicationContext = getInstrumentation().getTargetContext().getApplicationContext();
		SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(applicationContext).edit();
		editor.clear();
		editor.apply();
	}

	// This test requires the NXTBTTestServer to be running or a LegoNXT Robot to run! Check connect string to see if you connect to the right device!
	@Device
	public void testNXTFunctionality() {
		createTestproject(projectName);

		TestUtils.enableBluetooth();

		ArrayList<String> autoConnectIDs = new ArrayList<String>();
		autoConnectIDs.add("IM_NOT_A_MAC_ADDRESS");
		Reflection.setPrivateField(BTConnectDeviceActivity.class, "autoConnectIDs", autoConnectIDs);

		BTConnectDeviceActivity.setDeviceFactory(new BTDeviceFactory() {
			@Override
			public <T extends BTDeviceService> BTDeviceService createDevice(Class<T> service, Context context) {
				if (service == BTDeviceService.LEGO_NXT) {
					return new LegoNXTImpl(context);
				}

				assertFalse("Started service must be lego nxt", false);
				return null;
			}

			@Override
			public <T extends BTDeviceService> BluetoothConnection createBTConnectionForDevice(Class<T> service, String address, UUID deviceUUID, Context applicationContext) {
				wrappedConnection = new BluetoothConnectionWrapper(address, deviceUUID, false);
				return wrappedConnection;
			}
		});

		solo.clickOnText(solo.getString(R.string.main_menu_continue));
		solo.waitForActivity(ProjectActivity.class.getSimpleName());
		UiTestUtils.clickOnBottomBar(solo, R.id.button_play);
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
		solo.sleep(8000);
		solo.assertCurrentActivity("Not in stage - connection to bluetooth-device failed", StageActivity.class);

		solo.clickOnScreen(ScreenValues.SCREEN_WIDTH / 2, ScreenValues.SCREEN_HEIGHT / 2);
		solo.sleep(10000);

		ArrayList<byte[]> executedCommands = wrappedConnection.getSentMessages(2, true);
		assertEquals("Commands seem to have not been executed! Connected to correct device??", commands.size(),
				executedCommands.size());

		int i = 0;
		for (int[] item : commands) {
			switch (item[0]) {
				case MOTOR_ACTION:
					assertEquals("Wrong motor was used!", item[1], executedCommands.get(i)[2]);
					assertEquals("Wrong speed was used!", item[2], executedCommands.get(i)[3]);
					break;
				case MOTOR_STOP:
					assertEquals("Wrong motor was used!", item[1], executedCommands.get(i)[2]);
					assertEquals("Motor didn't actually stop!", 0, executedCommands.get(i)[3]);
					break;
				case MOTOR_TURN:
					assertEquals("Wrong motor was used!", item[1], executedCommands.get(i)[2]);
					int turnValue = (0x000000FF & executedCommands.get(i)[8]); //unsigned types would be too smart for java, sorry no chance mate!
					turnValue += ((0x000000FF & executedCommands.get(i)[9]) << 8);
					turnValue += ((0x000000FF & executedCommands.get(i)[10]) << 16);
					turnValue += ((0x000000FF & executedCommands.get(i)[11]) << 24);

					int turnSpeed = 30; //fixed value in Brick, however LegoBot needs negative speed instead of negative angles
					if (item[2] < 0) {
						item[2] += -2 * item[2];
						turnSpeed -= 2 * turnSpeed;
					}

					assertEquals("Motor turned wrong angle", item[2], turnValue);
					assertEquals("Motor didn't turn with fixed value 30!", turnSpeed, executedCommands.get(i)[3]);
					break;
				case PLAY_TONE:
					int frequency = (0x000000FF & executedCommands.get(i)[2]);
					frequency += ((0x000000FF & executedCommands.get(i)[3]) << 8);
					assertEquals("wrong frequency used", item[1], frequency);

					int duration = (0x000000FF & executedCommands.get(i)[4]);
					duration += ((0x000000FF & executedCommands.get(i)[5]) << 8);
					assertEquals("wrong duration used", item[2], duration);
			}
			i++;
		}
	}

	@Device
	public void testNXTConnectionDialogGoBack() {
		createTestproject(projectName);

		ArrayList<String> autoConnectIDs = new ArrayList<String>();
		autoConnectIDs.add("IM_NOT_A_MAC_ADDRESS");
		BTConnectDeviceActivity deviceListActivity = new BTConnectDeviceActivity();
		Reflection.setPrivateField(deviceListActivity, "autoConnectIDs", autoConnectIDs);

		TestUtils.enableBluetooth();

		solo.clickOnText(solo.getString(R.string.main_menu_continue));
		solo.waitForActivity(ProjectActivity.class.getSimpleName());
		UiTestUtils.clickOnBottomBar(solo, R.id.button_play);
		solo.sleep(1000);
		solo.assertCurrentActivity("Devicelist not shown!", BTConnectDeviceActivity.class);
		solo.goBack();
		solo.sleep(1000);
		UiTestUtils.clickOnBottomBar(solo, R.id.button_play);
		solo.sleep(1000);
		solo.assertCurrentActivity("Devicelist not shown!", BTConnectDeviceActivity.class);

	}

	private void createTestproject(String projectName) {
		Sprite firstSprite = new Sprite(spriteName);
		Script startScript = new StartScript();
		Script whenScript = new WhenScript();
		SetLookBrick setLookBrick = new SetLookBrick();

		LegoNxtMotorActionBrick legoMotorActionBrick = new LegoNxtMotorActionBrick(
				LegoNxtMotorActionBrick.Motor.MOTOR_B_C, 100);
		commands.add(new int[] { MOTOR_ACTION, 1, 100 });
		commands.add(new int[] { MOTOR_ACTION, 2, 100 });
		WaitBrick firstWaitBrick = new WaitBrick(500);

		LegoNxtMotorStopBrick legoMotorStopBrick = new LegoNxtMotorStopBrick(
				LegoNxtMotorStopBrick.Motor.MOTOR_B_C);
		commands.add(new int[] { MOTOR_STOP, 1 });
		commands.add(new int[] { MOTOR_STOP, 2 });
		WaitBrick secondWaitBrick = new WaitBrick(500);

		LegoNxtMotorTurnAngleBrick legoMotorTurnAngleBrick = new LegoNxtMotorTurnAngleBrick(
				LegoNxtMotorTurnAngleBrick.Motor.MOTOR_C, 515);
		commands.add(new int[] { MOTOR_TURN, 2, 515 });

		WaitBrick thirdWaitBrick = new WaitBrick(500);
		LegoNxtPlayToneBrick legoPlayToneBrick = new LegoNxtPlayToneBrick(5000, 1000);
		//Tone does not return a command
		commands.add(new int[] { PLAY_TONE, 5000, 1000 });

		whenScript.addBrick(legoMotorActionBrick);
		whenScript.addBrick(firstWaitBrick);
		whenScript.addBrick(legoMotorStopBrick);
		whenScript.addBrick(secondWaitBrick);
		whenScript.addBrick(legoMotorTurnAngleBrick);
		whenScript.addBrick(thirdWaitBrick);
		whenScript.addBrick(legoPlayToneBrick);

		startScript.addBrick(setLookBrick);
		firstSprite.addScript(startScript);
		firstSprite.addScript(whenScript);

		ArrayList<Sprite> spriteList = new ArrayList<Sprite>();
		spriteList.add(firstSprite);
		Project project = UiTestUtils.createProject(projectName, spriteList, getActivity());

		String imageName = "image";
		File image = UiTestUtils.saveFileToProject(projectName, imageName, IMAGE_FILE_ID, getInstrumentation()
				.getContext(), UiTestUtils.FileTypes.IMAGE);

		LookData lookData = new LookData();
		lookData.setLookFilename(image.getName());
		lookData.setLookName(imageName);
		setLookBrick.setLook(lookData);
		firstSprite.getLookDataList().add(lookData);

		StorageHandler.getInstance().saveProject(project);
	}
}