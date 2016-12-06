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

import com.luanvotrong.touchcasting.MainActivity;

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
    private Thread m_recieverThread;
    private Socket m_socket;

    private enum STATE {
        FINDING_RECEIVER,
        CONNECTING_RECEIVER,
        CONNECTED_RECEIVER
    }

    private STATE m_state;

    public void setState(STATE state) {
        m_state = state;
        switch (m_state) {
            case FINDING_RECEIVER:
                if (m_listener == null) {
                    m_listener = new Listener();
                }
                m_listener.startListening();

                m_recieverThread = new Thread(new ReceiverWorker());
                m_recieverThread.start();
                break;
            case CONNECTING_RECEIVER:
                m_listener.stopListening();
                m_recieverThread.interrupt();
                try {
                    m_socket = new Socket(m_listener.getShouterAddress(), m_tcpPort);
                } catch (Exception e) {
                    Log.d(TAG, e.toString());
                }
                m_state = STATE.CONNECTED_RECEIVER;
                break;
            case CONNECTED_RECEIVER:
                break;
        }
    }

    private class ReceiverWorker implements Runnable {
        private long m_last;

        public ReceiverWorker() {
            m_last = System.currentTimeMillis();
        }

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                if (System.currentTimeMillis() - m_last > 1000) {
                    if (m_listener.getState() == Listener.STATE.LISTENED) {
                        setState(STATE.CONNECTING_RECEIVER);
                    }
                }
            }
        }
    }

    public void start() {
        setState(STATE.FINDING_RECEIVER);
    }
}