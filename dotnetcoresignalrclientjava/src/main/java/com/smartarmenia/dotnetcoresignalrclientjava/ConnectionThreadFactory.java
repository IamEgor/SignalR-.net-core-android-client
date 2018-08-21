package com.smartarmenia.dotnetcoresignalrclientjava;

import java.util.concurrent.ThreadFactory;

public class ConnectionThreadFactory implements ThreadFactory {
    private static String CONNECTION_THREAD_NAME = "WebSocketHubConnectionThread";

    @Override
    public Thread newThread(Runnable r) {
        return new Thread(r, CONNECTION_THREAD_NAME);
    }
}