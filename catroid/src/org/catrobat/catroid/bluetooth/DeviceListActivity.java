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
 *
 *    This file incorporates work covered by the following copyright and  
 *    permission notice: 
 *
 *		   	Copyright 2010 Guenther Hoelzl, Shawn Brown
 *
 *		   	This file is part of MINDdroid.
 *
 * 		  	MINDdroid is free software: you can redistribute it and/or modify
 * 		  	it under the terms of the GNU Affero General Public License as
 * 		  	published by the Free Software Foundation, either version 3 of the
 *   		License, or (at your option) any later version.
 *
 *   		MINDdroid is distributed in the hope that it will be useful,
 *   		but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   		MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   		GNU Affero General Public License for more details.
 *
 *   		You should have received a copy of the GNU Affero General Public License
 *   		along with MINDdroid.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.catrobat.catroid.R;

import java.util.Set;

public class DeviceListActivity extends Activity {

	private static final int REQUEST_ENABLE_BT = 2000;

	public static final int BLUETOOTH_ALREADY_ON = 1;
	public static final int BLUETOOTH_ACTIVATING = 2;

	public static final int BLUETOOTH_NOT_SUPPORTED = -100;
	public static final int BLUETOOTH_ACTIVATION_CANCELED = -101;

	public static final String PAIRING = "pairing";
	public static final String DEVICE_NAME_AND_ADDRESS = "device_infos";
	public static final String EXTRA_DEVICE_ADDRESS = "device_address";
	public static final String RESOURCE_CONSTANT = "resource_constant";
	public static final String RESOURCE_NAME_TEXT = "resource_text";

	private BluetoothAdapter btAdapter;
	private ArrayAdapter<String> pairedDevicesArrayAdapter;
	private ArrayAdapter<String> newDevicesArrayAdapter;
	private int resourceConstant;
	private String resourceText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.setVisible(false);

		resourceConstant = this.getIntent().getExtras().getInt(RESOURCE_CONSTANT);
		resourceText = this.getIntent().getExtras().getString(RESOURCE_NAME_TEXT);

		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.device_list);
		setTitle(resourceText);

		setResult(Activity.RESULT_CANCELED);

		Button scanButton = (Button) findViewById(R.id.button_scan);
		scanButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				doDiscovery();
				view.setVisibility(View.GONE);
			}
		});

		pairedDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_name);
		newDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_name);

		ListView pairedListView = (ListView) findViewById(R.id.paired_devices);
		pairedListView.setAdapter(pairedDevicesArrayAdapter);
		pairedListView.setOnItemClickListener(deviceClickListener);

		ListView newDevicesListView = (ListView) findViewById(R.id.new_devices);
		newDevicesListView.setAdapter(newDevicesArrayAdapter);
		newDevicesListView.setOnItemClickListener(deviceClickListener);

		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		this.registerReceiver(receiver, filter);

		filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		this.registerReceiver(receiver, filter);

		//btAdapter = BluetoothAdapter.getDefaultAdapter();

		int activateBluetooth = activateBluetooth();
		if (activateBluetooth == BLUETOOTH_NOT_SUPPORTED) {
			errorHandling(BLUETOOTH_NOT_SUPPORTED);
		}

		if (activateBluetooth != BLUETOOTH_ACTIVATING) {
			addPairedDevices();
			this.setVisible(true);
		}

		//if (autoConnect && possibleConnections == 1) {
		//btAdapter.cancelDiscovery();
		//Intent intent = new Intent();
		//Bundle data = new Bundle();
		//data.putString(DEVICE_NAME_AND_ADDRESS, legoNXT.getName() + "-" + legoNXT.getAddress());
		//data.putString(EXTRA_DEVICE_ADDRESS, legoNXT.getAddress());
		//data.putBoolean(PAIRING, false);
		//data.putBoolean(AUTO_CONNECT, true);
		//intent.putExtras(data);
		//setResult(RESULT_OK, intent);
		//finish();
		//			this.setVisible(false);
		//} else {
		//this.setVisible(true);
		//}
		//autoConnect = true;
	}

	private void addPairedDevices() {
		Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();

		//BluetoothDevice legoNXT = null;
		//int possibleConnections = 0;
		if (pairedDevices.size() > 0) {
			findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE);
			for (BluetoothDevice device : pairedDevices) {
				pairedDevicesArrayAdapter.add(device.getName() + "-" + device.getAddress());
			}
		}

		if (pairedDevices.size() == 0) {
			String noDevices = getResources().getText(R.string.none_paired).toString();
			pairedDevicesArrayAdapter.add(noDevices);
		}
	}

	private int activateBluetooth() {
		btAdapter = BluetoothAdapter.getDefaultAdapter();
		if (btAdapter == null) {
			return BLUETOOTH_NOT_SUPPORTED;
		}
		if (!btAdapter.isEnabled()) {
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
			return BLUETOOTH_ACTIVATING;
		} else {
			//Log.d("TAG", "LOOP!!!!!");
			//connectLegoNXT();
			return BLUETOOTH_ALREADY_ON;
		}
	}

	private void errorHandling(int errorCode) {
		switch (errorCode) {
			case (BLUETOOTH_NOT_SUPPORTED):
				Toast.makeText(this, R.string.notification_blueth_err, Toast.LENGTH_LONG).show();
				setResult(BLUETOOTH_NOT_SUPPORTED);
				finish();
				break;
			case (BLUETOOTH_ACTIVATION_CANCELED):
				Toast.makeText(this, R.string.notification_blueth_err, Toast.LENGTH_LONG).show();
				setResult(BLUETOOTH_ACTIVATION_CANCELED);
				finish();
				break;
			default:
				setResult(BLUETOOTH_NOT_SUPPORTED);
				finish();
				break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (btAdapter != null) {
			btAdapter.cancelDiscovery();
		}

		this.unregisterReceiver(receiver);
	}

	private void doDiscovery() {

		setProgressBarIndeterminateVisibility(true);
		setTitle(R.string.scanning);

		findViewById(R.id.title_new_devices).setVisibility(View.VISIBLE);

		if (btAdapter.isDiscovering()) {
			btAdapter.cancelDiscovery();
		}

		btAdapter.startDiscovery();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.i("bt", "requestcode " + requestCode + " result code " + resultCode);

		switch (requestCode) {
			case REQUEST_ENABLE_BT:
				switch (resultCode) {
					case Activity.RESULT_OK:
						addPairedDevices();
						this.setVisible(true);
						break;
					case Activity.RESULT_CANCELED:
						errorHandling(BLUETOOTH_ACTIVATION_CANCELED);
						break;
					default:
						errorHandling(BLUETOOTH_ACTIVATION_CANCELED);
						break;
				}
				break;
		}
	}

	private OnItemClickListener deviceClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> av, View view, int arg2, long arg3) {

			String info = ((TextView) view).getText().toString();
			if (info.lastIndexOf('-') != info.length() - 18) {
				return;
			}

			btAdapter.cancelDiscovery();
			String address = info.substring(info.lastIndexOf('-') + 1);
			Intent intent = new Intent();
			Bundle data = new Bundle();
			data.putString(DEVICE_NAME_AND_ADDRESS, info);
			data.putString(EXTRA_DEVICE_ADDRESS, address);
			data.putBoolean(PAIRING, av.getId() == R.id.new_devices);
			data.putInt(RESOURCE_CONSTANT, resourceConstant);
			intent.putExtras(data);
			setResult(RESULT_OK, intent);
			finish();
		}
	};

	private final BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();

			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				if ((device.getBondState() != BluetoothDevice.BOND_BONDED)) {
					newDevicesArrayAdapter.add(device.getName() + "-" + device.getAddress());
				}
			} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
				setProgressBarIndeterminateVisibility(false);
				setTitle(R.string.select_device);
				if (newDevicesArrayAdapter.getCount() == 0) {
					String noDevices = getResources().getText(R.string.none_found).toString();
					newDevicesArrayAdapter.add(noDevices);
				}
			}
		}
	};

}
