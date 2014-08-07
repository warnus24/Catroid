/**
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2013 The Catrobat Team
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
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.badlogic.gdx.backends.android.AndroidApplication;

import org.catrobat.catroid.BuildConfig;
import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.ScreenValues;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.utils.DownloadUtil;
import org.catrobat.catroid.utils.ImageEditing;

import java.io.File;

@SuppressLint("SetJavaScriptEnabled")
public class StandaloneWebViewActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_standalone_advertising);

		TextView app_name = (TextView) findViewById(R.id.title);
		app_name.setText(BuildConfig.START_PROJECT);

		Bitmap bitmap = scaleDrawable2Bitmap();

		ImageView imageView = (ImageView) findViewById(R.id.imageView);
		imageView.setImageBitmap(bitmap);
		imageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Uri uri = Uri.parse("market://details?id=org.catrobat.catroid");
				Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
				try {
					startActivity(myAppLinkToMarket);
				} catch (ActivityNotFoundException e) {
					Toast.makeText(StandaloneWebViewActivity.this, R.string.main_menu_play_store_not_installed, Toast.LENGTH_SHORT).show();
				}
			}
		});

		TextView playstore, website;
		website = (TextView) findViewById(R.id.website_link);
		playstore = (TextView) findViewById(R.id.playStore_link);

		website.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String url = ProjectManager.getInstance().getCurrentProject().getXmlHeader().getUrl();
				startWebViewActivity(url);
			}
		});

		playstore.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Uri uri = Uri.parse("market://search?q=Catrobat");
				Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
				try {
					startActivity(myAppLinkToMarket);
				} catch (ActivityNotFoundException e) {
					Toast.makeText(StandaloneWebViewActivity.this, R.string.main_menu_play_store_not_installed, Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	private Bitmap scaleDrawable2Bitmap() {
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.pocket_code);

		int width = ScreenValues.SCREEN_WIDTH;
		double factor = ((float) width / (float) bitmap.getWidth());
		int height = (int) ((float) bitmap.getHeight() * factor);
		Log.d("GSOC", "width: " + width + "  height: " + height + "   scaleFactor: " + (int) ((float) width / (float) bitmap.getWidth()));
		Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
		return scaledBitmap;
	}

	private void startWebViewActivity(String url) {
		// TODO just a quick fix for not properly working webview on old devices
		if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) {
			Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.BASE_URL_HTTPS));
			startActivity(browserIntent);
		} else {
			Intent intent = new Intent(StandaloneWebViewActivity.this, WebViewActivity.class);
			intent.putExtra(WebViewActivity.INTENT_PARAMETER_URL, url);
			startActivity(intent);
		}

	}
}