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

package org.catrobat.catroid.ui.fragment;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Bundle;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.formulaeditor.FormularEditorSensorAdapter;
import org.catrobat.catroid.formulaeditor.SensorHandler;
import org.catrobat.catroid.formulaeditor.SensorValuePair;
import org.catrobat.catroid.formulaeditor.Sensors;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class FormularEditorSensorFragment extends FormulaEditorListFragment implements SensorEventListener {

	private FormularEditorSensorAdapter sensorAdapter;
	private List<SensorValuePair> valuePairList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.actionBarTitle = getString(R.string.formula_editor_sensors);

		itemsIds = Constants.SENSOR_ITEMS;

		SensorHandler.startSensorListener(getActivity().getApplicationContext());
		SensorHandler.registerListener(this);

		valuePairList = new ArrayList<SensorValuePair>();

		for (int id : itemsIds) {
			valuePairList.add(new SensorValuePair(getString(id), " "));
		}

		sensorAdapter = new FormularEditorSensorAdapter(getActivity(),
				R.layout.fragment_formula_editor_sensor_list_item, valuePairList);

		this.setListAdapter(sensorAdapter);

	}

	@Override
	public void onSensorChanged(SensorEvent event) {

		DecimalFormat df = new DecimalFormat("0.00");

		valuePairList.get(0).setValue(df.format(SensorHandler.getSensorValue(Sensors.X_ACCELERATION)));
		valuePairList.get(1).setValue(df.format(SensorHandler.getSensorValue(Sensors.Y_ACCELERATION)));
		valuePairList.get(2).setValue(df.format(SensorHandler.getSensorValue(Sensors.Z_ACCELERATION)));
		valuePairList.get(3).setValue(df.format(SensorHandler.getSensorValue(Sensors.COMPASS_DIRECTION)));
		valuePairList.get(4).setValue(df.format(SensorHandler.getSensorValue(Sensors.X_INCLINATION)));
		valuePairList.get(5).setValue(df.format(SensorHandler.getSensorValue(Sensors.Y_INCLINATION)));
		valuePairList.get(6).setValue(df.format(SensorHandler.getSensorValue(Sensors.LOUDNESS)));

		sensorAdapter.notifyDataSetChanged();
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}

	@Override
	public void onHiddenChanged(boolean hidden) {

		if (hidden) {
			SensorHandler.unregisterListener(this);
			SensorHandler.stopSensorListeners();
		} else {
			SensorHandler.startSensorListener(getActivity().getApplicationContext());
			SensorHandler.registerListener(this);
		}

	}
}
