package info.narazaki.android.lib.agent.http.task;

import info.narazaki.android.lib.agent.http.HttpTaskAgentInterface;
import info.narazaki.android.lib.list.ListUtils;
import info.narazaki.android.lib.system.MigrationConst;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;

import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.RedirectHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultRedirectHandler;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;

public abstract class HttpTaskBase implements Runnable {
    static private final String TAG = "HttpTaskBase";

    private String request_uri_;
    private HttpTaskAgentInterface agent_;

    private AbstractHttpClient http_client_;
    private final RedirectHandler redirect_handler_;
    private volatile String real_request_uri_;

    protected int buf_size_;

    private HttpRequestBase req_;

    abstract protected boolean sendRequest(String request_uri) throws InterruptedException, ClientProtocolException,
    IOException;

    abstract protected void onConnectionError(boolean connection_failed);

    abstract protected void onInterrupted();

    public String getRequestUri() {
        return request_uri_;
    }

    public String getRealRequestUri() {
        return real_request_uri_;
    }

    protected void setRequestUri(final String request_uri) {
        request_uri_ = request_uri;
        real_request_uri_ = request_uri;
    }

    protected String getTextEncode() {
        return "UTF-8";
    }

    private class LocalRedirectHandler extends DefaultRedirectHandler {
        @Override
        public URI getLocationURI(final HttpResponse response, final HttpContext context) throws ProtocolException {
            if (response.containsHeader("Location")) {
                real_request_uri_ = response.getFirstHeader("Location").getValue();
            }
            return super.getLocationURI(response, context);
        }
    }

    public HttpTaskBase(final String request_uri) {
        request_uri_ = request_uri;
        real_request_uri_ = request_uri;
        agent_ = null;
        http_client_ = null;
        req_ = null;
        buf_size_ = MigrationConst.getDefaultBufSize(); // TODO
        redirect_handler_ = new LocalRedirectHandler();
    }

    public void setBufSize(final int buf_size) {
        buf_size_ = buf_size;
    }

    public final void setHttpClient(final HttpTaskAgentInterface agent, final AbstractHttpClient http_client) {
        agent_ = agent;
        http_client_ = http_client;
    }

    protected void finish() {
        if (agent_ != null)
            agent_.onHttpTaskFinished(this);
        agent_ = null;
        http_client_ = null;
    }

    public final void abort() {
        if (req_ != null) {
            req_.abort();
            req_ = null;
        }
    }

    @Override
    public final void run() {
        boolean connection_failed = false;
        try {
            execRequests();
            return;
        } catch (final InterruptedException e) {
            e.printStackTrace();
            onInterrupted();
            finish();
            return;
        } catch (final ClientProtocolException e) {
            e.printStackTrace();
            connection_failed = true;
        } catch (final IOException e) {
            e.printStackTrace();
            connection_failed = true;
        } catch (final Exception e) {
            e.printStackTrace();
        }

        onConnectionError(connection_failed);
        finish();
    }

    protected final void execRequests() throws ClientProtocolException, InterruptedException, IOException {
        if (sendRequest(request_uri_)) {
            finish();
        }
    }

    public static String urlencode(final String orig, final String encode) {
        try {
            return URLEncoder.encode(orig, encode);
        } catch (final UnsupportedEncodingException e) {
        }
        return orig;
    }

    protected final String urlencode(final String orig) {
        return urlencode(orig, getTextEncode());
    }

    public void sendTo(final HttpTaskAgentInterface http_agent) {
        http_agent.send(this);
    }

    public HttpGet factoryGetRequest(final String request_uri) {
        final URI uri = URI.create(request_uri);
        final HttpGet req = new HttpGet(uri);

        setCredentials(req, uri);

        return req;
    }

    public HttpPost factoryPostRequest(final String request_uri) {
        final URI uri = URI.create(request_uri);
        final HttpPost req = new HttpPost(uri);

        setCredentials(req, uri);

        return req;
    }

    protected HttpResponse executeRequest(final HttpRequestBase req) throws ClientProtocolException, IOException {
        if (!agent_.isOnline())
            throw new IOException();
        http_client_.setRedirectHandler(redirect_handler_);
        return http_client_.execute(req);
    }

    public void setCredentials(final HttpRequestBase req, final URI uri) {
        boolean no_auth = true;
        if (uri.getUserInfo() != null) {
            final String user_info = uri.getUserInfo();
            final String[] list = ListUtils.split(":", user_info);
            if (list.length == 2) {
                final HttpParams param = req.getParams();
                param.getBooleanParameter(ClientPNames.HANDLE_AUTHENTICATION, true);
                req.setParams(param);

                final Credentials cred = new UsernamePasswordCredentials(list[0], list[1]);
                http_client_.getCredentialsProvider().setCredentials(new AuthScope(uri.getHost(), uri.getPort()), cred);
                no_auth = false;
            }
        }
        if (no_auth) {
            http_client_.getCredentialsProvider().clear();
        }
    }
}
