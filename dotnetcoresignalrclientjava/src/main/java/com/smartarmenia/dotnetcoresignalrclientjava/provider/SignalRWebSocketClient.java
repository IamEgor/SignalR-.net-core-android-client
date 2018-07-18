package com.smartarmenia.dotnetcoresignalrclientjava.provider;

public interface SignalRWebSocketClient {

    void connect();

    void close();

    void send(String text);

    boolean isConnecting();

    boolean isOpen();

    boolean isClosing();

    boolean isClosed();
}
