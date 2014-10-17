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
package org.catrobat.catroid.bluetoothtestserver;

//import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.LocalDevice;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;

public final class BTServer {
	private static final String TAG = BTServer.class.getSimpleName();

	static BTServer btServer;
	private static boolean gui = false;
	private static Writer out = null;
	private boolean run = true;

	public static final String LEGO_NXT_UUID = "0000110100001000800000805F9B34FB";
	public static final String COMMON_BT_TEST_UUID = "fd2835bb9d8041e097215372b90342da";

	private Collection<Client> supportedClients = new ArrayList<Client>();
	
	// Suppress default constructor for noninstantiability
	private BTServer() {
		
		supportedClients.add(new Client("LEGO NXT", LEGO_NXT_UUID));
		supportedClients.add(new Client("Common BT Test", COMMON_BT_TEST_UUID));
	}

	public static void writeMessage(String arg) {
		if (gui == false) {
			try {
				out.write(arg);
				out.flush();
			} catch (Exception localException) {
				// Log.e(TAG, "Exception in writeMessage!", localException);
			}
		} else {
			GUI.writeMessage(arg);
		}
	}

	public static void main(String[] args) {

		try {

			if (args.length == 0) {
				gui = true;
				GUI.startGUI();
			} else {
				out = new OutputStreamWriter(new FileOutputStream(args[0]));
			}

			printSystemConfiguration();

			btServer = new BTServer();
			btServer.startServer();
			
		} catch (IOException ioException) {
			ioException.printStackTrace();
			// Log.e(TAG, "IOexception!", ioException);
		}

	}

	private static void printSystemConfiguration()
			throws BluetoothStateException {
		LocalDevice localDevice = LocalDevice.getLocalDevice();
		writeMessage("Local System:\n");
		writeMessage("Address: "
				+ localDevice.getBluetoothAddress().replaceAll("(.{2})(?!$)",
						"$1:") + "\n");
		writeMessage("Name: " + localDevice.getFriendlyName() + "\n");
	}

	private void startServer() throws IOException {		
	
		writeMessage("Bluetooth Server started. Waiting for Bluetooth test clients... \n");
		writeMessage("Listening for: \n");
	
		for (Client client : supportedClients) {
			writeMessage("  - " + client.name + " (" + client.uuid + ")\n");
			new InputConnectionHandler(client).start();
		}
	}
	
	private class InputConnectionHandler extends Thread {
		
		private Client client;
		
		public InputConnectionHandler(Client client) {
			this.client = client;
		}
		
		@Override
		public void run() {
			try {
				tryHandleInputConnection();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		private void tryHandleInputConnection() throws IOException
		{
			String connectionString = "btspp://localhost:" + client.uuid
					+ ";name=BT Test Server";
			
			StreamConnectionNotifier streamConnNotifier = (StreamConnectionNotifier) Connector
					.open(connectionString);
			
			while (BTServer.this.run) {
				StreamConnection connection = streamConnNotifier.acceptAndOpen();
				
				BTServer.writeMessage("Incomming connection for " + client.name + "\n");
				
				BTClientHandler btc = BluetoothClientHandlerFactory.create(client.uuid);
				btc.setConnection(connection);
				
				btc.start();
			}
			
			streamConnNotifier.close();
		}
		
	}
}
