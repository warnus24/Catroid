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
package org.catrobat.catroid.livewallpaper.ui;

import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.livewallpaper.ColorPickerDialog;

public class SelectProgramActivity extends BaseActivity implements ColorPickerDialog.OnColorChangedListener {

	public static final String ACTION_PROJECT_LIST_INIT = "org.catroba Fragment.PROJECT_LIST_INIT";

	private SelectProgramFragment selectProgramFragment;
	private int tintingColor = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_projects_lwp);
		setUpActionBar();

		selectProgramFragment = (SelectProgramFragment) getSupportFragmentManager().findFragmentById(
				R.id.fragment_projects_list_lwp);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus) {
			sendBroadcast(new Intent(ACTION_PROJECT_LIST_INIT));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.menu_selectprogram, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.delete: {
				selectProgramFragment.startDeleteActionMode();
				break;
			}
			case R.id.about: {
				AboutPocketCodeDialog aboutPocketCodeDialog = new AboutPocketCodeDialog(this);
				aboutPocketCodeDialog.show();
				break;
			}
			case R.id.lwp_new: {
				Intent pocketCodeIntent = new Intent("android.intent.action");
				pocketCodeIntent.setComponent(new ComponentName(Constants.POCKET_CODE_PACKAGE_NAME,
						Constants.POCKET_CODE_INTENT_ACTIVITY_NAME));
				//				//boolean isInstalled = Utils.checkIfPocketCodeInstalled(pocketCodeIntent, this);
				//				if (isInstalled) {
				//
				//					pocketCodeIntent.addCategory("android.intent.category.LAUNCHER");
				//					startActivity(pocketCodeIntent);
				//				} else {
				//
				//					displayDownloadPocketCodeDialog();
				//				}

				break;
			}
			case R.id.lwp_tinting: {
				tinting();
				break;
			}
			case R.id.lwp_disable_tinting: {
				disableTinting();
				break;
			}
		}
		return super.onOptionsItemSelected(item);
	}

	public void disableTinting() {
		selectProgramFragment.disableTinting();
	}

	public void tinting() {
		Paint mPaint = new Paint();
		ColorPickerDialog dialog = new ColorPickerDialog(SelectProgramActivity.this, SelectProgramActivity.this,
				mPaint.getColor());
		dialog.show();
	}

	private void setUpActionBar() {
		final ActionBar actionBar = getSupportActionBar();
		actionBar.setTitle(R.string.lwp_select_program);
		actionBar.setDisplayHomeAsUpEnabled(false);
		actionBar.setHomeButtonEnabled(false);
	}

	public SelectProgramFragment getSelectProgramFragment() {
		return selectProgramFragment;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.catrobat.catroid.livewallpaper.ColorPickerDialog.OnColorChangedListener#colorChanged(int)
	 */
	@Override
	public void colorChanged(int color) {
		selectProgramFragment.tinting(color);
	}

	//	private void displayDownloadPocketCodeDialog() {
	//
	//		AlertDialog.Builder builder = new AlertDialog.Builder(this);
	//		builder.setMessage(R.string.pocket_code_not_installed).setCancelable(false)
	//				.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
	//					@Override
	//					public void onClick(DialogInterface dialog, int id) {
	//
	//						Intent downloadPocketPaintIntent = new Intent(Intent.ACTION_VIEW, Uri
	//								.parse(Constants.POCKET_CODE_DOWNLOAD_LINK));
	//						startActivity(downloadPocketPaintIntent);
	//					}
	//				}).setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
	//					@Override
	//					public void onClick(DialogInterface dialog, int id) {
	//						dialog.cancel();
	//					}
	//				});
	//		AlertDialog alert = builder.create();
	//		alert.show();
	//	}

}
