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
package org.catrobat.catroid.legonxt;

import android.content.res.Resources;
import android.os.Handler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * This class is for talking to a LEGO NXT robot via bluetooth.
 * The communciation to the robot is done via LCP (LEGO communication protocol).
 * Objects of this class can either be run as standalone thread or controlled
 * by the owners, i.e. calling the send/recive methods by themselves.
 */
public class LegoNXTBtCommunicator extends LegoNXTCommunicator {

	private OutputStream nxtOutputStream;
	private InputStream nxtInputStream;

	//private static final UUID SERIAL_PORT_SERVICE_CLASS_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	// this is the only OUI registered by LEGO, see http://standards.ieee.org/regauth/oui/index.shtml

	//private LegoNXT myOwner;

	public LegoNXTBtCommunicator(LegoNXT myOwner, Handler uiHandler, Resources resources, String macAddress) {
		super(uiHandler, resources, macAddress);
		//this.myOwner = myOwner;
	}

	/**
	 * Creates the connection, waits for incoming messages and dispatches them. The thread will be terminated
	 * on closing of the connection.
	 */
	@Override
	public void run() {
		while (connected) {
			try {
				returnMessage = receiveMessage();
				if ((returnMessage.length >= 2)
						&& ((returnMessage[0] == LCPMessage.REPLY_COMMAND) || (returnMessage[0] == LCPMessage.DIRECT_COMMAND_NOREPLY))) {
					dispatchMessage(returnMessage);
				}

			} catch (IOException e) {
				// don't inform the user when connection is already closed
				if (connected) {
					sendState(STATE_RECEIVEERROR);
				}
				return;
			}
		}
	}

	/**
	 * Create a bluetooth connection with SerialPortServiceClass_UUID
	 * 
	 * @see <a href=
	 *      "http://lejos.sourceforge.net/forum/viewtopic.php?t=1991&highlight=android"
	 *      />
	 *      On error the method either sends a message to it's owner or creates an exception in the
	 *      case of no message handler.
	 */

	@Override
	public void createNXTconnection() throws IOException {
		States state = connect();
		switch (state) {
			case CONNECTED:
				sendState(STATE_CONNECTED);
				nxtOutputStream = getBTSocket().getOutputStream();
				nxtInputStream = getBTSocket().getInputStream();
				break;
			case ERROR_ADAPTER:
				sendState(STATE_CONNECTERROR);
				throw new IOException();
			case ERROR_BONDING:
				sendState(STATE_CONNECTERROR_PAIRING);
				throw new IOException();
			case ERROR_SOCKET:
				sendState(STATE_CONNECTERROR);
				throw new IOException();

			default:
				throw new IOException();
		}
	}

	/**
	 * Closes the bluetooth connection. On error the method either sends a message
	 * to it's owner or creates an exception in the case of no message handler.
	 */
	@Override
	public void destroyNXTconnection() {
		stopAllNXTMovement();
		try {
			if (nxtOutputStream != null) {
				nxtOutputStream.close();
			}
			if (nxtInputStream != null) {
				nxtInputStream.close();
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		//
		//		try {
		//			if (nxtBTsocket != null) {
		//				connected = false;
		//				nxtBTsocket.close();
		//				nxtBTsocket = null;
		//			}
		//
		//			nxtInputStream = null;
		//			nxtOutputStream = null;
		//
		//		} catch (IOException e) {
		//			if (uiHandler == null) {
		//				throw e;
		//			} else {
		//				sendToast(resources.getString(R.string.problem_at_closing));
		//			}
		//		}
	}

	@Override
	public void stopAllNXTMovement() {
		myHandler.removeMessages(0);
		myHandler.removeMessages(1);
		myHandler.removeMessages(2);

		moveMotor(0, 0, 0);
		moveMotor(1, 0, 0);
		moveMotor(2, 0, 0);
	}

	/**
	 * Sends a message on the opened OutputStream
	 * 
	 * @param message
	 *            , the message as a byte array
	 */
	@Override
	public void sendMessage(byte[] message) throws IOException {

		if (nxtOutputStream == null) {
			throw new IOException();
		}

		// send message length
		int messageLength = message.length;
		nxtOutputStream.write(messageLength);
		nxtOutputStream.write(messageLength >> 8);
		nxtOutputStream.write(message, 0, message.length);
		nxtOutputStream.flush();
	}

	/**
	 * Receives a message on the opened InputStream
	 * 
	 * @return the message
	 */
	@Override
	public byte[] receiveMessage() throws IOException {
		if (nxtInputStream == null) {
			throw new IOException();
		}

		int length = nxtInputStream.read();
		length = (nxtInputStream.read() << 8) + length;
		byte[] returnMessage = new byte[length];
		nxtInputStream.read(returnMessage);
		//Log.i("bt", returnMessage.toString());
		return returnMessage;
	}

	@Override
	public void onStagePause() {
		stopAllNXTMovement();
		super.onStagePause();
	}

	@Override
	public void onStageResume() {
		//stopAllNXTMovement();
		super.onStageResume();
	}

	@Override
	public void onStageDispose() {
		destroyNXTconnection();
		super.onStageDispose();
	}

}
