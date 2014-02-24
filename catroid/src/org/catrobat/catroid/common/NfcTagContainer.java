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
package org.catrobat.catroid.common;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.BroadcastScript;
import org.catrobat.catroid.content.WhenNfcScript;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NfcTagContainer {

    private static ArrayAdapter<String> messageAdapter = null;
    private static Map<String, List<WhenNfcScript>> receiverMap = new HashMap<String, List<WhenNfcScript>>();

    private NfcTagContainer() {
        throw new AssertionError();
    }

    public static ArrayAdapter<String> getMessageAdapter(Context context) {
        if (messageAdapter == null) {
            messageAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item);
            messageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            messageAdapter.add(context.getString(R.string.new_nfc_tag));
            if (receiverMap.isEmpty()) {
                addMessage(context.getString(R.string.brick_when_nfc_default_all));
            } else {
                for (String message : receiverMap.keySet()) {
                    addMessageToAdapter(message);
                }
            }
        }
        return messageAdapter;
    }


    public static void addMessage(String message) {
        if (message == null || message.isEmpty()) {
            return;
        }

        if (!receiverMap.containsKey(message)) {
            receiverMap.put(message, new ArrayList<WhenNfcScript>());
            addMessageToAdapter(message);
        }
    }


    private static void addMessageToAdapter(String message) {
        if (messageAdapter != null) {
            if (messageAdapter.getPosition(message) < 0) {
                messageAdapter.add(message);
            }
        }
    }

    public static int getPositionOfMessageInAdapter(Context context, String tagName) {
        if (messageAdapter == null) {
            getMessageAdapter(context);
        }
        return messageAdapter.getPosition(tagName);
    }

}
