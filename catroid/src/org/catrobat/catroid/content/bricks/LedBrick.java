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
package org.catrobat.catroid.content.bricks;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.view.View;
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
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ExtendedActions;

import java.util.List;

public class LedBrick extends BrickBaseType implements OnItemSelectedListener {
	private static final long serialVersionUID = 1L;

	private transient LightValue lightEnum;
	private String currentLightValue;
	protected transient AdapterView<?> adapterView;

	public static enum LightValue {
		LED_ON, LED_OFF
	}

	public LedBrick(LightValue lightValue) {
		this.lightEnum = lightValue;
		this.currentLightValue = lightValue.name();
	}

	protected Object readResolve() {
		if (currentLightValue != null) {
			lightEnum = LightValue.valueOf(currentLightValue);
		}
		return this;
	}

	@Override
	public int getRequiredResources() {
		return CAMERA_LED;
	}

	@Override
	public Brick copyBrickForSprite(Sprite sprite) {
		return clone();
	}

	@Override
	public View getPrototypeView(Context context) {
		View prototypeView = View.inflate(context, R.layout.brick_set_led, null);
		Spinner setLedSpinner = (Spinner) prototypeView.findViewById(R.id.brick_set_led_spinner);
		setLedSpinner.setFocusableInTouchMode(false);
		setLedSpinner.setFocusable(false);

		ArrayAdapter<CharSequence> ledAdapter = ArrayAdapter.createFromResource(context,
				R.array.set_led_chooser, android.R.layout.simple_spinner_item);
		ledAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		setLedSpinner.setAdapter(ledAdapter);
		setLedSpinner.setSelection(lightEnum.ordinal());
		return prototypeView;
	}

	@Override
	public Brick clone() {
		return new LedBrick(lightEnum);
	}

	@Override
	public View getView(final Context context, int brickId, BaseAdapter baseAdapter) {
		if (animationState) {
			return view;
		}

		if (view == null) {
			alphaValue = 0xFF;
		}

		view = View.inflate(context, R.layout.brick_set_led, null);
		view = getViewWithAlpha(alphaValue);

		setCheckboxView(R.id.brick_set_led_checkbox);
		final Brick brickInstance = this;
		checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				checked = isChecked;
				adapter.handleCheck(brickInstance, isChecked);
			}
		});

		ArrayAdapter<CharSequence> ledAdapter = ArrayAdapter.createFromResource(context,
				R.array.set_led_chooser, android.R.layout.simple_spinner_item);
		ledAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		Spinner ledSpinner = (Spinner) view.findViewById(R.id.brick_set_led_spinner);
		ledSpinner.setOnItemSelectedListener(this);

		if (!(checkbox.getVisibility() == View.VISIBLE)) {
			ledSpinner.setClickable(true);
			ledSpinner.setEnabled(true);
		} else {
			ledSpinner.setClickable(false);
			ledSpinner.setEnabled(false);
		}

		ledSpinner.setAdapter(ledAdapter);
		ledSpinner.setSelection(lightEnum.ordinal());
		return view;
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		lightEnum = LightValue.values()[position];
		currentLightValue = lightEnum.name();
		adapterView = parent;
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
	}

	@Override
	public View getViewWithAlpha( int alphaValue ) {
		if (view != null) {
			View layout = view.findViewById(R.id.brick_set_led_layout);
			Drawable background = layout.getBackground();
			background.setAlpha(alphaValue);

			TextView textLedBrickLabel = (TextView) view.findViewById(R.id.brick_set_led_label);
			textLedBrickLabel.setTextColor(textLedBrickLabel.getTextColors().withAlpha(alphaValue));
			Spinner onOffSpinner = (Spinner) view.findViewById(R.id.brick_set_led_spinner);
			ColorStateList color = textLedBrickLabel.getTextColors().withAlpha(alphaValue);
			onOffSpinner.getBackground().setAlpha(alphaValue);
			if (adapterView != null) {
				((TextView) adapterView.getChildAt(0)).setTextColor(color);
			}
			this.alphaValue = (alphaValue);
		}
		return view;
	}

	@Override
	public List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence) {
		if (lightEnum.equals(LightValue.LED_ON)) {
			sequence.addAction(ExtendedActions.lights(true));
		} else {
			sequence.addAction(ExtendedActions.lights(false));
		}

		return null;
	}


}