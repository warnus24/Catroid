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

import android.widget.SeekBar;

import org.catrobat.catroid.livewallpaper.postprocessing.PostProcessingEffectAttributContainer;

/**
 * Created by White on 30.08.2014.
 */
public class CustomOnSeekbarListener implements SeekBar.OnSeekBarChangeListener
{
	private float attribute;
	private float factor;

	public CustomOnSeekbarListener(float factor)
	{
		super();
		this.factor = factor;
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
		float progress = i;
		attribute = progress / factor;
	}

	public float getAttribute()
	{
		return attribute;
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {

	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {

	}
}
