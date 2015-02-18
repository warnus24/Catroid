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

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageButton;
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
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.FormulaEditorEditText;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.formulaeditor.InternFormulaParser;
import org.catrobat.catroid.ui.BottomBar;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.dialogs.CustomAlertDialogBuilder;
import org.catrobat.catroid.ui.dialogs.FormulaEditorComputeDialog;
import org.catrobat.catroid.ui.dialogs.NewStringDialog;

public class KodeyMultipleSeekbarFragment extends SherlockFragment implements OnKeyListener, ViewTreeObserver.OnGlobalLayoutListener {

	private static final int PARSER_OK = -1;
	private static final int PARSER_STACK_OVERFLOW = -2;
	private static final int PARSER_INPUT_SYNTAX_ERROR = -3;

	private static final int SET_FORMULA_ON_CREATE_VIEW = 0;
	private static final int SET_FORMULA_ON_SWITCH_EDIT_TEXT = 1;
	private static final int TIME_WINDOW = 2000;

	public static final String KODEY_COLOR_EDITOR_FRAGMENT_TAG = "kodey_editor_fragment";
	public static final String FORMULA_EDITOR_MULTIPLE_SEEKBAR_FRAGMENT_TAG = "kodey_editor_multiple_seekbar_fragment";
	public static final String BRICK_BUNDLE_ARGUMENT = "brick";
	public static final String FORMULA_BUNDLE_ARGUMENT = "formula";

	private Context context;
	private Brick currentBrick;
	private Formula currentFormula;
	private FormulaEditorEditText formulaEditorEditTextRed;
	private FormulaEditorEditText formulaEditorEditTextGreen;
	private FormulaEditorEditText formulaEditorEditTextBlue;
	private TextView redLightBrickTextView;
	private TextView greenLightBrickTextView;
	private TextView blueLightBrickTextView;
	private SeekBar redSeekBar;
	private SeekBar greenSeekBar;
	private SeekBar blueSeekBar;
	private int color;
	private LinearLayout kodeyBrick;
	private Toast toast;
	private View brickView;
	private long[] confirmSwitchEditTextTimeStamp = { 0, 0 };
	private int confirmSwitchEditTextCounter = 0;
	private CharSequence previousActionBarTitle;
	private View fragmentView;
	private VariableDeletedReceiver variableDeletedReceiver;

	public KodeyMultipleSeekbarFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		setUpActionBar();
		currentBrick = (Brick) getArguments().getSerializable(BRICK_BUNDLE_ARGUMENT);
		currentFormula = (Formula) getArguments().getSerializable(FORMULA_BUNDLE_ARGUMENT);
		color = Color.rgb(0, 255, 255);

		//ToDO: also for Kodey Sensors
		if (currentFormula.containsArduinoSensors()) {
			ProjectManager.getInstance().getCurrentProject().setIsArduinoProject(true);
		} else {
			ProjectManager.getInstance().getCurrentProject().setIsArduinoProject(false);
		}
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

	public static void showFragment(View view, Brick brick, Formula formula) {

		SherlockFragmentActivity activity = null;
		activity = (SherlockFragmentActivity) view.getContext();

		KodeyMultipleSeekbarFragment formulaEditorFragment = (KodeyMultipleSeekbarFragment) activity.getSupportFragmentManager()
				.findFragmentByTag(KODEY_COLOR_EDITOR_FRAGMENT_TAG);

		FragmentManager fragmentManager = activity.getSupportFragmentManager();
		FragmentTransaction fragTransaction = fragmentManager.beginTransaction();

		if (formulaEditorFragment == null) {
			formulaEditorFragment = new KodeyMultipleSeekbarFragment();
			Bundle bundle = new Bundle();
			bundle.putSerializable(BRICK_BUNDLE_ARGUMENT, brick);
			bundle.putSerializable(FORMULA_BUNDLE_ARGUMENT, formula);
			formulaEditorFragment.setArguments(bundle);

			fragTransaction.add(R.id.script_fragment_container, formulaEditorFragment, KODEY_COLOR_EDITOR_FRAGMENT_TAG);
			fragTransaction.hide(fragmentManager.findFragmentByTag(ScriptFragment.TAG));
			fragTransaction.show(formulaEditorFragment);
			BottomBar.hideBottomBar(activity);
		} else if (formulaEditorFragment.isHidden()) {
			formulaEditorFragment.updateBrickViewAndFormula(brick, formula);
			fragTransaction.hide(fragmentManager.findFragmentByTag(ScriptFragment.TAG));
			fragTransaction.show(formulaEditorFragment);
			BottomBar.hideBottomBar(activity);
		} else {
			formulaEditorFragment.setInputFormula(formula, SET_FORMULA_ON_SWITCH_EDIT_TEXT);
		}
		fragTransaction.commit();
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
			bundle.putSerializable(FORMULA_BUNDLE_ARGUMENT, red);
			bundle.putSerializable(FORMULA_BUNDLE_ARGUMENT, green);
			bundle.putSerializable(FORMULA_BUNDLE_ARGUMENT, blue);
			formulaEditorMultipleSeekbarFragment.setArguments(bundle);

			fragTransaction.add(R.id.script_fragment_container, formulaEditorMultipleSeekbarFragment, FORMULA_EDITOR_MULTIPLE_SEEKBAR_FRAGMENT_TAG);
			fragTransaction.hide(fragmentManager.findFragmentByTag(ScriptFragment.TAG));
			fragTransaction.show(formulaEditorMultipleSeekbarFragment);
			BottomBar.hideBottomBar(activity);
		} else if (formulaEditorMultipleSeekbarFragment.isHidden()) {
			formulaEditorMultipleSeekbarFragment.updateBrickViewAndFormula(brick, red);//nur ein Wert
			fragTransaction.hide(fragmentManager.findFragmentByTag(ScriptFragment.TAG));
			fragTransaction.show(formulaEditorMultipleSeekbarFragment);
			BottomBar.hideBottomBar(activity);
		} else {
			formulaEditorMultipleSeekbarFragment.setInputFormula(red, SET_FORMULA_ON_SWITCH_EDIT_TEXT);
		}
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
		fragmentView.getViewTreeObserver().addOnGlobalLayoutListener(this);

	}

	private void updateBrickViewAndFormula(Brick newBrick, Formula newFormula) {
		updateBrickView(newBrick);
		currentFormula = newFormula;
		setInputFormula(newFormula, SET_FORMULA_ON_CREATE_VIEW);
		fragmentView.getViewTreeObserver().addOnGlobalLayoutListener(this);
	}

	private void onUserDismiss() {
		formulaEditorEditTextRed.endEdit();
		formulaEditorEditTextGreen.endEdit();
		formulaEditorEditTextBlue.endEdit();
		currentFormula.prepareToRemove();

		SherlockFragmentActivity activity = getSherlockActivity();
		FragmentManager fragmentManager = activity.getSupportFragmentManager();
		FragmentTransaction fragTransaction = fragmentManager.beginTransaction();
		fragTransaction.hide(this);
		fragTransaction.show(fragmentManager.findFragmentByTag(ScriptFragment.TAG));
		fragTransaction.commit();

		resetActionBar();

		BottomBar.showBottomBar(activity);
		BottomBar.showPlayButton(activity);

	}

	/*
	public void setSelectedColor(int color) {
		int colorRed = Color.red(color);
		int colorGreen = Color.green(color);
		int colorBlue = Color.blue(color);
		redSeekBar.setProgress(colorRed);
		greenSeekBar.setProgress(colorGreen);
		blueSeekBar.setProgress(colorBlue);
		formulaEditorEditTextRed.setText(Integer.toString(colorRed));
		formulaEditorEditTextGreen.setText(Integer.toString(colorGreen));
		formulaEditorEditTextBlue.setText(Integer.toString(colorBlue));
	}

	public int getSelectedColor() {
		return Color.rgb(redSeekBar.getProgress(), greenSeekBar.getProgress(),
				blueSeekBar.getProgress());
	}
	*/

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

		formulaEditorEditTextRed = (FormulaEditorEditText) fragmentView.findViewById(R.id.rgb_red_value);
		formulaEditorEditTextRed.init(this);
		formulaEditorEditTextGreen = (FormulaEditorEditText) fragmentView.findViewById(R.id.rgb_green_value);
		formulaEditorEditTextGreen.init(this);
		formulaEditorEditTextBlue = (FormulaEditorEditText) fragmentView.findViewById(R.id.rgb_blue_value);
		formulaEditorEditTextBlue.init(this);

		fragmentView.getViewTreeObserver().addOnGlobalLayoutListener(this);

		redSeekBar = (SeekBar) fragmentView.findViewById(R.id.color_rgb_seekbar_red);
		greenSeekBar = (SeekBar) fragmentView.findViewById(R.id.color_rgb_seekbar_green);
		blueSeekBar = (SeekBar) fragmentView.findViewById(R.id.color_rgb_seekbar_blue);

		redLightBrickTextView = (TextView) fragmentView.findViewById(R.id.brick_kodey_rgb_led_action_red_edit_text);
		greenLightBrickTextView = (TextView) fragmentView.findViewById(R.id.brick_kodey_rgb_led_action_green_edit_text);
		blueLightBrickTextView = (TextView) fragmentView.findViewById(R.id.brick_kodey_rgb_led_action_blue_edit_text);

		formulaEditorEditTextRed.setText(redLightBrickTextView.getText());
		formulaEditorEditTextGreen.setText(greenLightBrickTextView.getText());
		formulaEditorEditTextBlue.setText(blueLightBrickTextView.getText());

		redSeekBar.setProgress(Color.red(color));
		greenSeekBar.setProgress(Color.green(color));
		blueSeekBar.setProgress(Color.blue(color));


		SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
				//enter value to textview
				color = Color.rgb(redSeekBar.getProgress(), greenSeekBar.getProgress(), blueSeekBar.getProgress());

				switch(seekBar.getId())
				{
					case R.id.color_rgb_seekbar_red:
						formulaEditorEditTextRed.setText(Integer.toString(seekBar.getProgress()));
						redLightBrickTextView.setText(Integer.toString(seekBar.getProgress()));
						//saveFormulaRedIfPossible();
						break;
					case R.id.color_rgb_seekbar_green:
						formulaEditorEditTextGreen.setText(Integer.toString(seekBar.getProgress()));
						greenLightBrickTextView.setText(Integer.toString(seekBar.getProgress()));
						//saveFormulaGreenIfPossible();
						break;
					case R.id.color_rgb_seekbar_blue:
						formulaEditorEditTextBlue.setText(Integer.toString(seekBar.getProgress()));
						blueLightBrickTextView.setText(Integer.toString(seekBar.getProgress()));
						//saveFormulaBlueIfPossible();
						break;
					default:
						break;
				}
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
		View.OnTouchListener touchListener = new View.OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent event) {
				Log.i("info", "viewId: " + view.getId());
				if (event.getAction() == MotionEvent.ACTION_UP) {
					view.setPressed(false);
					return true;
				}
				return false;
			}
		};

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

	private void setInputFormula(Formula newFormula, int mode) {

		int orientation = getResources().getConfiguration().orientation;

		switch (mode) {
			case SET_FORMULA_ON_CREATE_VIEW:
				formulaEditorEditTextRed.enterNewFormula(currentFormula.getInternFormulaState());
//				currentFormula.highlightTextField(brickView, orientation);
				refreshFormulaPreviewString();
				formulaEditorEditTextGreen.enterNewFormula(currentFormula.getInternFormulaState());
//				currentFormula.highlightTextField(brickView, orientation);
				refreshFormulaPreviewString();
				formulaEditorEditTextBlue.enterNewFormula(currentFormula.getInternFormulaState());
//				currentFormula.highlightTextField(brickView, orientation);
				refreshFormulaPreviewString();
				break;
			case SET_FORMULA_ON_SWITCH_EDIT_TEXT:

				if (currentFormula == newFormula && formulaEditorEditTextRed.hasChanges()) {
					formulaEditorEditTextRed.quickSelect();
					break;
				}
				if (currentFormula == newFormula && formulaEditorEditTextGreen.hasChanges()) {
					formulaEditorEditTextGreen.quickSelect();
					break;
				}
				if (currentFormula == newFormula && formulaEditorEditTextBlue.hasChanges()) {
					formulaEditorEditTextBlue.quickSelect();
					break;
				}
				if (formulaEditorEditTextRed.hasChanges()) {
					confirmSwitchEditTextTimeStamp[0] = confirmSwitchEditTextTimeStamp[1];
					confirmSwitchEditTextTimeStamp[1] = System.currentTimeMillis();
					confirmSwitchEditTextCounter++;
					if (!saveFormulaRedIfPossible()) {
						return;
					}
				}
				if (formulaEditorEditTextGreen.hasChanges()) {
					confirmSwitchEditTextTimeStamp[0] = confirmSwitchEditTextTimeStamp[1];
					confirmSwitchEditTextTimeStamp[1] = System.currentTimeMillis();
					confirmSwitchEditTextCounter++;
					if (!saveFormulaGreenIfPossible()) {
						return;
					}
				}
				if (formulaEditorEditTextBlue.hasChanges()) {
					confirmSwitchEditTextTimeStamp[0] = confirmSwitchEditTextTimeStamp[1];
					confirmSwitchEditTextTimeStamp[1] = System.currentTimeMillis();
					confirmSwitchEditTextCounter++;
					if (!saveFormulaBlueIfPossible()) {
						return;
					}
				}

				formulaEditorEditTextRed.endEdit();
				formulaEditorEditTextGreen.endEdit();
				formulaEditorEditTextBlue.endEdit();

				currentFormula = newFormula;
				formulaEditorEditTextRed.enterNewFormula(newFormula.getInternFormulaState());
				formulaEditorEditTextGreen.enterNewFormula(newFormula.getInternFormulaState());
				formulaEditorEditTextBlue.enterNewFormula(newFormula.getInternFormulaState());

				refreshFormulaPreviewString();

				break;
			default:
				break;
		}
	}

	public boolean saveFormulaRedIfPossible() {
		InternFormulaParser formulaToParse = formulaEditorEditTextRed.getFormulaParser();
		FormulaElement formulaParseTree = formulaToParse.parseFormula();
		int err = formulaToParse.getErrorTokenIndex();
		switch (err) {
			case PARSER_OK:
				currentFormula.setRoot(formulaParseTree);
				if (kodeyBrick != null) {
					currentFormula.refreshTextField(brickView);
				}
				formulaEditorEditTextRed.formulaSaved();
				showToast(R.string.formula_editor_changes_saved);
				return true;
			case PARSER_STACK_OVERFLOW:
				return checkReturnWithoutSaving(PARSER_STACK_OVERFLOW);
			default:
				formulaEditorEditTextRed.setParseErrorCursorAndSelection();
				return checkReturnWithoutSaving(PARSER_INPUT_SYNTAX_ERROR);
		}
	}

	public boolean saveFormulaGreenIfPossible() {
		InternFormulaParser formulaToParse = formulaEditorEditTextGreen.getFormulaParser();
		FormulaElement formulaParseTree = formulaToParse.parseFormula();
		int err = formulaToParse.getErrorTokenIndex();
		switch (err) {
			case PARSER_OK:
				currentFormula.setRoot(formulaParseTree);
				if (kodeyBrick != null) {
					currentFormula.refreshTextField(brickView);
				}
				formulaEditorEditTextGreen.formulaSaved();
				showToast(R.string.formula_editor_changes_saved);
				return true;
			case PARSER_STACK_OVERFLOW:
				return checkReturnWithoutSaving(PARSER_STACK_OVERFLOW);
			default:
				formulaEditorEditTextGreen.setParseErrorCursorAndSelection();
				return checkReturnWithoutSaving(PARSER_INPUT_SYNTAX_ERROR);
		}
	}

	public boolean saveFormulaBlueIfPossible() {
		InternFormulaParser formulaToParse = formulaEditorEditTextBlue.getFormulaParser();
		FormulaElement formulaParseTree = formulaToParse.parseFormula();
		int err = formulaToParse.getErrorTokenIndex();
		switch (err) {
			case PARSER_OK:
				currentFormula.setRoot(formulaParseTree);
				if (kodeyBrick != null) {
					currentFormula.refreshTextField(brickView);
				}
				formulaEditorEditTextBlue.formulaSaved();
				showToast(R.string.formula_editor_changes_saved);
				return true;
			case PARSER_STACK_OVERFLOW:
				return checkReturnWithoutSaving(PARSER_STACK_OVERFLOW);
			default:
				formulaEditorEditTextBlue.setParseErrorCursorAndSelection();
				return checkReturnWithoutSaving(PARSER_INPUT_SYNTAX_ERROR);
		}
	}

	private boolean checkReturnWithoutSaving(int errorType) {
		Log.i("info",
				"confirmSwitchEditTextCounter=" + confirmSwitchEditTextCounter + " "
						+ (System.currentTimeMillis() <= confirmSwitchEditTextTimeStamp[0] + TIME_WINDOW));

		if ((System.currentTimeMillis() <= confirmSwitchEditTextTimeStamp[0] + TIME_WINDOW)
				&& (confirmSwitchEditTextCounter > 1)) {
			confirmSwitchEditTextTimeStamp[0] = 0;
			confirmSwitchEditTextTimeStamp[1] = 0;
			confirmSwitchEditTextCounter = 0;
			currentFormula.setDisplayText(null);
			showToast(R.string.formula_editor_changes_discarded);
			return true;
		} else {
			switch (errorType) {
				case PARSER_INPUT_SYNTAX_ERROR:
					showToast(R.string.formula_editor_parse_fail);
					break;
				case PARSER_STACK_OVERFLOW:
					showToast(R.string.formula_editor_parse_fail_formula_too_long);
					break;
			}
			return false;
		}

	}

	/*
	 * TODO Remove Toasts from this class and replace them with something useful
	 * This is a hack more than anything else. We shouldn't use Toasts if we're going to change the message all the time
	 */
	private void showToast(int resourceId) {
		if (toast == null || toast.getView().getWindowVisibility() != View.VISIBLE) {
			toast = Toast.makeText(getActivity().getApplicationContext(), resourceId, Toast.LENGTH_SHORT);
		} else {
			toast.setText(resourceId);
		}
		toast.show();
	}

	@Override
	public boolean onKey(View view, int keyCode, KeyEvent event) {
		Log.i("info", "onKey() in FE-Fragment! keyCode: " + keyCode);
		switch (keyCode) {
			case KeyEvent.KEYCODE_BACK:
				if (formulaEditorEditTextRed.hasChanges()) {
					AlertDialog.Builder builder = new CustomAlertDialogBuilder(getActivity());
					builder.setTitle(R.string.formula_editor_discard_changes_dialog_title)
							.setMessage(R.string.formula_editor_discard_changes_dialog_message)
							.setNegativeButton(R.string.no, new OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog, int which) {

									showToast(R.string.formula_editor_changes_discarded);
									currentFormula.setDisplayText(null);
									onUserDismiss();
								}
							}).setPositiveButton(R.string.yes, new OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog, int which) {
									if (saveFormulaRedIfPossible() && saveFormulaGreenIfPossible() && saveFormulaBlueIfPossible()) {
										onUserDismiss();
									}
								}
							}).create().show();

				} else {
					onUserDismiss();
				}
				if (formulaEditorEditTextGreen.hasChanges()) {
					AlertDialog.Builder builder = new CustomAlertDialogBuilder(getActivity());
					builder.setTitle(R.string.formula_editor_discard_changes_dialog_title)
							.setMessage(R.string.formula_editor_discard_changes_dialog_message)
							.setNegativeButton(R.string.no, new OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog, int which) {

									showToast(R.string.formula_editor_changes_discarded);
									currentFormula.setDisplayText(null);
									onUserDismiss();
								}
							}).setPositiveButton(R.string.yes, new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (saveFormulaGreenIfPossible()) {
								onUserDismiss();
							}
						}
					}).create().show();

				} else {
					onUserDismiss();
				}
				if (formulaEditorEditTextBlue.hasChanges()) {
					AlertDialog.Builder builder = new CustomAlertDialogBuilder(getActivity());
					builder.setTitle(R.string.formula_editor_discard_changes_dialog_title)
							.setMessage(R.string.formula_editor_discard_changes_dialog_message)
							.setNegativeButton(R.string.no, new OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog, int which) {

									showToast(R.string.formula_editor_changes_discarded);
									currentFormula.setDisplayText(null);
									onUserDismiss();
								}
							}).setPositiveButton(R.string.yes, new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (saveFormulaBlueIfPossible()) {
								onUserDismiss();
							}
						}
					}).create().show();

				} else {
					onUserDismiss();
				}

				return true;
		}
		return false;
	}

	private void endFormulaEditor() {
		if (formulaEditorEditTextRed.hasChanges()) {
			if (saveFormulaRedIfPossible()) {
				onUserDismiss();
			}
		} else {
			onUserDismiss();
		}
		if (formulaEditorEditTextGreen.hasChanges()) {
			if (saveFormulaGreenIfPossible()) {
				onUserDismiss();
			}
		} else {
			onUserDismiss();
		}
		if (formulaEditorEditTextBlue.hasChanges()) {
			if (saveFormulaBlueIfPossible()) {
				onUserDismiss();
			}
		} else {
			onUserDismiss();
		}
	}

	public void refreshFormulaPreviewString() {
		refreshFormulaPreviewString(formulaEditorEditTextRed.getStringFromInternFormula());
		refreshFormulaPreviewString(formulaEditorEditTextGreen.getStringFromInternFormula());
		refreshFormulaPreviewString(formulaEditorEditTextBlue.getStringFromInternFormula());
	}

	public void refreshFormulaPreviewString(String newString) {
		currentFormula.setDisplayText(newString);

		updateBrickView();

		currentFormula.refreshTextField(brickView, newString);

		int orientation = getResources().getConfiguration().orientation;
		currentFormula.highlightTextField(brickView, orientation);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onGlobalLayout() {
		fragmentView.getViewTreeObserver().removeGlobalOnLayoutListener(this);

		Rect brickRect = new Rect();
		Rect keyboardRec = new Rect();
		kodeyBrick.getGlobalVisibleRect(brickRect);

		//ToDo: Check this
		formulaEditorEditTextRed.setMaxHeight(keyboardRec.top - brickRect.bottom);
		formulaEditorEditTextGreen.setMaxHeight(keyboardRec.top - brickRect.bottom);
		formulaEditorEditTextBlue.setMaxHeight(keyboardRec.top - brickRect.bottom);

	}

	public void addResourceToActiveFormula(int resource) {
		formulaEditorEditTextRed.handleKeyEvent(resource, "");
		formulaEditorEditTextGreen.handleKeyEvent(resource, "");
		formulaEditorEditTextBlue.handleKeyEvent(resource, "");
	}

	public void addUserVariableToActiveFormula(String userVariableName) {
		formulaEditorEditTextRed.handleKeyEvent(0, userVariableName);
		formulaEditorEditTextGreen.handleKeyEvent(0, userVariableName);
		formulaEditorEditTextBlue.handleKeyEvent(0, userVariableName);
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

		IntentFilter filterVariableDeleted = new IntentFilter(ScriptActivity.ACTION_VARIABLE_DELETED);
		getActivity().registerReceiver(variableDeletedReceiver, filterVariableDeleted);
		BottomBar.hideBottomBar(getSherlockActivity());
	}
}
