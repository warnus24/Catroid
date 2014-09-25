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

import com.robotium.solo.Solo;

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
import org.catrobat.catroid.livewallpaper.ui.SelectPostProcessingEffectActivity;
import org.catrobat.catroid.test.livewallpaper.utils.TestUtils;
import org.catrobat.catroid.uitest.util.UiTestUtils;

/**
 * Created by White on 25.09.2014.
 */
public class SelectPostProcessingEffectActivityTest extends
		SingleLaunchActivityTestCase<SelectPostProcessingEffectActivity> {

	private static final String PACKAGE = "org.catrobat.catroid";
	private ProjectManager projectManager = ProjectManager.getInstance(ProjectManagerState.LWP);
	private Solo solo;

	public SelectPostProcessingEffectActivityTest() {
		super(PACKAGE, SelectPostProcessingEffectActivity.class);
	}

	protected void setUp() throws Exception {
		super.setUp();
		Intent intent = new Intent(getActivity(), LiveWallpaper.class);
		getActivity().startService(intent);
		UiTestUtils.prepareStageForTest();
		solo = new Solo(getInstrumentation(),getActivity());
		solo.sleep(2000);

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

		//TestUtils.restartActivity(getActivity());
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testBloomActivity() {
		solo.clickOnText(PostProcessingEffectsEnum.BLOOM.toString());
		solo.waitForActivity(SelectBloomEffectActivity.class);
		assertTrue("Current activity is not Bloom Activity", solo.getCurrentActivity().getClass().equals(SelectBloomEffectActivity.class));

		View switch1 = (View) solo.getCurrentActivity().findViewById(R.id.switch1);
		solo.clickOnView(switch1);

		solo.setProgressBar(0, 20);
		solo.setProgressBar(1, 40);
		solo.setProgressBar(2, 60);
		solo.setProgressBar(3, 80);
		solo.setProgressBar(4, 100);
		solo.clickOnButton("OK!");

		solo.waitForActivity(SelectPostProcessingEffectActivity.class);
		solo.sleep(4000);
	}
}
