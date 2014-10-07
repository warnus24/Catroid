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
package org.catrobat.catroid.lego.mindstorm.nxt;

import org.catrobat.catroid.lego.mindstorm.MindstormCommand;

import java.io.ByteArrayOutputStream;

public class Command implements MindstormCommand {

	private CommandType commandType;
	private CommandByte commandByte;

	private boolean replyRequired;
	//private List<Byte> commandData = new ArrayList<Byte>();
	private ByteArrayOutputStream commandData = new ByteArrayOutputStream();

	public Command(CommandType commandType, CommandByte commandByte, boolean reply) {

		this.commandType = commandType;
		this.commandByte = commandByte;
		if (reply){
			replyRequired = true;
			commandData.write(commandType.getByte());
		}
		else{
			replyRequired = false;
			commandData.write((byte) (commandType.getByte() | 0x80));
		}
		commandData.write(commandByte.getByte());
	}

	public void append(byte data) {
		commandData.write(data);
	}

	public void append(byte[] data) {
		commandData.write(data, 0, data.length);
	}

	public void append(int data) {
		append((byte)data);
		append((byte)(data >> 8));
		append((byte)(data >> 16));
		append((byte)(data >> 24));
	}

	@Override
	public int getLength() {
		return commandData.size();
	}

	public byte[] getRawCommand() {
		return commandData.toByteArray();
	}
}