/**
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
package org.catrobat.catroid.content.bricks;

import android.content.Context;
import android.view.View;
import android.widget.BaseAdapter;

import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;

public class ArduinoIrBrick extends BrickBaseType {
	private static final long serialVersionUID = 1L;
	private String text = "";

	private transient View prototypeView;

	public ArduinoIrBrick(Sprite sprite, String text) {
		this.sprite = sprite;
		this.text = text;
	}

	public ArduinoIrBrick() {
	}

	@Override
	public int getRequiredResources() {
		return BLUETOOTH_ARDUINO;
	}

	@Override
	public Brick copyBrickForSprite(Sprite sprite, Script script) {
		ArduinoIrBrick copyBrick = (ArduinoIrBrick) clone();
		copyBrick.sprite = sprite;
		return copyBrick;
	}

	public String getText() {
		return text;
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter adapter) {
		return null;
	}

	@Override
	public View getViewWithAlpha(int alphaValue) {
		return null;
	}

	@Override
	public View getPrototypeView(Context context) {
		return null;
	}

	@Override
	public Brick clone() {
		return new ArduinoIrBrick(this.sprite, this.text);
	}

	@Override
	public List<SequenceAction> addActionToSequence(SequenceAction sequence) {
		return null;
	}
}
