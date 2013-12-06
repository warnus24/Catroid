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
import cucumber.api.java.en.Then;

import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.SetSizeToBrick;

//CHECKSTYLE DISABLE MethodNameCheck FOR 1000 LINES
public class SetSizeToBrickSteps extends AndroidTestCase {
	@Given("^this script has a Set size to (-?\\d+) brick$")
	public void this_script_has_a_Set_size_to_brick(float size) {
		Sprite object = (Sprite) Cucumber.get(Cucumber.KEY_CURRENT_OBJECT);
		Script script = (Script) Cucumber.get(Cucumber.KEY_CURRENT_SCRIPT);

		SetSizeToBrick setSizeToBrick = new SetSizeToBrick(object, size);
		script.addBrick(setSizeToBrick);
	}

	@Then("^'Object' should have the size (\\d+)$")
	public void object_should_have_the_size(float size) {
		Sprite object = (Sprite) Cucumber.get(Cucumber.KEY_CURRENT_OBJECT);
		assertEquals("Size was not updated", size / 100, object.look.getScaleX());
		assertEquals("Size was not updated", size / 100, object.look.getScaleY());
	}
}
