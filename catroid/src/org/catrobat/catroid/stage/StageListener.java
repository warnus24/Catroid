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

import android.content.Context;
import android.graphics.Color;
import android.os.SystemClock;
import android.util.Log;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ScreenUtils;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.common.ScreenValues;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.io.SoundManager;

import java.util.List;

public class StageListener implements ApplicationListener {

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

	private Project project;

	private OrthographicCamera camera;
	private SpriteBatch batch;
	private BitmapFont font;

	private List<Sprite> sprites;

	private float virtualWidth;
	private float virtualHeight;

	private int screenWidth;
	private int screenHeight;

	private Thread postExecThread = null;

	private enum ScreenModes {
		STRETCH, MAXIMIZE
	};

	private ScreenModes screenMode;

	private boolean makeTestPixels = false;
	private byte[] testPixels;
	private int testX = 0;
	private int testY = 0;
	private int testWidth = 0;
	private int testHeight = 0;

	public int maximizeViewPortX = 0;
	public int maximizeViewPortY = 0;
	public int maximizeViewPortHeight = 0;
	public int maximizeViewPortWidth = 0;

	private boolean isLiveWallpaper = false;

	public StageListener() {

	}

	public StageListener(boolean isLiveWallpaper) {
		this.isLiveWallpaper = isLiveWallpaper;
		screenWidth = ScreenValues.SCREEN_WIDTH;
		screenHeight = ScreenValues.SCREEN_HEIGHT;
	}

	@Override
	public void create() {
		font = new BitmapFont();
		font.setColor(1f, 0f, 0.05f, 1f);
		font.setScale(1.2f);

		project = ProjectManager.getInstance().getCurrentProject();

		virtualWidth = project.getXmlHeader().virtualScreenWidth;
		virtualHeight = project.getXmlHeader().virtualScreenHeight;

		screenMode = ScreenModes.STRETCH;

		stage = new Stage(virtualWidth, virtualHeight, true);
		batch = stage.getSpriteBatch();

		camera = (OrthographicCamera) stage.getCamera();
		camera.position.set(0, 0, 0);

		sprites = project.getSpriteList();

		if (!finished) {
			for (Sprite sprite : sprites) {
				sprite.resetSprite();
				sprite.look.createBrightnessContrastShader();
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
				Gdx.input.setInputProcessor(stage);
			}
		}
	}

	public void menuResume() {
		if (reloadProject || !paused || finished) {
			return;
		}
		paused = false;
		SoundManager.getInstance().resume();
		for (Sprite sprite : sprites) {
			sprite.resume();
		}
	}

	public void menuPause() {
		if (finished || reloadProject || (sprites == null)) {
			return;
		}
		paused = true;
		SoundManager.getInstance().pause();
		for (Sprite sprite : sprites) {
			sprite.pause();
		}
	}

	public void reloadProject(Context context, Thread postExec) {
		if (reloadProject) {
			return;
		}
		postExecThread = postExec;
		project.getUserVariables().resetAllUserVariables();

		if (this.isLiveWallpaper) {
			create();
		}

		reloadProject = true;
	}

	@Override
	public void resume() {
		if (finished) {
			return;
		}
		if (!paused) {
			SoundManager.getInstance().resume();
			for (Sprite sprite : sprites) {
				Log.d("LWP", "Resuming sprite " + sprite.getName());
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

			paused = true;
			firstStart = true;
			reloadProject = false;
		}

		switch (screenMode) {
			case MAXIMIZE:
				Gdx.gl.glViewport(maximizeViewPortX, maximizeViewPortY, maximizeViewPortWidth, maximizeViewPortHeight);
				break;
			case STRETCH:
			default:
				Gdx.gl.glViewport(0, 0, screenWidth, screenHeight);
				break;
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

		if (!paused && !finished) {
			try {
				stage.draw();
			} catch (IllegalStateException e) {
				Log.d("LWP", "IllegalStateException caught while rendering the stage listner " + hashCode());
				return;
			}

		}

		if (DEBUG) {
			fpsLogger.log();
		}

		if (makeTestPixels) {
			testPixels = ScreenUtils.getFrameBufferPixels(testX, testY, testWidth, testHeight, false);
			makeTestPixels = false;
		}
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void dispose() {
		if (!finished) {
			this.finish();
		}
		if (stage != null) {
			stage.dispose();
		}
		if (font != null) {
			font.dispose();
		}

		disposeTextures();
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
		return testPixels;
	}

	public void changeScreenSize() {
		switch (screenMode) {
			case MAXIMIZE:
				screenMode = ScreenModes.STRETCH;
				break;
			case STRETCH:
				screenMode = ScreenModes.MAXIMIZE;
				break;
		}
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

	public boolean isFinished() {
		return finished;
	}
}
