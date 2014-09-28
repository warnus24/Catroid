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
import org.catrobat.catroid.livewallpaper.postprocessing.CrtMonitorAttributeContainer;
import org.catrobat.catroid.livewallpaper.postprocessing.PostProcessingEffectAttributContainer;
import org.catrobat.catroid.livewallpaper.postprocessing.PostProcessingEffectsEnum;

public class SelectCrtMonitorEffectActivity extends BaseActivity {

	private BloomAttributeContainer attributes;
	private SeekBar seekBar1;
	private SeekBar seekBar2;
	private SeekBar seekBar3;
	private Switch mySwitch;
	private CustomOnSeekbarListener chromaticDispersionRCListener;
	private CustomOnSeekbarListener chromaticDispersionBYListener;
	private CustomOnSeekbarListener timeListener;

	public static final float CHROMATIC_DISPERSION_RC_FACTOR = -150.0F;
	public static final float CHROMATIC_DISPERSION_BY_FACTOR = -150.0F;
	public SelectCrtMonitorEffectActivity INSTANCE;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_crt_monitor_effect);
		setUpActionBar();
		initializeControlElements();
		setUpActualContext();
		INSTANCE = this;
	}

	public void setUpActualContext()
	{
		PostProcessingEffectAttributContainer attributesContainer = LiveWallpaper.getInstance().getPostProcessingEffectAttributes(PostProcessingEffectsEnum.CRTMONITOR);

		if(attributesContainer == null)
		{
			return;
		}

		CrtMonitorAttributeContainer attributes = (CrtMonitorAttributeContainer) LiveWallpaper.getInstance().getPostProcessingEffectAttributes(
																						PostProcessingEffectsEnum.CRTMONITOR);
		mySwitch.setChecked(attributes.isEnabled());

		int progress1 = (int) (attributes.getChromaticDispersionBY() * CHROMATIC_DISPERSION_BY_FACTOR);
		seekBar1.setProgress(progress1);

		int progress2 = (int) (attributes.getChromaticDispersionRC() * CHROMATIC_DISPERSION_RC_FACTOR);
		seekBar2.setProgress(progress2);
	}

	private void initializeControlElements()
	{
		Button button = (Button)findViewById(R.id.button);
		button.setOnClickListener(myButtonListener);

		//Switch
		mySwitch = (Switch) findViewById(R.id.switch1);

		//ChromaticDispersionBY
		seekBar1 = (SeekBar) findViewById(R.id.seekBar1);
		chromaticDispersionBYListener = new CustomOnSeekbarListener(CHROMATIC_DISPERSION_BY_FACTOR);
		seekBar1.setOnSeekBarChangeListener(chromaticDispersionBYListener);

		//ChromaticDispersionRC
		seekBar2 = (SeekBar) findViewById(R.id.seekBar2);
		chromaticDispersionRCListener = new CustomOnSeekbarListener(CHROMATIC_DISPERSION_RC_FACTOR);
		seekBar2.setOnSeekBarChangeListener(chromaticDispersionRCListener);
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

			CrtMonitorAttributeContainer crtMonitorAttributeContainer = new CrtMonitorAttributeContainer();
			crtMonitorAttributeContainer.setChromaticDispersionBY(chromaticDispersionBYListener.getAttribute());
			crtMonitorAttributeContainer.setChromaticDispersionRC(chromaticDispersionRCListener.getAttribute());

			if(mySwitch.isChecked())
			{
				crtMonitorAttributeContainer.setEnabled(true);
				LiveWallpaper.getInstance().activatePostProcessingEffect(crtMonitorAttributeContainer);
			}
			else
			{
				crtMonitorAttributeContainer.setEnabled(false);
				LiveWallpaper.getInstance().deactivatePostProcessingEffect(crtMonitorAttributeContainer);
			}
			INSTANCE.onBackPressed();
		}
	};

}
