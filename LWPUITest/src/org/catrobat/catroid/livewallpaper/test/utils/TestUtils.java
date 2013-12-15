package org.catrobat.catroid.livewallpaper.test.utils;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.FileChecksumContainer;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.io.StorageHandler;

public class TestUtils {

	public static Project createAndSetEmptyProject(String projectName){
		Project project = new Project(null, projectName);
		Sprite firstSprite = new Sprite("cat");
		Script testScript = new StartScript(firstSprite);

		firstSprite.addScript(testScript);
		project.addSprite(firstSprite);

		ProjectManager projectManager = ProjectManager.getInstance(); 
		projectManager.setFileChecksumContainer(new FileChecksumContainer());
		projectManager.setProject(project);
		projectManager.setCurrentSprite(firstSprite);
		projectManager.setCurrentScript(testScript);
		StorageHandler.getInstance().saveProject(project);
		
		return project; 
		
	}
	
	
	public static Project createEmptyProject(String projectName) {
		Project project = new Project(null, projectName);
		Sprite firstSprite = new Sprite("cat");
		Script testScript = new StartScript(firstSprite);

		firstSprite.addScript(testScript);
		project.addSprite(firstSprite);
		StorageHandler.getInstance().saveProject(project);
		
		return project;
	}
}
