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

import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.widget.Toast;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.CatrobatService;
import org.catrobat.catroid.common.ServiceProvider;

public class BTDeviceConnectionStateService extends Service {

	private BroadcastReceiver btStateReceiver;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		final IntentFilter disconnectedFilter = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);

		this.btStateReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {

				String action = intent.getAction();

				if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
					BTDeviceConnector btDeviceConnector = ServiceProvider.getService(CatrobatService.BLUETOOTH_DEVICE_CONNECTOR);
					btDeviceConnector.disconnectDevices();

					Toast.makeText(BTDeviceConnectionStateService.this.getApplicationContext(), R.string.bt_connection_failed, Toast.LENGTH_LONG);
				}
			}
		};

		this.registerReceiver(this.btStateReceiver, disconnectedFilter);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		this.unregisterReceiver(this.btStateReceiver);
	}
}
