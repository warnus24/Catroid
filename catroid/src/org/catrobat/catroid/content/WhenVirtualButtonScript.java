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
package org.catrobat.catroid.content;

import java.util.ArrayList;

import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.ScriptBrick;
import org.catrobat.catroid.content.bricks.WhenVirtualButtonBrick;

public class WhenVirtualButtonScript extends Script {

	private static final long serialVersionUID = 1L;
	private int id;

	public WhenVirtualButtonScript() {
		id = -1;
	}

	public WhenVirtualButtonScript(Sprite sprite) {
		super(sprite);
		this.id = -1;
	}

	public WhenVirtualButtonScript(Sprite sprite, int id) {
		super(sprite);
		this.id = id;
	}

	public WhenVirtualButtonScript(Sprite sprite, WhenVirtualButtonBrick brick, int id) {
		this(sprite, id);
		this.brick = brick;
		this.id = id;
	}

	@Override
	protected Object readResolve() {
		super.readResolve();
		return this;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	@Override
	public ScriptBrick getScriptBrick() {
		if (brick == null) {
			brick = new WhenVirtualButtonBrick(object, this, WhenVirtualButtonBrick.Action.getActionById(id));
		}
		return brick;
	}

	@Override
	public Script copyScriptForSprite(Sprite copySprite) {
		WhenVirtualButtonScript cloneScript = new WhenVirtualButtonScript(copySprite);
		ArrayList<Brick> cloneBrickList = cloneScript.getBrickList();

		for (Brick b : getBrickList()) {
			cloneBrickList.add(b.copyBrickForSprite(copySprite, cloneScript));
		}

		return cloneScript;
	}

}