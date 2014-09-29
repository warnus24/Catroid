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

package org.catrobat.catroid.livewallpaper;

import android.os.AsyncTask;
import android.util.Log;

import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.utils.UtilFile;
import org.catrobat.catroid.utils.Utils;

import java.io.File;
import java.io.IOException;

/**
 * Created by Tom on 29.09.2014.
 */
public class TempHandler extends AsyncTask<String, Long, Boolean> {
	String oldProjectName;
	public TempHandler() {
	}

	@Override
	protected Boolean doInBackground(String... projectNameArray) {
		oldProjectName = projectNameArray[0];

		try {
			File oldProjectRootDirectory = new File(Utils.buildProjectPath(oldProjectName));
			File newProjectRootDirectory = new File(Utils.buildTempPath(oldProjectName));

			copyDirectory(newProjectRootDirectory, oldProjectRootDirectory);

			Project copiedProject = StorageHandler.getInstance().loadTempProject(oldProjectName);
			copiedProject.setName(oldProjectName);
			StorageHandler.getInstance().saveProject(copiedProject);

		} catch (IOException exception) {
			UtilFile.deleteDirectory(new File(Utils.buildProjectPath(oldProjectName)));
			Log.e("CATROID", "Error while copying project, destroy newly created directories.", exception);
			return false;
		}
		return true;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);

	}

	private void copyDirectory(File destinationFile, File sourceFile) throws IOException {
		if (sourceFile.isDirectory()) {

			destinationFile.mkdirs();
			for (String subDirectoryName : sourceFile.list()) {
				copyDirectory(new File(destinationFile, subDirectoryName), new File(sourceFile, subDirectoryName));
			}
		} else {
			UtilFile.copyFile(destinationFile, sourceFile);
		}
	}
}
