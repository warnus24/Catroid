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

import cucumber.api.java.en.Given;

import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.IfLogicBeginBrick;
import org.catrobat.catroid.content.bricks.IfLogicElseBrick;
import org.catrobat.catroid.content.bricks.IfLogicEndBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.formulaeditor.FormulaElement.ElementType;
import org.catrobat.catroid.formulaeditor.Operators;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.test.cucumber.util.Util;

public class IfBrickSteps extends AndroidTestCase {
	@Given("^'([\\w|\\d]+)' has an user variable '([\\w|\\d]+)' with '(.+)'$")
	public void object_has_an_user_variable_with(String objectName, String userVariableName, double userVariableValue) {
		Project project = (Project) Cucumber.get(Cucumber.KEY_PROJECT);
		Sprite object = Util.findSprite(project, objectName);

		UserVariable userVariable = project.getUserVariables().getUserVariable(userVariableName, object);
		if (userVariable == null) {
			userVariable = project.getUserVariables().addSpriteUserVariableToSprite(object, userVariableName);
		}
		userVariable.setValue(userVariableValue);
	}

	@Given("^this script has an If '(TRUE|FALSE)' brick$")
	public void this_script_has_an_If_true_brick(String condition) {
		Sprite object = (Sprite) Cucumber.get(Cucumber.KEY_CURRENT_OBJECT);
		Script script = (Script) Cucumber.get(Cucumber.KEY_CURRENT_SCRIPT);

		Formula formula = new Formula(new FormulaElement(ElementType.FUNCTION, condition, null));
		IfLogicBeginBrick ifLogicBeginBrick = new IfLogicBeginBrick(object, formula);
		Cucumber.put(Cucumber.KEY_IF_LOGIC_BEGIN_BRICK, ifLogicBeginBrick);
		script.addBrick(ifLogicBeginBrick);
	}

	@Given("^this script has an If '(\\d+.?\\d*) (<|>|=) (\\d+.?\\d*)' brick$")
	public void this_script_has_an_If_comparison_brick(double number1, String operator, double number2) {
		Sprite object = (Sprite) Cucumber.get(Cucumber.KEY_CURRENT_OBJECT);
		Script script = (Script) Cucumber.get(Cucumber.KEY_CURRENT_SCRIPT);

		String operatorName = null;
		if (operator.equals("<")) {
			operatorName = Operators.SMALLER_THAN.name();
		} else if (operator.equals(">")) {
			operatorName = Operators.GREATER_THAN.name();
		} else {
			operatorName = Operators.EQUAL.name();
		}

		Formula validFormula = new Formula(1);
		validFormula.setRoot(new FormulaElement(ElementType.OPERATOR, operatorName, null, new FormulaElement(
				ElementType.NUMBER, Double.toString(number1), null), new FormulaElement(ElementType.NUMBER, Double
				.toString(number2), null)));

		IfLogicBeginBrick ifLogicBeginBrick = new IfLogicBeginBrick(object, validFormula);
		Cucumber.put(Cucumber.KEY_IF_LOGIC_BEGIN_BRICK, ifLogicBeginBrick);
		script.addBrick(ifLogicBeginBrick);
	}

	@Given("^this script has an If user variable '([\\w|\\d]+) (<|>|=) (.+)' brick$")
	public void this_script_has_an_If_user_variable_brick(String userVariableName, String operator,
			double userVariableValue) {
		Sprite object = (Sprite) Cucumber.get(Cucumber.KEY_CURRENT_OBJECT);
		Project project = (Project) Cucumber.get(Cucumber.KEY_PROJECT);

		this_script_has_an_If_comparison_brick(project.getUserVariables().getUserVariable(userVariableName, object)
				.getValue(), operator, userVariableValue);
	}

	@Given("^this script has an Else brick$")
	public void this_script_has_an_Else_brick() {
		Script script = (Script) Cucumber.get(Cucumber.KEY_CURRENT_SCRIPT);
		IfLogicBeginBrick ifLogicBeginBrick = (IfLogicBeginBrick) Cucumber.get(Cucumber.KEY_IF_LOGIC_BEGIN_BRICK);

		if (!ifLogicBeginBrick.isInitialized()) {
			ifLogicBeginBrick.initialize();
		}

		IfLogicElseBrick ifLogicElseBrick = ifLogicBeginBrick.getIfElseBrick();
		script.addBrick(ifLogicElseBrick);
	}

	@Given("^this script has an End If brick$")
	public void this_script_has_an_End_If_brick() {
		Script script = (Script) Cucumber.get(Cucumber.KEY_CURRENT_SCRIPT);
		IfLogicBeginBrick ifLogicBeginBrick = (IfLogicBeginBrick) Cucumber.get(Cucumber.KEY_IF_LOGIC_BEGIN_BRICK);

		if (!ifLogicBeginBrick.isInitialized()) {
			ifLogicBeginBrick.initialize();
		}

		IfLogicEndBrick ifLogicEndBrick = ifLogicBeginBrick.getIfEndBrick();
		script.addBrick(ifLogicEndBrick);
	}
}
