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

package org.catrobat.catroid.uitest.ui.fragment;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.NfcTagData;
import org.catrobat.catroid.common.ScreenValues;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.WhenScript;
import org.catrobat.catroid.nfc.NfcHandler;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.adapter.NfcTagAdapter;
import org.catrobat.catroid.ui.fragment.NfcTagFragment;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;
import org.catrobat.catroid.uitest.util.ArduinoConnection;
import org.catrobat.catroid.utils.Utils;

import java.util.ArrayList;

public class NfcTagFragmentHardwareTest extends BaseActivityInstrumentationTestCase<MainMenuActivity> {

	private static final int NUMBER_EMULATE_TRIES = 3;
	private static final int TIME_TO_WAIT = 50;

	private static final String FIRST_TEST_TAG_NAME = "tagNameTest";
	private static final String FIRST_TEST_TAG_ID = "111";

	private static final String SECOND_TEST_TAG_NAME = "tagNameTest2";
	private static final String SECOND_TEST_TAG_ID = "222";

	private NfcTagData tagData;
	private NfcTagData tagData2;

	private ArrayList<NfcTagData> tagDataList;

	private ProjectManager projectManager;

	public NfcTagFragmentHardwareTest() {
		super(MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		UiTestUtils.createTestProject();
		UiTestUtils.prepareStageForTest();

		projectManager = ProjectManager.getInstance();
		tagDataList = projectManager.getCurrentSprite().getNfcTagList();

		tagData = new NfcTagData();
		tagData.setNfcTagName(FIRST_TEST_TAG_NAME);
		tagData.setNfcTagUid(FIRST_TEST_TAG_ID);
		tagDataList.add(tagData);

		tagData2 = new NfcTagData();
		tagData2.setNfcTagName(SECOND_TEST_TAG_NAME);
		tagData2.setNfcTagUid(SECOND_TEST_TAG_ID);
		tagDataList.add(tagData2);

		Utils.updateScreenWidthAndHeight(solo.getCurrentActivity());
		projectManager.getCurrentProject().getXmlHeader().virtualScreenWidth = ScreenValues.SCREEN_WIDTH;
		projectManager.getCurrentProject().getXmlHeader().virtualScreenHeight = ScreenValues.SCREEN_HEIGHT;

		UiTestUtils.getIntoNfcTagsFromMainMenu(solo, true);

		if (getNfcTagAdapter().getShowDetails()) {
			solo.clickOnMenuItem(solo.getString(R.string.hide_details), true);
			solo.sleep(TIME_TO_WAIT);
		}
	}

	public void testNfcUid() {
		int emulateUid = 0x123456;
		byte[] expectedUid = { (byte) 0x08, 0x12, 0x34, 0x56 }; // first byte is fixed to 0x08

		solo.sleep(6000);
		//ArduinoConnection ac = new ArduinoConnection("192.168.0.166", 6789);
		ArduinoConnection ac = new ArduinoConnection("129.27.202.103", 6789);

		try {
			System.out.println("starting emulation");
			boolean emulateOk = false;
			for (int i = 0; i < NUMBER_EMULATE_TRIES && emulateOk == false; i++) {
				emulateOk = ac.nfcEmulateTag(emulateUid, false);
			}
			System.out.println("emulation ended");
			assertTrue("Arduino timed out when emulating nfc tag. (no read from emulated tag occured)", emulateOk);
		} catch (Exception e) {
			fail("Connection or communication to arduino failed.");
			e.printStackTrace();
		}

		solo.clickOnScreen(200, 200);
		solo.sleep(2000);

		//assertEquals("uid does not match!", NfcHandler.convertByteArrayToDouble(expectedUid), userVariable.getValue());
		//assertEquals("uid_sensor was not resetted to zero", 0.0, NfcHandler.getInstance().getAndResetUid());

	}

	private NfcTagFragment getNfcTagFragment() {
		ScriptActivity activity = (ScriptActivity) solo.getCurrentActivity();
		return (NfcTagFragment) activity.getFragment(ScriptActivity.FRAGMENT_NFCTAGS);
	}

	private NfcTagAdapter getNfcTagAdapter() {
		return (NfcTagAdapter) getNfcTagFragment().getListAdapter();
	}
}
