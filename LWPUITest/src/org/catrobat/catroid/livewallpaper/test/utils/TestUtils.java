package org.catrobat.catroid.livewallpaper.test.utils;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.io.StorageHandler;

import android.content.Context;

public class TestUtils {

	
	public static Project createEmptyProjectWithoutSettingIt(Context context, String projectName) {
		if (StorageHandler.getInstance().projectExists(projectName)) {
			throw new IllegalArgumentException("Project with name '" + projectName + "' already exists!");
		}
		Project emptyProject = new Project(context, projectName);
		emptyProject.setDeviceData(context);
		StorageHandler.getInstance().saveProject(emptyProject);
		ProjectManager.getInstance().setProject(emptyProject);

		return emptyProject;
	}
}
