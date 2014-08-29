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

package org.catrobat.catroid.common.standardprojectcreators;

import android.content.Context;

import com.badlogic.gdx.math.Vector2;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.common.MessageContainer;
import org.catrobat.catroid.content.BroadcastScript;
import org.catrobat.catroid.content.CollisionScript;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.WhenScript;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.BroadcastBrick;
import org.catrobat.catroid.content.bricks.ForeverBrick;
import org.catrobat.catroid.content.bricks.WaitBrick;
import org.catrobat.catroid.content.bricks.conditional.IfOnEdgeBounceBrick;
import org.catrobat.catroid.content.bricks.conditional.NextLookBrick;
import org.catrobat.catroid.content.bricks.conditional.PlaceAtBrick;
import org.catrobat.catroid.content.bricks.conditional.PointInDirectionBrick;
import org.catrobat.catroid.content.bricks.conditional.SetLookBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.physics.PhysicsObject;
import org.catrobat.catroid.physics.content.bricks.SetBounceBrick;
import org.catrobat.catroid.physics.content.bricks.SetFrictionBrick;
import org.catrobat.catroid.physics.content.bricks.SetGravityBrick;
import org.catrobat.catroid.physics.content.bricks.SetPhysicsObjectTypeBrick;
import org.catrobat.catroid.physics.content.bricks.TurnLeftSpeedBrick;
import org.catrobat.catroid.utils.ImageEditing;
import org.catrobat.catroid.utils.UtilFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;

public class StandardProjectCreatorPhysics extends StandardProjectCreator {

	private static final String FILENAME_SEPARATOR = "_";

	public StandardProjectCreatorPhysics() {
		standardProjectNameID = R.string.default_project_name_physics;
	}

	@Override
	public Project createStandardProject(String projectName, Context context) throws IOException,
			IllegalArgumentException {
		String backgroundName = context.getString(R.string.default_project_backgroundname);

		Project defaultPhysicsProject = new Project(context, projectName);
		defaultPhysicsProject.setDeviceData(context); // density anywhere here
		StorageHandler.getInstance().saveProject(defaultPhysicsProject);
		ProjectManager.getInstance().setProject(defaultPhysicsProject);

		backgroundImageScaleFactor = ImageEditing.calculateScaleFactorToScreenSize(
				R.drawable.physics_background_480_800, context);

		File backgroundFile = UtilFile.copyImageFromResourceIntoProject(projectName, backgroundName
						+ Constants.IMAGE_STANDARD_EXTENTION, R.drawable.default_project_background, context, true,
				backgroundImageScaleFactor
		);

		LookData backgroundLookData = new LookData();
		backgroundLookData.setLookName(backgroundName);
		backgroundLookData.setLookFilename(backgroundFile.getName());

		// Background sprite
		Sprite backgroundSprite = defaultPhysicsProject.getSpriteList().get(0);
		backgroundSprite.getLookDataList().add(backgroundLookData);
		Script backgroundStartScript = new StartScript();

		SetLookBrick setLookBrick = new SetLookBrick();
		setLookBrick.setLook(backgroundLookData);
		backgroundStartScript.addBrick(setLookBrick);

		backgroundSprite.addScript(backgroundStartScript);

		Sprite ball = new Sprite("Ball");
		Sprite leftButton = new Sprite("Left button");
		Sprite rightButton = new Sprite("Right button");
		Sprite leftArm = new Sprite("Left arm");
		Sprite rightArm = new Sprite("Right arm");

		Sprite[] upperBouncers = {new Sprite("Middle cat bouncer"), new Sprite("Right cat bouncer")};

		Sprite[] lowerBouncers = {new Sprite("Left circle bouncer"), new Sprite("Middle circle bouncer"),
				new Sprite("Right circle bouncer")};

		Sprite middleBouncer = new Sprite("Cat head bouncer");
		Sprite leftHardBouncer = new Sprite("Left hard bouncer");
		Sprite leftHardBouncerBouncer = new Sprite("Left hard bouncer bouncer");
		Sprite rightHardBouncer = new Sprite("Right hard bouncer");
		Sprite rightHardBouncerBouncer = new Sprite("Right hard bouncer bouncer");

		Sprite leftVerticalWall = new Sprite("Left vertical wall");
		Sprite leftBottomWall = new Sprite("Left bottom wall");
		Sprite rightVerticalWall = new Sprite("Right vertical wall");
		Sprite rightBottomWall = new Sprite("Right bottom wall");

		final String leftButtonPressed = "Left button pressed";
		final String rightButtonPressed = "Right button pressed";

		final float armMovingSpeed = 720.0f;
		float doodlydoo = 50.0f;

		// Background
		createElement(context, projectName, backgroundSprite, "physics_background_480_800", R.drawable.physics_background_480_800, new Vector2(), Float.NaN);

		// Ball
		StartScript startScript = new StartScript();
		startScript.addBrick(new SetGravityBrick(new Vector2(0.0f, -10.0f)));
		ball.addScript(startScript);

		Script ballStartScript = createElement(context, projectName, ball, "physics_pinball", R.drawable.physics_pinball, new Vector2(0.0f, 250.0f),
				Float.NaN);
		setPhysicsProperties(ball, ballStartScript, PhysicsObject.Type.DYNAMIC, 60.0f, 40.0f);

		Brick foreverBrick = new ForeverBrick();
		Brick ifOnEdgeBounceBrick = new IfOnEdgeBounceBrick();
		ballStartScript.addBrick(foreverBrick);
		ballStartScript.addBrick(ifOnEdgeBounceBrick);

		// Buttons
		createElement(context, projectName, leftButton, "physics_button", R.drawable.physics_button, new Vector2(-175.0f, -330.0f), Float.NaN);
		createButtonPressed(context, projectName, leftButton, leftButtonPressed);
		createElement(context, projectName, rightButton, "physics_button", R.drawable.physics_button, new Vector2(175.0f, -330.0f), Float.NaN);
		createButtonPressed(context, projectName, rightButton, rightButtonPressed);

		// Arms
		Script leftArmStartScript = createElement(context, projectName, leftArm, "physics_left_arm", R.drawable.physics_left_arm,
				new Vector2(-80.0f, -315.0f), Float.NaN);
		setPhysicsProperties(leftArm, leftArmStartScript, PhysicsObject.Type.FIXED, 50.0f, -1.0f);
		createMovingArm(leftArm, leftButtonPressed, armMovingSpeed);
		Script rightArmStartScript = createElement(context, projectName, rightArm, "physics_right_arm", R.drawable.physics_right_arm, new Vector2(80.0f,
				-315.0f), Float.NaN);
		setPhysicsProperties(rightArm, rightArmStartScript, PhysicsObject.Type.FIXED, 50.0f, -1.0f);
		createMovingArm(rightArm, rightButtonPressed, -armMovingSpeed);

		// Lower walls
		Script leftVerticalWallStartScript = createElement(context, projectName, leftVerticalWall, "physics_vertical_wall", R.drawable.physics_vertical_wall,
				new Vector2(-232.0f, -160.0f), 82f);
		setPhysicsProperties(leftVerticalWall, leftVerticalWallStartScript, PhysicsObject.Type.FIXED, 5.0f, -1.0f);
		Script rightVerticalWallStartScript = createElement(context, projectName, rightVerticalWall, "physics_vertical_wall",
				R.drawable.physics_vertical_wall, new Vector2(232.0f, -160.0f), -82f);
		setPhysicsProperties(rightVerticalWall, rightVerticalWallStartScript, PhysicsObject.Type.FIXED, 5.0f, -1.0f);
		//
		Script leftBottomWallStartScript = createElement(context, projectName, leftBottomWall, "physics_wall_bottom", R.drawable.physics_wall_bottom,
				new Vector2(-155.0f, -255.0f), 31.2f);
		setPhysicsProperties(leftBottomWall, leftBottomWallStartScript, PhysicsObject.Type.FIXED, 5.0f, -1.0f);
		Script rightBottomWallStartScript = createElement(context, projectName, rightBottomWall, "physics_wall_bottom", R.drawable.physics_wall_bottom,
				new Vector2(155.0f, -255.0f), -31.2f);
		setPhysicsProperties(rightBottomWall, rightBottomWallStartScript, PhysicsObject.Type.FIXED, 5.0f, -1.0f);

		// Hard Bouncer
		Script leftHardBouncerStartScript = createElement(context, projectName, leftHardBouncer, "physics_left_hard_bouncer",
				R.drawable.physics_left_hard_bouncer, new Vector2(-140.0f, -165.0f), Float.NaN);
		setPhysicsProperties(leftHardBouncer, leftHardBouncerStartScript, PhysicsObject.Type.FIXED, 10.0f, -1.0f);
		Script leftHardBouncerBouncerStartScript = createElement(context, projectName, leftHardBouncerBouncer, "physics_left_light_bouncer",
				R.drawable.physics_left_light_bouncer, new Vector2(-129.0f, -163.0f), Float.NaN);
		setPhysicsProperties(leftHardBouncerBouncer, leftHardBouncerBouncerStartScript, PhysicsObject.Type.FIXED,
				124.0f, -1.0f);

		Script rightHardBouncerStartScript = createElement(context, projectName, rightHardBouncer, "physics_right_hard_bouncer",
				R.drawable.physics_right_hard_bouncer, new Vector2(140.0f, -165.0f), Float.NaN);
		setPhysicsProperties(rightHardBouncer, rightHardBouncerStartScript, PhysicsObject.Type.FIXED, 10.0f, -1.0f);
		Script rightHardBouncerBouncerStartScript = createElement(context, projectName, rightHardBouncerBouncer, "physics_right_light_bouncer",
				R.drawable.physics_right_light_bouncer, new Vector2(129.0f, -163.0f), Float.NaN);
		setPhysicsProperties(rightHardBouncerBouncer, rightHardBouncerBouncerStartScript, PhysicsObject.Type.FIXED,
				124.0f, -1.0f);

		// Lower circle bouncers
		Vector2[] lowerBouncersPositions = {new Vector2(-100.0f, -80.0f + doodlydoo),
				new Vector2(0.0f, -140.0f + doodlydoo), new Vector2(100.0f, -80.0f + doodlydoo)};
		for (int index = 0; index < lowerBouncers.length; index++) {
			Script lowerBouncerStartScript = createElement(context, projectName, lowerBouncers[index], "physics_circle_bouncer",
					R.drawable.physics_circle_bouncer, lowerBouncersPositions[index], new Random().nextInt(360));
			setPhysicsProperties(lowerBouncers[index], lowerBouncerStartScript, PhysicsObject.Type.FIXED, 116.0f, -1.0f);


			CollisionScript collisionScript = new CollisionScript(lowerBouncers[index].getName()+"<->"+ball.getName());
			collisionScript.getScriptBrick();
			collisionScript.addBrick(new NextLookBrick());
			WaitBrick waitBrick = new WaitBrick();
			waitBrick.setTimeToWait(new Formula(0.5));
			collisionScript.addBrick(waitBrick);
			collisionScript.addBrick(new NextLookBrick());

			File file = UtilFile.copyImageFromResourceIntoProject(projectName, "physics_circle_bouncer_hit", R.drawable.physics_circle_bouncer_hit, context, true, backgroundImageScaleFactor);
			LookData lookData = new LookData();
			lookData.setLookName("physics_circle_bouncer_hit");
			lookData.setLookFilename(file.getName());
			List<LookData> looks = lowerBouncers[index].getLookDataList();
			looks.add(lookData);

			SetLookBrick setLookBrick1 = new SetLookBrick();
			setLookBrick1.setLook(looks.get(0));

			lowerBouncers[index].addScript(collisionScript);
		}

		// Middle bouncer
		Script middleBouncerStartScript = createElement(context, projectName, middleBouncer, "physics_lego", R.drawable.physics_lego, new Vector2(0.0f,
				75.0f + doodlydoo), Float.NaN);
		setPhysicsProperties(middleBouncer, middleBouncerStartScript, PhysicsObject.Type.FIXED, 40.0f, 80.0f);
		middleBouncerStartScript.addBrick(new TurnLeftSpeedBrick(145));

		// Upper bouncers
		Vector2[] upperBouncersPositions = {new Vector2(0.0f, 240.f + doodlydoo),
				new Vector2(150.0f, 200.0f + doodlydoo)};
		for (int index = 0; index < upperBouncers.length; index++) {
			Script upperBouncersStartScript = createElement(context, projectName, upperBouncers[index], "cat_bouncer",
					R.drawable.physics_cat_bouncer, upperBouncersPositions[index], Float.NaN);
			setPhysicsProperties(upperBouncers[index], upperBouncersStartScript, PhysicsObject.Type.FIXED, 106.0f, -1.0f);
		}

		defaultPhysicsProject.addSprite(leftButton);
		defaultPhysicsProject.addSprite(rightButton);
		defaultPhysicsProject.addSprite(ball);
		defaultPhysicsProject.addSprite(leftArm);
		defaultPhysicsProject.addSprite(rightArm);
		defaultPhysicsProject.addSprite(middleBouncer);
		defaultPhysicsProject.addSprite(leftHardBouncerBouncer);
		defaultPhysicsProject.addSprite(leftHardBouncer);
		defaultPhysicsProject.addSprite(rightHardBouncerBouncer);
		defaultPhysicsProject.addSprite(rightHardBouncer);
		defaultPhysicsProject.addSprite(leftVerticalWall);
		defaultPhysicsProject.addSprite(leftBottomWall);
		defaultPhysicsProject.addSprite(rightVerticalWall);
		defaultPhysicsProject.addSprite(rightBottomWall);

		for (Sprite sprite : upperBouncers) {
			defaultPhysicsProject.addSprite(sprite);
		}

		for (Sprite sprite : lowerBouncers) {
			defaultPhysicsProject.addSprite(sprite);
		}

		StorageHandler.getInstance().saveProject(defaultPhysicsProject);

		return defaultPhysicsProject;
	}

	private Script createElement(Context context, String projectName, Sprite sprite, String fileName, int fileId, Vector2 position, float angle)
			throws IOException {
		File file = UtilFile.copyImageFromResourceIntoProject(projectName, fileName, fileId, context, true, backgroundImageScaleFactor);

		LookData lookData = new LookData();
		lookData.setLookName(fileName);
		lookData.setLookFilename(file.getName());

		List<LookData> looks = sprite.getLookDataList();
		looks.add(lookData);

		SetLookBrick lookBrick = new SetLookBrick();
		lookBrick.setLook(lookData);

		Script startScript = new StartScript();
		startScript.addBrick(new PlaceAtBrick(new Formula(position.x * backgroundImageScaleFactor), new Formula(position.y * backgroundImageScaleFactor)));
		startScript.addBrick(lookBrick);

		if (!Float.isNaN(angle)) {
			PointInDirectionBrick pointInDirectionBrick = new PointInDirectionBrick(new Formula(angle));
			startScript.addBrick(pointInDirectionBrick);
		}

		sprite.addScript(startScript);
		return startScript;
	}

	private Script setPhysicsProperties(Sprite sprite, Script startScript, PhysicsObject.Type type,
			float bounce, float friction) {
		if (startScript == null) {
			startScript = new StartScript();
		}

		startScript.addBrick(new SetPhysicsObjectTypeBrick(type));

		if (bounce >= 0.0f) {
			startScript.addBrick(new SetBounceBrick(bounce));
		}

		if (friction >= 0.0f) {
			startScript.addBrick(new SetFrictionBrick(friction));
		}

		sprite.addScript(startScript);
		return startScript;
	}

	private void createButtonPressed(Context context, String projectName, Sprite sprite, String broadcastMessage) throws IOException {
		MessageContainer.addMessage(broadcastMessage);

		WhenScript whenPressedScript = new WhenScript();
		whenPressedScript.setAction(0);

		BroadcastBrick leftButtonBroadcastBrick = new BroadcastBrick(broadcastMessage);

		String filename = "button_pressed";
		File file = UtilFile.copyImageFromResourceIntoProject(projectName, filename, R.drawable.physics_button_pressed, context, true, backgroundImageScaleFactor);
		LookData lookData = new LookData();
		lookData.setLookName(filename);
		lookData.setLookFilename(file.getName());

		List<LookData> looks = sprite.getLookDataList();
		looks.add(lookData);

		SetLookBrick lookBrick = new SetLookBrick();
		lookBrick.setLook(lookData);

		WaitBrick waitBrick = new WaitBrick(500);

		SetLookBrick lookBack = new SetLookBrick();
		lookBack.setLook(looks.get(0));

		whenPressedScript.addBrick(leftButtonBroadcastBrick);
		whenPressedScript.addBrick(lookBrick);
		whenPressedScript.addBrick(waitBrick);
		whenPressedScript.addBrick(lookBack);
		sprite.addScript(whenPressedScript);
	}

	private void createMovingArm(Sprite sprite, String broadcastMessage, float degreeSpeed) {
		BroadcastScript broadcastScript = new BroadcastScript(broadcastMessage);

		int waitInMillis = 110;

		broadcastScript.addBrick(new TurnLeftSpeedBrick(degreeSpeed));
		broadcastScript.addBrick(new WaitBrick(waitInMillis));

		broadcastScript.addBrick(new TurnLeftSpeedBrick(0));
		broadcastScript.addBrick(new PointInDirectionBrick(90.0f));

		sprite.addScript(broadcastScript);
	}
}
