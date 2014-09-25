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

package org.catrobat.catroid.livewallpaper.postprocessing;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by White on 25.09.2014.
 */
public class DefaultPostProcessingEffectAttributeContainers
{
	private static Map<PostProcessingEffectsEnum,PostProcessingEffectAttributContainer> map = new HashMap<PostProcessingEffectsEnum,PostProcessingEffectAttributContainer>();
	private static Map<PostProcessingEffectsEnum,PostProcessingEffectAttributContainer> effectAttributes = Collections.synchronizedMap(map);

	public DefaultPostProcessingEffectAttributeContainers()
	{
		effectAttributes.put(PostProcessingEffectsEnum.BLOOM, new BloomAttributeContainer());
		effectAttributes.put(PostProcessingEffectsEnum.CURVATURE, new CurvatureAttributeContainer());
		effectAttributes.put(PostProcessingEffectsEnum.CRTMONITOR, new CrtMonitorAttributeContainer());
		effectAttributes.put(PostProcessingEffectsEnum.VIGNETTE, new VignetteAttributeContainer());
	}

	public PostProcessingEffectAttributContainer getAttributes(PostProcessingEffectsEnum type) {
		return effectAttributes.get(type);
	}
}
