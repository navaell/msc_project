package com.example.msc;

import android.os.Handler;
import android.os.Message;

import org.zeromq.SocketType;
import org.zeromq.ZMQ;
import org.zeromq.ZContext;

public class ZeroMQServer implements Runnable {
    private final Handler uiThreadHandler;

    public ZeroMQServer(Handler uiThreadHandler) {
        this.uiThreadHandler = uiThreadHandler;
    }

    @Override
    public void run() {
        try (ZContext context = new ZContext()) {
            // Socket to talk to clients
            ZMQ.Socket socket = context.createSocket(SocketType.REP);
            socket.bind("tcp://127.0.0.1:5555");

            while (!Thread.currentThread().isInterrupted()) {
                byte[] msg = socket.recv(0);
                Message message = new Message();
                message.setTarget(uiThreadHandler);
                message.obj=new String(msg);

                uiThreadHandler.sendMessage(message);
                socket.send(msg, 0);
            }
            socket.close();
        }
    }
}