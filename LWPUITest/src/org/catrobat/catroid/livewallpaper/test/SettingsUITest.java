package org.catrobat.catroid.livewallpaper.test;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.StandardProjectHandler;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.livewallpaper.test.utils.TestUtils;
import org.catrobat.catroid.livewallpaper.ui.SelectProgramActivity;

import com.jayway.android.robotium.solo.Solo;

import org.catrobat.catroid.livewallpaper.LiveWallpaper;
import org.catrobat.catroid.livewallpaper.R;
import org.catrobat.catroid.utils.Utils;
import android.test.SingleLaunchActivityTestCase;
import android.util.Log;


public class SettingsUITest extends
SingleLaunchActivityTestCase<SelectProgramActivity> {


	private static final String TEST_PROJECT_NAME = "Test project";
	private static final String PACKAGE = "org.catrobat.catroid.livewallpaper"; 
	private Solo solo;
	private Project testProject;
	
	private ProjectManager projectManager = ProjectManager.getInstance(); 

	public SettingsUITest() {
		super(PACKAGE,SelectProgramActivity.class);
	}


	protected void setUp() throws Exception {
		super.setUp();
		
		solo = new Solo(getInstrumentation(),getActivity());
			
		if(projectManager.getCurrentProject() == null || projectManager.getCurrentProject().getName()!= solo.getString(R.string.default_project_name)){
			Project defaultProject; 
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
		testProject = TestUtils.createEmptyProjectWithoutSettingIt(getActivity().getApplicationContext(), TEST_PROJECT_NAME);
		LiveWallpaper lwp = new LiveWallpaper();
		lwp.TEST = true; 
		lwp.onCreate();
	}

	protected void tearDown() throws Exception {
		StorageHandler.getInstance().deleteProject(testProject);
		super.tearDown();
	}
	
	public void testComingUp()
	{	
		solo.assertCurrentActivity("SelectProgramActivity is not the current activity", SelectProgramActivity.class);
	}
	
    public void testAboutDialog()
    {
    	solo.clickOnActionBarItem(R.id.about);
    	assertTrue("About pocket code text not found", solo.searchText(solo.getString(R.string.dialog_about_license_info)));
    	assertTrue("About pocket code link not found", solo.searchText(solo.getString(R.string.dialog_about_catrobat_link_text)));
    	assertTrue("About pocket code version not found", solo.searchText(Utils.getVersionName(getActivity().getApplicationContext())));
    	solo.goBack();
    }
    
    public void testWallpaperSelection()
    {
    	assertEquals("The current project should be set to the standard project", solo.getString(R.string.default_project_name), projectManager.getCurrentProject().getName());
    	
    	solo.clickOnText(TEST_PROJECT_NAME);
		solo.sleep(200);
		solo.clickOnText(solo.getString(R.string.yes));
		solo.sleep(2000);
		
		String currentProjectName = projectManager.getCurrentProject().getName();
		assertTrue("The project was not successfully changed", currentProjectName.equals(TEST_PROJECT_NAME));			
    }
    
    
	
}
