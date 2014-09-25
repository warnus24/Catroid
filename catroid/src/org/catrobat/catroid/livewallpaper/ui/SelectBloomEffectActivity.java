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

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Switch;

import com.actionbarsherlock.app.ActionBar;

import org.catrobat.catroid.R;
import org.catrobat.catroid.livewallpaper.LiveWallpaper;
import org.catrobat.catroid.livewallpaper.postprocessing.BloomAttributeContainer;
import org.catrobat.catroid.livewallpaper.postprocessing.PostProcessingEffectAttributContainer;
import org.catrobat.catroid.livewallpaper.postprocessing.PostProcessingEffectsEnum;

public class SelectBloomEffectActivity extends BaseActivity {

	private BloomAttributeContainer attributes;
	private SeekBar seekBar1;
	private SeekBar seekBar2;
	private SeekBar seekBar3;
	private SeekBar seekBar4;
	private SeekBar seekBar5;
	private Switch mySwitch;
	private CustomOnSeekbarListener baseIntListener;
	private CustomOnSeekbarListener baseSatListener;
	private CustomOnSeekbarListener bloomIntListener;
	private CustomOnSeekbarListener bloomSatListener;
	private CustomOnSeekbarListener bloomThresholdListener;

	public static final float BASE_INT_FACTOR = 50.0F;
	public static final float BASE_SAT_FACTOR = 58.8F;
	public static final float BLOOM_INT_FACTOR = 45.4F;
	public static final float BLOOM_SAT_FACTOR = 58.8F;
	public static final float BLOOM_THRESHOLD_FACTOR = 180.51F;
	public SelectBloomEffectActivity INSTANCE;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_bloom_effect);
		setUpActionBar();
		initializeControlElements();
		setUpActualContext();
		INSTANCE = this;
	}

	public void setUpActualContext()
	{
		PostProcessingEffectAttributContainer attributesContainer = LiveWallpaper.getInstance().getPostProcessingEffectAttributes(PostProcessingEffectsEnum.BLOOM);

		if(attributesContainer == null)
		{
			return;
		}

		BloomAttributeContainer attributes = (BloomAttributeContainer) LiveWallpaper.getInstance().getPostProcessingEffectAttributes(
																						PostProcessingEffectsEnum.BLOOM);
		mySwitch.setChecked(attributes.isEnabled());

		int progress1 = (int) (attributes.getBaseInt() * BASE_INT_FACTOR);
		seekBar1.setProgress(progress1);

		int progress2 = (int) (attributes.getBaseSat() * BASE_SAT_FACTOR);
		seekBar2.setProgress(progress2);

		int progress3 = (int) (attributes.getBloomInt() * BLOOM_INT_FACTOR);
		seekBar3.setProgress(progress3);

		int progress4 = (int) (attributes.getBloomSat() * BLOOM_SAT_FACTOR);
		seekBar4.setProgress(progress4);

		int progress5 = (int) (attributes.getThreshold() * BLOOM_THRESHOLD_FACTOR);
		seekBar5.setProgress(progress5);

	}

	private void initializeControlElements()
	{
		Button button = (Button)findViewById(R.id.button);
		button.setOnClickListener(myButtonListener);

		//Switch
		mySwitch = (Switch) findViewById(R.id.switch1);

		//Base Int
		seekBar1 = (SeekBar) findViewById(R.id.seekBar1);
		baseIntListener = new CustomOnSeekbarListener(BASE_INT_FACTOR);
		seekBar1.setOnSeekBarChangeListener(baseIntListener);

		//Base Sat
		seekBar2 = (SeekBar) findViewById(R.id.seekBar2);
		baseSatListener = new CustomOnSeekbarListener(BASE_SAT_FACTOR);
		seekBar2.setOnSeekBarChangeListener(baseSatListener);

		//Bloom Int
		seekBar3 = (SeekBar) findViewById(R.id.seekBar3);
		bloomIntListener = new CustomOnSeekbarListener(BLOOM_INT_FACTOR);
		seekBar3.setOnSeekBarChangeListener(bloomIntListener);

		//Bloom Sat
		seekBar4 = (SeekBar) findViewById(R.id.seekBar4);
		bloomSatListener = new CustomOnSeekbarListener(BLOOM_SAT_FACTOR);
		seekBar4.setOnSeekBarChangeListener(bloomSatListener);

		//Bloom Threshold
		seekBar5 = (SeekBar) findViewById(R.id.seekBar5);
		bloomThresholdListener = new CustomOnSeekbarListener(BLOOM_THRESHOLD_FACTOR);
		seekBar5.setOnSeekBarChangeListener(bloomThresholdListener);
	}

	private void setUpActionBar() {
		final ActionBar actionBar = getSupportActionBar();
		actionBar.setTitle(R.string.lwp_select_program);
		actionBar.setDisplayHomeAsUpEnabled(false);
		actionBar.setHomeButtonEnabled(false);
	}

	// Create an anonymous implementation of OnClickListener
	private View.OnClickListener myButtonListener = new View.OnClickListener() {
		public void onClick(View v) {

			BloomAttributeContainer bloomAttributes = new BloomAttributeContainer();
			bloomAttributes.setBaseInt(baseIntListener.getAttribute());
			bloomAttributes.setBaseSat(baseSatListener.getAttribute());
			bloomAttributes.setBloomInt(bloomIntListener.getAttribute());
			bloomAttributes.setBloomSat(bloomSatListener.getAttribute());
			bloomAttributes.setThreshold(bloomThresholdListener.getAttribute());

			if(mySwitch.isChecked())
			{
				bloomAttributes.setEnabled(true);
				LiveWallpaper.getInstance().activatePostProcessingEffect(bloomAttributes);
			}
			else
			{
				bloomAttributes.setEnabled(false);
				LiveWallpaper.getInstance().deactivatePostProcessingEffect(bloomAttributes);
			}
			INSTANCE.onBackPressed();
		}
	};

}
