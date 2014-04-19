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
package org.catrobat.catroid.ui.dialogs;


import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.util.Log;

import org.catrobat.catroid.common.NfcTagContainer;
import org.catrobat.catroid.nfc.NfcHandler;

public abstract class NewNfcTagDialog  extends TextDialog {
    public static final String TAG = NewNfcTagDialog.class.getSimpleName();
    private PendingIntent pendingIntent;

    private NfcAdapter nfcAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        pendingIntent = PendingIntent.getActivity(getActivity(), 0,
                new Intent(getActivity(), getActivity().getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        nfcAdapter = NfcAdapter.getDefaultAdapter(getActivity());
        Log.d(TAG, "onCreate()");

        if (nfcAdapter == null) {
            Log.d(TAG, "could not get nfc adapter :(");
        } else{
            // TODO: inform nfc not possible
        }

    }

    @Override
    public void onDestroyView() {
        if (getDialog() != null && getRetainInstance()) {
            getDialog().setOnDismissListener(null);
        }
        super.onDestroyView();
    }

    @Override
    protected String getHint() {
        return null;
    }

    @Override
    protected String getTitle() {
        return null;
    }

    @Override
    protected void onOkButtonHandled() {
        super.onOkButtonHandled();
        //getActivity().sendBroadcast(new Intent(ScriptActivity.ACTION_BRICK_LIST_CHANGED));
    }

    @Override
    public void onResume() {
        super.onResume();
        if (nfcAdapter != null) {
            Log.d(TAG, "onResume()enableForegroundDispatch()");
            nfcAdapter.enableForegroundDispatch(getActivity(), pendingIntent, null, null);
        }else{
            // TODO: inform nfc not possible
        }

        Log.d(TAG, "activity:" + getActivity().getClass().getSimpleName());
        Log.d(TAG, "got intent:" + getActivity().getIntent().getAction());
        String uid = NfcHandler.getUid(getActivity().getIntent());
        if(uid != null){
            NfcTagContainer.addTagName(uid, input.getText().toString().trim());
            // TODO: inform user that read nfc was successfull
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (nfcAdapter != null) {
            Log.d(TAG, "onPause()disableForegroundDispatch()");
            nfcAdapter.disableForegroundDispatch(getActivity());
        } else{
            // TODO: inform nfc not possible
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (nfcAdapter != null) {
            Log.d(TAG, "onStop()disableForegroundDispatch()");
            nfcAdapter.disableForegroundDispatch(getActivity());
        }
    }
}
