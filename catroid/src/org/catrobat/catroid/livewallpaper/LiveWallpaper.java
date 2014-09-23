
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
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Binder;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.WindowManager;
import android.widget.Toast;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.backends.android.AndroidLiveWallpaperService;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.ScreenValues;
import org.catrobat.catroid.exceptions.CompatibilityProjectException;
import org.catrobat.catroid.exceptions.LoadingProjectException;
import org.catrobat.catroid.exceptions.OutdatedVersionProjectException;
import org.catrobat.catroid.stage.PreStageActivity;
import org.catrobat.catroid.stage.StageListener;
import org.catrobat.catroid.utils.Utils;

@SuppressLint("NewApi")
//eventuell unnötig 10 intern 15 vorraussetzen Fehlerfall abfangen API Level vorraussetzen  prüfen mit 10
public class LiveWallpaper extends AndroidLiveWallpaperService {

	private AndroidApplicationConfiguration cfg;
	private Context context;
	private boolean previewEnginePaused;
	private boolean homeEnginePaused;
	private String oldProjectName;

	private LiveWallpaperEngine previewEngine;
	private LiveWallpaperEngine homeEngine;

	private ApplicationListener stageListener = null;

	public LiveWallpaper() {
		super();
		if(INSTANCE == null)
		{
			INSTANCE = this;
		}

		Log.e("Error", "new LiveWallpaper");
	}

	private static LiveWallpaper INSTANCE = null;

	public static synchronized LiveWallpaper getInstance() {
		return INSTANCE;
	}

	public void saveOldProjectName(String name) {
		this.oldProjectName = name;
	}

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
		this.previewEngine.name = "PREVIEW";
	}

	/**
	 * @param homeEngine
	 *            the homeEngine to set
	 */
	public void setHomeEngine(LiveWallpaperEngine homeEngine) {
		this.homeEngine = homeEngine;
		this.homeEngine.name = "HOME";
	}

	@Override
	public void onCreate() {
		//android.os.Debug.waitForDebugger();
			super.onCreate();
			SharedPreferences sharedPreferences = PreferenceManager
					.getDefaultSharedPreferences(getApplicationContext());
			//SoundManager.getInstance().soundDisabledByLwp = sharedPreferences.getBoolean(Constants.PREF_SOUND_DISABLED,
			//		false);
			context = this;

		oldProjectName = sharedPreferences.getString(Constants.PREF_PROJECTNAME_KEY, null);
		Log.d("LWP", "Neuer Service wurde geladen");
	}

	@Override
	public void onCreateApplication() {
		super.onCreateApplication();
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		//config.getTouchEventsForLiveWallpaper = true;

		setScreenSize(false);
		ProjectManager.changeState(ProjectManagerState.LWP);
		loadProject();

		stageListener = new StageListener(true);
		initialize(stageListener, config);
		Log.d("LWP", "Preview was initialized");
	}

	public Context getContext() {
		return context;
	}

	@Override
	public void onDestroy() {
		Log.d("LWP", "Service wird beendet");
		ProjectManager.changeState(ProjectManagerState.NORMAL);
		Utils.saveToPreferences(context, Constants.PREF_PROJECTNAME_KEY, oldProjectName);
		INSTANCE = null;
		super.onDestroy();
		PreStageActivity.shutDownTextToSpeechForLiveWallpaper();
	}

	public void changeWallpaperProgram() {
		if (previewEngine != null) {
			previewEngine.changeWallpaperProgram();
		}
	}

	public void loadProject() {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		String projectName = "";
		boolean loadable = true;
		ProjectManager projectManagerLWP = ProjectManager.getInstance(ProjectManagerState.LWP);

		projectName = sharedPreferences.getString(Constants.PREF_LWP_PROJECTNAME_KEY, null);
		if (projectName == null) {
			projectName = sharedPreferences.getString(Constants.PREF_PROJECTNAME_KEY, null);
		}

		if (projectManagerLWP.getCurrentProject() != null
				&& projectManagerLWP.getCurrentProject().getName().equals(projectName)) {
			return;
		}

		String result = "";

		try {
			projectManagerLWP.loadProject(projectName, context);
		} catch (LoadingProjectException e) {
			loadable = false;
			e.printStackTrace();
		} catch (OutdatedVersionProjectException e) {
			loadable = false;
			e.printStackTrace();
		} catch (CompatibilityProjectException e) {
			loadable = false;
			e.printStackTrace();
		}

		if(!loadable)
		{
			result = ProjectLoadableEnum.IS_NOT_LOADABLE.toString();
			Toast toast = Toast.makeText(context, result, Toast.LENGTH_LONG);
			toast.show();
			Log.d("LWP", "Project is not loadable");
			return;
		}

		result = ProjectLoadableEnum.IS_LOADABLE.toString();
		Toast toast = Toast.makeText(context, result, Toast.LENGTH_LONG);
		toast.show();
		Log.d("LWP", "Project is loadable");

		Editor editor = sharedPreferences.edit();
		editor.putString(Constants.PREF_LWP_PROJECTNAME_KEY, projectName);
		editor.commit();
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

	public void tinting(int c) {
		if (previewEngine != null) {
			previewEngine.tinting(c);
		}
		if (homeEngine != null) {
			homeEngine.tinting(c);
		}
	}

	public void disableTinting() {
		if (previewEngine != null) {
			previewEngine.setTinting(false);
		}
		if (homeEngine != null) {
			homeEngine.setTinting(false);
		}
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
//			activateTextToSpeechIfNeeded();
			//SensorHandler.startSensorListener(getApplicationContext());
		}

		@Override
		public void onSurfaceCreated(final SurfaceHolder holder) {
			if (!isPreview() && homeEngine != null && previewEngine != null) {
				Log.d("LWP", "Home Engine erstellt (nicht zum ersten Mal)");
			} else if (!isPreview() && previewEngine != null) {
				Log.d("LWP", "Home Engine erstellt (zum ersten Mal)");
			} else if (isPreview() && previewEngine != null && homeEngine != null) {
				Log.d("LWP", "Preview Engine erstellt (nicht zum ersten Mal)");
			} else if (isPreview() && previewEngine != null && homeEngine == null) {
				Log.d("LWP", "Preview Engine erstellt (nicht zum ersten Mal) und Home Engine wurde nie erstellt");
			}

			if (isPreview()) {
				setPreviewEngine(this);
			} else {
				setHomeEngine(this);
			}

			super.onSurfaceCreated(holder);
		}

		private StageListener getLocalStageListener() {
			return (StageListener) stageListener;
		}

		@Override
		public void onVisibilityChanged(boolean visible) {
			Log.d("LWP", "Engine: " + name + " the engine is visible: " + visible);

			mVisible = visible;
			super.onVisibilityChanged(visible);

			changeVisibilityStates(visible);
			Log.d("LWP", "Visibility changed: isPreview(" + isPreview() + ") is visible: " + visible);
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

		@Override
		public void onResume() {

			Log.d("LWP", "StageListener LiveWallpaperEngine onResume() ANFANG");
			if (getLocalStageListener() == null) {
				return;
			}


			//SensorHandler.startSensorListener(getApplicationContext());
			mHandler.postDelayed(mUpdateDisplay, REFRESH_RATE);
			super.onResume();
			Log.d("LWP", "StageListener LiveWallpaperEngine onResume() ENDE");
		}

		@Override
		public void onPause() {
			if (getLocalStageListener() == null) {
				return;
			}

			mHandler.removeCallbacks(mUpdateDisplay);
			//SensorHandler.stopSensorListeners();
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

		public synchronized void changeWallpaperProgram() {

			if (getLocalStageListener() == null) {
				Log.d("LWP", "StageListener, Fehler bei changeWallpaper " + name);
				return;
			}
			if (!isPreview()) {
				return;
			}
			//onPause();
			LiveWallpaperEngine engine = this;


			Log.d("LWP", "StageListener, changeWallpaper Engine: " + name);

			synchronized (engine) {
				try {
					Log.d("LWP", "StageListener, changeWallpaper wait... ANFANG");
					ProjectManager.changeState(ProjectManagerState.LWP);
					getLocalStageListener().create();
					ProjectManager.changeState(ProjectManagerState.NORMAL);
					getLocalStageListener().reloadProjectLWP(engine);
					onResume();
					engine.wait();
				} catch (InterruptedException e) {
					Log.d("LWP", "StageListener, Fehler bei changeWallpaper wait...");
				}
			}
			Log.d("LWP", "StageListener, changeWallpaper wait... ENDE");

			getLocalStageListener().menuResume();
		}

		public void tinting(int c) {
			setTinting(true);
			getLocalStageListener().setTintingColor(c);
		}

		public void setTinting(boolean isTinting) {
			getLocalStageListener().setTinting(isTinting);
		}

		private void activateTextToSpeechIfNeeded() {
						if (PreStageActivity.initTextToSpeechForLiveWallpaper(context) != 0) {
							Intent installIntent = new Intent();
							installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
							startActivity(installIntent);
						}
		}
	}

	/**
	 * 
	 */
	public void presetSprites() {
		if (previewEngine != null) {
			previewEngine.getLocalStageListener().resetSprites();
		}
	}

	public void sayHelloInLog()
	{
		Log.e("LiveWallpaperBinder","Hello :-)");
	}

	public class LiveWallpaperBinder extends Binder {

		public LiveWallpaper getService() {
			return LiveWallpaper.getInstance();
		}


	}
}
