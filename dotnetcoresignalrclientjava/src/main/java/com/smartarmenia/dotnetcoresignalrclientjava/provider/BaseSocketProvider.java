package com.smartarmenia.dotnetcoresignalrclientjava.provider;

import java.util.Map;

public abstract class BaseSocketProvider<T> {

    protected T modifySocketImpl(T socketImpl) {
        return socketImpl;
    }

    public abstract SignalRWebSocketClient createSocketClient(String serverUri, Map<String, String> httpHeaders, int connectTimeout, SignalRWebSocketCallbacks callbacks) throws Exception;
}
