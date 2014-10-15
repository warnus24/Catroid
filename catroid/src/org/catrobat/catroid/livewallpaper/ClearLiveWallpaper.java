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

import android.app.WallpaperManager;
import android.content.Intent;
import android.service.wallpaper.WallpaperService;

import org.catrobat.catroid.common.BroadcastSequenceMap;
import org.catrobat.catroid.common.BroadcastWaitSequenceMap;

/**
 * Created by White on 09.10.2014.
 */
public class ClearLiveWallpaper {

	public static void clearLWP(){
		if(LiveWallpaper.getInstance()!= null) {
			BroadcastSequenceMap.clear();
			BroadcastWaitSequenceMap.clear();
			BroadcastWaitSequenceMap.clearCurrentBroadcastEvent();

			LiveWallpaper.getInstance().resetWallpaper();
			LiveWallpaper.getInstance().pauseAndFinish();
			LiveWallpaper.getInstance().getLocalStageListener().pauseForLiveWallpaperToPocketCodeSwitch();
			LiveWallpaper.getInstance().onDeepPauseApplication();
			LiveWallpaper.getInstance().onDestroy();
			Intent intent = new Intent(LiveWallpaper.getInstance().getContext(), LiveWallpaper.class);
			LiveWallpaper.getInstance().getContext().stopService(intent);

			if(LiveWallpaper.getInstance()!= null){
				LiveWallpaper.getInstance().stopSelf();
				LiveWallpaper.getInstance().finalize();
			}
		}
	}
}