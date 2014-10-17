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

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.catrobat.catroid.R;

public class BTErrorToaster {
	public static final String TAG = BTErrorToaster.class.getSimpleName();

	public static void handle (BluetoothConnection.State state , Context context) {
		Toast toast = null;

		switch (state) {
			case CONNECTED:
				break;
			case NOT_CONNECTED:
				break;
			case ERROR_BLUETOOTH_NOT_SUPPORTED:
				break;
			case ERROR_BLUETOOTH_NOT_ON:
				break;
			case ERROR_ADAPTER:
				Log.e(TAG, "ERROR_ADAPTER");
				toast = Toast.makeText(context, R.string.bt_connection_failed, Toast.LENGTH_SHORT);
				break;
			case ERROR_DEVICE:
				break;
			case ERROR_SOCKET:
				Log.e(TAG, "ERROR_SOCKET");
				toast = Toast.makeText(context, R.string.bt_connection_failed, Toast.LENGTH_SHORT);
				break;
			case ERROR_STILL_BONDING:
				break;
			case ERROR_NOT_BONDED:
				break;
			case ERROR_CLOSING:
				Log.e(TAG, "ERROR_CLOSING");
				toast = Toast.makeText(context, R.string.bt_connection_failed, Toast.LENGTH_SHORT);
				break;
		}

		if (toast != null) {
			toast.show();
		}
	}
}
