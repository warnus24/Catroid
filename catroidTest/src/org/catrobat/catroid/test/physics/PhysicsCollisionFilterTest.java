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


import com.badlogic.gdx.math.Vector2;

import org.catrobat.catroid.physics.PhysicsObject;

public class PhysicsCollisionFilterTest extends PhysicsCollisionBaseTest {

	public PhysicsCollisionFilterTest() {
		spritePosition = new Vector2(-200.0f, 0.0f);
		sprite2Position = new Vector2(200.0f, 0.0f);
		physicsObject1Type = PhysicsObject.Type.DYNAMIC;
		physicsObject2Type = PhysicsObject.Type.DYNAMIC;
	}

	public void testNeutralBehavior() {
		boolean collided = false;

		physicsWorld.setGravity(0.0f, 0.0f);
		physicsObject1.setVelocity(100.0f, 0.0f);
		physicsObject2.setVelocity(-100.0f, 0.0f);

		while (!collisionDetected()){
			physicsWorld.step(0.1f);
		}
		physicsWorld.step(0.1f);
		collided = true;
		assertEquals("Should have been collided", true, collided);
	}

	public void testNeutralWithFriendlyCollisionBehavior() {
		boolean collided = false;

		physicsWorld.setGravity(0.0f, 0.0f);
		physicsObject1.setVelocity(100.0f, 0.0f);
		physicsObject2.setVelocity(-100.0f, 0.0f);
		physicsObject1.setBehavior(PhysicsObject.Behavior.FRIENDLY);

		while (!collisionDetected()){
			physicsWorld.step(0.1f);
		}
		physicsWorld.step(0.1f);
		collided = true;
		assertEquals("Should have been collided", true, collided);
	}

	public void testNeutralWithHostileCollisionBehavior() {
		boolean collided = false;

		physicsWorld.setGravity(0.0f, 0.0f);
		physicsObject1.setVelocity(100.0f, 0.0f);
		physicsObject2.setVelocity(-100.0f, 0.0f);
		physicsObject1.setBehavior(PhysicsObject.Behavior.HOSTILE);

		while (!collisionDetected()){
			physicsWorld.step(0.1f);
		}
		physicsWorld.step(0.1f);
		collided = true;
		assertEquals("Should have been collided", true, collided);
	}

	public void testFriendlyWithHostileCollisionBehavior() {
		boolean collided = false;

		physicsWorld.setGravity(0.0f, 0.0f);
		physicsObject1.setVelocity(100.0f, 0.0f);
		physicsObject2.setVelocity(-100.0f, 0.0f);
		physicsObject1.setBehavior(PhysicsObject.Behavior.HOSTILE);
		physicsObject2.setBehavior(PhysicsObject.Behavior.FRIENDLY);

		while (!collisionDetected()){
			physicsWorld.step(0.1f);
		}
		physicsWorld.step(0.1f);
		collided = true;
		assertEquals("Should have been collided", true, collided);
	}

	public void testFriendlyWithFriendlyCollisionBehavior() {
		boolean collided = false;
		short timeout = 0;

		physicsWorld.setGravity(0.0f, 0.0f);
		physicsObject1.setVelocity(100.0f, 0.0f);
		physicsObject2.setVelocity(-100.0f, 0.0f);
		physicsObject1.setBehavior(PhysicsObject.Behavior.FRIENDLY);
		physicsObject2.setBehavior(PhysicsObject.Behavior.FRIENDLY);


		while (timeout < 1000) {
			if (collisionDetected()) {
				collided = true;
			}
			physicsWorld.step(0.1f);
			timeout++;
			assertEquals("Should not have been collided", false, collided);
		}
	}

	public void testHostileWithHostileCollisionBehavior() {
		boolean collided = false;
		short timeout = 0;

		physicsWorld.setGravity(0.0f, 0.0f);
		physicsObject1.setVelocity(100.0f, 0.0f);
		physicsObject2.setVelocity(-100.0f, 0.0f);
		physicsObject1.setBehavior(PhysicsObject.Behavior.HOSTILE);
		physicsObject2.setBehavior(PhysicsObject.Behavior.HOSTILE);


		while (timeout < 1000) {
			if (collisionDetected()) {
				collided = true;
			}
			physicsWorld.step(0.1f);
			timeout++;
			assertEquals("Should not have been collided", false, collided);
		}
	}

}
