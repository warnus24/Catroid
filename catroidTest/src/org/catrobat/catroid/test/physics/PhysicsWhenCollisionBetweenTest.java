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

package org.catrobat.catroid.test.physics;

import android.util.Log;

import com.badlogic.gdx.math.Vector2;

import org.catrobat.catroid.content.CollisionScript;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.conditional.PlaceAtBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.physics.PhysicsObject;
import org.catrobat.catroid.physics.content.bricks.SetBounceBrick;
import org.catrobat.catroid.physics.content.bricks.SetPhysicsObjectTypeBrick;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.catrobat.catroid.physics.PhysicsObject.Type.DYNAMIC;
import static org.catrobat.catroid.physics.PhysicsObject.Type.FIXED;

public class PhysicsWhenCollisionBetweenTest extends PhysicsCollisionBaseTest {

	private static final String TAG = PhysicsWhenCollisionBetweenTest.class.getSimpleName();
	private Vector2 endPosition = new Vector2(0.0f, 50.0f);
	private float deltaTime = 0.1f;

	public PhysicsWhenCollisionBetweenTest() {

		spritePosition = new Vector2(0.0f, -50.0f);
		sprite2Position = new Vector2(0.0f, -200.0f);

		physicsObject1Type = PhysicsObject.Type.DYNAMIC;
		physicsObject2Type = PhysicsObject.Type.FIXED;
	}

	public void testWhenCollisionBetweenEvent () {
		while (!collisionDetected()) {
			physicsWorld.step(deltaTime);
		}
		physicsWorld.step(deltaTime*3);

		physicsObject1.setType(FIXED);
		assertEquals("Unexpected Type of Physics Object 1", FIXED, physicsObject1.getType());
		physicsObject1.setPosition(endPosition);
		assertEquals("Unexpected End-position of Physics Object 1", endPosition, physicsObject1.getPosition());

		Log.d(TAG, "pos:"  + physicsObject1.getPosition());
	}

	public void testWhenCollisionBetweenBrick() {

		ArrayList<Brick> brickList;
		int numberOfScripts = 1;
		int numberOfBricks = 3;

		CollisionScript collisionScript = new CollisionScript(sprite, sprite2.getName());

		SetPhysicsObjectTypeBrick setPhysicsObjectTypeBrickFixed = new SetPhysicsObjectTypeBrick(sprite, FIXED);
		PlaceAtBrick placeAtBrick = new PlaceAtBrick(sprite, new Formula(0.0f), new Formula(100.0f));
		SetBounceBrick setBounceBrick = new SetBounceBrick(sprite, 0.0f);

		collisionScript.addBrick(setPhysicsObjectTypeBrickFixed);
		collisionScript.addBrick(placeAtBrick);
		collisionScript.addBrick(setBounceBrick);

		sprite.addScript(collisionScript);
		brickList = collisionScript.getBrickList();

		assertEquals("Unexpected number of scripts", numberOfScripts, sprite.getNumberOfScripts());
		assertEquals("Wrong size of brick list", numberOfBricks, brickList.size());
		assertEquals("setPhysicsObjectTypeFixedBrick is not at index 0", 0, brickList.indexOf(setPhysicsObjectTypeBrickFixed));
		assertEquals("placeAtBrick is not at index 1", 1, brickList.indexOf(placeAtBrick));
		assertEquals("setBounceBrick is not at index 2", 2, brickList.indexOf(setBounceBrick));
	}

	public void testWhenCollisionBetweenBrickEvent() {

		String key1 = sprite.getName().concat(sprite2.getName());
		//String key2 = sprite2.getName().concat(sprite.getName());

		physicsObject1.setType(DYNAMIC);
		physicsObject1.setBounceFactor(0.8f);
		physicsObject1.setPosition(0.0f, -50.0f);

		physicsObject2.setType(FIXED);
		physicsObject2.setBounceFactor(0.0f);
		physicsObject2.setPosition(0.0f, -200.0f);

		StartScript startScript = new StartScript(sprite);
//		CollisionScript collisionScript = new CollisionScript(sprite, key1);
		SetPhysicsObjectTypeBrick setPhysicsObjectTypeFixedBrick = new SetPhysicsObjectTypeBrick(sprite, FIXED);
		PlaceAtBrick placeAtBrick = new PlaceAtBrick(sprite, new Formula(0.0f), new Formula(50.0f));

		//NOT WHAT IS INTENDED TO TEST
		startScript.addBrick(setPhysicsObjectTypeFixedBrick); //NOT WHAT IS INTENDED TO TEST
		startScript.addBrick(placeAtBrick);
		sprite.addScript(startScript);

//		NEED HELP WITH THAT ONE
//		collisionScript.addBrick(setPhysicsObjectTypeFixedBrick);
//		collisionScript.addBrick(placeAtBrick);
//		sprite.addScript(collisionScript);

		sprite.createStartScriptActionSequenceAndPutToMap(new HashMap<String, List<String>>());

		while (!sprite.look.getAllActionsAreFinished()) {
			Log.d(TAG, "Sprite: " + sprite.getName() + " Sprite-Y-Position: " + sprite.look.getY());
			Log.d(TAG, "Sprite: " + sprite2.getName() + " Sprite-Y-Position: "  + sprite2.look.getY());
			sprite.look.act(0.1f);
			physicsWorld.step(0.1f);
		}

		assertEquals("Unexpected Type of Physics Object 1", FIXED, physicsObject1.getType());
		assertEquals("Unexpected End-position of Physics Object 1", endPosition, physicsObject1.getPosition());
	}



}
