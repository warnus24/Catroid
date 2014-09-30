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

package org.catrobat.catroid.test.livewallpaper;

import android.content.Intent;
import android.os.IBinder;
import android.test.ServiceTestCase;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;

import org.catrobat.catroid.livewallpaper.LiveWallpaper;

/**
 * Created by White on 24.09.2014.
 */
public class LiveWallpaperTest extends ServiceTestCase<LiveWallpaper> {

	// Contains an Intent used to start the service
	Intent mStartServiceIntent;

	// Contains a handle to the system alarm service
	LiveWallpaper mService;

	public LiveWallpaperTest() {
		super(LiveWallpaper.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		// Sets up an intent to start the service under test
		mStartServiceIntent = new Intent(this.getSystemContext(), LiveWallpaper.class);
	}

	@Override
	protected void tearDown() throws Exception {
		// Always call the super constructor when overriding tearDown()
		super.tearDown();
	}

	/**
	 * Tests the service's onCreate() method. Starts the service using startService(Intent)
	 */
	public void testServiceCreate() {
		// Starts the service under test
		this.startService(mStartServiceIntent);

		// Gets a handle to the service under test.
		mService = this.getService();
		LiveWallpaper liveWallpaper = (LiveWallpaper) mService;

		// Asserts that the Notification Manager was created in the service under test.
		assertNotNull("Live Wallpaper should not be null", liveWallpaper);
	}

	/**
	 * The name 'test preconditions' is a convention to signal that if this
	 * test doesn't pass, the test case was not set up properly and it might
	 * explain any and all failures in other tests.  This is not guaranteed
	 * to run before other tests, as junit uses reflection to find the tests.
	 */
	@SmallTest
	public void testPreconditions() {
	}

	/**
	 * Test basic startup/shutdown of Service
	 */
	@SmallTest
	public void testStartable() {
		Intent startIntent = new Intent();
		startIntent.setClass(getContext(), LiveWallpaper.class);
		startService(startIntent);
	}

	/**
	 * Test binding to service
	 */
	@MediumTest
	public void testBindable() {
		Intent startIntent = new Intent();
		startIntent.setClass(getContext(), LiveWallpaper.class);
		IBinder service = bindService(startIntent);
	}
}
