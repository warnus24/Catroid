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
import android.view.LayoutInflater;
import android.view.View;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ExtendedActions;

import java.util.List;


public class VideoBrick extends BrickBaseType {

	private String selectedObject;
	private transient String oldVideoSetting;
	private transient AdapterView<?> adapterView;
	private transient View prototypeView;
	private transient ArrayAdapter<CharSequence> videoSpinnerAdapter;
	private transient Context context;

	public VideoBrick(Context context) {
		this.context = context;
		videoSpinnerAdapter =  ArrayAdapter.createFromResource(context,
				R.array.video_on_off, android.R.layout.simple_spinner_item);
	}

	@Override
	public int getRequiredResources() {
		return CAMERA;
	}

	@Override
	public View getPrototypeView(Context context) {

		Log.d("Lausi", "PrototypeView");
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View prototypeView = inflater.inflate(R.layout.brick_video, null);
		final Spinner videoSpinner = (Spinner) prototypeView.findViewById(R.id.brick_video_spinner);
		videoSpinner.setFocusableInTouchMode(false);
		videoSpinner.setFocusable(false);
		videoSpinner.setAdapter(videoSpinnerAdapter);
		videoSpinner.setSelection(videoSpinnerAdapter.getPosition(selectedObject), true);
		return prototypeView;
	}

	@Override
	public View getViewWithAlpha(int alphaValue) {

		Log.d("Lausi", "AlphaView");
		if (view != null) {

			View layout = view.findViewById(R.id.brick_video_layout);
			layout.getBackground().setAlpha(alphaValue);

			TextView textPointToLabel = (TextView) view.findViewById(R.id.brick_video_text_view);
			textPointToLabel.setTextColor(textPointToLabel.getTextColors().withAlpha(alphaValue));
			Spinner pointToSpinner = (Spinner) view.findViewById(R.id.brick_video_spinner);
			ColorStateList color = textPointToLabel.getTextColors().withAlpha(alphaValue);
			pointToSpinner.getBackground().setAlpha(alphaValue);
			if (adapterView != null) {
				((TextView) adapterView.getChildAt(0)).setTextColor(color);
			}

			this.alphaValue = alphaValue;
		}

		return view;
	}

	@Override
	public List<SequenceAction> addActionToSequence(SequenceAction sequence) {
		boolean videoOn = false;
		if (selectedObject.equals(context.getString(R.string.brick_video_on))) {
			videoOn = true;
		}
		sequence.addAction(ExtendedActions.turn(videoOn));
		return null;
	}

	@Override
	public View getView(final Context context, int brickId, BaseAdapter baseAdapter) {

		Log.d("Lausi", "View");
		if (animationState) {
			return view;
		}

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = inflater.inflate(R.layout.brick_video, null);
		view = getViewWithAlpha(alphaValue);

		setCheckboxView(R.id.brick_video_checkbox);

		final Brick brickInstance = this;
		checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				checked = isChecked;
				adapter.handleCheck(brickInstance, isChecked);
			}
		});

		final Spinner spinner = (Spinner) view.findViewById(R.id.brick_video_spinner);
		spinner.setFocusableInTouchMode(false);
		spinner.setFocusable(false);
		if (!(checkbox.getVisibility() == View.VISIBLE)) {
			spinner.setClickable(true);
			spinner.setEnabled(true);
		} else {
			spinner.setClickable(false);
			spinner.setEnabled(false);
		}

		spinner.setAdapter(videoSpinnerAdapter);
		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				String itemSelected = parent.getSelectedItem().toString();

				if (itemSelected.equals(context.getString(R.string.brick_video_on))) {
					selectedObject = context.getString(R.string.brick_video_on);
				} else if(itemSelected.equals(context.getString(R.string.brick_video_off))){
					selectedObject = context.getString(R.string.brick_video_off);
				}
				adapterView = parent;
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		spinner.setSelection(videoSpinnerAdapter.getPosition(selectedObject), true);
		return view;
	}

	@Override
	public Brick clone() {
		return this;
	}
}
