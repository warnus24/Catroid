package org.catrobat.catroid.legonxt;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.catrobat.catroid.formulaeditor.SensorCustomEvent;
import org.catrobat.catroid.formulaeditor.SensorCustomEventListener;
import org.catrobat.catroid.formulaeditor.Sensors;

import java.io.IOException;
import java.util.ArrayList;

public class NXTTouchSensor extends NXTSensor {

	private static final int UPDATE_INTERVAL = 50;
	private static final String TAG = NXTTouchSensor.class.getSimpleName();
	private static NXTTouchSensor instance = null;
	private ArrayList<SensorCustomEventListener> listenerList = new ArrayList<SensorCustomEventListener>();

	private Handler handler;
	private boolean lastSensorValue = false;

	private NXTTouchSensor() {
		handler = new Handler();
	}

	private Runnable statusChecker = new Runnable() {
		@Override
		//poll vor new sensor inputs values
		public void run() {
			byte[] message = new byte[3];
			message[0] = 0x00;
			message[1] = CMD_GET_INPUT_VALUES;
			message[2] = 0x01; // Port

			LegoNXT.sendSensorMessage(message);

			handler.postDelayed(statusChecker, UPDATE_INTERVAL);
		}
	};

	public void receivedMessage(byte[] message) {
		boolean currentSensorValue = (message[12] != 0);

		if (lastSensorValue == currentSensorValue)
			return;

		lastSensorValue = currentSensorValue;

		SensorCustomEvent event = new SensorCustomEvent(Sensors.LEGO_NXT_TOUCH, currentSensorValue);
		for (SensorCustomEventListener listener : listenerList) {
			listener.onCustomSensorChanged(event);
		}
	}

	public static NXTTouchSensor getInstance() {
		if (instance == null) {
			instance = new NXTTouchSensor();
		}
		return instance;
	}

	public synchronized boolean registerListener(SensorCustomEventListener listener) {

		if (listenerList.contains(listener)) {
			return true;
		}
		else {
			byte[] message = new byte[5];

			message[0] = 0x00;
			message[1] = CMD_SET_INPUT_MODE;
			message[2] = 0x01; // Port
			message[3] = NXTSensor.SWITCH; //Sensor Type
			message[4] = NXTSensor.BOOLEAN_MODE; // Sensor Mode
			LegoNXT.sendSensorMessage(message);

			statusChecker.run();

		}
		listenerList.add(listener);

		return true;
	}

	public synchronized void unregisterListener(SensorCustomEventListener listener) {
		if (listenerList.contains(listener)) {
			listenerList.remove(listener);
			if (listenerList.size() == 0) {
				handler.removeCallbacks(statusChecker);
				lastSensorValue = false;
			}
		}
	}
}
