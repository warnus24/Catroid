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

import android.app.Dialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.badlogic.gdx.Gdx;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.ScreenValues;

/**
 * @author White
 * 
 */
public class ColorPickerDialog extends Dialog {

	private OnColorChangedListener mListener;
	private int mInitialColor;
	private ColorPickerView view;

	public interface OnColorChangedListener {
		void colorChanged(int color);
	}

	public ColorPickerDialog(Context context, OnColorChangedListener listener, int initialColor) {
		super(context);

		mListener = listener;
		mInitialColor = initialColor;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		OnColorChangedListener l = new OnColorChangedListener() {
			@Override
			public void colorChanged(int color) {
				mListener.colorChanged(color);
				dismiss();
			}
		};

		view = new ColorPickerView(getContext(), l, mInitialColor);
		setContentView(view);
		setTitle(R.string.lwp_color_dialog_title);
	}

	public void simulateSepiaTouchEvent()
	{
		view.simulateSepiaTouchEvent();
	}

	public static class ColorPickerView extends View {
		private Paint mPaint;
		private Paint mCenterPaint;
		private final int[] mColors;
		private OnColorChangedListener mListener;

		private static int CENTER_X = 250;
		private static int CENTER_Y = 250;
		private static int CENTER_RADIUS = 80;

		ColorPickerView(Context c, OnColorChangedListener l, int color) {
			super(c);
			mListener = l;
			mColors = new int[] { 0xFFFF0000, 0xFFFF00FF, 0xFF0000FF, 0xFF00FFFF, 0xFF00FF00, 0xFFFFFF00, 0xFFFF0000 };
			Shader s = new SweepGradient(0, 0, mColors, null);

			mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
			mPaint.setShader(s);
			mPaint.setStyle(Paint.Style.STROKE);
			mPaint.setStrokeWidth(32);

			mCenterPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
			mCenterPaint.setColor(color);
			mCenterPaint.setStrokeWidth(5);
		}

		private boolean mTrackingCenter;
		private boolean mHighlightCenter;

		@Override
		protected void onDraw(Canvas canvas) {
			int center_min = Math.min(canvas.getWidth(), canvas.getHeight());
			int center_x = canvas.getWidth();
			int center_y = canvas.getHeight();
			CENTER_X = Math.round(center_x * 0.5f);
			CENTER_Y = Math.round(center_y * 0.5f);
			CENTER_RADIUS = center_min / 6;
			float r = CENTER_X - (mPaint.getStrokeWidth()*2);

			canvas.translate(CENTER_X, CENTER_Y);

			canvas.drawOval(new RectF(-r, -r, r, r), mPaint);
			canvas.drawCircle(0, 0, CENTER_RADIUS, mCenterPaint);



			if (mTrackingCenter) {
				int c = mCenterPaint.getColor();
				mCenterPaint.setStyle(Paint.Style.STROKE);

				if (mHighlightCenter) {
					mCenterPaint.setAlpha(0xFF);
				} else {
					mCenterPaint.setAlpha(0x80);
				}
				int screenWidth = ScreenValues.SCREEN_WIDTH;
				canvas.drawCircle(0, 0, CENTER_RADIUS + mCenterPaint.getStrokeWidth(), mCenterPaint);

				mCenterPaint.setStyle(Paint.Style.FILL);
				mCenterPaint.setColor(c);
			}
		}

		@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);

			int width = getMeasuredWidth();
			int height = getMeasuredHeight();

			int size = width < height ? width : height;
			setMeasuredDimension(size, size);
		}


		private int floatToByte(float x) {
			int n = java.lang.Math.round(x);
			return n;
		}

		private int pinToByte(int n) {
			if (n < 0) {
				n = 0;
			} else if (n > 255) {
				n = 255;
			}
			return n;
		}

		private int ave(int s, int d, float p) {
			return s + java.lang.Math.round(p * (d - s));
		}

		private int interpColor(int colors[], float unit) {
			if (unit <= 0) {
				return colors[0];
			}
			if (unit >= 1) {
				return colors[colors.length - 1];
			}

			float p = unit * (colors.length - 1);
			int i = (int) p;
			p -= i;

			// now p is just the fractional part [0...1) and i is the index
			int c0 = colors[i];
			int c1 = colors[i + 1];
			int a = ave(Color.alpha(c0), Color.alpha(c1), p);
			int r = ave(Color.red(c0), Color.red(c1), p);
			int g = ave(Color.green(c0), Color.green(c1), p);
			int b = ave(Color.blue(c0), Color.blue(c1), p);

			return Color.argb(a, r, g, b);
		}

		private int rotateColor(int color, float rad) {
			float deg = rad * 180 / 3.1415927f;
			int r = Color.red(color);
			int g = Color.green(color);
			int b = Color.blue(color);

			ColorMatrix cm = new ColorMatrix();
			ColorMatrix tmp = new ColorMatrix();

			cm.setRGB2YUV();
			tmp.setRotate(0, deg);
			cm.postConcat(tmp);
			tmp.setYUV2RGB();
			cm.postConcat(tmp);

			final float[] a = cm.getArray();

			int ir = floatToByte(a[0] * r + a[1] * g + a[2] * b);
			int ig = floatToByte(a[5] * r + a[6] * g + a[7] * b);
			int ib = floatToByte(a[10] * r + a[11] * g + a[12] * b);

			return Color.argb(Color.alpha(color), pinToByte(ir), pinToByte(ig), pinToByte(ib));
		}

		private static final float PI = 3.1415926f;

		public void simulateSepiaTouchEvent()
		{
			// Click on Color
			float x = CENTER_X + (CENTER_X - (mPaint.getStrokeWidth()*2));
			float y = CENTER_Y + (CENTER_Y*0.1f - (mPaint.getStrokeWidth()*2));
			long downTime = SystemClock.uptimeMillis();
			long eventTime = SystemClock.uptimeMillis() + 100;
			int metaState = 0;
			MotionEvent motionEvent = MotionEvent.obtain(
					downTime,
					eventTime,
					MotionEvent.ACTION_MOVE,
					x,
					y,
					metaState
			);

			onTouchEvent(motionEvent);


			//Click on Middle
			float x_1 = CENTER_X;
			float y_1 = CENTER_Y;
			long downTime_1 = SystemClock.uptimeMillis();
			long eventTime_1 = SystemClock.uptimeMillis() + 100;
			int metaState_1 = 0;


			MotionEvent motionEvent_2 = MotionEvent.obtain(
					downTime_1,
					eventTime_1,
					MotionEvent.ACTION_DOWN,
					x_1,
					y_1,
					metaState_1
			);

			onTouchEvent(motionEvent_2);

			MotionEvent motionEvent_1 = MotionEvent.obtain(
					downTime_1,
					eventTime_1,
					MotionEvent.ACTION_UP,
					x_1,
					y_1,
					metaState_1
			);

			onTouchEvent(motionEvent_1);
		}

		@Override
		public boolean onTouchEvent(MotionEvent event) {
			float x = event.getX() - CENTER_X;
			float y = event.getY() - CENTER_Y;

			Log.d("ColorPickerView", "width = " + String.valueOf(this.getWidth()));

			boolean inCenter = java.lang.Math.sqrt(x * x + y * y) <= CENTER_RADIUS;

			switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					mTrackingCenter = inCenter;
					if (inCenter) {
						mHighlightCenter = true;
						invalidate();
						break;
					}
				case MotionEvent.ACTION_MOVE:
					if (mTrackingCenter) {
						if (mHighlightCenter != inCenter) {
							mHighlightCenter = inCenter;
							invalidate();
						}
					} else {
						float angle = (float) java.lang.Math.atan2(y, x);
						// need to turn angle [-PI ... PI] into unit [0....1]
						float unit = angle / (2 * PI);
						if (unit < 0) {
							unit += 1;
						}
						mCenterPaint.setColor(interpColor(mColors, unit));
						invalidate();
					}
					break;
				case MotionEvent.ACTION_UP:
					if (mTrackingCenter) {
						if (inCenter) {
							mListener.colorChanged(mCenterPaint.getColor());
						}
						mTrackingCenter = false; // so we draw w/o halo
						invalidate();
					}
					break;
			}
			return true;
		}
	}

}