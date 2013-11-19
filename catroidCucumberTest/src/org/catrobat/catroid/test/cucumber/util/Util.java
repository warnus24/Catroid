/**
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2013 The Catrobat Team
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
package org.catrobat.catroid.test.cucumber.util;

import static junit.framework.Assert.fail;

import android.content.Context;

import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.test.cucumber.Cucumber;
import org.catrobat.catroid.utils.UtilFile;

import java.io.File;
import java.io.IOException;

public final class Util {
	private Util() {
	}

	public static Sprite addNewObjectWithLook(Context context, Project project, String spriteName, int resourceId) {
		Sprite sprite = new Sprite(spriteName);
		project.addSprite(sprite);
		File file = createObjectImage(context, spriteName, resourceId);
		LookData lookData = newLookData(spriteName, file);
		sprite.getLookDataList().add(lookData);
		return sprite;
	}

	public static Sprite findSprite(Project project, String name) {
		for (Sprite sprite : project.getSpriteList()) {
			if (sprite.getName().equals(name)) {
				return sprite;
			}
		}
		fail(String.format("Sprite not found '%s'", name));
		return null;
	}

	public static LookData newLookData(String name, File file) {
		LookData lookData = new LookData();
		lookData.setLookName(name);
		lookData.setLookFilename(file.getName());
		return lookData;
	}

	public static SoundInfo newSoundInfo(String name) {
		SoundInfo soundInfo = new SoundInfo();
		soundInfo.setSoundFileName(name);
		return soundInfo;
	}

	public static File createObjectImage(Context context, String outputFilename, int resourceId) {
		Project project = (Project) Cucumber.get(Cucumber.KEY_PROJECT);

		File result = null;
		try {
			result = UtilFile.copyImageFromResourceIntoProject(project.getName(), outputFilename, resourceId, context,
					true, 1.0);
		} catch (IOException exception) {
			exception.printStackTrace();
		}

		return result;
	}

	public static File createObjectSound(Context context, String outputFilename, int resourceId) {
		Project project = (Project) Cucumber.get(Cucumber.KEY_PROJECT);

		File result = null;
		try {
			result = UtilFile.copySoundFromResourceIntoProject(project.getName(), outputFilename, resourceId, context,
					true);
		} catch (IOException exception) {
			exception.printStackTrace();
		}

		return result;
	}
}
