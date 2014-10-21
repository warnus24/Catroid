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

package org.catrobat.catroid.test.mindstorms.nxt;

import junit.framework.Assert;

import org.catrobat.catroid.lego.mindstorm.MindstormCommand;
import org.catrobat.catroid.lego.mindstorm.MindstormConnection;
import org.catrobat.catroid.lego.mindstorm.nxt.CommandByte;
import org.catrobat.catroid.lego.mindstorm.nxt.CommandType;
import org.catrobat.catroid.lego.mindstorm.nxt.NXTReply;

import java.util.LinkedList;
import java.util.Queue;

public class MindstormTestConnection implements MindstormConnection {

	private Queue<MindstormCommand> sentCommands;


	public MindstormTestConnection() {
		Assert.assertTrue("Need a assert in class", true);
		this.sentCommands = new LinkedList<MindstormCommand>();
	}

	private boolean isConnected = false;

	@Override
	public void init() {
		isConnected = true;
	}

	@Override
	public boolean isConnected() {
		return isConnected;
	}

	@Override
	public void disconnect() {
		isConnected = false;
	}

	@Override
	public byte[] sendAndReceive(MindstormCommand command) {
		send(command);
		return receive(command);
	}

	@Override
	public void send(MindstormCommand command) {
		this.sentCommands.add(command);
	}


	protected byte[] receive(MindstormCommand command) {
		byte[] reply;
		byte commandType = command.getRawCommand()[0];
		byte commandByte = command.getRawCommand()[1];

		if (commandType != 0x00) {
			return null;
		}

		if (commandByte == CommandByte.SET_INPUT_MODE.getByte()) {
			reply = new byte[3];

			reply[0] = CommandType.REPLY_COMMAND.getByte();
			reply[1] = commandByte;
			reply[2] = NXTReply.NO_ERROR;

		} else if (commandByte == CommandByte.GET_INPUT_VALUES.getByte()) {
			byte inputPort = command.getRawCommand()[2];
			reply = new byte[16];

			reply[0] = CommandType.REPLY_COMMAND.getByte();
			reply[1] = commandByte;
			reply[2] = NXTReply.NO_ERROR;
			reply[3] = inputPort;

		} else if (commandByte == CommandByte.LS_WRITE.getByte()) {
			reply = new byte[3];

			reply[0] = CommandType.REPLY_COMMAND.getByte();
			reply[1] = commandByte;
			reply[2] = NXTReply.NO_ERROR;

		} else if (commandByte == CommandByte.LS_GET_STATUS.getByte()) {
			reply = new byte[4];

			reply[0] = CommandType.REPLY_COMMAND.getByte();
			reply[1] = commandByte;
			reply[2] = NXTReply.NO_ERROR;
			reply[3] = 1;//Bytes Ready

		} else if (commandByte == CommandByte.LS_READ.getByte()) {
			reply = new byte[20];

			reply[0] = CommandType.REPLY_COMMAND.getByte();
			reply[1] = commandByte;
			reply[2] = NXTReply.NO_ERROR;
			reply[3] = 1;//Bytes Read

		} else if (commandByte == CommandByte.RESET_INPUT_SCALED_VALUE.getByte()) {
			reply = new byte[3];

			reply[0] = CommandType.REPLY_COMMAND.getByte();
			reply[1] = commandByte;
			reply[2] = NXTReply.NO_ERROR;

		} else {
			reply = null;
		}

		return reply;
	}

	public MindstormCommand getLastSentCommand(){
		return this.sentCommands.poll();
	}
}
