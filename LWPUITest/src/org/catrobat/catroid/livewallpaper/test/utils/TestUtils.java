package org.catrobat.catroid.livewallpaper.test.utils;

import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;

public class TestUtils {

	
	public static Project createEmptyProject(String projectName) {
		Project project = new Project(null, projectName);
		Sprite firstSprite = new Sprite("cat");
		Script testScript = new StartScript(firstSprite);

		firstSprite.addScript(testScript);
		project.addSprite(firstSprite);
		
		return project;
	}
}
