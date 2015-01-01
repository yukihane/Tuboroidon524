package info.narazaki.android.lib.agent.http.task;

import org.apache.http.client.methods.HttpRequestBase;

public abstract class TextHttpPostTaskBase extends TextHttpTaskBase {

    public TextHttpPostTaskBase(final String request_uri) {
        super(request_uri);
    }

    @Override
    protected HttpRequestBase createHttpRequest(final String request_uri) {
        return factoryPostRequest(request_uri);
    }
}
