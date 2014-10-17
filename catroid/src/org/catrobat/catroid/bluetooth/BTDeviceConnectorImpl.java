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
package org.catrobat.catroid.bluetooth;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;

import org.catrobat.catroid.common.ServiceProvider;

import java.util.ArrayList;
import java.util.Collection;

public class BTDeviceConnectorImpl implements BTDeviceConnector {

	private Collection<BTDeviceService> runningBTServices = new ArrayList<BTDeviceService>();

	public void connectDevice(Class<? extends BTDeviceService> serviceToStart, Activity activity, int requestCode) {
		Intent intent = createStartIntent(serviceToStart, activity);
		activity.startActivityForResult(intent, requestCode);
	}

	@Override
	public void deviceConnected(BTDeviceService service) {
		runningBTServices.add(service);
	}

	public void disconnectDevices() {
		for (BTDeviceService service : runningBTServices) {
			service.disconnect();
			ServiceProvider.unregisterService(service.getServiceType());
		}

		runningBTServices.clear();
	}

	protected Intent createStartIntent(Class<? extends BTDeviceService> serviceToStart, Activity activity) {
		Intent intent = new Intent(activity, BTConnectDeviceActivity.class);
		intent.putExtra(BTConnectDeviceActivity.SERVICE_TO_START, serviceToStart);
		return intent;
	}

	@Override
	public void initialise() {
		for (BTDeviceService service : runningBTServices) {
			service.initialise();
		}
	}

	@Override
	public void start() {
		for (BTDeviceService service : runningBTServices) {
			service.start();
		}
	}

	@Override
	public void pause() {
		for (BTDeviceService service : runningBTServices) {
			service.pause();
		}
	}

	@Override
	public void destroy() {
		for (BTDeviceService service : runningBTServices) {
			service.destroy();
		}

		disconnectDevices();
	}
}
