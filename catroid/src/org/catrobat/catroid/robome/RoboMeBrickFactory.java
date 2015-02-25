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
package org.catrobat.catroid.robome;

import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.BrickBaseType;

public class RoboMeBrickFactory {

	private RoboMeBrickFactory() {
	}

	public static enum RoboMeBricks {
		ROBOME_MOVE_FORWARD_BRICK, ROBOME_MOVE_BACKWARD_BRICK, ROBOME_TURN_LEFT_BRICK, ROBOME_TURN_RIGHT_BRICK
	}

	public static BrickBaseType getInstanceOfDroneBrick(RoboMeBricks brick, Sprite sprite, int speed, int angle,
			int cycles, int powerInPercent) {

		switch (brick) {
			default:
				return null;
		}
	}
}
