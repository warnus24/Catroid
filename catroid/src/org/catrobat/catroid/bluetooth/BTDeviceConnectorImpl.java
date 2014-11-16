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
import android.content.Context;
import android.content.Intent;

import org.catrobat.catroid.common.ServiceProvider;

import java.util.HashSet;
import java.util.Set;

public class BTDeviceConnectorImpl implements BTDeviceConnector {


	private Set<BTDeviceService> connectedBTServices = new HashSet<BTDeviceService>();

	public synchronized void connectDevice(Class<? extends BTDeviceService> serviceToStart,
			Activity activity, int requestCode, boolean autoConnect) {


		BTDeviceService service = ServiceProvider.getService(serviceToStart);

		if (service != null) {
			service.disconnect();
			connectedBTServices.remove(service);
			ServiceProvider.unregisterService(serviceToStart);
		}

		Intent intent = createStartIntent(serviceToStart, activity, autoConnect);
		activity.startActivityForResult(intent, requestCode);
	}

	@Override
	public synchronized void deviceConnected(BTDeviceService service) {
		ServiceProvider.registerService(service.getServiceType(), service);
		connectedBTServices.add(service);
	}

	@Override
	public synchronized void disconnectDevices() {
		for (BTDeviceService service : connectedBTServices) {
			service.disconnect();
			ServiceProvider.unregisterService(service.getServiceType());
		}

		connectedBTServices.clear();
	}

	protected Intent createStartIntent(Class<? extends BTDeviceService> serviceToStart,
			Context context, boolean autoConnect) {
		Intent intent = new Intent(context, BTConnectDeviceActivity.class);
		intent.putExtra(BTConnectDeviceActivity.SERVICE_TO_START, serviceToStart);
		intent.putExtra(BTConnectDeviceActivity.AUTO_CONNECT, autoConnect);
		return intent;
	}

	@Override
	public synchronized void initialise() {
		for (BTDeviceService service : connectedBTServices) {
			service.initialise();
		}
	}

	@Override
	public synchronized void start() {
		for (BTDeviceService service : connectedBTServices) {
			service.start();
		}
	}

	@Override
	public synchronized void pause() {
		for (BTDeviceService service : connectedBTServices) {
			service.pause();
		}
	}

	@Override
	public synchronized void destroy() {
		for (BTDeviceService service : connectedBTServices) {
			service.destroy();
		}
	}
}
