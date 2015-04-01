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

package org.catrobat.catroid.common.bluetooth.models;


import android.util.Log;

import org.catrobat.catroid.devices.albert.AlbertReceiveSensorCommands;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;

public class AlbertModel implements DeviceModel {

	private static final String TAG = AlbertModel.class.getSimpleName();
	private DataInputStream inputStream;
	private OutputStream outputStream;
	private int distanceLeft;
	private int distanceRight;
	private int size;
	private boolean run;

	@Override
	public void start(DataInputStream inputStream, OutputStream outStream) throws IOException {
		this.inputStream = inputStream;
		this.outputStream = outStream;
		this.distanceLeft = 50;
		this.distanceRight = 60;
		this.size = 36;
		this.run = true;
	}

	@Override
	public void stop() {
		run = false;
	}

	public void sendSensorCommands(final int distanceLeft, final int distanceRight, final int size) throws IOException {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					AlbertReceiveSensorCommands sensorCommands = new AlbertReceiveSensorCommands(size);
					sensorCommands.setDistanceLeft(distanceLeft);
					sensorCommands.setDistanceRight(distanceRight);
					byte[] command = sensorCommands.getSensorCommandMessage();
					synchronized (outputStream) {
						outputStream.write(command);
						outputStream.flush();
					}
				} catch (IOException e) {
					Log.d(TAG, "Cannot send albert message");
				}
			}
		}).start();

	}
}
