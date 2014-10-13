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

package org.catrobat.catroid.livewallpaper.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Created by White on 09.10.2014.
 */
public class LiveWallpaperAlertDialogBuilder {
	private Activity activity;

	public LiveWallpaperAlertDialogBuilder(Activity activity){
		this.activity = activity;
	}



	public void createLWPActiveAlert()  {
		AlertDialog show = new AlertDialog.Builder(activity)
				.setTitle("LiveWallpaper Notification")
				.setMessage("Ey, du kommst hier net rein")
				.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						synchronized (activity){
							activity.notify();
						}
						dialog.cancel();
						goBack(activity);
					}
				})
				/*
				.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// do nothing
					}
				})*/
				.setIcon(android.R.drawable.ic_dialog_alert)
				.show();
		show.setCancelable(false);
	}

	public void goBack(Activity activity){
		activity.onBackPressed();
	}
}
