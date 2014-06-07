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

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class TCPClient {
	private final static int CONNECTION_TIMEOUT = 3000;
	private BufferedReader in;
	private DataOutputStream out;
	private Socket socket;

	public void connect(String dstName, int dstPort) throws UnknownHostException, IOException {
		socket = new Socket();
		socket.connect(new InetSocketAddress(dstName, dstPort), CONNECTION_TIMEOUT);
		out = new DataOutputStream(socket.getOutputStream());
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	}

	public void close() throws IOException {
		if (socket != null) {
			socket.close();
		}
		if (out != null) {
			out.close();
		}
		if (in != null) {
			in.close();
		}
	}

	public synchronized String readUntilString(String text) throws IOException {
		StringBuilder sb = new StringBuilder();
		int chr = -1;

		if (in == null) {
			throw new IOException();
		}

		while ((chr = in.read()) > -1) {
			sb.append((char) chr);

			if (sb.toString().contains(text)) {
				break;
			}
		}
		return sb.toString();
	}

	public synchronized void write(byte[] b) throws IOException {
		if (out != null) {
			out.write(b);
		} else {
			throw new IOException();
		}
	}
}
