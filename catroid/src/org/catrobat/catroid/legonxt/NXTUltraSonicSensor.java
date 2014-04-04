package org.catrobat.catroid.legonxt;

import android.os.Handler;
import android.util.Log;

import org.catrobat.catroid.formulaeditor.SensorCustomEvent;
import org.catrobat.catroid.formulaeditor.SensorCustomEventListener;
import org.catrobat.catroid.formulaeditor.Sensors;

import java.util.ArrayList;

public class NXTUltraSonicSensor extends NXTSensor {

	private static final int UPDATE_INTERVAL = 250;
	private static final int INITIAL_WAIT = 1000;
	private static final String TAG = NXTUltraSonicSensor.class.getSimpleName();
	private static NXTUltraSonicSensor instance = null;
	private ArrayList<SensorCustomEventListener> listenerList = new ArrayList<SensorCustomEventListener>();

	private Handler handler;
	private float lastSensorValue = 50.0f;

	private static final int PORT = 0x03;

	private NXTUltraSonicSensor() {
		handler = new Handler();
	}

	public void receivedMessage(byte[] message) {

		switch (message[1]) {

			case NXTSensor.LS_WRITE:
				sendLSGetStatus.run();
				break;

			case NXTSensor.LS_GET_STATUS:
				if (message[3] != 0) {
					sendLSRead.run();
				}
				else {
					//Log.e(TAG, String.format("GET_STATUS reply is byte-count: %d", message[3]));
					handler.postDelayed(sendLSGetStatus, UPDATE_INTERVAL);
				}
				break;

			case NXTSensor.LS_READ:

				float currentSensorValue = (message[4] & 0xFF); // make sure that we get a positive value

				if (lastSensorValue == currentSensorValue) {
					handler.postDelayed(sendLSWrite, UPDATE_INTERVAL);
					return;
				}

				//Log.d(TAG, String.format("Distance: %f", currentSensorValue));

				lastSensorValue = currentSensorValue;

				SensorCustomEvent event = new SensorCustomEvent(Sensors.LEGO_NXT_ULTRASONIC, new float[] {currentSensorValue});
				for (SensorCustomEventListener listener : listenerList) {
					listener.onCustomSensorChanged(event);
				}

				handler.postDelayed(sendLSWrite, UPDATE_INTERVAL);

				break;

			default:
				Log.d(TAG, String.format("Unknown LS message: %d", message[1]));
		}
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

		listenerList.add(listener);

		handler.postDelayed(sendLSWrite, INITIAL_WAIT);

		return true;
	}

	private Runnable sendLSWrite = new Runnable() {
		@Override
		public void run() {
			byte[] lsWriteMessage = new byte[7];
			lsWriteMessage[0] = REQUEST_MESSAGE;
			lsWriteMessage[1] = LS_WRITE;
			lsWriteMessage[2] = PORT;
			lsWriteMessage[3] = 0x02; // length of data in write message
			lsWriteMessage[4] = 0x01; // length of return message
			lsWriteMessage[5] = 0x02; // Address
			lsWriteMessage[6] = 0x42; // register of data

			LegoNXT.sendSensorMessage(lsWriteMessage);
		}
	};

	private Runnable sendLSGetStatus = new Runnable() {
		@Override
		public void run() {
			byte[] getStatusMessage = new byte[3];
			getStatusMessage[0] = REQUEST_MESSAGE;
			getStatusMessage[1] = LS_GET_STATUS;
			getStatusMessage[2] = PORT;

			LegoNXT.sendSensorMessage(getStatusMessage);
		}
	};

	private Runnable sendLSRead = new Runnable() {
		@Override
		public void run() {
			byte[] getStatusMessage = new byte[3];
			getStatusMessage[0] = REQUEST_MESSAGE;
			getStatusMessage[1] = LS_READ;
			getStatusMessage[2] = PORT;

			LegoNXT.sendSensorMessage(getStatusMessage);
		}
	};

	public synchronized void unregisterListener(SensorCustomEventListener listener) {
		if (listenerList.contains(listener)) {
			listenerList.remove(listener);
			if (listenerList.size() == 0) {
				handler.removeCallbacks(sendLSRead);
				handler.removeCallbacks(sendLSWrite);
				handler.removeCallbacks(sendLSGetStatus);
				lastSensorValue = 0.0f;
			}
		}
	}

}
