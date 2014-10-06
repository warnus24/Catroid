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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.TimeUtils;
import com.bitfire.postprocessing.PostProcessor;
import com.bitfire.postprocessing.PostProcessorEffect;
import com.bitfire.postprocessing.demo.ResourceFactory;
import com.bitfire.postprocessing.effects.Bloom;
import com.bitfire.postprocessing.effects.CrtMonitor;
import com.bitfire.postprocessing.effects.Curvature;
import com.bitfire.postprocessing.effects.Vignette;

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
	private Map<PostProcessingEffectsEnum,PostProcessorEffect> activeEffects = Collections.synchronizedMap(map);
	PostProcessor postProcessor = null;
	EffectsContainer effectsContainer = null;
	//PostProcessor postProcessor = new PostProcessor(false, true, false);
	//EffectsContainer effectsContainer = new EffectsContainer();
	long startTime = TimeUtils.millis();
	boolean isTest = false;

	public PostProcessorWrapper(PostProcessor postProcessor, EffectsContainer effectsContainer, boolean isTest){
		this.postProcessor = postProcessor;
		this.effectsContainer = effectsContainer;
		if(this.postProcessor == null){
			this.postProcessor = new PostProcessor(false, true, false);
		}
		if(this.effectsContainer == null){
			this.effectsContainer = new EffectsContainer();
		}
		this.isTest = isTest;
	}


	public void add(PostProcessingEffectsEnum type, PostProcessingEffectAttributContainer attributes)
	{
		synchronized (postProcessor) {
			PostProcessorEffect effect = effectsContainer.get(type);
			if (activeEffects.containsKey(type)) {
				PostProcessorEffect activeEffect = activeEffects.get(type);
				setAttributes(type, activeEffect, attributes);
				activeEffect.setEnabled(true);
				Log.e("Error", "Effekt in die Liste NICHT hinzugefügt");
			} else {
				Log.e("Error", "Effekt in die Liste hinzugefügt");
				setAttributes(type, effect, attributes);
				postProcessor.addEffect(effect);
				activeEffects.put(type, effect);
			}
			initializeTimeForCrtMonitor(type);
			LiveWallpaper.getInstance().setPostProcessingEffectAttributes(attributes);
			SelectPostProcessingEffectFragment.refresh();
		}
	}

	public Map<PostProcessingEffectsEnum,PostProcessorEffect> getActiveEffects(){
		return activeEffects;
	}

	private void initializeTimeForCrtMonitor(PostProcessingEffectsEnum type)
	{
		if(type.equals(PostProcessingEffectsEnum.CRTMONITOR)){
			startTime = TimeUtils.millis();
		}
	}

	public void updateEffects()
	{
		float elapsedSecs = (float)(TimeUtils.millis() - startTime) / 1000;
		CrtMonitor crtMonitor = (CrtMonitor) activeEffects.get(PostProcessingEffectsEnum.CRTMONITOR);
		if(crtMonitor != null){
			crtMonitor.setTime(elapsedSecs);
		}
	}

	public void remove(PostProcessingEffectsEnum type, PostProcessingEffectAttributContainer attributes)
	{
		synchronized (postProcessor) {
			PostProcessorEffect effect = effectsContainer.get(type);
			if (activeEffects.containsKey(type)) {
				setAttributes(type, effect, attributes);
				effect.setEnabled(false);
			}
			LiveWallpaper.getInstance().setPostProcessingEffectAttributes(attributes);
			refreshGUI();
		}
	}

	private void refreshGUI(){
		if(!isTest){
			SelectPostProcessingEffectFragment.refresh();
		}
	}

	public void removeAll()
	{
		synchronized (postProcessor) {
			Set<PostProcessingEffectsEnum> keys = activeEffects.keySet();
			Iterator<PostProcessingEffectsEnum> iterator = keys.iterator();
			while(iterator.hasNext())
			{
				PostProcessingEffectsEnum effectType = iterator.next();
				PostProcessorEffect effect = activeEffects.get(effectType);
				//postProcessor.removeEffect(effect);
				effect.setEnabled(false);
				PostProcessingEffectAttributContainer attributes = LiveWallpaper.getInstance().getPostProcessingEffectAttributes(effectType);
				attributes.setEnabled(false);
				LiveWallpaper.getInstance().setPostProcessingEffectAttributes(attributes);
			}
			refreshGUI();
			//effects.clear();
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
			bloom.setThreshold(bloomAttributes.getThreshold());

			Log.e("Error", "Base Int: "+bloom.getBaseIntensity());
			Log.e("Error", "Base Sat: "+bloom.getBaseSaturation());
			Log.e("Error", "Bloom Int: "+bloom.getBloomIntensity());
			Log.e("Error", "Bloom Sat: "+bloom.getBloomSaturation());
			Log.e("Error", "Bloom Threshold: "+bloom.getThreshold());
		}

		else if(type.equals(PostProcessingEffectsEnum.VIGNETTE))
		{
			VignetteAttributeContainer vignetteAttributes = (VignetteAttributeContainer) attributes;
			Vignette vignette = (Vignette) effect;
			vignette.setIntensity(vignetteAttributes.getIntensity());

			//boolean gradientActive = setGradientAndReturnActive(vignetteAttributes.getGradientType(), vignette);
			//Log.e("Error", "Vignette intensity: "+vignette.getIntensity());
			//Log.e("Error", "Gradient Type: "+vignetteAttributes.getGradientType().toString());
			//if(gradientActive){
			//	//Texture t = new Texture( Gdx.files.internal( "data/" + "gradient-mapping.png" ), Pixmap.Format.RGBA4444, false );
			//	//t.setFilter( Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest );
			//	//vignette.setLutTexture( t);
			//	vignette.setLutTexture( ResourceFactory.newTexture( "gradient-mapping.png", false ));
			//	Log.e("Error", "Yeah Vignette Gradient");
			////}
			//else{
			//	vignette.setLutTexture( null );
			//}

		}

		else if(type.equals(PostProcessingEffectsEnum.CURVATURE))
		{
			CurvatureAttributeContainer curvatureAttributes = (CurvatureAttributeContainer) attributes;
			Curvature curvature = (Curvature) effect;
			curvature.setDistortion(curvatureAttributes.getDistortion());
		}

		else if(type.equals(PostProcessingEffectsEnum.CRTMONITOR))
		{
			CrtMonitorAttributeContainer crtMonitorAttributeContainer = (CrtMonitorAttributeContainer) attributes;
			CrtMonitor crtMonitor = (CrtMonitor) effect;
			crtMonitor.setChromaticDispersion(crtMonitorAttributeContainer.getChromaticDispersionRC(),
																crtMonitorAttributeContainer.getChromaticDispersionBY());
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

	private boolean setGradientAndReturnActive(VignetteGradientEnum gradientType, Vignette vignette)
	{
			switch (gradientType) {
				case CROSSPROCESSING:
					vignette.setLutIndexVal(0, 16);
					return true;
				case SUNSET:
					vignette.setLutIndexVal(0, 5);
					return true;
				case MARS:
					vignette.setLutIndexVal(0, 7);
					return true;
				case VIVID:
					vignette.setLutIndexVal(0, 6);
					return true;
				case GREENLAND:
					vignette.setLutIndexVal(0, 8);
					return true;
				case CLOUDY:
					vignette.setLutIndexVal(0, 3);
					return true;
				case MUDDY:
					vignette.setLutIndexVal(0, 0);
					return true;
				case NONE:
					vignette.setLutIndexVal(0, -1 );
					return false;
			}

		return false;
	}
}
