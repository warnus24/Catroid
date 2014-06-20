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
package org.catrobat.catroid.soundrecorder;

import android.media.MediaRecorder;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.IOException;

public class SoundRecorder {

	public static final String RECORDING_EXTENSION = ".m4a";
	private MediaRecorder recorder;
	private boolean isRecording;

	private String path;

	public SoundRecorder(String path) {
        Log.d("Lausi", "SoundRecorder_Constructor");
		this.recorder = new MediaRecorder();
		this.path = path;
	}

	public void start() throws IOException, IllegalStateException {
        Log.d("Lausi", "SoundRecorder_Start");
		File soundFile = new File(path);
		if (soundFile.exists()) {
			soundFile.delete();
		}
		File directory = soundFile.getParentFile();
		if (!directory.exists() && !directory.mkdirs()) {
			throw new IOException("Path to file could not be created.");
		}

		recorder.reset();
		recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
		recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
		recorder.setOutputFile(path);
		recorder.prepare();
		recorder.start();
		isRecording = true;
	}

	public void stop() throws IOException {
        Log.d("Lausi", "SoundRecorder_Stop");
        //recorder.stop();
        Log.d("Lausi", "SoundRecorder_Stop1");
        recorder.reset();
        Log.d("Lausi", "SoundRecorder_Stop2");
        recorder.release();
        Log.d("Lausi", "SoundRecorder_Stop3");
		isRecording = false;
        Log.d("Lausi", "SoundRecorder_Stop_end");
	}

	public Uri getPath() {
		return Uri.fromFile(new File(path));
	}

	public int getMaxAmplitude() {
		return recorder.getMaxAmplitude();
	}

	public boolean isRecording() {
		return isRecording;
	}

}
