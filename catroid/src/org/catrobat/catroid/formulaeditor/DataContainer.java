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
package org.catrobat.catroid.formulaeditor;

import android.content.Context;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.ui.adapter.DataAdapter;
import org.catrobat.catroid.ui.adapter.UserListAdapter;
import org.catrobat.catroid.ui.adapter.UserVariableAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class DataContainer implements Serializable {
	private static final long serialVersionUID = 1L;

	@XStreamAlias("programVariableList")
	private List<UserVariable> projectVariables;
	@XStreamAlias("objectVariableList")
	private Map<Sprite, List<UserVariable>> spriteVariables;

	@XStreamAlias("programListOfLists")
	private List<UserList> projectLists;
	@XStreamAlias("objectListOfList")
	private Map<Sprite, List<UserList>> spriteListOfLists;

	public DataContainer() {
		projectVariables = new ArrayList<UserVariable>();
		spriteVariables = new HashMap<Sprite, List<UserVariable>>();

		projectLists = new ArrayList<UserList>();
		spriteListOfLists = new HashMap<Sprite, List<UserList>>();
	}

	public UserListAdapter createUserListAdapter(Context context, Sprite sprite) {
		return new UserListAdapter(context, getOrCreateUserListListForSprite(sprite), projectLists);
	}

	public UserVariableAdapter createUserVariableAdapter(Context context, Sprite sprite) {
		return new UserVariableAdapter(context, getOrCreateVariableListForSprite(sprite), projectVariables);
	}

	public DataAdapter createDataAdapter(Context context, Sprite sprite) {
		return new DataAdapter(context, getOrCreateUserListListForSprite(sprite), projectLists, getOrCreateVariableListForSprite(sprite), projectVariables);
	}

	public UserVariable getUserVariable(String userVariableName, Sprite sprite) {
		UserVariable var;
		var = findUserVariable(userVariableName, getOrCreateVariableListForSprite(sprite));
		if (var == null) {
			var = findUserVariable(userVariableName, projectVariables);
		}
		return var;
	}

	public UserVariable addSpriteUserVariable(String userVariableName) {
		Sprite currentSprite = ProjectManager.getInstance().getCurrentSprite();
		return addSpriteUserVariableToSprite(currentSprite, userVariableName);
	}

	public UserVariable addSpriteUserVariableToSprite(Sprite sprite, String userVariableName) {
		UserVariable userVariableToAdd = new UserVariable(userVariableName);
		List<UserVariable> varList = getOrCreateVariableListForSprite(sprite);
		varList.add(userVariableToAdd);
		return userVariableToAdd;
	}

	public UserVariable addProjectUserVariable(String userVariableName) {
		UserVariable userVariableToAdd = new UserVariable(userVariableName);
		projectVariables.add(userVariableToAdd);
		return userVariableToAdd;
	}

	public void deleteUserVariableByName(String userVariableName) {
		Sprite currentSprite = ProjectManager.getInstance().getCurrentSprite();
		UserVariable variableToDelete;
		List<UserVariable> spriteVariables = getOrCreateVariableListForSprite(currentSprite);
		variableToDelete = findUserVariable(userVariableName, spriteVariables);
		if (variableToDelete != null) {
			spriteVariables.remove(variableToDelete);
		}

		variableToDelete = findUserVariable(userVariableName, projectVariables);
		if (variableToDelete != null) {
			projectVariables.remove(variableToDelete);
		}
	}

	public List<UserVariable> getOrCreateVariableListForSprite(Sprite sprite) {
		List<UserVariable> variables = spriteVariables.get(sprite);
		if (variables == null) {
			variables = new ArrayList<UserVariable>();
			spriteVariables.put(sprite, variables);
		}
		return variables;
	}

	public void cleanVariableListForSprite(Sprite sprite) {
		List<UserVariable> vars = spriteVariables.get(sprite);
		if (vars != null) {
			vars.clear();
		}
		spriteVariables.remove(sprite);
	}

	private UserVariable findUserVariable(String name, List<UserVariable> variables) {
		if (variables == null) {
			return null;
		}
		for (UserVariable variable : variables) {
			if (variable.getName().equals(name)) {
				return variable;
			}
		}
		return null;
	}

	public void resetAllDataObjects() {
		resetAllUserLists();
		resetAllUserVariables();
	}

	private void resetAllUserVariables() {
		resetUserVariables(projectVariables);

		Iterator<Sprite> spriteIterator = spriteVariables.keySet().iterator();
		while (spriteIterator.hasNext()) {
			Sprite currentSprite = spriteIterator.next();
			resetUserVariables(spriteVariables.get(currentSprite));
		}
	}

	private void resetUserVariables(List<UserVariable> userVariableList) {
		for (UserVariable userVariable : userVariableList) {
			userVariable.setValue(0.0);
		}
	}

	public UserList getUserList(String userListName, Sprite sprite) {
		UserList userList;
		userList = findUserList(userListName, getOrCreateUserListListForSprite(sprite));
		if (userList == null) {
			userList = findUserList(userListName, projectLists);
		}
		return userList;
	}

	public UserList addSpriteUserList(String userListName) {
		Sprite currentSprite = ProjectManager.getInstance().getCurrentSprite();
		return addSpriteUserListToSprite(currentSprite, userListName);
	}

	public UserList addSpriteUserListToSprite(Sprite sprite, String userListName) {
		UserList userListToAdd = new UserList(userListName);
		List<UserList> listOfUserLists = getOrCreateUserListListForSprite(sprite);
		listOfUserLists.add(userListToAdd);
		return userListToAdd;
	}

	public UserList addProjectUserList(String userListName) {
		UserList userListToAdd = new UserList(userListName);
		projectLists.add(userListToAdd);
		return userListToAdd;
	}

	public void deleteUserListByName(String userListName) {
		Sprite currentSprite = ProjectManager.getInstance().getCurrentSprite();
		UserList listToDelete;
		List<UserList> spriteVariables = getOrCreateUserListListForSprite(currentSprite);
		listToDelete = findUserList(userListName, spriteVariables);
		if (listToDelete != null) {
			spriteVariables.remove(listToDelete);
		}

		listToDelete = findUserList(userListName, projectLists);
		if (listToDelete != null) {
			projectLists.remove(listToDelete);
		}
	}

	private List<UserList> getOrCreateUserListListForSprite(Sprite sprite) {
		List<UserList> userLists = spriteListOfLists.get(sprite);
		if (userLists == null) {
			userLists = new ArrayList<UserList>();
			spriteListOfLists.put(sprite, userLists);
		}
		return userLists;
	}

	public void cleanUserListForSprite(Sprite sprite) {
		List<UserList> listOfUserLists = spriteListOfLists.get(sprite);
		if (listOfUserLists != null) {
			listOfUserLists.clear();
		}
		spriteListOfLists.remove(sprite);
	}

	private UserList findUserList(String name, List<UserList> userLists) {
		if (userLists == null) {
			return null;
		}
		for (UserList userList : userLists) {
			if (userList.getName().equals(name)) {
				return userList;
			}
		}
		return null;
	}

	private void resetAllUserLists() {

		resetUserLists(projectLists);

		Iterator<Sprite> spriteIterator = spriteListOfLists.keySet().iterator();
		while (spriteIterator.hasNext()) {
			Sprite currentSprite = spriteIterator.next();
			resetUserLists(spriteListOfLists.get(currentSprite));
		}
	}

	private void resetUserLists(List<UserList> userVariableList) {
		for (UserList userList : userVariableList) {
			userList.getList().clear();
		}
	}

	public UserList getUserList() {

		if (projectLists.size() > 0) {
			return projectLists.get(0);
		}

		Iterator<Sprite> spriteIterator = spriteListOfLists.keySet().iterator();
		while (spriteIterator.hasNext()) {
			Sprite currentSprite = spriteIterator.next();
			if (spriteListOfLists.get(currentSprite).size() > 0) {
				return spriteListOfLists.get(currentSprite).get(0);
			}
		}
		return null;
	}
}