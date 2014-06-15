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

import android.bluetooth.BluetoothAdapter;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.IfLogicBeginBrick;
import org.catrobat.catroid.content.bricks.IfLogicElseBrick;
import org.catrobat.catroid.content.bricks.IfLogicEndBrick;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;

public class ArduinoReceiveWithFormulaEditorTest extends BaseActivityInstrumentationTestCase<MainMenuActivity> {

	private static final int IF_BRICK_TEXT_FIELD = R.id.brick_if_begin_edit_text;
	private IfLogicBeginBrick ifBrick;

	public ArduinoReceiveWithFormulaEditorTest() {
		super(MainMenuActivity.class);

	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		createProject(UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);
	}

	private void createProject(String projectName) throws InterruptedException {
		Project project = new Project(null, projectName);
		Sprite firstSprite = new Sprite("Arduino Sensors");
		Script startScript = new StartScript(firstSprite);
		firstSprite.addScript(startScript);

		ifBrick = new IfLogicBeginBrick(firstSprite, 0);
		IfLogicElseBrick ifElseBrick = new IfLogicElseBrick(firstSprite, ifBrick);
		IfLogicEndBrick ifEndBrick = new IfLogicEndBrick(firstSprite, ifElseBrick, ifBrick);
		ifBrick.setIfElseBrick(ifElseBrick);
		ifBrick.setIfEndBrick(ifEndBrick);

		startScript.addBrick(ifBrick);
		startScript.addBrick(ifElseBrick);
		startScript.addBrick(ifEndBrick);
		project.addSprite(firstSprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(firstSprite);
	}

	public void testIfArduinoLEDPinIsLow() {
		//check if Bluetooth is enabled
		assertTrue("Bluetooth not enabled!", BluetoothAdapter.getDefaultAdapter().isEnabled());

		//Formulaeditor Sensor Test
		solo.clickOnView(solo.getView(IF_BRICK_TEXT_FIELD));
		solo.waitForView(solo.getView(R.id.formula_editor_edit_field));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_0));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_equal));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_sensors));
		solo.clickOnText(getActivity().getString(R.string.formula_editor_sensor_arduino_read_pin_value_digital));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_redo));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_1));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_3));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_ok));

		//press play
		UiTestUtils.clickOnBottomBar(solo, R.id.button_play);
		solo.clickOnText("ARDUINO");

		solo.

		//press back
		solo.sleep(3000);
		solo.goBack();

		//press return
		solo.clickOnText("Back");
		solo.sleep(3000);
	}

	//	public void testIfAllArduinoAnalogPinsAreLowWithSensor() {
	//		solo.clickOnView(solo.getView(CHANGE_SIZE_BY_EDIT_TEXT_RID));
	//
	//		solo.waitForView(solo.getView(R.id.formula_editor_edit_field));
	//		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_sensors));
	//		solo.clickOnText(getActivity().getString(R.string.formula_editor_sensor_arduino_read_pin_value_analog));
	//		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_redo));
	//		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_0));
	//		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_1));
	//
	//		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_compute));
	//		solo.waitForView(solo.getView(R.id.formula_editor_compute_dialog_textview));
	//
	//		solo.goBack();
	//
	//		solo.clickOnView(solo.getView(R.id.formula_editor_edit_field_clear));
	//		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_2));
	//
	//		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_compute));
	//		solo.waitForView(solo.getView(R.id.formula_editor_compute_dialog_textview));
	//
	//		solo.goBack();
	//
	//		solo.clickOnView(solo.getView(R.id.formula_editor_edit_field_clear));
	//		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_3));
	//
	//		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_compute));
	//		solo.waitForView(solo.getView(R.id.formula_editor_compute_dialog_textview));
	//
	//		solo.goBack();
	//
	//		solo.clickOnView(solo.getView(R.id.formula_editor_edit_field_clear));
	//		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_4));
	//
	//		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_compute));
	//		solo.waitForView(solo.getView(R.id.formula_editor_compute_dialog_textview));
	//
	//		solo.goBack();
	//
	//		solo.clickOnView(solo.getView(R.id.formula_editor_edit_field_clear));
	//		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_5));
	//
	//		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_compute));
	//		solo.waitForView(solo.getView(R.id.formula_editor_compute_dialog_textview));
	//
	//		solo.goBack();
	//
	//		solo.clickOnView(solo.getView(R.id.formula_editor_edit_field_clear));
	//		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_6));
	//
	//		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_compute));
	//		solo.waitForView(solo.getView(R.id.formula_editor_compute_dialog_textview));
	//
	//		solo.goBack();
	//		//		TextView computeTextView = (TextView) solo.getView(R.id.formula_editor_compute_dialog_textview);
	//		//		assertEquals("computeTextView did not contain the correct value", "0.0", computeTextView.getText().toString());
	//	}
	//
	//	public void testEmptySensorInput() {
	//		solo.clickOnView(solo.getView(CHANGE_SIZE_BY_EDIT_TEXT_RID));
	//
	//		solo.waitForView(solo.getView(R.id.formula_editor_edit_field));
	//		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_sensors));
	//		solo.clickOnText(getActivity().getString(R.string.formula_editor_sensor_arduino_read_pin_value_analog));
	//		solo.clickOnView(solo.getView(R.id.formula_editor_edit_field_clear));
	//
	//		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_compute));
	//
	//		solo.goBack();
	//
	//	}
	//
	//	public void testInvalideInputPinNumberSensorInput() {
	//		solo.clickOnView(solo.getView(CHANGE_SIZE_BY_EDIT_TEXT_RID));
	//
	//		solo.waitForView(solo.getView(R.id.formula_editor_edit_field));
	//		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_sensors));
	//		solo.clickOnText(getActivity().getString(R.string.formula_editor_sensor_arduino_read_pin_value_analog));
	//		solo.clickOnView(solo.getView(R.id.formula_editor_edit_field_clear));
	//		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_minus));
	//		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_0));
	//		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_1));
	//
	//		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_compute));
	//
	//		solo.clickOnView(solo.getView(R.id.formula_editor_edit_field_clear));
	//
	//		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_sensors));
	//		solo.clickOnText(getActivity().getString(R.string.formula_editor_sensor_arduino_read_pin_value_analog));
	//		solo.clickOnView(solo.getView(R.id.formula_editor_edit_field_clear));
	//		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_plus));
	//		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_0));
	//		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_1));
	//
	//		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_compute));
	//
	//		solo.clickOnView(solo.getView(R.id.formula_editor_edit_field_clear));
	//
	//		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_sensors));
	//		solo.clickOnText(getActivity().getString(R.string.formula_editor_sensor_arduino_read_pin_value_analog));
	//		solo.clickOnView(solo.getView(R.id.formula_editor_edit_field_clear));
	//		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_mult));
	//		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_0));
	//		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_1));
	//
	//		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_compute));
	//
	//		solo.clickOnView(solo.getView(R.id.formula_editor_edit_field_clear));
	//
	//		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_sensors));
	//		solo.clickOnText(getActivity().getString(R.string.formula_editor_sensor_arduino_read_pin_value_analog));
	//		solo.clickOnView(solo.getView(R.id.formula_editor_edit_field_clear));
	//		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_divide));
	//		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_0));
	//		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_1));
	//
	//		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_compute));
	//
	//		solo.goBack();
	//
	//	}
	//
	//	public void testNotAvailablePinSensorInput() {
	//		solo.clickOnView(solo.getView(CHANGE_SIZE_BY_EDIT_TEXT_RID));
	//
	//		solo.waitForView(solo.getView(R.id.formula_editor_edit_field));
	//		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_sensors));
	//		solo.clickOnText(getActivity().getString(R.string.formula_editor_sensor_arduino_read_pin_value_analog));
	//		solo.clickOnView(solo.getView(R.id.formula_editor_edit_field_clear));
	//		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_0));
	//		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_0));
	//
	//		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_compute));
	//
	//		solo.clickOnView(solo.getView(R.id.formula_editor_edit_field_clear));
	//
	//		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_sensors));
	//		solo.clickOnText(getActivity().getString(R.string.formula_editor_sensor_arduino_read_pin_value_analog));
	//		solo.clickOnView(solo.getView(R.id.formula_editor_edit_field_clear));
	//		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_0));
	//		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_7));
	//
	//		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_compute));
	//
	//		solo.goBack();
	//	}

}
