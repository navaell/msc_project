package com.example.msc;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import org.zeromq.ZMQ;

public class ZeroMQMessageTask extends AsyncTask<String, Void, String> {
    private final Handler uiThreadHandler;
    private final Runnable onMessageSent;

    public ZeroMQMessageTask(Handler uiThreadHandler) {
        this.uiThreadHandler = uiThreadHandler;
        this.onMessageSent = () -> {};
    }

    public ZeroMQMessageTask(Handler uiThreadHandler, @NonNull Runnable onMessageSent) {
        this.uiThreadHandler = uiThreadHandler;
        this.onMessageSent = onMessageSent;
    }

    @Override
    protected String doInBackground(String... params) {
        ZMQ.Context context = ZMQ.context(1);
        ZMQ.Socket socket = context.socket(ZMQ.REQ);
        socket.connect("tcp://192.168.0.31:5558");

        socket.send(params[0].getBytes(), 0);
        String result = new String(socket.recv(0));

        socket.close();
        context.term();

        onMessageSent.run();
        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        Message message = new Message();
        message.setTarget(uiThreadHandler);
        message.obj=result;
        uiThreadHandler.sendMessage(message);
    }
}