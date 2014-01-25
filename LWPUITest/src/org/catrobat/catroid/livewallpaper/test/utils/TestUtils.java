package org.catrobat.catroid.livewallpaper.test.utils;

import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.io.StorageHandler;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;

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
