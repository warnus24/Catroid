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
package org.catrobat.catroid.lego.mindstorm.nxt.sensors;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.CatrobatService;
import org.catrobat.catroid.lego.mindstorm.MindstormConnection;
import org.catrobat.catroid.lego.mindstorm.MindstormException;
import org.catrobat.catroid.ui.SettingsActivity;
import org.catrobat.catroid.utils.Stopwatch;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class NXTSensorService implements CatrobatService, SharedPreferences.OnSharedPreferenceChangeListener {

	private static final String TAG = NXTSensorService.class.getSimpleName();

    private SensorRegistry sensorRegistry;

	SharedPreferences preferences;
	MindstormConnection connection;
	Context context;

	PausableScheduledThreadPoolExecutor sensorScheduler;

	public NXTSensorService(Context context, MindstormConnection connection) {
		preferences = PreferenceManager.getDefaultSharedPreferences(context);
		preferences.registerOnSharedPreferenceChangeListener(this);
		this.connection = connection;
		this.context = context;

		sensorRegistry = new SensorRegistry();

        sensorScheduler = new PausableScheduledThreadPoolExecutor(2);
	}

	private class SensorRegistry {

		private class SensorTuple {

			public ScheduledFuture scheduledFuture;
			public NXTSensor sensor;

			public SensorTuple(ScheduledFuture scheduledFuture, NXTSensor sensor) {
				this.scheduledFuture = scheduledFuture;
				this.sensor = sensor;
			}
		}

		private Map<Integer, SensorTuple> registeredSensors = new HashMap<Integer, SensorTuple>();

		synchronized public void add(NXTSensor sensor) {
			remove(sensor.getConnectedPort());
			ScheduledFuture scheduledFuture = sensorScheduler.scheduleWithFixedDelay(new SensorValueUpdater(sensor),
					500, sensor.getUpdateInterval(), TimeUnit.MILLISECONDS);

			registeredSensors.put(sensor.getConnectedPort(), new SensorTuple(scheduledFuture, sensor));
		}

		synchronized public void remove(NXTSensor sensor) {
			int port = sensor.getConnectedPort();
			remove(port);
		}

		synchronized public void remove(int port) {
			SensorTuple tuple = registeredSensors.get(port);
			if (tuple != null) {
				tuple.scheduledFuture.cancel(false);

			}
			registeredSensors.remove(port);
		}
	}

    private class SensorValueUpdater implements Runnable {
        private NXTSensor sensor;

		public SensorValueUpdater(NXTSensor sensor) {
			this.sensor = sensor;
		}

		@Override
        public void run() {
            Stopwatch stopwatch = new Stopwatch();
            stopwatch.start();
            sensor.updateLastSensorValue();
            Log.d(TAG, String.format("Time for %s sensor: %d ms | Value: %d", sensor.getName(),
                    stopwatch.getElapsedMilliseconds(), sensor.getLastSensorValue()));
        }
    }

	public void pauseSensorUpdate() {
		sensorScheduler.pause();
	}

	public void resumeSensorUpdate() {
		sensorScheduler.resume();
	}

    public void destory() {
        sensorScheduler.shutdown();
	}

	public NXTSensor createSensor1() {
		String sensorTypeName = preferences.getString(SettingsActivity.NXT_SENSOR_1, "");
        return  createSensor(sensorTypeName, 0);
	}

	public NXTSensor createSensor2() {
		String sensorTypeName = preferences.getString(SettingsActivity.NXT_SENSOR_2, "");
        return  createSensor(sensorTypeName, 1);
	}

	public NXTSensor createSensor3() {
		String sensorTypeName = preferences.getString(SettingsActivity.NXT_SENSOR_3, "");
        return  createSensor(sensorTypeName, 2);
	}

	public NXTSensor createSensor4() {
		String sensorTypeName = preferences.getString(SettingsActivity.NXT_SENSOR_4, "");
        return  createSensor(sensorTypeName, 3);
	}

	private boolean equals(String name, int type) {
		return name.equals(context.getString(type));
	}

	private NXTSensor createSensor(String sensorTypeName, int port) {

		if (equals(sensorTypeName, R.string.nxt_no_sensor)) {
			sensorRegistry.remove(port);
			return null;
		}

        NXTSensor sensor = null;

		if (equals(sensorTypeName, R.string.nxt_sensor_touch)) {
			sensor = new NXTTouchSensor(port, connection);
		}

		if (equals(sensorTypeName, R.string.nxt_sensor_sound)) {
			sensor = new NXTSoundSensor(port, connection);
		}

		if (equals(sensorTypeName, R.string.nxt_sensor_light)) {
			sensor = new NXTLightSensor(port, connection);
		}

		if (equals(sensorTypeName, R.string.nxt_sensor_ultrasonic)) {
			sensor = new NXTI2CUltraSonicSensor(connection);
		}

        if (sensor == null) {
            throw new MindstormException("No valid sensor found!"); // Should never occur
        }

		sensorRegistry.add(sensor);

        return sensor;
	}

	List<OnSensorChangedListener> sensorChangedListeners = new LinkedList<OnSensorChangedListener>();

	public void registerOnSensorChangedListener(OnSensorChangedListener listener) {
		sensorChangedListeners.add(listener);
	}

	private boolean isChangedPreferenceASensorPreference(String preference) {
		return (preference.equals(SettingsActivity.NXT_SENSOR_1) ||
				preference.equals(SettingsActivity.NXT_SENSOR_2) ||
				preference.equals(SettingsActivity.NXT_SENSOR_3) ||
				preference.equals(SettingsActivity.NXT_SENSOR_4));
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String preference) {

		if (!isChangedPreferenceASensorPreference(preference)) {
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
