package org.catrobat.catroid.web;

import android.os.Bundle;
import android.os.ResultReceiver;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.ResponseBody;

import org.catrobat.catroid.common.Constants;

import java.io.IOException;

import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

public class ProgressResponseBody extends ResponseBody {

	public static final String TAG_PROGRESS = "currentDownloadProgress";
	public static final String TAG_ENDOFFILE = "endOfFileReached";
	public static final String TAG_NOTIFICATION_ID = "notificationId";

	private int notificationId;
	private long fileSize;
	private ResultReceiver receiver;
	private Source source;

	public ProgressResponseBody(long fileSize, ResultReceiver receiver,	int notificationId, Source source) throws IOException {
		this.fileSize = fileSize;
		this.receiver = receiver;
		this.notificationId = notificationId;
		this.source = source;
	}

	@Override
	public MediaType contentType() {
		return null;
	}

	@Override
	public long contentLength() {
		return fileSize;
	}

	@Override
	public BufferedSource source() {
		return Okio.buffer(source(source));
	}

	private Source source(Source source) {
		return new ForwardingSource(source) {
			long totalBytesWritten = 0L;
			@Override
			public long read(Buffer sink, long byteCount) throws IOException {
				totalBytesWritten += byteCount;
				sendUpdateIntent((100 * totalBytesWritten) / fileSize, false);
				return super.read(sink, byteCount);
			}

			@Override
			public void close() throws IOException {
				super.close();
				sendUpdateIntent(100, true);
			}
		};
	}

	private void sendUpdateIntent(long progress, boolean endOfFileReached) {
		Bundle progressBundle = new Bundle();
		progressBundle.putLong(TAG_PROGRESS, progress);
		progressBundle.putBoolean(TAG_ENDOFFILE, endOfFileReached);
		progressBundle.putInt(TAG_NOTIFICATION_ID, notificationId);
		receiver.send(Constants.UPDATE_DOWNLOAD_PROGRESS, progressBundle);
	}
}
