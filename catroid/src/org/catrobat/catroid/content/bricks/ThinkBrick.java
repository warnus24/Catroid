/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2014 The Catrobat Team
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
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.BrickValues;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ExtendedActions;
import org.catrobat.catroid.formulaeditor.Formula;

import java.util.List;

public class ThinkBrick extends BubbleBrickBaseType {

	private static final long serialVersionUID = 1L;

	public ThinkBrick() {
		addAllowedBrickField(BrickField.BUBBLE_TEXT);
		addAllowedBrickField(BrickField.BUBBLE_DURATION);
	}

	public ThinkBrick(String think) {
		initializeBrickFields(new Formula(think), new Formula((BUBBLE_DURATION)));
	}

	public ThinkBrick(Formula think) {
		initializeBrickFields(think, new Formula((BUBBLE_DURATION)));
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter baseAdapter) {
		if (animationState) {
			return view;
		}
		view = View.inflate(context, R.layout.brick_think, null);
		view = getViewWithAlpha(alphaValue);
		setCheckboxView(R.id.brick_think_checkbox);

		final Brick brickInstance = this;
		checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				checked = isChecked;
				adapter.handleCheck(brickInstance, isChecked);
			}
		});

		view.findViewById(R.id.brick_think_prototype_text_view).setVisibility(View.GONE);
		TextView sayEditText = (TextView) view.findViewById(R.id.brick_think_edit_text);
		sayEditText.setVisibility(View.VISIBLE);
		sayEditText.setOnClickListener(this);
		getFormulaWithBrickField(BrickField.BUBBLE_TEXT).setTextFieldId(R.id.brick_think_edit_text);
		getFormulaWithBrickField(BrickField.BUBBLE_TEXT).refreshTextField(view);

		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		prototypeView = View.inflate(context, R.layout.brick_think, null);
		TextView sayPrototypeTextView = (TextView) prototypeView.findViewById(R.id.brick_think_prototype_text_view);
		sayPrototypeTextView.setText(BrickValues.THINK);
		return prototypeView;
	}

	@Override
	public View getViewWithAlpha(int alphaValue) {
		if (view != null) {
			View layout = view.findViewById(R.id.brick_think_layout);
			layout.getBackground().setAlpha(alphaValue);

			TextView sayTextView = (TextView) view.findViewById(R.id.brick_think_textview);
			sayTextView.setTextColor(sayTextView.getTextColors().withAlpha(alphaValue));
			TextView sayEditText = (TextView) view.findViewById(R.id.brick_think_edit_text);
			sayEditText.setTextColor(sayEditText.getTextColors().withAlpha(alphaValue));
			sayEditText.getBackground().setAlpha(alphaValue);

			this.alphaValue = (alphaValue);
		}
		return view;
	}

	@Override
	public List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence) {
		bubble = View.inflate(this.context, R.layout.bubble_speech_new, null);
		((TextView) bubble.findViewById(R.id.bubble_edit_text)).setText(getNormalizedText(sprite));
		updateBubbleByteArrayFromDrawingCache();
		sequence.addAction(ExtendedActions.say(sprite, bubbleByteArray,
				getFormulaWithBrickField(BrickField.BUBBLE_DURATION.BUBBLE_DURATION)));
		return null;
	}
}
