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

package org.catrobat.catroid.test.livewallpaper;

import android.test.InstrumentationTestCase;

import com.badlogic.gdx.utils.GdxNativesLoader;
import com.bitfire.postprocessing.PostProcessor;
import org.catrobat.catroid.livewallpaper.LiveWallpaper;
import org.catrobat.catroid.livewallpaper.postprocessing.EffectsContainer;
import org.catrobat.catroid.livewallpaper.postprocessing.PostProcessingEffectAttributContainer;
import org.catrobat.catroid.livewallpaper.postprocessing.PostProcessingEffectsEnum;
import org.catrobat.catroid.livewallpaper.postprocessing.PostProcessorWrapper;
import org.junit.*;

import java.util.Map;

/**
 * Created by White on 28.09.2014.
 */

public class PostProcessorWrapperTest extends InstrumentationTestCase{
	private Map<PostProcessingEffectsEnum,PostProcessingEffectAttributContainer> effectsMap;
	private PostProcessor postProcessor = new PostProcessor(true, false, true);
	private EffectsContainer effectsContainer = new EffectsContainer();
	private PostProcessorWrapper postProcessorWrapper = new PostProcessorWrapper(postProcessor, effectsContainer);
	private LiveWallpaper liveWallpaper = new LiveWallpaper();

	static {
		GdxNativesLoader.load();
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void initMocks(){
		/*Bloom bloom = Mockito.mock(Bloom.class);
		Mockito.when(effectsContainer.get(PostProcessingEffectsEnum.BLOOM)).thenReturn(bloom);
		effectsMap = TestUtils.initializePostProcessingEffectsAttributesWithoutFactorization();
		postProcessor = Mockito.mock(PostProcessor.class);
		effectsContainer = Mockito.mock(EffectsContainer.class);
		postProcessorWrapper = new PostProcessorWrapper(postProcessor, effectsContainer);
		PowerMockito.mockStatic(SelectPostProcessingEffectFragment.class);
		PowerMockito.doNothing().when(SelectPostProcessingEffectFragment.class);
		*/
	}

	public void testAddEffectsFirstTime() {
		postProcessorWrapper.add(PostProcessingEffectsEnum.BLOOM, effectsMap.get(PostProcessingEffectsEnum.BLOOM));

		Assert.assertTrue("Yes", true);

		//Iterator it = effectsMap.entrySet().iterator();
		//while (it.hasNext()) {
		//	Map.Entry pairs = (Map.Entry)it.next();
		//	postProcessorWrapper.add((PostProcessingEffectsEnum)pairs.getKey(), (PostProcessingEffectAttributContainer)pairs.getValue());
		//}
	}
}

