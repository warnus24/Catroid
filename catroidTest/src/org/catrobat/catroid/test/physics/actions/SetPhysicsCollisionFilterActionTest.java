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
package org.catrobat.catroid.test.physics.actions;

import org.catrobat.catroid.physics.PhysicsObject;
import org.catrobat.catroid.physics.content.actions.SetPhysicsCollisionFilterAction;
import org.catrobat.catroid.physics.content.actions.SetPhysicsObjectTypeAction;
import org.catrobat.catroid.test.physics.PhysicsBaseTest;

public class SetPhysicsCollisionFilterActionTest extends PhysicsBaseTest {

	public void testPhysicsCollisionFilterNeutral() {
		PhysicsObject.Behavior behavior = PhysicsObject.Behavior.NEUTRAL;
		SetPhysicsCollisionFilterAction setPhysicsCollisionFilterAction = new SetPhysicsCollisionFilterAction();
		PhysicsObject physicsObject = physicsWorld.getPhysicsObject(sprite);
		setPhysicsCollisionFilterAction.setPhysicsObject(physicsObject);
		setPhysicsCollisionFilterAction.setBehavior(behavior);

		assertEquals("Unexpected physics collision filter", PhysicsObject.Behavior.NEUTRAL, physicsObject.getBehavior());

		setPhysicsCollisionFilterAction.act(1.0f);

		assertEquals("Unexpected physics object type", behavior, physicsObject.getBehavior());
	}

	public void testPhysicsCollisionFilterFriendly() {
		PhysicsObject.Behavior behavior = PhysicsObject.Behavior.FRIENDLY;
		SetPhysicsCollisionFilterAction setPhysicsCollisionFilterAction = new SetPhysicsCollisionFilterAction();
		PhysicsObject physicsObject = physicsWorld.getPhysicsObject(sprite);
		setPhysicsCollisionFilterAction.setPhysicsObject(physicsObject);
		setPhysicsCollisionFilterAction.setBehavior(behavior);

		assertEquals("Unexpected physics collision filter", PhysicsObject.Behavior.NEUTRAL, physicsObject.getBehavior());

		setPhysicsCollisionFilterAction.act(1.0f);

		assertEquals("Unexpected physics object type", behavior, physicsObject.getBehavior());
	}

	public void testPhysicsCollisionFilterHostile() {
		PhysicsObject.Behavior behavior = PhysicsObject.Behavior.HOSTILE;
		SetPhysicsCollisionFilterAction setPhysicsCollisionFilterAction = new SetPhysicsCollisionFilterAction();
		PhysicsObject physicsObject = physicsWorld.getPhysicsObject(sprite);
		setPhysicsCollisionFilterAction.setPhysicsObject(physicsObject);
		setPhysicsCollisionFilterAction.setBehavior(behavior);

		assertEquals("Unexpected physics collision filter", PhysicsObject.Behavior.NEUTRAL, physicsObject.getBehavior());

		setPhysicsCollisionFilterAction.act(1.0f);

		assertEquals("Unexpected physics object type", behavior, physicsObject.getBehavior());
	}

}
