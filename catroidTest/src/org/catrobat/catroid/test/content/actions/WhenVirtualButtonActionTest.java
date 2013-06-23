package org.catrobat.catroid.test.content.actions;

import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.WhenVirtualButtonScript;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.PlaceAtBrick;
import org.catrobat.catroid.content.bricks.WhenVirtualButtonBrick.Action;

import android.test.AndroidTestCase;

public class WhenVirtualButtonActionTest extends AndroidTestCase {

	public void testWhenVirtualButtonBrick() throws InterruptedException {
		int testPosition = 100;

		Sprite sprite = new Sprite("new sprite");
		WhenVirtualButtonScript whenVirtualButtonScript = new WhenVirtualButtonScript(sprite);
		whenVirtualButtonScript.setId(Action.TOUCH.getId());
		Brick placeAtBrick = new PlaceAtBrick(sprite, testPosition, testPosition);
		whenVirtualButtonScript.addBrick(placeAtBrick);
		sprite.addScript(whenVirtualButtonScript);
		sprite.createWhenVirtualButtonScriptActionSequence(whenVirtualButtonScript.getId());

		while (!sprite.look.getAllActionsAreFinished()) {
			sprite.look.act(1.0f);
		}

		assertEquals("Simple virtual button test failed", (float) testPosition, sprite.look.getX());
	}
}