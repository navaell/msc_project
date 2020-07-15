package com.example.msc;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import org.zeromq.ZMQ;

public class ZeroMQMessageTask extends AsyncTask<String, Void, String> {
    private final Handler uiThreadHandler;

    public ZeroMQMessageTask(Handler uiThreadHandler) {
        this.uiThreadHandler = uiThreadHandler;
    }

    @Override
    protected String doInBackground(String... params) {
        ZMQ.Context context = ZMQ.context(1);
        ZMQ.Socket socket = context.socket(ZMQ.REQ);
        socket.connect("tcp://10.0.2.2:5555");

        socket.send(params[0].getBytes(), 0);
        String result = new String(socket.recv(0));

        socket.close();
        context.term();

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