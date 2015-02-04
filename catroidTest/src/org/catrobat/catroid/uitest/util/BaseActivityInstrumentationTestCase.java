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

import com.robotium.solo.Solo;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.stage.StageListener;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.utils.UtilFile;
import org.catrobat.catroid.utils.UtilZip;
import org.catrobat.catroid.utils.Utils;

import java.io.File;
import java.io.IOException;

public abstract class BaseActivityInstrumentationTestCase<T extends Activity> extends
		ActivityInstrumentationTestCase2<T> {
	protected Solo solo;

	private static final String TAG = "BaseActivityInstrumentationTestCase";
	private Class clazz;
	private SystemAnimations systemAnimations;
	private static final String ZIPFILE_NAME = "testzip";

	public BaseActivityInstrumentationTestCase(Class<T> clazz) {
		super(clazz);
		this.clazz = clazz;
	}

	private boolean unzip;

	@Override
	protected void setUp() throws Exception {
		Log.v(TAG, "setUp");
		super.setUp();

		systemAnimations = new SystemAnimations(getInstrumentation().getContext());
		systemAnimations.disableAll();

		unzip = false;
		saveProjectsToZip();

		//UiTestUtils.clearAllUtilTestProjects();
		if (clazz.getSimpleName().equalsIgnoreCase(MainMenuActivity.class.getSimpleName())) {
			UiTestUtils.createEmptyProject();
		}
		solo = new Solo(getInstrumentation(), getActivity());
		Reflection.setPrivateField(StageListener.class, "checkIfAutomaticScreenshotShouldBeTaken", false);

		solo.unlockScreen();
		Log.v(TAG, "setUp end");
	}

	@Override
	protected void tearDown() throws Exception {

		File rootDirectory = new File(Constants.DEFAULT_ROOT);

		Log.v(TAG, "tearDown");
		Log.v(TAG, "remove Projectname from SharedPreferences");
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
		SharedPreferences.Editor edit = preferences.edit();
		edit.remove(Constants.PREF_PROJECTNAME_KEY);
		edit.commit();

		solo.finishOpenedActivities();

//		try {
//			Project currentProject = ProjectManager.getInstance().getCurrentProject();
//			if (currentProject != null) {
//				ProjectManager.getInstance().deleteProject(currentProject.getName(), null);
//			}
//		} catch (IOException e) {
//			Log.d(TAG, "deleteCurrentProject exception", e);
//		}
//
//		UiTestUtils.clearAllUtilTestProjects();

		systemAnimations.enableAll();
		solo = null;
		Log.i(TAG, "tearDown end 1");

		try {
			Log.d(TAG, UtilFile.getProjectNames(rootDirectory).toString());
		} catch (NullPointerException e) {
			Log.d(TAG, "List is empty");
		}

		for (String projectName : UtilFile.getProjectNames(rootDirectory)) {
			Log.d(TAG, projectName + "will be deleted");
			ProjectManager.getInstance().deleteProject(projectName, this.getActivity());
		}

		Log.i(TAG, "tearDown end 2");

		unzipProjects();

		Log.i(TAG, "tearDown end 3");
		super.tearDown();

		Log.i(TAG, "tearDown end 4");
	}

	@Override
	public void runBare() throws Throwable {
		try {
			setUp();
			runTest();
		} catch(Exception e) {
			Log.d(TAG, "###########################");
			Log.d(TAG, Log.getStackTraceString(e));
		} finally {
			try {
				tearDown();
			} catch (Exception e) {
				Log.d(TAG, "###########################");
				Log.d(TAG, Log.getStackTraceString(e));
			}
		}
	}

	public void saveProjectsToZip() {
		File rootDirectory = new File(Constants.DEFAULT_ROOT);

		rootDirectory.mkdirs();

		String[] paths = rootDirectory.list();

		if (paths == null) {
			fail("could not determine catroid directory");
		}
		else if (paths.length > 0) {
			for (int i = 0; i < paths.length; i++) {
				paths[i] = Utils.buildPath(rootDirectory.getAbsolutePath(), paths[i]);
			}
			try {
				String zipFileString = Utils.buildPath(Constants.DEFAULT_ROOT, ZIPFILE_NAME);
				Log.d(TAG, "i am the zipfile: " + zipFileString);
				File zipFile = new File(zipFileString);
				if (zipFile.exists()) {
					zipFile.delete();
				}
				zipFile.getParentFile().mkdirs();
				zipFile.createNewFile();
				if (!UtilZip.writeToZipFile(paths, zipFileString)) {
					zipFile.delete();
					throw new IOException("asdf");
				}
			} catch (IOException e) {
				fail("IOException while zipping projects");
			}

			for (int i = 0; i < paths.length; i++) {
				Log.d(TAG, "Path to delete: " + paths[i]);
				StorageHandler.getInstance().deleteFile(paths[i]);
			}
			unzip = true;
		}
	}

	public void unzipProjects() {
		if (unzip) {
			String zipFileString = Utils.buildPath(Constants.DEFAULT_ROOT, ZIPFILE_NAME);
			Log.d(TAG, "i am the unzipfile: " + zipFileString);
			File zipFile = new File(zipFileString);
			UtilZip.unZipFile(zipFileString, Constants.DEFAULT_ROOT);
			zipFile.delete();
		}
	}
}
