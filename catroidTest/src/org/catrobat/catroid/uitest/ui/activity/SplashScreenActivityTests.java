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

package org.catrobat.catroid.test.standalone;

import org.catrobat.catroid.R;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;

public class SplashScreenActivityTests extends BaseActivityInstrumentationTestCase<MainMenuActivity> {
	public SplashScreenActivityTests() {
		super(MainMenuActivity.class);
	}

	public void testSplashScreenOccurs() {
		assertTrue("MainMenuActivity not found", solo.waitForActivity(MainMenuActivity.class));
		assertTrue("SplashScreen layout not found", solo.waitForView(R.layout.activity_main_menu_splashscreen));
	}

	public void testSplashScreenDisappear() {
		solo.waitForActivity(StageActivity.class);
		solo.assertCurrentActivity("SplashScreen does not disappear", StageActivity.class);
	}
}
