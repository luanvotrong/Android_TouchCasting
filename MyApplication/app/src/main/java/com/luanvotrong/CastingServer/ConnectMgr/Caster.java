package com.luanvotrong.CastingServer.ConnectMgr;


import android.content.Context;
import android.graphics.Point;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.SystemClock;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.widget.Toast;

import com.luanvotrong.CastingServer.Touch;
import com.luanvotrong.CastingServer.TouchesPool;
import com.luanvotrong.touchcasting.MainActivity;

import java.io.DataOutputStream;
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
    private TouchesPool m_touchesPool;
    private float m_screenW;
    private float m_screenH;
    private Thread m_serverSocketThread;
    private Thread m_castingThread;
    private Context m_context;

    private class ServerSocketWorker implements Runnable {

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Socket socket = m_serverSocket.accept();
                    m_receiverSockets.add(socket);
                    ((MainActivity)m_context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(m_context, "Connected " + m_receiverSockets.size() + " client", Toast.LENGTH_LONG).show();
                        }
                    });
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
                    Touch touch = m_touchesPool.GetTouch();
                    if (touch != null) {
                        float pX = touch.m_x / m_screenW;
                        float pY = touch.m_y / m_screenH;
                        String mess = "" + pX + ":" + pY + ":" + touch.m_type;
                        Socket socket = m_receiverSockets.get(i);
                        try {
                            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                            dos.writeUTF(mess);
                        } catch (Exception e) {

                        }
                        Log.d(TAG, "sent");
                    }
                }
            }
        }
    }

    public void start(MainActivity context, TouchesPool touchesPool) {
        m_context = context;
        m_touchesPool = touchesPool;
        m_receiverSockets = new ArrayList<>();
        Display display = context.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        m_screenW = size.x;
        m_screenH = size.y;

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