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

import org.catrobat.catroid.common.NfcTagContainer;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.IfLogicEndBrick;
import org.catrobat.catroid.content.bricks.LoopEndBrick;
import org.catrobat.catroid.content.bricks.ScriptBrick;
import org.catrobat.catroid.content.bricks.WhenNfcBrick;

import java.util.ArrayList;

public class WhenNfcScript extends Script {

	private static final long serialVersionUID = 1L;
	private String nfcTagName;
	private boolean matchAll = true;

	public WhenNfcScript(Sprite sprite) {
		super(sprite);
	}

	public WhenNfcScript(Sprite sprite, String nfcTagName) {
		super(sprite);
		this.nfcTagName = nfcTagName;
	}

	@Override
	public Script copyScriptForSprite(Sprite copySprite) {
		WhenNfcScript cloneScript = new WhenNfcScript(copySprite);
		ArrayList<Brick> cloneBrickList = cloneScript.getBrickList();

		for (Brick brick : getBrickList()) {
			Brick copiedBrick = brick.copyBrickForSprite(copySprite, cloneScript);
			if (copiedBrick instanceof IfLogicEndBrick) {
				setIfBrickReferences((IfLogicEndBrick) copiedBrick, (IfLogicEndBrick) brick);
			} else if (copiedBrick instanceof LoopEndBrick) {
				setLoopBrickReferences((LoopEndBrick) copiedBrick, (LoopEndBrick) brick);
			}
			cloneBrickList.add(copiedBrick);
		}
		return cloneScript;
	}

	@Override
	public ScriptBrick getScriptBrick() {
		if (brick == null) {
			brick = new WhenNfcBrick(object, this);
		}
		return brick;
	}

	public void setTagName(String tagName) {
		this.nfcTagName = tagName;
	}

	public String getTagName() {
		return nfcTagName;
	}

	public void setMatchAll(boolean matchAll) {
		this.matchAll = matchAll;
	}

	public boolean isMatchAll() {
		return matchAll;
	}
}
