
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
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.WindowManager;
import android.widget.Toast;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.backends.android.AndroidLiveWallpaperService;

import org.catrobat.catroid.ProjectHandler;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.BroadcastSequenceMap;
import org.catrobat.catroid.common.BroadcastWaitSequenceMap;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.ScreenValues;
import org.catrobat.catroid.content.BroadcastHandler;
import org.catrobat.catroid.livewallpaper.ui.SelectProgramActivity;
import org.catrobat.catroid.stage.PreStageActivity;
import org.catrobat.catroid.stage.StageListener;
import org.catrobat.catroid.utils.Utils;

import java.io.IOException;

@SuppressLint("NewApi")
//eventuell unnötig 10 intern 15 vorraussetzen Fehlerfall abfangen API Level vorraussetzen  prüfen mit 10
public class LiveWallpaper extends AndroidLiveWallpaperService {
	private int rememberVolume=50;
	private Context context;
	private String oldProjectName;
	boolean resumeFromPocketCode = false;
	private LiveWallpaperEngine previewEngine;
	private LiveWallpaperEngine homeEngine;

	private ApplicationListener stageListener = null;
	private boolean isTest = false;

	public LiveWallpaper() {
		super();
		if(INSTANCE == null){
			INSTANCE = this;
		}

		Log.e("Error", "new LiveWallpaper");
	}

	private static LiveWallpaper INSTANCE = null;

	public void setResumeFromPocketCode(boolean resumeFromPocketCode) {
		this.resumeFromPocketCode = resumeFromPocketCode;
	}

	public boolean isResumeFromPocketCode() {
		return resumeFromPocketCode;
	}

	public static synchronized LiveWallpaper getInstance() {
		return INSTANCE;
	}
	public void resetWallpaper()
	{
		try {
			WallpaperManager.getInstance(getContext()).clear();
		} catch (IOException e) {
			Log.e("LWP", "Something somewhere went wrong :-P ");
			e.printStackTrace();
		}
	}
	public int getRememberVolume() {
		return rememberVolume;
	}

	public void setRememberVolume(int rememberVolume) {
		this.rememberVolume = rememberVolume;
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
			//super.onCreate();
			SharedPreferences sharedPreferences = PreferenceManager
					.getDefaultSharedPreferences(getApplicationContext());
			context = this;
		oldProjectName = sharedPreferences.getString(Constants.PREF_LWP_PROJECTNAME_KEY, null);

		try {
			getApplication().clearWallpaper();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Log.d("LWP", "Neuer Service wurde geladen");
	}

	@Override
	public void onCreateApplication() {
		//super.onCreateApplication();
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		setScreenSize(false);
		Utils.loadWallpaperIfNeeded(context);
		ProjectHandler.getInstance().changeToLiveWallpaper();
		stageListener = new StageListener(true);
		initialize(stageListener, config);
		Log.d("LWP", "Preview was initialized");
	}

	public void initializeForTest(){
		Utils.loadWallpaperIfNeeded(context);
		stageListener = new StageListener(true);
		previewEngine = new LiveWallpaperEngine();
		isTest = true;
	}

	public Context getContext() {
		return context;
	}

	public StageListener getLocalStageListener() {
		return (StageListener) stageListener;
	}

	@Override
	public void onDestroy() {
		Log.d("LWP", "Service wird beendet");
		ProjectHandler.getInstance().changeToPocketCode();
		Utils.saveToPreferences(context, Constants.PREF_LWP_PROJECTNAME_KEY, oldProjectName);
		INSTANCE = null;
		super.onDestroy();
		PreStageActivity.shutDownTextToSpeechForLiveWallpaper();
	}

	public void changeWallpaperProgram() {
		if (previewEngine != null) {
			previewEngine.changeWallpaperProgram();
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

		public LiveWallpaperEngine() {
			super();
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

			if (isPreview()){
				startSettingsActivity();
			}
		}

		public void startSettingsActivity(){
			this.onPause();
			Intent intent;
			intent = new Intent(getContext(), SelectProgramActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
		}

		public StageListener getLocalStageListener() {
			return (StageListener) stageListener;
		}

		@Override
		public void onVisibilityChanged(boolean visible) {
			Log.d("LWP", "Engine: " + name + " the engine is visible: " + visible);

			mVisible = visible;
			super.onVisibilityChanged(visible);

			Log.d("LWP", "Visibility changed: isPreview(" + isPreview() + ") is visible: " + visible);
		}

		@Override
		public void onResume() {
			Log.d("LWP", "StageListener LiveWallpaperEngine onResume() ANFANG");
			if (getLocalStageListener() == null) {
				return;
			}
			ProjectHandler.getInstance().changeToLiveWallpaper();
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
			stopSelf();
			super.onSurfaceDestroyed(holder);
		}

		@Override
		public void onDestroy() {
			mVisible = false;
			Log.d("LWP", "destroying engine: " + name);
			mHandler.removeCallbacks(mUpdateDisplay);
			super.onDestroy();
		}

		private void clearBroadcastMaps() {
			BroadcastSequenceMap.clear();
			BroadcastWaitSequenceMap.clear();
			BroadcastWaitSequenceMap.clearCurrentBroadcastEvent();
		}

		public synchronized void changeWallpaperProgram() {

			if (getLocalStageListener() == null || isTest) {
				Log.d("LWP", "StageListener, Fehler bei changeWallpaper " + name);
				return;
			}
			if (!isPreview()) {
				return;
			}
			LiveWallpaperEngine engine = this;
			clearBroadcastMaps();
			getLocalStageListener().create();
			getLocalStageListener().reloadProjectLWP(engine);
			mHandler.postDelayed(mUpdateDisplay, REFRESH_RATE);

			synchronized (engine) {
				try {
					Log.d("LWP", "StageListener, changeWallpaper wait... ANFANG");
					engine.wait();
				} catch (InterruptedException e) {
					Log.d("LWP", "StageListener, Fehler bei changeWallpaper wait...");
				}
			}

			getLocalStageListener().menuResume();
		}

		public void tinting(int c) {
			setTinting(true);
			getLocalStageListener().setTintingColor(c);
		}

		public void setTinting(boolean isTinting) {
			getLocalStageListener().setTinting(isTinting);
		}


	}
}
