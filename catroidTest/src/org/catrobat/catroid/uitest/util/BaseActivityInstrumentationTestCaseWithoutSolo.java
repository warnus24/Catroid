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
package org.catrobat.catroid.uitest.util;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.stage.StageListener;
import org.catrobat.catroid.ui.MainMenuActivity;

import java.io.IOException;

/**
 * This class is needed for special initializations in child classes.
 * Methods like setActivityIntent(Intent) in child classes supposes that
 * setUp() of ActivityInstrumentationTestCase2 was called. But it must be
 * called after getActivity() of ActivityInstrumentationTestCase2 otherwise
 * it has no effect.
 *
 * So BaseActivityInstrumentationTestCase will not work for such cases,
 * because getActivity() is called in the setUp() method there.
 */
public abstract class BaseActivityInstrumentationTestCaseWithoutSolo<T extends Activity> extends
		ActivityInstrumentationTestCase2<T> {

	private static final String TAG = "BaseActivityInstrumentationTestCase";
	private Class clazz;
	private SystemAnimations systemAnimations;

	public BaseActivityInstrumentationTestCaseWithoutSolo(Class<T> clazz) {
		super(clazz);
		this.clazz = clazz;
	}

	@Override
	protected void setUp() throws Exception {
		Log.v(TAG, "setUp");
		super.setUp();
		systemAnimations = new SystemAnimations(getInstrumentation().getContext());
		systemAnimations.disableAll();
		UiTestUtils.clearAllUtilTestProjects();
		if (clazz.getSimpleName().equalsIgnoreCase(MainMenuActivity.class.getSimpleName())) {
			UiTestUtils.createEmptyProject();
		}

		Reflection.setPrivateField(StageListener.class, "checkIfAutomaticScreenshotShouldBeTaken", false);
		Log.v(TAG, "setUp end");
	}

	@Override
	protected void tearDown() throws Exception {
		Log.v(TAG, "tearDown - remove Projectname from SharedPreferences");
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
		SharedPreferences.Editor edit = preferences.edit();
		edit.remove(Constants.PREF_PROJECTNAME_KEY);
		edit.commit();

		try {
			Project currentProject = ProjectManager.getInstance().getCurrentProject();
			if (currentProject != null) {
				ProjectManager.getInstance().deleteProject(currentProject.getName(), null);
			}
		} catch (IOException e) {
			Log.d(TAG, "deleteCurrentProject exception", e);
		}

		UiTestUtils.clearAllUtilTestProjects();

		super.tearDown();
		systemAnimations.enableAll();
		Log.v(TAG, "tearDown end");
	}

}
