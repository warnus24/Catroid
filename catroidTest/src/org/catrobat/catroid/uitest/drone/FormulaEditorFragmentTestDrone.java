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
package org.catrobat.catroid.uitest.formulaeditor;

import android.content.SharedPreferences;
import android.graphics.Rect;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;

import com.jayway.android.robotium.solo.Solo;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.PlaceAtBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.FormulaEditorEditText;
import org.catrobat.catroid.formulaeditor.FormulaEditorHistory;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.SettingsActivity;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.Reflection;
import org.catrobat.catroid.uitest.util.UiTestUtils;

public class FormulaEditorFragmentTestDrone extends BaseActivityInstrumentationTestCase<MainMenuActivity> {
	private Project project;
	private PlaceAtBrick placeAtBrick;
	private static final int INITIAL_X = 8;
	private static final int INITIAL_Y = 7;

	private static final int X_POS_EDIT_TEXT_RID = R.id.brick_place_at_edit_text_x;
	private String settings;
	private Sprite sprite;

	public FormulaEditorFragmentTestDrone() {
		super(MainMenuActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		createProject();
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);
		settings = solo.getString(R.string.settings);
	}

	private void createProject() {
		project = new Project(null, UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
		sprite = new Sprite("cat");
		Script script = new StartScript();
		placeAtBrick = new PlaceAtBrick(INITIAL_X, INITIAL_Y);
		script.addBrick(placeAtBrick);

		sprite.addScript(script);
		project.addSprite(sprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		ProjectManager.getInstance().setCurrentScript(script);
	}

	public void testChangeFormula() {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
		preferences.edit().putBoolean(SettingsActivity.SETTINGS_SHOW_PARROT_AR_DRONE_BRICKS, true);

		solo.clickOnView(solo.getView(X_POS_EDIT_TEXT_RID));
		solo.sleep(100);
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_sensors));
		solo.sleep(100);
		solo.clickOnText(solo.getString(R.string.formula_editor_sensor_drone_battery_status));
		solo.sleep(100);
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_ok));
		assertTrue("Saved changes message not found!",
				solo.searchText(solo.getString(R.string.formula_editor_changes_saved)));
		solo.goBack();
		solo.goBack();
	}
}
