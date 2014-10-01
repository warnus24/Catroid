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

package org.catrobat.catroid;

import android.util.Log;

import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.livewallpaper.LiveWallpaper;

/**
 * Created by Tom on 30.09.2014.
 */
public final class ProjectHandler {

	private String TAG = getClass().getSimpleName();
	private Project liveWallpaperProject;
	private Project pocketCodeProject;

	private static ProjectHandler INSTANCE = new ProjectHandler();

	private ProjectHandler(){

	}

	public static ProjectHandler getInstance() {
		return INSTANCE;
	}

	public void changeToPocketCode(){
		Log.d(TAG,"changing to Pocketcode!!!");
		ProjectManager.getInstance().setProject(pocketCodeProject);
	}

	public void changeToLiveWallpaper(){
		Log.d(TAG,"changing to LiveWallpaper!!!");
		ProjectManager.getInstance().setProject(liveWallpaperProject);
	}

	public Project getLiveWallpaperProject() {
		return liveWallpaperProject;
	}

	public void setLiveWallpaperProject(Project liveWallpaperProject) {
		Log.d(TAG,"set LiveWallpaper Project!!!");
		this.liveWallpaperProject = liveWallpaperProject;
	}

	public Project getPocketCodeProject() {
		return pocketCodeProject;
	}

	public void setPocketCodeProject(Project pocketCodeProject) {
		Log.d(TAG,"set Pocketcode Project!!!");
		if (LiveWallpaper.getInstance() != null)
			LiveWallpaper.getInstance().setResumeFromPocketCode(true);
		this.pocketCodeProject = pocketCodeProject;
	}


}
