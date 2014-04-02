package org.catrobat.catroid.legonxt;

/**
 * Created by martin on 02.04.14.
 */
public abstract class NXTSensor {

	// return Types

	public static final byte CMD_SET_INPUT_MODE = 0x05;
	public static final byte CMD_GET_INPUT_VALUES = 0x07;

	// Sensor Types
	protected static byte SWITCH = 0x01;


	// Sensor Modes
	protected static byte BOOLEAN_MODE = 0x20;
}
