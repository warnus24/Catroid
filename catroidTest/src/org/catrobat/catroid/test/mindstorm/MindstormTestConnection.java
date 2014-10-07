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

package org.catrobat.catroid.test.mindstorm;

import android.os.Handler;

import org.catrobat.catroid.lego.mindstorm.MindstormCommand;
import org.catrobat.catroid.lego.mindstorm.MindstormConnection;
import org.catrobat.catroid.lego.mindstorm.nxt.Command;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by gerulf on 07.10.14.
 */
public class MindstormTestConnection extends MindstormConnection{
	private	Queue<MindstormCommand> sentCommands;


	public MindstormTestConnection() {
		super(null);
		this.sentCommands = new LinkedList<MindstormCommand>();
	}

	@Override
	public void send(MindstormCommand command) {

		//Datenstruktur, welche im NXTTest ausgelesen wird
		this.sentCommands.add(command);


	}

	@Override
	protected byte[] receive() {

		return null;
	}

	public MindstormCommand getLastSentCommand(){

		return this.sentCommands.poll();
	}


}
