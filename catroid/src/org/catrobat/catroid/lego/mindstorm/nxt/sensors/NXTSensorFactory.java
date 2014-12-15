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

import org.catrobat.catroid.R;
import org.catrobat.catroid.lego.mindstorm.MindstormConnection;
import org.catrobat.catroid.lego.mindstorm.MindstormException;

public class NXTSensorFactory {
	private MindstormConnection connection;

	public NXTSensorFactory(MindstormConnection connection) {
		this.connection = connection;
	}

	public NXTSensor create(Integer sensorType, int port) {

		if (sensorType == R.string.nxt_sensor_touch) {
			return new NXTTouchSensor(port, connection);
		}

		if (sensorType == R.string.nxt_sensor_sound) {
			return new NXTSoundSensor(port, connection);
		}

		if (sensorType == R.string.nxt_sensor_light) {
			return new NXTLightSensor(port, connection);
		}

		if (sensorType == R.string.nxt_sensor_ultrasonic) {
			return new NXTI2CUltraSonicSensor(connection);
		}

		throw new MindstormException("No valid sensor found!"); // Should never occur
	}

	public boolean isSensorAssigned(Integer sensorType) {
			return !( sensorType ==  R.string.nxt_no_sensor
					  || sensorType == null
			);
	}
}
