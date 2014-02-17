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
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.catrobat.catroid.bluetooth.BTConnection;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is for talking to a LEGO NXT robot via bluetooth.
 * The communciation to the robot is done via LCP (LEGO communication protocol).
 * Objects of this class can either be run as standalone thread or controlled
 * by the owners, i.e. calling the send/recive methods by themselves.
 */
public class LegoNXTBtCommunicator extends BTConnection implements Runnable {

    //==============================================================================================

    public static final int MOTOR_A = 0;
    public static final int MOTOR_B = 1;
    public static final int MOTOR_C = 2;
    public static final int MOTOR_B_ACTION = 40;
    public static final int MOTOR_RESET = 10;
    public static final int DO_BEEP = 51;
    public static final int DO_ACTION = 52;
    public static final int READ_MOTOR_STATE = 60;
    public static final int GET_FIRMWARE_VERSION = 70;
    public static final int DISCONNECT = 99;

    public static final int DISPLAY_TOAST = 1000;
    public static final int STATE_CONNECTED = 1001;
    public static final int STATE_CONNECTERROR = 1002;
    public static final int STATE_CONNECTERROR_PAIRING = 1022;
    public static final int MOTOR_STATE = 1003;
    public static final int STATE_RECEIVEERROR = 1004;
    public static final int STATE_SENDERROR = 1005;
    public static final int FIRMWARE_VERSION = 1006;
    public static final int FIND_FILES = 1007;
    public static final int START_PROGRAM = 1008;
    public static final int STOP_PROGRAM = 1009;
    public static final int GET_PROGRAM_NAME = 1010;
    public static final int PROGRAM_NAME = 1011;
    public static final int SAY_TEXT = 1030;
    public static final int VIBRATE_PHONE = 1031;
    public static final int RECEIVED_MESSAGE = 1111;

    public static final int NO_DELAY = 0;
    public static final int GENERAL_COMMAND = 100;
    public static final int MOTOR_COMMAND = 102;
    public static final int TONE_COMMAND = 101;

    protected static List<byte[]> receivedMessages = new ArrayList<byte[]>();
    protected byte[] returnMessage;

    private static boolean requestConfirmFromDevice = false;

    //==============================================================================================

    protected Resources resources;

    private static final String TAG = LegoNXTBtCommunicator.class.getSimpleName();

	private OutputStream nxtOutputStream;
	private InputStream nxtInputStream;
    //Handler uiHandler;

	//private LegoNXT myOwner;

	public LegoNXTBtCommunicator(LegoNXT myOwner, Resources resources, String macAddress) {
        super(macAddress);
		//super(uiHandler, resources, macAddress);
		//this.myOwner = myOwner;
        //this.uiHandler = uiHandler;
	}

    //==============================================================================================

    public static void enableRequestConfirmFromDevice(boolean cfd) {
        requestConfirmFromDevice = cfd;
    }

    protected void sendMessageAndState(byte[] message) {
        try {
            sendMessage(message);
        } catch (IOException e) {
            //sendState(STATE_SENDERROR);
        }
    }

    protected void dispatchMessage(byte[] message) {

        //Log.i("bt", "Received response, length: " + message.length);
        //		for (int i = 0; i < message.length; i++) {
        //			Log.i("bt", " " + (0x000000FF & message[i]));
        //		}

        switch (message[1]) {

            case LCPMessage.SET_OUTPUT_STATE:
                //sendState(RECEIVED_MESSAGE, message);
                analyzeMessageSetOutputState(message);
                break;

            case LCPMessage.GET_OUTPUT_STATE:
                //sendState(RECEIVED_MESSAGE, message);
                receivedMessages.add(message);
                analyzeMessageGetOutputState(message);
                break;
            default:
                Log.i("bt", "Unknown Message received by LegoNXTCommunicator over bluetooth " + message.length);
                receivedMessages.add(message);
                break;
        }
    }

    protected void analyzeMessageSetOutputState(byte[] message) {
        //change command byte0 to DIRECT_COMMAND_REPLY to use!
        Log.i("bt", "Direct command executed: " + (int) message[0]);
        Log.i("bt", "executed Command was: " + (int) message[1]);
        Log.i("bt", "Status: " + (int) message[2]);
        Log.i("bt", "Length: " + message.length);

    }

    protected void analyzeMessageGetOutputState(byte[] message) {
        //See Lego NXT Docu or LCPMessage class for info on numbers!
        Log.i("bt", "Message Length: " + message.length);
        Log.i("bt", "GetOutputState executed: " + (int) message[0]);
        //		Log.i("bt", "----- executed Command:  " + (int) message[1]);
        //		Log.i("bt", "Status: " + (int) message[2]);
        //		Log.i("bt", "Used Motor: " + (int) message[3]);
        //		Log.i("bt", "Used Power: " + (int) message[4]);
        //Log.i("bt", "Mode: " + (int) message[5]);
        //Log.i("bt", "Regulation: " + (int) message[6]);
        //Log.i("bt", "Turn Ratio: " + (int) message[7]);
        //Log.i("bt", "Run State: " + (int) message[8]);

        //		int tacholimit = (0x000000FF & message[9]); //unsigned types would be too smart for java, sorry no chance mate!
        //		tacholimit += ((0x000000FF & message[10]) << 8);
        //		tacholimit += ((0x000000FF & message[11]) << 16);
        //		tacholimit += ((0x000000FF & message[12]) << 24);

        //Log.i("bt", "Tacholimit " + tacholimit);
		/*
		 * int tachocount = message[13];
		 * tachocount += (message[14] << 8);
		 * tachocount += (message[15] << 16);
		 * tachocount += (message[16] << 24);
		 *
		 * Log.i("bt", "Tachocount " + tachocount);
		 */
    }

    protected void doBeep(int frequency, int duration) {
        byte[] message = LCPMessage.getBeepMessage(frequency, duration);
        sendMessageAndState(message);
        waitSomeTime(20);
    }

    protected void waitSomeTime(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
        }
    }

    protected synchronized void moveMotor(int motor, int speed, int end) {
        byte[] message = LCPMessage.getMotorMessage(motor, speed, end);
        sendMessageAndState(message);
        //Log.i("bto", "Motor " + motor + " speed " + speed);

        if (requestConfirmFromDevice) {
            byte[] test = LCPMessage.getOutputStateMessage(motor);
            sendMessageAndState(test);
        }
    }

    public Handler getHandler() {
		return myHandler;
	}


    //==============================================================================================

	/**
	 * Creates the connection, waits for incoming messages and dispatches them. The thread will be terminated
	 * on closing of the connection.
	 */
	public void run() {
		while (getState() == States.CONNECTED) {
			try {
				returnMessage = receiveMessage();
				if ((returnMessage.length >= 2)
						&& ((returnMessage[0] == LCPMessage.REPLY_COMMAND) || (returnMessage[0] == LCPMessage.DIRECT_COMMAND_NOREPLY))) {
					dispatchMessage(returnMessage);
				}
			} catch (IOException e) {
				// don't inform the user when connection is already closed
				if (getState() == States.CONNECTED) {
					//sendState(STATE_RECEIVEERROR);
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

	public void createNXTconnection() throws IOException {
		States state = connect();
        setState(state);
		switch (state) {
			case CONNECTED:
				//sendState(STATE_CONNECTED);
				nxtOutputStream = getBTSocket().getOutputStream();
				nxtInputStream = getBTSocket().getInputStream();
				break;
			case ERROR_ADAPTER:
				//sendState(STATE_CONNECTERROR);
				throw new IOException();
			case ERROR_BONDING:
				//sendState(STATE_CONNECTERROR_PAIRING);
				throw new IOException();
			case ERROR_SOCKET:
				//sendState(STATE_CONNECTERROR);
				throw new IOException();

			default:
				throw new IOException();
		}
	}

	/**
	 * Closes the bluetooth connection. On error the method either sends a message
	 * to it's owner or creates an exception in the case of no message handler.
	 */
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
            Log.e(TAG, Log.getStackTraceString(ex));
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

    // receive messages from the UI
    // TODO should be fixed - could lead to problems
    final Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message myMessage) {
            switch (myMessage.what) {
                case TONE_COMMAND:
                    doBeep(myMessage.getData().getInt("frequency"), myMessage.getData().getInt("duration"));
                    break;
                case DISCONNECT:
                    break;
                default:
                    int motor;
                    int speed;
                    int angle;
                    motor = myMessage.getData().getInt("motor");
                    speed = myMessage.getData().getInt("speed");
                    angle = myMessage.getData().getInt("angle");
                    moveMotor(motor, speed, angle);
                    break;
            }
        }
    };

}
