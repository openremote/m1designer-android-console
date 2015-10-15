package org.openremote.android;

import com.squareup.okhttp.*;
import gumi.builders.UrlBuilder;
import org.openremote.android.util.UrlUtil;
import org.openremote.shared.Constants;
import org.openremote.shared.event.Event;
import org.openremote.shared.event.bus.EventBus;
import org.openremote.shared.event.bus.EventListener;

import java.io.IOException;
import java.net.SocketException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public abstract class AbstractEventListener<E extends Event> implements EventListener<E> {

    private static final Logger LOG = Logger.getLogger(AbstractEventListener.class.getName());

    public static final String LOG_MESSAGE_CANCELLED = "The request was cancelled.";
    public static final String LOG_MESSAGE_SOCKET_CLOSED = "The socket is closed.";

    public abstract class ResponseCallback implements Callback {

        @Override
        public void onFailure(Request request, IOException ex) {
            LOG.fine("On response failure: " + request.urlString() + " => " + ex);
            if (ex != null && "Canceled".equals(ex.getMessage())) {
                LOG.fine(LOG_MESSAGE_CANCELLED);
                return;
            }

            if (ex != null && ex instanceof SocketException && "Socket closed".equals(ex.getMessage())) {
                LOG.fine(LOG_MESSAGE_SOCKET_CLOSED);
                return;
            }

            onFailure(null, null, ex);
        }

        @Override
        public void onResponse(Response response) throws IOException {
            LOG.fine("On response: " + response.request().urlString() + " => " + response.code());
            if (!response.isSuccessful()) {
                onFailure(response.code(), response.message(), null);
            } else {
                onSuccess(response.code(), response.message(), response.body());
            }
        }

        abstract protected void onSuccess(Integer responseCode, String responseMessage, ResponseBody body);

        abstract protected void onFailure(Integer responseCode, String responseMessage, IOException ex);
    }

    final protected String controllerUrl;
    final protected OkHttpClient httpClient;

    public AbstractEventListener(String controllerUrl) {
        this.controllerUrl = controllerUrl;
        this.httpClient = new OkHttpClient();
        this.httpClient.setConnectTimeout(5, TimeUnit.SECONDS);
        this.httpClient.setWriteTimeout(60, TimeUnit.SECONDS);
        this.httpClient.setReadTimeout(60, TimeUnit.SECONDS);
    }

    public String getControllerUrl() {
        return controllerUrl;
    }

    public OkHttpClient getHttpClient() {
        return httpClient;
    }

    public Request.Builder request(UrlBuilder urlBuilder) {
        return new Request.Builder()
            .url(urlBuilder.toUrl())
            .addHeader("Accept", "application/json");
    }

    public UrlBuilder resource(String... pathSegments) {
        return UrlUtil.url(controllerUrl, Constants.REST_SERVICE_CONTEXT_PATH, pathSegments);
    }

    public void enqueue(Request.Builder requestBuilder, ResponseCallback responseCallback) {
        Request request = requestBuilder.build();
        LOG.fine("Enqueuing request: " + request.method() + " " + request.urlString());
        getHttpClient()
            .newCall(request)
            .enqueue(responseCallback);
    }

    public void dispatch(Event event) {
        EventBus.dispatch(event);
    }

}
