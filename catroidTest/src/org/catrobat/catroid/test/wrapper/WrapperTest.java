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

package org.catrobat.catroid.test.wrapper;

import android.test.AndroidTestCase;

import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.wrapper.FormulaParser;

public class WrapperTest extends AndroidTestCase {

	public void testParse() {
		assertEquals(Math.PI, interpret("PI"));

		assertEquals(1.0, interpret("((1))"));

		assertEquals(2.0, interpret("1 + 1"));
		assertEquals(2.0, interpret("(1 + 1)"));
		assertEquals(2.0, interpret("(((1) + 1))"));

		Sprite sprite = new Sprite("Test");
		sprite.look.setXInUserInterfaceDimensionUnit(3);
		sprite.look.setYInUserInterfaceDimensionUnit(4);

		assertEquals(12.0, interpret("(position_x * position_y)", sprite));

		assertEquals(1.0, interpret("TRUE != FALSE"));

		assertEquals(8.0, interpret("2 + 2 * 3"));
		assertEquals(12.0, interpret("(2 + 2) * 3"));

		assertEquals(8.0, interpret("2^3"));

		assertEquals(1.0, interpret("mod(3,2)"));
	}

	private double interpret(String formula, Sprite sprite) {
		return FormulaParser.parse(formula).interpretDouble(sprite);
	}

	private double interpret(String formula) {
		return interpret(formula, null);
	}
}
