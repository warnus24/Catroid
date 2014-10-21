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

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.bluetooth.RemoteDevice;
import javax.microedition.io.StreamConnection;

public abstract class BTClientHandler extends Thread
{
    private static final String TAG = BTClientHandler.class.getSimpleName();

    private StreamConnection connection;
    
    public void setConnection(StreamConnection connection)
    {
    	this.connection = connection;
    }

    public void run() {
        String handledClient = "null";
        try {
            handledClient = tryHandleClient();
        }
        catch (IOException ioException) {
        	Log.e(TAG, "IO Exception", ioException);
        }

        BTServer.writeMessage("Client " + handledClient + " disconnected!\n");
    }

    private String tryHandleClient() throws IOException {
        RemoteDevice dev = RemoteDevice.getRemoteDevice(this.connection);
        String client = dev.getFriendlyName(true);

		BTServer.writeMessage("Address: " + dev.getBluetoothAddress().replaceAll("(.{2})(?!$)", "$1:") + "\n");
        BTServer.writeMessage("Remote device name: " + client + "\n");

        InputStream inStream = this.connection.openInputStream();
        OutputStream outStream = this.connection.openOutputStream();
        
        this.handle(inStream, outStream);

    	outStream.close();
        inStream.close();
        this.connection.close();        

        return client;
    }
    
    public abstract void handle(InputStream inStream, OutputStream outStream) throws IOException;
}
