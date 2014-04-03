package org.catrobat.catroid.legonxt;

import android.os.Handler;
import android.util.Log;

import org.catrobat.catroid.formulaeditor.SensorCustomEvent;
import org.catrobat.catroid.formulaeditor.SensorCustomEventListener;
import org.catrobat.catroid.formulaeditor.Sensors;

import java.util.ArrayList;

public class NXTSoundSensor extends NXTSensor {

	private static final int UPDATE_INTERVAL = 250;
	private static final String TAG = NXTSoundSensor.class.getSimpleName();
	private static NXTSoundSensor instance = null;
	private ArrayList<SensorCustomEventListener> listenerList = new ArrayList<SensorCustomEventListener>();

	private Handler handler;
	private float lastSensorValue = 0.0f;

	private static final int PORT = 0x02;

	private NXTSoundSensor() {
		handler = new Handler();
	}

	private static class SoundMessage {

		private byte[] SoundMessage;
		private SoundMessage() {
			SoundMessage = new byte[3];
			SoundMessage[0] = REQUEST_MESSAGE;
			SoundMessage[1] = CMD_GET_INPUT_VALUES;
			SoundMessage[2] = PORT;
		}

		private byte[] getRawMessage() { return SoundMessage; }
	}

	private Runnable statusChecker = new Runnable() {
		@Override
		//poll vor new sensor inputs values
		public void run() {
			SoundMessage message = new SoundMessage();
			LegoNXT.sendSensorMessage(message.getRawMessage());

			handler.postDelayed(statusChecker, UPDATE_INTERVAL);
		}
	};

	public void receivedMessage(byte[] message) {
		float currentSensorValue = message[12];

		if (lastSensorValue == currentSensorValue)
			return;

		lastSensorValue = currentSensorValue;

		SensorCustomEvent event = new SensorCustomEvent(Sensors.LEGO_NXT_SOUND, new float[] {currentSensorValue});
		for (SensorCustomEventListener listener : listenerList) {
			listener.onCustomSensorChanged(event);
		}
	}

	public static NXTSoundSensor getInstance() {
		if (instance == null) {
			instance = new NXTSoundSensor();
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
			message[3] = NXTSensor.SOUND_DBA; //Sensor Type
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