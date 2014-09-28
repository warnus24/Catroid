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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;

import com.actionbarsherlock.app.ActionBar;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.bitfire.postprocessing.demo.ResourceFactory;

import org.catrobat.catroid.R;
import org.catrobat.catroid.livewallpaper.LiveWallpaper;
import org.catrobat.catroid.livewallpaper.postprocessing.BloomAttributeContainer;
import org.catrobat.catroid.livewallpaper.postprocessing.PostProcessingEffectAttributContainer;
import org.catrobat.catroid.livewallpaper.postprocessing.PostProcessingEffectsEnum;
import org.catrobat.catroid.livewallpaper.postprocessing.VignetteAttributeContainer;
import org.catrobat.catroid.livewallpaper.postprocessing.VignetteGradientEnum;

public class SelectVignetteEffectActivity extends BaseActivity implements AdapterView.OnItemSelectedListener
{

	private BloomAttributeContainer attributes;
	private SeekBar seekBar1;
	private Spinner spinner;
	private Switch mySwitch;
	private CustomOnSeekbarListener intensityListener;
	String gradientString;
	VignetteGradientEnum gradientType;
	int gradientPosition;

	public static final float INTENSITY_FACTOR = 50.0F;
	public SelectVignetteEffectActivity INSTANCE;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_vignette_effect);
		setUpActionBar();
		initializeControlElements();
		setUpActualContext();
		INSTANCE = this;
	}

	public void setUpActualContext()
	{
		PostProcessingEffectAttributContainer attributesContainer = LiveWallpaper.getInstance().getPostProcessingEffectAttributes(PostProcessingEffectsEnum.VIGNETTE);

		if(attributesContainer == null)
		{
			return;
		}

		VignetteAttributeContainer attributes = (VignetteAttributeContainer) LiveWallpaper.getInstance().getPostProcessingEffectAttributes(
																						PostProcessingEffectsEnum.VIGNETTE);
		mySwitch.setChecked(attributes.isEnabled());

		int progress1 = (int) (attributes.getIntensity() * INTENSITY_FACTOR);
		seekBar1.setProgress(progress1);

		spinner.setSelection(attributes.getGradientPosition());
	}

	private void initializeControlElements()
	{
		Button button = (Button)findViewById(R.id.button);
		button.setOnClickListener(myButtonListener);

		//Switch
		mySwitch = (Switch) findViewById(R.id.switch1);

		//Base Int
		seekBar1 = (SeekBar) findViewById(R.id.seekBar1);
		intensityListener = new CustomOnSeekbarListener(INTENSITY_FACTOR);
		seekBar1.setOnSeekBarChangeListener(intensityListener);

		//Spinner
		spinner = (Spinner) findViewById(R.id.spinner);
		// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
				R.array.vignette_gradients_array, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		spinner.setAdapter(adapter);

		spinner.setOnItemSelectedListener(this);
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

			VignetteAttributeContainer vignetteAttributes = new VignetteAttributeContainer();
			vignetteAttributes.setIntensity(intensityListener.getAttribute());
			vignetteAttributes.setGradientType(gradientType);
			vignetteAttributes.setGradientPosition(gradientPosition);


			if(mySwitch.isChecked()){
				vignetteAttributes.setEnabled(true);
				LiveWallpaper.getInstance().activatePostProcessingEffect(vignetteAttributes);
			}
			else{
				vignetteAttributes.setEnabled(false);
				LiveWallpaper.getInstance().deactivatePostProcessingEffect(vignetteAttributes);
			}
			INSTANCE.onBackPressed();
		}
	};


	@Override
	public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
		gradientString = (String) adapterView.getItemAtPosition(i);
		gradientType = getGradient(gradientString);
		gradientPosition = i;
	}

	@Override
	public void onNothingSelected(AdapterView<?> adapterView) {
		gradientString = (String) adapterView.getItemAtPosition(0);
		gradientType = getGradient(gradientString);
		gradientPosition = 0;
	}

	public VignetteGradientEnum getGradient(String gradient)
	{
		if(gradient.equals(VignetteGradientEnum.CROSSPROCESSING.toString())){
			return VignetteGradientEnum.CROSSPROCESSING;
		}
		else if(gradient.equals(VignetteGradientEnum.SUNSET.toString())){
			return VignetteGradientEnum.SUNSET;
		}
		else if(gradient.equals(VignetteGradientEnum.CLOUDY.toString())){
			return VignetteGradientEnum.CLOUDY;
		}
		else if(gradient.equals(VignetteGradientEnum.VIVID.toString())){
			return VignetteGradientEnum.VIVID;
		}
		else if(gradient.equals(VignetteGradientEnum.GREENLAND.toString())){
			return VignetteGradientEnum.GREENLAND;
		}
		else if(gradient.equals(VignetteGradientEnum.MARS.toString())){
			return VignetteGradientEnum.MARS;
		}
		else if(gradient.equals(VignetteGradientEnum.MUDDY.toString())){
			return VignetteGradientEnum.MUDDY;
		}
		else if(gradient.equals(VignetteGradientEnum.NONE.toString())){
			return VignetteGradientEnum.NONE;
		}
		else{
			return VignetteGradientEnum.NONE;
		}
	}

}