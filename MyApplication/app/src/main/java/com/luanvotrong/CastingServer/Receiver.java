package com.luanvotrong.CastingServer;

import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;

import com.luanvotrong.CastingServer.CastingMgr;
import com.luanvotrong.ConnectMgr.Listener;

import java.io.DataInputStream;
import java.net.Socket;
import java.util.ArrayList;

public class Receiver {
    private int m_udpPort = 63678;
    private int m_tcpPort = 63679;
    private String TAG = "Lulu Receiver";
    private String m_serviceName = "TouchCasting";
    private Listener m_listener;
    private CastingMgr m_castingMgr;
    private Thread m_connectThread;
    private Thread m_receiverThread;
    private Socket m_socket;
    private ArrayList<String> m_touches;

    private class ConnectWorker implements Runnable {
        private long m_last;

        public ConnectWorker() {
            m_last = System.currentTimeMillis();
        }

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                if (System.currentTimeMillis() - m_last > 1000) {
                    if (m_listener.getState() == Listener.STATE.LISTENED) {
                        onFoundCaster();
                    }
                }
            }
        }
    }

    private class ReceiverWorker implements Runnable {

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    DataInputStream dis = new DataInputStream(m_socket.getInputStream());
                    String mess = dis.readUTF();
                    String[] infos = mess.split(":");
                    synchronized (m_touches) {
                        m_touches.add(mess);
                        Log.d(TAG, mess);
                    }
                } catch(Exception e) {
                    Log.d(TAG, e.toString());
                }
            }
        }
    }

    public ArrayList<String> getTouches() {
        return m_touches;
    }

    public void onFoundCaster() {
        m_listener.stopListening();
        m_connectThread.interrupt();
        try {
            m_socket = new Socket(m_listener.getShouterAddress(), m_tcpPort);
            m_castingMgr.resetDimension();
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }

        m_receiverThread = new Thread(new ReceiverWorker());
        m_receiverThread.start();
    }

    public void start(CastingMgr castingMgr) {
        m_castingMgr = castingMgr;
        m_touches = new ArrayList<String>();

        if (m_listener == null) {
            m_listener = new Listener();
        }
        m_listener.startListening();

        m_connectThread = new Thread(new ConnectWorker());
        m_connectThread.start();
    }

    public void stop() {

    }
}