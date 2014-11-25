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

import org.catrobat.catroid.R;
import org.catrobat.catroid.lego.mindstorm.nxt.LegoNXT;
import org.catrobat.catroid.lego.mindstorm.nxt.LegoNXTImpl;
import org.catrobat.catroid.lego.mindstorm.nxt.sensors.NXTI2CUltraSonicSensor;
import org.catrobat.catroid.lego.mindstorm.nxt.sensors.NXTLightSensor;
import org.catrobat.catroid.lego.mindstorm.nxt.sensors.NXTSoundSensor;
import org.catrobat.catroid.lego.mindstorm.nxt.sensors.NXTTouchSensor;
import org.catrobat.catroid.test.utils.BluetoothConnectionWrapper;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.SettingsActivity;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;

public class LegoNXTPreferencesTests extends BaseActivityInstrumentationTestCase<MainMenuActivity> {

	private SharedPreferences mPreferences;
	private Context mApplicationContext;

	public LegoNXTPreferencesTests() {
		super(MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		UiTestUtils.prepareStageForTest();

		mApplicationContext = getInstrumentation().getTargetContext().getApplicationContext();
		mPreferences = PreferenceManager.getDefaultSharedPreferences(mApplicationContext);
		mPreferences.edit().clear();
		mPreferences.edit().apply();
	}

	public void testNXTBricksEnabled() throws InterruptedException {
		boolean nxtBricksEnabledStart = mPreferences.getBoolean("setting_mindstorms_enable_nxt_bricks", false);

		solo.clickOnActionBarItem(R.id.settings);

		String preferenceTitle = solo.getString(R.string.preference_title_enable_mindstorms_bricks);
		solo.waitForText(preferenceTitle);
		solo.clickOnText(preferenceTitle);
		solo.waitForText(solo.getString(R.string.preference_title_mindstorms_sensors));
		solo.clickOnText(preferenceTitle);

		solo.goBack();
		solo.goBack();

		boolean enableNXTBricks = mPreferences.getBoolean("setting_mindstorms_enable_nxt_bricks", nxtBricksEnabledStart);
		assertTrue("NXT category brick ON/OFF not changed!", nxtBricksEnabledStart != enableNXTBricks);

		solo.waitForText(solo.getString(R.string.main_menu_new));
		solo.clickOnText(solo.getString(R.string.main_menu_new));
		solo.enterText(0, "testNXTBricksEnabled");
		solo.waitForText(solo.getString(R.string.ok));
		solo.clickOnText(solo.getString(R.string.ok));
		solo.waitForText(solo.getString(R.string.background));
		solo.clickOnText(solo.getString(R.string.background));
		solo.waitForText(solo.getString(R.string.scripts));
		solo.clickOnText(solo.getString(R.string.scripts));
		UiTestUtils.clickOnBottomBar(solo, R.id.button_add);

		solo.sleep(300);

		if(enableNXTBricks) {
			assertTrue("NXT category brick shown!",solo.searchText(solo.getString(R.string.category_lego_nxt)));
		} else {
			assertFalse("NXT category brick not shown!", solo.searchText(solo.getString(R.string.category_lego_nxt)));
		}

		solo.clickOnActionBarItem(R.id.settings);

		solo.waitForText(preferenceTitle);
		solo.clickOnText(preferenceTitle);
		solo.waitForText(solo.getString(R.string.preference_title_mindstorms_sensors));
		solo.clickOnText(preferenceTitle);

		solo.goBack();
		solo.goBack();

		enableNXTBricks = mPreferences.getBoolean("setting_mindstorms_enable_nxt_bricks", !nxtBricksEnabledStart);
		assertTrue("SecondCheck: NXT category brick ON/OFF not changed!", nxtBricksEnabledStart == enableNXTBricks);

		solo.sleep(300);

		if(enableNXTBricks) {
			assertTrue("SecondCheck: NXT category brick shown!", solo.searchText(solo.getString(R.string.category_lego_nxt)));
		} else {
			assertFalse("SecondCheck: NXT category brick not shown!", solo.searchText(solo.getString(R.string.category_lego_nxt)));
		}
	}

	public void testNXTAllBricksAvailable() throws InterruptedException {
		boolean nxtBricksEnabledStart = mPreferences.getBoolean("setting_mindstorms_enable_nxt_bricks", false);

		if(!nxtBricksEnabledStart) {
			solo.clickOnActionBarItem(R.id.settings);

			String preferenceTitle = solo.getString(R.string.preference_title_enable_mindstorms_bricks);
			solo.waitForText(preferenceTitle);
			solo.clickOnText(preferenceTitle);
			solo.waitForText(solo.getString(R.string.preference_title_mindstorms_sensors));
			solo.clickOnText(preferenceTitle);

			solo.goBack();
			solo.goBack();
		}

		solo.waitForText(solo.getString(R.string.main_menu_new));
		solo.clickOnText(solo.getString(R.string.main_menu_new));
		solo.enterText(0, "testNXTAllBricksAvailable");
		solo.waitForText(solo.getString(R.string.ok));
		solo.clickOnText(solo.getString(R.string.ok));
		solo.waitForText(solo.getString(R.string.background));
		solo.clickOnText(solo.getString(R.string.background));
		solo.waitForText(solo.getString(R.string.scripts));
		solo.clickOnText(solo.getString(R.string.scripts));
		UiTestUtils.clickOnBottomBar(solo, R.id.button_add);
		solo.waitForText(solo.getString(R.string.category_lego_nxt));
		solo.clickOnText(solo.getString(R.string.category_lego_nxt));

		solo.sleep(300);

		assertTrue("NXT turn motor brick not available!",solo.searchText(solo.getString(R.string.brick_motor_turn_angle)));
		assertTrue("NXT stop motor brick not available!",solo.searchText(solo.getString(R.string.motor_stop)));
		assertTrue("NXT move motor brick not available!",solo.searchText(solo.getString(R.string.brick_motor_action)));
		assertTrue("NXT play tone brick not available!",solo.searchText(solo.getString(R.string.nxt_play_tone)));
	}

	public void testNXTSensorsSetCorrectly() throws InterruptedException {
		LegoNXT nxt = new LegoNXTImpl(mApplicationContext);
		BluetoothConnectionWrapper connectionWrapper = connectionWrapper = new BluetoothConnectionWrapper();
		nxt.setConnection(connectionWrapper);

		boolean nxtBricksEnabledStart = mPreferences.getBoolean("setting_mindstorms_enable_nxt_bricks", false);

		solo.clickOnActionBarItem(R.id.settings);

		String preferenceTitle = solo.getString(R.string.preference_title_enable_mindstorms_bricks);
		solo.waitForText(preferenceTitle);
		solo.clickOnText(preferenceTitle);
		solo.waitForText(solo.getString(R.string.preference_title_mindstorms_sensors));

		if(!nxtBricksEnabledStart) {
			solo.clickOnText(preferenceTitle);
		}

		solo.clickOnText(solo.getString(R.string.nxt_choose_sensor_1));
		solo.waitForText(solo.getString(R.string.nxt_no_sensor));
		solo.clickOnText(solo.getString(R.string.nxt_sensor_light));
		solo.clickOnText(solo.getString(R.string.nxt_choose_sensor_2));
		solo.waitForText(solo.getString(R.string.nxt_no_sensor));
		solo.clickOnText(solo.getString(R.string.nxt_sensor_touch));
		solo.clickOnText(solo.getString(R.string.nxt_choose_sensor_3));
		solo.waitForText(solo.getString(R.string.nxt_no_sensor));
		solo.clickOnText(solo.getString(R.string.nxt_sensor_touch));
		solo.clickOnText(solo.getString(R.string.nxt_choose_sensor_4));
		solo.waitForText(solo.getString(R.string.nxt_no_sensor));
		solo.clickOnText(solo.getString(R.string.nxt_sensor_ultrasonic));

		solo.goBack();
		solo.goBack();

		String sensor = mPreferences.getString("setting_mindstorms_nxt_sensor_1", solo.getString(R.string.nxt_no_sensor));
		assertEquals("NXT sensor 1 not set correctly!", sensor, solo.getString(R.string.nxt_sensor_light));
		sensor = mPreferences.getString("setting_mindstorms_nxt_sensor_2", solo.getString(R.string.nxt_no_sensor));
		assertEquals("NXT sensor 2 not set correctly!", sensor, solo.getString(R.string.nxt_sensor_touch));
		sensor = mPreferences.getString("setting_mindstorms_nxt_sensor_3", solo.getString(R.string.nxt_no_sensor));
		assertEquals("NXT sensor 3 not set correctly!", sensor, solo.getString(R.string.nxt_sensor_touch));
		sensor = mPreferences.getString("setting_mindstorms_nxt_sensor_4", solo.getString(R.string.nxt_no_sensor));
		assertEquals("NXT sensor 4 not set correctly!", sensor, solo.getString(R.string.nxt_sensor_ultrasonic));

		nxt.initialise();

		assertNotNull("Sensor 1 not initialized correctly", nxt.getSensor1());
		assertTrue("Sensor 1 is of wrong instance, SensorFactory may has an error",
				nxt.getSensor1() instanceof NXTLightSensor);

		assertNotNull("Sensor 2 not initialized correctly", nxt.getSensor2());
		assertTrue("Sensor 2 is of wrong instance, SensorFactory may has an error",
				nxt.getSensor2() instanceof NXTTouchSensor);

		assertNotNull("Sensor 3 not initialized correctly", nxt.getSensor3());
		assertTrue("Sensor 3 is of wrong instance, SensorFactory may has an error",
				nxt.getSensor3() instanceof NXTTouchSensor);

		assertNotNull("Sensor 4 not initialized correctly", nxt.getSensor4());
		assertTrue("Sensor 4 is of wrong instance, SensorFactory may has an error",
				nxt.getSensor4() instanceof NXTI2CUltraSonicSensor);

		solo.clickOnActionBarItem(R.id.settings);
		solo.waitForText(preferenceTitle);
		solo.clickOnText(preferenceTitle);
		solo.waitForText(solo.getString(R.string.preference_title_mindstorms_sensors));
		solo.clickOnText(solo.getString(R.string.nxt_choose_sensor_1));
		solo.waitForText(solo.getString(R.string.nxt_no_sensor));
		solo.clickOnText(solo.getString(R.string.nxt_sensor_touch));
		solo.clickOnText(solo.getString(R.string.nxt_choose_sensor_2));
		solo.waitForText(solo.getString(R.string.nxt_no_sensor));
		solo.clickOnText(solo.getString(R.string.nxt_sensor_sound));
		solo.clickOnText(solo.getString(R.string.nxt_choose_sensor_3));
		solo.waitForText(solo.getString(R.string.nxt_no_sensor));
		solo.clickOnText(solo.getString(R.string.nxt_sensor_light));
		solo.clickOnText(solo.getString(R.string.nxt_choose_sensor_4));
		solo.waitForText(solo.getString(R.string.nxt_no_sensor));
		solo.clickOnText(solo.getString(R.string.nxt_sensor_ultrasonic));

		solo.goBack();
		solo.goBack();

		sensor = mPreferences.getString("setting_mindstorms_nxt_sensor_1", solo.getString(R.string.nxt_no_sensor));
		assertEquals("NXT sensor 1 not set correctly!", sensor, solo.getString(R.string.nxt_sensor_touch));
		sensor = mPreferences.getString("setting_mindstorms_nxt_sensor_2", solo.getString(R.string.nxt_no_sensor));
		assertEquals("NXT sensor 2 not set correctly!", sensor, solo.getString(R.string.nxt_sensor_sound));
		sensor = mPreferences.getString("setting_mindstorms_nxt_sensor_3", solo.getString(R.string.nxt_no_sensor));
		assertEquals("NXT sensor 3 not set correctly!", sensor, solo.getString(R.string.nxt_sensor_light));
		sensor = mPreferences.getString("setting_mindstorms_nxt_sensor_4", solo.getString(R.string.nxt_no_sensor));
		assertEquals("NXT sensor 4 not set correctly!", sensor, solo.getString(R.string.nxt_sensor_ultrasonic));

		nxt.initialise();

		assertNotNull("Sensor 1 not reinitialized correctly", nxt.getSensor1());
		assertTrue("Sensor 1 is of wrong instance now, SensorFactory may has an error",
				nxt.getSensor1() instanceof NXTTouchSensor);

		assertNotNull("Sensor 2 not reinitialized correctly", nxt.getSensor2());
		assertTrue("Sensor 2 is of wrong instance now, SensorFactory may has an error",
				nxt.getSensor2() instanceof NXTSoundSensor);

		assertNotNull("Sensor 3 not reinitialized correctly", nxt.getSensor3());
		assertTrue("Sensor 3 is of wrong instance now, SensorFactory may has an error",
				nxt.getSensor3() instanceof NXTLightSensor);

		assertNotNull("Sensor 4 not reinitialized correctly", nxt.getSensor4());
		assertTrue("Sensor 4 is of wrong instance now, SensorFactory may has an error",
				nxt.getSensor4() instanceof NXTI2CUltraSonicSensor);
	}

	public void testNXTSensorsAvailable() throws InterruptedException {
		boolean nxtBricksEnabledStart = mPreferences.getBoolean("setting_mindstorms_enable_nxt_bricks", false);

		solo.clickOnActionBarItem(R.id.settings);

		String preferenceTitle = solo.getString(R.string.preference_title_enable_mindstorms_bricks);
		solo.waitForText(preferenceTitle);
		solo.clickOnText(preferenceTitle);
		solo.waitForText(solo.getString(R.string.preference_title_mindstorms_sensors));

		if(!nxtBricksEnabledStart) {
			solo.clickOnText(preferenceTitle);
		}

		solo.clickOnText(solo.getString(R.string.nxt_choose_sensor_1));
		solo.waitForText(solo.getString(R.string.nxt_no_sensor));
		solo.clickOnText(solo.getString(R.string.nxt_sensor_touch));
		solo.clickOnText(solo.getString(R.string.nxt_choose_sensor_2));
		solo.waitForText(solo.getString(R.string.nxt_no_sensor));
		solo.clickOnText(solo.getString(R.string.nxt_sensor_sound));
		solo.clickOnText(solo.getString(R.string.nxt_choose_sensor_3));
		solo.waitForText(solo.getString(R.string.nxt_no_sensor));
		solo.clickOnText(solo.getString(R.string.nxt_sensor_light));
		solo.clickOnText(solo.getString(R.string.nxt_choose_sensor_4));
		solo.waitForText(solo.getString(R.string.nxt_no_sensor));
		solo.clickOnText(solo.getString(R.string.nxt_sensor_ultrasonic));

		solo.goBack();
		solo.goBack();

		solo.waitForText(solo.getString(R.string.main_menu_new));
		solo.clickOnText(solo.getString(R.string.main_menu_new));
		solo.enterText(0, "testNXTSensorsAvailable");
		solo.waitForText(solo.getString(R.string.ok));
		solo.clickOnText(solo.getString(R.string.ok));
		solo.waitForText(solo.getString(R.string.background));
		solo.clickOnText(solo.getString(R.string.background));
		solo.waitForText(solo.getString(R.string.scripts));
		solo.clickOnText(solo.getString(R.string.scripts));
		UiTestUtils.clickOnBottomBar(solo, R.id.button_add);
		solo.waitForText(solo.getString(R.string.category_lego_nxt));
		solo.clickOnText(solo.getString(R.string.category_lego_nxt));
		solo.waitForText(solo.getString(R.string.nxt_play_tone));
		solo.clickOnText(solo.getString(R.string.nxt_play_tone));
		solo.sleep(300);
		solo.clickOnView(solo.getViews().get(0));
		solo.sleep(300);
		solo.clickOnText("1");
		solo.waitForText(solo.getString(R.string.formula_editor_sensors));
		solo.clickOnText(solo.getString(R.string.formula_editor_sensors));

		solo.sleep(300);

		assertTrue("NXT sensor 1 not available!",solo.searchText(solo.getString(R.string.formula_editor_sensor_lego_nxt_1)));
		assertTrue("NXT sensor 2 not available!",solo.searchText(solo.getString(R.string.formula_editor_sensor_lego_nxt_2)));
		assertTrue("NXT sensor 3 not available!",solo.searchText(solo.getString(R.string.formula_editor_sensor_lego_nxt_3)));
		assertTrue("NXT sensor 4 not available!",solo.searchText(solo.getString(R.string.formula_editor_sensor_lego_nxt_4)));
	}
}
