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

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import org.catrobat.catroid.R;
import org.catrobat.catroid.livewallpaper.LiveWallpaper;

import java.util.ArrayList;
import java.util.List;


public class SelectPostProcessingEffectFragment extends ListFragment {
	private SelectPostProcessingEffectFragment selectPostProcessingEffectFragment;

	private String selectedEffect;
	private List<String> effectList;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		selectPostProcessingEffectFragment = this;
		return inflater.inflate(R.layout.fragment_lwp_select_program, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initListeners();
	}

	private void initListeners() {
		effectList = new ArrayList<String>();
		effectList.add(PostProcessingEffectsEnum.BLOOM.toString());
		effectList.add(PostProcessingEffectsEnum.VIGNETTE.toString());
		effectList.add(PostProcessingEffectsEnum.CURVATURE.toString());
		effectList.add(PostProcessingEffectsEnum.CRTMONITOR.toString());
		effectList.add(PostProcessingEffectsEnum.EFFECT_1.toString());
		effectList.add(PostProcessingEffectsEnum.EFFECT_2.toString());

		String[] effectArray = effectList.toArray(new String[effectList.size()]);

		ArrayAdapter<String> adapter;
		adapter = new ArrayAdapter<String>(
				getActivity(),
				R.layout.activity_postprocessing_list_item,
				R.id.activity_postprocessing_text1,
				effectArray);

		setListAdapter(adapter);
	}

	public void goToSelectPostProcessingEffects()
	{
		Intent intent = new Intent(this.getActivity(), SelectBloomEffectActivity.class);
		//String message = editText.getText().toString();
		//intent.putExtra(EXTRA_MESSAGE, message);
		startActivity(intent);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		String item = (String) getListAdapter().getItem(position);

		if(item.equals(PostProcessingEffectsEnum.EFFECT_1.toString())){
			LiveWallpaper.getInstance().activateEffect1();
		}
		else if(item.equals(PostProcessingEffectsEnum.EFFECT_2.toString())){
			LiveWallpaper.getInstance().activateEffect2();
		}
		else if(item.equals(PostProcessingEffectsEnum.BLOOM.toString())){
			goToSelectPostProcessingEffects();
		}



		Toast.makeText(getActivity(), item, Toast.LENGTH_LONG).show();
	}
}
