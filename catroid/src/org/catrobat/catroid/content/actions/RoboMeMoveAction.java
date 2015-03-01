package org.catrobat.catroid.content.actions;

import android.util.Log;

import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.RoboMeMoveBrick.Direction;
import org.catrobat.catroid.content.bricks.RoboMeMoveBrick.Speed;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.InterpretationException;
import org.catrobat.catroid.robome.RoboMeConnector;

public class RoboMeMoveAction extends TemporalAction {

	private static final int MIN_SPEED = 1;
	private static final int MAX_SPEED = 5;
	private static final int DEFAULT_CYCLES = 1;

	private Formula cycles;
	private Sprite sprite;
	private Direction direction;
	private Speed speed;

	@Override
	protected void update(float percent) {

		int speedValue = speed.getValue();

		if (speedValue < MIN_SPEED) {
			speedValue = MIN_SPEED;
		} else if (speedValue > MAX_SPEED) {
			speedValue = MAX_SPEED;
		}

		int cyclesValue;
		try {
			cyclesValue = cycles.interpretInteger(sprite);
		} catch (InterpretationException e) {
			cyclesValue = DEFAULT_CYCLES;
			Log.d(getClass().getSimpleName(), "Formula interpretation for this specific Brick failed.", e);
		}

		if (direction.equals(Direction.FORWARD)) {
			RoboMeConnector.moveForward(speedValue, cyclesValue);
		}
		else if (direction.equals(Direction.BACKWARD)) {
			RoboMeConnector.moveBackward(speedValue, cyclesValue);
		}
		else {
			Log.d(this.getClass().getSimpleName(), "No direction set!");
		}
	}

	public void setSprite(Sprite sprite) {
		this.sprite = sprite;
	}

	public void setCycles(Formula cycles) {
		this.cycles = cycles;
	}

	public void setSpeedEnum(Speed speedEnum) {
		this.speed = speedEnum;
	}

	public void setDirection(Direction directionEnum) {
		this.direction = directionEnum;
	}
}
