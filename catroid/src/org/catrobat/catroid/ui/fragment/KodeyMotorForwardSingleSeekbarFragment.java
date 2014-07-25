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
package org.catrobat.catroid.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.KodeyMotorForwardActionBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.ui.BottomBar;
import org.catrobat.catroid.ui.ScriptActivity;

public class KodeyMotorForwardSingleSeekbarFragment extends SherlockFragment implements OnKeyListener {

	public static final String FORMULA_EDITOR_MOTOR_FORWARD_SINGLE_SEEKBAR_FRAGMENT_TAG = "kodey_motor_forward_editor_single_seekbar_fragment";
	public static final String BRICK_BUNDLE_ARGUMENT = "brick";
	public static final String FORMULA_BUNDLE_ARGUMENT_KODEY_MOTOR_SPEED = "speed";

	private Context context;
	private Brick currentBrick;
	private TextView formulaEditorEditTextSpeed;
	private TextView motorActionBrickSpeedTextView;
	private SeekBar speedSeekBar;
	private Formula speedFormula;
	private int speed;
	private LinearLayout kodeyBrick;
	private View brickView;
	private CharSequence previousActionBarTitle;
	private View fragmentView;
	private VariableDeletedReceiver variableDeletedReceiver;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		setUpActionBar();

		if(getArguments() != null) {
			currentBrick = (Brick) getArguments().getSerializable(BRICK_BUNDLE_ARGUMENT);
			speedFormula = (Formula) getArguments().getSerializable(FORMULA_BUNDLE_ARGUMENT_KODEY_MOTOR_SPEED);
		}
	}

	private void setUpActionBar() {
		ActionBar actionBar = getSherlockActivity().getSupportActionBar();
		previousActionBarTitle = ProjectManager.getInstance().getCurrentSprite().getName();
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(R.string.kodey_motor_forward_title);
	}

	private void resetActionBar() {
		ActionBar actionBar = getSherlockActivity().getSupportActionBar();
		actionBar.setTitle(previousActionBarTitle);
	}

	public static void showSingleSeekBarFragment(View view, Brick brick, Formula speed)
	{
		SherlockFragmentActivity activity = null;
		activity = (SherlockFragmentActivity) view.getContext();

		KodeyMotorForwardSingleSeekbarFragment formulaEditorSingleSeekbarFragment = (KodeyMotorForwardSingleSeekbarFragment) activity.getSupportFragmentManager()
				.findFragmentByTag(FORMULA_EDITOR_MOTOR_FORWARD_SINGLE_SEEKBAR_FRAGMENT_TAG);

		FragmentManager fragmentManager = activity.getSupportFragmentManager();
		FragmentTransaction fragTransaction = fragmentManager.beginTransaction();

		if (formulaEditorSingleSeekbarFragment == null) {
			formulaEditorSingleSeekbarFragment = new KodeyMotorForwardSingleSeekbarFragment();
			Bundle bundle = new Bundle();
			bundle.putSerializable(BRICK_BUNDLE_ARGUMENT, brick);
			bundle.putSerializable(FORMULA_BUNDLE_ARGUMENT_KODEY_MOTOR_SPEED, speed);
			formulaEditorSingleSeekbarFragment.setArguments(bundle);
			fragTransaction.add(R.id.script_fragment_container, formulaEditorSingleSeekbarFragment, FORMULA_EDITOR_MOTOR_FORWARD_SINGLE_SEEKBAR_FRAGMENT_TAG);
		}
			fragTransaction.hide(fragmentManager.findFragmentByTag(ScriptFragment.TAG));
			fragTransaction.show(formulaEditorSingleSeekbarFragment);
			BottomBar.hideBottomBar(activity);

		fragTransaction.commit();
	}

	public void updateBrickView() {
		updateBrickView(currentBrick);
	}

	private void updateBrickView(Brick newBrick) {
		currentBrick = newBrick;
		kodeyBrick.removeAllViews();
		View newBrickView = newBrick.getView(context, 0, null);
		kodeyBrick.addView(newBrickView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT));
		brickView = newBrickView;
	}

	private void onUserDismiss() {
		SherlockFragmentActivity activity = getSherlockActivity();
		FragmentManager fragmentManager = activity.getSupportFragmentManager();
		FragmentTransaction fragTransaction = fragmentManager.beginTransaction();
		fragTransaction.remove(this);
		fragTransaction.show(fragmentManager.findFragmentByTag(ScriptFragment.TAG));
		fragTransaction.commit();
		resetActionBar();
		BottomBar.showBottomBar(activity);
		BottomBar.showPlayButton(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		fragmentView = inflater.inflate(R.layout.fragment_kodey_motor_speed_chooser, container, false);
		fragmentView.setFocusableInTouchMode(true);
		fragmentView.requestFocus();

		context = getActivity();
		brickView = currentBrick.getView(context, 0, null);

		kodeyBrick = (LinearLayout) fragmentView.findViewById(R.id.motor_base_layout);

		kodeyBrick.addView(brickView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT));

		OnClickListener onClickListener = new OnClickListener() {
			@Override
			public void onClick(View view) {
				switch (view.getId())
				{
					case R.id.kodey_motor_speed_value:
						FormulaEditorFragment.showFragment(fragmentView, currentBrick, speedFormula);
						break;
				}
			}
		};

		formulaEditorEditTextSpeed = (TextView) fragmentView.findViewById(R.id.kodey_motor_speed_value);
		formulaEditorEditTextSpeed.setOnClickListener(onClickListener);
		speedFormula.setTextFieldId(R.id.kodey_motor_speed_value);

		speedSeekBar = (SeekBar) fragmentView.findViewById(R.id.kodey_motor_speed_seekbar);

		motorActionBrickSpeedTextView = (TextView) fragmentView.findViewById(R.id.brick_kodey_motor_forward_action_speed_edit_text);

		formulaEditorEditTextSpeed.setText(motorActionBrickSpeedTextView.getText());

		String speedFormulaString = formulaEditorEditTextSpeed.getText().toString().trim();

		speedSeekBar.setProgress(Integer.parseInt(speedFormulaString));

		SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
				switch(seekBar.getId())
				{
					case R.id.kodey_motor_speed_seekbar:
						formulaEditorEditTextSpeed.setText(String.valueOf(seekBar.getProgress()));
						((KodeyMotorForwardActionBrick)currentBrick).setSpeedTextValue(seekBar.getProgress());
						break;
					default:
						break;
				}
				speed = speedSeekBar.getProgress();
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

			}
		};

		speedSeekBar.setOnSeekBarChangeListener(seekBarChangeListener);

		return fragmentView;
	}

	@Override
	public void onStart() {
		getView().requestFocus();
		super.onStart();
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		for (int index = 0; index < menu.size(); index++) {
			menu.getItem(index).setVisible(false);
		}

		getSherlockActivity().getSupportActionBar().setDisplayShowTitleEnabled(true);
		getSherlockActivity().getSupportActionBar().setTitle(getString(R.string.kodey_motor_forward_title));

		super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onKey(View view, int keyCode, KeyEvent event) {
		Log.i("info", "onKey() in FE-Fragment! keyCode: " + keyCode);
		switch (keyCode) {
			case KeyEvent.KEYCODE_BACK:
					onUserDismiss();
				return true;
		}
		return false;
	}

	private class VariableDeletedReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(ScriptActivity.ACTION_VARIABLE_DELETED)) {
				updateBrickView(currentBrick);
			}
		}
	}

	@Override
	public void onPause() {
		super.onPause();

		if (variableDeletedReceiver != null) {
			getActivity().unregisterReceiver(variableDeletedReceiver);
		}
	}

	@Override
	public void onResume() {
		super.onResume();

		if (variableDeletedReceiver == null) {
			variableDeletedReceiver = new VariableDeletedReceiver();
		}

		if(getArguments() != null) {
			currentBrick = (Brick) getArguments().getSerializable(BRICK_BUNDLE_ARGUMENT);
			speedFormula = (Formula) getArguments().getSerializable(FORMULA_BUNDLE_ARGUMENT_KODEY_MOTOR_SPEED);
		}

		formulaEditorEditTextSpeed.setText(speedFormula.getDisplayString(this.context));

		String speedFormulaString = formulaEditorEditTextSpeed.getText().toString().trim();

		//ToDO: talk with Wolfgang (what happens if a values is greater than 255 and smaller than 0)
		speed = Integer.parseInt(speedFormulaString);
		speedSeekBar.setProgress(speed);

		IntentFilter filterVariableDeleted = new IntentFilter(ScriptActivity.ACTION_VARIABLE_DELETED);
		getActivity().registerReceiver(variableDeletedReceiver, filterVariableDeleted);
		BottomBar.hideBottomBar(getSherlockActivity());
	}
}