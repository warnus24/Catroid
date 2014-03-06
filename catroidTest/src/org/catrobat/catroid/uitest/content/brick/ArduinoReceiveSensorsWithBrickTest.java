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

import android.widget.TextView;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.ChangeSizeByNBrick;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;

public class ArduinoReceiveSensorsWithBrickTest extends BaseActivityInstrumentationTestCase<MainMenuActivity> {

	private static final int CHANGE_SIZE_BY_EDIT_TEXT_RID = R.id.brick_change_size_by_edit_text;

	public ArduinoReceiveSensorsWithBrickTest() {
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
		Brick changeBrick = new ChangeSizeByNBrick(firstSprite, 0);
		firstSprite.addScript(startScript);
		startScript.addBrick(changeBrick);
		project.addSprite(firstSprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(firstSprite);
	}

	public void testArduinoDigitalSensor() {
		//happens in the ArduinoReceiveAction method initBluetooth()
		//		//turn on BT
		//		solo.sleep(500);
		//		ArduinoSendAction.tunOnBluetooth();
		//		solo.sleep(800);
		//Formulaeditor Sensor Test
		solo.clickOnView(solo.getView(CHANGE_SIZE_BY_EDIT_TEXT_RID));

		solo.waitForView(solo.getView(R.id.formula_editor_edit_field));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_sensors));
		solo.clickOnText(getActivity().getString(R.string.formula_editor_sensor_arduino_read_pin_value_digital));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_redo));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_0));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_3));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_compute));
		solo.waitForView(solo.getView(R.id.formula_editor_compute_dialog_textview));
		TextView computeTextView = (TextView) solo.getView(R.id.formula_editor_compute_dialog_textview);
		assertEquals("computeTextView did not contain the correct value", "0.0", computeTextView.getText().toString());

		//		//turn off BT
		//		solo.sleep(500);
		//		ArduinoSendAction.turnOffBluetooth();
		//		solo.sleep(800);
	}

	public void testArduinoAnalogSensor() {
		//happens in the ArduinoReceiveAction method initBluetooth()
		//		//turn on BT
		//		solo.sleep(500);
		//		ArduinoSendAction.tunOnBluetooth();
		//		solo.sleep(800);
		//Formulaeditor Sensor Test
		solo.clickOnView(solo.getView(CHANGE_SIZE_BY_EDIT_TEXT_RID));

		solo.waitForView(solo.getView(R.id.formula_editor_edit_field));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_sensors));
		solo.clickOnText(getActivity().getString(R.string.formula_editor_sensor_arduino_read_pin_value_analog));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_redo));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_0));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_3));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_compute));
		solo.waitForView(solo.getView(R.id.formula_editor_compute_dialog_textview));
		TextView computeTextView = (TextView) solo.getView(R.id.formula_editor_compute_dialog_textview);
		assertEquals("computeTextView did not contain the correct value", "0.0", computeTextView.getText().toString());

		//		//turn off BT
		//		solo.sleep(500);
		//		ArduinoSendAction.turnOffBluetooth();
		//		solo.sleep(800);
	}
}
