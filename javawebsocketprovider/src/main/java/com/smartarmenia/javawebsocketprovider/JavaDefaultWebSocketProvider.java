package com.smartarmenia.javawebsocketprovider;

import org.java_websocket.client.WebSocketClient;

import java.io.IOException;

import javax.net.ssl.SSLSocketFactory;

public class JavaDefaultWebSocketProvider extends JavaWebSocketProvider {
    @Override
    protected WebSocketClient modifySocketImpl(WebSocketClient socketImpl) {
        if (socketImpl.getURI().getScheme().equals("https")) {
            try {
                socketImpl.setSocket(SSLSocketFactory.getDefault().createSocket());
            } catch (IOException e) {
                mWebSocketCallbacks.onError(e);
            }
        }
        return socketImpl;
    }
}
