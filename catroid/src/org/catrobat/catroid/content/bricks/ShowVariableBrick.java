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
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ExtendedActions;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.ui.adapter.UserVariableAdapter;
import org.catrobat.catroid.ui.adapter.UserVariableAdapterWrapper;
import org.catrobat.catroid.ui.dialogs.NewVariableDialog;
import org.catrobat.catroid.ui.dialogs.NewVariableDialog.NewVariableDialogListener;
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment;

import java.util.List;

public class ShowVariableBrick extends FormulaBrick implements OnClickListener, NewVariableDialogListener {
	private static final long serialVersionUID = 1L;
	private UserVariable userVariable;
	private transient AdapterView<?> adapterView;

	public ShowVariableBrick(Formula variableFormulaX, Formula variableFormulaY, UserVariable userVariable) {
		this.userVariable = userVariable;
		initializeBrickFields(variableFormulaX, variableFormulaY);
	}

	public ShowVariableBrick(double xValue, double yValue) {
		this.userVariable = null;
		initializeBrickFields(new Formula(xValue), new Formula (yValue));
	}

	private void initializeBrickFields(Formula xPositionVariable, Formula yPositionVariable) {
		addAllowedBrickField(BrickField.X_POSITION_VARIABLE);
		addAllowedBrickField(BrickField.Y_POSITION_VARIABLE);
		setFormulaWithBrickField(BrickField.X_POSITION_VARIABLE, xPositionVariable);
		setFormulaWithBrickField(BrickField.Y_POSITION_VARIABLE, yPositionVariable);
	}

	@Override
	public int getRequiredResources() {
		return getFormulaWithBrickField(BrickField.X_POSITION_VARIABLE).getRequiredResources();
	}

	@Override
	public List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence) {
		sequence.addAction(ExtendedActions.showVariable(sprite, getFormulaWithBrickField(BrickField.X_POSITION_VARIABLE), getFormulaWithBrickField(BrickField.Y_POSITION_VARIABLE), userVariable));
		return null;
	}

	@Override
	public View getView(final Context context, int brickId, BaseAdapter baseAdapter) {
		if (animationState) {
			return view;
		}
		if (view == null) {
			alphaValue = 255;
		}

		view = View.inflate(context, R.layout.brick_show_variable, null);
		view = getViewWithAlpha(alphaValue);
		setCheckboxView(R.id.brick_show_variable_checkbox);

		final Brick brickInstance = this;
		checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				checked = isChecked;
				adapter.handleCheck(brickInstance, isChecked);
			}
		});

		TextView textX = (TextView) view.findViewById(R.id.brick_show_variable_prototype_text_view_x);
		TextView editX = (TextView) view.findViewById(R.id.brick_show_variable_edit_text_x);
		getFormulaWithBrickField(BrickField.X_POSITION_VARIABLE).setTextFieldId(R.id.brick_show_variable_edit_text_x);
		getFormulaWithBrickField(BrickField.X_POSITION_VARIABLE).refreshTextField(view);

		textX.setVisibility(View.GONE);
		editX.setVisibility(View.VISIBLE);
		editX.setOnClickListener(this);

		TextView textY = (TextView) view.findViewById(R.id.brick_show_variable_prototype_text_view_y);
		TextView editY = (TextView) view.findViewById(R.id.brick_show_variable_edit_text_y);
		getFormulaWithBrickField(BrickField.Y_POSITION_VARIABLE).setTextFieldId(R.id.brick_show_variable_edit_text_y);
		getFormulaWithBrickField(BrickField.Y_POSITION_VARIABLE).refreshTextField(view);
		textY.setVisibility(View.GONE);
		editY.setVisibility(View.VISIBLE);
		editY.setOnClickListener(this);

		Spinner variableSpinner = (Spinner) view.findViewById(R.id.show_variable_spinner);
		UserVariableAdapter userVariableAdapter = ProjectManager.getInstance().getCurrentProject().getUserVariables()
				.createUserVariableAdapter(context, ProjectManager.getInstance().getCurrentSprite());
		UserVariableAdapterWrapper userVariableAdapterWrapper = new UserVariableAdapterWrapper(context,
				userVariableAdapter);
		userVariableAdapterWrapper.setItemLayout(android.R.layout.simple_spinner_item, android.R.id.text1);

		variableSpinner.setAdapter(userVariableAdapterWrapper);

		if (!(checkbox.getVisibility() == View.VISIBLE)) {
			variableSpinner.setClickable(true);
			variableSpinner.setEnabled(true);
		} else {
			variableSpinner.setClickable(false);
			variableSpinner.setFocusable(false);
		}

		setSpinnerSelection(variableSpinner, null);

		variableSpinner.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View view, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP
						&& (((Spinner) view).getSelectedItemPosition() == 0
						&& ((Spinner) view).getAdapter().getCount() == 1)) {
					NewVariableDialog dialog = new NewVariableDialog((Spinner) view);
					dialog.addVariableDialogListener(ShowVariableBrick.this);
					dialog.show(((SherlockFragmentActivity) view.getContext()).getSupportFragmentManager(),
							NewVariableDialog.DIALOG_FRAGMENT_TAG);
					return true;
				}

				return false;
			}
		});
		variableSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				if (position == 0 && ((UserVariableAdapterWrapper) parent.getAdapter()).isTouchInDropDownView()) {
					NewVariableDialog dialog = new NewVariableDialog((Spinner) parent);
					dialog.addVariableDialogListener(ShowVariableBrick.this);
					dialog.show(((SherlockFragmentActivity) view.getContext()).getSupportFragmentManager(),
							NewVariableDialog.DIALOG_FRAGMENT_TAG);
				}
				((UserVariableAdapterWrapper) parent.getAdapter()).resetIsTouchInDropDownView();
				userVariable = (UserVariable) parent.getItemAtPosition(position);
				adapterView = parent;
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				userVariable = null;
			}
		});

		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		View prototypeView = View.inflate(context, R.layout.brick_show_variable, null);
		Spinner variableSpinner = (Spinner) prototypeView.findViewById(R.id.show_variable_spinner);
		variableSpinner.setFocusableInTouchMode(false);
		variableSpinner.setFocusable(false);
		UserVariableAdapter userVariableAdapter = ProjectManager.getInstance().getCurrentProject().getUserVariables()
				.createUserVariableAdapter(context, ProjectManager.getInstance().getCurrentSprite());

		UserVariableAdapterWrapper userVariableAdapterWrapper = new UserVariableAdapterWrapper(context,
				userVariableAdapter);

		userVariableAdapterWrapper.setItemLayout(android.R.layout.simple_spinner_item, android.R.id.text1);
		variableSpinner.setAdapter(userVariableAdapterWrapper);
		setSpinnerSelection(variableSpinner, null);

		TextView textSetXPosition = (TextView) prototypeView.findViewById(R.id.brick_show_variable_prototype_text_view_x);
		textSetXPosition.setText(String.valueOf(BrickField.X_POSITION_VARIABLE));

		TextView textSetYPosition = (TextView) prototypeView.findViewById(R.id.brick_show_variable_prototype_text_view_y);
		textSetYPosition.setText(String.valueOf(BrickField.Y_POSITION_VARIABLE));

		return prototypeView;
	}

	@Override
	public View getViewWithAlpha(int alphaValue) {

		if (view != null) {

			TextView textShowVariable = (TextView) view.findViewById(R.id.brick_show_variable_label);
			Spinner variablebrickSpinner = (Spinner) view.findViewById(R.id.show_variable_spinner);

			ColorStateList color = textShowVariable.getTextColors().withAlpha(alphaValue);
			variablebrickSpinner.getBackground().setAlpha(alphaValue);
			if (adapterView != null) {
				((TextView) adapterView.getChildAt(0)).setTextColor(color);
			}

			TextView showX = (TextView) view.findViewById(R.id.brick_show_variable_x_textview);
			TextView showY = (TextView) view.findViewById(R.id.brick_show_variable_y_textview);
			TextView editX = (TextView) view.findViewById(R.id.brick_show_variable_edit_text_x);
			TextView editY = (TextView) view.findViewById(R.id.brick_show_variable_edit_text_y);
			textShowVariable.setTextColor(textShowVariable.getTextColors().withAlpha(alphaValue));
			showX.setTextColor(showX.getTextColors().withAlpha(alphaValue));
			showY.setTextColor(showY.getTextColors().withAlpha(alphaValue));
			editX.setTextColor(editX.getTextColors().withAlpha(alphaValue));
			editX.getBackground().setAlpha(alphaValue);
			editY.setTextColor(editY.getTextColors().withAlpha(alphaValue));
			editY.getBackground().setAlpha(alphaValue);

			this.alphaValue = (alphaValue);
		}

		return view;
	}

	@Override
	public Brick clone() {
		ShowVariableBrick clonedBrick = new ShowVariableBrick(getFormulaWithBrickField(BrickField.X_POSITION_VARIABLE).clone(), getFormulaWithBrickField(BrickField.Y_POSITION_VARIABLE).clone(), userVariable);
		return clonedBrick;
	}

	@Override
	public void onClick(View view) {
		if (checkbox.getVisibility() == View.VISIBLE) {
			return;
		}
		switch (view.getId()) {
			case R.id.brick_show_variable_edit_text_x:
				FormulaEditorFragment.showFragment(view, this, getFormulaWithBrickField(BrickField.X_POSITION_VARIABLE));
				break;

			case R.id.brick_show_variable_edit_text_y:
				FormulaEditorFragment.showFragment(view, this, getFormulaWithBrickField(BrickField.Y_POSITION_VARIABLE));
				break;
		}
	}

	@Override
	public Brick copyBrickForSprite(Sprite sprite) {
		ShowVariableBrick copyBrick = (ShowVariableBrick) clone();
		copyBrick.userVariable = ProjectManager.getInstance().getCurrentProject().getUserVariables().getUserVariable(userVariable.getName(), sprite);
		return copyBrick;
	}

	private void updateUserVariableIfDeleted(UserVariableAdapterWrapper userVariableAdapterWrapper) {
		if (userVariable != null
				&& (userVariableAdapterWrapper.getPositionOfItem(userVariable) == 0)) {
			userVariable = null;
		}

	}

	private void setSpinnerSelection(Spinner variableSpinner, UserVariable newUserVariable) {
		UserVariableAdapterWrapper userVariableAdapterWrapper = (UserVariableAdapterWrapper) variableSpinner
				.getAdapter();

		updateUserVariableIfDeleted(userVariableAdapterWrapper);

		if (userVariable != null) {
			variableSpinner.setSelection(userVariableAdapterWrapper.getPositionOfItem(userVariable), true);
		} else if (newUserVariable != null) {
			variableSpinner.setSelection(userVariableAdapterWrapper.getPositionOfItem(newUserVariable), true);
			userVariable = newUserVariable;
		} else {
			variableSpinner.setSelection(userVariableAdapterWrapper.getCount() - 1, true);
			userVariable = userVariableAdapterWrapper.getItem(userVariableAdapterWrapper.getCount() - 1);
		}
	}

	@Override
	public void onFinishNewVariableDialog(Spinner spinnerToUpdate, UserVariable newUserVariable) {
		UserVariableAdapterWrapper userVariableAdapterWrapper = ((UserVariableAdapterWrapper) spinnerToUpdate
				.getAdapter());
		userVariableAdapterWrapper.notifyDataSetChanged();
		setSpinnerSelection(spinnerToUpdate, newUserVariable);
	}
}
