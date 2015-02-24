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

package org.catrobat.catroid.uitest.content.brick;

import android.bluetooth.BluetoothAdapter;
import android.widget.EditText;
import android.widget.ListView;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.ArduinoIrBrick;
import org.catrobat.catroid.content.bricks.ArduinoSendBrick;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.ForeverBrick;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.adapter.BrickAdapter;
import org.catrobat.catroid.ui.dialogs.NewVariableDialog;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.Reflection;
import org.catrobat.catroid.uitest.util.UiTestUtils;
import org.catrobat.catroid.formulaeditor.InternFormula;
import org.catrobat.catroid.formulaeditor.InternFormulaParser;
import java.util.ArrayList;

/**
 * Created by david on 17.02.15.
 */
public class ArduinoIrBrickTest extends BaseActivityInstrumentationTestCase<ScriptActivity> {


	private Project project;
	private ArduinoIrBrick arduinoIrBrick;



	public ArduinoIrBrickTest() {
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

	private void createProject() {

		project = new Project(null, UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
		Sprite sprite = new Sprite("Arduino Ir Brick");
		Script script = new StartScript();
		arduinoIrBrick = new ArduinoIrBrick("0");

		script.addBrick(new ForeverBrick());
		script.addBrick(arduinoIrBrick);
		sprite.addScript(script);
		project.addSprite(sprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		ProjectManager.getInstance().setCurrentScript(script);
	}


	public void testIfProjectWasCreatedCorrectly() {
		ListView dragDropListView = UiTestUtils.getScriptListView(solo);
		BrickAdapter adapter = (BrickAdapter) dragDropListView.getAdapter();

		int childrenCount = adapter.getChildCountFromLastGroup();
		int groupCount = adapter.getScriptCount();

		assertEquals("Incorrect number of bricks.", 3, dragDropListView.getChildCount());
		assertEquals("Incorrect number of bricks.", 2, childrenCount);

		ArrayList<Brick> projectBrickList = project.getSpriteList().get(0).getScript(0).getBrickList();
		assertEquals("Incorrect number of bricks.", 2, projectBrickList.size());

		assertEquals("Wrong Brick instance.", projectBrickList.get(0), adapter.getChild(groupCount - 1, 0));
		assertNotNull("TextView does not exist.", solo.getText(solo.getString(R.string.brick_arduino_ir)));
		assertNotNull("TextView does not exist.", solo.getView(R.id.brick_arduino_ir_prototype_text_view));
		assertNotNull("TextView does not exist", solo.getText(solo.getString(R.string.brick_forever)));
	}


	public void testCommands(){

		solo.clickOnView(solo.getView(R.id.brick_arduino_ir_edit_text));
		solo.sleep(1000);
		//assertTrue("NewVariableDialog not visible", solo.waitForFragmentByTag(NewVariableDialog.DIALOG_FRAGMENT_TAG));
	     final EditText editText = (EditText) solo.getView(R.id.brick_arduino_ir_edit_text);
		getInstrumentation().runOnMainSync(new Runnable(){
			@Override
		public void run(){
				editText.requestFocus();
			}
		});
		getInstrumentation().waitForIdleSync();
		getInstrumentation().sendStringSync("FFFFFF");
		getInstrumentation().waitForIdleSync();
		solo.clickOnButton(solo.getString(R.string.ok));
		assertTrue("ScriptFragment not visible", solo.waitForText("command"));
		solo.sleep(300);

		//press play
		UiTestUtils.clickOnBottomBar(solo, R.id.button_play);
		solo.sleep(300);



		//check if Bluetooth is enabled
		assertTrue("Bluetooth not enabled!", BluetoothAdapter.getDefaultAdapter().isEnabled());

		//select the Arduino Bluetooth board
		solo.clickOnText("ARDUINOBT");
		solo.sleep(8000);

		//press back
		solo.goBack();
		solo.sleep(1000);

		//press return
		solo.clickOnText("Zurück");
		solo.sleep(300);
	}


}
