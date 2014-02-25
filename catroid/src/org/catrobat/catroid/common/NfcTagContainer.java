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

import org.catrobat.catroid.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NfcTagContainer {
    private static ArrayAdapter<String> tagNameAdapter = null;
    private static List<String> tagNameList = new ArrayList<String>();
    private static Map<Double,String> mapUidToTagName = new HashMap<Double, String>();

    private NfcTagContainer() {
        throw new AssertionError();
    }

    public static ArrayAdapter<String> getMessageAdapter(Context context) {
        if (tagNameAdapter == null) {
            tagNameAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, tagNameList);
            tagNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            if (tagNameList.isEmpty()) {
                addTagName(context.getString(R.string.new_nfc_tag));
                addTagName(context.getString(R.string.brick_when_nfc_default_all));
            }
        }
        return tagNameAdapter;
    }


    public static void addTagName(String tagName) {
        if (tagName == null || tagName.isEmpty()) {
            return;
        }

        if(!tagNameList.contains(tagName)){
            tagNameList.add(tagName);
        }
    }


    public static int getPositionOfMessageInAdapter(Context context, String tagName) {
        if (tagNameAdapter == null) {
            getMessageAdapter(context);
        }
        return tagNameAdapter.getPosition(tagName);
    }

    public static String getNameForUid(Double uid) {
        return mapUidToTagName.get(uid);
    }
}
