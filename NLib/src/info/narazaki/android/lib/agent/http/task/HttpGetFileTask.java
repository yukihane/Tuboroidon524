package info.narazaki.android.lib.agent.http.task;

import info.narazaki.android.lib.text.TextUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpRequestBase;

import android.util.Log;

public class HttpGetFileTask extends HttpTaskBase {
    private static final String TAG = "HttpGetFileTask";
    public static final int DEFAULT_PROGRESS_INTERVAL = 300;

    static public interface Callback {
        void onCompleted();

        void onFailed();

        void onStart();

        void onProgress(int current_length, int content_length);
    }

    private Callback callback_;
    private final File save_file_;
    private File temp_file_;

    public HttpGetFileTask(final String request_uri, final File save_file, final Callback callback) {
        super(request_uri);
        save_file_ = save_file;
        callback_ = callback;

        try {
            temp_file_ = File.createTempFile(save_file_.getName(), ".tmp", save_file_.getParentFile());
        } catch (final IOException e) {
            e.printStackTrace();
            temp_file_ = save_file_;
        }
    }

    @Override
    protected void onConnectionError(final boolean connectionFailed) {
        try {
            temp_file_.delete();
        } catch (final SecurityException e) {
        }
        if (callback_ != null)
            callback_.onFailed();
        callback_ = null;
    }

    @Override
    protected void onInterrupted() {
        onConnectionError(false);
    }

    @Override
    protected boolean sendRequest(final String request_uri) throws InterruptedException, ClientProtocolException, IOException {
        if (Thread.interrupted())
            throw new InterruptedException();

        callback_.onStart();

        final FileOutputStream fos = new FileOutputStream(temp_file_);

        final HttpRequestBase req = factoryGetRequest(request_uri);
        final HttpResponse res = executeRequest(req);

        final StatusLine statusLine = res.getStatusLine();
        switch (statusLine.getStatusCode()) {
        case HttpStatus.SC_OK:
        case HttpStatus.SC_CREATED:
        case HttpStatus.SC_NON_AUTHORITATIVE_INFORMATION:
            break;
        default:
            Log.i(TAG, "Request Failed Code:" + statusLine.getStatusCode());
            throw new IOException();
        }

        if (Thread.interrupted())
            throw new InterruptedException();

        final Header content_length_header = res.getFirstHeader("Content-Length");
        int content_length = 0;
        if (content_length_header != null) {
            final String content_length_str = content_length_header.getValue();
            try {
                content_length = TextUtils.parseInt(content_length_str);
            } catch (final Exception e) {
            }
        }

        final int bufsize = 1024 * 16;
        final InputStream is = new BufferedInputStream(res.getEntity().getContent(), bufsize);

        final byte[] buf = new byte[bufsize];
        int size = 0;
        int current_length = 0;
        long next_time = 0;

        while (true) {
            size = is.read(buf, 0, buf.length);
            if (size < 0)
                break;
            fos.write(buf, 0, size);
            current_length += size;

            final long current_time = System.currentTimeMillis();
            if (current_time > next_time) {
                callback_.onProgress(current_length, content_length);
                next_time = current_time + DEFAULT_PROGRESS_INTERVAL;
            }
        }

        fos.flush();
        fos.close();
        is.close();

        try {
            temp_file_.renameTo(save_file_);
        } catch (final SecurityException e) {
            throw new IOException();
        }

        callback_.onCompleted();
        callback_ = null;
        return true;
    }
}
