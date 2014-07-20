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
package org.catrobat.catroid.content.actions;

import com.badlogic.gdx.scenes.scene2d.actions.DelegateAction;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.UserBrickStageToken;
<<<<<<< HEAD
=======
import org.catrobat.catroid.content.bricks.UserBrickVariable;
import org.catrobat.catroid.formulaeditor.UserVariable;
>>>>>>> fixed GSOCSF-6 Variabletext doesn't get deleted
import org.catrobat.catroid.formulaeditor.UserVariablesContainer;

public class UserBrickAction extends DelegateAction {

//	private Sprite sprite;
	private UserBrickStageToken userBrickToken;

//	public void setSprite(Sprite sprite) {
//		this.sprite = sprite;
//	}

	public void setUserBrickStageToken(UserBrickStageToken userBrickToken) {
		this.userBrickToken = userBrickToken;
	}

	@Override
	public boolean act(float delta) {
<<<<<<< HEAD
		UserVariablesContainer userVariables = ProjectManager.getInstance().getCurrentProject().getUserVariables();
		userVariables.setCurrentUserBrickBeingEvaluated(userBrickToken.userBrickId);
=======
//		for (UserBrickVariable userBrickVariable : userBrickToken.variables) {
//			Log.e("UserBrickAction_act()", "bug2 - " + userBrickVariable.variable.getName() + "value: " + userBrickVariable.variable.getValue());
//			double value = userBrickVariable.formula.interpretDouble(sprite);
//			userBrickVariable.variable.setValue(value);
//			Log.e("UserBrickAction_act()", "bug2 - " + userBrickVariable.variable.getName() + "value: " + userBrickVariable.variable.getValue());
//		}

		UserVariablesContainer userVariables = ProjectManager.getInstance().getCurrentProject().getUserVariables();
		userVariables.setCurrentUserBrickBeingEvaluated(userBrickToken.userBrickId);

//		for(UserVariable uv : userVariables.getProjectVariables())
//			Log.e("UserBrickAction_act()", "bug2 - ProjectVariable: " + uv.getName() + "value: " + uv.getValue());
//		for(UserVariable uv : userVariables.getOrCreateVariableListForUserBrick(userBrickToken.userBrickId))
//			Log.e("UserBrickAction_act()", "bug2 - UserBrickVariable: " + uv.getName() + "value: " + uv.getValue());


>>>>>>> fixed GSOCSF-6 Variabletext doesn't get deleted
		return action.act(delta);
	}

}
