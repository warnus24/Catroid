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

package org.catrobat.catroid.uitest.mindstorms.nxt;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.robotium.solo.Solo;

import org.catrobat.catroid.R;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;

/**
 * Created by gerulf on 18.11.14.
 */
public class LegoNXTPreferencesTests extends BaseActivityInstrumentationTestCase<MainMenuActivity> {

	private SharedPreferences mPreferences;

	public LegoNXTPreferencesTests() {
		super(MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		Context applicationContext = getInstrumentation().getTargetContext().getApplicationContext();
		mPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext);
		mPreferences.edit().clear();
		mPreferences.edit().apply();
		UiTestUtils.prepareStageForTest();
	}

	public void testNXTPreferencesOnOff() throws InterruptedException {

		boolean nxtPreferencesOnOff = mPreferences.getBoolean("setting_mindstorms_enable_nxt_bricks", false);

		String preferenceTitle = solo.getString(R.string.preference_title_enable_mindstorms_bricks);
		solo.clickOnActionBarItem(R.id.settings);
		solo.waitForText(preferenceTitle);
		solo.clickOnText(preferenceTitle);
		solo.waitForText(preferenceTitle);
		solo.clickOnText(preferenceTitle);

		solo.goBack();
		solo.goBack();

		boolean enableNXTBricks = mPreferences.getBoolean("setting_mindstorms_enable_nxt_bricks", nxtPreferencesOnOff);

		assertTrue("NXT Category Brick ON/OFF not changed", nxtPreferencesOnOff != enableNXTBricks);

		solo.clickOnText(solo.getString(R.string.main_menu_new));
		solo.enterText(0, "RobotiumTest");
		solo.clickOnText(solo.getString(R.string.ok));
		solo.clickOnText(solo.getString(R.string.background));
		solo.clickOnText(solo.getString(R.string.scripts));
		UiTestUtils.clickOnBottomBar(solo, R.id.button_add);

		if(enableNXTBricks)
		{
			assertTrue("NXT Category Brick shown.",solo.searchText(solo.getString(R.string.category_lego_nxt)));
		}
		else
		{
			assertFalse("NXT Category Brick not shown.", solo.searchText(solo.getString(R.string.category_lego_nxt)));
		}

		solo.clickOnActionBarItem(R.id.settings);
		solo.waitForText(preferenceTitle);
		solo.clickOnText(preferenceTitle);
		solo.waitForText(preferenceTitle);
		solo.clickOnText(preferenceTitle);

		solo.goBack();
		solo.goBack();

		enableNXTBricks = mPreferences.getBoolean("setting_mindstorms_enable_nxt_bricks", !nxtPreferencesOnOff);
		assertTrue("SecondCheck: NXT Category Brick ON/OFF not changed", nxtPreferencesOnOff == enableNXTBricks);

		if(enableNXTBricks)
		{
			assertTrue("SecondCheck: NXT Category Brick shown.", solo.searchText(solo.getString(R.string.category_lego_nxt)));
		}
		else
		{
			assertFalse("SecondCheck: NXT Category Brick not shown.", solo.searchText(solo.getString(R.string.category_lego_nxt)));
		}
	}
}
