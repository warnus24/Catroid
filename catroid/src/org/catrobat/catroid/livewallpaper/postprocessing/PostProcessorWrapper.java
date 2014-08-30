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

import android.util.Log;

import com.bitfire.postprocessing.PostProcessor;
import com.bitfire.postprocessing.PostProcessorEffect;
import com.bitfire.postprocessing.effects.Bloom;

import org.catrobat.catroid.livewallpaper.LiveWallpaper;
import org.catrobat.catroid.livewallpaper.ui.SelectPostProcessingEffectFragment;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by White on 29.08.2014.
 */
public class PostProcessorWrapper
{
	private Map<PostProcessingEffectsEnum,PostProcessorEffect> map = new HashMap<PostProcessingEffectsEnum,PostProcessorEffect>();
	private Map<PostProcessingEffectsEnum,PostProcessorEffect> effects = Collections.synchronizedMap(map);
	PostProcessor postProcessor = new PostProcessor(false, false, false);
	EffectsContainer effectsContainer = new EffectsContainer();


	public void add(PostProcessingEffectsEnum type, PostProcessingEffectAttributContainer attributes)
	{
		synchronized (postProcessor) {
			PostProcessorEffect effect = effectsContainer.get(type);
			if (effects.containsKey(type)) {
				PostProcessorEffect activeEffect = effects.get(type);
				setAttributes(type, activeEffect, attributes);
				Log.e("Error", "Effekt in die Liste NICHT hinzugefügt");
			} else {
				Log.e("Error", "Effekt in die Liste hinzugefügt");
				setAttributes(type, effect, attributes);
				postProcessor.addEffect(effect);
				effects.put(type, effect);
			}
			SelectPostProcessingEffectFragment.setActivated(type, true);
			LiveWallpaper.getInstance().setPostProcessingEffectAttributes(attributes);
		}
	}

	public void remove(PostProcessingEffectsEnum type, PostProcessingEffectAttributContainer attributes)
	{
		synchronized (postProcessor) {
			PostProcessorEffect effect = effectsContainer.get(type);
			if (effects.containsKey(type)) {
				setAttributes(type, effect, attributes);
				postProcessor.removeEffect(effect);
				effects.remove(type);
			}
			SelectPostProcessingEffectFragment.setActivated(type, false);
			LiveWallpaper.getInstance().setPostProcessingEffectAttributes(attributes);
		}

	}

	public void removeAll()
	{
		synchronized (postProcessor) {
			Set<PostProcessingEffectsEnum> keys = effects.keySet();
			Iterator<PostProcessingEffectsEnum> iterator = keys.iterator();
			while(iterator.hasNext())
			{
				PostProcessingEffectsEnum effectType = iterator.next();
				PostProcessorEffect effect = effects.get(effectType);
				postProcessor.removeEffect(effect);
				SelectPostProcessingEffectFragment.setActivated(effectType, false);
			}

			effects.clear();
		}
	}

	private void setAttributes(PostProcessingEffectsEnum type, PostProcessorEffect effect, PostProcessingEffectAttributContainer attributes)
	{
		if(type.equals(PostProcessingEffectsEnum.BLOOM))
		{
			BloomAttributeContainer bloomAttributes = (BloomAttributeContainer) attributes;
			Bloom bloom = (Bloom) effect;
			bloom.setBaseIntesity(bloomAttributes.getBaseInt());
			bloom.setBaseSaturation(bloomAttributes.getBaseSat());
			bloom.setBloomIntesity(bloomAttributes.getBloomInt());
			bloom.setBloomSaturation(bloomAttributes.getBloomSat());

			Log.e("Error", "Base Int: "+bloom.getBaseIntensity());
			Log.e("Error", "Base Sat: "+bloom.getBaseSaturation());
			Log.e("Error", "Bloom Int: "+bloom.getBloomIntensity());
			Log.e("Error", "Bloom Sat: "+bloom.getBloomSaturation());
		}

		else if(type.equals(PostProcessingEffectsEnum.VIGNETTE))
		{
		}

		else if(type.equals(PostProcessingEffectsEnum.CURVATURE))
		{
		}

		else if(type.equals(PostProcessingEffectsEnum.CRTMONITOR))
		{
		}
	}

	public PostProcessor getPostProcessor()
	{
		return postProcessor;
	}

	public void dispose()
	{
		synchronized (postProcessor)
		{
			postProcessor.dispose();
		}
	}

	public void rebind()
	{
		synchronized (postProcessor) {
			postProcessor.rebind();
		}
	}
}
