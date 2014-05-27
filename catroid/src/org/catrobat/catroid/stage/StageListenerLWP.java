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

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.os.SystemClock;
import android.util.Log;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.backends.android.AndroidWallpaperListener;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.StretchViewport;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.common.ScreenModes;
import org.catrobat.catroid.common.ScreenValues;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.io.SoundManager;
import org.catrobat.catroid.livewallpaper.LiveWallpaper.LiveWallpaperEngine;
import org.catrobat.catroid.livewallpaper.ProjectManagerState;
import org.catrobat.catroid.ui.dialogs.StageDialog;
import org.catrobat.catroid.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class StageListenerLWP implements ApplicationListener, AndroidWallpaperListener {

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
	private LiveWallpaperEngine lwpEngine = null;
	// in first frame, framebuffer could be empty and screenshot
	// would be white
	private boolean skipFirstFrameForAutomaticScreenshot;

	private Project project;

	private OrthographicCamera camera;
	private Batch batch;
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

	private boolean isPreview = true;

	@Override
	public void create() {
		font = new BitmapFont();
		font.setColor(1f, 0f, 0.05f, 1f);
		font.setScale(1.2f);

		project = ProjectManager.getInstance(ProjectManagerState.LWP).getCurrentProject();

		pathForScreenshot = Utils.buildProjectPath(project.getName()) + "/";

		virtualWidth = project.getXmlHeader().virtualScreenWidth;
		virtualHeight = project.getXmlHeader().virtualScreenHeight;

		virtualWidthHalf = virtualWidth / 2;
		virtualHeightHalf = virtualHeight / 2;

		stage = new Stage(new StretchViewport(virtualWidth, virtualHeight));

		stage.getViewport().setWorldSize(ScreenValues.SCREEN_WIDTH, ScreenValues.SCREEN_HEIGHT);
		batch = stage.getBatch();

		Gdx.gl.glViewport(0, 0, ScreenValues.SCREEN_WIDTH, ScreenValues.SCREEN_HEIGHT);
		initScreenMode();

		sprites = project.getSpriteList();
		for (Sprite sprite : sprites) {
			sprite.resetSprite();
			sprite.look.createBrightnessContrastShader();
			stage.addActor(sprite.look);
			sprite.resume();
		}

		passepartout = new Passepartout(ScreenValues.SCREEN_WIDTH, ScreenValues.SCREEN_HEIGHT, maximizeViewPortWidth,
				maximizeViewPortHeight, virtualWidth, virtualHeight);
		stage.addActor(passepartout);

		if (DEBUG) {
			OrthoCamController camController = new OrthoCamController(camera);
			InputMultiplexer multiplexer = new InputMultiplexer();
			multiplexer.addProcessor(camController);
			multiplexer.addProcessor(stage);
			Gdx.input.setInputProcessor(multiplexer);
			fpsLogger = new FPSLogger();
		} else {
			Gdx.input.setInputProcessor(stage);
		}

		axes = new Texture(Gdx.files.internal("stage/red_pixel.bmp"));
		skipFirstFrameForAutomaticScreenshot = true;
		if (checkIfAutomaticScreenshotShouldBeTaken) {
			makeAutomaticScreenshot = project.manualScreenshotExists(SCREENSHOT_MANUAL_FILE_NAME);
		}
		Log.d("LWP", "StageListener created!!!!!");
	}

	public void menuResume() {
		Log.d("LWP", "StageListener menuResume() ANFANG");
		if (reloadProject) {
			return;
		}
		Log.d("LWP", "StageListener menuResume() 2");
		paused = false;
		Log.d("LWP", "StageListener menuResume() 3");
		SoundManager.getInstance().resume();
		Log.d("LWP", "StageListener menuResume() 4");
		for (Sprite sprite : sprites) {
			sprite.resume();
		}
		Log.d("LWP", "StageListener menuResume() 5");

		Log.d("LWP", "StageListener menuResume() ENDE");
	}

	public void menuPause() {
		if (finished || reloadProject) {
			return;
		}
		paused = true;
		SoundManager.getInstance().pause();
		for (Sprite sprite : sprites) {
			sprite.pause();
		}

		Log.d("LWP", "StageListener menuPause()!!!!!");
	}

	public void reloadProject(LiveWallpaperEngine engine) {
		this.lwpEngine = engine;
		if (reloadProject) {
			return;
		}

		project.getUserVariables().resetAllUserVariables();

		reloadProject = true;
		//this.firstStart = true;
		Log.d("LWP", "StageListener reloadProject!!!!!");
	}

	@Override
	public void resume() {
		if (!paused) {
			SoundManager.getInstance().resume();
			for (Sprite sprite : sprites) {
				sprite.resume();
			}

			Log.d("LWP", "StageListener WIRKLICH resume()!!!!!");
		}

		for (Sprite sprite : sprites) {
			sprite.look.refreshTextures();
		}

		Log.d("LWP", "StageListener resume()!!!!!");

	}

	@Override
	public void pause() {
		Log.d("LWP", "StageListener pause() anfang Methode!!!!!");
		if (finished) {
			return;
		}
		if (!paused) {
			SoundManager.getInstance().pause();
			for (Sprite sprite : sprites) {
				sprite.pause();
			}
		}

		Log.d("LWP", "StageListener pause() ende Methode!!!!!");
	}

	public void finish() {
		finished = true;
		SoundManager.getInstance().clear();
		if (thumbnail != null && !makeAutomaticScreenshot) {
			saveScreenshot(thumbnail, SCREENSHOT_AUTOMATIC_FILE_NAME);
		}

		Log.d("LWP", "StageListener finish()!!!!!");
	}

	/**
	 * 
	 */
	public void resetSprites() {
		for (Sprite sprite : sprites) {
			sprite.resetSprite();
			sprite.look.createBrightnessContrastShader();
			stage.addActor(sprite.look);
			sprite.pause();
		}

		Log.d("LWP", "StageListener resetSprites()!!!!!");
	}

	@Override
	public void render() {
		//Log.d("LWP", "StageListener render!!!!!");
		Gdx.gl.glClearColor(1f, 1f, 1f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		if (reloadProject) {
			Log.d("LWP", "StageListener render  reloadProject 1!!!!!");
			int spriteSize = sprites.size();
			for (int i = 0; i < spriteSize; i++) {
				sprites.get(i).pause();
			}
			stage.clear();
			SoundManager.getInstance().clear();
			Log.d("LWP", "StageListener render  reloadProject 2!!!!!");
			Sprite sprite;
			if (spriteSize > 0) {
				sprites.get(0).look.setLookData(createWhiteBackgroundLookData());
			}
			Log.d("LWP", "StageListener render  reloadProject 3!!!!!");
			for (int i = 0; i < spriteSize; i++) {
				sprite = sprites.get(i);
				sprite.resetSprite();
				sprite.look.createBrightnessContrastShader();
				stage.addActor(sprite.look);
				sprite.pause();
			}
			Log.d("LWP", "StageListener render  reloadProject 4!!!!!");
			stage.addActor(passepartout);
			Log.d("LWP", "StageListener render  reloadProject 5!!!!!");
			paused = true;
			firstStart = true;
			reloadProject = false;

			if (lwpEngine != null) {
				synchronized (lwpEngine) {
					Log.d("LWP", "StageListener render vor notify()!!!!!");
					Log.d("LWP", "StageListener Listener isPreview (" + isPreview + ")");
					Log.d("LWP", "StageListener, render Engine: " + lwpEngine.name);
					lwpEngine.notifyAll();
					Log.d("LWP", "StageListener render nach notify()!!!!!");
				}
			}
		}

		//Log.d("LWP", "StageListener render 1");
		batch.setProjectionMatrix(camera.combined);
		//Log.d("LWP", "StageListener render 2");
		if (firstStart) {
			int spriteSize = sprites.size();
			if (spriteSize > 0) {
				sprites.get(0).look.setLookData(createWhiteBackgroundLookData());
			}
			Sprite sprite;
			for (int i = 0; i < spriteSize; i++) {
				//Log.d("LWP", "StageListener render 3 Sprite-Size: " + spriteSize);
				sprite = sprites.get(i);
				sprite.createStartScriptActionSequence();
				if (!sprite.getLookDataList().isEmpty()) {
					sprite.look.setLookData(sprite.getLookDataList().get(0));
				}
			}
			firstStart = false;
			//Log.d("LWP", "StageListener render 3.1");
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
				//Log.d("LWP", "StageListener render 3.5");
				stage.act(deltaTime);
			} else {
				float optimizedDeltaTime = deltaTime / deltaActionTimeDivisor;
				long timeBeforeActionsUpdate = SystemClock.uptimeMillis();
				while (deltaTime > 0f) {
					//Log.d("LWP", "StageListener render 4");
					stage.act(optimizedDeltaTime);
					deltaTime -= optimizedDeltaTime;
					//Log.d("LWP", "StageListener render 5");
				}
				//Log.d("LWP", "StageListener render 6");
				long executionTimeOfActionsUpdate = SystemClock.uptimeMillis() - timeBeforeActionsUpdate;
				//Log.d("LWP", "StageListener render 7");
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
			//Log.d("LWP", "StageListener render 8");
			stage.draw();
			//Log.d("LWP", "StageListener render 9");
		}

		if (makeAutomaticScreenshot) {
			if (skipFirstFrameForAutomaticScreenshot) {
				skipFirstFrameForAutomaticScreenshot = false;
			} else {
				//Log.d("LWP", "StageListener render 10");
				thumbnail = ScreenUtils.getFrameBufferPixels(screenshotX, screenshotY, screenshotWidth,
						screenshotHeight, true);
				//Log.d("LWP", "StageListener render 11");
				makeAutomaticScreenshot = false;
			}
		}

		if (makeScreenshot) {
			//Log.d("LWP", "StageListener render 12");
			screenshot = ScreenUtils.getFrameBufferPixels(screenshotX, screenshotY, screenshotWidth, screenshotHeight,
					true);
			//Log.d("LWP", "StageListener render 13");
			makeScreenshot = false;
		}

		if (axesOn && !finished) {
			//Log.d("LWP", "StageListener render 14");
			drawAxes();
			//Log.d("LWP", "StageListener render 15");
		}

		if (DEBUG) {
			//Log.d("LWP", "StageListener render 16");
			fpsLogger.log();
			//Log.d("LWP", "StageListener render 17");
		}

		if (makeTestPixels) {
			//Log.d("LWP", "StageListener render 18");
			testPixels = ScreenUtils.getFrameBufferPixels(testX, testY, testWidth, testHeight, false);
			makeTestPixels = false;
			//Log.d("LWP", "StageListener render 19");
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

		Log.d("LWP", "StageListener disposed!!!!!");
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
		System.arraycopy(testPixels, 0, copyOfTestPixels, 0, testPixels.length);
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
				stage.getViewport().setWorldSize(virtualWidth, virtualHeight);
				screenshotWidth = ScreenValues.SCREEN_WIDTH;
				screenshotHeight = ScreenValues.SCREEN_HEIGHT;
				screenshotX = 0;
				screenshotY = 0;
				break;

			case MAXIMIZE:
				stage.getViewport().setWorldSize(virtualWidth, virtualHeight);
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.badlogic.gdx.backends.android.AndroidWallpaperListener#offsetChange(float, float, float, float, int,
	 * int)
	 */
	@Override
	public void offsetChange(float xOffset, float yOffset, float xOffsetStep, float yOffsetStep, int xPixelOffset,
			int yPixelOffset) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.badlogic.gdx.backends.android.AndroidWallpaperListener#previewStateChange(boolean)
	 */
	@Override
	public void previewStateChange(boolean isPreview) {
		this.isPreview = isPreview;

		Log.d("LWP", "StageListener previewState changed(" + isPreview + ")");
	}
}
