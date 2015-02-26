package org.catrobat.catroid.content.actions;

import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.RoboMeMoveBrick.Direction;
import org.catrobat.catroid.robome.RoboMeConnector;

public class RoboMeMoveForwardAction extends TemporalAction {

	private static final int MIN_SPEED = 1;
	private static final int MAX_SPEED = 5;

	private int speed;
	private int cycles;
	private Sprite sprite;
	private Direction direction;

	@Override
	protected void update(float percent) {
		if (speed < MIN_SPEED) {
			speed = MIN_SPEED;
		} else if (speed > MAX_SPEED) {
			speed = MAX_SPEED;
		}

		if (direction.equals(Direction.FORWARD)) {
			RoboMeConnector.moveForward(speed, cycles);
		}
	}

	public void setSprite(Sprite sprite) {
		this.sprite = sprite;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public void setCycles(int cycles) {
		this.cycles = cycles;
	}
}
