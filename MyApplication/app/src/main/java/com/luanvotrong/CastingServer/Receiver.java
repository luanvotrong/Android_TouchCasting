package com.luanvotrong.CastingServer;

import android.util.Log;

import com.luanvotrong.ConnectMgr.Finder;

import java.io.DataInputStream;
import java.net.Socket;
import java.util.ArrayList;

public class Receiver {
    private int m_tcpPort = 63679;
    private String TAG = "Lulu Receiver";
    private String m_serviceName = "TouchCasting";
    private CastMgr castMgr;
    private Thread m_connectThread;
    private Thread m_receiverThread;
    private Socket m_socket;
    private ArrayList<String> m_touches;

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

    public void start(CastMgr castingMgr) {
        castMgr = castingMgr;
        m_touches = new ArrayList<String>();
    }
}