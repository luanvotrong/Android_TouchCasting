package com.luanvotrong.ConnectMgr;

import android.util.Log;

import com.luanvotrong.Utilities.Define;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Finder {
    private String TAG = "Lulu Finder";
    private String m_serviceName = "TouchCasting";
    private Thread m_listenThread;
    private FinderCallback m_finderCallback;

    public Finder() {

    }

    public void start() {
        try {
            m_listenThread = new Thread(new Listener());
            m_listenThread.start();
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    public void stop() {
        try {
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
                    DatagramSocket s = new DatagramSocket(Define.PORT_SHOUTING_UDP);
                    s.setBroadcast(true);
                    DatagramPacket p = new DatagramPacket(message, message.length);
                    s.receive(p);
                    String mess = new String(message, 0, p.getLength());
                    if (mess.contains(m_serviceName)) {
                        m_finderCallback.onFoundBeacon(mess, p.getAddress());
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        }
    }
}