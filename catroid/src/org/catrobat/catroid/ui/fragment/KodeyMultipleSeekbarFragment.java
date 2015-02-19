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
import android.graphics.Color;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.KodeyRGBLightBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.FormulaEditorEditText;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.formulaeditor.InternFormulaParser;
import org.catrobat.catroid.formulaeditor.InternFormulaState;
import org.catrobat.catroid.ui.BottomBar;
import org.catrobat.catroid.ui.ScriptActivity;

public class KodeyMultipleSeekbarFragment extends SherlockFragment implements OnKeyListener {

	public static final String FORMULA_EDITOR_MULTIPLE_SEEKBAR_FRAGMENT_TAG = "kodey_editor_multiple_seekbar_fragment";
	public static final String BRICK_BUNDLE_ARGUMENT = "brick";
	public static final String FORMULA_BUNDLE_ARGUMENT_RED = "red";
	public static final String FORMULA_BUNDLE_ARGUMENT_GREEN = "green";
	public static final String FORMULA_BUNDLE_ARGUMENT_BLUE = "blue";

	private Context context;
	private Brick currentBrick;
	private Formula redFormula;
	private Formula greenFormula;
	private Formula blueFormula;
	private View colorPreviewView;
	private TextView formulaEditorEditTextRed;
	private TextView formulaEditorEditTextGreen;
	private TextView formulaEditorEditTextBlue;
	private TextView redLightBrickTextView;
	private TextView greenLightBrickTextView;
	private TextView blueLightBrickTextView;
	private SeekBar redSeekBar;
	private SeekBar greenSeekBar;
	private SeekBar blueSeekBar;
	private int color;
	private int referenceColor;
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
			redFormula = (Formula) getArguments().getSerializable(FORMULA_BUNDLE_ARGUMENT_RED);
			greenFormula = (Formula) getArguments().getSerializable(FORMULA_BUNDLE_ARGUMENT_GREEN);
			blueFormula = (Formula) getArguments().getSerializable(FORMULA_BUNDLE_ARGUMENT_BLUE);
			//color = Color.rgb(0, 255, 255);
		}
		/*
		//ToDO: also for Kodey Sensors
		if (currentFormula.containsArduinoSensors()) {
			ProjectManager.getInstance().getCurrentProject().setIsArduinoProject(true);
		} else {
			ProjectManager.getInstance().getCurrentProject().setIsArduinoProject(false);
		}
		*/
	}

	private void setUpActionBar() {
		ActionBar actionBar = getSherlockActivity().getSupportActionBar();
		previousActionBarTitle = ProjectManager.getInstance().getCurrentSprite().getName();
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(R.string.kodey_color_chooser_title);
	}

	private void resetActionBar() {
		ActionBar actionBar = getSherlockActivity().getSupportActionBar();
		actionBar.setTitle(previousActionBarTitle);
	}

	public static void showMultipleSeekBarFragment(View view, Brick brick, Formula red, Formula green, Formula blue)
	{
		SherlockFragmentActivity activity = null;
		activity = (SherlockFragmentActivity) view.getContext();

		KodeyMultipleSeekbarFragment formulaEditorMultipleSeekbarFragment = (KodeyMultipleSeekbarFragment) activity.getSupportFragmentManager()
				.findFragmentByTag(FORMULA_EDITOR_MULTIPLE_SEEKBAR_FRAGMENT_TAG);

		FragmentManager fragmentManager = activity.getSupportFragmentManager();
		FragmentTransaction fragTransaction = fragmentManager.beginTransaction();

		if (formulaEditorMultipleSeekbarFragment == null) {
			formulaEditorMultipleSeekbarFragment = new KodeyMultipleSeekbarFragment();
			Bundle bundle = new Bundle();
			bundle.putSerializable(BRICK_BUNDLE_ARGUMENT, brick);
			bundle.putSerializable(FORMULA_BUNDLE_ARGUMENT_RED, red);
			bundle.putSerializable(FORMULA_BUNDLE_ARGUMENT_GREEN, green);
			bundle.putSerializable(FORMULA_BUNDLE_ARGUMENT_BLUE, blue);
			formulaEditorMultipleSeekbarFragment.setArguments(bundle);
			fragTransaction.add(R.id.script_fragment_container, formulaEditorMultipleSeekbarFragment, FORMULA_EDITOR_MULTIPLE_SEEKBAR_FRAGMENT_TAG);
		}

			fragTransaction.hide(fragmentManager.findFragmentByTag(ScriptFragment.TAG));
			fragTransaction.show(formulaEditorMultipleSeekbarFragment);
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

		fragmentView = inflater.inflate(R.layout.fragment_kodey_rgb_color_chooser, container, false);
		fragmentView.setFocusableInTouchMode(true);
		fragmentView.requestFocus();

		context = getActivity();
		brickView = currentBrick.getView(context, 0, null);

		kodeyBrick = (LinearLayout) fragmentView.findViewById(R.id.rgb_base_layout);

		kodeyBrick.addView(brickView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT));

		OnClickListener onClickListener = new OnClickListener() {
			@Override
			public void onClick(View view) {
				switch (view.getId())
				{
					case R.id.rgb_red_value:
						FormulaEditorFragment.showFragment(fragmentView, currentBrick, redFormula);
						break;
					case R.id.rgb_green_value:
						FormulaEditorFragment.showFragment(fragmentView, currentBrick, greenFormula);
						break;
					case R.id.rgb_blue_value:
						FormulaEditorFragment.showFragment(fragmentView, currentBrick, blueFormula);
						break;
				}
			}
		};

		formulaEditorEditTextRed = (TextView) fragmentView.findViewById(R.id.rgb_red_value);
		formulaEditorEditTextRed.setOnClickListener(onClickListener);
		redFormula.setTextFieldId(R.id.rgb_red_value);

		formulaEditorEditTextGreen = (TextView) fragmentView.findViewById(R.id.rgb_green_value);
		formulaEditorEditTextGreen.setOnClickListener(onClickListener);
		greenFormula.setTextFieldId(R.id.rgb_green_value);

		formulaEditorEditTextBlue = (TextView) fragmentView.findViewById(R.id.rgb_blue_value);
		formulaEditorEditTextBlue.setOnClickListener(onClickListener);
		blueFormula.setTextFieldId(R.id.rgb_blue_value);

		redSeekBar = (SeekBar) fragmentView.findViewById(R.id.color_rgb_seekbar_red);
		greenSeekBar = (SeekBar) fragmentView.findViewById(R.id.color_rgb_seekbar_green);
		blueSeekBar = (SeekBar) fragmentView.findViewById(R.id.color_rgb_seekbar_blue);

		redLightBrickTextView = (TextView) fragmentView.findViewById(R.id.brick_kodey_rgb_led_action_red_edit_text);
		greenLightBrickTextView = (TextView) fragmentView.findViewById(R.id.brick_kodey_rgb_led_action_green_edit_text);
		blueLightBrickTextView = (TextView) fragmentView.findViewById(R.id.brick_kodey_rgb_led_action_blue_edit_text);

		formulaEditorEditTextRed.setText(redLightBrickTextView.getText());
		formulaEditorEditTextGreen.setText(greenLightBrickTextView.getText());
		formulaEditorEditTextBlue.setText(blueLightBrickTextView.getText());

		String redFormulaString = formulaEditorEditTextRed.getText().toString().trim();
		String greenFormulaString = formulaEditorEditTextGreen.getText().toString().trim();
		String blueFormulaString = formulaEditorEditTextBlue.getText().toString().trim();

		colorPreviewView = fragmentView.findViewById(R.id.color_rgb_preview);

		color = Color.rgb(Integer.parseInt(redFormulaString), Integer.parseInt(greenFormulaString), Integer.parseInt(blueFormulaString));
		redSeekBar.setProgress(Color.red(color));
		greenSeekBar.setProgress(Color.green(color));
		blueSeekBar.setProgress(Color.blue(color));

		SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
				//enter value to textview
				switch(seekBar.getId())
				{
					case R.id.color_rgb_seekbar_red:
						formulaEditorEditTextRed.setText(String.valueOf(seekBar.getProgress()));
						((KodeyRGBLightBrick)currentBrick).setRedTextValues(seekBar.getProgress());
						break;
					case R.id.color_rgb_seekbar_green:
						formulaEditorEditTextGreen.setText(String.valueOf(seekBar.getProgress()));
						((KodeyRGBLightBrick)currentBrick).setGreenTextValues(seekBar.getProgress());
						break;
					case R.id.color_rgb_seekbar_blue:
						formulaEditorEditTextBlue.setText(String.valueOf(seekBar.getProgress()));
						((KodeyRGBLightBrick)currentBrick).setBlueTextValues(seekBar.getProgress());
						break;
					default:
						break;
				}
				color = Color.rgb(redSeekBar.getProgress(), greenSeekBar.getProgress(), blueSeekBar.getProgress());
				colorPreviewView.setBackgroundColor(color | 0xFF000000);
				colorPreviewView.invalidate();
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

			}
		};

		redSeekBar.setOnSeekBarChangeListener(seekBarChangeListener);
		greenSeekBar.setOnSeekBarChangeListener(seekBarChangeListener);
		blueSeekBar.setOnSeekBarChangeListener(seekBarChangeListener);

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
		getSherlockActivity().getSupportActionBar().setTitle(getString(R.string.kodey_color_chooser_title));

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
			redFormula = (Formula) getArguments().getSerializable(FORMULA_BUNDLE_ARGUMENT_RED);
			greenFormula = (Formula) getArguments().getSerializable(FORMULA_BUNDLE_ARGUMENT_GREEN);
			blueFormula = (Formula) getArguments().getSerializable(FORMULA_BUNDLE_ARGUMENT_BLUE);
		}

		formulaEditorEditTextRed.setText(redFormula.getDisplayString(this.context));
		formulaEditorEditTextGreen.setText(greenFormula.getDisplayString(this.context));
		formulaEditorEditTextBlue.setText(blueFormula.getDisplayString(this.context));

		String redFormulaString = formulaEditorEditTextRed.getText().toString().trim();
		String greenFormulaString = formulaEditorEditTextGreen.getText().toString().trim();
		String blueFormulaString = formulaEditorEditTextBlue.getText().toString().trim();

		color = Color.rgb(Integer.parseInt(redFormulaString), Integer.parseInt(greenFormulaString), Integer.parseInt(blueFormulaString));
		redSeekBar.setProgress(Color.red(color));
		greenSeekBar.setProgress(Color.green(color));
		blueSeekBar.setProgress(Color.blue(color));

		colorPreviewView.setBackgroundColor(color | 0xFF000000);
		colorPreviewView.invalidate();

		IntentFilter filterVariableDeleted = new IntentFilter(ScriptActivity.ACTION_VARIABLE_DELETED);
		getActivity().registerReceiver(variableDeletedReceiver, filterVariableDeleted);
		BottomBar.hideBottomBar(getSherlockActivity());
	}
}