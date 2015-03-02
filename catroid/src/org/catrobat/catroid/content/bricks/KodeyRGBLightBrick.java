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
package org.catrobat.catroid.content.bricks;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.BrickValues;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ExtendedActions;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.formulaeditor.InterpretationException;
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment;
import org.catrobat.catroid.ui.fragment.KodeyMultipleSeekbarFragment;

import java.util.List;
import java.util.logging.Handler;

public class KodeyRGBLightBrick extends FormulaBrick implements OnClickListener {
	private static final long serialVersionUID = 1L;

	private transient View prototypeView;
	private transient AdapterView<?> adapterView;

	public static enum Eye {
		Left, Right, Both
	}

	private String eye;
	private transient Eye eyeEnum;
	private transient TextView editRedValue;
	private transient TextView editGreenValue;
	private transient TextView editBlueValue;
	private Formula red;
	private Formula green;
	private Formula blue;
	private Boolean isFormulaEditorPreview = false;

	public void setIsFormulaEditorPreview(Boolean isFormulaEditorPreview) {
		this.isFormulaEditorPreview = isFormulaEditorPreview;
	}

	protected Object readResolve() {
		if (eye != null) {
			eyeEnum = Eye.valueOf(eye);
		}
		return this;
	}

	public KodeyRGBLightBrick() {
		addAllowedBrickField(BrickField.KODEY_LIGHT);
	}

	public KodeyRGBLightBrick(Eye eye, int red, int green, int blue) {
		this.eyeEnum = eye;
		this.eye = eyeEnum.name();

		this.red = new Formula(red);
		this.green = new Formula(green);
		this.blue = new Formula(blue);
	}

	public KodeyRGBLightBrick(Eye eye, Formula red, Formula green, Formula blue) {
		this.eyeEnum = eye;
		this.eye = eyeEnum.name();

		this.red = red;
		this.green = green;
		this.blue = blue;
	}

	public void setRedTextValues(int red)
	{
		editRedValue.setText(String.valueOf(red));
		this.red.setDisplayText(String.valueOf(red));
	}

	public void setGreenTextValues(int green)
	{
		editBlueValue.setText(String.valueOf(green));
		this.green.setDisplayText(String.valueOf(green));
	}

	public void setBlueTextValues(int blue)
	{
		editBlueValue.setText(String.valueOf(blue));
		this.blue.setDisplayText(String.valueOf(blue));
	}

	@Override
	public int getRequiredResources() { return BLUETOOTH_KODEY;	}


	@Override
	public View getPrototypeView(Context context) {
		prototypeView = View.inflate(context, R.layout.brick_kodey_rgb_light, null);

		TextView textValueRed = (TextView) prototypeView.findViewById(R.id.brick_kodey_rgb_led_red_prototype_text_view);
		textValueRed.setText(String.valueOf(BrickValues.KODEY_VALUE_RED));

		TextView textValueGreen = (TextView) prototypeView.findViewById(R.id.brick_kodey_rgb_led_green_prototype_text_view);
		textValueGreen.setText(String.valueOf(BrickValues.KODEY_VALUE_GREEN));

		TextView textValueBlue = (TextView) prototypeView.findViewById(R.id.brick_kodey_rgb_led_blue_prototype_text_view);
		textValueBlue.setText(String.valueOf(BrickValues.KODEY_VALUE_BLUE));

		Spinner eyeSpinner = (Spinner) prototypeView.findViewById(R.id.brick_kodey_rgb_light_spinner);
		eyeSpinner.setFocusableInTouchMode(false);
		eyeSpinner.setFocusable(false);

		ArrayAdapter<CharSequence> eyeAdapter = ArrayAdapter.createFromResource(context,
				R.array.brick_kodey_select_light_spinner, android.R.layout.simple_spinner_item);
		eyeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		eyeSpinner.setAdapter(eyeAdapter);
		eyeSpinner.setSelection(eyeEnum.ordinal());

		return prototypeView;
	}

	@Override
	public Brick clone() {
		return new KodeyRGBLightBrick(eyeEnum, red.clone(), green.clone(), blue.clone());
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter baseAdapter) {
		if (animationState) {
			return view;
		}
		if (view == null) {
			alphaValue = 255;
		}

		view = View.inflate(context, R.layout.brick_kodey_rgb_light, null);
		setCheckboxView(R.id.brick_kodey_rgb_led_action_checkbox);

		final Brick brickInstance = this;
		checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				checked = isChecked;
				adapter.handleCheck(brickInstance, isChecked);
			}
		});

		TextView textRed = (TextView) view.findViewById(R.id.brick_kodey_rgb_led_red_prototype_text_view);
		editRedValue = (TextView) view.findViewById(R.id.brick_kodey_rgb_led_action_red_edit_text);
		red.setTextFieldId(R.id.brick_kodey_rgb_led_action_red_edit_text);
		red.refreshTextField(view);

		textRed.setVisibility(View.GONE);
		editRedValue.setVisibility(View.VISIBLE);

		editRedValue.setOnClickListener(this);

		TextView textGreen = (TextView) view.findViewById(R.id.brick_kodey_rgb_led_green_prototype_text_view);
		editGreenValue = (TextView) view.findViewById(R.id.brick_kodey_rgb_led_action_green_edit_text);
		green.setTextFieldId(R.id.brick_kodey_rgb_led_action_green_edit_text);
		green.refreshTextField(view);

		textGreen.setVisibility(View.GONE);
		editGreenValue.setVisibility(View.VISIBLE);

		editGreenValue.setOnClickListener(this);

		TextView textBlue = (TextView) view.findViewById(R.id.brick_kodey_rgb_led_blue_prototype_text_view);
		editBlueValue = (TextView) view.findViewById(R.id.brick_kodey_rgb_led_action_blue_edit_text);
		blue.setTextFieldId(R.id.brick_kodey_rgb_led_action_blue_edit_text);
		blue.refreshTextField(view);

		textBlue.setVisibility(View.GONE);
		editBlueValue.setVisibility(View.VISIBLE);

		editBlueValue.setOnClickListener(this);

		ArrayAdapter<CharSequence> eyeAdapter = ArrayAdapter.createFromResource(context,
				R.array.brick_kodey_select_light_spinner, android.R.layout.simple_spinner_item);
		eyeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		Spinner eyeSpinner = (Spinner) view.findViewById(R.id.brick_kodey_rgb_light_spinner);

		if (!(checkbox.getVisibility() == View.VISIBLE)) {
			eyeSpinner.setClickable(true);
			eyeSpinner.setEnabled(true);
		} else {
			eyeSpinner.setClickable(false);
			eyeSpinner.setEnabled(false);
		}

		eyeSpinner.setAdapter(eyeAdapter);
		eyeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
				eyeEnum = Eye.values()[position];
				eye = eyeEnum.name();
				adapterView = arg0;
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});

		eyeSpinner.setSelection(eyeEnum.ordinal());

		return view;
	}

	@Override
	public void onClick(View view) {
		if (checkbox.getVisibility() == View.VISIBLE) {
			return;
		}
		if((red.getRoot().getElementType() == FormulaElement.ElementType.NUMBER) &&
				(green.getRoot().getElementType() == FormulaElement.ElementType.NUMBER) &&
				(blue.getRoot().getElementType() == FormulaElement.ElementType.NUMBER) && (isFormulaEditorPreview == false)){
			KodeyMultipleSeekbarFragment.showMultipleSeekBarFragment(view, this, red, green, blue);
		} else {
			if(view.getId() == R.id.brick_kodey_rgb_led_action_red_edit_text) {
				FormulaEditorFragment.showFragment(view, this, red);
			} else if(view.getId() == R.id.brick_kodey_rgb_led_action_green_edit_text) {
				FormulaEditorFragment.showFragment(view, this, green);
			} else if(view.getId() == R.id.brick_kodey_rgb_led_action_blue_edit_text) {
				FormulaEditorFragment.showFragment(view, this, blue);
			}
		}
	}

	@Override
	public View getViewWithAlpha(int alphaValue) {

		if (view != null) {

			View layout = view.findViewById(R.id.brick_kodey_rgb_led_layout);
			Drawable background = layout.getBackground();
			background.setAlpha(alphaValue);

			TextView textKodeyLabel = (TextView) view.findViewById(R.id.brick_kodey_rgb_led_action_label);
			TextView textKodeyEyeRed = (TextView) view.findViewById(R.id.brick_kodey_rgb_led_red_text_view);  //vielleicht mit der unten tauschen
			TextView editRed = (TextView) view.findViewById(R.id.brick_kodey_rgb_led_action_red_edit_text);

			//			TextView textAlbertEyeColorLabel = (TextView) view
			//					.findViewById(R.id.robot_albert_rgb_led_color_text_view_label);
			TextView textKodeyEyeRedView = (TextView) view.findViewById(R.id.brick_kodey_rgb_led_red_text_view);

			textKodeyLabel.setTextColor(textKodeyLabel.getTextColors().withAlpha(alphaValue));
			textKodeyEyeRed.setTextColor(textKodeyEyeRed.getTextColors().withAlpha(alphaValue));
			//textAlbertEyeColorLabel.setTextColor(textAlbertEyeColorLabel.getTextColors().withAlpha(alphaValue));

			//			textAlbertMotorActionLabelSpeedView.setTextColor(textAlbertMotorActionLabelSpeedView.getTextColors()
			//					.withAlpha(alphaValue));
			Spinner eyeSpinner = (Spinner) view.findViewById(R.id.brick_kodey_rgb_light_spinner);
			ColorStateList color = textKodeyEyeRedView.getTextColors().withAlpha(alphaValue);
			eyeSpinner.getBackground().setAlpha(alphaValue);
			if (adapterView != null) {
				((TextView) adapterView.getChildAt(0)).setTextColor(color);
			}
			editRed.setTextColor(editRed.getTextColors().withAlpha(alphaValue));
			editRed.getBackground().setAlpha(alphaValue);

			//green
			TextView textKodeyEyeGreen = (TextView) view.findViewById(R.id.brick_kodey_rgb_led_green_text_view);
			TextView editGreen = (TextView) view.findViewById(R.id.brick_kodey_rgb_led_action_green_edit_text);
			TextView textKodeyEyeGreenView = (TextView) view.findViewById(R.id.brick_kodey_rgb_led_green_text_view);
			editGreen.setTextColor(editGreen.getTextColors().withAlpha(alphaValue));
			editGreen.getBackground().setAlpha(alphaValue);
			ColorStateList color2 = textKodeyEyeGreenView.getTextColors().withAlpha(alphaValue);
			if (adapterView != null) {
				((TextView) adapterView.getChildAt(0)).setTextColor(color2);
			}
			textKodeyEyeGreen.setTextColor(textKodeyEyeGreen.getTextColors().withAlpha(alphaValue));

			//blue
			TextView textKodeyEyeBlue = (TextView) view.findViewById(R.id.brick_kodey_rgb_led_blue_text_view);
			TextView editBlue = (TextView) view.findViewById(R.id.brick_kodey_rgb_led_action_blue_edit_text);
			TextView textKodeyEyeBlueView = (TextView) view.findViewById(R.id.brick_kodey_rgb_led_action_blue_edit_text);
			editBlue.setTextColor(editGreen.getTextColors().withAlpha(alphaValue));
			editBlue.getBackground().setAlpha(alphaValue);
			ColorStateList color3 = textKodeyEyeBlueView.getTextColors().withAlpha(alphaValue);
			if (adapterView != null) {
				((TextView) adapterView.getChildAt(0)).setTextColor(color3);
			}
			textKodeyEyeBlue.setTextColor(textKodeyEyeBlue.getTextColors().withAlpha(alphaValue));

			this.alphaValue = (alphaValue);
		}

		return view;
	}

	@Override
	public List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence) {
		sequence.addAction(ExtendedActions.kodeyRgbLedEyeAction(sprite, eyeEnum, red, green, blue));
		return null;
	}
}
