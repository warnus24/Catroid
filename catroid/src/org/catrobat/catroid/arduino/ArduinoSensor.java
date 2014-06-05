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
package org.catrobat.catroid.arduino;

import android.os.Handler;
import android.util.Log;

import org.catrobat.catroid.formulaeditor.SensorCustomEvent;
import org.catrobat.catroid.formulaeditor.SensorCustomEventListener;
import org.catrobat.catroid.formulaeditor.Sensors;

import java.util.ArrayList;

public final class ArduinoSensor {

	private static ArduinoSensor instance = null;
	private static final int ARDUINO_ANALOG_SENSOR_UPDATE_INTERVAL = 50;

	private ArrayList<SensorCustomEventListener> listenerList = new ArrayList<SensorCustomEventListener>();

	private Handler handler;
	private float[] incomingSensorValue = new float[2];
	private static boolean debugOutput = false;

	public static final String KEY_SETTINGS_ARDUINO_BRICKS = "setting_arduino_bricks";
	public boolean usingArduinoBricks = false;
	public boolean usingDigitalArduinoSensor = true;

	//Periodic update the distance_value
	Runnable updateSensorValue = new Runnable() {
		@Override
		public void run() {

			if (usingDigitalArduinoSensor) {
				incomingSensorValue[0] = ArduinoSensorData.getInstance().getArduinoDigitalSensor();
			} else {
				incomingSensorValue[0] = ArduinoSensorData.getInstance().getArduinoAnalogSensor();
			}

			if (debugOutput == true) {
				Log.d("ArduinoSensor", "DigitalSensorValue: "
						+ ArduinoSensorData.getInstance().getArduinoDigitalSensor());
				Log.d("ArduinoSensor", "AnalogSensorValue: " + ArduinoSensorData.getInstance().getArduinoAnalogSensor());
			}

			SensorCustomEvent eventDigtial = new SensorCustomEvent(Sensors.ARDUINODIGITAL, incomingSensorValue);
			SensorCustomEvent eventAnalog = new SensorCustomEvent(Sensors.ARDUINOANALOG, incomingSensorValue);
			for (SensorCustomEventListener listener : listenerList) {
				listener.onCustomSensorChanged(eventDigtial);
				listener.onCustomSensorChanged(eventAnalog);
			}
			handler.postDelayed(updateSensorValue, ARDUINO_ANALOG_SENSOR_UPDATE_INTERVAL);
		}
	};

	private ArduinoSensor() {
		handler = new Handler();
	};

	public static ArduinoSensor getArduinoSensorInstance() {
		if (instance == null) {
			instance = new ArduinoSensor();
		}
		return instance;
	}

	public synchronized boolean registerListener(SensorCustomEventListener listener) {

		if (usingArduinoBricks == false) {
			return false;
		}

		if (listenerList.contains(listener)) {
			return true;
		}
		listenerList.add(listener);

		try {
			updateSensorValue.run();
		} catch (Exception e) {
			Log.w(ArduinoSensor.class.getSimpleName(), "Could not register SensorCustomEventListener", e);
			listenerList.remove(listener);
			return false;
		}

		return true;
	}

	public synchronized void unregisterListener(SensorCustomEventListener listener) {
		if (listenerList.contains(listener)) {
			listenerList.remove(listener);
			if (listenerList.size() == 0) {
				handler.removeCallbacks(updateSensorValue);
			}

		}
	}

	//if arduino bricks are used, set the variable true
	public void setAreArduinoBricksUsed(boolean status) {
		usingArduinoBricks = status;
	}

	public boolean getAreArduinoBricksUsed() {
		return usingArduinoBricks;
	}
}
