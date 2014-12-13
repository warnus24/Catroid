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
package org.catrobat.catroid.uitest.util;

import android.app.Instrumentation;
import android.bluetooth.BluetoothAdapter;

import junit.framework.Assert;

import org.catrobat.catroid.bluetooth.BTConnectDeviceActivity;

public final class BluetoothUtils {

	private BluetoothUtils() {}

	public static void enableBluetooth() {
		BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		Assert.assertTrue("Bluetooth not supported on device", bluetoothAdapter != null);

		if (!bluetoothAdapter.isEnabled()) {
			bluetoothAdapter.enable();
			try {
				Thread.sleep(4000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public static void disableBluetooth() {

		BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		Assert.assertTrue("Bluetooth not supported on device", bluetoothAdapter != null);

		if (bluetoothAdapter.isEnabled()) {
			bluetoothAdapter.disable();
			try {
				Thread.sleep(4000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public static boolean isBluetoothEnabled() {
		BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		Assert.assertTrue("Bluetooth not supported on device", bluetoothAdapter != null);

		return bluetoothAdapter.isEnabled();
	}

	public static void addPairedDevice(final String deviceName, final BTConnectDeviceActivity activity, Instrumentation instrumentation) {
		instrumentation.runOnMainSync(new Runnable() {
			@Override
			public void run() {
				activity.addPairedDevice(deviceName + "-00:00:00:00:00:00");
			}
		});
	}
}
