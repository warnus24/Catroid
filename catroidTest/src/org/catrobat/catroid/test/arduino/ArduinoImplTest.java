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
package org.catrobat.catroid.test.arduino;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.test.AndroidTestCase;

import org.catrobat.catroid.R;
import org.catrobat.catroid.arduino.Arduino;
import org.catrobat.catroid.arduino.ArduinoImpl;
import org.catrobat.catroid.test.utils.BluetoothConnectionWrapper;
import org.catrobat.catroid.ui.SettingsActivity;

public class ArduinoImplTest extends AndroidTestCase {

	private Context applicationContext;
	private SharedPreferences preferences;

	private Arduino arduino;
	BluetoothConnectionWrapper connectionWrapper;

	private static final int PREFERENCES_SAVE_DELAY = 50;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		applicationContext = this.getContext().getApplicationContext();
		preferences = PreferenceManager.getDefaultSharedPreferences(applicationContext);

		arduino = new ArduinoImpl(this.applicationContext);
		connectionWrapper = new BluetoothConnectionWrapper();
		arduino.setConnection(connectionWrapper);
	}

	private void setSensor(SharedPreferences.Editor editor, String sensor, int sensorType) {
		editor.putString(sensor, applicationContext.getString(sensorType));
	}

	public void testSensorAssignment() throws InterruptedException {
		SharedPreferences.Editor editor = preferences.edit();
		editor.clear();

		setSensor(editor, SettingsActivity.ARDUINO_SENSOR_DIGITAL, R.string.formula_editor_function_arduino_read_pin_value_digital);
		setSensor(editor, SettingsActivity.ARDUINO_SENSOR_ANALOG, R.string.formula_editor_function_arduino_read_pin_value_analog);

		editor.apply();
		Thread.sleep(PREFERENCES_SAVE_DELAY); // Preferences need some time to get saved

		arduino.initialise();

		//ToDo: check if this test is needed
		assertNotNull("Reading digitial PIN 01 failed", arduino.getDigitalArduinoPin("01"));
		assertNotNull("Reading digitial PIN 03 failed", arduino.getDigitalArduinoPin("03"));
		assertNotNull("Reading digitial PIN 04 failed", arduino.getDigitalArduinoPin("04"));
		assertNotNull("Reading digitial PIN 05 failed", arduino.getDigitalArduinoPin("05"));
		assertNotNull("Reading digitial PIN 06 failed", arduino.getDigitalArduinoPin("06"));
		assertNotNull("Reading digitial PIN 08 failed", arduino.getDigitalArduinoPin("08"));
		assertNotNull("Reading digitial PIN 09 failed", arduino.getDigitalArduinoPin("09"));
		assertNotNull("Reading digitial PIN 10 failed", arduino.getDigitalArduinoPin("10"));
		assertNotNull("Reading digitial PIN 11 failed", arduino.getDigitalArduinoPin("11"));
		assertNotNull("Reading digitial PIN 12 failed", arduino.getDigitalArduinoPin("12"));
		assertNotNull("Reading digitial PIN 13 failed", arduino.getDigitalArduinoPin("13"));

		assertNotNull("Reading analog PIN 00 failed", arduino.getAnalogArduinoPin("00"));
		assertNotNull("Reading analog PIN 01 failed", arduino.getAnalogArduinoPin("01"));
		assertNotNull("Reading analog PIN 02 failed", arduino.getAnalogArduinoPin("02"));
		assertNotNull("Reading analog PIN 03 failed", arduino.getAnalogArduinoPin("03"));
		assertNotNull("Reading analog PIN 04 failed", arduino.getAnalogArduinoPin("04"));
		assertNotNull("Reading analog PIN 05 failed", arduino.getAnalogArduinoPin("05"));

	}

	//ToDo: same for set to LOW
	public void testSetPinHighTest() {
		byte[] message = {1,3,'H'};

		arduino.initialise();
		arduino.setDigitalArduinoPin("13",'H');

		byte[] setOutputState = connectionWrapper.getNextSentMessage(0, 2);

		assertEquals("Expected message is different", message, setOutputState);
	}
}
