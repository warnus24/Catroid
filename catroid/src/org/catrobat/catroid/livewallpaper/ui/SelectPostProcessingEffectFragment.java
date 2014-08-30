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
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.bitfire.postprocessing.effects.CrtMonitor;
import com.bitfire.postprocessing.effects.Curvature;
import com.bitfire.postprocessing.effects.Vignette;

import org.catrobat.catroid.R;
import org.catrobat.catroid.livewallpaper.LiveWallpaper;
import org.catrobat.catroid.livewallpaper.postprocessing.BloomAttributeContainer;
import org.catrobat.catroid.livewallpaper.postprocessing.CrtMonitorAttributeContainer;
import org.catrobat.catroid.livewallpaper.postprocessing.CurvatureAttributeContainer;
import org.catrobat.catroid.livewallpaper.postprocessing.PostProcessingEffectAttributContainer;
import org.catrobat.catroid.livewallpaper.postprocessing.PostProcessingEffectsEnum;
import org.catrobat.catroid.livewallpaper.postprocessing.VignetteAttributeContainer;
import org.catrobat.catroid.livewallpaper.postprocessing.ZoomerAttributeContainer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class SelectPostProcessingEffectFragment extends ListFragment {
	public static final String ATTRIBUTES = "ATTRIBUTES";

	private SelectPostProcessingEffectFragment selectPostProcessingEffectFragment;

	private String selectedEffect;
	private static ArrayAdapter<PostProcessingEffectAttributContainer> adapter;
	private static PostProcessingEffectAttributContainer[] effectArray;
	private final int EFFECT_ARRAY_SIZE = 4;
	private static Activity activity;
	private static BloomAttributeContainer bloom = new BloomAttributeContainer();
	private static VignetteAttributeContainer vignette = new VignetteAttributeContainer();
	private static CurvatureAttributeContainer curvature = new CurvatureAttributeContainer();
	private static CrtMonitorAttributeContainer crtMonitor = new CrtMonitorAttributeContainer();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		selectPostProcessingEffectFragment = this;
		return inflater.inflate(R.layout.fragment_lwp_select_program, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initListeners();
		activity = getActivity();
	}

	public static void setActivated(PostProcessingEffectsEnum type, boolean active)
	{
		for(int i = 0; i < effectArray.length; i++)
		{
			PostProcessingEffectAttributContainer attributes = effectArray[i];
			if(attributes.getType().equals(type))
			{
				attributes.setEnabled(active);
				if(active)
				{
					Log.e("Error", "Effekt für " + type.toString() + " enabled: ");
				}
				else
				{
					Log.e("Error", "Effekt für " + type.toString() + " disabled: ");
				}
			}
		}
		adapter.notifyDataSetChanged();
		adapter = new CustomArrayAdapter(activity, activity,
				R.layout.activity_postprocessing_list_item_enabled,
				R.id.activity_postprocessing_text1,
				effectArray);

		//ToDo Refresh erst ab Android 3.0 möglich
		int version_code = Integer.valueOf(android.os.Build.VERSION.SDK);
		if(version_code >= 11)
		{
			activity.recreate();
		}

	}

	private void initListeners() {
		effectArray = new PostProcessingEffectAttributContainer[EFFECT_ARRAY_SIZE];
		effectArray[0] = bloom;
		effectArray[1] = vignette;
		effectArray[2] = curvature;
		effectArray[3] = crtMonitor;

		//adapter = new ArrayAdapter<String>(
		//		getActivity(), getActivity(),
		//		R.layout.activity_postprocessing_list_item_enabled,
		//		R.id.activity_postprocessing_text1,
		//		effectArray);

		adapter = new CustomArrayAdapter(getActivity(), getActivity(),
				R.layout.activity_postprocessing_list_item_enabled,
				R.id.activity_postprocessing_text1,
				effectArray);

		setListAdapter(adapter);
		adapter.notifyDataSetChanged();
	}

	public void goToSelectPostProcessingEffectGUI(Class<? extends BaseActivity> activityClass)
	{
		Intent intent = new Intent(this.getActivity(), activityClass);
		//intent.putExtra(ATTRIBUTES, attributes);
		startActivity(intent);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		adapter.notifyDataSetChanged();
		PostProcessingEffectAttributContainer item = (PostProcessingEffectAttributContainer) getListAdapter().getItem(position);
		chooseEffectAndInsertAttributes(item.getType().toString());
	}

	public void chooseEffectAndInsertAttributes(String item)
	{
		//BLOOM
		if(item.equals(PostProcessingEffectsEnum.BLOOM.toString())
				|| item.equals(PostProcessingEffectsEnum.EFFECT_1.toString())){
			goToSelectPostProcessingEffectGUI(SelectBloomEffectActivity.class);
		}

		//VIGNETTE
		if(item.equals(PostProcessingEffectsEnum.VIGNETTE.toString())
				|| item.equals(PostProcessingEffectsEnum.EFFECT_1.toString())){
			VignetteAttributeContainer vignetteAttributes = new VignetteAttributeContainer();
			LiveWallpaper.getInstance().activatePostProcessingEffect(vignetteAttributes);
		}

		//CURVATURE
		if(item.equals(PostProcessingEffectsEnum.CURVATURE.toString())
				|| item.equals(PostProcessingEffectsEnum.EFFECT_2.toString())){
			CurvatureAttributeContainer curvatureAttributes = new CurvatureAttributeContainer();
			LiveWallpaper.getInstance().activatePostProcessingEffect(curvatureAttributes);
		}

		//CRT-MONITOR
		if(item.equals(PostProcessingEffectsEnum.CRTMONITOR.toString())
				|| item.equals(PostProcessingEffectsEnum.EFFECT_2.toString())){
			CrtMonitorAttributeContainer crtMonitorAttributes = new CrtMonitorAttributeContainer();
			LiveWallpaper.getInstance().activatePostProcessingEffect(crtMonitorAttributes);
		}

		//ZOOMER
		if(item.equals(PostProcessingEffectsEnum.ZOOMER.toString())){
			ZoomerAttributeContainer zommerAttributes = new ZoomerAttributeContainer();
			LiveWallpaper.getInstance().activatePostProcessingEffect(zommerAttributes);
		}
	}
}
