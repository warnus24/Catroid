/**
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2013 The Catrobat Team
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
package org.catrobat.catroid.test.cucumber;

import android.test.AndroidTestCase;
import android.widget.Spinner;

import com.jayway.android.robotium.solo.Solo;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.SetLookBrick;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.ProgramMenuActivity;
import org.catrobat.catroid.ui.ProjectActivity;
import org.catrobat.catroid.ui.ScriptActivity;

//CHECKSTYLE DISABLE MethodNameCheck FOR 1000 LINES
public class SetLookBrickSteps extends AndroidTestCase {
	@Given("^this script has a Set look '(\\w+)' brick$")
	public void this_script_has_a_set_look_brick(String name) {
		Sprite object = (Sprite) Cucumber.get(Cucumber.KEY_CURRENT_OBJECT);
		Script script = (Script) Cucumber.get(Cucumber.KEY_CURRENT_SCRIPT);

		SetLookBrick setLookBrick = new SetLookBrick(object);
		script.addBrick(setLookBrick);

		Solo solo = (Solo) Cucumber.get(Cucumber.KEY_SOLO);
		assertEquals("I am in the wrong Activity.", MainMenuActivity.class, solo.getCurrentActivity().getClass());
		solo.clickOnView(solo.getView(org.catrobat.catroid.R.id.main_menu_button_continue));
		solo.waitForActivity(ProjectActivity.class.getSimpleName(), 3000);
		assertEquals("I am in the wrong Activity.", ProjectActivity.class, solo.getCurrentActivity().getClass());
		solo.clickOnText(object.getName());
		solo.waitForActivity(ProgramMenuActivity.class.getSimpleName(), 3000);
		assertEquals("I am in the wrong Activity.", ProgramMenuActivity.class, solo.getCurrentActivity().getClass());
		solo.clickOnButton(solo.getString(R.string.scripts));
		solo.waitForActivity(ScriptActivity.class.getSimpleName(), 3000);
		assertEquals("I am in the wrong Activity.", ScriptActivity.class, solo.getCurrentActivity().getClass());
		Spinner spinner = solo.getView(Spinner.class, 0);
		solo.clickOnView(spinner);
		solo.clickOnText(name);
		solo.clickOnActionBarHomeButton();
		solo.sleep(1000);
	}

	@Then("^'Object' should have the look '(\\w+)' set$")
	public void object_should_have_the_look_set(String name) {
		Sprite object = (Sprite) Cucumber.get(Cucumber.KEY_CURRENT_OBJECT);
		assertEquals("The look is not set", name, object.look.getLookData().getLookName());
	}
}
