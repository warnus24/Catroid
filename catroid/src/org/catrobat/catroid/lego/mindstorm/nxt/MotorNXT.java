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
 */
package org.catrobat.catroid.lego.mindstorm.nxt;

import org.catrobat.catroid.lego.mindstorm.MindstormConnection;
import org.catrobat.catroid.lego.mindstorm.MindstormMotor;

public class MotorNXT implements MindstormMotor {

	private int port;
	private MindstormConnection connection;

	MotorNXT(int port, MindstormConnection connection) {
		this.port = port;
		this.connection = connection;
	}

	@Override
	public void stop() {

		OutputState state = new OutputState();
		state.speed = 0;
		state.mode = MotorMode.BREAK | MotorMode.ON | MotorMode.REGULATED;
		state.regulation = MotorRegulation.SPEED;
		state.turnRatio = 100;
		state.runState = MotorRunState.RUNNING;
		state.tachoLimit = 0;
		setOutputState(state, false);
	}

	private void setOutputState(OutputState state, boolean requestReply) {
		if(state.speed > 100){
			state.speed = 100;
		}
		if(state.speed < -100){
			state.speed = -100;
		}
		if(state.turnRatio > 100){
			state.turnRatio = 100;
		}
		if(state.turnRatio < -100){
			state.turnRatio = 100;
		}

		Command command = new Command(CommandType.DIRECT_COMMAND, CommandByte.SET_OUTPUT_STATE, false);
		command.append((byte)port);
		command.append(state.speed);
		command.append(state.mode);
		command.append(state.regulation.getByte());
		command.append(state.turnRatio);
		command.append(state.runState.getByte());
		command.append(state.tachoLimit);
		command.append((byte)0x00);

        if (requestReply) {
            NXTReply reply = new NXTReply(connection.sendAndReceive(command));
            NXTError.checkForError(reply, 3);
        }
        else {
            connection.send(command);
        }
	}

    @Override
    public void move(int speed, int degrees) {
        move(speed, degrees, false);
    }

	@Override
	public void move(int speed, int degrees, boolean reply) {
		OutputState state = new OutputState();
		state.speed = (byte)speed;
		state.mode = MotorMode.BREAK | MotorMode.ON | MotorMode.REGULATED;
		state.regulation =  MotorRegulation.SPEED;
		state.turnRatio = 100;
		state.runState = MotorRunState.RUNNING;
		state.tachoLimit = degrees;
		setOutputState(state, reply);
	}

	private static class OutputState {

		public byte speed;

		public byte mode;

		public MotorRegulation regulation;

		public byte turnRatio;

		public MotorRunState runState;

		public int tachoLimit; //Current limit on a movement in progress, if any

//		public int tachoCount; //Internal count. Number of counts since last reset of motor
//
//		public int blockTachoCount; //Current position relative to last programmed movement
//
//		public int rotationCount; //Current position relative to last reset of the rotation sensor for this
	}


	public static class MotorMode {
		public static final byte ON = 0x01;
		public static final byte BREAK = 0x02;
		public static final byte REGULATED = 0x04;
	}

	public enum MotorRegulation {
		IDLE(0x00), SPEED(0x01), SYNC(0x02);

		private int motorRegulationValue;
		private MotorRegulation(int motorRegulationValue) {
			this.motorRegulationValue = motorRegulationValue;
		}

		private byte getByte() {
			return (byte)motorRegulationValue;
		}
	}

	public enum MotorRunState {
		IDLE(0x00), RAMP_UP(0x10), RUNNING(0x20), RAMP_DOWN(0x40);

		private int motorRunStateValue;
		private MotorRunState(int motorRunStateValue) {
			this.motorRunStateValue = motorRunStateValue;
		}

		private byte getByte() {
			return (byte)motorRunStateValue;
		}
	}
}
