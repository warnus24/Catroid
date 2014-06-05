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
import android.hardware.Sensor;
import android.preference.PreferenceManager;
import android.util.Log;

import org.catrobat.catroid.R;
import org.catrobat.catroid.formulaeditor.Sensors;
import org.catrobat.catroid.lego.mindstorm.MindstormConnection;
import org.catrobat.catroid.lego.mindstorm.MindstormException;
import org.catrobat.catroid.lego.mindstorm.MindstormServiceProvider;
import org.catrobat.catroid.lego.mindstorm.nxt.sensors.NXTI2CUltraSonicSensor;
import org.catrobat.catroid.lego.mindstorm.nxt.sensors.NXTLightSensor;
import org.catrobat.catroid.lego.mindstorm.nxt.sensors.NXTSensor;
import org.catrobat.catroid.lego.mindstorm.nxt.sensors.NXTSoundSensor;
import org.catrobat.catroid.lego.mindstorm.nxt.sensors.NXTTouchSensor;
import org.catrobat.catroid.ui.SettingsActivity;
import org.catrobat.catroid.utils.Stopwatch;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class NXTSensorService implements SharedPreferences.OnSharedPreferenceChangeListener {

	private static final String TAG = NXTSensorService.class.getSimpleName();

    private SensorRegistry sensorRegistry;

	SharedPreferences preferences;
	MindstormConnection connection;
	Context context;

    ScheduledThreadPoolExecutor sensorScheduler;

	public NXTSensorService(Context context, MindstormConnection connection) {
		preferences = PreferenceManager.getDefaultSharedPreferences(context);
		preferences.registerOnSharedPreferenceChangeListener(this);
		this.connection = connection;
		this.context = context;

        sensorRegistry = new SensorRegistry();

        sensorScheduler = new ScheduledThreadPoolExecutor(1);
	}

    private class GetSensorValueRunner implements Runnable {
        private NXTSensor sensor;

        public GetSensorValueRunner(NXTSensor sensor) {
            this.sensor = sensor;
        }

        @Override
        public void run() {
            synchronized (NXTSensorService.this) {
                if (!NXTSensorService.this.isRunning()) {
                    try {
                        NXTSensorService.this.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        return;
                    }
                }
            }
            Stopwatch stopwatch = new Stopwatch();
            stopwatch.start();
            sensor.updateLastSensorValue();
//            Log.d(TAG, String.format("Time for %s sensor: %d | Value: %d", sensor.getName(),
//                    stopwatch.getElapsedMilliseconds(), sensor.getLastSensorValue()));
        }
    }

    public void destory() {
        sensorScheduler.shutdown();
        MindstormServiceProvider.unregister(NXTSensorService.class);
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

        public void add(NXTSensor sensor) {
            remove(sensor.getConnectedPort());
            ScheduledFuture scheduledFuture = sensorScheduler.scheduleAtFixedRate(new GetSensorValueRunner(sensor),
                    500, sensor.getUpdateInterval(), TimeUnit.MILLISECONDS);

            registeredSensors.put(sensor.getConnectedPort(), new SensorTuple(scheduledFuture, sensor));
        }

        public void remove(NXTSensor sensor) {
            int port = sensor.getConnectedPort();
            remove(port);
        }

        public void remove(int port) {

            synchronized (registeredSensors) {
                SensorTuple tuple = registeredSensors.get(port);
                if (tuple != null) {
                    tuple.scheduledFuture.cancel(false);

                }
                registeredSensors.remove(port);
            }
        }

        public NXTSensor getSensor(int port) {
            synchronized (registeredSensors) {
                return registeredSensors.get(port).sensor;
            }
        }

        public NXTSensor getSensor(String name) {
            synchronized (registeredSensors) {
                for (SensorTuple t : registeredSensors.values()) {
                    if (t.sensor.getName().equals(name)) {
                        return t.sensor;
                    }
                }
            }

            return null;
        }

        // TODO only for testing
        @Deprecated
        public int getSensorValue(String name) {
            synchronized (registeredSensors) {
                for (SensorTuple t : registeredSensors.values()) {
                    if (t.sensor.getName().toLowerCase().contains(name)) {
                        return t.sensor.getLastSensorValue();
                    }
                }
            }

            return 0;

        }
    }

    boolean runHandler = false;

	public int getValue(Sensors sensors) {
        switch (sensors) {
            case LEGO_NXT_LIGHT:
                return sensorRegistry.getSensorValue("light");
            case LEGO_NXT_SOUND:
                return sensorRegistry.getSensorValue("sound");
            case LEGO_NXT_TOUCH:
                return sensorRegistry.getSensorValue("touch");
            case LEGO_NXT_ULTRASONIC:
                return sensorRegistry.getSensorValue("ultrasonic");
        }

        return -1;
    }

    public boolean isRunning() {
        return runHandler;
    }

	public void listenToSensors(boolean b) {

        synchronized (this) {
            runHandler = b;
            if (b) {
                this.notifyAll();
            }
        }
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

//        if (sensor == null) {
//            throw new MindstormException("No valid sensor found!"); // Should never occur
//        }

        if (equals(sensorTypeName, R.string.nxt_no_sensor) || sensor == null) {
            sensorRegistry.remove(port);
            return null;
        }

        sensorRegistry.add(sensor);

        return sensor;
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
