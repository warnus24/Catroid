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
package org.catrobat.catroid.livewallpaper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.WindowManager;
import android.widget.Toast;

import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.backends.android.AndroidLiveWallpaperService;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.ScreenValues;
import org.catrobat.catroid.formulaeditor.SensorHandler;
import org.catrobat.catroid.stage.StageListenerLWP;

@SuppressLint("NewApi")
//eventuell unnötig 10 intern 15 vorraussetzen Fehlerfall abfangen API Level vorraussetzen  prüfen mit 10
public class LiveWallpaper extends AndroidLiveWallpaperService {

	private static LiveWallpaper INSTANCE;

	private AndroidApplicationConfiguration cfg;
	private Context context;
	private boolean previewEnginePaused;
	private boolean homeEnginePaused;

	private LiveWallpaperEngine previewEngine;

	/**
	 * @return the previewEngine
	 */
	public LiveWallpaperEngine getPreviewEngine() {
		return previewEngine;
	}

	/**
	 * @return the homeEngine
	 */
	public LiveWallpaperEngine getHomeEngine() {
		return homeEngine;
	}

	/**
	 * @param previewEngine
	 *            the previewEngine to set
	 */
	public void setPreviewEngine(LiveWallpaperEngine previewEngine) {
		this.previewEngine = previewEngine;
	}

	/**
	 * @param homeEngine
	 *            the homeEngine to set
	 */
	public void setHomeEngine(LiveWallpaperEngine homeEngine) {
		this.homeEngine = homeEngine;
	}

	private LiveWallpaperEngine homeEngine;
	private StageListenerLWP stageListener = null;

	public boolean TEST = false;

	@Override
	public void onCreate() {
		//android.os.Debug.waitForDebugger();
		INSTANCE = this;
		if (!TEST) {
			super.onCreate();
			SharedPreferences sharedPreferences = PreferenceManager
					.getDefaultSharedPreferences(getApplicationContext());
			//SoundManager.getInstance().soundDisabledByLwp = sharedPreferences.getBoolean(Constants.PREF_SOUND_DISABLED,
			//		false);
			context = this;
		}
	}

	@Override
	public void onCreateApplication() {
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		config.getTouchEventsForLiveWallpaper = true;

		setScreenSize(false);
		loadProject(true);
		ProjectManager.changeState(ProjectManagerState.PREVIEW);
		stageListener = new StageListenerLWP();
		initialize(stageListener, config);
		Log.d("LWP", "Preview was initialized");
	}

	public Context getContext() {
		return context;
	}

	public static LiveWallpaper getInstance() {

		return INSTANCE;
	}

	@Override
	public void onDestroy() {
		ProjectManager.changeState(ProjectManagerState.NORMAL);
		super.onDestroy();
		//PreStageActivity.shutDownTextToSpeechForLiveWallpaper();
	}

	/*
	 * @Override
	 * public ApplicationListener createListener() {
	 * setScreenSize(isPreview);
	 * 
	 * if (isPreview) {
	 * if (previewEngine != null) {
	 * previewEngine.resuming();
	 * }
	 * previewStageListener = new StageListenerLWP();
	 * previewEngine = lastCreatedPreviewEngine;
	 * previewEngine.name = "Preview";
	 * Log.d("LWP", "Created " + "new prev Listener");
	 * 
	 * return previewStageListener;
	 * } else {
	 * if (homeEngine != null) {
	 * homeEngine.resuming();
	 * }
	 * homeScreenStageListener = new StageListenerLWP();
	 * homeEngine = lastCreatedHomeEngine;
	 * homeEngine.name = "Home";
	 * Log.d("LWP", "Created " + "new home Listener");
	 * 
	 * return homeScreenStageListener;
	 * }
	 * }
	 */

	public void changeWallpaperProgram() {
		if (previewEngine != null) {
			previewEngine.changeWallpaperProgram();
		}

		if (homeEngine != null) {
			homeEngine.changeWallpaperProgram();
		}
	}

	/*
	 * @Override
	 * public AndroidApplicationConfiguration createConfig() {
	 * if (cfg == null) {
	 * cfg = new AndroidApplicationConfiguration();
	 * cfg.useGL20 = true;
	 * }
	 * return cfg;
	 * }
	 */

	public void loadProject(boolean isPreview) {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		String projectName = "";
		boolean loadable = false;
		ProjectManager projectManagerPreview = ProjectManager.getInstance(ProjectManagerState.PREVIEW);
		ProjectManager projectManagerHome = ProjectManager.getInstance(ProjectManagerState.HOME);
		//ProjectManager projectManager = ProjectManager.getInstance();

		if (isPreview) {
			projectName = sharedPreferences.getString(Constants.PREF_LWP_PREVIEW_PROJECTNAME_KEY, null);
			if (projectName == null) {
				projectName = sharedPreferences.getString(Constants.PREF_PROJECTNAME_KEY, null);
			}
			if (projectManagerPreview.getCurrentProject() != null
					&& projectManagerPreview.getCurrentProject().getName().equals(projectName)) {
				return;
			}
			loadable = projectManagerPreview.loadProject(projectName, context, true);
			loadable = projectManagerHome.loadProject(projectName, context, true);
			//projectManager.loadProject(projectName, context, true);

		} else {
			projectName = sharedPreferences.getString(Constants.PREF_LWP_HOME_PROJECTNAME_KEY, null);
			if (projectName == null) {
				projectName = sharedPreferences.getString(Constants.PREF_PROJECTNAME_KEY, null);
			}
			if (projectManagerHome.getCurrentProject() != null
					&& projectManagerHome.getCurrentProject().getName().equals(projectName)) {
				return;
			}
			Log.d("LWP", "Project " + projectName + " wurde für die Home Engine geladen!!!!");
			loadable = projectManagerHome.loadProject(projectName, context, true);
		}

		String result = "";

		if (!loadable) {
			result = ProjectLoadableEnum.IS_NOT_LOADABLE.toString();
			Toast toast = Toast.makeText(context, result, 10000);
			toast.show();
			Log.d("LWP", "Project is not loadable");
			return;
		}

		result = ProjectLoadableEnum.IS_LOADABLE.toString();
		Toast toast = Toast.makeText(context, result, 10000);
		toast.show();
		Log.d("LWP", "Project is loadable");

		//SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
		if (isPreview) {
			Editor editor = sharedPreferences.edit();
			editor.putString(Constants.PREF_LWP_PREVIEW_PROJECTNAME_KEY, projectName);
			editor.commit();

			editor = sharedPreferences.edit();
			editor.putString(Constants.PREF_LWP_HOME_PROJECTNAME_KEY, projectName);
			editor.commit();
		} else {
			Editor editor = sharedPreferences.edit();
			editor.putString(Constants.PREF_LWP_HOME_PROJECTNAME_KEY, projectName);
			editor.commit();
		}

	}

	@Override
	public Engine onCreateEngine() {

		Log.d("LWP", "Eine neue Engine wurde erstellt!!!");
		return new LiveWallpaperEngine();
	}

	private void setScreenSize(boolean isPreview) {
		DisplayMetrics displayMetrics = new DisplayMetrics();
		int currentApiVersion = android.os.Build.VERSION.SDK_INT;
		if (!isPreview && currentApiVersion >= 19) {
			((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay().getRealMetrics(displayMetrics);
		} else {
			((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay().getMetrics(displayMetrics);
		}
		ScreenValues.SCREEN_WIDTH = displayMetrics.widthPixels;
		ScreenValues.SCREEN_HEIGHT = displayMetrics.heightPixels;
	}

	public class LiveWallpaperEngine extends AndroidWallpaperEngine {

		public String name = "";
		private final int REFRESH_RATE = 300;
		private boolean mVisible = false;
		private final Handler mHandler = new Handler();
		private final Runnable mUpdateDisplay = new Runnable() {
			@Override
			public void run() {
				if (mVisible) {
					mHandler.postDelayed(mUpdateDisplay, REFRESH_RATE);
				}
			}
		};

		private boolean change = false;

		public LiveWallpaperEngine() {
			super();
			//activateTextToSpeechIfNeeded();
			SensorHandler.startSensorListener(getApplicationContext());
		}

		@Override
		public void onSurfaceCreated(final SurfaceHolder holder) {
			Log.d("LWP", "00000000000000000000000000000000000000000000000000000000000000000000000000000000000000");

			if (!isPreview() && homeEngine != null && previewEngine != null) {
				Log.d("LWP", "111111111111111111111111111111111111111111111111111111111111111111111111111111");
				//previewEngine.pausing();
				//homeEngine.resuming();

			} else if (!isPreview() && previewEngine != null) {
				Log.d("LWP", "2222222222222222222222222222222222222222222222222222222222222222222222222222222");
				//previewEngine.pausing();
			} else if (isPreview() && previewEngine != null && homeEngine != null) {
				Log.d("LWP", "33333333333333333333333333333333333333333333333333333333333333333333333333333333");
				//homeEngine.pausing();
				//previewEngine.resuming();
			} else if (isPreview() && previewEngine != null) {
				Log.d("LWP", "44444444444444444444444444444444444444444444444444444444444444444444444444444444");
				//previewEngine.resuming();
			}

			if (isPreview()) {
				setPreviewEngine(this);
			} else {
				setHomeEngine(this);
			}

			super.onSurfaceCreated(holder);
		}

		private StageListenerLWP getLocalStageListener() {
			return stageListener;
		}

		@Override
		public void onVisibilityChanged(boolean visible) {
			Log.d("LWP", "Engine: " + name + " the engine is visible: " + visible);

			if (!isPreview() && visible) {
				//previewEngine.pausing();
				//homeEngine.resuming();
				ProjectManager.changeState(ProjectManagerState.HOME);
				Log.d("LWP", "Home visible true");
			}

			if (isPreview() && visible) {
				//ProjectManager.changeState(ProjectManagerState.PREVIEW);
				//homeEngine.pausing();
				//previewEngine.resuming();
				ProjectManager.changeState(ProjectManagerState.PREVIEW);
				Log.d("LWP", "Preview visible true");
			}

			mVisible = visible;
			super.onVisibilityChanged(visible);

			changeVisibilityStates(visible);

			//if (previewEnginePaused && homeEnginePaused) {
			//	ProjectManager.changeState(ProjectManagerState.NORMAL);
			//}

		}

		private void changeVisibilityStates(boolean visible) {
			if (isPreview() && visible) {
				previewEnginePaused = false;
			}
			if (isPreview() && !visible) {
				previewEnginePaused = true;
			}

			if (!isPreview() && visible) {
				homeEnginePaused = false;
			}
			if (!isPreview() && !visible) {
				homeEnginePaused = true;
			}
		}

		public void resuming() {

			onResume();
		}

		@Override
		public void onResume() {
			if (getLocalStageListener() == null) {
				return;
			}

			if (isPreview()) {
				ProjectManager.changeState(ProjectManagerState.PREVIEW);
				//previewEngine.setTouchEventsEnabled(true);
				Log.d("LWP", "Resuming from Preview" + name + ": " + " SL-" + getLocalStageListener().hashCode());
			} else {
				ProjectManager.changeState(ProjectManagerState.HOME);
				//homeEngine.setTouchEventsEnabled(true);
				Log.d("LWP", "Resuming from Home" + name + ": " + " SL-" + getLocalStageListener().hashCode());
			}

			mHandler.postDelayed(mUpdateDisplay, REFRESH_RATE);
			SensorHandler.startSensorListener(getApplicationContext());
			//mHandler.postDelayed(mUpdateDisplay, REFRESH_RATE);

			super.onResume();
		}

		public void pausing() {

			onPause();
		}

		@Override
		public void onPause() {
			if (getLocalStageListener() == null) {
				return;
			}

			//mHandler.removeCallbacks(mUpdateDisplay);
			SensorHandler.stopSensorListeners();
			super.onPause();
			Log.d("LWP", "Pausing " + name + ": " + " SL-" + getLocalStageListener().hashCode());

		}

		@Override
		public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
			super.onSurfaceChanged(holder, format, width, height);

			if (width > height) {
				Toast.makeText(context, context.getResources().getString(R.string.lwp_no_landscape_support),
						Toast.LENGTH_SHORT).show();

				Log.d("LWP", "Surface changed and finished!!!");
				getLocalStageListener().finish();
				mHandler.removeCallbacks(mUpdateDisplay);
				return;
			}

			if (mVisible) {
				mHandler.postDelayed(mUpdateDisplay, REFRESH_RATE);
			} else {
				mHandler.removeCallbacks(mUpdateDisplay);
			}
		}

		@Override
		public void onSurfaceDestroyed(SurfaceHolder holder) {
			Log.d("LWP", "destroying surface: " + name);
			mVisible = false;
			mHandler.removeCallbacks(mUpdateDisplay);
			super.onSurfaceDestroyed(holder);
		}

		@Override
		public void onDestroy() {
			mVisible = false;
			Log.d("LWP", "destroying engine: " + name);
			mHandler.removeCallbacks(mUpdateDisplay);
			super.onDestroy();
		}

		public void changeWallpaperProgram() {

			if (getLocalStageListener() == null) {
				return;
			}

			getLocalStageListener().create();
			getLocalStageListener().reloadProject(null);

			if (isPreview()) {
				previewEngine.resuming();
			} else {
				homeEngine.resuming();
			}

			//synchronized (this) {
			//	try {
			//		this.wait();
			//	} catch (InterruptedException e) {
			//		Log.e("CATROID", "Thread activated too early!", e);
			//	}
			//}

			//activateTextToSpeechIfNeeded();

		}

		private void activateTextToSpeechIfNeeded() {
			//			if (PreStageActivity.initTextToSpeechForLiveWallpaper(context) != 0) {
			//				Intent installIntent = new Intent();
			//				installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
			//				startActivity(installIntent);
			//			}
		}
	}

	/**
	 * 
	 */
	public void presetSprites() {
		//stageListener.resetSprites();
	}
}
