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
import android.service.wallpaper.WallpaperService;
import android.test.ActivityInstrumentationTestCase2;

import com.robotium.solo.Solo;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.StandardProjectHandler;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.livewallpaper.LiveWallpaper;
import org.catrobat.catroid.livewallpaper.ProjectManagerState;
import org.catrobat.catroid.livewallpaper.ui.SelectProgramActivity;
import org.catrobat.catroid.uitest.util.UiTestUtils;

/**
 * Created by Christian on 12.08.2014.
 */
public class LiveWallpaperTest extends ActivityInstrumentationTestCase2<SelectProgramActivity> {

	private Solo solo;
	private final static String LWP_TEST_1 = "Test 1";

	private ProjectManager projectManager;
	private WallpaperService.Engine engine;
	private LiveWallpaper lwp;

	public LiveWallpaperTest() { super(SelectProgramActivity.class); }

	private void setUpLivewallpaper() throws Exception {
		Intent intent = new Intent(getActivity(), LiveWallpaper.class);
		getActivity().startService(intent);
	}

	private void setUpProjectManager() throws Exception {
		ProjectManager.changeState(ProjectManagerState.LWP);
		projectManager = ProjectManager.getInstance(ProjectManagerState.LWP);

		if(projectManager.getCurrentProject() == null || projectManager.getCurrentProject().getName()!= solo.getString(R.string.default_project_name)){
			Project defaultProject;
			try{
				defaultProject = StandardProjectHandler.createAndSaveStandardProject(getActivity().getApplicationContext());
			}
			catch(IllegalArgumentException e){
				defaultProject = StorageHandler.getInstance().loadProject(solo.getString(R.string.default_project_name));
			}
			projectManager.setProject(defaultProject);
		}
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();

		solo = new Solo(getInstrumentation(), getActivity());

		setUpProjectManager();
		setUpLivewallpaper();
	}

	@Override
	public void tearDown() throws Exception {
		solo.clickOnText(solo.getString(R.string.default_project_name));
		solo.clickOnButton(solo.getString(R.string.yes));

		solo.clickOnView(solo.getView(R.id.delete));
		solo.clickOnView(solo.getView(R.id.select_all));
		UiTestUtils.acceptAndCloseActionMode(solo);
		solo.clickOnButton(solo.getString(R.string.yes));
		super.tearDown();
	}


	public void testCreateNewProject() throws Exception {
		if(!StorageHandler.getInstance().projectExists(LWP_TEST_1)) {
			solo.clickOnMenuItem(solo.getString(R.string.lwp_new));
			solo.enterText(solo.getEditText(solo.getString(R.string.new_project_dialog_hint)), LWP_TEST_1);
			solo.clickOnButton("OK");
		}

		//solo.goBack();
		solo.clickOnText(LWP_TEST_1);
		solo.clickOnText("Ja");
	}

}
