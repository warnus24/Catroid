package org.catrobat.catroid.content.bricks;

import android.content.Context;
import android.view.View;
import android.widget.BaseAdapter;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.Formula;

import java.util.List;

public abstract class RoboMeMoveBrick extends FormulaBrick implements View.OnClickListener {

	public static enum Direction {
		FORWARD, BACKWARD, LEFT, RIGHT
	}

	public RoboMeMoveBrick() {
		addAllowedBrickField(BrickField.ROBOME_MOVE_SPEED);
		addAllowedBrickField(BrickField.ROBOME_MOVE_CYCLES);
	}

	public RoboMeMoveBrick(int speed, int cycles) {
		initializeBrickFields(new Formula(speed), new Formula(cycles));
	}

	private void initializeBrickFields(Formula speed, Formula cycles) {
		addAllowedBrickField(BrickField.ROBOME_MOVE_SPEED);
		addAllowedBrickField(BrickField.ROBOME_MOVE_CYCLES);
		setFormulaWithBrickField(BrickField.ROBOME_MOVE_SPEED, speed);
		setFormulaWithBrickField(BrickField.ROBOME_MOVE_CYCLES, cycles);
	}

	protected abstract String getBrickLabel(View view);

	@Override
	public abstract List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence);

	@Override
	public View getView(Context context, int brickId, BaseAdapter baseAdapter) {
		if (animationState) {
			return view;
		}
		//TODO implement
	}
}
