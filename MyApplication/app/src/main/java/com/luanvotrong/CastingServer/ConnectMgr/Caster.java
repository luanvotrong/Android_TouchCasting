package com.luanvotrong.CastingServer.ConnectMgr;


import android.content.Context;
import android.graphics.Point;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.SystemClock;
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

public class Caster {
    private int m_udpPort = 63678;
    private int m_tcpPort = 63679;
    private String TAG = "Lulu Caster";
    private String m_serviceName = "TouchCasting";
    private Shouter m_shouter;
    private ServerSocket m_serverSocket;
    private ArrayList<Socket> m_receiverSockets;

    private Thread m_serverSocketThread;
    private Thread m_castingThread;

    private class ServerSocketWorker implements Runnable {

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Socket socket = m_serverSocket.accept();
                    m_receiverSockets.add(socket);
                } catch (Exception e) {
                    Log.d(TAG, e.toString());
                }
            }
        }
    }

    private class CastingWorker implements Runnable {

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                for (int i = 0; i < m_receiverSockets.size(); i++) {
                    //Send instruction;
                }
            }
        }
    }

    public void start(Context context) {
        try {
            m_serverSocket = new ServerSocket(m_tcpPort);
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }

        if (m_shouter == null) {
            m_shouter = new Shouter();
        }
        m_shouter.startRegistration(context);

        m_serverSocketThread = new Thread(new ServerSocketWorker());
        m_serverSocketThread.start();

        m_castingThread = new Thread(new CastingWorker());
        m_castingThread.start();
    }

    public void stop() {
        m_shouter.stopRegistration();
        m_serverSocketThread.interrupt();
        m_serverSocketThread = null;
        m_castingThread.interrupt();
        m_serverSocketThread = null;
    }
}