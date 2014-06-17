package org.catrobat.catroid.uitest.wrapper;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.physics.PhysicsObject;
import org.catrobat.catroid.wrapper.ProjectWrapper;
import org.catrobat.catroid.wrapper.SpriteWrapper;

public class CreateSimpleProject extends CreateAndPlayProject {

	@Override
	protected void createProject() {
		new ProjectWrapper(getActivity(), "Muku_" + System.currentTimeMillis() / 1000, 480, 800) {

			@Override
			protected void createSprites() {
				getBackground()
				// Add look manually to choose a name
					.addLook(R.drawable.default_project_background, "landscape");

				SpriteWrapper mole1 = new SpriteWrapper(new Sprite("Mole1"))
					.whenProgramStarted()
						// Look is automatically added (name == id)
						.setLook(R.drawable.default_project_mole_digged_out)
						.setSize(20)
						.placeAt(0, 300)
						.setPhysicalObject(PhysicsObject.Type.DYNAMIC)
						.setBounceFactor(92)
						.forever()
							.ifOnEdgeBounce()
						.endForever();

				new SpriteWrapper(new Sprite("Mole2"))
					// Add variables
					.addSpriteVariable("size_increment")

					.whenProgramStarted().setVariable("size_increment", 0.05)
						.setLook(R.drawable.default_project_mole_digged_out)
						.setSize(20)
						.setPhysicalObject(PhysicsObject.Type.DYNAMIC)
						// Strings are parsed automatically to Formula
						.setBounceFactor("92")
						.forever()
							.ifOnEdgeBounce()
						.endForever()

					.whenCollisionBetween(mole1)
						// Simply use object parameters, math functions and operators
						.ifCondition("size < max(10,60)")
							// Use single quotation marks for variables
							.changeSizeBy("size * 'size_increment'")
						// don't forget the else and end brick
						.elseCondition()
						.endIfCondition();
			}
		}.createProject();
	}

}
