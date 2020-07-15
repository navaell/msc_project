package com.example.msc;

import android.os.Handler;

import android.os.Message;

public class MessageListenerHandler extends Handler {
    public static final String PAYLOAD_KEY = "payload_key";

    private final IMessageListener messageListener;
    private final String payloadKey;

    public MessageListenerHandler(IMessageListener messageListener, String payloadKey) {
        this.messageListener = messageListener;
        this.payloadKey = payloadKey;
}

    @Override
    public void handleMessage(Message msg) {
        messageListener.messageReceived((String) msg.obj);
    }
}
