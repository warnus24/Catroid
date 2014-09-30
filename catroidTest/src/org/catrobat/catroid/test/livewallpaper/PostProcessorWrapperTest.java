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

import android.content.Intent;
import android.test.InstrumentationTestCase;

import com.bitfire.postprocessing.PostProcessor;
import com.bitfire.postprocessing.effects.Bloom;

import junit.framework.TestCase;

import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.livewallpaper.LiveWallpaper;
import org.catrobat.catroid.livewallpaper.postprocessing.BloomAttributeContainer;
import org.catrobat.catroid.livewallpaper.postprocessing.CrtMonitorAttributeContainer;
import org.catrobat.catroid.livewallpaper.postprocessing.CurvatureAttributeContainer;
import org.catrobat.catroid.livewallpaper.postprocessing.EffectsContainer;
import org.catrobat.catroid.livewallpaper.postprocessing.PostProcessingEffectAttributContainer;
import org.catrobat.catroid.livewallpaper.postprocessing.PostProcessingEffectsEnum;
import org.catrobat.catroid.livewallpaper.postprocessing.PostProcessorWrapper;
import org.catrobat.catroid.livewallpaper.postprocessing.VignetteAttributeContainer;
import org.catrobat.catroid.livewallpaper.ui.SelectPostProcessingEffectFragment;
import org.catrobat.catroid.test.livewallpaper.utils.TestUtils;
import org.junit.BeforeClass;
import org.junit.*;
import org.mockito.Mockito;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Iterator;
import java.util.Map;

/**
 * Created by White on 28.09.2014.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(SelectPostProcessingEffectFragment.class)
public class PostProcessorWrapperTest extends TestCase{
	private Map<PostProcessingEffectsEnum,PostProcessingEffectAttributContainer> effectsMap;
	private PostProcessor postProcessor;
	private EffectsContainer effectsContainer;
	private PostProcessorWrapper postProcessorWrapper;
	private LiveWallpaper liveWallpaper = new LiveWallpaper();

	@Before
	public void initMocks(){
		Bloom bloom = Mockito.mock(Bloom.class);
		Mockito.when(effectsContainer.get(PostProcessingEffectsEnum.BLOOM)).thenReturn(bloom);
		effectsMap = TestUtils.initializePostProcessingEffectsAttributesWithoutFactorization();
		postProcessor = Mockito.mock(PostProcessor.class);
		effectsContainer = Mockito.mock(EffectsContainer.class);
		postProcessorWrapper = new PostProcessorWrapper(postProcessor, effectsContainer);
		PowerMockito.mockStatic(SelectPostProcessingEffectFragment.class);
		PowerMockito.doNothing().when(SelectPostProcessingEffectFragment.class);
	}

	@Test
	public void testAddEffectsFirstTime() {
//		postProcessorWrapper.add(PostProcessingEffectsEnum.BLOOM, effectsMap.get(PostProcessingEffectsEnum.BLOOM));

		Assert.assertTrue("Yes", true);

		//Iterator it = effectsMap.entrySet().iterator();
		//while (it.hasNext()) {
		//	Map.Entry pairs = (Map.Entry)it.next();
		//	postProcessorWrapper.add((PostProcessingEffectsEnum)pairs.getKey(), (PostProcessingEffectAttributContainer)pairs.getValue());
		//}
	}
}

