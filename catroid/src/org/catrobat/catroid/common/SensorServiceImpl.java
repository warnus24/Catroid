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

package org.catrobat.catroid.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.catrobat.catroid.formulaeditor.Sensors;
import org.catrobat.catroid.ui.SettingsActivity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SensorServiceImpl implements SensorService {

	public static final String TAG = SensorServiceImpl.class.getSimpleName();

	Map<Sensors, Integer> sensorMappings = new HashMap<Sensors, Integer>();

	@Override
	public Sensors getMappedSensor(String sensorToken) {

		for (Sensors sensor : sensorMappings.keySet()) {
			if (sensor.name().endsWith(sensorToken)) {
				return sensor;
			}
		}

		return null;
	}

	public String getMappedSensorString(String sensorToken, Context applicationContext) {

		for (Map.Entry<Sensors, Integer> entry : sensorMappings.entrySet()) {
			if (entry.getKey().name().endsWith(sensorToken)) {
				int resourceId = entry.getValue();
				return getFormatedSensorString(applicationContext, resourceId, sensorToken);

			}
		}

		return null;
	}

	@Override
	public String getMappedSensorString(Sensors sensor, Context applicationContext) {
		int resourceId = sensorMappings.get(sensor);
		return getFormatedSensorString(applicationContext, resourceId, sensor.name());
	}

	@Override
	public Integer getMappedSensor(Sensors sensors) {
		return sensorMappings.get(sensors);
	}

	private String getFormatedSensorString(Context context, int resourceId, String token) {
		char sensorPort = token.charAt(token.length() - 1);
		String sensorName = context.getString(resourceId);

		return String.format("(%c) %s", sensorPort , sensorName);
	}

	@Override
	public void registerSensor(Sensors sensor, int stringId) {
		sensorMappings.put(sensor, stringId);
	}

	@Override
	public void loadProjectSpecificMappings(Context applicationContext) {

		sensorMappings.clear();

		registerSensor(Sensors.NXT_SENSOR_1, getId(SettingsActivity.NXT_SENSOR_1, applicationContext));
		registerSensor(Sensors.NXT_SENSOR_2, getId(SettingsActivity.NXT_SENSOR_2, applicationContext));
		registerSensor(Sensors.NXT_SENSOR_3, getId(SettingsActivity.NXT_SENSOR_3, applicationContext));
		registerSensor(Sensors.NXT_SENSOR_4, getId(SettingsActivity.NXT_SENSOR_4, applicationContext));
	}

	private int getId(String preferencesKey, Context applicationContext) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(applicationContext);
		String resourceStringId = preferences.getString(preferencesKey, "");
		return applicationContext.getResources().getIdentifier(resourceStringId, "string", "org.catrobat.catroid");
	}
}
