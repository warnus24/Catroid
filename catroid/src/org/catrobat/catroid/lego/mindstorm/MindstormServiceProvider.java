/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.lego.mindstorm;

import java.util.HashMap;

public class MindstormServiceProvider {

	private MindstormServiceProvider() {}

	private static HashMap<Class<?>, Object> services = new HashMap<Class<?>, Object>();

	public static <T> void register(T service, Class<?> type) {
		services.put(type, service);
	}

	public static <T> void register(T service) {
		services.put(service.getClass(), service);
	}

    public static void unregister (Class<?> serviceType) {
        services.remove(serviceType);
    }

	public static <T> T resolve(Class<T> type) {
		Object service = services.get(type);
		if (service != null)
			return (T)service;
		return null;
	}
}
