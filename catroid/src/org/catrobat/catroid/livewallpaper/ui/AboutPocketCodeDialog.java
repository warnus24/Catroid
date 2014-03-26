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
package org.catrobat.catroid.livewallpaper.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Window;
import android.widget.TextView;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.utils.Utils;

public class AboutPocketCodeDialog extends Dialog {

	private Context context;

	public AboutPocketCodeDialog(Context context) {
		super(context);
		this.context = context;
	}

	//This is basically a copy of AboutDialogFragment. It displays (or should display)
	//exactly the same information. The reason the code is copied is because the fragment dialog
	//can not be shown from the PreferenceFragment (used in live wallpaper settings) since the
	//PreferenceFragment is not a part of support package. As soon as a FragmentDialog can be shown
	// from a PreferenceFragment, delete this shame and use AboutDialogFragment

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_LEFT_ICON);
		setContentView(R.layout.dialog_lwp_about);
		setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, android.R.drawable.ic_dialog_info);

		setTitle(R.string.dialog_about_title);
		setCanceledOnTouchOutside(true);

		TextView aboutUrlTextView = (TextView) findViewById(R.id.dialog_about_text_view_url);
		aboutUrlTextView.setMovementMethod(LinkMovementMethod.getInstance());
		String aboutUrl = context.getString(R.string.about_link_template, Constants.ABOUT_POCKETCODE_LICENSE_URL,
				context.getString(R.string.dialog_about_pocketcode_license_link_text));
		aboutUrlTextView.setText(Html.fromHtml(aboutUrl));

		TextView aboutUrlCatrobatView = (TextView) findViewById(R.id.dialog_about_text_catrobat_url);
		aboutUrlCatrobatView.setMovementMethod(LinkMovementMethod.getInstance());
		String aboutCatrobatUrl = context.getString(R.string.about_link_template, Constants.CATROBAT_ABOUT_URL,
				context.getString(R.string.dialog_about_catrobat_link_text));
		aboutUrlCatrobatView.setText(Html.fromHtml(aboutCatrobatUrl));

		TextView aboutVersionNameTextView = (TextView) findViewById(R.id.dialog_about_text_view_version_name);
		String versionName = Utils.getVersionName(context);
		aboutVersionNameTextView.setText(versionName);

	}
}