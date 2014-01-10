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
package org.catrobat.catroid.nfc;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.util.Log;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Sprite;

import java.math.BigInteger;
import java.util.List;

public class NfcHandler {
	private static final String TAG = NfcHandler.class.getSimpleName();
	private static NfcHandler INSTANCE = new NfcHandler();
	private Double uid;

	private NfcHandler() {
		resetUid();
	}

	public static NfcHandler getInstance() {
		return INSTANCE;
	}

	public void resetUid() {
		uid = 0.0;
	}

	public void processIntent(Intent intent) {
		if (intent == null) {
			return;
		}

		if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())
				|| NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())
				|| NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())) {
			Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

			byte[] byteId = tag.getId();

			uid = convertByteArrayToDouble(byteId);

			Log.d(TAG, "read successfull. uid = hex:" + byteArrayToHex(byteId) + " double:" + uid);

			// uncomment for debugging ndef information			
			//			Ndef ndefTag;
			//			if (null != (ndefTag = Ndef.get(tag))) {
			//				try {
			//					ndefTag.connect();
			//					NdefMessage ndefMessage = ndefTag.getNdefMessage();
			//					for (NdefRecord record : ndefMessage.getRecords()) {
			//						Log.d(TAG, "record, tnf: " + record.getTnf() + " " + new String(record.getPayload()));
			//					}
			//					ndefTag.close();
			//				} catch (IOException e) {
			//					e.printStackTrace();
			//				} catch (FormatException e) {
			//					e.printStackTrace();
			//				}
			//
			//			}
		}

		List<Sprite> spriteList = ProjectManager.getInstance().getCurrentProject().getSpriteList();
		for (Sprite sprite : spriteList) {
			sprite.createWhenNfcScriptAction();
		}
	}

	public static double convertByteArrayToDouble(byte[] byteId) {
		BigInteger n = new BigInteger(byteId);
		return n.doubleValue();
	}

	public static String byteArrayToHex(byte[] a) {
		StringBuilder sb = new StringBuilder();
		for (byte b : a) {
			sb.append(String.format("%02x", b & 0xff));
		}
		return sb.toString();
	}

	public Double getAndResetUid() {
		Double tmp = uid;
		resetUid();
		return tmp;
	}
}