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

import com.robotium.solo.Solo;

import org.catrobat.catroid.R;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;

/**
 * Created by gerulf on 18.11.14.
 */
public class LegoNXTPreferencesTests extends BaseActivityInstrumentationTestCase<MainMenuActivity> {

	public LegoNXTPreferencesTests() {
		super(MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		UiTestUtils.prepareStageForTest();
	}

	public void testNXTPreferencesOnOff() {
		// Button: Enable Lego Mindstorm Bricks
		solo.clickOnActionBarItem(R.id.settings);
		solo.clickOnButton(R.string.settings);
		//UiTestUtils.clickOnExactText(solo, "Enable Lego Mindstorm Bricks");
		//solo.clickOnButton(R.string.preference_title_enable_mindstorms_bricks);
		//solo.clickOnButton(R.string.preference_title_enable_mindstorms_bricks);
		//solo.clickOnCheckBox(R.string.preference_title_enable_mindstorm_bricks);
		//solo.clickOnCheckBox(R.string.preference_title_enable_mindstorm_bricks);

		//solo.clickOnToggleButton("Enable Lego Mindstorm Bricks");
		solo.clickLongOnText("Enable Lego Mindstorm Bricks");



		//solo.clickOnButton(applicationContext.getString(R.string.preference_title_enable_mindstorms_bricks));



	}



}
