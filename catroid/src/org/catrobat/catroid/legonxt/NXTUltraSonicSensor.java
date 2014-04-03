package org.catrobat.catroid.legonxt;

import android.os.Handler;
import android.util.Log;

import org.catrobat.catroid.formulaeditor.SensorCustomEvent;
import org.catrobat.catroid.formulaeditor.SensorCustomEventListener;
import org.catrobat.catroid.formulaeditor.Sensors;

import java.util.ArrayList;

public class NXTUltraSonicSensor extends NXTSensor {

	private static final int UPDATE_INTERVAL = 50;
	private static final String TAG = NXTUltraSonicSensor.class.getSimpleName();
	private static NXTUltraSonicSensor instance = null;
	private ArrayList<SensorCustomEventListener> listenerList = new ArrayList<SensorCustomEventListener>();

	private Handler handler;
	private float lastSensorValue = 0.0f;

	private static final int PORT = 0x03;

	private NXTUltraSonicSensor() {
		handler = new Handler();
	}

	private static class Message {

		private byte[] message;
		private Message() {
			message = new byte[3];
			message[0] = REQUEST_MESSAGE;
			message[1] = 0x10;
			message[2] = PORT;
		}

		private byte[] getRawMessage() { return message; }
	}

	private Runnable statusChecker = new Runnable() {
		@Override
		//poll vor new sensor inputs values
		public void run() {
			Message message = new Message();
			LegoNXT.sendSensorMessage(message.getRawMessage());

			handler.postDelayed(statusChecker, UPDATE_INTERVAL);
		}
	};

	public void receivedMessage(byte[] message) {
		//float currentSensorValue = message[12];

		byte[] m = message;
		Log.d(TAG, "received Ultrasonic message");

//		if (lastSensorValue == currentSensorValue)
//			return;
//
//		lastSensorValue = currentSensorValue;
//
//		SensorCustomEvent event = new SensorCustomEvent(Sensors.LEGO_NXT_ULTRASONIC, new float[] {currentSensorValue});
//		for (SensorCustomEventListener listener : listenerList) {
//			listener.onCustomSensorChanged(event);
//		}
	}

	public static NXTUltraSonicSensor getInstance() {
		if (instance == null) {
			instance = new NXTUltraSonicSensor();
		}
		return instance;
	}

	public synchronized boolean registerListener(SensorCustomEventListener listener) {

		if (listenerList.contains(listener))
			return true;

		byte[] setModeMessage = new byte[5];
		setModeMessage[0] = REQUEST_MESSAGE;
		setModeMessage[1] = CMD_SET_INPUT_MODE;
		setModeMessage[2] = PORT;
		setModeMessage[3] = NXTSensor.LOWSPEED_9V; //Sensor Type
		setModeMessage[4] = NXTSensor.RAW_MODE; // Sensor Mode
		LegoNXT.sendSensorMessage(setModeMessage);

		byte[] lsWriteMessage = new byte[5];
		lsWriteMessage[0] = REQUEST_MESSAGE;
		lsWriteMessage[1] = LS_WRITE;
		lsWriteMessage[2] = PORT;
		lsWriteMessage[3] = 0x03; // TX
		lsWriteMessage[4] = 0x00; // RX
		lsWriteMessage[5] = 0x07; // N
		lsWriteMessage[6] = 0x02; // begin data
		lsWriteMessage[7] = 0x41;
		lsWriteMessage[8] = 0x02;
		LegoNXT.sendSensorMessage(lsWriteMessage);

		byte[] getStatusMessage = new byte[3];
		getStatusMessage[0] = REQUEST_MESSAGE;
		getStatusMessage[1] = LS_GET_STATUS;
		getStatusMessage[2] = PORT;

		LegoNXT.sendSensorMessage(getStatusMessage);

		statusChecker.run();

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
