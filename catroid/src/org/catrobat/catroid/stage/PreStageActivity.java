/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.stage;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.speech.tts.TextToSpeech.OnUtteranceCompletedListener;
import android.util.Log;
import android.widget.Toast;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.arduino.Arduino;
import org.catrobat.catroid.arduino.ArduinoSensor;
import org.catrobat.catroid.bluetooth.DeviceListActivity;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.legonxt.LegoNXT;
import org.catrobat.catroid.legonxt.LegoNXTBtCommunicator;
import org.catrobat.catroid.ui.dialogs.CustomAlertDialogBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Queue;

@SuppressWarnings("deprecation")
public class PreStageActivity extends Activity {
	public static final int REQUEST_RESOURCES_INIT = 101;
	public static final int REQUEST_TEXT_TO_SPEECH = 10;
	private static final String TAG = PreStageActivity.class.getSimpleName();
	private static final int REQUEST_ENABLE_BLUETOOTH = 2000;
	private static LegoNXT legoNXT;
	//messages from Lego NXT device can be handled here
	// TODO should be fixed - could lead to problems
	@SuppressLint("HandlerLeak")
	final Handler recieveHandler = new Handler() {
		@Override
		public void handleMessage(Message myMessage) {

			Log.i("bt", "message" + myMessage.getData().getInt("message"));
			switch (myMessage.getData().getInt("message")) {
				case LegoNXTBtCommunicator.STATE_CONNECTED:
					connectingProgressDialog.dismiss();
					resourceInitialized();
					initNextBTRessource();
					break;
				case LegoNXTBtCommunicator.STATE_CONNECTERROR:
					Toast.makeText(PreStageActivity.this, R.string.bt_connection_failed, Toast.LENGTH_SHORT).show();
					connectingProgressDialog.dismiss();
					legoNXT.destroyCommunicator();
					legoNXT = null;
					resourceFailed();
					break;
			}
		}
	};
	private static TextToSpeech textToSpeech;
	private static OnUtteranceCompletedListenerContainer onUtteranceCompletedListenerContainer;
	private static Arduino arduino;
	private int requiredResourceCounter;
	private ProgressDialog connectingProgressDialog;
	private Queue<Bundle> BTResourceQueue;

	//all resources that should be reinitialized with every stage start
	public static void shutdownResources() {
		if (textToSpeech != null) {
			textToSpeech.stop();
			textToSpeech.shutdown();
		}
		if (legoNXT != null) {
			legoNXT.pauseCommunicator();
		}
		if (arduino != null) {
			arduino.pauseCommunicator();
		}
	}

	//all resources that should not have to be reinitialized every stage start
	public static void shutdownPersistentResources() {
		if (legoNXT != null) {
			legoNXT.destroyCommunicator();
			legoNXT = null;
		}
		if (arduino != null) {
			arduino.destroyCommunicator();
			arduino = null;
		}
		deleteSpeechFiles();
	}

	private static void deleteSpeechFiles() {
		File pathToSpeechFiles = new File(Constants.TEXT_TO_SPEECH_TMP_PATH);
		if (pathToSpeechFiles.isDirectory()) {
			for (File file : pathToSpeechFiles.listFiles()) {
				file.delete();
			}
		}
	}

	public static void textToSpeech(String text, File speechFile, OnUtteranceCompletedListener listener,
									HashMap<String, String> speakParameter) {
		if (text == null) {
			text = "";
		}

		if (onUtteranceCompletedListenerContainer.addOnUtteranceCompletedListener(speechFile, listener,
				speakParameter.get(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID))) {
			int status = textToSpeech.synthesizeToFile(text, speakParameter, speechFile.getAbsolutePath());
			if (status == TextToSpeech.ERROR) {
				Log.e(TAG, "File synthesizing failed");
			}
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		int requiredResources = getRequiredRessources();
		requiredResourceCounter = Integer.bitCount(requiredResources);

		setContentView(R.layout.activity_prestage);
		BTResourceQueue = new LinkedList<Bundle>();

		if ((requiredResources & Brick.TEXT_TO_SPEECH) > 0) {
			Intent checkIntent = new Intent();
			checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
			startActivityForResult(checkIntent, REQUEST_TEXT_TO_SPEECH);
		}
		if ((requiredResources & Brick.BLUETOOTH_LEGO_NXT) > 0) {

			if (legoNXT != null) {
				legoNXT.destroyCommunicator();
				legoNXT = null;
			}
			Bundle bundle = new Bundle();
			bundle.putInt(DeviceListActivity.RESOURCE_CONSTANT, Brick.BLUETOOTH_LEGO_NXT);
			bundle.putString(DeviceListActivity.RESOURCE_NAME_TEXT, getResources().getString(R.string.select_device_nxt));
			BTResourceQueue.add(bundle);
			//startBluetoothCommunication();
		}
		if ((requiredResources & Brick.BLUETOOTH_ARDUINO) > 0) {
			Bundle bundle = new Bundle();
			bundle.putInt(DeviceListActivity.RESOURCE_CONSTANT, Brick.BLUETOOTH_ARDUINO);
			bundle.putString(DeviceListActivity.RESOURCE_NAME_TEXT,
					getResources().getString(R.string.select_device_arduino));
			BTResourceQueue.add(bundle);
		}

		if ((requiredResources & Brick.BLUETOOTH_SENSORS_ARDUINO) > 0) {
			//set flag to start thread to update sensor values in formula editor
			ArduinoSensor sensor = ArduinoSensor.getArduinoSensorInstance();
			sensor.setBooleanArduinoBricks(true);

			Bundle bundle = new Bundle();
			bundle.putInt(DeviceListActivity.RESOURCE_CONSTANT, Brick.BLUETOOTH_SENSORS_ARDUINO);
			bundle.putString(DeviceListActivity.RESOURCE_NAME_TEXT,
					getResources().getString(R.string.select_device_arduino));
			BTResourceQueue.add(bundle);
		} else {
			//disable flag to start thread to update sensor values in formula editor
			ArduinoSensor sensor = ArduinoSensor.getArduinoSensorInstance();
			sensor.setBooleanArduinoBricks(false);
		}

		if (requiredResourceCounter == Brick.NO_RESOURCES) {
			startStage();
		}
		if (true) {
			initNextBTRessource();
		}
	}

	private void initNextBTRessource() {
		if (BTResourceQueue.iterator().hasNext()) {
			Iterator<Bundle> iterator = BTResourceQueue.iterator();
			startBluetoothCommunication(iterator.next());
			iterator.remove();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if (requiredResourceCounter == 0) {
			finish();
		}
	}

	private void resourceFailed() {
		setResult(RESULT_CANCELED, getIntent());
		finish();
	}

	private synchronized void resourceInitialized() {
		//Log.i("res", "Resource initialized: " + requiredResourceCounter);

		requiredResourceCounter--;
		if (requiredResourceCounter == 0) {
			startStage();
		}
	}

	public void startStage() {
		setResult(RESULT_OK, getIntent());
		finish();
	}

	private void startBluetoothCommunication(Bundle bundle) {
		connectingProgressDialog = ProgressDialog.show(this, "",
				getResources().getString(R.string.connecting_please_wait), true);

		Intent serverIntent = new Intent(this, DeviceListActivity.class);
		Bundle data = new Bundle();
		data.putInt(DeviceListActivity.RESOURCE_CONSTANT, bundle.getInt(DeviceListActivity.RESOURCE_CONSTANT));
		data.putString(DeviceListActivity.RESOURCE_NAME_TEXT, bundle.getString(DeviceListActivity.RESOURCE_NAME_TEXT));
		serverIntent.putExtras(data);
		this.startActivityForResult(serverIntent, REQUEST_ENABLE_BLUETOOTH);
	}

	private int getRequiredRessources() {
		ArrayList<Sprite> spriteList = (ArrayList<Sprite>) ProjectManager.getInstance().getCurrentProject()
				.getSpriteList();

		int ressources = Brick.NO_RESOURCES;
		for (Sprite sprite : spriteList) {
			ressources |= sprite.getRequiredResources();
		}
		return ressources;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		Log.i("preStage", "requestcode " + requestCode + " result code " + resultCode);

		switch (requestCode) {
			case REQUEST_ENABLE_BLUETOOTH:
				switch (resultCode) {
					case Activity.RESULT_OK:
						Bundle bundle = data.getExtras();
						switch (bundle.getInt(DeviceListActivity.RESOURCE_CONSTANT)) {
							case (Brick.BLUETOOTH_LEGO_NXT):
								legoNXT = new LegoNXT(this, recieveHandler);
								String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
								legoNXT.startBTCommunicator(address);
								break;
							case (Brick.BLUETOOTH_ARDUINO):

								String arduinoMacAddress = data.getExtras().getString(
										DeviceListActivity.EXTRA_DEVICE_ADDRESS);
								arduino = new Arduino(this, recieveHandler);
								arduino.startBTCommunicator(arduinoMacAddress);
								connectingProgressDialog.dismiss();
								resourceInitialized();
								break;
							case (Brick.BLUETOOTH_SENSORS_ARDUINO):

								String MacAddress = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
								arduino = new Arduino(this, recieveHandler);
								arduino.startBTCommunicator(MacAddress);
								connectingProgressDialog.dismiss();
								resourceInitialized();
								break;
						}
						break;

					case DeviceListActivity.BLUETOOTH_ACTIVATION_CANCELED:
						connectingProgressDialog.dismiss();
						resourceFailed();
						break;

					case Activity.RESULT_CANCELED:
						Toast.makeText(PreStageActivity.this, R.string.bt_connection_failed, Toast.LENGTH_LONG).show();
						connectingProgressDialog.dismiss();
						resourceFailed();
						break;
				}
				break;

			case REQUEST_TEXT_TO_SPEECH:
				if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
					textToSpeech = new TextToSpeech(getApplicationContext(), new OnInitListener() {
						@Override
						public void onInit(int status) {
							onUtteranceCompletedListenerContainer = new OnUtteranceCompletedListenerContainer();
							textToSpeech.setOnUtteranceCompletedListener(onUtteranceCompletedListenerContainer);
							resourceInitialized();
							if (status == TextToSpeech.ERROR) {
								Toast.makeText(PreStageActivity.this,
										"Error occurred while initializing Text-To-Speech engine", Toast.LENGTH_LONG)
										.show();
								resourceFailed();
							}
						}
					});
					if (textToSpeech.isLanguageAvailable(Locale.getDefault()) == TextToSpeech.LANG_MISSING_DATA) {
						Intent installIntent = new Intent();
						installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
						startActivity(installIntent);
						resourceFailed();
					}
				} else {
					AlertDialog.Builder builder = new CustomAlertDialogBuilder(this);
					builder.setMessage(R.string.text_to_speech_engine_not_installed).setCancelable(false)
							.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int id) {
									Intent installIntent = new Intent();
									installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
									startActivity(installIntent);
									resourceFailed();
								}
							}).setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
							resourceFailed();
						}
					});
					AlertDialog alert = builder.create();
					alert.show();
				}
				break;
			default:
				resourceFailed();
				break;
		}
	}
}
