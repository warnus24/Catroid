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

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Spinner;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.MessageContainer;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.WhenNfcScript;

public class WhenNfcBrick extends ScriptBrick {
	protected WhenNfcScript whenNfcScript;
	private static final long serialVersionUID = 1L;
	private transient AdapterView<?> adapterView;
	protected String tagName;

	public WhenNfcBrick(Sprite sprite) {
		this.sprite = sprite;
	}

	public WhenNfcBrick(Sprite sprite, WhenNfcScript script) {
		this.sprite = sprite;
		this.whenNfcScript = script;
	}

	@Override
	public Brick copyBrickForSprite(Sprite sprite, Script script) {
		WhenNfcBrick copyBrick = (WhenNfcBrick) clone();
		copyBrick.sprite = sprite;
		copyBrick.whenNfcScript = (WhenNfcScript) script;
		return copyBrick;
	}

	@Override
	public Script initScript(Sprite sprite) {
		if (whenNfcScript == null) {
			whenNfcScript = new WhenNfcScript(sprite);
		}
		return whenNfcScript;
	}

	@Override
	public Brick clone() {
		return new WhenNfcBrick(sprite, new WhenNfcScript(sprite));
	}

	@Override
	public View getView(final Context context, int brickId, BaseAdapter adapter) {
		if (animationState) {
			return view;
		}
		view = View.inflate(context, R.layout.brick_when_nfc, null);

		setCheckboxView(R.id.brick_when_nfc_checkbox);

		final Spinner nfcSpinner = (Spinner) view.findViewById(R.id.brick_when_nfc_spinner);
		nfcSpinner.setFocusableInTouchMode(false);
		nfcSpinner.setFocusable(false);
		if (!(checkbox.getVisibility() == View.VISIBLE)) {
			nfcSpinner.setClickable(true);
			nfcSpinner.setEnabled(true);
		} else {
			nfcSpinner.setClickable(false);
			nfcSpinner.setEnabled(false);
		}

		// TODO: here we need a "TagContainer" instead of the MessageContainer
		nfcSpinner.setAdapter(MessageContainer.getMessageAdapter(context));
		nfcSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				String selectedTag = nfcSpinner.getSelectedItem().toString();

				// TODO: change new_broad_cast_message to something generic

				if (selectedTag.equals(context.getString(R.string.new_broadcast_message))) {
					showNewTagDialog(nfcSpinner);
				} else {
					tagName = selectedTag;
					adapterView = parent;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});

		setSpinnerSelection(nfcSpinner);

		return view;
	}

	private void setSpinnerSelection(Spinner spinner) {
		int position = MessageContainer.getPositionOfMessageInAdapter(spinner.getContext(), tagName);
		spinner.setSelection(position, true);
	}

	protected void showNewTagDialog(Spinner nfcSpinner) {
		// TODO: implement this like in BroadCastBrick.java
	}

	@Override
	public View getPrototypeView(Context context) {
		return getView(context, 0, null);
	}
}
