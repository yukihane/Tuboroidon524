package info.narazaki.android.lib.agent.http;

import info.narazaki.android.lib.agent.http.task.HttpTaskBase;
import info.narazaki.android.lib.system.NAndroidSystem;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.Future;

import org.apache.http.HttpHost;

import android.content.Context;
import android.net.Uri;

public class HttpMultiTaskAgent implements HttpTaskAgentInterface {
    private static final String TAG = "HttpMultiTaskAgent";
    final private Context context_;
    final private String user_agent_;
    final private HttpHost proxy_;

    private final HashMap<String, SoftReference<HttpTaskAgent>> agent_map_;

    public HttpMultiTaskAgent(final Context context, final String user_agent, final HttpHost proxy) {
        super();
        context_ = context;
        user_agent_ = user_agent;
        proxy_ = proxy;
        agent_map_ = new HashMap<String, SoftReference<HttpTaskAgent>>();
    }

    @Override
    public String getCookieStoreData() {
        return "";
    }

    private synchronized HttpTaskAgent getHttpAgent(final HttpTaskBase task) {
        final String host = Uri.parse(task.getRequestUri()).getHost();
        final SoftReference<HttpTaskAgent> agent_ref = agent_map_.get(host);
        if (agent_ref != null) {
            final HttpTaskAgent agent = agent_ref.get();
            if (agent != null)
                return agent;
            agent_map_.remove(host);
        }

        final HttpTaskAgent agent = new HttpTaskAgent(context_, user_agent_, proxy_);
        agent_map_.put(host, new SoftReference<HttpTaskAgent>(agent));
        return agent;
    }

    @Override
    public Future<?> send(final HttpTaskBase task) {
        return getHttpAgent(task).send(task);
    }

    @Override
    public void setCookieStoreData(final String cookieBareData) {
    }

    @Override
    public synchronized void clearCookieStore() {
        for (final Entry<String, SoftReference<HttpTaskAgent>> agent_set : agent_map_.entrySet()) {
            final SoftReference<HttpTaskAgent> agent_ref = agent_set.getValue();
            if (agent_ref != null) {
                final HttpTaskAgent agent = agent_ref.get();
                if (agent != null)
                    agent.clearCookieStore();
            }
        }
    }

    @Override
    public void onHttpTaskFinished(final HttpTaskBase task) {
    }

    @Override
    public boolean isOnline() {
        return NAndroidSystem.isOnline(context_);
    }
}
