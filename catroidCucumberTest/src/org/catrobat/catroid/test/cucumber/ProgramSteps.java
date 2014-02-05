/**
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
package org.catrobat.catroid.test.cucumber;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.lessThanOrEqualTo;

import android.test.AndroidTestCase;

import com.jayway.android.robotium.solo.Solo;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.BroadcastScript;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.BroadcastBrick;
import org.catrobat.catroid.content.bricks.BroadcastWaitBrick;
import org.catrobat.catroid.content.bricks.ChangeVariableBrick;
import org.catrobat.catroid.content.bricks.LoopBeginBrick;
import org.catrobat.catroid.content.bricks.LoopEndBrick;
import org.catrobat.catroid.content.bricks.RepeatBrick;
import org.catrobat.catroid.content.bricks.SetVariableBrick;
import org.catrobat.catroid.content.bricks.WaitBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.formulaeditor.FormulaElement.ElementType;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.test.cucumber.util.PrintBrick;
import org.catrobat.catroid.test.cucumber.util.ScriptEndCallbackBrick;
import org.catrobat.catroid.test.cucumber.util.ScriptStartCallbackBrick;
import org.catrobat.catroid.test.cucumber.util.Util;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.ProjectActivity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

// CHECKSTYLE DISABLE MethodNameCheck FOR 1000 LINES
public class ProgramSteps extends AndroidTestCase {
	private final Object programStartWaitLock = new Object();
	private final Object programScriptWaitLock = new Object();
	private boolean programHasStarted = false;
	private int numberOfStartedScripts = 0;
	private Semaphore programHasFinished;
	private OutputStream outputStream;

	@Given("^I have a Program$")
	public void I_have_a_program() throws IOException {
		ProjectManager projectManager = ProjectManager.getInstance();
		projectManager.initializeNewProject("Cucumber", getContext(), true);
		Project project = projectManager.getCurrentProject();
		Cucumber.put(Cucumber.KEY_PROJECT, project);
	}

	@Given("^this program has an Object '(.+)'$")
	public void program_has_object(String name) {
		int lookId = org.catrobat.catroid.R.drawable.default_project_mole_digged_out;
		ProjectManager projectManager = ProjectManager.getInstance();
		Project project = projectManager.getCurrentProject();
		Sprite sprite = Util.addNewObjectWithLook(getContext(), project, name, lookId);
		Cucumber.put(Cucumber.KEY_CURRENT_OBJECT, sprite);
	}

	@Given("^'(.+)' has a Start script$")
	public void object_has_start_script(String object) {
		Project project = ProjectManager.getInstance().getCurrentProject();
		Sprite sprite = Util.findSprite(project, object);
		StartScript script = new StartScript(sprite);

		script.addBrick(new ScriptStartCallbackBrick(sprite, new ScriptStartCallbackBrick.BrickCallback() {
			@Override
			public void onCallback() {
				synchronized (programScriptWaitLock) {
					++numberOfStartedScripts;
				}

				synchronized (programStartWaitLock) {
					if (!programHasStarted) {
						programHasStarted = true;
						programStartWaitLock.notify();
					}
				}
			}
		}));

		sprite.addScript(script);
		Cucumber.put(Cucumber.KEY_CURRENT_SCRIPT, script);
	}

	@Given("^'(.+)' has a When '(.+)' script$")
	public void object_has_a_when_script(String object, String message) {
		Project project = ProjectManager.getInstance().getCurrentProject();
		Sprite sprite = Util.findSprite(project, object);
		BroadcastScript script = new BroadcastScript(sprite, message);

		script.addBrick(new ScriptStartCallbackBrick(sprite, new ScriptStartCallbackBrick.BrickCallback() {
			@Override
			public void onCallback() {
				synchronized (programScriptWaitLock) {
					++numberOfStartedScripts;
				}
			}
		}));

		sprite.addScript(script);
		Cucumber.put(Cucumber.KEY_CURRENT_SCRIPT, script);
	}

	@And("^this script has a set '(\\w+)' to (\\d+.?\\d*) brick$")
	public void script_has_set_variable_to_value_brick(String userVariableName, String userVariableValue) {
		Sprite object = (Sprite) Cucumber.get(Cucumber.KEY_CURRENT_OBJECT);
		Script script = (Script) Cucumber.get(Cucumber.KEY_CURRENT_SCRIPT);
		Project project = ProjectManager.getInstance().getCurrentProject();

		UserVariable userVariable = project.getUserVariables().getUserVariable(userVariableName, object);
		if (userVariable == null) {
			userVariable = project.getUserVariables().addSpriteUserVariableToSprite(object, userVariableName);
		}

		FormulaElement formulaElement = new FormulaElement(ElementType.NUMBER, userVariableValue, null);

		Brick brick = new SetVariableBrick(object, new Formula(formulaElement), userVariable);
		script.addBrick(brick);
	}

	@And("^this script has a set '(\\w+)' to '(\\w+)' brick$")
	public void script_has_set_variable_to_variable_brick(String userVariableName, String userVariableValue) {
		Sprite object = (Sprite) Cucumber.get(Cucumber.KEY_CURRENT_OBJECT);
		Script script = (Script) Cucumber.get(Cucumber.KEY_CURRENT_SCRIPT);
		Project project = ProjectManager.getInstance().getCurrentProject();

		UserVariable userVariable = project.getUserVariables().getUserVariable(userVariableName, object);
		if (userVariable == null) {
			userVariable = project.getUserVariables().addSpriteUserVariableToSprite(object, userVariableName);
		}

		FormulaElement formulaElement = new FormulaElement(ElementType.USER_VARIABLE, userVariableValue, null);

		Brick brick = new SetVariableBrick(object, new Formula(formulaElement), userVariable);
		script.addBrick(brick);
	}

	@And("^this script has a change '(\\w+)' by (\\d+.?\\d*) brick$")
	public void script_has_change_variable_by_value_brick(String name, String value) {
		Sprite object = (Sprite) Cucumber.get(Cucumber.KEY_CURRENT_OBJECT);
		Script script = (Script) Cucumber.get(Cucumber.KEY_CURRENT_SCRIPT);
		Project project = ProjectManager.getInstance().getCurrentProject();

		UserVariable variable = project.getUserVariables().getUserVariable(name, object);
		if (variable == null) {
			variable = project.getUserVariables().addSpriteUserVariableToSprite(object, name);
		}

		FormulaElement formulaElement = new FormulaElement(ElementType.NUMBER, value, null);

		Brick brick = new ChangeVariableBrick(object, new Formula(formulaElement), variable);
		script.addBrick(brick);
	}

	@And("^this script has a Repeat (\\d+) times brick$")
	public void script_has_repeat_times_brick(int iterations) {
		Sprite object = (Sprite) Cucumber.get(Cucumber.KEY_CURRENT_OBJECT);
		Script script = (Script) Cucumber.get(Cucumber.KEY_CURRENT_SCRIPT);

		Brick brick = new RepeatBrick(object, new Formula(iterations));
		Cucumber.put(Cucumber.KEY_LOOP_BEGIN_BRICK, brick);
		script.addBrick(brick);
	}

	@And("^this script has a Repeat end brick$")
	public void script_has_repeat_end_brick() {
		Sprite object = (Sprite) Cucumber.get(Cucumber.KEY_CURRENT_OBJECT);
		Script script = (Script) Cucumber.get(Cucumber.KEY_CURRENT_SCRIPT);

		LoopBeginBrick loopBeginBrick = (LoopBeginBrick) Cucumber.get(Cucumber.KEY_LOOP_BEGIN_BRICK);
		Brick brick = new LoopEndBrick(object, loopBeginBrick);
		script.addBrick(brick);
	}

	@And("^this script has a Broadcast '(.+)' brick$")
	public void script_has_broadcast_brick(String message) {
		Sprite object = (Sprite) Cucumber.get(Cucumber.KEY_CURRENT_OBJECT);
		Script script = (Script) Cucumber.get(Cucumber.KEY_CURRENT_SCRIPT);

		BroadcastBrick brick = new BroadcastBrick(object, message);
		script.addBrick(brick);
	}

	@And("^this script has a BroadcastWait '(.+)' brick$")
	public void script_has_broadcast_wait_brick(String message) {
		Sprite object = (Sprite) Cucumber.get(Cucumber.KEY_CURRENT_OBJECT);
		Script script = (Script) Cucumber.get(Cucumber.KEY_CURRENT_SCRIPT);

		BroadcastWaitBrick brick = new BroadcastWaitBrick(object, message);
		script.addBrick(brick);
	}

	@And("^this script has a Wait (\\d+) milliseconds brick$")
	public void script_has_wait_milliseconds_brick(int millis) {
		Sprite object = (Sprite) Cucumber.get(Cucumber.KEY_CURRENT_OBJECT);
		Script script = (Script) Cucumber.get(Cucumber.KEY_CURRENT_SCRIPT);

		WaitBrick brick = new WaitBrick(object, millis);
		script.addBrick(brick);
	}

	@And("^this script has a Wait (\\d+.?\\d*) seconds? brick$")
	public void script_has_wait_seconds_brick(int seconds) {
		Sprite object = (Sprite) Cucumber.get(Cucumber.KEY_CURRENT_OBJECT);
		Script script = (Script) Cucumber.get(Cucumber.KEY_CURRENT_SCRIPT);

		WaitBrick brick = new WaitBrick(object, seconds * 1000);
		script.addBrick(brick);
	}

	@And("^this script has a Print brick with '(.*)'$")
	public void script_has_a_print_brick_s(String text) {
		script_has_a_print_brick(text);
	}

	@And("^this script has a Print brick with$")
	public void script_has_a_print_brick(String text) {
		Sprite object = (Sprite) Cucumber.get(Cucumber.KEY_CURRENT_OBJECT);
		Script script = (Script) Cucumber.get(Cucumber.KEY_CURRENT_SCRIPT);

		if (outputStream == null) {
			outputStream = new ByteArrayOutputStream();
		}
		PrintBrick brick = new PrintBrick(object, text);
		brick.setOutputStream(outputStream);
		script.addBrick(brick);
	}

	@When("^I start the program$")
	public void I_start_the_program() throws InterruptedException {
		programHasFinished = new Semaphore(0);
		addScriptEndCallbacks();

		Solo solo = (Solo) Cucumber.get(Cucumber.KEY_SOLO);
		assertEquals("I am in the wrong Activity.", MainMenuActivity.class, solo.getCurrentActivity().getClass());
		solo.clickOnView(solo.getView(org.catrobat.catroid.R.id.main_menu_button_continue));
		solo.waitForActivity(ProjectActivity.class.getSimpleName(), 3000);
		assertEquals("I am in the wrong Activity.", ProjectActivity.class, solo.getCurrentActivity().getClass());
		solo.clickOnView(solo.getView(org.catrobat.catroid.R.id.button_play));
		solo.waitForActivity(StageActivity.class.getSimpleName(), 3000);
		assertEquals("I am in the wrong Activity.", StageActivity.class, solo.getCurrentActivity().getClass());

		synchronized (programStartWaitLock) {
			if (!programHasStarted) {
				programStartWaitLock.wait(10000);
			}
		}
	}

	private void addScriptEndCallbacks() {
		Project project = ProjectManager.getInstance().getCurrentProject();
		for (Sprite sprite : project.getSpriteList()) {
			for (int i = 0; i < sprite.getNumberOfScripts(); i++) {
				sprite.getScript(i).addBrick(
						new ScriptEndCallbackBrick(sprite, new ScriptEndCallbackBrick.BrickCallback() {
							@Override
							public void onCallback() {
								synchronized (programScriptWaitLock) {
									if (--numberOfStartedScripts == 0) {
										programHasFinished.release();
									}
								}
							}
						}));
			}
		}
	}

	@And("^I wait until the program has stopped$")
	public void wait_until_program_has_stopped() throws InterruptedException {
		programHasFinished.tryAcquire(1, 60, TimeUnit.SECONDS);
	}

	@And("^I wait for at least (\\d+) milliseconds?$")
	public void I_wait_for_at_least_milliseconds(int milliseconds) {
		Solo solo = (Solo) Cucumber.get(Cucumber.KEY_SOLO);
		solo.sleep(milliseconds);
	}

	@Then("^the variable '(\\w+)' should be greater than or equal (\\d+.?\\d*)$")
	public void variable_should_greater_than_equal_float(String name, float expected) {
		Sprite object = (Sprite) Cucumber.get(Cucumber.KEY_CURRENT_OBJECT);
		Project project = ProjectManager.getInstance().getCurrentProject();

		UserVariable variable = project.getUserVariables().getUserVariable(name, object);
		assertNotNull("The variable does not exist.", variable);

		float actual = variable.getValue().floatValue();
		assertThat("The variable is lesser than the value.", actual, greaterThanOrEqualTo(expected));
	}

	@Then("^the variable '(\\w+)' should be be less than or equal (\\d+.?\\d*)$")
	public void variable_should_less_than_equal_float(String name, float expected) {
		Sprite object = (Sprite) Cucumber.get(Cucumber.KEY_CURRENT_OBJECT);
		Project project = ProjectManager.getInstance().getCurrentProject();

		UserVariable variable = project.getUserVariables().getUserVariable(name, object);
		assertNotNull("The variable does not exist.", variable);

		float actual = variable.getValue().floatValue();
		assertThat("The variable is greater than the value.", actual, lessThanOrEqualTo(expected));
	}

	@Then("^the variable '([\\w|\\d]+)' should be equal (.+)$")
	public void variable_should_equal_float(String userVariableName, float expectedValue) {
		Sprite object = (Sprite) Cucumber.get(Cucumber.KEY_CURRENT_OBJECT);
		Project project = ProjectManager.getInstance().getCurrentProject();
		UserVariable userVariable = project.getUserVariables().getUserVariable(userVariableName, object);

		assertNotNull("The variable does not exist.", userVariable);
		assertEquals("The variable has the wrong value.", userVariable.getValue().floatValue(), expectedValue);
	}

	@Then("^I should see the printed output '(\\w+)'$")
	public void I_should_see_printed_output_s(String text) throws IOException {
		I_should_see_printed_output(text);
	}

	@Then("^I should see the printed output '(\\w+)' or '(\\w+)'$")
	public void I_should_see_printed_output_s(String text1, String text2) throws IOException {
		String actual = outputStream.toString().replace("\r", "").replace("\n", "");
		String expected1 = text1.replace("\r", "").replace("\n", "");
		String expected2 = text2.replace("\r", "").replace("\n", "");
		assertTrue("The printed output is wrong.", actual.equals(expected1) || actual.equals(expected2));
		outputStream.close();
	}

	@Then("^I should see the printed output$")
	public void I_should_see_printed_output(String text) throws IOException {
		String actual = outputStream.toString().replace("\r", "").replace("\n", "");
		String expected = text.replace("\r", "").replace("\n", "");
		assertEquals("The printed output is wrong.", expected, actual);
		outputStream.close();
	}

	@Then("^I should see no printed output$")
	public void I_should_see_no_printed_output() throws IOException {
		I_should_see_printed_output("");
	}
}
