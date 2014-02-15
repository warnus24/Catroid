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
package org.catrobat.catroid.stage;

import java.util.Observable;
import java.util.Observer;

public abstract class StageObserver implements Observer {
	public static enum ObservedEvent {
		STAGE_CREATE, STAGE_PAUSE, STAGE_RESUME, STAGE_DISPOSE
	}

	@Override
	public final void update(Observable observable, Object data) {
		if (data instanceof ObservedEvent) {
			ObservedEvent event = (ObservedEvent) data;
			switch (event) {
				case STAGE_CREATE:
					onStageCreate();
					break;
				case STAGE_PAUSE:
					onStagePause();
					break;
				case STAGE_RESUME:
					onStageResume();
					break;
				case STAGE_DISPOSE:
					onStageDispose();
					break;
			}
		}
	}

	public abstract void onStageCreate();

	public abstract void onStagePause();

	public abstract void onStageResume();

	public abstract void onStageDispose();

}
