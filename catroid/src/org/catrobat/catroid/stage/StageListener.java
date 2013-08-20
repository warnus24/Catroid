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

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.common.Values;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.WhenVirtualButtonScript;
import org.catrobat.catroid.content.WhenVirtualPadScript;
import org.catrobat.catroid.io.SoundManager;
import org.catrobat.catroid.ui.dialogs.StageDialog;
import org.catrobat.catroid.utils.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.os.SystemClock;
import android.util.Log;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ScreenUtils;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.common.ScreenModes;
import org.catrobat.catroid.common.ScreenValues;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.WhenVirtualButtonScript;
import org.catrobat.catroid.content.WhenVirtualPadScript;
import org.catrobat.catroid.io.SoundManager;
import org.catrobat.catroid.ui.dialogs.StageDialog;
import org.catrobat.catroid.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class StageListener implements ApplicationListener {

	private static final int AXIS_WIDTH = 4;
	private static final float DELTA_ACTIONS_DIVIDER_MAXIMUM = 50f;
	private static final int ACTIONS_COMPUTATION_TIME_MAXIMUM = 8;
	private static final boolean DEBUG = false;

	// needed for UiTests - is disabled to fix crashes with EMMA coverage
	// CHECKSTYLE DISABLE StaticVariableNameCheck FOR 1 LINES
	private static boolean DYNAMIC_SAMPLING_RATE_FOR_ACTIONS = true;

	private float deltaActionTimeDivisor = 10f;
	public static final String SCREENSHOT_AUTOMATIC_FILE_NAME = "automatic_screenshot"
			+ Constants.IMAGE_STANDARD_EXTENTION;
	public static final String SCREENSHOT_MANUAL_FILE_NAME = "manual_screenshot" + Constants.IMAGE_STANDARD_EXTENTION;
	private FPSLogger fpsLogger;

	private Stage stage;
	private boolean paused = false;
	private boolean finished = false;
	private boolean firstStart = true;
	private boolean reloadProject = false;

	private static boolean checkIfAutomaticScreenshotShouldBeTaken = true;
	private boolean makeAutomaticScreenshot = false;
	private boolean makeScreenshot = false;
	private String pathForScreenshot;
	private int screenshotWidth;
	private int screenshotHeight;
	private int screenshotX;
	private int screenshotY;
	private byte[] screenshot = null;
	// in first frame, framebuffer could be empty and screenshot
	// would be white
	private boolean skipFirstFrameForAutomaticScreenshot;

	private Project project;

	private OrthographicCamera camera;
	private SpriteBatch batch;
	private BitmapFont font;
	private Passepartout passepartout;

	private List<Sprite> sprites;

	private float virtualWidthHalf;
	private float virtualHeightHalf;
	private float virtualWidth;
	private float virtualHeight;

	private Texture axes;

	private boolean makeTestPixels = false;
	private byte[] testPixels;
	private int testX = 0;
	private int testY = 0;
	private int testWidth = 0;
	private int testHeight = 0;

	private StageDialog stageDialog;

	public int maximizeViewPortX = 0;
	public int maximizeViewPortY = 0;
	public int maximizeViewPortHeight = 0;
	public int maximizeViewPortWidth = 0;

	public boolean axesOn = false;


	private byte[] thumbnail;

	private boolean virtualGamepadSelected = false;

	private Sprite vgpPadSprite;


	StageListener() {
	}

	@Override
	public void create() {
		font = new BitmapFont();
		font.setColor(1f, 0f, 0.05f, 1f);
		font.setScale(1.2f);

		project = ProjectManager.getInstance().getCurrentProject();
		pathForScreenshot = Utils.buildProjectPath(project.getName()) + "/";

		virtualWidth = project.getXmlHeader().virtualScreenWidth;
		virtualHeight = project.getXmlHeader().virtualScreenHeight;

		virtualWidthHalf = virtualWidth / 2;
		virtualHeightHalf = virtualHeight / 2;

		stage = new Stage(virtualWidth, virtualHeight, true);
		screenMode = ScreenModes.STRETCH;


		//stage = new Stage(virtualWidth, virtualHeight, true);
		stage = new Stage(virtualWidth, virtualHeight, true) {

			private float dPadInitValue = -100000.0f;
			private float dPadStartX = -100000.0f;
			private float dPadStartY = -100000.0f;
			private double dPadMinMotion = 20.0;
			private double dPadMaxMotion = 80.0;
			private DPadThread dPadThread;

			private boolean dPadUp = false;
			private boolean dPadDown = false;
			private boolean dPadLeft = false;
			private boolean dPadRight = false;

			class DPadThread extends Thread {

				private boolean running = false;

				@Override
				public void run() {
					try {
						Log.e("DPadThread<run>", "thread started");

						while (running) {

							Sprite sprite = ProjectManager.getInstance().getCurrentSprite();
							if (sprite == null) {
								List<Sprite> spriteList = ProjectManager.getInstance().getCurrentProject()
										.getSpriteList();
								for (Sprite tmp : spriteList) {
									for (int script = 0; script < tmp.getNumberOfScripts(); script++) {
										if (tmp.getScript(script) instanceof WhenVirtualPadScript
												|| tmp.getScript(script) instanceof WhenVirtualButtonScript) {
											sprite = tmp;
											break;
										}
									}
									if (sprite != null) {
										break;
									}
								}
								if (sprite == null) {
									running = false;
									Log.e("DPadThread<run>", "sprite is null");
									return;
								}
							} else if (sprite.isPaused) {
								running = false;
								return;
							}

							boolean changeImage = true;
							if (dPadUp && dPadLeft) {
								for (LookData lookData : vgpPadSprite.getLookDataList()) {
									if (lookData.getLookName().equals(Constants.VGP_IMAGE_PAD_UPLEFT)) {
										vgpPadSprite.look.setLookData(lookData);
										break;
									}
								}
								changeImage = false;
							} else if (dPadUp && dPadRight) {
								for (LookData lookData : vgpPadSprite.getLookDataList()) {
									if (lookData.getLookName().equals(Constants.VGP_IMAGE_PAD_UPRIGHT)) {
										vgpPadSprite.look.setLookData(lookData);
										break;
									}
								}
								changeImage = false;
							} else if (dPadDown && dPadLeft) {
								for (LookData lookData : vgpPadSprite.getLookDataList()) {
									if (lookData.getLookName().equals(Constants.VGP_IMAGE_PAD_DOWNLEFT)) {
										vgpPadSprite.look.setLookData(lookData);
										break;
									}
								}
								changeImage = false;
							} else if (dPadDown && dPadRight) {
								for (LookData lookData : vgpPadSprite.getLookDataList()) {
									if (lookData.getLookName().equals(Constants.VGP_IMAGE_PAD_DOWNRIGHT)) {
										vgpPadSprite.look.setLookData(lookData);
										break;
									}
								}
								changeImage = false;
							}

							if (dPadUp) {
								if (changeImage) {
									for (LookData lookData : vgpPadSprite.getLookDataList()) {
										if (lookData.getLookName().equals(Constants.VGP_IMAGE_PAD_UP)) {
											vgpPadSprite.look.setLookData(lookData);
											break;
										}
									}
								}
								sprite.createWhenVirtualPadScriptActionSequence(Direction.UP.getId());
							}
							if (dPadDown) {
								if (changeImage) {
									for (LookData lookData : vgpPadSprite.getLookDataList()) {
										if (lookData.getLookName().equals(Constants.VGP_IMAGE_PAD_DOWN)) {
											vgpPadSprite.look.setLookData(lookData);
											break;
										}
									}
								}
								sprite.createWhenVirtualPadScriptActionSequence(Direction.DOWN.getId());
							}
							if (dPadLeft) {
								if (changeImage) {
									for (LookData lookData : vgpPadSprite.getLookDataList()) {
										if (lookData.getLookName().equals(Constants.VGP_IMAGE_PAD_LEFT)) {
											vgpPadSprite.look.setLookData(lookData);
											break;
										}
									}
								}
								sprite.createWhenVirtualPadScriptActionSequence(Direction.LEFT.getId());
							}
							if (dPadRight) {
								if (changeImage) {
									for (LookData lookData : vgpPadSprite.getLookDataList()) {
										if (lookData.getLookName().equals(Constants.VGP_IMAGE_PAD_RIGHT)) {
											vgpPadSprite.look.setLookData(lookData);
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

							Thread.sleep(20);
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
					//				Log.e("touchDown", "x=" + screenX);
					//				Log.e("touchDown", "y=" + screenY);
					//				Log.e("touchDown", "pointer=" + pointer);
					//				Log.e("touchDown", "button=" + button);

					//					if (pointer == 0) {
					dPadThread = new DPadThread();
					dPadThread.start();

					vgpPadSprite.look.setXInUserInterfaceDimensionUnit(screenX - virtualWidth / 2.0f);
					vgpPadSprite.look.setYInUserInterfaceDimensionUnit(virtualHeight / 2.0f - screenY);

					vgpPadSprite.look.setVisible(true);
					//					}
					return true;
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}
			}

			@Override
			public boolean touchDragged(int screenX, int screenY, int pointer) {
				try {
					//				Log.i("touchDragged", "x=" + screenX);
					//				Log.i("touchDragged", "y=" + screenY);
					//				Log.i("touchDragged", "pointer=" + pointer);

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
					return true;
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}
			}

			@Override
			public boolean touchUp(int screenX, int screenY, int pointer, int button) {
				try {
					//				Log.e("touchUp", "x=" + screenX);
					//				Log.e("touchUp", "y=" + screenY);
					//				Log.e("touchUp", "pointer=" + pointer);
					//				Log.e("touchUp", "button=" + button);

					//					if (pointer == 0) {
					dPadStartX = dPadInitValue;
					dPadStartY = dPadInitValue;

					dPadThread.stopThread();

					vgpPadSprite.look.setVisible(false);
					//					}
					return true;
				} catch (Exception e) {
					return false;
				}
			}

		};

		batch = stage.getSpriteBatch();

		Gdx.gl.glViewport(0, 0, ScreenValues.SCREEN_WIDTH, ScreenValues.SCREEN_HEIGHT);
		initScreenMode();


		sprites = project.getSpriteList();
		for (Sprite sprite : sprites) {
			sprite.resetSprite();
			sprite.look.createBrightnessContrastShader();
			stage.addActor(sprite.look);
			sprite.resume();
		sprites.get(0).look.setLookData(createWhiteBackgroundLookData());

		for (Sprite sprite : sprites) {
			for (int script = 0; script < sprite.getNumberOfScripts(); script++) {
				if (sprite.getScript(script) instanceof WhenVirtualPadScript
						|| sprite.getScript(script) instanceof WhenVirtualButtonScript) {
					virtualGamepadSelected = true;
					break;
				}
			}
			if (virtualGamepadSelected) {
				break;
			}
		}


		passepartout = new Passepartout(ScreenValues.SCREEN_WIDTH, ScreenValues.SCREEN_HEIGHT, maximizeViewPortWidth,
				maximizeViewPortHeight, virtualWidth, virtualHeight);
		stage.addActor(passepartout);

		if (virtualGamepadSelected) {
			stage = new VirtualGamepadStage(virtualWidth, virtualHeight, true);
		} else {
			stage = new Stage(virtualWidth, virtualHeight, true);
		}

		batch = stage.getSpriteBatch();

		camera = (OrthographicCamera) stage.getCamera();
		camera.position.set(0, 0, 0);

		for (Sprite sprite : sprites) {
			sprite.resetSprite();
			stage.addActor(sprite.look);
			sprite.resume();
		}

		if (sprites.size() > 0) {
			sprites.get(0).look.setLookData(createWhiteBackgroundLookData());
		}


		if (DEBUG) {
			OrthoCamController camController = new OrthoCamController(camera);
			InputMultiplexer multiplexer = new InputMultiplexer();
			multiplexer.addProcessor(camController);
			multiplexer.addProcessor(stage);
			Gdx.input.setInputProcessor(multiplexer);
			fpsLogger = new FPSLogger();
		} else {
			Log.i("GamepadSelected", "selected: " + virtualGamepadSelected);
			if (virtualGamepadSelected) {
				loadVirtualGamepadImagesLookData();
				((VirtualGamepadStage) stage).setVgpPadSprite(vgpPadSprite);
				Gdx.input.setInputProcessor(stage);
			} else {
				Gdx.input.setInputProcessor(stage);
			}
		}

		axes = new Texture(Gdx.files.internal("stage/red_pixel.bmp"));
		skipFirstFrameForAutomaticScreenshot = true;
		if (checkIfAutomaticScreenshotShouldBeTaken) {
			makeAutomaticScreenshot = project.manualScreenshotExists(SCREENSHOT_MANUAL_FILE_NAME);
		}

	}

	void menuResume() {
		if (reloadProject) {
			return;
		}
		paused = false;
		SoundManager.getInstance().resume();
		for (Sprite sprite : sprites) {
			sprite.resume();
		}
	}

	void menuPause() {
		if (finished || reloadProject) {
			return;
		}
		paused = true;
		SoundManager.getInstance().pause();
		for (Sprite sprite : sprites) {
			sprite.pause();
		}
	}

	public void reloadProject(Context context, StageDialog stageDialog) {
		if (reloadProject) {
			return;
		}
		this.stageDialog = stageDialog;

		project.getUserVariables().resetAllUserVariables();

		reloadProject = true;
	}

	@Override
	public void resume() {
		if (!paused) {
			SoundManager.getInstance().resume();
			for (Sprite sprite : sprites) {
				sprite.resume();
			}
		}

		for (Sprite sprite : sprites) {
			sprite.look.refreshTextures();
		}

	}

	@Override
	public void pause() {
		if (finished) {
			return;
		}
		if (!paused) {
			SoundManager.getInstance().pause();
			for (Sprite sprite : sprites) {
				sprite.pause();
			}
		}
	}

	public void finish() {
		finished = true;
		SoundManager.getInstance().clear();

		if (thumbnail != null && !makeAutomaticScreenshot) {
			saveScreenshot(thumbnail, SCREENSHOT_AUTOMATIC_FILE_NAME);


		for (int i = 0; i < sprites.size(); i++) {
			if (sprites.get(i).getName().equals(Constants.VGP_SPRITE_PAD)) {
				sprites.remove(i);
				break;
			}

		}
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(1f, 1f, 1f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		if (reloadProject) {
			int spriteSize = sprites.size();
			for (int i = 0; i < spriteSize; i++) {
				sprites.get(i).pause();
			}
			stage.clear();
			SoundManager.getInstance().clear();

			Sprite sprite;
			if (spriteSize > 0) {
				sprites.get(0).look.setLookData(createWhiteBackgroundLookData());
			}
			for (int i = 0; i < spriteSize; i++) {
				sprite = sprites.get(i);
				sprite.resetSprite();
				sprite.look.createBrightnessContrastShader();
				stage.addActor(sprite.look);
				sprite.pause();
			}
			stage.addActor(passepartout);

			paused = true;
			firstStart = true;
			reloadProject = false;
			synchronized (stageDialog) {
				stageDialog.notify();
			}
		}

		batch.setProjectionMatrix(camera.combined);

		if (firstStart) {
			int spriteSize = sprites.size();
			if (spriteSize > 0) {
				sprites.get(0).look.setLookData(createWhiteBackgroundLookData());
			}
			Sprite sprite;
			for (int i = 0; i < spriteSize; i++) {
				sprite = sprites.get(i);
				sprite.createStartScriptActionSequence();
				if (!sprite.getLookDataList().isEmpty()) {
					sprite.look.setLookData(sprite.getLookDataList().get(0));
				}
			}
			firstStart = false;
		}
		if (!paused) {
			float deltaTime = Gdx.graphics.getDeltaTime();

			/*
			 * Necessary for UiTests, when EMMA - code coverage is enabled.
			 * 
			 * Without setting DYNAMIC_SAMPLING_RATE_FOR_ACTIONS to false(via reflection), before
			 * the UiTest enters the stage, random segmentation faults(triggered by EMMA) will occur.
			 * 
			 * Can be removed, when EMMA is replaced by an other code coverage tool, or when a
			 * future EMMA - update will fix the bugs.
			 */
			if (DYNAMIC_SAMPLING_RATE_FOR_ACTIONS == false) {
				stage.act(deltaTime);
			} else {
				float optimizedDeltaTime = deltaTime / deltaActionTimeDivisor;
				long timeBeforeActionsUpdate = SystemClock.uptimeMillis();
				while (deltaTime > 0f) {
					stage.act(optimizedDeltaTime);
					deltaTime -= optimizedDeltaTime;
				}
				long executionTimeOfActionsUpdate = SystemClock.uptimeMillis() - timeBeforeActionsUpdate;
				if (executionTimeOfActionsUpdate <= ACTIONS_COMPUTATION_TIME_MAXIMUM) {
					deltaActionTimeDivisor += 1f;
					deltaActionTimeDivisor = Math.min(DELTA_ACTIONS_DIVIDER_MAXIMUM, deltaActionTimeDivisor);
				} else {
					deltaActionTimeDivisor -= 1f;
					deltaActionTimeDivisor = Math.max(1f, deltaActionTimeDivisor);
				}
			}
		}

		if (!finished) {
			stage.draw();
		}

		if (makeAutomaticScreenshot) {
			if (skipFirstFrameForAutomaticScreenshot) {
				skipFirstFrameForAutomaticScreenshot = false;
			} else {
				thumbnail = ScreenUtils.getFrameBufferPixels(screenshotX, screenshotY, screenshotWidth,
						screenshotHeight, true);
				makeAutomaticScreenshot = false;
			}
		}

		if (makeScreenshot) {
			screenshot = ScreenUtils.getFrameBufferPixels(screenshotX, screenshotY, screenshotWidth, screenshotHeight,
					true);
			makeScreenshot = false;
		}

		if (axesOn && !finished) {
			drawAxes();
		}

		if (DEBUG) {
			fpsLogger.log();
		}

		if (makeTestPixels) {
			testPixels = ScreenUtils.getFrameBufferPixels(testX, testY, testWidth, testHeight, false);
			makeTestPixels = false;
		}
	}

	private void drawAxes() {
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		batch.draw(axes, -virtualWidthHalf, -AXIS_WIDTH / 2, virtualWidth, AXIS_WIDTH);
		batch.draw(axes, -AXIS_WIDTH / 2, -virtualHeightHalf, AXIS_WIDTH, virtualHeight);

		TextBounds bounds = font.getBounds(String.valueOf((int) virtualHeightHalf));
		font.draw(batch, "-" + (int) virtualWidthHalf, -virtualWidthHalf + 3, -bounds.height / 2);
		font.draw(batch, String.valueOf((int) virtualWidthHalf), virtualWidthHalf - bounds.width, -bounds.height / 2);

		font.draw(batch, "-" + (int) virtualHeightHalf, bounds.height / 2, -virtualHeightHalf + bounds.height + 3);
		font.draw(batch, String.valueOf((int) virtualHeightHalf), bounds.height / 2, virtualHeightHalf - 3);
		font.draw(batch, "0", bounds.height / 2, -bounds.height / 2);
		batch.end();
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void dispose() {
		if (!finished) {
			this.finish();
		}
		stage.dispose();
		font.dispose();
		axes.dispose();
		disposeTextures();
	}

	public boolean makeManualScreenshot() {
		makeScreenshot = true;
		while (makeScreenshot) {
			Thread.yield();
		}
		return saveScreenshot(this.screenshot, SCREENSHOT_MANUAL_FILE_NAME);
	}

	private boolean saveScreenshot(byte[] screenshot, String fileName) {
		int length = screenshot.length;
		Bitmap fullScreenBitmap;
		Bitmap centerSquareBitmap;
		int[] colors = new int[length / 4];

		if (colors.length != screenshotWidth * screenshotHeight || colors.length == 0) {
			return false;
		}

		for (int i = 0; i < length; i += 4) {
			colors[i / 4] = android.graphics.Color.argb(255, screenshot[i + 0] & 0xFF, screenshot[i + 1] & 0xFF,
					screenshot[i + 2] & 0xFF);
		}
		fullScreenBitmap = Bitmap.createBitmap(colors, 0, screenshotWidth, screenshotWidth, screenshotHeight,
				Config.ARGB_8888);

		if (screenshotWidth < screenshotHeight) {
			int verticalMargin = (screenshotHeight - screenshotWidth) / 2;
			centerSquareBitmap = Bitmap.createBitmap(fullScreenBitmap, 0, verticalMargin, screenshotWidth,
					screenshotWidth);
		} else if (screenshotWidth > screenshotHeight) {
			int horizontalMargin = (screenshotWidth - screenshotHeight) / 2;
			centerSquareBitmap = Bitmap.createBitmap(fullScreenBitmap, horizontalMargin, 0, screenshotHeight,
					screenshotHeight);
		} else {
			centerSquareBitmap = Bitmap.createBitmap(fullScreenBitmap, 0, 0, screenshotWidth, screenshotHeight);
		}

		FileHandle image = Gdx.files.absolute(pathForScreenshot + fileName);
		OutputStream stream = image.write(false);
		try {
			new File(pathForScreenshot + Constants.NO_MEDIA_FILE).createNewFile();
			centerSquareBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
			stream.close();
		} catch (IOException e) {
			return false;
		}
		return true;
	}

	public byte[] getPixels(int x, int y, int width, int height) {
		testX = x;
		testY = y;
		testWidth = width;
		testHeight = height;
		makeTestPixels = true;
		while (makeTestPixels) {
			Thread.yield();
		}
		byte[] copyOfTestPixels = new byte[testPixels.length];
		System.arraycopy(testPixels,0,copyOfTestPixels,0,testPixels.length);
		return copyOfTestPixels;
	}

	public void toggleScreenMode() {
		switch (project.getScreenMode()) {
			case MAXIMIZE:
				project.setScreenMode(ScreenModes.STRETCH);
				break;
			case STRETCH:
				project.setScreenMode(ScreenModes.MAXIMIZE);
				break;
		}

		initScreenMode();

		if (checkIfAutomaticScreenshotShouldBeTaken) {
			makeAutomaticScreenshot = project.manualScreenshotExists(SCREENSHOT_MANUAL_FILE_NAME);
		}
	}

	private void initScreenMode() {
		switch (project.getScreenMode()) {
			case STRETCH:
				stage.setViewport(virtualWidth, virtualHeight, false);
				screenshotWidth = ScreenValues.SCREEN_WIDTH;
				screenshotHeight = ScreenValues.SCREEN_HEIGHT;
				screenshotX = 0;
				screenshotY = 0;
				break;

			case MAXIMIZE:
				stage.setViewport(virtualWidth, virtualHeight, true);
				screenshotWidth = maximizeViewPortWidth;
				screenshotHeight = maximizeViewPortHeight;
				screenshotX = maximizeViewPortX;
				screenshotY = maximizeViewPortY;
				break;

			default:
				break;

		}
		camera = (OrthographicCamera) stage.getCamera();
		camera.position.set(0, 0, 0);
		camera.update();
	}

	private LookData createWhiteBackgroundLookData() {
		LookData whiteBackground = new LookData();
		Pixmap whiteBackgroundPixmap = new Pixmap((int) virtualWidth, (int) virtualHeight, Format.RGBA8888);
		whiteBackgroundPixmap.setColor(Color.WHITE);
		whiteBackgroundPixmap.fill();
		whiteBackground.setPixmap(whiteBackgroundPixmap);
		whiteBackground.setTextureRegion();
		return whiteBackground;
	}

	private void disposeTextures() {
		List<Sprite> sprites = project.getSpriteList();
		int spriteSize = sprites.size();
		for (int i = 0; i > spriteSize; i++) {
			List<LookData> data = sprites.get(i).getLookDataList();
			int dataSize = data.size();
			for (int j = 0; j < dataSize; j++) {
				LookData lookData = data.get(j);
				lookData.getPixmap().dispose();
				lookData.getTextureRegion().getTexture().dispose();
			}
		}
	}
	public void setMakeAutomaticScreenshot(boolean makeAutomaticScreenshot) {
		this.makeAutomaticScreenshot = makeAutomaticScreenshot;
	}

	public boolean isMakeAutomaticScreenshot() {
		return this.makeAutomaticScreenshot;
	}

	private void prepareScreenshotFiles() {
		File noMediaFile = new File(pathForScreenshot + ".nomedia");
		File screenshotFile = new File(pathForScreenshot + SCREENSHOT_FILE_NAME);
		try {
			if (screenshotFile.exists()) {
				screenshotFile.delete();
				screenshotFile = new File(pathForScreenshot + SCREENSHOT_FILE_NAME);
			}
			screenshotFile.createNewFile();

			if (!noMediaFile.exists()) {
				noMediaFile.createNewFile();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void loadVirtualGamepadImagesLookData() {
		try {

			String path = Utils.buildPath(Utils.buildProjectPath(project.getName()), Constants.IMAGE_DIRECTORY);
			String[] imageName = new String[] { Constants.VGP_IMAGE_PAD_CENTER, Constants.VGP_IMAGE_PAD_UP,
					Constants.VGP_IMAGE_PAD_DOWN, Constants.VGP_IMAGE_PAD_LEFT, Constants.VGP_IMAGE_PAD_RIGHT,
					Constants.VGP_IMAGE_PAD_UPLEFT, Constants.VGP_IMAGE_PAD_UPRIGHT, Constants.VGP_IMAGE_PAD_DOWNLEFT,
					Constants.VGP_IMAGE_PAD_DOWNRIGHT };

			ArrayList<LookData> lookDataList = new ArrayList<LookData>();

			for (int i = 0; i < imageName.length; i++) {
				String filePath = Utils.buildPath(path, imageName[i]);
				File file = new File(filePath);
				if (file.exists()) {
					Log.i("StageListener<create>", "filename: " + file.getName());

					Pixmap pixmap = Utils.getPixmapFromFile(file);

					LookData dpadLookData = new LookData();
					dpadLookData.setLookName(imageName[i]);
					dpadLookData.setLookFilename(file.getName());
					dpadLookData.setPixmap(pixmap);
					dpadLookData.setTextureRegion();
					lookDataList.add(dpadLookData);
				} else {
					Log.e("StageListener<loadVirtualGamepadImagesLookData>", "File do not exist. filePath: " + filePath);
				}
			}

			vgpPadSprite = new Sprite(Constants.VGP_SPRITE_PAD);
			if (lookDataList.size() > 0) {
				vgpPadSprite.look.setLookData(lookDataList.get(0));
				vgpPadSprite.setLookDataList(lookDataList);
			}
			vgpPadSprite.look.setVisible(false);

			sprites.add(vgpPadSprite);
			stage.addActor(vgpPadSprite.look);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
