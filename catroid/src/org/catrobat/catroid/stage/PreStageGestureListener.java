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
package org.catrobat.catroid.stage;

import android.util.Log;

import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.Vector2;

public class PreStageGestureListener implements GestureListener {

	//	public boolean onTouchEvent(MotionEvent event) {
	//
	//		Log.d("onTouchEvent", "action: " + event.getAction());
	//		Log.d("onTouchEvent", "actionMask: " + event.getActionMasked());
	//
	//		switch (event.getAction()) {
	//			case MotionEvent.ACTION_DOWN:
	//				Log.d("onTouchEvent", "ACTION_DOWN");
	//				return true;
	//			case MotionEvent.ACTION_MOVE:
	//				Log.d("onTouchEvent", "ACTION_MOVE");
	//				break;
	//			case MotionEvent.ACTION_UP:
	//				Log.d("onTouchEvent", "ACTION_UP");
	//				break;
	//			default:
	//				return false;
	//		}
	//		return true;
	//	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.badlogic.gdx.input.GestureDetector.GestureListener#touchDown(float, float, int, int)
	 */
	@Override
	public boolean touchDown(float x, float y, int pointer, int button) {
		// TODO Auto-generated method stub

		Log.d("touchDown", "x: " + x + " - y: " + y);

		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.badlogic.gdx.input.GestureDetector.GestureListener#tap(float, float, int, int)
	 */
	@Override
	public boolean tap(float x, float y, int count, int button) {
		// TODO Auto-generated method stub

		Log.d("tap", "x: " + x + " - y: " + y);

		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.badlogic.gdx.input.GestureDetector.GestureListener#longPress(float, float)
	 */
	@Override
	public boolean longPress(float x, float y) {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.badlogic.gdx.input.GestureDetector.GestureListener#fling(float, float, int)
	 */
	@Override
	public boolean fling(float velocityX, float velocityY, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.badlogic.gdx.input.GestureDetector.GestureListener#pan(float, float, float, float)
	 */
	@Override
	public boolean pan(float x, float y, float deltaX, float deltaY) {
		// TODO Auto-generated method stub

		Log.d("pan", "x: " + x + " - y: " + y);

		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.badlogic.gdx.input.GestureDetector.GestureListener#zoom(float, float)
	 */
	@Override
	public boolean zoom(float initialDistance, float distance) {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.badlogic.gdx.input.GestureDetector.GestureListener#pinch(com.badlogic.gdx.math.Vector2,
	 * com.badlogic.gdx.math.Vector2, com.badlogic.gdx.math.Vector2, com.badlogic.gdx.math.Vector2)
	 */
	@Override
	public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
		// TODO Auto-generated method stub

		Log.d("pinch", "P1x: " + pointer1.x + " - P1y: " + pointer1.y);
		Log.d("pinch", "P2x: " + pointer2.x + " - P2y: " + pointer2.y);
		//Log.d("pinch", "InitP1x: " + initialPointer1.x + " - InitP1y: " + initialPointer1.y);
		//Log.d("pinch", "InitP2x: " + initialPointer2.x + " - InitP2y: " + initialPointer2.y);

		return false;
	}

}
