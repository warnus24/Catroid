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
package org.catrobat.catroid.physics.content.bricks;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.BrickBaseType;
import org.catrobat.catroid.physics.PhysicsObject;

import java.util.List;

public class SetCollisionFilterBrick extends BrickBaseType implements Cloneable{
	private static final long serialVersionUID = 1L;

	private PhysicsObject.Behavior behavior = PhysicsObject.Behavior.NEUTRAL;
	private transient AdapterView<?> adapterView;

	private transient View prototypeView;

	public SetCollisionFilterBrick() {
	}

	public SetCollisionFilterBrick(Sprite sprite, PhysicsObject.Behavior behavior) {
		this.sprite = sprite;
		this.behavior = behavior;
	}

	@Override
	public int getRequiredResources() {
		return PHYSIC;
	}

	@Override
	public Sprite getSprite() {
		return this.sprite;
	}

	@Override
	public Brick clone() {
		return new SetCollisionFilterBrick(sprite, behavior);
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter baseAdapter) {
		if (animationState) {
			return view;
		}

		view = View.inflate(context, R.layout.brick_physics_set_physics_collision_filter, null);
		view = getViewWithAlpha(alphaValue);

		setCheckboxView(R.id.brick_set_physics_collision_filter_checkbox);
		final Brick brickInstance = this;
		checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				checked = isChecked;
				adapter.handleCheck(brickInstance, isChecked);
			}

		});

		final Spinner spinner = (Spinner) view.findViewById(R.id.brick_set_physics_collision_filter_spinner);
		spinner.setAdapter(createAdapter(context));
		spinner.setSelection(behavior.ordinal());

		spinner.setClickable(true);
		spinner.setFocusable(true);
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				if (position < PhysicsObject.Behavior.values().length) {
					behavior = PhysicsObject.Behavior.values()[position];
					adapterView = parent;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		return view;
	}

	private ArrayAdapter<String> createAdapter(Context context) {
		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item);
		arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		for (String type : context.getResources().getStringArray(R.array.physics_collision_filters)) {
			arrayAdapter.add(type);
		}

		return arrayAdapter;
	}

	@Override
	public View getViewWithAlpha(int alphaValue) {

		if (view != null) {

			View layout = view.findViewById(R.id.brick_set_physics_collision_filter_layout);
			layout.getBackground().setAlpha(alphaValue);

			TextView textPointToLabel = (TextView) view.findViewById(R.id.brick_set_physics_collision_filter_text_view);
			textPointToLabel.setTextColor(textPointToLabel.getTextColors().withAlpha(alphaValue));
			Spinner pointToSpinner = (Spinner) view.findViewById(R.id.brick_set_physics_collision_filter_spinner);
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
	public View getPrototypeView(Context context) {
		prototypeView = View.inflate(context, R.layout.brick_physics_set_physics_collision_filter, null);
		Spinner pointToSpinner = (Spinner) prototypeView.findViewById(R.id.brick_set_physics_collision_filter_spinner);
		pointToSpinner.setFocusableInTouchMode(false);
		pointToSpinner.setFocusable(false);
		SpinnerAdapter objectTypeSpinnerAdapter = createAdapter(context);
		pointToSpinner.setAdapter(objectTypeSpinnerAdapter);
		pointToSpinner.setSelection(PhysicsObject.Behavior.NEUTRAL.ordinal());
		return prototypeView;
	}

	@Override
	public List<SequenceAction> addActionToSequence(SequenceAction sequence) {
		//		sequence.addAction(ExtendedActions.setPhysicsObjectType(sprite, physicsObject, type));
		sequence.addAction(sprite.getActionFactory().createSetPhysicsCollisionFilterAction(sprite, behavior));
		return null;
	}

	@Override
	public Brick copyBrickForSprite(Sprite sprite, Script script) {
		SetCollisionFilterBrick copyBrick = (SetCollisionFilterBrick) clone();
		copyBrick.sprite = sprite;
		return copyBrick;
	}
}
