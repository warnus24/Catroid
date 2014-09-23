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

import com.badlogic.gdx.Gdx;
import com.bitfire.postprocessing.PostProcessorEffect;
import com.bitfire.postprocessing.effects.Bloom;
import com.bitfire.postprocessing.effects.CrtMonitor;
import com.bitfire.postprocessing.effects.Curvature;
import com.bitfire.postprocessing.effects.Vignette;
import com.bitfire.postprocessing.effects.Zoomer;
import com.bitfire.postprocessing.filters.CrtScreen;
import com.bitfire.postprocessing.filters.RadialBlur;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by White on 30.08.2014.
 */
public class EffectsContainer
{
	private Bloom bloom;
	private Curvature curvature;
	private CrtMonitor crtMonitor;
	private Vignette vignette;
	private Zoomer zoomer;
	private PostProcessingEffectsEnum postProcessingEnum = PostProcessingEffectsEnum.NONE;
	private Map<PostProcessingEffectsEnum, PostProcessorEffect> effectsMap = new ConcurrentHashMap<PostProcessingEffectsEnum, PostProcessorEffect>();

	public EffectsContainer()
	{
		bloom = new Bloom((int) (Gdx.graphics.getWidth() * 0.25f), (int) (Gdx.graphics.getHeight() * 0.25f));
		vignette = new Vignette((int) (Gdx.graphics.getWidth() * 0.25f), (int) (Gdx.graphics.getHeight() * 0.25f), false);
		zoomer = new Zoomer((int) (Gdx.graphics.getWidth() * 0.25f), (int) (Gdx.graphics.getHeight() * 0.25f), RadialBlur.Quality.Low);

		effectsMap.put(PostProcessingEffectsEnum.BLOOM, bloom);
		effectsMap.put(PostProcessingEffectsEnum.VIGNETTE, vignette);
		effectsMap.put(PostProcessingEffectsEnum.ZOOMER, zoomer);


		int version_code = Integer.valueOf(android.os.Build.VERSION.SDK);
		if(version_code >= 19){
			int effectsForCrt = CrtScreen.Effect.TweakContrast.v | CrtScreen.Effect.PhosphorVibrance.v | CrtScreen.Effect.Scanlines.v | CrtScreen.Effect.Tint.v;
			crtMonitor = new CrtMonitor( (int) (Gdx.graphics.getWidth() * 0.25f), (int) (Gdx.graphics.getHeight() * 0.25f), false, false, CrtScreen.RgbMode.ChromaticAberrations, effectsForCrt );
			curvature = new Curvature();
			effectsMap.put(PostProcessingEffectsEnum.CRTMONITOR, crtMonitor);
			effectsMap.put(PostProcessingEffectsEnum.CURVATURE, curvature);
		}
	}

	public PostProcessorEffect get(PostProcessingEffectsEnum effectType)
	{
		return effectsMap.get(effectType);
	}
}
