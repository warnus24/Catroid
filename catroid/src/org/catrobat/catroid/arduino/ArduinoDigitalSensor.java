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

import org.catrobat.catroid.arduino.ArduinoConnection;
import org.catrobat.catroid.arduino.ArduinoDigitalSensor;
import org.catrobat.catroid.arduino.ArduinoSensor;
import org.catrobat.catroid.arduino.ArduinoSensorMode;
import org.catrobat.catroid.arduino.ArduinoSensorType;

public class ArduinoDigitalSensor extends ArduinoSensor {

	private static final String TAG = ArduinoDigitalSensor.class.getSimpleName();
	private static final int DEFAULT_VALUE = 0;

	public ArduinoDigitalSensor(int port, ArduinoConnection connection) {
		super(port, ArduinoSensorType.DIGITAL, ArduinoSensorMode.Percent, connection);
		lastValidValue = DEFAULT_VALUE;
	}

	@Override
	public int getValue() {
		return getScaledValue();
	}
}
