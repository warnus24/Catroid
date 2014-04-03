package org.catrobat.catroid.legonxt;

import android.os.Handler;

import org.catrobat.catroid.formulaeditor.SensorCustomEvent;
import org.catrobat.catroid.formulaeditor.SensorCustomEventListener;
import org.catrobat.catroid.formulaeditor.Sensors;

import java.util.ArrayList;

public class NXTLightSensor extends NXTSensor {

	private static final int UPDATE_INTERVAL = 250;
	private static final String TAG = NXTLightSensor.class.getSimpleName();
	private static NXTLightSensor instance = null;
	private ArrayList<SensorCustomEventListener> listenerList = new ArrayList<SensorCustomEventListener>();

	private Handler handler;
	private float lastSensorValue = 0.0f;

	private static final int PORT = 0x00;

	private NXTLightSensor() {
		handler = new Handler();
	}

	private static class LightMessage {

		private byte[] lightMessage;
		private LightMessage() {
			lightMessage = new byte[3];
			lightMessage[0] = REQUEST_MESSAGE;
			lightMessage[1] = CMD_GET_INPUT_VALUES;
			lightMessage[2] = PORT;
		}

		private byte[] getRawMessage() {
			return lightMessage;
		}
	}

	private Runnable statusChecker = new Runnable() {
		@Override
		//poll vor new sensor inputs values
		public void run() {
			LightMessage message = new LightMessage();
			LegoNXT.sendSensorMessage(message.getRawMessage());

			handler.postDelayed(statusChecker, UPDATE_INTERVAL);
		}
	};

	public void receivedMessage(byte[] message) {
		float currentSensorValue = message[12];

		if (lastSensorValue == currentSensorValue)
			return;

		lastSensorValue = currentSensorValue;

		SensorCustomEvent event = new SensorCustomEvent(Sensors.LEGO_NXT_LIGHT, new float[] {currentSensorValue});
		for (SensorCustomEventListener listener : listenerList) {
			listener.onCustomSensorChanged(event);
		}
	}

	public static NXTLightSensor getInstance() {
		if (instance == null) {
			instance = new NXTLightSensor();
		}
		return instance;
	}

	public synchronized boolean registerListener(SensorCustomEventListener listener) {

		if (listenerList.contains(listener)) {
			return true;
		} else {
			byte[] message = new byte[5];

			message[0] = REQUEST_MESSAGE;
			message[1] = CMD_SET_INPUT_MODE;
			message[2] = PORT;
			message[3] = NXTSensor.LIGHT_INACTIVE; //Sensor Type
			message[4] = NXTSensor.PCT_FULL_SCALE_MODE; // Sensor Mode
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
				lastSensorValue = 0.0f;
			}
		}
	}
}