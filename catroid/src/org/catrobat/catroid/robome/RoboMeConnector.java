package org.catrobat.catroid.robome;

import android.content.Context;
import android.util.Log;

import com.wowwee.robome.RoboMe;
import com.wowwee.robome.RoboMeCommands;

public class RoboMeConnector implements RoboMe.RoboMeListener {

	private static final String TAG = RoboMeConnector.class.getSimpleName();

	private static RoboMe roboMe;

	private boolean get_mood_enabled = false;

	public RoboMeConnector(Context stageActivityContext) {
		roboMe = new RoboMe(stageActivityContext, this);
	}

	public void initialize() {
		resume();
	}

	public void resume() {
		roboMe.setVolume(RoboMeConstants.ROBOME_DEFAULT_VOLUME);

		if ((roboMe.isRoboMeConnected()) && (!roboMe.isListening())) {
			roboMe.startListening();
		}
	}

	public static RoboMe getRoboMe() {
		return roboMe;
	}

	@Override
	public void commandReceived(RoboMeCommands.IncomingRobotCommand incomingRobotCommand) {
		if(get_mood_enabled && incomingRobotCommand.isMoodValue()){

		}
		Log.d(TAG, "Command received" + incomingRobotCommand);
	}

	@Override
	public void roboMeConnected() {
		Log.d(TAG, "RoboMe connected");
	}

	@Override
	public void roboMeDisconnected() {
		Log.d(TAG, "RoboMe disconnected");
	}

	@Override
	public void headsetPluggedIn() {
		Log.d(TAG, "Headset plugged in");

		if (!roboMe.isListening()) {
			roboMe.startListening();
		}
	}

	@Override
	public void headsetUnplugged() {
		Log.d(TAG, "Headset unplugged");

		if (roboMe.isListening()) {
			roboMe.stopListening();
		}
	}

	@Override
	public void volumeChanged(float v) {
		Log.d(TAG, "Volume changed to " + v);
	}

	public static void moveForward(int speed, int cycles){
		switch (speed) {
			case RoboMeConstants.ROBOME_MOVE_SPEED_1:
				roboMe.sendCommand(RoboMeCommands.RobotCommand.kRobot_MoveForwardSpeed1, cycles);
				break;
			case RoboMeConstants.ROBOME_MOVE_SPEED_2:
				roboMe.sendCommand(RoboMeCommands.RobotCommand.kRobot_MoveForwardSpeed2, cycles);
				break;
			case RoboMeConstants.ROBOME_MOVE_SPEED_3:
				roboMe.sendCommand(RoboMeCommands.RobotCommand.kRobot_MoveForwardSpeed3, cycles);
				break;
			case RoboMeConstants.ROBOME_MOVE_SPEED_4:
				roboMe.sendCommand(RoboMeCommands.RobotCommand.kRobot_MoveForwardSpeed4, cycles);
				break;
			case RoboMeConstants.ROBOME_MOVE_SPEED_5:
				roboMe.sendCommand(RoboMeCommands.RobotCommand.kRobot_MoveForwardSpeed5, cycles);
				break;
		}
	}

	public void moveBackward(int speed, int cycles){
		switch (speed) {
			case RoboMeConstants.ROBOME_MOVE_SPEED_1:
				roboMe.sendCommand(RoboMeCommands.RobotCommand.kRobot_MoveBackwardSpeed1, cycles);
				break;
			case RoboMeConstants.ROBOME_MOVE_SPEED_2:
				roboMe.sendCommand(RoboMeCommands.RobotCommand.kRobot_MoveBackwardSpeed2, cycles);
				break;
			case RoboMeConstants.ROBOME_MOVE_SPEED_3:
				roboMe.sendCommand(RoboMeCommands.RobotCommand.kRobot_MoveBackwardSpeed3, cycles);
				break;
			case RoboMeConstants.ROBOME_MOVE_SPEED_4:
				roboMe.sendCommand(RoboMeCommands.RobotCommand.kRobot_MoveBackwardSpeed4, cycles);
				break;
			case RoboMeConstants.ROBOME_MOVE_SPEED_5:
				roboMe.sendCommand(RoboMeCommands.RobotCommand.kRobot_MoveBackwardSpeed5, cycles);
				break;
		}
	}

	public void turnLeftSpeed(int speed, int cycles) {
		switch (speed) {
			case RoboMeConstants.ROBOME_MOVE_SPEED_1:
				roboMe.sendCommand(RoboMeCommands.RobotCommand.kRobot_TurnLeftSpeed1, cycles);
				break;
			case RoboMeConstants.ROBOME_MOVE_SPEED_2:
				roboMe.sendCommand(RoboMeCommands.RobotCommand.kRobot_TurnLeftSpeed2, cycles);
				break;
			case RoboMeConstants.ROBOME_MOVE_SPEED_3:
				roboMe.sendCommand(RoboMeCommands.RobotCommand.kRobot_TurnLeftSpeed3, cycles);
				break;
			case RoboMeConstants.ROBOME_MOVE_SPEED_4:
				roboMe.sendCommand(RoboMeCommands.RobotCommand.kRobot_TurnLeftSpeed4, cycles);
				break;
			case RoboMeConstants.ROBOME_MOVE_SPEED_5:
				roboMe.sendCommand(RoboMeCommands.RobotCommand.kRobot_TurnLeftSpeed5, cycles);
				break;
		}
	}

	public void turnLeftAngle(int degrees, int cycles) {
		switch (degrees) {
			case RoboMeConstants.ROBOME_DEGREES_90:
				roboMe.sendCommand(RoboMeCommands.RobotCommand.kRobot_TurnLeft90Degrees, cycles);
				break;
			case RoboMeConstants.ROBOME_DEGREES_180:
				roboMe.sendCommand(RoboMeCommands.RobotCommand.kRobot_TurnLeft180Degrees, cycles);
				break;
			case RoboMeConstants.ROBOME_DEGREES_360:
				roboMe.sendCommand(RoboMeCommands.RobotCommand.kRobot_TurnLeft360Degrees, cycles);
				break;
		}
	}

	public void turnRightSpeed(int speed, int cycles) {

		switch (speed) {
			case RoboMeConstants.ROBOME_MOVE_SPEED_1:
				roboMe.sendCommand(RoboMeCommands.RobotCommand.kRobot_TurnRightSpeed1, cycles);
				break;
			case RoboMeConstants.ROBOME_MOVE_SPEED_2:
				roboMe.sendCommand(RoboMeCommands.RobotCommand.kRobot_TurnRightSpeed2, cycles);
				break;
			case RoboMeConstants.ROBOME_MOVE_SPEED_3:
				roboMe.sendCommand(RoboMeCommands.RobotCommand.kRobot_TurnRightSpeed3, cycles);
				break;
			case RoboMeConstants.ROBOME_MOVE_SPEED_4:
				roboMe.sendCommand(RoboMeCommands.RobotCommand.kRobot_TurnRightSpeed4, cycles);
				break;
			case RoboMeConstants.ROBOME_MOVE_SPEED_5:
				roboMe.sendCommand(RoboMeCommands.RobotCommand.kRobot_TurnRightSpeed5, cycles);
				break;
		}
	}

	public void turnRightAngle(int degrees, int cycles) {
		switch (degrees) {
			case RoboMeConstants.ROBOME_DEGREES_90:
				roboMe.sendCommand(RoboMeCommands.RobotCommand.kRobot_TurnRight90Degrees, cycles);
				break;
			case RoboMeConstants.ROBOME_DEGREES_180:
				roboMe.sendCommand(RoboMeCommands.RobotCommand.kRobot_TurnRight180Degrees, cycles);
				break;
			case RoboMeConstants.ROBOME_DEGREES_360:
				roboMe.sendCommand(RoboMeCommands.RobotCommand.kRobot_TurnRight360Degrees, cycles);
				break;
		}
	}

	public void moveHeadUp(int amount){
		switch (amount) {
			case RoboMeConstants.ROBOME_HEAD_SPEED_SLOW:
				roboMe.sendCommand(RoboMeCommands.RobotCommand.kRobot_HeadTiltUp1);
				break;
			case RoboMeConstants.ROBOME_HEAD_SPEED_FAST:
				roboMe.sendCommand(RoboMeCommands.RobotCommand.kRobot_HeadTiltUp2);
				break;
			case RoboMeConstants.ROBOME_HEAD_MAX_MIN_POS:
				roboMe.sendCommand(RoboMeCommands.RobotCommand.kRobot_HeadTiltAllUp);
				break;
		}
	}

	//----------------------------------------------------------------------------------------------
	public void moveHeadDown(int amount){
		switch (amount) {
			case RoboMeConstants.ROBOME_HEAD_SPEED_SLOW:
				roboMe.sendCommand(RoboMeCommands.RobotCommand.kRobot_HeadTiltDown1);
				break;
			case RoboMeConstants.ROBOME_HEAD_SPEED_FAST:
				roboMe.sendCommand(RoboMeCommands.RobotCommand.kRobot_HeadTiltDown2);
				break;
			case RoboMeConstants.ROBOME_HEAD_MAX_MIN_POS:
				roboMe.sendCommand(RoboMeCommands.RobotCommand.kRobot_HeadTiltAllDown);
				break;
		}
	}

	public void resetHead(){
		roboMe.sendCommand(RoboMeCommands.RobotCommand.kRobot_HeadReset);
	}

	public void resetMood() {
		roboMe.sendCommand(RoboMeCommands.RobotCommand.kRobot_ResetMood);
	}

	public void increaseMood() {
		roboMe.sendCommand(RoboMeCommands.RobotCommand.kRobot_IncreaseMood);
	}

	public void decreaseMood() {
		roboMe.sendCommand(RoboMeCommands.RobotCommand.kRobot_DecreaseMood);
	}

	public void getMood(){
		roboMe.sendCommand(RoboMeCommands.RobotCommand.kRobot_GetMood);
	}

	public void setHeart(RoboMeConstants.RoboMeColors color) {
		switch (color){
			case BLUE:
				roboMe.sendCommand(RoboMeCommands.RobotCommand.kRobot_RGBHeartBlue);
				break;
			case RED:
				roboMe.sendCommand(RoboMeCommands.RobotCommand.kRobot_RGBHeartRed);
			break;
			case GREEN:
				roboMe.sendCommand(RoboMeCommands.RobotCommand.kRobot_RGBHeartGreen);
				break;
			case ORANGE:
				roboMe.sendCommand(RoboMeCommands.RobotCommand.kRobot_RGBHeartOrange);
				break;
			case YELLOW:
				roboMe.sendCommand(RoboMeCommands.RobotCommand.kRobot_RGBHeartYellow);
				break;
			case WHITE:
				roboMe.sendCommand(RoboMeCommands.RobotCommand.kRobot_RGBHeartWhite);
				break;
			case CYAN:
				roboMe.sendCommand(RoboMeCommands.RobotCommand.kRobot_RGBHeartCyan);
				break;
			case OFF:
				roboMe.sendCommand(RoboMeCommands.RobotCommand.kRobot_RGBHeartOff);
				break;
		}
	}
}
