/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.plugin.Drone.other;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.plugin.Drone.DroneConsts;
import at.tugraz.ist.catroid.plugin.Drone.DroneHandler;

public class DroneWifiConnectionActivity extends Activity {

	private int errorCount;
	private boolean scanned;
	private boolean configured;
	private int status;
	private WifiManager wifiManager;

	private List<WifiConfiguration> wifiConfigs;
	private ConnectThread connectThread;
	private boolean runConnectThread;

	private ProgressBar pbConnectionStatus;
	private TextView tvWifiStatus;
	private TextView tvWifiScanStatus;
	private TextView tvDroneConnectStatus;
	private TextView tvCheckFirmwareStatus;
	private TextView tvGetNavdataStatus;
	private TextView tvCheckConfigStatus;
	private TextView tvWifiConnectionError;
	private ImageView ivWifiStatus;
	private ImageView ivWifiScanStatus;
	private ImageView ivDroneConnectStatus;
	private ImageView ivCheckFirmwareStatus;
	private ImageView ivGetNavdataStatus;
	private ImageView ivCheckConfigStatus;

	private Button startButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_drone_wificonnection);
		DroneHandler.getInstance().getDrone().connect();

		//		tvWifiStatus = (TextView) findViewById(R.id.tv_status_wifi);
		//		tvWifiScanStatus = (TextView) findViewById(R.id.tv_status_wifi_scan_for_drone);
		//		tvDroneConnectStatus = (TextView) findViewById(R.id.tv_status_connect_drone);
		//		tvGetNavdataStatus = (TextView) findViewById(R.id.tv_status_get_navdata);
		//		tvCheckFirmwareStatus = (TextView) findViewById(R.id.tv_status_check_firmware);
		//		tvCheckConfigStatus = (TextView) findViewById(R.id.tv_status_check_config);
		//		tvWifiConnectionError = (TextView) findViewById(R.id.tv_wifi_connection_error);
		//
		//		ivWifiStatus = (ImageView) findViewById(R.id.iv_status_wifi);
		//		ivWifiScanStatus = (ImageView) findViewById(R.id.iv_status_wifi_scan_for_drone);
		//		ivDroneConnectStatus = (ImageView) findViewById(R.id.iv_status_connect_drone);
		//		ivGetNavdataStatus = (ImageView) findViewById(R.id.iv_status_get_navdata);
		//		ivCheckFirmwareStatus = (ImageView) findViewById(R.id.iv_status_check_firmware);
		//		ivCheckConfigStatus = (ImageView) findViewById(R.id.iv_status_check_config);
		//
		//		pbConnectionStatus = (ProgressBar) findViewById(R.id.pb_connection_status);
		//
		//		startButton = (Button) findViewById(R.id.buttonStart);
		//		startButton.setOnClickListener(new OnClickListener() {
		//
		//			public void onClick(View view) {
		//				startButton.setClickable(false);
		//				startButton.setEnabled(false);
		//				startButton.setText(R.string.drone_processing);
		//
		//				if (!PluginManager.getInstance().areDroneTermsOfUseAccepted()) {
		//					showDialog(DroneConsts.DIALOG_TERMS_OF_USE);
		//				} else {
		//					changeStatus(DroneConsts.START);
		//				}
		//			}
		//		});
		//
		setResult(RESULT_OK);
		finish();
	}

	@Override
	public void onPause() {
		Log.d(DroneConsts.DroneLogTag, "ONPAUSE ()");
		super.onPause();
	}

	@Override
	public void onBackPressed() {
		changeStatus(DroneConsts.CANCEL);
	}

	@Override
	protected Dialog onCreateDialog(int whichDialog) {

		Dialog dialog = null;

		switch (whichDialog) {

			case DroneConsts.DIALOG_TERMS_OF_USE:
				dialog = new DroneTermsOfUseDialog(this);
				dialog.setOnDismissListener(new OnDismissListener() {
					public void onDismiss(DialogInterface dialog) {
						if (((DroneTermsOfUseDialog) dialog).accepted()) {
							changeStatus(DroneConsts.START);
						} else {
							changeStatus(DroneConsts.CANCEL);
						}
					}
				});
				break;

			case DroneConsts.SELECT_DRONE_DIALOG:
				String[] items = new String[wifiConfigs.size()];
				for (int i = 0; i < wifiConfigs.size(); i++) {
					items[i] = wifiConfigs.get(i).SSID.replace("\"", "");
				}

				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle(R.string.drone_pick_drone);
				builder.setCancelable(false);
				builder.setItems(items, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int item) {
						enableDroneNetwork(wifiConfigs.get(item));

					}
				});
				dialog = builder.create();
				break;
		}

		return dialog;
	}

	public void changeStatus(int status) {
		Message msg = new Message();
		msg.what = status;
		handler.sendMessage(msg);
	}

	private void statusSTART() {
		tvWifiConnectionError.setText("");

		tvWifiStatus.setText(R.string.drone_wifi_status_wifi);
		tvWifiScanStatus.setText(R.string.drone_wifi_status_wifi_scan_for_drone);
		tvDroneConnectStatus.setText(R.string.drone_wifi_status_connect_drone);
		tvGetNavdataStatus.setText(R.string.drone_wifi_status_get_navdata);
		tvCheckConfigStatus.setText(R.string.drone_wifi_status_check_config);
		tvCheckFirmwareStatus.setText(R.string.drone_wifi_status_check_firmware);

		ivWifiStatus.setVisibility(ImageView.GONE);
		ivWifiStatus.setImageDrawable(getApplicationContext().getResources().getDrawable(R.drawable.yes_icon));
		ivWifiScanStatus.setVisibility(ImageView.GONE);
		ivWifiScanStatus.setImageDrawable(getApplicationContext().getResources().getDrawable(R.drawable.yes_icon));
		ivDroneConnectStatus.setVisibility(ImageView.GONE);
		ivDroneConnectStatus.setImageDrawable(getApplicationContext().getResources().getDrawable(R.drawable.yes_icon));
		ivGetNavdataStatus.setVisibility(ImageView.GONE);
		ivGetNavdataStatus.setImageDrawable(getApplicationContext().getResources().getDrawable(R.drawable.yes_icon));
		ivCheckConfigStatus.setVisibility(ImageView.GONE);
		ivCheckConfigStatus.setImageDrawable(getApplicationContext().getResources().getDrawable(R.drawable.yes_icon));
		ivCheckFirmwareStatus.setVisibility(ImageView.GONE);
		ivCheckFirmwareStatus.setImageDrawable(getApplicationContext().getResources().getDrawable(R.drawable.yes_icon));

		pbConnectionStatus.setVisibility(ProgressBar.VISIBLE);

		runConnectThread = false;
		connectThread = null;

		scanned = false;
		configured = false;
		errorCount = 0;

		if (DroneHandler.getInstance().getDrone().isConnected()) {
			DroneHandler.getInstance().getDrone().disconnect();
		}

		connectThread = new ConnectThread();
		runConnectThread = true;
		connectThread.start();

		if (wifiManager == null) {
			wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
		}

		// check if wifi is already running and user already connected to drone
		if (!checkConnectionToDrone()) { // ping not possible

			IntentFilter intentFilter = new IntentFilter();
			intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
			registerReceiver(wifiStateChangedReceiver, intentFilter);
			changeStatus(DroneConsts.WIFI_ACTIVATING);

		} else { // ping possible

			// TODO Now Check firmware
			if (!DroneHandler.getInstance().getDrone().connect()) {

				changeStatus(DroneConsts.ERROR_CONNECTING_DRONE);
				return;
			}

			tvWifiStatus.setText(getApplicationContext().getResources().getString(R.string.drone_wifi_status_enabled));
			ivWifiStatus.setVisibility(ImageView.VISIBLE);
			tvWifiScanStatus.setText(getApplicationContext().getResources().getString(
					R.string.drone_wifi_status_scan_for_drone_scanned));
			ivWifiScanStatus.setVisibility(ImageView.VISIBLE);
			tvDroneConnectStatus.setText(getApplicationContext().getResources().getString(
					R.string.drone_wifi_status_connect_drone_connected));
			ivDroneConnectStatus.setVisibility(ImageView.VISIBLE);
			scanned = true;
			changeStatus(DroneConsts.CHECKING_FIRMWARE);
		}
	}

	public void statusWIFIACTIVATING(Message msg) {
		tvWifiStatus.setText(getApplicationContext().getResources().getString(R.string.drone_wifi_status_enabling));
		status = msg.what;
	}

	public void statusSCANNING(Message msg) {
		tvWifiStatus.setText(getApplicationContext().getResources().getString(R.string.drone_wifi_status_enabled));
		ivWifiStatus.setVisibility(ImageView.VISIBLE);
		tvWifiScanStatus.setText(getApplicationContext().getResources().getString(
				R.string.drone_wifi_status_scan_for_drone_scanning));
		status = msg.what;
	}

	public void statusCONNECTINGTODRONEWIFI(Message msg) {
		tvWifiScanStatus.setText(getApplicationContext().getResources().getString(
				R.string.drone_wifi_status_scan_for_drone_scanned));
		ivWifiScanStatus.setVisibility(ImageView.VISIBLE);
		tvDroneConnectStatus.setText(getApplicationContext().getResources().getString(
				R.string.drone_wifi_status_connect_drone_connecting));
		status = msg.what;
	}

	public void statusCONNECTINGTODRONE(Message msg) {
		// TODO generate new string
		tvWifiScanStatus.setText(getApplicationContext().getResources().getString(
				R.string.drone_wifi_status_scan_for_drone_scanned));
		ivWifiScanStatus.setVisibility(ImageView.VISIBLE);
		tvDroneConnectStatus.setText(getApplicationContext().getResources().getString(
				R.string.drone_wifi_status_connect_drone_connecting));
		status = msg.what;
	}

	private void statusCHECKINGFIRMWARE(Message msg) {
		tvDroneConnectStatus.setText(getApplicationContext().getResources().getString(
				R.string.drone_wifi_status_connect_drone_connected));
		ivDroneConnectStatus.setVisibility(ImageView.VISIBLE);
		tvCheckFirmwareStatus.setText(getApplicationContext().getResources().getString(
				R.string.drone_wifi_status_checking_firmware));
		status = msg.what;
	}

	private void statusWAITINGFORNAVDATA(Message msg) {
		tvCheckFirmwareStatus.setText(getApplicationContext().getResources().getString(
				R.string.drone_wifi_status_checked_firmware));
		ivCheckFirmwareStatus.setVisibility(ImageView.VISIBLE);
		tvGetNavdataStatus.setText(getApplicationContext().getResources().getString(
				R.string.drone_wifi_status_get_navdata_getting));
		status = msg.what;
	}

	private void statusCHECKINGCONFIG(Message msg) {
		tvGetNavdataStatus.setText(getApplicationContext().getResources().getString(
				R.string.drone_wifi_status_get_navdata_got));
		ivGetNavdataStatus.setVisibility(ImageView.VISIBLE);
		tvCheckConfigStatus.setText(getApplicationContext().getResources().getString(
				R.string.drone_wifi_status_checking_config));
		status = msg.what;
	}

	private void statusCANCEL(Message msg) {
		// Not necessary to close Wifi
		if (wifiManager != null) {
			// wifiManager.setWifiEnabled(false);
			try {
				unregisterReceiver(wifiStateChangedReceiver);
			} catch (IllegalArgumentException e) {
			}
		}
		setResult(RESULT_CANCELED);

		runConnectThread = false;
		connectThread = null;

		finish();
	}

	private void statusSUCCESS(Message msg) {
		tvCheckConfigStatus.setText(getApplicationContext().getResources().getString(
				R.string.drone_wifi_status_checked_config));
		ivCheckConfigStatus.setVisibility(ImageView.VISIBLE);
		setResult(RESULT_OK);
		if (wifiManager != null) {
			try {
				unregisterReceiver(wifiStateChangedReceiver);
			} catch (IllegalArgumentException e) {
			}
		}

		runConnectThread = false;
		connectThread = null;

		status = DroneConsts.FINSIHED;

		finish();
	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			switch (msg.what) {

				case DroneConsts.START:
					statusSTART();
					break;
				case DroneConsts.WIFI_ACTIVATING:
					statusWIFIACTIVATING(msg);
					break;

				case DroneConsts.SCANNING:
					statusSCANNING(msg);
					break;

				case DroneConsts.CONNECTING_TO_DRONE_WIFI:
					statusCONNECTINGTODRONEWIFI(msg);
					break;

				case DroneConsts.CONNECTING_TO_DRONE:
					statusCONNECTINGTODRONE(msg);
					break;

				case DroneConsts.CHECKING_FIRMWARE:
					statusCHECKINGFIRMWARE(msg);
					break;

				case DroneConsts.WAITING_FOR_NAVDATA:
					statusWAITINGFORNAVDATA(msg);
					break;

				case DroneConsts.CHECKING_CONFIG:
					statusCHECKINGCONFIG(msg);
					break;

				case DroneConsts.SELECT_DRONE_DIALOG:
					showDialog(DroneConsts.SELECT_DRONE_DIALOG);
					break;

				// errors
				case DroneConsts.ERROR_SCANNING:
				case DroneConsts.ERROR_FINDING_DRONE:
				case DroneConsts.ERROR_CONNECTING_DRONE:
				case DroneConsts.ERROR_NAVDATA:
				case DroneConsts.ERROR_CONFIG:
					handleError(msg.what);
					break;

				case DroneConsts.CANCEL:
					statusCANCEL(msg);
					break;

				case DroneConsts.SUCCESS:
					statusSUCCESS(msg);
					break;
			}
		}
	};

	private void errorERRORSCANNING() {
		tvWifiConnectionError.setText(getApplicationContext().getResources().getString(R.string.drone_scan_err));
		ivWifiScanStatus.setImageDrawable(getApplicationContext().getResources().getDrawable(R.drawable.no_icon));
		ivWifiScanStatus.setVisibility(ImageView.VISIBLE);
		pbConnectionStatus.setVisibility(ProgressBar.GONE);
	}

	private void errorERRORFINDINGDRONE() {
		tvWifiConnectionError.setText(getApplicationContext().getResources()
				.getString(R.string.drone_finding_drone_err));
		ivWifiScanStatus.setImageDrawable(getApplicationContext().getResources().getDrawable(R.drawable.no_icon));
		ivWifiScanStatus.setVisibility(ImageView.VISIBLE);
		pbConnectionStatus.setVisibility(ProgressBar.GONE);
	}

	private void errorERRORCONNECTINGDRONE() {
		tvWifiConnectionError.setText(getApplicationContext().getResources()
				.getString(R.string.drone_connect_drone_err));
		ivDroneConnectStatus.setImageDrawable(getApplicationContext().getResources().getDrawable(R.drawable.no_icon));
		ivDroneConnectStatus.setVisibility(ImageView.VISIBLE);
		pbConnectionStatus.setVisibility(ProgressBar.GONE);

		errorCount = 0; // TODO, necessary?
	}

	private void errorERRORNAVDATA() {
		tvWifiConnectionError.setText(getApplicationContext().getResources().getString(R.string.drone_navdata_err));
		ivGetNavdataStatus.setImageDrawable(getApplicationContext().getResources().getDrawable(R.drawable.no_icon));
		ivGetNavdataStatus.setVisibility(ImageView.VISIBLE);
		pbConnectionStatus.setVisibility(ProgressBar.GONE);
	}

	private void errorERRORCONFIG() {
		tvWifiConnectionError.setText(getApplicationContext().getResources().getString(R.string.drone_config_err));
		ivCheckConfigStatus.setImageDrawable(getApplicationContext().getResources().getDrawable(R.drawable.no_icon));
		ivCheckConfigStatus.setVisibility(ImageView.VISIBLE);
		pbConnectionStatus.setVisibility(ProgressBar.GONE);
	}

	private void handleError(int error) {
		runConnectThread = false;
		connectThread = null;

		if (wifiManager != null) {
			// TODO Remove line
			// wifiManager.setWifiEnabled(false);
			try {
				unregisterReceiver(wifiStateChangedReceiver);
			} catch (IllegalArgumentException e) {

			}
		}

		switch (error) {
			case DroneConsts.ERROR_SCANNING:
				errorERRORSCANNING();
				break;

			case DroneConsts.ERROR_FINDING_DRONE:
				errorERRORFINDINGDRONE();
				break;

			case DroneConsts.ERROR_CONNECTING_DRONE:
				errorERRORCONNECTINGDRONE();
				break;

			case DroneConsts.ERROR_NAVDATA:
				errorERRORNAVDATA();
				break;

			case DroneConsts.ERROR_CONFIG:
				errorERRORCONFIG();
				break;
		}

		startButton.setClickable(true);
		startButton.setEnabled(true);
		startButton.setText(R.string.drone_bt_wifi_start);
	}

	private BroadcastReceiver wifiStateChangedReceiver = new BroadcastReceiver() {

		@Override
		public synchronized void onReceive(Context context, Intent intent) {

			// WIFI_STATE_ENABLED
			if (intent.getAction().equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
				if (intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 10) == WifiManager.WIFI_STATE_ENABLED) {
					Log.d(DroneConsts.DroneLogTag, "WIFI_STATE_CHANGED_ACTION -> WIFI_STATE_ENABLED");

					unregisterReceiver(wifiStateChangedReceiver);
					IntentFilter intentFilter = new IntentFilter();
					intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
					registerReceiver(wifiStateChangedReceiver, intentFilter);

					changeStatus(DroneConsts.SCANNING);
				}
			}

			// SCAN_RESULTS_AVAILABLE_ACTION
			if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION) && wifiManager.isWifiEnabled()) {
				Log.d(DroneConsts.DroneLogTag, "SCAN_RESULTS_AVAILABLE_ACTION");

				scanned = true;

				unregisterReceiver(wifiStateChangedReceiver);
				IntentFilter intentFilter = new IntentFilter();
				intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
				registerReceiver(wifiStateChangedReceiver, intentFilter);

				if (foundDrone()) {
					changeStatus(DroneConsts.CONNECTING_TO_DRONE_WIFI);
				} else {
					changeStatus(DroneConsts.ERROR_FINDING_DRONE);
					return;
				}
			}

			// NETWORK_STATE_CHANGED_ACTION
			if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION) && wifiManager.isWifiEnabled()) {
				NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
				Log.d(DroneConsts.DroneLogTag, "SUPPLICANT_CONNECTION_CHANGE_ACTION " + info.isConnected() + " "
						+ wifiManager.getConnectionInfo().getSSID());

				if (info.isConnected()) {
					if (checkConnectionToDrone()) {
						/**
						 * TODO check firmware version first
						 * Check Firmware Version, BEFORE connectiong NAVDATA, CMD Sockets
						 */
						changeStatus(DroneConsts.CHECKING_FIRMWARE);
					} else if (errorCount++ > 3) {
						changeStatus(DroneConsts.ERROR_CONNECTING_DRONE);
						return;
					}
				}
			}
		}
	};

	private synchronized boolean enableWifi() {
		Log.d(DroneConsts.DroneLogTag, "enableWifi()");
		try {
			if (!wifiManager.isWifiEnabled()) {
				wifiManager.setWifiEnabled(true);

				int counter = 0;
				while (!wifiManager.isWifiEnabled()) {
					Thread.sleep(500);
					if (counter++ > 10) {
						return false;
					}
				}
			}

		} catch (Exception e) {
			Log.d(DroneConsts.DroneLogTag, "Exception WifiConnector -> enableWifi()");
			e.printStackTrace();
			return false;
		}

		return true;
	}

	private synchronized boolean scan() {
		Log.d(DroneConsts.DroneLogTag, "scan()");
		return wifiManager.startScan();
	}

	private synchronized boolean foundDrone() {
		Log.d(DroneConsts.DroneLogTag, "foundDrone()");
		List<ScanResult> result = wifiManager.getScanResults();
		for (ScanResult element : result) {
			if (element.SSID.contains("ardrone_") || element.SSID.contains("ardrone2_")) {
				return true;
			}
		}
		return false;
	}

	private synchronized boolean connectToDrone() {
		Log.d(DroneConsts.DroneLogTag, "connectToDrone()");

		// look through scanresults
		List<ScanResult> result = wifiManager.getScanResults();

		int foundDrones = 0;
		wifiConfigs = new LinkedList<WifiConfiguration>();
		for (ScanResult element : result) {
			if (element.SSID.contains("ardrone_") || element.SSID.contains("ardrone2_")) {
				WifiConfiguration wifiConfig = new WifiConfiguration();
				wifiConfig.BSSID = element.BSSID;
				wifiConfig.SSID = "\"".concat(element.SSID).concat("\"");
				wifiConfig.status = WifiConfiguration.Status.DISABLED;
				wifiConfig.priority = 1;
				wifiConfigs.add(wifiConfig);
				foundDrones++;
			}
		}

		// for testing with 2 drones
		// foundDrones++;
		// WifiConfiguration wifiConfig = new WifiConfiguration();
		// wifiConfig.BSSID = wifiConfigs.get(0).BSSID;
		// wifiConfig.SSID = wifiConfigs.get(0).SSID;
		// wifiConfig.status = wifiConfigs.get(0).status;
		// wifiConfig.priority = wifiConfigs.get(0).priority;
		// wifiConfigs.add(wifiConfig);

		if (foundDrones > 1) {
			changeStatus(DroneConsts.SELECT_DRONE_DIALOG);
		} else {
			return enableDroneNetwork(wifiConfigs.get(0));
		}

		return true;
	}

	private synchronized boolean enableDroneNetwork(WifiConfiguration wifiConfig) {
		List<WifiConfiguration> configs = wifiManager.getConfiguredNetworks();

		boolean alreadyConfigured = false;
		for (WifiConfiguration element : configs) {
			if (element.SSID.toString().equals(wifiConfig.SSID.toString())) {
				wifiConfig = element;
				alreadyConfigured = true;
				break;
			}
		}

		if (alreadyConfigured) {
			if (wifiManager.enableNetwork(wifiConfig.networkId, true)) {
				Log.d(DroneConsts.DroneLogTag, "enabled already configured network!");
				return true;
			} else {
				return false;
			}
		}

		// create new config
		wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
		wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
		wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
		wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);

		wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);

		wifiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
		wifiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);

		wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
		wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);

		wifiConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);

		// connect drone, create new config
		int netId = wifiManager.addNetwork(wifiConfig);
		if (netId == -1) {
			Log.d(DroneConsts.DroneLogTag, "failure: wifiManager.addNetwork(wifiConfig)");
		}

		if (!wifiManager.enableNetwork(netId, true)) {
			return false;
		}

		Log.d(DroneConsts.DroneLogTag, "enabled new network!");

		return true;
	}

	private synchronized boolean checkConnectionToDrone() {
		Log.d(DroneConsts.DroneLogTag, "checkConnectionToDrone()");
		WifiInfo info = wifiManager.getConnectionInfo();
		String ssid = info.getSSID();
		if (ssid != null) {
			if ((ssid.contains("ardrone_") || ssid.contains("ardrone2_")) && wifiManager.pingSupplicant()) {
				return true;
			}
		}
		return false;
	}

	private synchronized boolean waitForNavData() {
		Log.d(DroneConsts.DroneLogTag, "waitForNavData()");
		try {
			int counter = 0;
			while (DroneHandler.getInstance().getDrone().getBatteryLoad() == -1) {
				Thread.sleep(100);
				if (counter++ > 30) {
					return false;
				}
			}
		} catch (Exception e) {
			Log.e(DroneConsts.DroneLogTag, "Exception DroneWifiConnectionActivity -> waitForNavData()");
		}
		return true;
	}

	private synchronized boolean checkConfig() {
		Log.d(DroneConsts.DroneLogTag, "checkConfig()");
		if (configured) {
			return true;
		}

		if (!DroneHandler.getInstance().getDrone().doStartUpConfiguration()) {
			return false;
		}

		configured = true;
		return true;
	}

	private synchronized boolean checkFirmware() {
		Log.d(DroneConsts.DroneLogTag, "checkFirmware()");
		// Move to Drone Consts
		String supportedDrone1firmwareVersion = "1.7.4";
		String supportedDrone2firmwareVersion = "2.1.18";
		String droneFirmwareVersion = "";
		droneFirmwareVersion = DroneHandler.getInstance().getDrone().getFirmwareVersion();

		if (droneFirmwareVersion.equals(supportedDrone1firmwareVersion)
				|| droneFirmwareVersion.equals(supportedDrone2firmwareVersion)) {
			Log.d(DroneConsts.DroneLogTag, "checkFirmware() -> Firmware is Fine");
			return true;

		} else {
			Log.d(DroneConsts.DroneLogTag, "checkFirmware() -> Firmware to old");
			return false; // FIRMWARE is to old
		}
	}

	private class ConnectThread extends Thread {
		@Override
		public void run() {
			try {
				int timeoutCounter = 0;
				while (runConnectThread) {

					switch (status) {
						case DroneConsts.WIFI_ACTIVATING:
							status = DroneConsts.WAITING;
							if (!wifiManager.isWifiEnabled()) {
								// BroadcastReceiver is triggerd on result
								enableWifi();
							} else {
								changeStatus(DroneConsts.SCANNING);
							}
							break;

						case DroneConsts.SCANNING:
							status = DroneConsts.WAITING;
							if (scan()) {
								// do nothing, BroadcastReceiver is triggerd on result
							} else {
								changeStatus(DroneConsts.ERROR_SCANNING);
							}
							break;

						case DroneConsts.CONNECTING_TO_DRONE_WIFI:
							status = DroneConsts.WAITING;
							if (connectToDrone()) {
								// do nothing, BroadcastReceiver is triggerd on result
							} else {
								changeStatus(DroneConsts.ERROR_CONNECTING_DRONE);
							}
							break;

						case DroneConsts.CHECKING_FIRMWARE:
							status = DroneConsts.WAITING_FOR_FWCHECK;
							if (checkFirmware()) {
								changeStatus(DroneConsts.CONNECTING_TO_DRONE);
							} else {
								//TODO What should happen when firmware is to old?
								changeStatus(DroneConsts.CONNECTING_TO_DRONE);
							}
							break;

						case DroneConsts.CONNECTING_TO_DRONE:
							status = DroneConsts.WAITING;
							if (DroneHandler.getInstance().getDrone().connect()) {
								changeStatus(DroneConsts.WAITING_FOR_NAVDATA);
							} else {
								changeStatus(DroneConsts.ERROR_CONNECTING_DRONE);
								return; // TODO necessary?
							}
							break;

						case DroneConsts.WAITING_FOR_NAVDATA:
							status = DroneConsts.WAITING;
							if (waitForNavData()) {
								changeStatus(DroneConsts.CHECKING_CONFIG);
							} else {
								changeStatus(DroneConsts.ERROR_NAVDATA);
							}
							break;

						case DroneConsts.CHECKING_CONFIG:
							status = DroneConsts.WAITING;
							if (checkConfig()) {
								changeStatus(DroneConsts.SUCCESS);
							} else {
								changeStatus(DroneConsts.ERROR_CONFIG);
							}
							break;

						case DroneConsts.WAITING:
							Thread.sleep(100L);
							if (!scanned) {
								if (timeoutCounter++ > 100) {
									changeStatus(DroneConsts.ERROR_SCANNING);
								}
							}
							break;

						case DroneConsts.WAITING_FOR_FWCHECK:
							Log.e(DroneConsts.DroneLogTag, "Run Thread Status : WAITING_FOR_FWUPDATE");
							Thread.sleep(100L);
							break;

						default:
							// do nothing
							break;

					}
				}
			} catch (Exception e) {
				Log.e(DroneConsts.DroneLogTag, "Exception DroneWifiConnectionActivity ConnectThread", e);
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(DroneConsts.DroneLogTag, "AroneWifiConnectionActivity::onActivityResult()");
		switch (resultCode) {

			default:
				// nothing
				break;

		}

	}

}
