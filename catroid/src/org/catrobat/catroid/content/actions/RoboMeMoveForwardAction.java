package org.catrobat.catroid.content.actions;

import android.util.Log;

import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.RoboMeMoveBrick.Direction;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.InterpretationException;

public class RoboMeMoveForwardAction extends TemporalAction {

	private static final int MIN_SPEED = 1;
	private static final int MAX_SPEED = 5;

	private Formula speed;
	private Sprite sprite;
	private Direction direction;

	@Override
	protected void update(float percent) {
		int speedValue;
		try {
			speedValue = speed.interpretInteger(sprite);
		} catch (InterpretationException e) {
			speedValue = 0;
			Log.d(getClass().getSimpleName(), "Formula interpretation for this specific Brick failed.", e);
		}

		if (speedValue < MIN_SPEED) {
			speedValue = MIN_SPEED;
		} else if (speedValue > MAX_SPEED) {
			speedValue = MAX_SPEED;
		}

		if (direction.equals(Direction.FORWARD)) {

		}
	}
}
