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
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

import org.junit.*;
import static org.junit.Assert.*;

import java.io.IOException;
import java.util.*;


public class SettingsUITest extends
		ActivityInstrumentationTestCase2<SelectProgramActivity> {

	private static Solo solo;
	private static final int DELAY = 1500;
	
	private static final String TEST_PROJECT_NAME = "Test project";
	private static Project testProject; 
	
	private static ProjectManager projectManager = ProjectManager.getInstance(); 
	
	public SettingsUITest() {
		super(SelectProgramActivity.class);
	}


	@BeforeClass
	public static void oneTimeSetup(){
//		if(projectManager.getCurrentProject() == null || projectManager.getCurrentProject().getName()!= solo.getString(R.string.default_project_name)){
//		
//			try{
//				Project defaultProject = StandardProjectHandler.createAndSaveStandardProject(getActivity().getApplicationContext());
//				projectManager.setProject(defaultProject);
//			}
//			catch(IllegalArgumentException e){
//				Log.d("LWP", "The default project was not created because it probably already exists");
//				Project defaultProject = StorageHandler.getInstance().loadProject(solo.getString(R.string.default_project_name));
//				projectManager.setProject(defaultProject);
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//		
//		testProject = TestUtils.createEmptyProjectWithoutSettingIt(getActivity().getApplicationContext()
//	    			, TEST_PROJECT_NAME);
		LiveWallpaper lwp = new LiveWallpaper();
		lwp.TEST = true; 
		lwp.onCreate();
	}
	
    @AfterClass
	public static void oneTimeTearDown(){
		StorageHandler.getInstance().deleteProject(testProject);
	}
	
    @Before
	public void setUp() throws Exception {
		super.setUp();
		solo = new Solo(getInstrumentation(),getActivity());		
	
	}

	
    @Test
	public void testComingUp()
	{	
		solo.assertCurrentActivity("SelectProgramActivity is not the current activity", SelectProgramActivity.class);
	}
	
//	public void testAboutButtton()
//	{
//		solo.clickOnText(solo.getString(R.string.about_this_wallpaper));
//		solo.sleep(DELAY);
//		assertTrue("The about this wallpaper title was not found", solo.searchText(solo.getString(R.string.about_this_wallpaper)));
//
//		assertTrue("The about this project title was not found", solo.searchText(ProjectManager.getInstance().getCurrentProject().getName()));
//  		if(ProjectManager.getInstance().getCurrentProject().getDescription()!= "")//kein null Pointer 
//		{
//		  assertTrue("The Description of this wallpaper was not found", solo.searchText(solo.getString(ProjectManager.getInstance().getCurrentProject().getDescription())));
//		}
//		solo.goBack();	
//		solo.clickOnText(solo.getString(R.string.main_menu_about_pocketcode));
//		solo.sleep(DELAY);
//		solo.goBack();
//	}
//	
//	public void testPreferences()
//	{
//		assertFalse(SoundManager.getInstance().soundDisabledByLwp);
//		assertFalse(solo.isCheckBoxChecked(solo.getString(R.string.lwp_allow_sounds)));
//		solo.sleep(DELAY);
//		solo.clickOnText(solo.getString(R.string.lwp_allow_sounds));
//		assertFalse(solo.isCheckBoxChecked(solo.getString(R.string.lwp_allow_sounds)));
//		
//		solo.clickOnText(solo.getString(R.string.lwp_select_program));
//		solo.sleep(DELAY);
//		solo.clickOnText(solo.getString(R.string.default_project_name));
//		solo.sleep(DELAY);
//		solo.clickOnText(solo.getString(R.string.no));
//		solo.goBack();
//
//	}
		
    @Test
    public void testWallpaperSelection()
    {
//    	assertEquals("The current project should be set to the standard project", solo.getString(R.string.default_project_name), projectManager.getCurrentProject().getName());
//    	
//    	solo.clickOnText(TEST_PROJECT_NAME);
//		solo.sleep(200);
//		solo.clickOnText(solo.getString(R.string.yes));
//		
//		String currentProjectName = projectManager.getCurrentProject().getName();
//		assertTrue("The project was not successfully changed", currentProjectName.equals(TEST_PROJECT_NAME));
			
    }
    
	
}
