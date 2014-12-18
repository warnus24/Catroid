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

package org.catrobat.catroid.arduino;

import org.catrobat.catroid.bluetooth.BluetoothConnection;
import org.catrobat.catroid.arduino.ArduinoCommand;
import org.catrobat.catroid.arduino.ArduinoConnection;
import org.catrobat.catroid.arduino.ArduinoException;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;

public class ArduinoConnectionImpl implements ArduinoConnection {

	private BluetoothConnection bluetoothConnection;
	private OutputStream arduinoOutputStream = null;
	private DataInputStream arduinoInputStream = null;

	private boolean isConnected = false;

	public ArduinoConnectionImpl(BluetoothConnection btConnection) {
		this.bluetoothConnection = btConnection;
	}

	@Override
	public void init() {

		try {
			arduinoInputStream = new DataInputStream(bluetoothConnection.getInputStream());
			arduinoOutputStream = bluetoothConnection.getOutputStream();
			isConnected = true;
		} catch (IOException e) {
			isConnected = false;
			throw new ArduinoException(e, "Cannot establish BtConnection");
		}
	}

	@Override
	public boolean isConnected() {
		return isConnected;
	}


	@Override
	public void disconnect() {

		isConnected = false;

		bluetoothConnection.disconnect();

		arduinoInputStream = null;
		arduinoOutputStream = null;

	}

	@Override
	public synchronized byte[] sendAndReceive(ArduinoCommand command) {
		send(command);
		return receive();
	}

	@Override
	public synchronized void send(ArduinoCommand command) {
		try {
			int messageLength = command.getLength();
			byte[] message = command.getRawCommand();
			byte[] data = new byte[command.getLength() + 2];
			data[0] = (byte)(messageLength & 0x00FF);
			data[1] = (byte)((messageLength & 0xFF00) >> 8) ;

			System.arraycopy(message, 0, data, 2, messageLength);

			arduinoOutputStream.write(data, 0, messageLength + 2);
			arduinoOutputStream.flush();

		} catch (IOException e) {
			throw new ArduinoException(e, "Error on message send.");
		}
	}

	protected synchronized byte[] receive() {
		byte[] data = new byte[2];
		byte[] payload;

		try {
			arduinoInputStream.readFully(data, 0, 2);
			int expectedLength = ((data[0] & 0xFF) | (data[1] & 0xFF) << 8);
			payload = new byte[expectedLength];

			arduinoInputStream.readFully(payload, 0, expectedLength);
		}
		catch (IOException e) {
			throw new ArduinoException(e, "Read Error");
		}

		return payload;
	}
}
