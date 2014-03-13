/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
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
package org.catrobat.catroid.uitest.content.brick;

import android.bluetooth.BluetoothSocket;
import android.widget.ListView;
import android.widget.Spinner;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.actions.ArduinoReceiveAction;
import org.catrobat.catroid.content.actions.ArduinoSendAction;
import org.catrobat.catroid.content.bricks.ArduinoSendBrick;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.adapter.BrickAdapter;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import java.util.ArrayList;

public class ArduinoSendBrickTest extends BaseActivityInstrumentationTestCase<ScriptActivity> {

	private Project project;
	private ArduinoSendBrick arduinoSendBrick;

	public ArduinoSendBrickTest() {
		super(ScriptActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		// normally super.setUp should be called first
		// but kept the test failing due to view is null
		// when starting in ScriptActivity
		createProject();
		super.setUp();
	}

	public void testArduinoSendBrick() {
		ListView dragDropListView = UiTestUtils.getScriptListView(solo);
		BrickAdapter adapter = (BrickAdapter) dragDropListView.getAdapter();

		int childrenCount = adapter.getChildCountFromLastGroup();
		int groupCount = adapter.getScriptCount();

		assertEquals("Incorrect number of bricks.", 2, dragDropListView.getChildCount());
		assertEquals("Incorrect number of bricks.", 1, childrenCount);

		ArrayList<Brick> projectBrickList = project.getSpriteList().get(0).getScript(0).getBrickList();
		assertEquals("Incorrect number of bricks.", 1, projectBrickList.size());

		assertEquals("Wrong Brick instance.", projectBrickList.get(0), adapter.getChild(groupCount - 1, 0));
		assertNotNull("TextView does not exist.", solo.getText(solo.getString(R.string.brick_arduino_select_value)));

	}

	public void testArduinoSendBrickClickOnPinSpinner() {
		String[] arduinoPins = getActivity().getResources().getStringArray(R.array.arduino_pin_chooser);
		assertTrue("Spinner items list too short!", arduinoPins.length == 11);

		int newPinSpinnerPosition = 0;
		Spinner currentPinSpinner = solo.getCurrentViews(Spinner.class).get(newPinSpinnerPosition);
		//Pin Spinner
		solo.pressSpinnerItem(newPinSpinnerPosition, 0);
		assertEquals("Wrong item in spinner!", arduinoPins[0], currentPinSpinner.getSelectedItem());
		solo.pressSpinnerItem(newPinSpinnerPosition, +1);
		assertEquals("Wrong item in spinner!", arduinoPins[1], currentPinSpinner.getSelectedItem());
		solo.pressSpinnerItem(newPinSpinnerPosition, +1);
		assertEquals("Wrong item in spinner!", arduinoPins[2], currentPinSpinner.getSelectedItem());
		solo.pressSpinnerItem(newPinSpinnerPosition, +1);
		assertEquals("Wrong item in spinner!", arduinoPins[3], currentPinSpinner.getSelectedItem());
		solo.pressSpinnerItem(newPinSpinnerPosition, +1);
		assertEquals("Wrong item in spinner!", arduinoPins[4], currentPinSpinner.getSelectedItem());
		solo.pressSpinnerItem(newPinSpinnerPosition, +1);
		assertEquals("Wrong item in spinner!", arduinoPins[5], currentPinSpinner.getSelectedItem());
		solo.pressSpinnerItem(newPinSpinnerPosition, +1);
		assertEquals("Wrong item in spinner!", arduinoPins[6], currentPinSpinner.getSelectedItem());
		solo.pressSpinnerItem(newPinSpinnerPosition, +1);
		assertEquals("Wrong item in spinner!", arduinoPins[7], currentPinSpinner.getSelectedItem());
		solo.pressSpinnerItem(newPinSpinnerPosition, +1);
		assertEquals("Wrong item in spinner!", arduinoPins[8], currentPinSpinner.getSelectedItem());
		solo.pressSpinnerItem(newPinSpinnerPosition, +1);
		assertEquals("Wrong item in spinner!", arduinoPins[9], currentPinSpinner.getSelectedItem());
		solo.pressSpinnerItem(newPinSpinnerPosition, +1);
		assertEquals("Wrong item in spinner!", arduinoPins[10], currentPinSpinner.getSelectedItem());
	}

	public void testSetPinToHighLowWithPinAndValueSpinner() {
		//turn on BT
		solo.sleep(500);
		ArduinoSendAction.turnOnBluetooth();
		solo.sleep(800);
		//check if the Spinner list element length is correct
		String[] arduinoPins = getActivity().getResources().getStringArray(R.array.arduino_pin_chooser);
		assertTrue("Spinner items list too short!", arduinoPins.length == 11);
		String[] arduinoValues = getActivity().getResources().getStringArray(R.array.arduino_value_chooser);
		assertTrue("Spinner items list too short!", arduinoValues.length == 2);

		//select Value from Spinner (H)
		solo.pressSpinnerItem(1, 1);
		//Pin Spinner (Pin 13)
		solo.pressSpinnerItem(0, 10);

		//send data via BT
		//		ArduinoSendAction.initBluetoothConnection("00:07:80:49:8B:61");
		ArduinoSendAction.initBluetoothConnection();

		char pinValue = ArduinoSendAction.getPinValue();
		char pinNumberLowerByte = ArduinoSendAction.getPinNumberLowerByte();
		char pinNumberHigherByte = ArduinoSendAction.getPinNumberHigherByte();
		BluetoothSocket outputBluetoothSocket = ArduinoSendAction.getBluetoothSocket();
		ArduinoSendAction.sendDataViaBluetoothSocket(outputBluetoothSocket, pinValue, pinNumberLowerByte,
				pinNumberHigherByte);

		//select Value form Spinner (L)
		solo.pressSpinnerItem(1, -1);
		//Pin Spinner (Pin 13)
		solo.pressSpinnerItem(0, 10);

		//send data via BT
		//		ArduinoSendAction.initBluetoothConnection("00:07:80:49:8B:61");
		ArduinoSendAction.initBluetoothConnection();
		pinValue = ArduinoSendAction.getPinValue();
		pinNumberLowerByte = ArduinoSendAction.getPinNumberLowerByte();
		pinNumberHigherByte = ArduinoSendAction.getPinNumberHigherByte();
		outputBluetoothSocket = ArduinoSendAction.getBluetoothSocket();
		ArduinoSendAction.sendDataViaBluetoothSocket(outputBluetoothSocket, pinValue, pinNumberLowerByte,
				pinNumberHigherByte);

		//turn off BT
		solo.sleep(500);
		ArduinoSendAction.turnOffBluetooth();
		solo.sleep(800);
	}

	public void testSetLedPinToHighWithSpinners() {
		//turn on BT
		solo.sleep(500);
		ArduinoSendAction.turnOnBluetooth();
		solo.sleep(800);

		//Pin Spinner (Pin 13)
		solo.pressSpinnerItem(0, 10);
		//select Value from Spinner (H)
		solo.pressSpinnerItem(1, 1);

		char pinValue = ArduinoSendAction.getPinValue();
		char pinNumberLowerByte = ArduinoSendAction.getPinNumberLowerByte();
		char pinNumberHigherByte = ArduinoSendAction.getPinNumberHigherByte();

		//send data via BT
		//		ArduinoSendAction.initBluetoothConnection("00:07:80:49:8B:61");
		ArduinoSendAction.initBluetoothConnection();
		BluetoothSocket tmpSocket = ArduinoSendAction.getBluetoothSocket();
		ArduinoSendAction.sendDataViaBluetoothSocket(tmpSocket, pinValue, pinNumberLowerByte, pinNumberHigherByte);

		//turn off BT
		solo.sleep(500);
		ArduinoSendAction.turnOffBluetooth();
		solo.sleep(800);
	}

	public void testSetLedPinToLowWithSpinners() {
		//turn on BT
		solo.sleep(500);
		ArduinoSendAction.turnOnBluetooth();
		solo.sleep(800);

		//Pin Spinner (Pin 13)
		solo.pressSpinnerItem(0, 10);
		//select Value from Spinner (L)
		solo.pressSpinnerItem(1, 0);

		char pinValue = ArduinoSendAction.getPinValue();
		char pinNumberLowerByte = ArduinoSendAction.getPinNumberLowerByte();
		char pinNumberHigherByte = ArduinoSendAction.getPinNumberHigherByte();

		//send data via BT
		//		ArduinoSendAction.initBluetoothConnection("00:07:80:49:8B:61");
		ArduinoSendAction.initBluetoothConnection();
		BluetoothSocket tmpSocket = ArduinoSendAction.getBluetoothSocket();
		ArduinoSendAction.sendDataViaBluetoothSocket(tmpSocket, pinValue, pinNumberLowerByte, pinNumberHigherByte);

		//turn off BT
		solo.sleep(500);
		ArduinoSendAction.turnOffBluetooth();
		solo.sleep(800);
	}

	public void testSetPinToHighAndSetToLowWithForLoop() {
		//turn on BT
		solo.sleep(500);
		ArduinoSendAction.turnOnBluetooth();
		solo.sleep(800);
		//set LED Pin high
		for (int i = 0; i < 10; i++) {
			//			ArduinoSendAction.initBluetoothConnection("00:07:80:49:8B:61");
			ArduinoSendAction.initBluetoothConnection();
			solo.sleep(800);
			BluetoothSocket sendReceiveBluetoothSocket = ArduinoSendAction.getBluetoothSocket();
			ArduinoSendAction.sendDataViaBluetoothSocket(sendReceiveBluetoothSocket, 'H', '1', '3');
			//set LED Pin low
			//			ArduinoSendAction.initBluetoothConnection("00:07:80:49:8B:61");
			ArduinoSendAction.initBluetoothConnection();
			solo.sleep(800);
			sendReceiveBluetoothSocket = ArduinoSendAction.getBluetoothSocket();
			ArduinoSendAction.sendDataViaBluetoothSocket(sendReceiveBluetoothSocket, 'L', '1', '3');
		}
		//turn off BT
		solo.sleep(500);
		ArduinoSendAction.turnOffBluetooth();
		solo.sleep(800);
	}

	public void testSetPinToHighReadIfHighAndSetToLow() {
		//turn on BT
		solo.sleep(500);
		ArduinoSendAction.turnOnBluetooth();
		solo.sleep(800);
		//set LED Pin high
		//		ArduinoSendAction.initBluetoothConnection("00:07:80:49:8B:61");
		ArduinoSendAction.initBluetoothConnection();
		solo.sleep(800);
		BluetoothSocket sendReceiveBluetoothSocket = ArduinoSendAction.getBluetoothSocket();
		ArduinoSendAction.sendDataViaBluetoothSocket(sendReceiveBluetoothSocket, 'H', '1', '3');
		//check if the Pin is high
		//		ArduinoSendAction.initBluetoothConnection("00:07:80:49:8B:61");
		ArduinoSendAction.initBluetoothConnection();
		solo.sleep(800);
		sendReceiveBluetoothSocket = ArduinoSendAction.getBluetoothSocket();
		boolean testValue = false;
		if (ArduinoReceiveAction.receiveDataViaBluetoothSocket(sendReceiveBluetoothSocket, 'D', '1', '3') == 'H') {
			testValue = true;
		}
		assertEquals(true, testValue);
		//set LED Pin low
		//		ArduinoSendAction.initBluetoothConnection("00:07:80:49:8B:61");
		ArduinoSendAction.initBluetoothConnection();
		solo.sleep(800);
		sendReceiveBluetoothSocket = ArduinoSendAction.getBluetoothSocket();
		ArduinoSendAction.sendDataViaBluetoothSocket(sendReceiveBluetoothSocket, 'L', '1', '3');
		//check if the Pin is low
		//		ArduinoSendAction.initBluetoothConnection("00:07:80:49:8B:61");
		ArduinoSendAction.initBluetoothConnection();
		solo.sleep(800);
		sendReceiveBluetoothSocket = ArduinoSendAction.getBluetoothSocket();
		if (ArduinoReceiveAction.receiveDataViaBluetoothSocket(sendReceiveBluetoothSocket, 'D', '1', '3') == 'L') {
			testValue = false;
		}
		assertEquals(false, testValue);
		//turn off BT
		solo.sleep(500);
		ArduinoSendAction.turnOffBluetooth();
		solo.sleep(800);
	}

	public void testSetPinWithReturnValue() {
		boolean testValue = false;
		//turn on BT
		solo.sleep(500);
		ArduinoSendAction.turnOnBluetooth();
		solo.sleep(800);
		//send 03T for test case
		//		ArduinoSendAction.initBluetoothConnection("00:07:80:49:8B:61");
		ArduinoSendAction.initBluetoothConnection();
		solo.sleep(800);
		BluetoothSocket sendReceiveBluetoothSocket = ArduinoSendAction.getBluetoothSocket();
		ArduinoSendAction.sendDataViaBluetoothSocket(sendReceiveBluetoothSocket, 'T', '0', '3');
		//read return value from Arduino
		//		ArduinoSendAction.initBluetoothConnection("00:07:80:49:8B:61");
		ArduinoSendAction.initBluetoothConnection();
		solo.sleep(800);
		sendReceiveBluetoothSocket = ArduinoSendAction.getBluetoothSocket();
		if (ArduinoReceiveAction.receiveDataViaBluetoothSocket(sendReceiveBluetoothSocket, 'D', '0', '3') == 'H') {
			testValue = true;
		}
		assertEquals(true, testValue);
		//turn off BT
		solo.sleep(500);
		ArduinoSendAction.turnOffBluetooth();
		solo.sleep(800);
	}

	public void testGetAnalogPinValueAsReturnValue() {
		boolean testValue = false;
		//turn on BT
		solo.sleep(500);
		ArduinoSendAction.turnOnBluetooth();
		solo.sleep(800);
		//send 03T for test case
		//		ArduinoSendAction.initBluetoothConnection("00:07:80:49:8B:61");
		ArduinoSendAction.initBluetoothConnection();
		solo.sleep(800);
		BluetoothSocket sendReceiveBluetoothSocket = ArduinoSendAction.getBluetoothSocket();
		ArduinoSendAction.sendDataViaBluetoothSocket(sendReceiveBluetoothSocket, 'T', '0', '2');
		//read return value from Arduino
		//		ArduinoSendAction.initBluetoothConnection("00:07:80:49:8B:61");
		ArduinoSendAction.initBluetoothConnection();
		solo.sleep(800);
		sendReceiveBluetoothSocket = ArduinoSendAction.getBluetoothSocket();
		//test if return value is between 0 - 255
		if (ArduinoReceiveAction.receiveDataViaBluetoothSocket(sendReceiveBluetoothSocket, 'A', '0', '2') < 255) {
			testValue = true;
		}
		assertEquals(true, testValue);
		//turn off BT
		solo.sleep(500);
		ArduinoSendAction.turnOffBluetooth();
		solo.sleep(800);
	}

	private void createProject() {

		project = new Project(null, UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
		Sprite sprite = new Sprite("Arduino Brick");
		Script script = new StartScript(sprite);
		arduinoSendBrick = new ArduinoSendBrick(sprite);
		script.addBrick(arduinoSendBrick);

		sprite.addScript(script);
		project.addSprite(sprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		ProjectManager.getInstance().setCurrentScript(script);
	}

}
