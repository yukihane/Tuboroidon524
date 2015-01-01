package info.narazaki.android.tuboroid.agent.http;

import info.narazaki.android.lib.agent.http.HttpSingleTaskAgent;

import org.apache.http.HttpHost;

import android.content.Context;

public class HttpMaruTaskAgent extends HttpSingleTaskAgent {

    public HttpMaruTaskAgent(final Context context, final String userAgent, final HttpHost proxy) {
        super(context, userAgent, proxy);
    }
}
