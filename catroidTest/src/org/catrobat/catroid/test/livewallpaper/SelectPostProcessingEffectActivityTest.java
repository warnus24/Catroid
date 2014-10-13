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

import android.content.Context;
import android.content.Intent;
import android.test.SingleLaunchActivityTestCase;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Switch;

import com.badlogic.gdx.graphics.Color;
import com.robotium.solo.Solo;

import org.catrobat.catroid.ProjectHandler;
import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.ScreenValues;
import org.catrobat.catroid.common.StandardProjectHandler;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.livewallpaper.LiveWallpaper;
import org.catrobat.catroid.livewallpaper.ProjectManagerState;
import org.catrobat.catroid.livewallpaper.postprocessing.PostProcessingEffectsEnum;
import org.catrobat.catroid.livewallpaper.ui.SelectBloomEffectActivity;
import org.catrobat.catroid.livewallpaper.ui.SelectCrtMonitorEffectActivity;
import org.catrobat.catroid.livewallpaper.ui.SelectCurvatureEffectActivity;
import org.catrobat.catroid.livewallpaper.ui.SelectPostProcessingEffectActivity;
import org.catrobat.catroid.livewallpaper.ui.SelectPostProcessingEffectFragment;
import org.catrobat.catroid.livewallpaper.ui.SelectVignetteEffectActivity;
import org.catrobat.catroid.test.livewallpaper.utils.TestUtils;
import org.catrobat.catroid.uitest.util.UiTestUtils;
import org.catrobat.catroid.utils.PostProcessingUtil;

import com.badlogic.gdx.*;

/**
 * Created by White on 25.09.2014.
 */
public class SelectPostProcessingEffectActivityTest extends
		SingleLaunchActivityTestCase<SelectPostProcessingEffectActivity> {

	private static final String PACKAGE = "org.catrobat.catroid";
	private ProjectManager projectManager = ProjectManager.getInstance();
	private Solo solo;
	private final int DEFAULT_SLEEP = 3100;

	public SelectPostProcessingEffectActivityTest() {
		super(PACKAGE, SelectPostProcessingEffectActivity.class);
	}

	protected void setUp() throws Exception {
		super.setUp();
		ProjectHandler.getInstance().changeToLiveWallpaper(this.getActivity());
		Intent intent = new Intent(getActivity(), LiveWallpaper.class);
		getActivity().startService(intent);
		UiTestUtils.prepareStageForTest();
		solo = new Solo(getInstrumentation(),getActivity());
		solo.sleep(DEFAULT_SLEEP);
		LiveWallpaper.getInstance().initializeForTest();
		TestUtils.initializePostProcessingGUISForTest(LiveWallpaper.getInstance().getEffectMap());

		DisplayMetrics disp = new DisplayMetrics();
		getActivity().getApplicationContext();
		((WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(disp);
		ScreenValues.SCREEN_HEIGHT = disp.heightPixels;
		ScreenValues.SCREEN_WIDTH = disp.widthPixels;

		ProjectManager.changeState(ProjectManagerState.LWP);
		Log.v("LWP", String.valueOf(ScreenValues.SCREEN_HEIGHT + " " + String.valueOf(ScreenValues.SCREEN_WIDTH)));
		Project defaultProject = null;
		if(projectManager.getCurrentProject() == null || projectManager.getCurrentProject().getName()!= solo.getString(R.string.default_project_name)){
			try{
				defaultProject = StandardProjectHandler.createAndSaveStandardProject(getActivity().getApplicationContext());
			}
			catch(IllegalArgumentException e){
				Log.d("LWP", "The default project was not created because it probably already exists");
				defaultProject = StorageHandler.getInstance().loadProject(solo.getString(R.string.default_project_name));
			}
			ProjectManager.getInstance().setProject(defaultProject);
		}

		TestUtils.restartActivity(getActivity());
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testIfEffectSizeIsRight(){
		solo.waitForActivity(getActivity().getClass());
		int effectsSize = SelectPostProcessingEffectFragment.EFFECT_ARRAY_SIZE;
		solo.sleep(DEFAULT_SLEEP);
		View view = null;
		int i = 0;
		for(i = 0; i < effectsSize; i++){
			view = solo.getCurrentActivity().findViewById(i);
			assertNotNull("View should not be null", view);
		}
		i++;
		assertNotNull("View be null", i);
	}

	public void testIfGUIIsGreenIfBloomIsEnabledAndIfBloomIsFirstInList(){
		final int bloomPosition = 0;
		solo.clickInList(bloomPosition);
		solo.sleep(DEFAULT_SLEEP);
		assertEquals("First Effect should be Bloom!", SelectBloomEffectActivity.class, solo.getCurrentActivity().getClass());
		solo.goBack();
		solo.waitForActivity(SelectPostProcessingEffectActivity.class);
		View view = solo.getCurrentActivity().findViewById(bloomPosition);
		String referenceEffectColor = getActivity().getResources().getString(R.string.lwp_postprocessing_enabled_color);;

		assertEquals("Bloom effect has not the same color! Therefore the state is wrong", referenceEffectColor, view.getTag());
	}

	public void testIfGUIIsOrangeIfBloomActivityIsDisabled() {
		TestUtils.setBloomEffectDisabled(LiveWallpaper.getInstance().getEffectMap());
		TestUtils.restartActivity(getActivity());
		solo.waitForActivity(getActivity().getClass());
		solo.sleep(DEFAULT_SLEEP);

		View view = solo.getCurrentActivity().findViewById(0);
		assertNotNull("View should not be null", view);

		String referenceEffectColor = getActivity().getResources().getString(R.string.lwp_postprocessing_disabled_color);
		assertEquals("Bloom effect has not the same color", referenceEffectColor, view.getTag());
	}

	public void testIfBloomActivityIsStartedAndIfValuesAreRightInitialized() {
		solo.clickOnText(PostProcessingEffectsEnum.BLOOM.toString());
		solo.waitForActivity(SelectBloomEffectActivity.class);
		assertTrue("Current activity is not Bloom Activity", solo.getCurrentActivity().getClass().equals(SelectBloomEffectActivity.class));

		solo.sleep(DEFAULT_SLEEP);

		Switch switch1 = (Switch) solo.getCurrentActivity().findViewById(R.id.switch1);
		assertNotNull("Switch shouldn't be null", switch1);

		assertEquals("Switch has false state", TestUtils.BLOOM_IS_ENABLED, switch1.isChecked());

		SeekBar seekBar1 = (SeekBar) solo.getCurrentActivity().findViewById(R.id.seekBar1);
		assertNotNull("Seekbar shouldn't be null", seekBar1);
		assertEquals("Seekbar1 has false state", seekBar1.getProgress(), Math.round(TestUtils.BASE_INT));

		SeekBar seekBar2 = (SeekBar) solo.getCurrentActivity().findViewById(R.id.seekBar2);
		assertNotNull("Seekbar shouldn't be null", seekBar2);
		assertEquals("Seekbar2 has false state", seekBar2.getProgress(), Math.round(TestUtils.BASE_SAT));

		SeekBar seekBar3 = (SeekBar) solo.getCurrentActivity().findViewById(R.id.seekBar3);
		assertNotNull("Seekbar shouldn't be null", seekBar3);
		assertEquals("Seekbar3 has false state", seekBar3.getProgress(), Math.round(TestUtils.BLOOM_INT));

		SeekBar seekBar4 = (SeekBar) solo.getCurrentActivity().findViewById(R.id.seekBar4);
		assertNotNull("Seekbar shouldn't be null", seekBar4);
		assertEquals("Seekbar4 has false state", seekBar4.getProgress(), Math.round(TestUtils.BLOOM_SAT));

		SeekBar seekBar5 = (SeekBar) solo.getCurrentActivity().findViewById(R.id.seekBar5);
		assertNotNull("Seekbar shouldn't be null", seekBar5);
		assertEquals("Seekbar5 has false state", seekBar5.getProgress(), Math.round(TestUtils.BLOOM_THRESHOLD));

		solo.clickOnView(switch1);

		solo.setProgressBar(0, 20);
		solo.setProgressBar(1, 40);
		solo.setProgressBar(2, 60);
		solo.setProgressBar(3, 80);
		solo.setProgressBar(4, 100);
		solo.clickOnButton("OK!");

		solo.waitForActivity(SelectPostProcessingEffectActivity.class);
	}

	public void testIfVignetteActivityIsStartedAndIfValuesAreRightInitialized() {
		solo.clickOnText(PostProcessingEffectsEnum.VIGNETTE.toString());
		solo.waitForActivity(SelectVignetteEffectActivity.class);
		assertTrue("Current activity is not Vignette Activity", solo.getCurrentActivity().getClass().equals(SelectVignetteEffectActivity.class));

		solo.sleep(DEFAULT_SLEEP);

		Switch switch1 = (Switch) solo.getCurrentActivity().findViewById(R.id.switch1);
		assertNotNull("Switch shouldn't be null", switch1);

		assertEquals("Switch has false state", TestUtils.VIGNETTE_IS_ENABLED, switch1.isChecked());

		SeekBar seekBar1 = (SeekBar) solo.getCurrentActivity().findViewById(R.id.seekBar1);
		assertNotNull("Seekbar shouldn't be null", seekBar1);
		assertEquals("Seekbar1 has false state", seekBar1.getProgress(), Math.round(TestUtils.INTENSITY));

		solo.clickOnView(switch1);

		solo.setProgressBar(0, 20);
		solo.clickOnButton("OK!");

		solo.waitForActivity(SelectPostProcessingEffectActivity.class);
	}

	public void testIfCurvatureActivityIsStartedAndIfValuesAreRightInitialized() {
		solo.clickOnText(PostProcessingEffectsEnum.CURVATURE.toString());
		solo.waitForActivity(SelectCurvatureEffectActivity.class);
		assertTrue("Current activity is not Curvature Activity", solo.getCurrentActivity().getClass().equals(SelectCurvatureEffectActivity.class));

		solo.sleep(DEFAULT_SLEEP);
		Switch switch1 = (Switch) solo.getCurrentActivity().findViewById(R.id.switch1);
		assertNotNull("Switch shouldn't be null", switch1);

		assertEquals("Switch has false state", TestUtils.CURVATURE_IS_ENABLED, switch1.isChecked());

		SeekBar seekBar1 = (SeekBar) solo.getCurrentActivity().findViewById(R.id.seekBar1);
		assertNotNull("Seekbar shouldn't be null", seekBar1);
		assertEquals("Seekbar1 has false state", seekBar1.getProgress(), Math.round(TestUtils.DISTORTION));

		solo.clickOnView(switch1);

		solo.setProgressBar(0, 30);
		solo.clickOnButton("OK!");

		solo.waitForActivity(SelectPostProcessingEffectActivity.class);
	}

	public void testIfCrtMonitorActivityIsStartedAndIfValuesAreRightInitialized() {
		solo.clickOnText(PostProcessingEffectsEnum.CRTMONITOR.toString());
		solo.waitForActivity(SelectCrtMonitorEffectActivity.class);
		assertTrue("Current activity is not Curvature Activity", solo.getCurrentActivity().getClass().equals(SelectCrtMonitorEffectActivity.class));

		solo.sleep(DEFAULT_SLEEP);
		Switch switch1 = (Switch) solo.getCurrentActivity().findViewById(R.id.switch1);
		assertNotNull("Switch shouldn't be null", switch1);

		assertEquals("Switch has false state", TestUtils.CRTMONITOR_IS_ENABLED, switch1.isChecked());

		SeekBar seekBar1 = (SeekBar) solo.getCurrentActivity().findViewById(R.id.seekBar1);
		assertNotNull("Seekbar shouldn't be null", seekBar1);
		assertEquals("Seekbar1 has false state", seekBar1.getProgress(), Math.round(TestUtils.CHROMATIC_DISPERSION_BY));

		SeekBar seekBar2 = (SeekBar) solo.getCurrentActivity().findViewById(R.id.seekBar2);
		assertNotNull("Seekbar shouldn't be null", seekBar2);
		assertEquals("Seekbar1 has false state", seekBar2.getProgress(), Math.round(TestUtils.CHROMATIC_DISPERSION_RC));

		solo.clickOnView(switch1);

		solo.setProgressBar(0, 40);
		solo.clickOnButton("OK!");

		solo.waitForActivity(SelectPostProcessingEffectActivity.class);
	}



}
