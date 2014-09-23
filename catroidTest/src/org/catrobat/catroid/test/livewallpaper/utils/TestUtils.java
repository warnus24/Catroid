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

package org.catrobat.catroid.test.livewallpaper.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.io.StorageHandler;

public class TestUtils {

	
	public static Project createEmptyProjectWithoutSettingIt(Context context, String projectName) {
		if (StorageHandler.getInstance().projectExists(projectName)) {
			throw new IllegalArgumentException("Project with name '" + projectName + "' already exists!");
		}
		Project emptyProject = new Project(context, projectName);
		emptyProject.setDeviceData(context);
		StorageHandler.getInstance().saveProject(emptyProject);
		//ProjectManager.getInstance().setProject(emptyProject);

		return emptyProject;
	}
	
	public static void restartActivity(Activity myActivity)
	{
		Intent myIntent = new Intent(myActivity, myActivity.getClass()); 
		myActivity.finish();
		myIntent.setAction(Intent.ACTION_MAIN); 
		myIntent.addCategory(Intent.CATEGORY_LAUNCHER); 
		myIntent.addFlags(Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY); 
		myActivity.startActivity(myIntent); 	
	}
	
}
