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
package org.catrobat.catroid.robome;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import org.catrobat.catroid.stage.PreStageActivity;

public class RoboMeInitializer {
	private static final String TAG = RoboMeInitializer.class.getSimpleName();

	public static final String INIT_ROBOME_STRING_EXTRA = "STRING_EXTRA_INIT_ROBOME";
	private PreStageActivity prestageStageActivity;
	private Intent returnToActivityIntent = null;

	public RoboMeInitializer(PreStageActivity prestageStageActivity, Intent returnToActivityIntent) {
		this.prestageStageActivity = prestageStageActivity;
		this.returnToActivityIntent = returnToActivityIntent;
	}

	public void initialize() {
		this.returnToActivityIntent.putExtra(INIT_ROBOME_STRING_EXTRA, true);
	}

	public static void addRoboMeSupportExtraToNewIntentIfPresentInOldIntent(Intent oldIntent, Intent newIntent) {
		if (newIntent == null || oldIntent == null) {
			return;
		}

		Boolean isRoboMeRequired = oldIntent.getBooleanExtra(INIT_ROBOME_STRING_EXTRA, false);
		Log.d(TAG, "Extra STRING_EXTRA_INIT_ROBOME=" + isRoboMeRequired.toString());
		newIntent.putExtra(INIT_ROBOME_STRING_EXTRA, isRoboMeRequired);
	}

	public void onPrestageActivityResume() {
	}

	public void onPrestageActivityPause() {
	}

	public void onPrestageActivityDestroy() {
	}
}
