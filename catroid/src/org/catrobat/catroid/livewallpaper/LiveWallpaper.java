
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
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.backends.android.AndroidLiveWallpaperService;
import com.bitfire.postprocessing.PostProcessorEffect;

import org.catrobat.catroid.ProjectHandler;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.BroadcastSequenceMap;
import org.catrobat.catroid.common.BroadcastWaitSequenceMap;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.ScreenValues;
import org.catrobat.catroid.exceptions.CompatibilityProjectException;
import org.catrobat.catroid.exceptions.LoadingProjectException;
import org.catrobat.catroid.exceptions.OutdatedVersionProjectException;
import org.catrobat.catroid.formulaeditor.SensorHandler;
import org.catrobat.catroid.livewallpaper.postprocessing.BloomAttributeContainer;
import org.catrobat.catroid.livewallpaper.postprocessing.CrtMonitorAttributeContainer;
import org.catrobat.catroid.livewallpaper.postprocessing.CurvatureAttributeContainer;
import org.catrobat.catroid.livewallpaper.postprocessing.PostProcessingEffectAttributContainer;
import org.catrobat.catroid.livewallpaper.postprocessing.PostProcessingEffectsEnum;
import org.catrobat.catroid.livewallpaper.postprocessing.VignetteAttributeContainer;
import org.catrobat.catroid.formulaeditor.SensorHandler;
import org.catrobat.catroid.livewallpaper.ui.SelectProgramActivity;
import org.catrobat.catroid.stage.PreStageActivity;
import org.catrobat.catroid.stage.StageListener;
import org.catrobat.catroid.utils.LedUtil;
import org.catrobat.catroid.utils.Utils;
import org.catrobat.catroid.utils.VibratorUtil;

import java.io.IOException;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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
	private Map<PostProcessingEffectsEnum,PostProcessingEffectAttributContainer> map = new HashMap<PostProcessingEffectsEnum,PostProcessingEffectAttributContainer>();
	private Map<PostProcessingEffectsEnum,PostProcessingEffectAttributContainer> effectAttributes = Collections.synchronizedMap(map);
	private StartLiveWallpaperSettingsThread startSettingsThread = null;

	private boolean isTest = false;
	private boolean firstStart = true;

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

	public Map<PostProcessingEffectsEnum,PostProcessingEffectAttributContainer>getEffectMap()
	{
		 return effectAttributes;
	}

	/**
	 * @return the previewEngine
	 */
	public LiveWallpaperEngine getPreviewEngine() {
		return previewEngine;
	}

	public static synchronized LiveWallpaper getInstance() {
		return INSTANCE;
	}
	public void resetWallpaper()
	{
		try {
			WallpaperManager.getInstance(getContext()).clear();
			//WallpaperManager.getInstance(getContext()).forgetLoadedWallpaper();
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
		this.firstStart = false;
	}

	@Override
	public void onCreate() {
			super.onCreate();
			SharedPreferences sharedPreferences = PreferenceManager
					.getDefaultSharedPreferences(getApplicationContext());
			context = this;
		oldProjectName = sharedPreferences.getString(Constants.PREF_LWP_PROJECTNAME_KEY, null);

		try {
			getApplication().clearWallpaper();
		} catch (IOException e) {
			e.printStackTrace();
		}
		INSTANCE = this;
		Log.d("LWP", "Neuer Service wurde geladen");
	}

	@Override
	public void onCreateApplication() {
		super.onCreateApplication();
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();

		setScreenSize(false);
		Utils.loadWallpaperIfNeeded(context);
		ProjectHandler.getInstance().changeToLiveWallpaper(this);
		stageListener = new StageListener(true);
		initialize(stageListener, config);
		StageListener listener = (StageListener) stageListener;
		listener.resetLiveWallpaperToPocketCodeSwitch();
		INSTANCE = this;
		Log.d("LWP", "Preview was initialized");
	}

	public void initializeForTest(){
		Utils.loadWallpaperIfNeeded(context);
		stageListener = new StageListener(true);
		previewEngine = new LiveWallpaperEngine();
	}

	public Context getContext() {
		if(context == null){
			context = this;
		}
		return context;
	}

	public StageListener getLocalStageListener() {
		return (StageListener) stageListener;
	}

	@Override
	public void onDestroy() {
		Log.d("LWP", "Service wird beendet");
		ProjectHandler.getInstance().changeToPocketCode(this);
		Utils.saveToPreferences(context, Constants.PREF_LWP_PROJECTNAME_KEY, oldProjectName);
		super.onDestroy();
		//PreStageActivity.shutDownTextToSpeechForLiveWallpaper();
	}

	@Override
	public void finalize(){
		try {
			super.finalize();
			if(previewEngine != null){
				previewEngine.mHandler.removeCallbacks(previewEngine.mUpdateDisplay);
				previewEngine.mVisible = false;
			}
			if(homeEngine != null){
				homeEngine.mHandler.removeCallbacks(homeEngine.mUpdateDisplay);
				homeEngine.mVisible = false;
			}
			INSTANCE = null;
		} catch (Throwable throwable) {
			throwable.printStackTrace();
		}
	}

	public void  changeWallpaperProgram() {
		if (previewEngine != null) {
			previewEngine.changeWallpaperProgram();
		}
	}


	public void setPostProcessingEffectAttributes(PostProcessingEffectAttributContainer attributes)
	{
		effectAttributes.put(attributes.getType(), attributes);
	}

	public PostProcessingEffectAttributContainer getPostProcessingEffectAttributes(PostProcessingEffectsEnum effectType)
	{
		return effectAttributes.get(effectType);
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

	public void activatePostProcessingEffect(PostProcessingEffectAttributContainer effectAttributes)
	{
		//effectsAttributContainer.put(effectAttributes.getType(), effectAttributes);
		if (previewEngine != null) {
			previewEngine.activatePostProcessingEffect(effectAttributes);
		}
		if (homeEngine != null) {
			homeEngine.activatePostProcessingEffect(effectAttributes);
		}
	}

	public void deactivatePostProcessingEffect(PostProcessingEffectAttributContainer effectAttributes)
	{
		//effectsAttributContainer.put(effectAttributes.getType(), effectAttributes);
		if (previewEngine != null) {
			previewEngine.deactivatePostProcessingEffect(effectAttributes);
		}
		if (homeEngine != null) {
			homeEngine.deactivatePostProcessingEffect(effectAttributes);
		}
	}


	public void disableEffects() {
		if (previewEngine != null) {
			previewEngine.disableEffects();
		}
		if (homeEngine != null) {
			homeEngine.disableEffects();
		}
	}

	public void createInstance(){
		INSTANCE = this;
		context = this;
	}

	public void pauseAndFinish(){
		if(previewEngine != null){
			previewEngine.onPause();
			previewEngine.onDestroy();
		}
		if(homeEngine != null){
			homeEngine.onPause();
			previewEngine.onDestroy();
		}
	}

	public class LiveWallpaperEngine extends AndroidWallpaperEngine {
		public String name = "";
		private final int REFRESH_RATE = 300;
		private boolean mVisible = true;
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

			if(firstStart){
				Intent intent = new Intent(LiveWallpaper.getInstance().getContext(), SelectProgramActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
				LiveWallpaper.getInstance().getContext().startActivity(intent);
			}
			else{
				startSettingsThread = new StartLiveWallpaperSettingsThread();
				startSettingsThread.start();
			}

		}

		public StageListener getLocalStageListener() {
			return (StageListener) stageListener;
		}

		@Override
		public void onVisibilityChanged(boolean visible) {
			Log.d("LWP", "Engine: " + name + " the engine is visible: " + visible);

			super.onVisibilityChanged(visible);

			Log.d("LWP", "Visibility changed: isPreview(" + isPreview() + ") is visible: " + visible);
		}

		@Override
		public void onResume() {
			Log.d("LWP", "StageListener LiveWallpaperEngine onResume() ANFANG");
			if (getLocalStageListener() == null) {
				return;
			}
			//SensorHandler.startSensorListener(getApplicationContext());
			ProjectHandler.getInstance().changeToLiveWallpaper(getContext());
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

			Log.d("LWP", "Surface changed");
			//if (mVisible) {
			//	mHandler.postDelayed(mUpdateDisplay, REFRESH_RATE);
			//} else {
			//	mHandler.removeCallbacks(mUpdateDisplay);
			//}

			createInstance();
			if(startSettingsThread != null){
				synchronized (startSettingsThread) {
					startSettingsThread.notify();
				}
			}
		}

		@Override
		public void onSurfaceDestroyed(SurfaceHolder holder) {
			Log.d("LWP", "destroying surface: " + name);
			//mVisible = false;
			mHandler.removeCallbacks(mUpdateDisplay);
			super.onSurfaceDestroyed(holder);
		}

		@Override
		public void onDestroy() {
			//mVisible = false;
			Log.d("LWP", "destroying engine: " + name);
			mHandler.removeCallbacks(mUpdateDisplay);
			super.onDestroy();
		}

		private void clearBroadcastMaps() {
			BroadcastSequenceMap.clear();
			BroadcastWaitSequenceMap.clear();
			BroadcastWaitSequenceMap.clearCurrentBroadcastEvent();
		}

		public void changeWallpaperProgram() {

			if (getLocalStageListener() == null) {
				Log.d("LWP", "StageListener, Fehler bei changeWallpaper " + name);
				return;
			}
			if (!isPreview()) {
				return;
			}
			LiveWallpaperEngine engine = this;
			clearBroadcastMaps();
			getLocalStageListener().create();

			if(Gdx.graphics.isContinuousRendering()){
				Log.d("LWP", "Is continous rendering");
			}

			if(!Gdx.graphics.isContinuousRendering()){
				Log.d("LWP", "Is NOT continous rendering");
				Gdx.graphics.setContinuousRendering(true);
			}

			synchronized (engine) {
				try {
					Log.d("LWP", "StageListener, changeWallpaper wait... ANFANG");
					getLocalStageListener().reloadProjectLWP(engine);
					mHandler.postDelayed(mUpdateDisplay, REFRESH_RATE);
					engine.wait();
				} catch (InterruptedException e) {
					Log.d("LWP", "StageListener, Fehler bei changeWallpaper wait...");
				}
			}

			getLocalStageListener().menuResume();
			LedUtil.resumeLed();
			VibratorUtil.resumeVibrator();
			SensorHandler.startSensorListener(context);
		}

		public void tinting(int c) {
			setTinting(true);
			getLocalStageListener().setTintingColor(c);
		}

		public void setTinting(boolean isTinting) {
			getLocalStageListener().setTinting(isTinting);
		}


		public void activatePostProcessingEffect(PostProcessingEffectAttributContainer effectAttributes)
		{
			getLocalStageListener().activatePostProcessingEffects(effectAttributes);
		}

		public void deactivatePostProcessingEffect(PostProcessingEffectAttributContainer effectAttributes)
		{
			getLocalStageListener().deactivatePostProcessingEffects(effectAttributes);
		}

		public void disableEffects()
		{
			getLocalStageListener().disableEffects();
		}

		private void activateTextToSpeechIfNeeded() {
			//if (PreStageActivity.initTextToSpeechForLiveWallpaper(context) != 0) {
			//	Intent installIntent = new Intent();
			//	installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
			//	startActivity(installIntent);
			//}
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
}
