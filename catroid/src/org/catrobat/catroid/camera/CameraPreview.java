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
package org.catrobat.catroid.camera;


import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.content.Sprite;

import java.io.IOException;


public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {

	private static CameraPreview instance;
	private static boolean surface = false;
	private static boolean paused = false;
	private SurfaceHolder mHolder;
	private Camera camera;
	private Context context;

	private CameraPreview(Context context, Camera camera) {
		super(context);
		this.camera = camera;

		// Install a SurfaceHolder.Callback so we get notified when the
		// underlying surface is created and destroyed.
		mHolder = getHolder();
		mHolder.addCallback(this);
		// deprecated setting, but required on Android versions prior to 3.0
		//mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	public static void createCameraPreview(Context context) {
		if (instance == null && context != null) {
			Log.d("Lausi", "createCameraPreview");
			instance = new CameraPreview(context, CameraManager.getInstance().getCamera());
			CameraManager.getInstance().updateCameraID(context);
		}
	}

	public static CameraPreview getInstance() {
		return instance;
	}

	public void surfaceCreated(SurfaceHolder holder) {
		Log.d("Lausi", "surfaceCreated");
		try {
			camera.setPreviewDisplay(holder);
			surface = true;
		} catch (IOException e) {
			Log.d("CamPreview", "Error setting surface: " + e.getMessage());
		}
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.d("Lausi", "surfaceDestroyed");
		// empty. Take care of releasing the Camera preview in your activity.
		surface = false;
		CameraManager.getInstance().releaseCamera();
	}

	public void startPreview() {
		Log.d("Lausi", "startPreview");
		if(surface) {
			Log.d("Lausi", "startPreview, in if");
			camera.startPreview();
		}
		/*LinearLayout preview = (LinearLayout)findViewById(R.id.stage_layout_linear);
		if(preview != null) {
			Log.d("Lausi", "preview not null");
			preview.addView(instance);
		}*/

		/*int backgroundSpriteIndex = 0;
		int backgroundLookDataIndex = 0;
		LookData backgroundLookData = testProject.getSpriteList().get(backgroundSpriteIndex).getLookDataList()
				.get(backgroundLookDataIndex);
		backgroundLookData.getTextureRegion()*/
		//Sprite background = ProjectManager.getInstance().getCurrentProject().getSpriteList().get(0);
		//FrameLayout preview = (FrameLayout)findViewById(R.id.spritelist_item_background);
		//preview.addView(instance);
		//FrameLayout preview = (FrameLayout) findViewById(R.id.);
		//preview.addView(instance);

	}

	public void stopPreview() {
		Log.d("Lausi", "stopPreview");
			camera.stopPreview();
	}
	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		// If your preview can change or rotate, take care of those events here.
		// Make sure to stop the preview before resizing or reformatting it.
/*
		if (mHolder.getSurface() == null){
			// preview surface does not exist
			return;
		}

		// stop preview before making changes
		try {
			camera.stopPreview();
		} catch (Exception e){
			// ignore: Try to stop a non-existent preview
			Log.d("CamPreview", "Try to stop a non-existent preview ");
		}

		// set preview size and make any resize, rotate or
		// reformatting changes here

		// start preview with new settings
		try {
			camera.setPreviewDisplay(mHolder);
			camera.startPreview();

		} catch (Exception e){
			Log.d("CamPreview", "Error starting camera preview: " + e.getMessage());
		}*/
	}
}