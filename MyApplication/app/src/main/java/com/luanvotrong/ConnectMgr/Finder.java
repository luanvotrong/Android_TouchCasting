package com.luanvotrong.ConnectMgr;

import android.util.Log;

import com.luanvotrong.Utilities.Define;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class Finder {
    private String TAG = "Lulu Finder";
    private String m_serviceName = Define.SERVICE_NAME;
    private Thread m_listenThread;
    private FinderCallback finderCallback;
    private DatagramSocket datagramSocket;

    public Finder(FinderCallback finderCallback) {
        this.finderCallback = finderCallback;
    }

    public void start() {
        try {
            datagramSocket = new DatagramSocket(Define.PORT_SHOUTING_UDP);
            m_listenThread = new Thread(new Listener());
            m_listenThread.start();
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    public void stop() {
        try {
            datagramSocket.close();
            m_listenThread.interrupt();
            m_listenThread = null;
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    private class Listener implements Runnable {
        private String TAG = "Lulu ListenWorker";

        @Override
        public void run() {
            byte[] message = new byte[1500];
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    datagramSocket.setBroadcast(true);
                    DatagramPacket p = new DatagramPacket(message, message.length);
                    datagramSocket.receive(p);
                    String mess = new String(message, 0, p.getLength());
                    Log.d(TAG, "received mess: " + mess);
                    if (mess.contains(m_serviceName)) {
                        finderCallback.onFoundBeacon(mess, p.getAddress());
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        }
    }
}