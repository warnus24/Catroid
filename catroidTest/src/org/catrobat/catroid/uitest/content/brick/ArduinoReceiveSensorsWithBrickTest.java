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

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.SetSizeToBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.formulaeditor.Functions;
import org.catrobat.catroid.formulaeditor.InternFormulaParser;
import org.catrobat.catroid.formulaeditor.InternToken;
import org.catrobat.catroid.formulaeditor.InternTokenType;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import java.util.LinkedList;
import java.util.List;

public class ArduinoReceiveSensorsWithBrickTest extends BaseActivityInstrumentationTestCase<ScriptActivity> {

	private Project project;
	private SetSizeToBrick setSizeToBrick;

	public ArduinoReceiveSensorsWithBrickTest() {
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

	public void testArduinoReceiveAnalogSensor() {
		Formula formula = new Formula(1);
		assertTrue("Formula should be single number formula", formula.isSingleNumberFormula());

		formula = new Formula(1.0d);
		assertTrue("Formula should be single number formula", formula.isSingleNumberFormula());

		formula = new Formula(1.0f);
		assertTrue("Formula should be single number formula", formula.isSingleNumberFormula());

		List<InternToken> internTokenList = new LinkedList<InternToken>();

		//Should look like this: Arduino_analog_read(1)
		internTokenList.add(new InternToken(InternTokenType.FUNCTION_NAME, Functions.ARDUINOANALOG.name()));
		internTokenList.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN, "("));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "1")); //read Pin Number 1
		internTokenList.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE, ")"));

		InternFormulaParser internParser = new InternFormulaParser(internTokenList);
		FormulaElement parseTree = internParser.parseFormula();

		internParser = new InternFormulaParser(internTokenList);
		parseTree = internParser.parseFormula();

		//assertNotNull("Formula is not parsed correctly: round(1.1111)", parseTree);
		//assertEquals("Formula interpretation is not as expected", 1d, parseTree.interpretRecursive(null));	
		internTokenList.clear();
	}

	//	public void testArduinoReceiveDigitalSensor() {
	//
	//	}

	private void createProject() {

		project = new Project(null, UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
		Sprite sprite = new Sprite("cat");
		Script script = new StartScript(sprite);
		setSizeToBrick = new SetSizeToBrick(sprite, 100);
		script.addBrick(setSizeToBrick);

		sprite.addScript(script);
		project.addSprite(sprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		ProjectManager.getInstance().setCurrentScript(script);
	}

}
