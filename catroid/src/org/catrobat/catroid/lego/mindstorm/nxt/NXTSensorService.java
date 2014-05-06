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
package org.catrobat.catroid.lego.mindstorm.nxt;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;

import org.catrobat.catroid.R;
import org.catrobat.catroid.formulaeditor.Sensors;
import org.catrobat.catroid.lego.mindstorm.MindstormConnection;
import org.catrobat.catroid.lego.mindstorm.MindstormException;
import org.catrobat.catroid.lego.mindstorm.nxt.sensors.NXTI2CUltraSonicSensor;
import org.catrobat.catroid.lego.mindstorm.nxt.sensors.NXTLightSensor;
import org.catrobat.catroid.lego.mindstorm.nxt.sensors.NXTSensor;
import org.catrobat.catroid.lego.mindstorm.nxt.sensors.NXTSoundSensor;
import org.catrobat.catroid.lego.mindstorm.nxt.sensors.NXTTouchSensor;
import org.catrobat.catroid.ui.SettingsActivity;

import java.util.LinkedList;
import java.util.List;

public class NXTSensorService implements SharedPreferences.OnSharedPreferenceChangeListener {

	private static final String TAG = NXTSensorService.class.getSimpleName();

	SharedPreferences preferences;
	MindstormConnection connection;
	Context context;

	public NXTSensorService(Context context, MindstormConnection connection) {
		preferences = PreferenceManager.getDefaultSharedPreferences(context);
		preferences.registerOnSharedPreferenceChangeListener(this);
		this.connection = connection;
		this.context = context;
	}

	private int lightValue = 0;
	private int soundValue = 0;
	private int touchValue = 0;
	private int ultrasonicValue = 0;

	private static final int INTERVAL = 2000;
	private static final int INIT_INTERVAL = 2000;

	public int getValue(Sensors sensors) {
		switch (sensors) {
			case LEGO_NXT_LIGHT:
				return lightValue;
			case LEGO_NXT_SOUND:
				return soundValue;
			case LEGO_NXT_TOUCH:
				return touchValue;
			case LEGO_NXT_ULTRASONIC:
				return ultrasonicValue;
		}

		return -1;
	}

	Handler sensorHandler = new Handler();
	NXTSensor[] activeSensors = new NXTSensor[4];
	boolean runHandler = false;

	public void listenToSensors(boolean b) {
		synchronized (this) {
			if (b && !runHandler) {
				sensorHandler.postDelayed(getSensorValuesPeriodically, INTERVAL);
			}

			runHandler = b;
		}
	}


	private Runnable getSensorValuesPeriodically = new Runnable() {

		@Override
		public void run() {
			synchronized (activeSensors) {
				for (NXTSensor sensor : activeSensors) {
					if (sensor == null) {
						continue;
					}

					if (sensor instanceof NXTSoundSensor) {
						soundValue = sensor.getValue();
						Log.d(TAG, "Time for SOUND sensor: " + soundValue);
					}
					else if (sensor instanceof NXTLightSensor) {
						lightValue = sensor.getValue();
						Log.d(TAG, "Time for LIGHT sensor: " + lightValue);
					}
					else if (sensor instanceof NXTTouchSensor) {
						touchValue = sensor.getValue();
						Log.d(TAG, "Time for TOUCH sensor: " + touchValue);
					}
					else if (sensor instanceof NXTI2CUltraSonicSensor) {
						ultrasonicValue = sensor.getValue();
						Log.d(TAG, "Time for SONAR sensor: " + ultrasonicValue);
					}
				}
			}
			synchronized (this) {
				if (runHandler) {
					sensorHandler.postDelayed(getSensorValuesPeriodically, INTERVAL);
				}
			}
		}
	};

	public NXTSensor createSensor1() {
		String sensorTypeName = preferences.getString(SettingsActivity.NXT_SENSOR_1, "");
		synchronized (activeSensors) {
			activeSensors[0] = createSensor(sensorTypeName, 0);
			return activeSensors[0];
		}
	}

	public NXTSensor createSensor2() {
		String sensorTypeName = preferences.getString(SettingsActivity.NXT_SENSOR_2, "");
		synchronized (activeSensors) {
			activeSensors[1] = createSensor(sensorTypeName, 1);
			return activeSensors[1];
		}
	}

	public NXTSensor createSensor3() {
		String sensorTypeName = preferences.getString(SettingsActivity.NXT_SENSOR_3, "");
		synchronized (activeSensors) {
			activeSensors[2] = createSensor(sensorTypeName, 2);
			return activeSensors[2];
		}
	}

	public NXTSensor createSensor4() {
		String sensorTypeName = preferences.getString(SettingsActivity.NXT_SENSOR_4, "");
		synchronized (activeSensors) {
			activeSensors[3] = createSensor(sensorTypeName, 3);
			return activeSensors[3];
		}
	}

	private boolean equals(String name, int type) {
		return name.equals(context.getString(type));
	}

	private NXTSensor createSensor(String sensorTypeName, int port) {

		if (equals(sensorTypeName, R.string.nxt_no_sensor)) {
			return null;
		}

		synchronized (this) {
			if (!runHandler) {
				runHandler = true;
				sensorHandler.postDelayed(getSensorValuesPeriodically, INIT_INTERVAL);
			}
		}

		if (equals(sensorTypeName, R.string.nxt_sensor_touch)) {
			return new NXTTouchSensor(port, connection);
		}

		if (equals(sensorTypeName, R.string.nxt_sensor_sound)) {
			return new NXTSoundSensor(port, connection);
		}

		if (equals(sensorTypeName, R.string.nxt_sensor_light)) {
			return new NXTLightSensor(port, connection);
		}

		if (equals(sensorTypeName, R.string.nxt_sensor_ultrasonic)) {
			return new NXTI2CUltraSonicSensor(connection);
		}

		throw new MindstormException("No valid sensor found!"); // Should never occur
	}

	List<OnSensorChangedListener> sensorChangedListeners = new LinkedList<OnSensorChangedListener>();

	public void registerOnSensorChangedListener(OnSensorChangedListener listener) {
		sensorChangedListeners.add(listener);
	}

	private boolean preferenceIsSensorPreference(String preference) {
		return (preference.equals(SettingsActivity.NXT_SENSOR_1) ||
				preference.equals(SettingsActivity.NXT_SENSOR_2) ||
				preference.equals(SettingsActivity.NXT_SENSOR_3) ||
				preference.equals(SettingsActivity.NXT_SENSOR_4));
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String preference) {

		if (!preferenceIsSensorPreference(preference)) {
			return;
		}

		for (OnSensorChangedListener listener : sensorChangedListeners) {
			if (listener != null)
				listener.onSensorChanged();
		}
	}

	public interface OnSensorChangedListener {
		public void onSensorChanged();
	}
}
