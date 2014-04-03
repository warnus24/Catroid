package org.catrobat.catroid.legonxt;

import android.util.Log;

public abstract class NXTSensor {

	public static final byte REQUEST_MESSAGE = 0X00;
	public static final byte RETURN_MESSAGE = 0X02;

	// return Types
	public static final byte CMD_SET_INPUT_MODE = 0x05;
	public static final byte CMD_GET_INPUT_VALUES = 0x07;

	// for ultrasonic sensor
	public static final byte LS_STATUS = 0x0E;
	public static final byte LS_WRITE = 0x0F;
	public static final byte LS_READ = 0x10;
	public static final byte LS_GET_STATUS = 0x0E;

	// Sensor Types
	protected static final byte SWITCH = 0x01;
	protected static final byte LIGHT_ACTIVE = 0x05;
	protected static final byte LIGHT_INACTIVE = 0x06;
	protected static final byte SOUND_DB = 0x07;
	protected static final byte SOUND_DBA = 0x08;

	protected static final byte LOWSPEED_9V = 0x0B;


	// Sensor Modes
	protected static final byte BOOLEAN_MODE = 0x20;
	protected static final byte RAW_MODE = 0x00;
	protected static final byte PCT_FULL_SCALE_MODE = (byte)0x80; //value scaled between 0 and 100

	public static void handleSensorValue(byte[] message) {

		switch (message[6]) {
			case NXTSensor.LIGHT_INACTIVE:
				NXTLightSensor.getInstance().receivedMessage(message);
				break;
			case NXTSensor.SWITCH:
				NXTTouchSensor.getInstance().receivedMessage(message);
				break;
            case NXTSensor.SOUND_DBA:
                NXTSoundSensor.getInstance().receivedMessage(message);
                break;
			default:
				Log.d("NXTSensor", String.format("Unknown Message Type: %d", message[1]));
				break;
		}
	}
}
