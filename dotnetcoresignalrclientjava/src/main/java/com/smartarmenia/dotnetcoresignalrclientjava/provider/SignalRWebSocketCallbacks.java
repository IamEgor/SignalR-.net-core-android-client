package com.smartarmenia.dotnetcoresignalrclientjava.provider;

public interface SignalRWebSocketCallbacks {

    void onOpen();

    void onMessage(String message);

    void onClosing(int code, String reason);

    void onClose(int code, String reason);

    void onError(Throwable ex);
}
