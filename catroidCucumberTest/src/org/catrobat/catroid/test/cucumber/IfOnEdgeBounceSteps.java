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
package org.catrobat.catroid.test.cucumber;

import android.test.AndroidTestCase;
import android.util.Log;

import com.badlogic.gdx.scenes.scene2d.Action;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Look;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ExtendedActions;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class IfOnEdgeBounceSteps extends AndroidTestCase {
	private Action ifOnEdgeBounceAction;
	private Sprite sprite;
	private Map<Float, Float> expectedDirections;
	private float BORDER_POSITION_VERTICAL;
	private float BORDER_POSITION_HORIZONTAL;
	private float DIRECTION;
	private float BOUNCE_POSITION_VERTICAL;
	private float BOUNCE_POSITION_HORIZONTAL;
	private float BOUNCE_DIRECTION;

	private static final float WIDTH = 100;
	private static final float HEIGHT = 100;

	private static final int SCREEN_WIDTH = 480;
	private static final int SCREEN_HEIGHT = 800;

	private static final float TOP_BORDER_POSITION = SCREEN_HEIGHT / 2f;
	private static final float BOTTOM_BORDER_POSITION = -TOP_BORDER_POSITION;
	private static final float RIGHT_BORDER_POSITION = SCREEN_WIDTH / 2f;
	private static final float LEFT_BORDER_POSITION = -RIGHT_BORDER_POSITION;

	private static final float BOUNCE_TOP_POSITION = TOP_BORDER_POSITION - (HEIGHT / 2f);
	private static final float BOUNCE_BOTTOM_POSITION = -BOUNCE_TOP_POSITION;
	private static final float BOUNCE_RIGHT_POSITION = RIGHT_BORDER_POSITION - (WIDTH / 2f);
	private static final float BOUNCE_LEFT_POSITION = -BOUNCE_RIGHT_POSITION;

	@Given("^this program has an Object which should bounce off screen borders$")
	public void this_program_has_an_Object_which_should_bounce_off_screen_borders() {
		sprite = new Sprite("Test");
		sprite.look.setWidth(WIDTH);
		sprite.look.setHeight(HEIGHT);
		sprite.look.setPositionInUserInterfaceDimensionUnit(0, 0);

		ifOnEdgeBounceAction = ExtendedActions.ifOnEdgeBounce(sprite);

		Project project = ProjectManager.getInstance().getCurrentProject();
		project.getXmlHeader().virtualScreenWidth = SCREEN_WIDTH;
		project.getXmlHeader().virtualScreenHeight = SCREEN_HEIGHT;
	}

	@When("^an Object would move beyond the (top|bottom|left|right) border$")
	public void an_Object_would_move_beyond_the_border(String border) {
		expectedDirections = new HashMap<Float, Float>();

		if (border.equalsIgnoreCase("top")) {
			Log.d("asdf", "top");
			BORDER_POSITION_VERTICAL = TOP_BORDER_POSITION;
			BORDER_POSITION_HORIZONTAL = 0;
			BOUNCE_POSITION_VERTICAL = BOUNCE_TOP_POSITION;
			BOUNCE_POSITION_HORIZONTAL = 0;

			expectedDirections.put(90f, 90f);
			expectedDirections.put(120f, 120f);
			expectedDirections.put(150f, 150f);
			expectedDirections.put(180f, 180f);
			expectedDirections.put(-150f, -150f);
			expectedDirections.put(-120f, -120f);
			expectedDirections.put(-90f, -90f);
			expectedDirections.put(-60f, -120f);
			expectedDirections.put(-30f, -150f);
			expectedDirections.put(0f, 180f);
			expectedDirections.put(30f, 150f);
			expectedDirections.put(60f, 120f);
		}

		if (border.equalsIgnoreCase("bottom")) {
			Log.d("asdf", "bottom");
			BORDER_POSITION_VERTICAL = BOTTOM_BORDER_POSITION;
			BORDER_POSITION_HORIZONTAL = 0;
			BOUNCE_POSITION_VERTICAL = BOUNCE_BOTTOM_POSITION;
			BOUNCE_POSITION_HORIZONTAL = 0;

			expectedDirections.put(90f, 90f);
			expectedDirections.put(120f, 60f);
			expectedDirections.put(150f, 30f);
			expectedDirections.put(180f, 0f);
			expectedDirections.put(-150f, -30f);
			expectedDirections.put(-120f, -60f);
			expectedDirections.put(-90f, -90f);
			expectedDirections.put(-60f, -60f);
			expectedDirections.put(-30f, -30f);
			expectedDirections.put(0f, 0f);
			expectedDirections.put(30f, 30f);
			expectedDirections.put(60f, 60f);
		}

		if (border.equalsIgnoreCase("left")) {
			Log.d("asdf", "left");
			BORDER_POSITION_VERTICAL = 0;
			BORDER_POSITION_HORIZONTAL = LEFT_BORDER_POSITION;
			BOUNCE_POSITION_VERTICAL = 0;
			BOUNCE_POSITION_HORIZONTAL = BOUNCE_LEFT_POSITION;

			expectedDirections.put(90f, 90f);
			expectedDirections.put(120f, 120f);
			expectedDirections.put(150f, 150f);
			expectedDirections.put(180f, 180f);
			expectedDirections.put(-150f, 150f);
			expectedDirections.put(-120f, 120f);
			expectedDirections.put(-90f, 90f);
			expectedDirections.put(-60f, 60f);
			expectedDirections.put(-30f, 30f);
			expectedDirections.put(0f, 0f);
			expectedDirections.put(30f, 30f);
			expectedDirections.put(60f, 60f);
		}

		if (border.equalsIgnoreCase("right")) {
			Log.d("asdf", "right");
			BORDER_POSITION_VERTICAL = 0;
			BORDER_POSITION_HORIZONTAL = RIGHT_BORDER_POSITION;
			BOUNCE_POSITION_VERTICAL = 0;
			BOUNCE_POSITION_HORIZONTAL = BOUNCE_RIGHT_POSITION;

			expectedDirections.put(90f, -90f);
			expectedDirections.put(120f, -120f);
			expectedDirections.put(150f, -150f);
			expectedDirections.put(180f, 180f);
			expectedDirections.put(-150f, -150f);
			expectedDirections.put(-120f, -120f);
			expectedDirections.put(-90f, -90f);
			expectedDirections.put(-60f, -60f);
			expectedDirections.put(-30f, -30f);
			expectedDirections.put(0f, 0f);
			expectedDirections.put(30f, -30f);
			expectedDirections.put(60f, -60f);
		}

		checkIfExpectedDirectionsContainsAllKeys(expectedDirections);
	}

	@When("^an Object would move beyond the (up|bottom) (left|right) corner in (-?\\d+) direction$")
	public void an_Object_would_move_beyond_the_corner_in_direction(String vertical, String horizontal, float direction) {
		if ((!vertical.equalsIgnoreCase("up") && !vertical.equalsIgnoreCase("bottom"))
				|| (!horizontal.equalsIgnoreCase("left") && !horizontal.equalsIgnoreCase("right"))) {
			fail("Corner must be <up|bottom> <left|right>");
		}

		if (direction < -180f || direction > 180) {
			fail("Direction must be between -180 and 180");
		}

		if (vertical.equalsIgnoreCase("up") && horizontal.equalsIgnoreCase("left")) {
			BORDER_POSITION_HORIZONTAL = LEFT_BORDER_POSITION;
			BORDER_POSITION_VERTICAL = TOP_BORDER_POSITION;
			DIRECTION = direction;
			BOUNCE_POSITION_HORIZONTAL = BOUNCE_LEFT_POSITION;
			BOUNCE_POSITION_VERTICAL = BOUNCE_TOP_POSITION;
		}

		if (vertical.equalsIgnoreCase("up") && horizontal.equalsIgnoreCase("right")) {
			BORDER_POSITION_HORIZONTAL = RIGHT_BORDER_POSITION;
			BORDER_POSITION_VERTICAL = TOP_BORDER_POSITION;
			DIRECTION = direction;
			BOUNCE_POSITION_HORIZONTAL = BOUNCE_RIGHT_POSITION;
			BOUNCE_POSITION_VERTICAL = BOUNCE_TOP_POSITION;
		}

		if (vertical.equalsIgnoreCase("bottom") && horizontal.equalsIgnoreCase("left")) {
			BORDER_POSITION_HORIZONTAL = LEFT_BORDER_POSITION;
			BORDER_POSITION_VERTICAL = BOTTOM_BORDER_POSITION;
			DIRECTION = direction;
			BOUNCE_POSITION_HORIZONTAL = BOUNCE_LEFT_POSITION;
			BOUNCE_POSITION_VERTICAL = BOUNCE_BOTTOM_POSITION;
		}

		if (vertical.equalsIgnoreCase("bottom") && horizontal.equalsIgnoreCase("right")) {
			BORDER_POSITION_HORIZONTAL = RIGHT_BORDER_POSITION;
			BORDER_POSITION_VERTICAL = BOTTOM_BORDER_POSITION;
			DIRECTION = direction;
			BOUNCE_POSITION_HORIZONTAL = BOUNCE_RIGHT_POSITION;
			BOUNCE_POSITION_VERTICAL = BOUNCE_BOTTOM_POSITION;
		}
	}

	@Then("^the Object should still be located within the boundaries of the screen$")
	public void the_Object_should_still_be_located_within_the_boundaries_of_the_screen() {
		for (Entry<Float, Float> entry : expectedDirections.entrySet()) {
			DIRECTION = entry.getKey();
			BOUNCE_DIRECTION = entry.getValue();

			checkPositionAndDirection();
		}
	}

	@Then("^the Object should still be located within the boundaries of the screen and move in (-?\\d+) direction$")
	public void the_Object_should_still_be_located_within_the_boundaries_of_the_screen_and_move_in_direction(
			float bounceDirection) {
		BOUNCE_DIRECTION = bounceDirection;
		checkPositionAndDirection();
	}

	private void checkPositionAndDirection() {
		setPositionAndDirection(BORDER_POSITION_HORIZONTAL, BORDER_POSITION_VERTICAL, DIRECTION);
		executeIfOnEdgeBounceAction();
		checkPositionAndDirection(BOUNCE_POSITION_HORIZONTAL, BOUNCE_POSITION_VERTICAL, BOUNCE_DIRECTION);
	}

	private void checkIfExpectedDirectionsContainsAllKeys(Map<Float, Float> expectedDirections) {
		assertEquals("The expected directions count is wrong", 12, expectedDirections.size());

		assertTrue("A 90Â° direction is missing", expectedDirections.containsKey(90f));
		assertTrue("A 120Â° direction is missing", expectedDirections.containsKey(120f));
		assertTrue("A 150Â° direction is missing", expectedDirections.containsKey(150f));
		assertTrue("A 180Â° direction is missing", expectedDirections.containsKey(180f));
		assertTrue("A -150Â° direction is missing", expectedDirections.containsKey(-150f));
		assertTrue("A -120Â° direction is missing", expectedDirections.containsKey(-120f));
		assertTrue("A -90Â° direction is missing", expectedDirections.containsKey(-90f));
		assertTrue("A -60Â° direction is missing", expectedDirections.containsKey(-60f));
		assertTrue("A -30Â° direction is missing", expectedDirections.containsKey(-30f));
		assertTrue("A 0Â° direction is missing", expectedDirections.containsKey(0f));
		assertTrue("A 30Â° direction is missing", expectedDirections.containsKey(30f));
		assertTrue("A 60Â° direction is missing", expectedDirections.containsKey(60f));
	}

	private void setPositionAndDirection(float x, float y, float direction) {
		Look look = sprite.look;
		look.setPositionInUserInterfaceDimensionUnit(x, y);
		look.setDirectionInUserInterfaceDimensionUnit(direction);
	}

	private void executeIfOnEdgeBounceAction() {
		ifOnEdgeBounceAction.restart();
		ifOnEdgeBounceAction.act(1.0f);
	}

	private void checkPositionAndDirection(float expectedX, float expectedY, float expectedDirection) {
		Look look = sprite.look;
		assertEquals("Wrong x after bounce", expectedX, look.getXInUserInterfaceDimensionUnit());
		assertEquals("Wrong y after bounce", expectedY, look.getYInUserInterfaceDimensionUnit());
		assertEquals("Wrong direction after bounce", expectedDirection, look.getDirectionInUserInterfaceDimensionUnit());
	}
}
