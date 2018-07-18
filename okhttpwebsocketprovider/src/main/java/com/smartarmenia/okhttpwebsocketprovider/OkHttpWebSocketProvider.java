package com.smartarmenia.okhttpwebsocketprovider;

import com.smartarmenia.dotnetcoresignalrclientjava.provider.BaseSocketProvider;
import com.smartarmenia.dotnetcoresignalrclientjava.provider.SignalRWebSocketCallbacks;
import com.smartarmenia.dotnetcoresignalrclientjava.provider.SignalRWebSocketClient;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class OkHttpWebSocketProvider extends BaseSocketProvider<OkHttpClient.Builder> {

    private final OkHttpClient.Builder mOkHttpClientBuilder;

    public OkHttpWebSocketProvider(OkHttpClient.Builder okHttpClientBuilder) {
        mOkHttpClientBuilder = okHttpClientBuilder;
    }

    @Override
    public SignalRWebSocketClient createSocketClient(String serverUri, Map<String, String> httpHeaders, int connectTimeout, SignalRWebSocketCallbacks callbacks) throws Exception {
        return new OkHttpSignalRWebSocketClient(serverUri, httpHeaders, connectTimeout, mOkHttpClientBuilder, callbacks);
    }

    static class OkHttpSignalRWebSocketClient implements SignalRWebSocketClient {
        private static final int NORMAL_CLOSURE_STATUS = 1000;

        private final OkHttpSignalRWebSocketListener mWebSocketCallbacks;
        private final okhttp3.OkHttpClient mOkHttpClient;
        private final okhttp3.Request mRequest;
        private final AtomicReference<SocketStatus> mSocketStatus = new AtomicReference<>();

        private okhttp3.WebSocket mWebSocket;

        public OkHttpSignalRWebSocketClient(String serverUri, Map<String, String> httpHeaders, int connectTimeout, OkHttpClient.Builder okHttpClient, SignalRWebSocketCallbacks webSocketCallbacks) {
            mWebSocketCallbacks = new OkHttpSignalRWebSocketListener(webSocketCallbacks, mSocketStatus);
            okHttpClient.connectTimeout(connectTimeout, TimeUnit.MILLISECONDS);
            okHttpClient.readTimeout(connectTimeout, TimeUnit.MILLISECONDS);
            okHttpClient.writeTimeout(connectTimeout, TimeUnit.MILLISECONDS);
            mOkHttpClient = okHttpClient.build();
            Request.Builder builder = new Request.Builder().url(serverUri);
            for (String header : httpHeaders.keySet()) {
                builder.header(header, httpHeaders.get(header));
            }
            mRequest = builder.build();
        }

        @Override
        public void connect() {
            mSocketStatus.set(SocketStatus.CONNECTING);
            mWebSocket = mOkHttpClient.newWebSocket(mRequest, mWebSocketCallbacks);
        }

        @Override
        public void close() {
            mWebSocket.close(NORMAL_CLOSURE_STATUS, "");
            mOkHttpClient.dispatcher().executorService().shutdown();
            mOkHttpClient.connectionPool().evictAll();
        }

        @Override
        public void send(String text) {
            mWebSocket.send(text);
        }

        @Override
        public boolean isConnecting() {
            return SocketStatus.CONNECTING == mSocketStatus.get();
        }

        @Override
        public boolean isOpen() {
            return SocketStatus.OPENED == mSocketStatus.get();
        }

        @Override
        public boolean isClosing() {
            return SocketStatus.CLOSING == mSocketStatus.get();
        }

        @Override
        public boolean isClosed() {
            return SocketStatus.CLOSED == mSocketStatus.get();
        }

        private static class OkHttpSignalRWebSocketListener extends WebSocketListener {

            private final SignalRWebSocketCallbacks mWebSocketCallbacks;
            private final AtomicReference<SocketStatus> mSocketStatus;

            private OkHttpSignalRWebSocketListener(SignalRWebSocketCallbacks mWebSocketCallbacks, AtomicReference<SocketStatus> mSocketStatus) {
                this.mWebSocketCallbacks = mWebSocketCallbacks;
                this.mSocketStatus = mSocketStatus;
            }

            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                mSocketStatus.set(SocketStatus.OPENED);
                mWebSocketCallbacks.onOpen();
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                mWebSocketCallbacks.onMessage(text);
            }

            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                mSocketStatus.set(SocketStatus.CLOSING);
                mWebSocketCallbacks.onClosing(code, reason);
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                mSocketStatus.set(SocketStatus.CLOSED);
                mWebSocketCallbacks.onClose(code, reason);
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                mWebSocketCallbacks.onError(t);
            }
        }

        private enum SocketStatus {
            CONNECTING, OPENED, CLOSING, CLOSED
        }
    }
}
