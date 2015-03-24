/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
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

package org.catrobat.catroid.devices.albert;

import android.util.Log;

import org.catrobat.catroid.bluetooth.base.BluetoothConnection;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class AlbertConnection {

	private final BluetoothConnection connection;
	private static final String TAG = AlbertConnection.class.getSimpleName();
	private OutputStream outputStream;
	private InputStream inputStream;

	private boolean connected = false;

	public AlbertConnection(BluetoothConnection connection){
		this.connection = connection;
		try {
			outputStream = connection.getOutputStream();
			inputStream = connection.getInputStream();
			connected = true;
		} catch (IOException e) {
			Log.d(TAG, "Cannot get Albert connection streams");
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				while(connected) {
					try {
						inputStream.read();
					} catch (IOException e) {
						Log.d(TAG, "Cannot receive albert message");
					}
				}
			}
		}).start();
	}

	public void disconnect(){
		connected = false;
		connection.disconnect();
	}

	public void send(AlbertState commands) {
		try {
			outputStream.write(commands.getCommandMessage());
		} catch (IOException e) {
			Log.d(TAG, "Cannot send Albert commands");
		}
	}

}
