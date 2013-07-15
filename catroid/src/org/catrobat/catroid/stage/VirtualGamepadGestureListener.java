<<<<<<< HEAD
<<<<<<< HEAD
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

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.WhenVirtualPadBrick.Direction;

import android.util.Log;

import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.Vector2;

public class VirtualGamepadGestureListener implements GestureListener {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.badlogic.gdx.input.GestureDetector.GestureListener#touchDown(float, float, int, int)
	 */
	@Override
	public boolean touchDown(float x, float y, int pointer, int button) {
		// TODO Auto-generated method stub

		Sprite sprite = ProjectManager.getInstance().getCurrentSprite();
		if (sprite == null) {
			Log.e("PreStageGestureListener<touchDown>", "sprite is null");
			return false;
		} else if (sprite.isPaused) {
			return false;
		}

		sprite.createWhenVirtualPadScriptActionSequence(Direction.UP.getId());

		Log.d("touchDown", "x: " + x + " - y: " + y);
		Log.d("touchDown - POINTER", "pointer: " + pointer + " - button: " + button);

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

		Log.d("longPress", "x: " + x + " - y: " + y);

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

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.WhenVirtualPadBrick.Direction;

import android.util.Log;

import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.Vector2;

public class VirtualGamepadGestureListener implements GestureListener {

	private static float dPadInitValue = -100000.0f;

	private float dPadStartX;
	private float dPadStartY;
	private double dPadMinMotion = 10.0;
	private double dPadMaxMotion = 50.0;
	private DPadThread dPadThread;

	private boolean dPadUp;
	private boolean dPadDown;
	private boolean dPadLeft;
	private boolean dPadRight;

	public VirtualGamepadGestureListener() {
		dPadStartX = dPadInitValue;
		dPadStartY = dPadInitValue;

		dPadUp = false;
		dPadDown = false;
		dPadLeft = false;
		dPadRight = false;

		dPadThread = new DPadThread();
	}

	class DPadThread extends Thread {

		private boolean running;

		@Override
		public void run() {
			try {

				Log.e("DPadThread<run>", "thread started");

				while (running) {

					Sprite sprite = ProjectManager.getInstance().getCurrentSprite();
					if (sprite == null) {
						Log.e("DPadThread<run>", "sprite is null");
						running = false;
						return;
					} else if (sprite.isPaused) {
						running = false;
						return;
					}

					if (dPadUp) {
						sprite.createWhenVirtualPadScriptActionSequence(Direction.UP.getId());
					}

					if (dPadDown) {
						sprite.createWhenVirtualPadScriptActionSequence(Direction.DOWN.getId());
					}

					if (dPadLeft) {
						sprite.createWhenVirtualPadScriptActionSequence(Direction.LEFT.getId());
					}

					if (dPadRight) {
						sprite.createWhenVirtualPadScriptActionSequence(Direction.RIGHT.getId());
					}

					//					Log.e("DPAD THREAD", "dpad thread is running... [" + dPadUp + "|" + dPadDown + "|" + dPadLeft + "|"
					//							+ dPadRight + "]");
					Thread.sleep(100);
				}

				Log.e("DPadThread<run>", "thread stopped");

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public void start() {
			try {
				running = true;
				Log.e("DPadThread<start>", "start thread");
				super.start();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public void stopThread() {
			running = false;
		}

		/**
		 * @return the running
		 */
		public boolean getRunning() {
			return running;
		}

		/**
		 * @param running
		 *            the running to set
		 */
		public void setRunning(boolean running) {
			this.running = running;
		}
	}

	private void setDPadDirection(boolean dPadUp, boolean dPadDown, boolean dPadLeft, boolean dPadRight) {
		this.dPadUp = dPadUp;
		this.dPadDown = dPadDown;
		this.dPadLeft = dPadLeft;
		this.dPadRight = dPadRight;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.badlogic.gdx.input.GestureDetector.GestureListener#touchDown(float, float, int, int)
	 */
	@Override
	public boolean touchDown(float x, float y, int pointer, int button) {
		// TODO Auto-generated method stub

		//Log.e(":::::: touchDown", "x: " + x + " - y: " + y);
		//Log.e(":::::: touchDown - POINTER", "pointer: " + pointer + " - button: " + button);

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

		Log.e(":::::: tap", "x: " + x + " - y: " + y + " - count: " + count + " - button: " + button);

		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.badlogic.gdx.input.GestureDetector.GestureListener#longPress(float, float)
	 */
	@Override
	public boolean longPress(float x, float y) {
		try {
			//Log.e(":::::: longPress", "x: " + x + " - y: " + y);
			//Log.e(":::::: INIT DPAD", "x: " + x + " - y: " + y);

			//dPadStartX = x;
			//dPadStartY = y;
			dPadThread = new DPadThread();
			dPadThread.start();

			//pan(x, y, x, y);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.badlogic.gdx.input.GestureDetector.GestureListener#fling(float, float, int)
	 */
	@Override
	public boolean fling(float velocityX, float velocityY, int button) {
		try {
			Log.e(":::::: fling", "x: " + velocityX + " - y: " + velocityY + " - button: " + button);

			dPadStartX = dPadInitValue;
			dPadStartY = dPadInitValue;

			dPadThread.stopThread();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.badlogic.gdx.input.GestureDetector.GestureListener#pan(float, float, float, float)
	 */
	@Override
	public boolean pan(float x, float y, float deltaX, float deltaY) {
		try {
			//init start values
			if (dPadStartX == dPadInitValue || dPadStartY == dPadInitValue) {
				dPadStartX = x;
				dPadStartY = y;
				return false;
			}

			double distance = Math.sqrt(Math.pow(dPadStartX - x, 2) + Math.pow(dPadStartY - y, 2));
			if (distance <= dPadMinMotion || distance > dPadMaxMotion) {
				setDPadDirection(false, false, false, false);

				Log.e(":::::: pan NO MOTION", "x: " + x + " - y: " + y + " - distance: " + distance);
			} else {
				boolean tmpUp = false;
				boolean tmpDown = false;
				boolean tmpLeft = false;
				boolean tmpRight = false;

				//direction
				if (x > dPadStartX && (x - dPadMinMotion) > dPadStartX) {
					tmpRight = true;
				} else if (x < dPadStartX && (x + dPadMinMotion) < dPadStartX) {
					tmpLeft = true;
				}

				if (y > dPadStartY && (y - dPadMinMotion) > dPadStartY) {
					tmpUp = true;
				} else if (y < dPadStartY && (y + dPadMinMotion) < dPadStartY) {
					tmpDown = true;
				}

				setDPadDirection(tmpUp, tmpDown, tmpLeft, tmpRight);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
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

		//Log.e(":::::: pinch", "P1x: " + pointer1.x + " - P1y: " + pointer1.y);
		//Log.e(":::::: pinch", "P2x: " + pointer2.x + " - P2y: " + pointer2.y);
		//Log.d("pinch", "InitP1x: " + initialPointer1.x + " - InitP1y: " + initialPointer1.y);
		//Log.d("pinch", "InitP2x: " + initialPointer2.x + " - InitP2y: " + initialPointer2.y);

		return false;
	}

}


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

import java.util.List;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.WhenVirtualPadBrick.Direction;

import android.util.Log;

import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.Vector2;

public class VirtualGamepadGestureListener implements GestureListener {

	private static float dPadInitValue = -100000.0f;

	private float dPadStartX;
	private float dPadStartY;
	private double dPadMinMotion = 10.0;
	private double dPadMaxMotion = 50.0;
	private DPadThread dPadThread;

	private boolean dPadUp;
	private boolean dPadDown;
	private boolean dPadLeft;
	private boolean dPadRight;

	public VirtualGamepadGestureListener() {
		dPadStartX = dPadInitValue;
		dPadStartY = dPadInitValue;

		dPadUp = false;
		dPadDown = false;
		dPadLeft = false;
		dPadRight = false;

		dPadThread = new DPadThread();
	}

	class DPadThread extends Thread {

		private boolean running;

		@Override
		public void run() {
			try {

				Log.e("DPadThread<run>", "thread started");

				List<Sprite> sprites = ProjectManager.getInstance().getCurrentProject().getSpriteList();

				for (int sprite = 0; sprite < sprites.size(); sprite++) {
					if (sprites.get(sprite).getName().equals(Constants.VGP_SPRITE_PAD)) {
						Sprite tmpSprite = sprites.get(sprite);
						tmpSprite.look.setVisible(true);
						break;
					}
				}

				while (running) {

					Sprite sprite = ProjectManager.getInstance().getCurrentSprite();
					if (sprite == null) {
						Log.e("DPadThread<run>", "sprite is null");
						running = false;
						return;
					} else if (sprite.isPaused) {
						running = false;
						return;
					}

					if (dPadUp) {
						sprite.createWhenVirtualPadScriptActionSequence(Direction.UP.getId());
					}

					if (dPadDown) {
						sprite.createWhenVirtualPadScriptActionSequence(Direction.DOWN.getId());
					}

					if (dPadLeft) {
						sprite.createWhenVirtualPadScriptActionSequence(Direction.LEFT.getId());
					}

					if (dPadRight) {
						sprite.createWhenVirtualPadScriptActionSequence(Direction.RIGHT.getId());
					}

					Thread.sleep(100);
				}

				Log.e("DPadThread<run>", "thread stopped");

				for (int sprite = 0; sprite < sprites.size(); sprite++) {
					if (sprites.get(sprite).getName().equals(Constants.VGP_SPRITE_PAD)) {
						Sprite tmpSprite = sprites.get(sprite);
						tmpSprite.look.setVisible(false);
						break;
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public void start() {
			try {
				running = true;
				Log.e("DPadThread<start>", "start thread");
				super.start();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public void stopThread() {
			running = false;
		}

		/**
		 * @return the running
		 */
		public boolean getRunning() {
			return running;
		}

		/**
		 * @param running
		 *            the running to set
		 */
		public void setRunning(boolean running) {
			this.running = running;
		}
	}

	private void setDPadDirection(boolean dPadUp, boolean dPadDown, boolean dPadLeft, boolean dPadRight) {
		this.dPadUp = dPadUp;
		this.dPadDown = dPadDown;
		this.dPadLeft = dPadLeft;
		this.dPadRight = dPadRight;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.badlogic.gdx.input.GestureDetector.GestureListener#touchDown(float, float, int, int)
	 */
	@Override
	public boolean touchDown(float x, float y, int pointer, int button) {
		// TODO Auto-generated method stub

		//Log.e(":::::: touchDown", "x: " + x + " - y: " + y);
		//Log.e(":::::: touchDown - POINTER", "pointer: " + pointer + " - button: " + button);

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

		Log.e(":::::: tap", "x: " + x + " - y: " + y + " - count: " + count + " - button: " + button);

		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.badlogic.gdx.input.GestureDetector.GestureListener#longPress(float, float)
	 */
	@Override
	public boolean longPress(float x, float y) {
		try {
			//Log.e(":::::: longPress", "x: " + x + " - y: " + y);
			//Log.e(":::::: INIT DPAD", "x: " + x + " - y: " + y);

			//dPadStartX = x;
			//dPadStartY = y;
			dPadThread = new DPadThread();
			dPadThread.start();

			//pan(x, y, x, y);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.badlogic.gdx.input.GestureDetector.GestureListener#fling(float, float, int)
	 */
	@Override
	public boolean fling(float velocityX, float velocityY, int button) {
		try {
			Log.e(":::::: fling", "x: " + velocityX + " - y: " + velocityY + " - button: " + button);

			dPadStartX = dPadInitValue;
			dPadStartY = dPadInitValue;

			dPadThread.stopThread();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.badlogic.gdx.input.GestureDetector.GestureListener#pan(float, float, float, float)
	 */
	@Override
	public boolean pan(float x, float y, float deltaX, float deltaY) {
		try {
			//init start values
			if (dPadStartX == dPadInitValue || dPadStartY == dPadInitValue) {
				dPadStartX = x;
				dPadStartY = y;
				return false;
			}

			double distance = Math.sqrt(Math.pow(dPadStartX - x, 2) + Math.pow(dPadStartY - y, 2));
			if (distance <= dPadMinMotion || distance > dPadMaxMotion) {
				setDPadDirection(false, false, false, false);

				Log.e(":::::: pan NO MOTION", "x: " + x + " - y: " + y + " - distance: " + distance);
			} else {
				boolean tmpUp = false;
				boolean tmpDown = false;
				boolean tmpLeft = false;
				boolean tmpRight = false;

				//direction
				if (x > dPadStartX && (x - dPadMinMotion) > dPadStartX) {
					tmpRight = true;
				} else if (x < dPadStartX && (x + dPadMinMotion) < dPadStartX) {
					tmpLeft = true;
				}

				if (y > dPadStartY && (y - dPadMinMotion) > dPadStartY) {
					tmpDown = true;
				} else if (y < dPadStartY && (y + dPadMinMotion) < dPadStartY) {
					tmpUp = true;
				}

				setDPadDirection(tmpUp, tmpDown, tmpLeft, tmpRight);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
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

		//Log.e(":::::: pinch", "P1x: " + pointer1.x + " - P1y: " + pointer1.y);
		//Log.e(":::::: pinch", "P2x: " + pointer2.x + " - P2y: " + pointer2.y);
		//Log.d("pinch", "InitP1x: " + initialPointer1.x + " - InitP1y: " + initialPointer1.y);
		//Log.d("pinch", "InitP2x: " + initialPointer2.x + " - InitP2y: " + initialPointer2.y);

		return false;
	}

}

