package com.luanvotrong.ConnectMgr;


import android.content.Context;
import android.graphics.Point;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.SystemClock;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Listener {
    private String TAG = "Lulu Listener";
    private int m_udpPort = 63678;
    private int m_tcpPort = 63679;
    private String m_serviceName = "TouchCasting";
    private InetAddress m_shouterAddress;
    private Thread m_listeningThread;

    public enum STATE {
        LISTENING,
        LISTENED
    }
    private STATE m_state;

    public void setState(STATE state) {
        m_state = state;
    }

    public STATE getState() {
        return m_state;
    }

    public InetAddress getShouterAddress() {
        return m_shouterAddress;
    }

    public void startListening() {
        m_listeningThread = new Thread(new ListenWorker());
        m_listeningThread.start();

        setState(STATE.LISTENING);
    }

    public void stopListening() {
        if(m_listeningThread != null) {
            m_listeningThread.interrupt();
            m_listeningThread = null;
        }
    }

    private class ListenWorker implements Runnable {
        private String TAG = "Lulu ListenWorker";

        @Override
        public void run() {
            byte[] message = new byte[1500];
            try {
                while(!Thread.currentThread().isInterrupted()) {
                    DatagramSocket s = new DatagramSocket(m_udpPort);
                    s.setBroadcast(true);
                    DatagramPacket p = new DatagramPacket(message, message.length);
                    s.receive(p);
                    String mess = new String(message, 0, p.getLength());
                    if(mess.equalsIgnoreCase(m_serviceName)) {
                        m_shouterAddress = p.getAddress();
                        setState(STATE.LISTENED);
                    }
                }
            }
            catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        }
    }
}