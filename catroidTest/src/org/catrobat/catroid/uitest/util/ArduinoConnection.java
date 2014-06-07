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

package org.catrobat.catroid.uitest.util;

import android.nfc.NdefMessage;

import org.catrobat.catroid.nfc.NfcHandler;

import java.io.IOException;
import java.net.UnknownHostException;

public class ArduinoConnection {
	private String dstName;
	private int port;

	private static String COMMAND_EMULATION_POSTFIX = "_NFC_EMULATION";
	private static String COMMAND_EMULATION_STARTED = "STARTED" + COMMAND_EMULATION_POSTFIX;
	private static String COMMAND_EMULATION_FINISHED = "FINISHED" + COMMAND_EMULATION_POSTFIX;
	private static String COMMAND_EMULATION_TIMEDOUT = "TIMEDOUT" + COMMAND_EMULATION_POSTFIX;

	private enum serialCommandPrefix {
		NFC_EMULATE
	};

	private TCPClient tcpClient;

	public ArduinoConnection(String dstName, int port) {
		tcpClient = new TCPClient();
		this.dstName = dstName;
		this.port = port;
	}

	private synchronized void connect() throws UnknownHostException, IOException {
		tcpClient.connect(dstName, port);
	}

	private synchronized void close() {
		try {
			tcpClient.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void writeln(serialCommandPrefix cmd, String string) throws IOException {
		try {
			tcpClient.write(String.format("%X%s\n", cmd.ordinal(), string).getBytes());

		} catch (IOException e) {
			tcpClient.close();
			throw e;
		}
	}

	private String readUntilString(String text) throws IOException {
		try {
			return tcpClient.readUntilString(text);

		} catch (IOException e) {
			tcpClient.close();
			throw e;
		}
	}

	public synchronized boolean nfcEmulateTag(int uid, boolean tagWriteable) throws IOException {
		return nfcEmulateTag(uid, tagWriteable, null);
	}

	public synchronized boolean nfcEmulateTag(int uid, boolean tagWriteable, NdefMessage msg) throws IOException {
		connect();

		uid = uid & 0xffffff; // first byte is fixed to 0x08

		int writeAble = tagWriteable ? 0 : 1;

		String ndefMessage = "";

		if (msg != null) {
			byte[] ndefByteArray = msg.toByteArray();
			ndefMessage = String.format("%04X%s", ndefByteArray.length, NfcHandler.byteArrayToHex(ndefByteArray));
		}
		writeln(serialCommandPrefix.NFC_EMULATE, String.format("%X%06X%s", writeAble, uid, ndefMessage));

		readUntilString(COMMAND_EMULATION_STARTED);
		String x = tcpClient.readUntilString(COMMAND_EMULATION_POSTFIX);

		//System.out.println("result: " + x);

		close();

		if (x.contains(COMMAND_EMULATION_FINISHED)) {
			return true;
		} else if (x.contains(COMMAND_EMULATION_TIMEDOUT)) {
			return false;
		} else {
			throw new IllegalStateException();
		}

	}
}
