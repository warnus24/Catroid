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
import org.catrobat.catroid.content.bricks.WhenKeyBrick;

public class WhenKeyScript extends Script {

	private static final long serialVersionUID = 1L;
	private int keyCode;

	public WhenKeyScript() {
		keyCode = -1;
	}

	public WhenKeyScript(Sprite sprite) {
		super(sprite);
		this.keyCode = -1;
	}

	public WhenKeyScript(Sprite sprite, int keyCode) {
		super(sprite);
		this.keyCode = keyCode;
	}

	public WhenKeyScript(Sprite sprite, WhenKeyBrick brick, int keyCode) {
		this(sprite, keyCode);
		this.brick = brick;
		this.keyCode = keyCode;
	}

	@Override
	protected Object readResolve() {
		super.readResolve();
		return this;
	}

	public void setKeyCode(int keyCode) {
		this.keyCode = keyCode;
	}

	public int getKeyCode() {
		return keyCode;
	}

	@Override
	public ScriptBrick getScriptBrick() {
		if (brick == null) {
			brick = new WhenKeyBrick(object, this, WhenKeyBrick.Key.getKeyByKeyCode(keyCode));
		}
		return brick;
	}

	@Override
	public Script copyScriptForSprite(Sprite copySprite) {
		WhenKeyScript cloneScript = new WhenKeyScript(copySprite);
		ArrayList<Brick> cloneBrickList = cloneScript.getBrickList();

		for (Brick b : getBrickList()) {
			cloneBrickList.add(b.copyBrickForSprite(copySprite, cloneScript));
		}

		return cloneScript;
	}

}