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

package org.catrobat.catroid.livewallpaper.ui;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.catrobat.catroid.R;
import org.catrobat.catroid.livewallpaper.LiveWallpaper;
import org.catrobat.catroid.livewallpaper.postprocessing.DefaultPostProcessingEffectAttributeContainers;
import org.catrobat.catroid.livewallpaper.postprocessing.PostProcessingEffectAttributContainer;
import org.catrobat.catroid.livewallpaper.postprocessing.PostProcessingEffectsEnum;
import org.catrobat.catroid.utils.PostProcessingUtil;

/**
 * Created by White on 30.08.2014.
 */
public class CustomArrayAdapter extends ArrayAdapter<PostProcessingEffectsEnum>
{
	Activity activity;
	public CustomArrayAdapter(Activity activity, android.content.Context context, int resource, int textViewResourceId, PostProcessingEffectsEnum[] objects)
	{
		super(context, resource, textViewResourceId, objects);
		this.activity = activity;
	}


	@Override
	public View getView (int position, View convertView, ViewGroup parent){
		PostProcessingEffectsEnum item = getItem (position);
		PostProcessingEffectAttributContainer attributes = null;
		if(LiveWallpaper.getInstance() != null){
			attributes = LiveWallpaper.getInstance().getPostProcessingEffectAttributes(item);
		}

		LayoutInflater inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View rowView;
		if(attributes == null) {
			rowView = inflater.inflate(R.layout.activity_postprocessing_list_item_disabled, null);
			PostProcessingUtil.setBloomPostProcessingEffectLayoutEnabled(false);
		}
		else if(attributes.isEnabled()) {
			rowView = inflater.inflate(R.layout.activity_postprocessing_list_item_enabled, null);
			PostProcessingUtil.setBloomPostProcessingEffectLayoutEnabled(true);
		}
		else {
			rowView = inflater.inflate(R.layout.activity_postprocessing_list_item_disabled, null);
			PostProcessingUtil.setBloomPostProcessingEffectLayoutEnabled(false);
		}

		rowView.setId(position);

		TextView effectDescription = (TextView)rowView.findViewById(R.id.activity_postprocessing_text1);
		effectDescription.setText(item.toString());

		return rowView;
	}
}
