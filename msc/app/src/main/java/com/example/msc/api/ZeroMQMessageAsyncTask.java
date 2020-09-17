package com.example.msc.api;

import android.os.AsyncTask;

import org.zeromq.ZMQ;

/**
 * AsyncTask used to communicate with the ZeroMQ server on the PyBullet side
 */
public class ZeroMQMessageAsyncTask extends AsyncTask<String, Void, String> {
    private final Runnable onMessageSent;

    /**
     * Constructor
     */
    public ZeroMQMessageAsyncTask() {
        this.onMessageSent = null;
    }

    /**
     * Constructor
     *
     * @param onMessageSent a {@link Runnable} which will be invoked once PyBullet has received
     *                      the message sent by this task
     */
    public ZeroMQMessageAsyncTask(Runnable onMessageSent) {
        this.onMessageSent = onMessageSent;
    }

    /**
     * Connects to PyBullet's ZeroMQ server and sends the json object provided as argument. This
     * happens asynchronously on a background thread to avoid blocking the UI (and making the app
     * unresponsive)
     *
     * @param params argument array containing the json object to be sent
     * @return the PyBullet response
     */
    @Override
    protected String doInBackground(String... params) {
        ZMQ.Context context = ZMQ.context(1);
        ZMQ.Socket socket = context.socket(ZMQ.REQ);
        socket.connect("tcp://192.168.0.31:5558");

        socket.send(params[0].getBytes(), 0);
        String result = new String(socket.recv(0));

        socket.close();
        context.term();

        if (onMessageSent != null) {
            onMessageSent.run();
        }
        return result;
    }
}