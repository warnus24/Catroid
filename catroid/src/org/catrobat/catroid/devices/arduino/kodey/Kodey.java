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
package org.catrobat.catroid.devices.arduino.kodey;

import org.catrobat.catroid.bluetooth.BTDeviceService;
import org.catrobat.catroid.formulaeditor.Sensors;

public interface Kodey extends BTDeviceService {

	public static final int PIN_SPEAKER_OUT = 3;

	public static final int PIN_RED_1 = 4;
	public static final int PIN_GREEN_1 = 5;
	public static final int PIN_BLUE_1 = 6;
	public static final int PIN_RED_2 = 7;
	public static final int PIN_GREEN_2 = 8;
	public static final int PIN_BLUE_2 = 9;

	public static final int LEFT_MOTOR_REVERSE = 10;
	public static final int LEFT_MOTOR_FORWARD = 11;
	public static final int RIGHT_MOTOR_FORWARD = 12;
	public static final int RIGHT_MOTOR_REVERSE = 13;

	public void playTone(int selected_tone, int duration);

	public void move(int motor, int speed);

	public void stopLeft();
	public void stopRight();


	public void stopAllMovements();

	public void setRGBLightColor(int eye, int red, int green, int blue);

	public int getSensorValue(Sensors sensor);

	public KodeySensor getSensor1();
	public KodeySensor getSensor2();
	public KodeySensor getSensor3();
	public KodeySensor getSensor4();
	public KodeySensor getSensor5();
	public KodeySensor getSensor6();
}
