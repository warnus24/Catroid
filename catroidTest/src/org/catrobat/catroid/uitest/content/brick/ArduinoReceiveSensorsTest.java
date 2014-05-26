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

import android.test.InstrumentationTestCase;

import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.ChangeSizeByNBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.formulaeditor.InternFormulaParser;
import org.catrobat.catroid.formulaeditor.InternToken;
import org.catrobat.catroid.formulaeditor.InternTokenType;
import org.catrobat.catroid.formulaeditor.Sensors;

import java.util.LinkedList;
import java.util.List;

public class ArduinoReceiveSensorsTest extends InstrumentationTestCase {

	private Project project;
	private Sprite firstSprite;
	private Brick changeBrick;
	Script startScript1;

	@Override
	public void setUp() throws Exception {
		super.setUp();
	}

	//	public void testArduinoReceiveAnalogSensor() throws SecurityException, IllegalArgumentException,
	//			NoSuchFieldException, IllegalAccessException {
	//		createProject();
	//
	//		Formula formula = createFormulaWithSensor(sensor)(Functions.ARDUINOANALOG);
	//		ChangeSizeByNBrick xAccelerationBrick = new ChangeSizeByNBrick(firstSprite, formula);
	//		startScript1.addBrick(xAccelerationBrick);
	//
	//		ProjectManager.getInstance().setProject(project);
	//		ProjectManager.getInstance().setCurrentSprite(firstSprite);
	//
	//		//For initialization
	//		SensorHandler.startSensorListener(getInstrumentation().getTargetContext());
	//		SensorHandler.stopSensorListeners();
	//	}

	//	public void testArduinoReceiveDigitalSensor() {
	//
	//	}

	private Formula createFormulaWithSensor(Sensors sensor) {

		List<InternToken> internTokenList = new LinkedList<InternToken>();
		internTokenList.add(new InternToken(InternTokenType.SENSOR, sensor.name()));
		InternFormulaParser internFormulaParser = new InternFormulaParser(internTokenList);
		FormulaElement root = internFormulaParser.parseFormula();
		Formula formula = new Formula(root);

		return formula;
	}

	private void createProject() {
		this.project = new Project(null, "testProject");
		firstSprite = new Sprite("zwoosh");
		startScript1 = new StartScript(firstSprite);
		changeBrick = new ChangeSizeByNBrick(firstSprite, 10);
		firstSprite.addScript(startScript1);
		startScript1.addBrick(changeBrick);
		project.addSprite(firstSprite);
	}

}
