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

package org.catrobat.catroid.livewallpaper.postprocessing;

/**
 * Created by White on 29.08.2014.
 */
public class BloomAttributeContainer extends PostProcessingEffectAttributContainer
{
	private float baseInt = 1f;
	private float baseSat = .85f;
	private float bloomInt = 1.1f;
	private float bloomSat = .85f;
	private float threshold = .277f;

	public BloomAttributeContainer()
	{
		super();
		this.setType(PostProcessingEffectsEnum.BLOOM);
	}

	public float getBaseInt() {
		return baseInt;
	}

	public void setBaseInt(float baseInt) {
		this.baseInt = baseInt;
	}

	public float getBaseSat() {
		return baseSat;
	}

	public void setBaseSat(float baseSat) {
		this.baseSat = baseSat;
	}

	public float getBloomInt() {
		return bloomInt;
	}

	public void setBloomInt(float bloomInt) {
		this.bloomInt = bloomInt;
	}

	public float getBloomSat() {
		return bloomSat;
	}

	public void setBloomSat(float bloomSat) {
		this.bloomSat = bloomSat;
	}

	public float getThreshold() {
		return threshold;
	}

	public void setThreshold(float threshold) {
		this.threshold = threshold;
	}
}
