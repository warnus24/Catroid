package org.catrobat.catroid.content.bricks;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.BrickValues;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment;

import java.util.List;

public abstract class RoboMeMoveBrick extends FormulaBrick implements View.OnClickListener {

	private transient View prototypeView;
	private transient AdapterView<?> directionAdapterView;
	private transient AdapterView<?> speedAdapterView;
	private transient TextView editCycles;
	public transient Direction directionEnum;
	public transient Speed speedEnum;
	private String direction;
	private String speed;

	public static enum Direction {
		FORWARD, BACKWARD
	}

	public static enum Speed {
		SPEED1, SPEED2, SPEED3, SPEED4, SPEED5
	}

	public RoboMeMoveBrick(Direction direction, Speed speed, int cycles) {
		this.directionEnum = direction;
		this.direction = directionEnum.name();
		this.speedEnum = speed;
		this.speed = speedEnum.name();
		initializeBrickFields(new Formula(cycles));
	}

	private void initializeBrickFields(Formula cycles) {
		addAllowedBrickField(BrickField.ROBOME_MOVE_CYCLES);
		setFormulaWithBrickField(BrickField.ROBOME_MOVE_CYCLES, cycles);
	}

	protected abstract String getBrickLabel(View view);

	@Override
	public abstract List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence);

	@Override
	public View getPrototypeView(Context context) {
		prototypeView = View.inflate(context, R.layout.brick_robome_move, null);
		TextView textCycles = (TextView) prototypeView.findViewById(R.id.robome_move_cycles_text_view);
		textCycles.setText(String.valueOf(BrickValues.ROBOME_MOVE_BRICK_DEFAULT_CYCLES));

		Spinner directionSpinner = (Spinner) prototypeView.findViewById(R.id.robome_move_spinner);
		directionSpinner.setFocusableInTouchMode(false);
		directionSpinner.setFocusable(false);

		Spinner speedSpinner = (Spinner) prototypeView.findViewById(R.id.robome_speed_spinner);
		speedSpinner.setFocusableInTouchMode(false);
		speedSpinner.setFocusable(false);

		ArrayAdapter<CharSequence> directionAdapter = ArrayAdapter.createFromResource(context, R.array.robome_move_chooser, android.R.layout.simple_spinner_item);

		ArrayAdapter<CharSequence> speedAdapter = ArrayAdapter.createFromResource(context, R.array.robome_speed_chooser, android.R.layout.simple_spinner_item);

		directionSpinner.setAdapter(directionAdapter);
		speedSpinner.setAdapter(speedAdapter);

		directionSpinner.setSelection(directionEnum.ordinal());
		speedSpinner.setSelection(speedEnum.ordinal());
		return prototypeView;
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter baseAdapter) {
		if (animationState) {
			return view;
		}

		view = View.inflate(context, R.layout.brick_robome_move, null);
		view = getViewWithAlpha(alphaValue);

		setCheckboxView(R.id.brick_robome_move_checkbox);

		final Brick brickInstance = this;
		checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				checked = isChecked;
				adapter.handleCheck(brickInstance, isChecked);
			}
		});

		TextView textCycles = (TextView) view.findViewById(R.id.robome_move_cycles_text_view);
		editCycles = (TextView) view.findViewById(R.id.robome_move_cycles_edit_text);
		getFormulaWithBrickField(BrickField.ROBOME_MOVE_CYCLES).setTextFieldId(R.id.robome_move_cycles_edit_text);
		getFormulaWithBrickField(BrickField.ROBOME_MOVE_CYCLES).refreshTextField(view);

		textCycles.setVisibility(View.GONE);
		editCycles.setVisibility(View.VISIBLE);

		editCycles.setOnClickListener(this);

		ArrayAdapter<CharSequence> speedAdapter = ArrayAdapter.createFromResource(context, R.array.robome_speed_chooser, android.R.layout.simple_spinner_item);
		speedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		Spinner speedSpinner = (Spinner) view.findViewById(R.id.robome_speed_spinner);

		ArrayAdapter<CharSequence> directionAdapter = ArrayAdapter.createFromResource(context, R.array.robome_move_chooser, android.R.layout.simple_spinner_item);
		speedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		Spinner directionSpinner = (Spinner) view.findViewById(R.id.robome_move_spinner);

		if (!(checkbox.getVisibility() == View.VISIBLE)) {
			speedSpinner.setClickable(true);
			directionSpinner.setClickable(true);
		} else {
			speedSpinner.setClickable(false);
			directionSpinner.setClickable(false);
		}

		speedSpinner.setAdapter(speedAdapter);
		directionSpinner.setAdapter(directionAdapter);

		speedSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				directionEnum = Direction.values()[position];
				direction = directionEnum.name();
				directionAdapterView = parent;
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {}
		});

		directionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				speedEnum = Speed.values()[position];
				speed = speedEnum.name();
				speedAdapterView = parent;
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {}
		});

		speedSpinner.setSelection(speedEnum.ordinal());
		directionSpinner.setSelection(directionEnum.ordinal());

		return view;
	}

	@Override
	public View getViewWithAlpha(int alphaValue) {
		if (view != null) {
			View layout = view.findViewById(R.id.brick_robome_move_layout);
			Drawable background = layout.getBackground();
			background.setAlpha(alphaValue);

			TextView textMoveLabel = (TextView) view.findViewById(R.id.brick_robome_move_label);
			TextView textMove = (TextView) view.findViewById(R.id.brick_robome_move);
			TextView textMoveCycles = (TextView) view.findViewById(R.id.robome_move_cycles_text_view);
			TextView editMoveCylces = (TextView) view.findViewById(R.id.robome_move_cycles_edit_text);

			textMoveLabel.setTextColor(textMoveLabel.getTextColors().withAlpha(alphaValue));
			textMove.setTextColor(textMove.getTextColors().withAlpha(alphaValue));
			textMoveCycles.setTextColor(textMoveCycles.getTextColors().withAlpha(alphaValue));

			Spinner directionSpinner = (Spinner) view.findViewById(R.id.robome_move_spinner);
			ColorStateList directionColor = textMoveCycles.getTextColors().withAlpha(alphaValue);
			directionSpinner.getBackground().setAlpha(alphaValue);
			if (directionAdapterView != null) {
				((TextView) directionAdapterView.getChildAt(0)).setTextColor(directionColor);
			}

			Spinner speedSpinner = (Spinner) view.findViewById(R.id.robome_speed_spinner);
			ColorStateList speedColor = textMoveCycles.getTextColors().withAlpha(alphaValue);
			speedSpinner.getBackground().setAlpha(alphaValue);
			if (speedAdapterView != null) {
				((TextView) speedAdapterView.getChildAt(0)).setTextColor(speedColor);
			}

			editMoveCylces.setTextColor(editMoveCylces.getTextColors().withAlpha(alphaValue));
			editMoveCylces.getBackground().setAlpha(alphaValue);

			this.alphaValue = alphaValue;
		}
		return view;
	}

	@Override
	public void onClick(View view) {
		if (checkbox.getVisibility() == View.VISIBLE) {
			return;
		}
		FormulaEditorFragment.showFragment(view, this, getFormulaWithBrickField(BrickField.ROBOME_MOVE_CYCLES));
	}
}
