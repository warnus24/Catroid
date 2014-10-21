/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2014 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.ServiceProvider;

import java.util.Set;

public class BTConnectDeviceActivity extends Activity {
	private static final int LENGTH_OF_FOO = 18; //TODO: figure out the meaning of the value

	public static final String SERVICE_TO_START = "org.catrobat.catroid.bluetooth.SERVICE";

	private BluetoothAdapter btAdapter;
	private ArrayAdapter<String> pairedDevicesArrayAdapter;
	private ArrayAdapter<String> newDevicesArrayAdapter;

	private static BTDeviceFactory btDeviceFactory;

	protected BTDeviceService deviceService;

	public static void setDeviceFactory(BTDeviceFactory deviceFactory) {
		btDeviceFactory = deviceFactory;
	}

	private AdapterView.OnItemClickListener deviceClickListener = new AdapterView.OnItemClickListener() {

		private String getSelectedBluetoothAddress(View view) {
			String info = ((TextView) view).getText().toString();
			if (info.lastIndexOf('-') != info.length() - LENGTH_OF_FOO) {
				return null;
			}

			return info.substring(info.lastIndexOf('-') + 1);
		}
		@Override
		public void onItemClick(AdapterView<?> av, View view, int position, long id) {

			String address = getSelectedBluetoothAddress(view);
			if (address == null) {
				return;
			}

			btAdapter.cancelDiscovery();

			BluetoothConnection connection = new BluetoothConnection(address, deviceService.getBluetoothDeviceUUID());

			BluetoothConnection.State connectionState = connection.connect();
			BTErrorToaster.handle(connectionState, BTConnectDeviceActivity.this);

			int result = RESULT_CANCELED;

			if (connectionState == BluetoothConnection.State.CONNECTED) {
				deviceService.setConnection(connection);
				ServiceProvider.registerService(deviceService.getServiceType(), deviceService);
				result = RESULT_OK;
				BTDeviceConnector btDeviceConnector = ServiceProvider.getService(BTDeviceConnector.class);
				btDeviceConnector.deviceConnected(deviceService);
			}

			setResult(result);
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
				setTitle(R.string.select_device + deviceService.getName());
				if (newDevicesArrayAdapter.isEmpty()) {
					String noDevices = getResources().getString(R.string.none_found);
					newDevicesArrayAdapter.add(noDevices);
				}
			}
		}
	};

	protected void setDeviceService() {
		Class<BTDeviceService> serviceType = (Class<BTDeviceService>)getIntent().getSerializableExtra(SERVICE_TO_START);

		if (btDeviceFactory == null) {
			btDeviceFactory = new BTDeviceFactoryImpl();
		}

		deviceService = btDeviceFactory.create(serviceType, this.getApplicationContext());
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

		int bluetoothState = enableBluetooth();
		if (bluetoothState == BluetoothManager.BLUETOOTH_ALREADY_ON) {
			connectDevice();
		}
	}

	private void connectDevice() {
		setDeviceService();

		setContentView(R.layout.device_list);
		setTitle(deviceService.getName());

		Button scanButton = (Button) findViewById(R.id.button_scan);
		scanButton.setOnClickListener(new View.OnClickListener() {
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

		btAdapter = BluetoothAdapter.getDefaultAdapter();

		Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();

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

		this.setVisible(true);
	}

	@Override
	protected void onDestroy() {
		if (btAdapter != null) {
			btAdapter.cancelDiscovery();
		}

		try {
			this.unregisterReceiver(receiver);
		}
		catch (IllegalArgumentException e) {
		}
		super.onDestroy();
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

	public int enableBluetooth() {

		BluetoothManager bluetoothManager = new BluetoothManager(this);

		int bluetoothState = bluetoothManager.activateBluetooth();
		if (bluetoothState == BluetoothManager.BLUETOOTH_NOT_SUPPORTED) {
			Toast.makeText(this, R.string.notification_blueth_err, Toast.LENGTH_LONG).show();
			setResult(Activity.RESULT_CANCELED);
			finish();
		}

		return bluetoothState;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		switch (requestCode) {

			case BluetoothManager.REQUEST_ENABLE_BT:
				handleEnableBTResult(resultCode);
				break;
		}

	}

	private void handleEnableBTResult(int resultCode) {
		if (resultCode == Activity.RESULT_OK) {
			connectDevice();
			return;
		}

		Toast.makeText(this, R.string.notification_blueth_err, Toast.LENGTH_LONG).show();
		setResult(Activity.RESULT_CANCELED);
		finish();
	}
}
