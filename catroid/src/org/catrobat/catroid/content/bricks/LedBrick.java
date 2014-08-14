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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
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

public class LedBrick extends BrickBaseType {
	private static final long serialVersionUID = 1L;

	private boolean currentSelectedValue = true;
	private ArrayAdapter<String> onOffAdapter = null;
	protected transient AdapterView<?> adapterView;

	public LedBrick() {
	}

	public LedBrick(boolean value) {
		this.currentSelectedValue = value;
	}

	@Override
	public Brick copyBrickForSprite(Sprite sprite) {
		return clone();
	}

	@Override
	public Brick clone() {
		return new LedBrick(this.currentSelectedValue);
	}

	@Override
	public View getView(final Context context, int brickId, BaseAdapter baseAdapter) {
		if (animationState) {
			return view;
		}

		if (view == null) {
			alphaValue = 0xFF;
		}

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = inflater.inflate(R.layout.brick_set_led, null);
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

		final Spinner onOffSpinner = (Spinner) view.findViewById(R.id.brick_set_led_spinner);
		onOffSpinner.setFocusableInTouchMode(false);
		onOffSpinner.setFocusable(false);
		if (!(checkbox.getVisibility() == View.VISIBLE)) {
			onOffSpinner.setClickable(true);
			onOffSpinner.setEnabled(true);
		} else {
			onOffSpinner.setClickable(false);
			onOffSpinner.setEnabled(false);
		}

		onOffSpinner.setAdapter(getAdapter(context));
		onOffSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				currentSelectedValue = parent.getSelectedItem().toString()
						.equals(context.getString(R.string.brick_led_on));
				adapterView = parent;
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});

		if (currentSelectedValue) {
			onOffSpinner.setSelection(onOffAdapter.getPosition(context.getString(R.string.brick_led_on)), true);
		} else {
			onOffSpinner.setSelection(onOffAdapter.getPosition(context.getString(R.string.brick_led_off)), true);
		}

		return view;
	}

	protected ArrayAdapter<String> getAdapter(Context context) {
		if (onOffAdapter == null) {
			onOffAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item);
			onOffAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			onOffAdapter.add(context.getString(R.string.brick_led_on));
			onOffAdapter.add(context.getString(R.string.brick_led_off));
		}
		return onOffAdapter;
	}

	@Override
	public View getViewWithAlpha( int alphaValue ) {
		if (view != null) {

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
		sequence.addAction(ExtendedActions.lights(currentSelectedValue));
		return null;
	}

	@Override
	public View getPrototypeView(Context context) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.brick_set_led, null);
		Spinner setLedSpinner = (Spinner) view.findViewById(R.id.brick_set_led_spinner);
		setLedSpinner.setFocusableInTouchMode(false);
		setLedSpinner.setFocusable(false);
		setLedSpinner.setAdapter(getAdapter(context));
		setLedSpinner.setSelection(onOffAdapter.getPosition(context.getString(R.string.brick_led_on)), true);
		return view;
	}

	@Override
	public int getRequiredResources() {
		return CAMERA_LED;
	}

}