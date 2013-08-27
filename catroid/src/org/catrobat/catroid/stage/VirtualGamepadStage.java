
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

import com.badlogic.gdx.scenes.scene2d.Stage;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.content.Look;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.WhenVirtualButtonScript;
import org.catrobat.catroid.content.WhenVirtualPadScript;
import org.catrobat.catroid.content.bricks.WhenVirtualButtonBrick;
import org.catrobat.catroid.content.bricks.WhenVirtualPadBrick.Direction;

import java.util.List;

public class VirtualGamepadStage extends Stage {

	private Sprite vgpPadSprite;
	private Sprite vgpButtonSprite;

	private static int dPadInitValue = -99999;

	private static double dPadMinMotion = 20.0;
	private static double dPadMaxMotion = 90.0;
	private static double buttonMotion = 20.0;

	private int dPadStartX;
	private int dPadStartY;
	private int buttonStartX;
	private int buttonStartY;

	private DPadThread dPadThread;
	private ButtonThread buttonThread;

	private boolean dPadUp = false;
	private boolean dPadDown = false;
	private boolean dPadLeft = false;
	private boolean dPadRight = false;

	private float width;
	private float height;

	public VirtualGamepadStage(float width, float height, boolean keepAspectRatio) {
		super(width, height, keepAspectRatio);

		this.width = width;
		this.height = height;

		dPadStartX = dPadInitValue;
		dPadStartY = dPadInitValue;
		buttonStartX = dPadInitValue;
		buttonStartY = dPadInitValue;
	}

	private void setDPadDirection(boolean dPadUp, boolean dPadDown, boolean dPadLeft, boolean dPadRight) {
		this.dPadUp = dPadUp;
		this.dPadDown = dPadDown;
		this.dPadLeft = dPadLeft;
		this.dPadRight = dPadRight;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		try {
			if (pointer == 0) {
				dPadThread = new DPadThread();
				dPadThread.start();

				vgpPadSprite.look.setXInUserInterfaceDimensionUnit(screenX - width / 2.0f);
				vgpPadSprite.look.setYInUserInterfaceDimensionUnit(height / 2.0f - screenY);

				vgpPadSprite.look.setVisible(true);
			} else {
				buttonStartX = screenX;
				buttonStartY = screenY;
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		try {

			if (pointer == 0) {
				//init start values
				if (dPadStartX == dPadInitValue || dPadStartY == dPadInitValue) {
					dPadStartX = screenX;
					dPadStartY = screenY;
					return false;
				}

				double distance = Math.sqrt(Math.pow(dPadStartX - screenX, 2) + Math.pow(dPadStartY - screenY, 2));
				if (distance <= dPadMinMotion || distance > dPadMaxMotion) {
					setDPadDirection(false, false, false, false);
				} else {
					boolean tmpUp = false;
					boolean tmpDown = false;
					boolean tmpLeft = false;
					boolean tmpRight = false;

					//direction
					if (screenX > dPadStartX && (screenX - dPadMinMotion) > dPadStartX) {
						tmpRight = true;
					} else if (screenX < dPadStartX && (screenX + dPadMinMotion) < dPadStartX) {
						tmpLeft = true;
					}

					if (screenY > dPadStartY && (screenY - dPadMinMotion) > dPadStartY) {
						tmpDown = true;
					} else if (screenY < dPadStartY && (screenY + dPadMinMotion) < dPadStartY) {
						tmpUp = true;
					}

					setDPadDirection(tmpUp, tmpDown, tmpLeft, tmpRight);
				}
			}

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		try {
			if (pointer == 0) {
				dPadStartX = dPadInitValue;
				dPadStartY = dPadInitValue;

				dPadThread.stopThread();

				vgpPadSprite.look.setVisible(false);
			} else {
				handleButtonAction(screenX, screenY);
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private void handleDPadAction(Sprite sprite) throws Exception {
		boolean changeImage = true;
		if (dPadUp && dPadLeft) {
			//up left
			for (LookData lookData : vgpPadSprite.getLookDataList()) {
				if (lookData.getLookName().equals(Constants.VGP_IMAGE_PAD_DIAGONAL)) {
					vgpPadSprite.look.setLookData(lookData);
					vgpPadSprite.look.setRotation(90.0f);
					break;
				}
			}
			changeImage = false;
		} else if (dPadUp && dPadRight) {
			//up right
			for (LookData lookData : vgpPadSprite.getLookDataList()) {
				if (lookData.getLookName().equals(Constants.VGP_IMAGE_PAD_DIAGONAL)) {
					vgpPadSprite.look.setLookData(lookData);
					vgpPadSprite.look.setRotation(0.0f);
					break;
				}
			}
			changeImage = false;
		} else if (dPadDown && dPadLeft) {
			//down left
			for (LookData lookData : vgpPadSprite.getLookDataList()) {
				if (lookData.getLookName().equals(Constants.VGP_IMAGE_PAD_DIAGONAL)) {
					vgpPadSprite.look.setLookData(lookData);
					vgpPadSprite.look.setRotation(180.0f);
					break;
				}
			}
			changeImage = false;
		} else if (dPadDown && dPadRight) {
			//down right
			for (LookData lookData : vgpPadSprite.getLookDataList()) {
				if (lookData.getLookName().equals(Constants.VGP_IMAGE_PAD_DIAGONAL)) {
					vgpPadSprite.look.setLookData(lookData);
					vgpPadSprite.look.setRotation(270.0f);
					break;
				}
			}
			changeImage = false;
		}

		if (dPadUp) {
			if (changeImage) {
				//up
				for (LookData lookData : vgpPadSprite.getLookDataList()) {
					if (lookData.getLookName().equals(Constants.VGP_IMAGE_PAD_STRAIGHT)) {
						vgpPadSprite.look.setLookData(lookData);
						vgpPadSprite.look.setRotation(0.0f);
						break;
					}
				}
			}
			sprite.createWhenVirtualPadScriptActionSequence(Direction.UP.getId());
		}
		if (dPadDown) {
			if (changeImage) {
				//down
				for (LookData lookData : vgpPadSprite.getLookDataList()) {
					if (lookData.getLookName().equals(Constants.VGP_IMAGE_PAD_STRAIGHT)) {
						vgpPadSprite.look.setLookData(lookData);
						vgpPadSprite.look.setRotation(180.0f);
						break;
					}
				}
			}
			sprite.createWhenVirtualPadScriptActionSequence(Direction.DOWN.getId());
		}
		if (dPadLeft) {
			if (changeImage) {
				//left
				for (LookData lookData : vgpPadSprite.getLookDataList()) {
					if (lookData.getLookName().equals(Constants.VGP_IMAGE_PAD_STRAIGHT)) {
						vgpPadSprite.look.setLookData(lookData);
						vgpPadSprite.look.setRotation(90.0f);
						break;
					}
				}
			}
			sprite.createWhenVirtualPadScriptActionSequence(Direction.LEFT.getId());
		}
		if (dPadRight) {
			if (changeImage) {
				//right
				for (LookData lookData : vgpPadSprite.getLookDataList()) {
					if (lookData.getLookName().equals(Constants.VGP_IMAGE_PAD_STRAIGHT)) {
						vgpPadSprite.look.setLookData(lookData);
						vgpPadSprite.look.setRotation(270.0f);
						break;
					}
				}
			}
			sprite.createWhenVirtualPadScriptActionSequence(Direction.RIGHT.getId());
		}
		if (!dPadUp && !dPadDown && !dPadLeft && !dPadRight) {
			//center
			for (LookData lookData : vgpPadSprite.getLookDataList()) {
				if (lookData.getLookName().equals(Constants.VGP_IMAGE_PAD_CENTER)) {
					vgpPadSprite.look.setLookData(lookData);
					break;
				}
			}
		}
	}

	private void handleButtonAction(int screenX, int screenY) throws Exception {
		Sprite sprite = ProjectManager.getInstance().getCurrentSprite();
		if (sprite == null) {
			sprite = getCurrentVirtualButtonSprite();
			if (sprite == null) {
				throw new Exception("VirtualGamepadStage<handleButtonAction> sprite is null");
			}
		} else if (sprite.isPaused) {
			throw new Exception("VirtualGamepadStage<handleButtonAction> sprite is paused");
		}

		vgpButtonSprite.look.setXInUserInterfaceDimensionUnit(buttonStartX - width / 2.0f);
		vgpButtonSprite.look.setYInUserInterfaceDimensionUnit(height / 2.0f - buttonStartY);

		//handle button coordinates
		double distance = Math.sqrt(Math.pow(buttonStartX - screenX, 2) + Math.pow(buttonStartY - screenY, 2));
		if (distance <= buttonMotion) {
			for (LookData lookData : vgpButtonSprite.getLookDataList()) {
				if (lookData.getLookName().equals(Constants.VGP_IMAGE_BUTTON_TOUCH)) {
					vgpButtonSprite.look.setLookData(lookData);
					break;
				}
			}

			buttonThread = new ButtonThread(vgpButtonSprite.look);
			buttonThread.start();

			sprite.createWhenVirtualButtonScriptActionSequence(WhenVirtualButtonBrick.Action.TOUCH.getId());
		} else {

			int diffX = Math.abs(screenX - buttonStartX);
			int diffY = Math.abs(screenY - buttonStartY);

			float slope = 1.0f;
			if (diffX != 0) {
				slope = diffY / diffX;
			}

			for (LookData lookData : vgpButtonSprite.getLookDataList()) {
				if (lookData.getLookName().equals(Constants.VGP_IMAGE_BUTTON_SWIPE)) {
					vgpButtonSprite.look.setLookData(lookData);
					break;
				}
			}

			int wipeId = -1;

			if (slope < 1.0f) {
				if (screenX >= buttonStartX) {
					//right
					wipeId = WhenVirtualButtonBrick.Action.WIPE_RIGHT.getId();
					vgpButtonSprite.look.setRotation(270.0f);
				} else {
					//left
					wipeId = WhenVirtualButtonBrick.Action.WIPE_LEFT.getId();
					vgpButtonSprite.look.setRotation(90.0f);
				}
			} else {
				if (screenY >= buttonStartY) {
					//down
					wipeId = WhenVirtualButtonBrick.Action.WIPE_DOWN.getId();
					vgpButtonSprite.look.setRotation(180.0f);
				} else {
					//up
					wipeId = WhenVirtualButtonBrick.Action.WIPE_UP.getId();
					vgpButtonSprite.look.setRotation(0.0f);
				}
			}

			buttonThread = new ButtonThread(vgpButtonSprite.look);
			buttonThread.start();

			sprite.createWhenVirtualButtonScriptActionSequence(wipeId);

			buttonStartX = dPadInitValue;
			buttonStartY = dPadInitValue;
		}
	}

	public Sprite getVgpPadSprite() {
		return vgpPadSprite;
	}

	public void setVgpPadSprite(Sprite vgpPadSprite) {
		this.vgpPadSprite = vgpPadSprite;
	}

	public Sprite getVgpButtonSprite() {
		return vgpButtonSprite;
	}

	public void setVgpButtonSprite(Sprite vgpButtonSprite) {
		this.vgpButtonSprite = vgpButtonSprite;
	}

	private Sprite getCurrentVirtualGamepadSprite() {
		try {
			List<Sprite> spriteList = ProjectManager.getInstance().getCurrentProject().getSpriteList();
			for (Sprite tmp : spriteList) {
				for (int script = 0; script < tmp.getNumberOfScripts(); script++) {
					if (tmp.getScript(script) instanceof WhenVirtualPadScript) {
						return tmp;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private Sprite getCurrentVirtualButtonSprite() {
		try {
			List<Sprite> spriteList = ProjectManager.getInstance().getCurrentProject().getSpriteList();
			for (Sprite tmp : spriteList) {
				for (int script = 0; script < tmp.getNumberOfScripts(); script++) {
					if (tmp.getScript(script) instanceof WhenVirtualButtonScript) {
						return tmp;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	class DPadThread extends Thread {

		private boolean running = false;

		@Override
		public void run() {
			try {
				while (running) {

					Sprite sprite = ProjectManager.getInstance().getCurrentSprite();
					if (sprite == null) {
						sprite = getCurrentVirtualGamepadSprite();
						if (sprite == null) {
							running = false;
							Log.e("DPadThread<run>", "sprite is null");
							return;
						}
					} else if (sprite.isPaused) {
						running = false;
						return;
					}

					handleDPadAction(sprite);

					Thread.sleep(20);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public void start() {
			try {
				running = true;
				super.start();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public void stopThread() {
			running = false;
		}

	}

	class ButtonThread extends Thread {

		private Look look;

		public ButtonThread(Look look) {
			super();
			this.look = look;
		}

		@Override
		public void run() {
			try {
				float transparency = 0.0f;
				look.setTransparencyInUserInterfaceDimensionUnit(transparency);
				look.setVisible(true);

				while (transparency < 100.0f) {
					transparency = transparency + 10.0f;
					look.setTransparencyInUserInterfaceDimensionUnit(transparency);
					Thread.sleep(30);
				}

				look.setVisible(false);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
