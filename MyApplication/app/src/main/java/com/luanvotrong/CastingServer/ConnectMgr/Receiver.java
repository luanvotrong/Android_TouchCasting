package com.luanvotrong.CastingServer.ConnectMgr;


import android.content.Context;
import android.graphics.Point;
import android.net.DhcpInfo;
import android.net.sip.SipAudioCall;
import android.net.wifi.WifiManager;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Receiver {
    private int m_udpPort = 63678;
    private int m_tcpPort = 63679;
    private String TAG = "Lulu Receiver";
    private String m_serviceName = "TouchCasting";
    private Listener m_listener;
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
                    m_touches.add(mess);
                    Log.d(TAG, mess);
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
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }

        m_receiverThread = new Thread(new ReceiverWorker());
        m_receiverThread.start();
    }

    public void start() {
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