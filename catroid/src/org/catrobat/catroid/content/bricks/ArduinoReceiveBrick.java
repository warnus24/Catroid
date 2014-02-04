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

import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ArduinoReceiveAction;
import org.catrobat.catroid.content.actions.ExtendedActions;

import java.util.List;

/**
 * @author Adrian Schnedlitz
 * 
 */

public class ArduinoReceiveBrick extends BrickBaseType implements OnClickListener {

	//TODO Change this to a proper value
	private static final long serialVersionUID = 1l;
	private transient View prototypeView;

	public ArduinoReceiveBrick() {
	}

	public ArduinoReceiveBrick(Sprite sprite) {
		this.sprite = sprite;
	}

	@Override
	public int getRequiredResources() {
		return BLUETOOTH_ARDUINO; //instead of NO_RESOURCES
	}

	@Override
	public Brick copyBrickForSprite(Sprite sprite, Script script) {
		ArduinoReceiveBrick copyBrick = (ArduinoReceiveBrick) clone();
		copyBrick.sprite = sprite;
		return copyBrick;
	}

	@Override
	public View getPrototypeView(Context context) {
		prototypeView = View.inflate(context, R.layout.brick_arduino_receive, null);

		//		Spinner arduinoPinSpinner = (Spinner) prototypeView.findViewById(R.id.brick_arduino_send_pin_spinner);
		//		arduinoPinSpinner.setFocusableInTouchMode(false);
		//		arduinoPinSpinner.setFocusable(false);
		//correct way to fill a spinner with values

		//		ArrayAdapter<CharSequence> pinSpinnerAdapter = ArrayAdapter.createFromResource(context,
		//				R.array.arduino_pin_chooser, android.R.layout.simple_spinner_item);
		//		pinSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		//
		//		arduinoPinSpinner.setAdapter(pinSpinnerAdapter);

		return prototypeView;

	}

	@Override
	public Brick clone() {
		return new ArduinoReceiveBrick(getSprite());
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter baseAdapter) {
		if (animationState) {
			return view;
		}

		view = View.inflate(context, R.layout.brick_arduino_receive, null);
		view = getViewWithAlpha(alphaValue);

		setCheckboxView(R.id.brick_arduino_receive_checkbox);
		checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				checked = isChecked;
				adapter.handleCheck(ArduinoReceiveBrick.this, isChecked);
			}
		});

		ArduinoReceiveAction.initBluetoothConnection();

		BluetoothSocket tmpSocket = ArduinoReceiveAction.getBluetoothSocket();
		TextView textField = (TextView) view.findViewById(R.id.brick_data_received_text_view);
		int tmpMessage = ArduinoReceiveAction.receiveDataViaBluetoothSocket(tmpSocket);

		if (tmpMessage == 76) {
			//same problem like at the sendBrick, implementation of the View in PC isn't correctly
			textField.setText('L');
		} else if (tmpMessage == 72) {
			textField.setText('H');
		} else {
			textField.setText("N/A");
		}

		return view;
	}

	@Override
	public View getViewWithAlpha(int alphaValue) {

		if (view != null) {
			//			Log.d("TAG", "VIEW != NULL");
			View layout = view.findViewById(R.id.brick_arduino_receive_layout);
			Drawable background = layout.getBackground();
			background.setAlpha(alphaValue);

			this.alphaValue = (alphaValue);
		}

		return view;
	}

	@Override
	public List<SequenceAction> addActionToSequence(SequenceAction sequence) {
		sequence.addAction(ExtendedActions.receiveArduinoVar(sprite));
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub

	}
}
