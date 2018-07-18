package com.smartarmenia.javawebsocketprovider;

import com.smartarmenia.dotnetcoresignalrclientjava.provider.BaseSocketProvider;
import com.smartarmenia.dotnetcoresignalrclientjava.provider.SignalRWebSocketCallbacks;
import com.smartarmenia.dotnetcoresignalrclientjava.provider.SignalRWebSocketClient;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

public abstract class JavaWebSocketProvider extends BaseSocketProvider<WebSocketClient> {

    protected SignalRWebSocketCallbacks mWebSocketCallbacks;

    @Override
    public SignalRWebSocketClient createSocketClient(String serverUri, Map<String, String> httpHeaders, int connectTimeout, SignalRWebSocketCallbacks callbacks) throws URISyntaxException {
        URI uri = new URI(serverUri);
        JavaSignalRWebSocketClient socketClient = new JavaSignalRWebSocketClient(uri, new Draft_6455(), httpHeaders, connectTimeout).setWebSocketCallbacks(callbacks);
        return (SignalRWebSocketClient) modifySocketImpl(socketClient);
    }

    static class JavaSignalRWebSocketClient extends WebSocketClient implements SignalRWebSocketClient {

        private SignalRWebSocketCallbacks mWebSocketCallbacks;

        public JavaSignalRWebSocketClient(URI serverUri) {
            super(serverUri);
        }

        public JavaSignalRWebSocketClient(URI serverUri, Draft protocolDraft) {
            super(serverUri, protocolDraft);
        }

        public JavaSignalRWebSocketClient(URI serverUri, Map<String, String> httpHeaders) {
            super(serverUri, httpHeaders);
        }

        public JavaSignalRWebSocketClient(URI serverUri, Draft protocolDraft, Map<String, String> httpHeaders) {
            super(serverUri, protocolDraft, httpHeaders);
        }

        public JavaSignalRWebSocketClient(URI serverUri, Draft protocolDraft, Map<String, String> httpHeaders, int connectTimeout) {
            super(serverUri, protocolDraft, httpHeaders, connectTimeout);
        }

        public JavaSignalRWebSocketClient setWebSocketCallbacks(SignalRWebSocketCallbacks webSocketCallbacks) {
            mWebSocketCallbacks = webSocketCallbacks;
            return this;
        }

        @Override
        public void onOpen(ServerHandshake handshakedata) {
            if (mWebSocketCallbacks != null) {
                mWebSocketCallbacks.onOpen();
            }
        }

        @Override
        public void onMessage(String message) {
            if (mWebSocketCallbacks != null) {
                mWebSocketCallbacks.onMessage(message);
            }
        }

        @Override
        public void onClose(int code, String reason, boolean remote) {
            if (mWebSocketCallbacks != null) {
                mWebSocketCallbacks.onClose(code, reason);
            }
        }

        @Override
        public void onClosing(int code, String reason, boolean remote) {
            if (mWebSocketCallbacks != null) {
                mWebSocketCallbacks.onClosing(code, reason);
            }
        }

        @Override
        public void onError(Exception ex) {
            if (mWebSocketCallbacks != null) {
                mWebSocketCallbacks.onError(ex);
            }
        }
    }
}
