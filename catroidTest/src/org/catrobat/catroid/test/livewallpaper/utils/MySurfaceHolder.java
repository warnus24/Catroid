/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2014 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.catroid.test.livewallpaper.utils;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Build;
import android.view.Surface;
import android.view.SurfaceHolder;

/**
 * Created by White on 24.09.2014.
 */
public class MySurfaceHolder implements SurfaceHolder
{

	@Override
	public void addCallback(Callback callback) {

	}

	@Override
	public void removeCallback(Callback callback) {

	}

	@Override
	public boolean isCreating() {
		return false;
	}

	@Override
	public void setType(int i) {

	}

	@Override
	public void setFixedSize(int i, int i2) {

	}

	@Override
	public void setSizeFromLayout() {

	}

	@Override
	public void setFormat(int i) {

	}

	@Override
	public void setKeepScreenOn(boolean b) {

	}

	@Override
	public Canvas lockCanvas() {
		return new Canvas();
	}

	@Override
	public Canvas lockCanvas(Rect rect) {
		return new Canvas();
	}

	@Override
	public void unlockCanvasAndPost(Canvas canvas) {

	}

	@Override
	public Rect getSurfaceFrame() {
		return new Rect();
	}

	@Override
	public Surface getSurface() {
		if(Build.VERSION.SDK_INT >= 14){
			return new Surface(new android.graphics.SurfaceTexture(0));
		}
		return null;
	}
}
