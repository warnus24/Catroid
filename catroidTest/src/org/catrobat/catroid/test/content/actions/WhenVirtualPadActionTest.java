package org.catrobat.catroid.test.content.actions;

import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.WhenVirtualPadScript;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.PlaceAtBrick;
import org.catrobat.catroid.content.bricks.WhenVirtualPadBrick.Direction;

import android.test.AndroidTestCase;

public class WhenVirtualPadActionTest extends AndroidTestCase {

	public void testWhenVirtualPadBrick() throws InterruptedException {
		int testPosition = 100;

		Sprite sprite = new Sprite("new sprite");
		WhenVirtualPadScript whenVirtualPadScript = new WhenVirtualPadScript(sprite);
		whenVirtualPadScript.setId(Direction.UP.getId());
		Brick placeAtBrick = new PlaceAtBrick(sprite, testPosition, testPosition);
		whenVirtualPadScript.addBrick(placeAtBrick);
		sprite.addScript(whenVirtualPadScript);
		sprite.createWhenVirtualPadScriptActionSequence(whenVirtualPadScript.getId());

		while (!sprite.look.getAllActionsAreFinished()) {
			sprite.look.act(1.0f);
		}

		assertEquals("Simple virtual pad test failed", (float) testPosition, sprite.look.getX());
	}
}