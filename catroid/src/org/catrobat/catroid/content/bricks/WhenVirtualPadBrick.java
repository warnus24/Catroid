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
package org.catrobat.catroid.content.bricks;

import java.util.List;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.WhenVirtualPadScript;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

public class WhenVirtualPadBrick extends ScriptBrick {
	protected WhenVirtualPadScript whenVirtualPadScript;
	private static final long serialVersionUID = 1L;

	public static enum Direction {
		UP(0), DOWN(1), LEFT(2), RIGHT(3);

		private int id;

		private Direction(int id) {
			this.id = id;
		}

		public int getId() {
			return this.id;
		}

		public static Direction getDirectionById(int id) {
			for (int i = 0; i < Direction.values().length; i++) {
				Direction direction = Direction.values()[i];
				if (direction.getId() == id) {
					return direction;
				}
			}
			return null;
		}

		public static int getIdByDirection(Direction direction) {
			for (int i = 0; i < Direction.values().length; i++) {
				Direction tmpDirection = Direction.values()[i];
				if (tmpDirection.getId() == direction.getId()) {
					return i;
				}
			}
			return 0;
		}
	}

	private transient Direction direction;

	public WhenVirtualPadBrick(Sprite sprite, WhenVirtualPadScript whenVirtualPadScript, Direction direction) {
		this.whenVirtualPadScript = whenVirtualPadScript;
		this.sprite = sprite;
		this.direction = direction;

		if (this.direction == null) {
			this.direction = Direction.UP;
		}

		if (this.whenVirtualPadScript == null) {
			this.whenVirtualPadScript = new WhenVirtualPadScript(this.sprite, this.direction.getId());
		} else {
			this.whenVirtualPadScript.setId(this.direction.getId());
		}

	}

	public WhenVirtualPadBrick() {
		if (this.direction == null) {
			this.direction = Direction.UP;
		}
	}

	@Override
	public int getRequiredResources() {
		return NO_RESOURCES;
	}

	@Override
	public Brick copyBrickForSprite(Sprite sprite, Script script) {
		WhenVirtualPadBrick copyBrick = (WhenVirtualPadBrick) clone();
		copyBrick.sprite = sprite;
		copyBrick.whenVirtualPadScript = (WhenVirtualPadScript) script;
		return copyBrick;
	}

	@Override
	public View getView(final Context context, int brickId, final BaseAdapter baseAdapter) {
		if (whenVirtualPadScript == null) {
			whenVirtualPadScript = new WhenVirtualPadScript(sprite, direction.getId());
		}

		view = View.inflate(context, R.layout.brick_when_virtual_pad, null);

		final Spinner directionSpinner = (Spinner) view.findViewById(R.id.brick_when_virtual_pad_spinner);

		ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(context, R.array.virtual_pad_chooser,
				android.R.layout.simple_spinner_item);
		arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		directionSpinner.setAdapter(arrayAdapter);
		directionSpinner.setClickable(true);
		directionSpinner.setFocusable(true);

		directionSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				direction = Direction.values()[position];
				if (whenVirtualPadScript == null) {
					whenVirtualPadScript = new WhenVirtualPadScript(sprite, direction.getId());
				} else {
					whenVirtualPadScript.setId(direction.getId());
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});

		//directionSpinner.setSelection(direction.ordinal());
		directionSpinner.setSelection(Direction.getIdByDirection(direction));
		directionSpinner.setFocusable(false);

		return view;
	}

	@Override
	public View getViewWithAlpha(int alphaValue) {
		LinearLayout layout = (LinearLayout) view.findViewById(R.id.brick_when_virtual_pad_layout);
		Drawable background = layout.getBackground();
		background.setAlpha(alphaValue);
		this.alphaValue = (alphaValue);
		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		return getView(context, 0, null);
	}

	@Override
	public Brick clone() {
		return new WhenVirtualPadBrick(getSprite(), whenVirtualPadScript, direction);
	}

	@Override
	public Script initScript(Sprite sprite) {
		if (whenVirtualPadScript == null) {
			whenVirtualPadScript = new WhenVirtualPadScript(sprite, direction.getId());
		}

		return whenVirtualPadScript;
	}

	@Override
	public List<SequenceAction> addActionToSequence(SequenceAction sequence) {
		return null;

	}
}
