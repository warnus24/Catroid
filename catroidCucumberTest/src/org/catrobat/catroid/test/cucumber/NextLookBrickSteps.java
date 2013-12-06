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

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.NextLookBrick;
import org.catrobat.catroid.test.cucumber.util.Util;

import java.io.File;

//CHECKSTYLE DISABLE MethodNameCheck FOR 1000 LINES
public class NextLookBrickSteps extends AndroidTestCase {
	@Given("^I add a look '(\\w+)'$")
	public void i_add_a_look(String name) {
		Sprite object = (Sprite) Cucumber.get(Cucumber.KEY_CURRENT_OBJECT);
		File testImage = Util.createObjectImage(getContext(), name + ".png", R.drawable.default_project_mole_whacked);
		LookData lookData = Util.newLookData(name, testImage);
		object.getLookDataList().add(lookData);
	}

	@Given("^this script has a Set next look brick$")
	public void this_script_has_a_set_next_look_brick() {
		Sprite object = (Sprite) Cucumber.get(Cucumber.KEY_CURRENT_OBJECT);
		Script script = (Script) Cucumber.get(Cucumber.KEY_CURRENT_SCRIPT);

		NextLookBrick nextLookBrick = new NextLookBrick(object);
		script.addBrick(nextLookBrick);
	}
}
