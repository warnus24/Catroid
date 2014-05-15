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
package org.catrobat.catroid.uitest.mindstorm.nxt;

import android.os.Handler;

import org.catrobat.catroid.lego.mindstorm.Mindstorm;
import org.catrobat.catroid.lego.mindstorm.MindstormConnection;
import org.catrobat.catroid.lego.mindstorm.nxt.NXTReply;

import java.util.ArrayList;
import java.util.Collection;

public class MindstormTestConnection extends MindstormConnection {

    ArrayList<NXTReply> receivedMessages;

    public MindstormTestConnection(Handler receiveHandler) {
        super(receiveHandler);

        receivedMessages = new ArrayList<NXTReply>();
    }

    @Override
    protected byte[] receive() {
        byte[] receivedBytes = super.receive();
        receivedMessages.add(new NXTReply(receivedBytes));
        return receivedBytes;
    }

    public ArrayList<NXTReply> getReceivedMessages() {
        return receivedMessages;
    }
}
