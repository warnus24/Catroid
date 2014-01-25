package org.catrobat.catroid.livewallpaper.test;

import java.util.List;
import java.util.Locale;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.ProjectData;
import org.catrobat.catroid.common.ScreenValues;
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
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent.DispatcherState;
import android.view.WindowManager;


public class SettingsUITest extends
SingleLaunchActivityTestCase<SelectProgramActivity> {

	private static final int ACTION_MODE_ACCEPT_IMAGE_BUTTON_INDEX = 0;
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
		DisplayMetrics disp = new DisplayMetrics();
		((WindowManager) getActivity().getSystemService(getActivity().getApplicationContext().WINDOW_SERVICE)).getDefaultDisplay().getMetrics(disp);
		ScreenValues.SCREEN_HEIGHT = disp.heightPixels;
		ScreenValues.SCREEN_WIDTH = disp.widthPixels;
		
			Log.v("LWP", String.valueOf(ScreenValues.SCREEN_HEIGHT + " " + String.valueOf(ScreenValues.SCREEN_WIDTH)));
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
    
		
    public void testDeleteSingleProject(){
    	solo.sleep(500);
    	SelectProgramActivity selectProgramActvity = (SelectProgramActivity) solo.getCurrentActivity();
     	List<ProjectData> projectList = selectProgramActvity.getSelectProgramFragment().getProjectList();
     	int initialProgramCount = projectList.size();
     	
    	solo.clickOnActionBarItem(R.id.delete);
    	solo.clickOnText(TEST_PROJECT_NAME);
    	solo.clickOnImage(ACTION_MODE_ACCEPT_IMAGE_BUTTON_INDEX);
    	
    	assertTrue("The title of the confirmation dialog for deleting a project was not shown", 
         		solo.searchText(solo.getString(R.string.dialog_confirm_delete_program_title)));
    	
        assertTrue("The content of the confirmation dialog for deleting a project was not shown", 
         		solo.searchText(solo.getString(R.string.dialog_confirm_delete_program_message)));
        
        solo.clickOnText(solo.getString(R.string.no));
        projectList = selectProgramActvity.getSelectProgramFragment().getProjectList();
        assertEquals("The program count does not match the initial program count", initialProgramCount, projectList.size());
        
        solo.clickOnActionBarItem(R.id.delete);
    	solo.clickOnText(TEST_PROJECT_NAME);
    	//solo.clickOnImage(ACTION_MODE_ACCEPT_IMAGE_BUTTON_INDEX);
    	float width = 0.9f * ScreenValues.SCREEN_WIDTH;
    	float height = 0.7f * ScreenValues.SCREEN_HEIGHT;
    	

		Log.v("LWP", String.valueOf(width) + " " + String.valueOf(height));
    	solo.clickOnScreen(width, height);
    	solo.clickOnText(solo.getString(R.string.yes));
    	assertFalse("The project was not deleted", solo.searchText(TEST_PROJECT_NAME));
    	
    	projectList = selectProgramActvity.getSelectProgramFragment().getProjectList();
    	int expectedProgramCountAfterDeletion = initialProgramCount - 1; 
    	assertEquals("The program count not okay after deleting one program", expectedProgramCountAfterDeletion, projectList.size());
    }
    
    public void testDeleteCurrentProject(){
    	assertEquals("The current project should be set to the standard project", solo.getString(R.string.default_project_name), projectManager.getCurrentProject().getName());
    	solo.clickOnActionBarItem(R.id.delete);
    	solo.clickOnText(solo.getString(R.string.default_project_name));
    	solo.clickOnImage(ACTION_MODE_ACCEPT_IMAGE_BUTTON_INDEX);
    	assertTrue("The error dialog was not shown", solo.searchText(solo.getString(R.string.lwp_error_delete_current_program)));
    }
    
    public void testDeleteAllProjects(){   	
    	SelectProgramActivity selectProgramActvity = (SelectProgramActivity) solo.getCurrentActivity();
     	List<ProjectData> projectList = selectProgramActvity.getSelectProgramFragment().getProjectList();
     	int initialProgramCount = projectList.size(); 
    	
     	solo.clickOnActionBarItem(R.id.delete);
    	String selectAll = solo.getString(R.string.select_all).toUpperCase(Locale.getDefault());
        solo.clickOnText(selectAll);
        solo.clickOnImage(ACTION_MODE_ACCEPT_IMAGE_BUTTON_INDEX);
        
        assertTrue("The error dialog for deleting all projects but the current one was not shown", 
        		solo.searchText(solo.getString(R.string.lwp_error_delete_multiple_program)));
        solo.clickOnButton(solo.getString(R.string.yes));
        
        assertTrue("The confirmation dialog for deleting projects was not shown", 
        		solo.searchText(solo.getString(R.string.dialog_confirm_delete_multiple_programs_title)));
        
        assertTrue("The title of the confirmation dialog for deleting projects was not shown", 
        		solo.searchText(solo.getString(R.string.dialog_confirm_delete_program_message)));
        
        solo.clickOnButton(solo.getString(R.string.no));
        assertTrue("The program count is not equal to program count before clicking on delete", projectList.size() == initialProgramCount);
        
        solo.clickOnActionBarItem(R.id.delete);
        solo.clickOnText(selectAll);
        solo.clickOnImage(ACTION_MODE_ACCEPT_IMAGE_BUTTON_INDEX);
        solo.clickOnText(solo.getString(R.string.yes));
        solo.clickOnText(solo.getString(R.string.yes));
        
        assertTrue("The program count should be 1 after delete all projects but the current one", projectList.size() == 1);
    	
    }

	
}
