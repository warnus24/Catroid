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
package org.catrobat.catroid.arduino;

import android.content.Context;

import org.catrobat.catroid.R;
import org.catrobat.catroid.arduino.ArduinoConnection;
import org.catrobat.catroid.arduino.ArduinoException;

public class ArduinoSensorFactory {

	private Context context;
	private ArduinoConnection connection;

	public ArduinoSensorFactory(Context context, ArduinoConnection connection) {
		this.context = context;
		this.connection = connection;
	}

	public ArduinoSensor create(String sensorTypeName, int port) {

		if (equals(sensorTypeName, R.string.formula_editor_function_arduino_read_pin_value_digital)) {
			return new ArduinoDigitalSensor(port, connection);
		}

		if (equals(sensorTypeName, R.string.formula_editor_function_arduino_read_pin_value_analog)) {
			return new ArduinoAnalogSensor(port, connection);
		}

		throw new ArduinoException("No valid sensor found!"); // Should never occur
	}

	public boolean isSensorAssigned(String sensorTypeName) {
			return !( equals(sensorTypeName, R.string.nxt_no_sensor)
					  || sensorTypeName == null
					  || sensorTypeName.isEmpty()
			);
	}

	private boolean equals(String sensorTypeName, int sensorType) {
		return sensorTypeName.equals(context.getString(sensorType));
	}

}
