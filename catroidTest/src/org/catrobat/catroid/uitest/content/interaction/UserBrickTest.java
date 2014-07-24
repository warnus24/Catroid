/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.uitest.content.interaction;

import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

import com.jayway.android.robotium.solo.Solo;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.bricks.UserBrick;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.fragment.AddBrickFragment;
import org.catrobat.catroid.uitest.util.UiTestUtils;

public class UserBrickTest extends ActivityInstrumentationTestCase2<MainMenuActivity> {
	private Solo solo = null;

	public UserBrickTest() {
		super(MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		UiTestUtils.prepareStageForTest();
		UiTestUtils.createTestProjectWithUserBrick();

		solo = new Solo(getInstrumentation(), getActivity());
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);
	}

	@Override
	public void tearDown() throws Exception {
		UiTestUtils.goBackToHome(getInstrumentation());
		solo.finishOpenedActivities();
		UiTestUtils.clearAllUtilTestProjects();
		super.tearDown();
		solo = null;
	}

	public void testEditFormulaWithUserBrickDataAndChangeValuesViaFormulaEditor()
	{
		//add 4 userbrick variables, userbrick text and a userbrick linebreak

		//click on EditFormula and change all values
	}

//	public void testCopyAndDeleteUserBrickFromScriptWithBothVariants() {
//		//copy via action mode
//		UiTestUtils.openActionMode(solo, solo.getString(R.string.copy), R.id.copy, solo.getCurrentActivity());
//		solo.scrollDown();
//		solo.clickOnCheckBox(6);
//		UiTestUtils.acceptAndCloseActionMode(solo);
//		boolean twoUserBricksExist = (ProjectManager.getInstance().getCurrentScript().getBrickList().size() == 8);
//		solo.scrollDown();
//		solo.sleep(300);
//		assertTrue("2 userbricks should exist in the script after copying via action mode, but they don't!", twoUserBricksExist);
//
//		//delete via action mode
//		UiTestUtils.openActionMode(solo, solo.getString(R.string.delete), R.id.delete, solo.getCurrentActivity());
//		solo.scrollDown();
//		solo.clickOnCheckBox(6);
//		UiTestUtils.acceptAndCloseActionMode(solo);
//		solo.clickOnButton(solo.getString(R.string.yes));
//		boolean oneUserBrickExists = (ProjectManager.getInstance().getCurrentScript().getBrickList().size() == 7);
//		solo.scrollDown();
//		solo.sleep(500);
//		assertTrue("only 1 userbrick should exist in the script after copying via action mode, but that's not the case!", oneUserBrickExists);
//
//		//copy via context menu
//		solo.clickOnText(UiTestUtils.TEST_USER_BRICK_NAME);
//		String stringOnCopy = solo.getCurrentActivity()
//				.getString(R.string.brick_context_dialog_copy_brick);
//		solo.waitForText(stringOnCopy);
//		solo.clickOnText(stringOnCopy);
//		solo.sleep(1000);
//		UiTestUtils.dragFloatingBrick(solo, -1);
//		solo.sleep(2000);
//		solo.scrollDown();
//		boolean twoUserBricksExistContextMenu = (ProjectManager.getInstance().getCurrentScript().getBrickList().size() == 8);
//		solo.sleep(500);
//		assertTrue("2 userbricks should exist in the script after copying via context menu, but they don't!", twoUserBricksExistContextMenu);
//		solo.sleep(300);
//
////		//delete via context menu
//		solo.clickOnText(UiTestUtils.TEST_USER_BRICK_NAME);
//		String stringOnDelete = solo.getCurrentActivity()
//				.getString(R.string.brick_context_dialog_delete_brick);
//		solo.waitForText(stringOnDelete);
//		solo.clickOnText(stringOnDelete);
//		solo.waitForDialogToOpen();
//		solo.clickOnButton(solo.getString(R.string.yes));
//		solo.waitForDialogToClose();
//		solo.scrollDown();
//		boolean oneUserBrickExistsContextMenu = (ProjectManager.getInstance().getCurrentScript().getBrickList().size() == 7);
//		solo.sleep(500);
//		assertTrue("only 1 userbrick should exist in the script after copying via context menu, but that's not the case!", oneUserBrickExistsContextMenu);
//		solo.sleep(300);
//	}

//	public void testMoveUserBrickUpAndDown() throws InterruptedException {
//		solo.clickOnText(UiTestUtils.TEST_USER_BRICK_NAME);
//
//		String stringOnMove = solo.getCurrentActivity()
//				.getString(R.string.brick_context_dialog_move_brick);
//		solo.waitForText(stringOnMove);
//		solo.clickOnText(stringOnMove);
//
//		int[] location = UiTestUtils.dragFloatingBrick(solo, -3);
//		assertTrue("was not able to move the brick up", location != null);
//		solo.sleep(1000);
//
//		solo.clickOnText(UiTestUtils.TEST_USER_BRICK_NAME);
//		solo.waitForText(stringOnMove);
//		solo.clickOnText(stringOnMove);
//		location = UiTestUtils.dragFloatingBrick(solo, 3);
//		assertTrue("was not able to move the brick down", location != null);
//		solo.sleep(300);
//	}

	// delete a userbrick, go back to scripts and check if the deletion was updated
//	public void testDeleteUserBrickAndCheckIfScriptActivityUpdates() throws InterruptedException {
//		solo.clickOnText(UiTestUtils.TEST_USER_BRICK_NAME);
//
//		String stringOnShowSourceButton = solo.getCurrentActivity()
//				.getString(R.string.brick_context_dialog_show_source);
//		solo.waitForText(stringOnShowSourceButton);
//		solo.clickOnText(stringOnShowSourceButton);
//
//		boolean addBrickShowedUp = solo.waitForFragmentByTag(AddBrickFragment.ADD_BRICK_FRAGMENT_TAG, 2000);
//		if (!addBrickShowedUp) {
//			fail("addBrickFragment should have showed up");
//		}
//
//		UiTestUtils.deleteFirstUserBrick(solo, UiTestUtils.TEST_USER_BRICK_NAME);
//		solo.sleep(500);
//		solo.goBack();
//		solo.sleep(200);
//		Script currentScript = UiTestUtils.getProjectManager().getCurrentScript();
//		int indexOfUserBrickInScript = currentScript.containsBrickOfTypeReturnsFirstIndex(UserBrick.class);
//		assertTrue("current script should not contain a User Brick after we tried to delete one.",
//				indexOfUserBrickInScript == -1);
//	}

//	public void testUserBrickEditInstanceScriptChangesOtherInstanceScript() throws InterruptedException {
//		UiTestUtils.addNewBrick(solo, R.string.category_user_bricks, UiTestUtils.TEST_USER_BRICK_NAME, 0);
//
//		solo.sleep(1000);
//		// click on position x brick-heights above/below the place where the brick currently is
//		int[] location = UiTestUtils.dragFloatingBrick(solo, -1);
//		assertTrue("was not able to find the brick we just added: first user brick", location != null);
//		solo.sleep(4000);
//
//		Script currentScript = UiTestUtils.getProjectManager().getCurrentScript();
//		int indexOfUserBrickInScript = currentScript.containsBrickOfTypeReturnsFirstIndex(UserBrick.class);
//		assertTrue("current script should contain a User Brick after we tried to add one.",
//				indexOfUserBrickInScript != -1);
//
//		UserBrick userBrick = (UserBrick) currentScript.getBrick(indexOfUserBrickInScript);
//		assertTrue("we should be able to cast the brick we found to a User Brick.", userBrick != null);

//		UiTestUtils.showSourceAndEditBrick(UiTestUtils.TEST_USER_BRICK_NAME, solo);
//
//		// add a new brick to the internal script of the user brick
//		UiTestUtils.addNewBrick(solo, R.string.brick_change_y_by);
//
//		// place it
//		location = UiTestUtils.dragFloatingBrick(solo, 1);
//		assertTrue("was not able to find the brick we just added: brick inside user brick", location != null);
//		solo.sleep(1000);
//
//		// go back to normal script activity
//		solo.goBack();
//		solo.sleep(2000);
//		solo.goBack();
//		solo.sleep(2000);
//
//		UiTestUtils.addNewBrick(solo, R.string.category_user_bricks, UiTestUtils.TEST_USER_BRICK_NAME, 0);
//
//		location = UiTestUtils.dragFloatingBrick(solo, 1);
//		assertTrue("was not able to find the brick we just added: second user brick", location != null);
//
//		solo.sleep(2000);
//
//		// click on the location the brick was just dragged to.
//		solo.clickLongOnScreen(location[0], location[1], 10);
//
//		UiTestUtils.showSourceAndEditBrick(UiTestUtils.TEST_USER_BRICK_NAME, false, solo);
//
//		String brickAddedToUserBrickScriptName = solo.getCurrentActivity().getString(R.string.brick_change_y_by);
//		assertTrue("was not able to find the script we added to the other instance",
//				solo.searchText(brickAddedToUserBrickScriptName));
//	}
}
