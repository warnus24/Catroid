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

package org.catrobat.catroid.utils;

import android.graphics.Color;
import android.util.Log;

import org.catrobat.catroid.livewallpaper.postprocessing.DefaultPostProcessingEffectAttributeContainers;
import org.catrobat.catroid.livewallpaper.postprocessing.PostProcessingEffectAttributContainer;
import org.catrobat.catroid.livewallpaper.postprocessing.PostProcessingEffectsEnum;

/**
 * Created by White on 24.09.2014.
 */
public class PostProcessingUtil {
	private static DefaultPostProcessingEffectAttributeContainers attributeContainers = new DefaultPostProcessingEffectAttributeContainers();
	private static boolean bloomInLayoutIsEnabled = false;

	public static com.badlogic.gdx.graphics.Color convertIntColorToColor(int c)
	{
		float a = Color.alpha(c);
		float r = Color.red(c);
		float g = Color.green(c);
		float b = Color.blue(c);
		Log.d("LWP", "Color(" + r + ", " + g + ", " + b + ", " + a + ")");
		return new com.badlogic.gdx.graphics.Color(r / 255, g / 255, b / 255, a / 255);
	}

	public static PostProcessingEffectAttributContainer getDefaultPostProcessingEffectAttributeContainers(PostProcessingEffectsEnum type){
		return attributeContainers.getAttributes(type);
	}

	public static void setBloomPostProcessingEffectLayoutEnabled(boolean isEnabled) {
		bloomInLayoutIsEnabled = isEnabled;
	}

	public static boolean isBloomPostProcessinginLayoutEnabled() {
		return bloomInLayoutIsEnabled;
	}

}
