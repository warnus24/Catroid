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

package org.catrobat.catroid.uitest.livewallpaper;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.Reflection;
import org.catrobat.catroid.uitest.util.UiTestUtils;
import org.catrobat.catroid.utils.UtilFile;
import org.catrobat.catroid.utils.UtilZip;
import org.catrobat.catroid.utils.Utils;

import java.io.File;
import java.io.IOException;

/**
 * Created by Tom on 19.08.2014.
 */
public class SelectProgramActivityTest extends BaseActivityInstrumentationTestCase<MainMenuActivity> {
	private static final String KEY_SHOW_DETAILS = "showDetailsMyProjects";
	private File lookFile;
	private boolean unzip;
	private File renameDirectory = null;
	private boolean deleteCacheProjects = false;
	private int numberOfCacheProjects = 27;
	private String cacheProjectName = "cachetestProject";
	private static final String ZIPFILE_NAME = "testzip";

	public SelectProgramActivityTest() {
		super(MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		UiTestUtils.prepareStageForTest();
		lookFile = UiTestUtils.setUpLookFile(solo);

		// disable show details when activated
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
		if (sharedPreferences.getBoolean(KEY_SHOW_DETAILS, true)) {
			sharedPreferences.edit().putBoolean(KEY_SHOW_DETAILS, false).commit();
		}

		unzip = false;
	}

	@Override
	public void tearDown() throws Exception {
		Reflection.setPrivateField(ProjectManager.class, ProjectManager.getInstance(), "asynchronTask", true);

		UtilFile.deleteDirectory(new File(Utils.buildProjectPath(UiTestUtils.NORMAL_AND_SPECIAL_CHAR_PROJECT_NAME)));
		UtilFile.deleteDirectory(new File(Utils.buildProjectPath(UiTestUtils.NORMAL_AND_SPECIAL_CHAR_PROJECT_NAME2)));
		UtilFile.deleteDirectory(new File(Utils.buildProjectPath(UiTestUtils.JUST_SPECIAL_CHAR_PROJECT_NAME)));
		UtilFile.deleteDirectory(new File(Utils.buildProjectPath(UiTestUtils.JUST_ONE_DOT_PROJECT_NAME)));
		UtilFile.deleteDirectory(new File(Utils.buildProjectPath(UiTestUtils.JUST_TWO_DOTS_PROJECT_NAME)));
		lookFile.delete();

		if (renameDirectory != null && renameDirectory.isDirectory()) {
			UtilFile.deleteDirectory(renameDirectory);
			renameDirectory = null;
		}
		if (deleteCacheProjects) {
			for (int i = 0; i < numberOfCacheProjects; i++) {
				File directory = new File(Utils.buildProjectPath(cacheProjectName + i));
				UtilFile.deleteDirectory(directory);
			}
			deleteCacheProjects = false;
		}

		// normally super.teardown should be called last
		// but tests crashed with Nullpointer
		super.tearDown();
		ProjectManager.getInstance().deleteCurrentProject();
		if (unzip) {
			unzipProjects();
		}
	}

	public void saveProjectsToZip() {
		File directory;
		File rootDirectory = new File(Constants.DEFAULT_ROOT);
		String[] paths = rootDirectory.list();

		if (paths == null) {
			fail("could not determine catroid directory");
		}

		for (int i = 0; i < paths.length; i++) {
			paths[i] = Utils.buildPath(rootDirectory.getAbsolutePath(), paths[i]);
		}
		try {
			String zipFileString = Utils.buildPath(Constants.DEFAULT_ROOT, ZIPFILE_NAME);
			File zipFile = new File(zipFileString);
			if (zipFile.exists()) {
				zipFile.delete();
			}
			zipFile.getParentFile().mkdirs();
			zipFile.createNewFile();
			if (!UtilZip.writeToZipFile(paths, zipFileString)) {
				zipFile.delete();
			}
		} catch (IOException e) {
			fail("IOException while zipping projects");
		}

		for (String projectName : UtilFile.getProjectNames(rootDirectory)) {
			directory = new File(Constants.DEFAULT_ROOT + "/" + projectName);
			if (directory.exists()) {
				UtilFile.deleteDirectory(directory);
			}
		}
	}

	public void unzipProjects() {
		String zipFileString = Utils.buildPath(Constants.DEFAULT_ROOT, ZIPFILE_NAME);
		File zipFile = new File(zipFileString);
		UtilZip.unZipFile(zipFileString, Constants.DEFAULT_ROOT);
		zipFile.delete();
	}

	public void testDeleteStandardProject() {

	}
}
