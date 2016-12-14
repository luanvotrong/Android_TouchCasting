package com.luanvotrong.CastingServer.ConnectMgr;


import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.support.v4.app.ActivityCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.widget.Toast;

import com.luanvotrong.CastingServer.CastingMgr;
import com.luanvotrong.CastingServer.Touch;
import com.luanvotrong.CastingServer.TouchesPool;

import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Caster {
    private int m_udpPort = 63678;
    private int m_tcpPort = 63679;
    private String TAG = "Lulu Caster";
    private String m_serviceName = "TouchCasting";
    private Shouter m_shouter;
    private CastingMgr m_castingMgr;
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
                    m_castingMgr.resetDimension();
                    m_receiverSockets.add(socket);
                    ((Activity) m_context).runOnUiThread(new Runnable() {
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

                try {
                    Touch touch = m_touchesPool.GetTouch();
                    //Send instruction;
                    float pX = touch.m_x / m_screenW;
                    float pY = touch.m_y / m_screenH;
                    String mess = "" + pX + ":" + pY + ":" + touch.m_type;
                    for (int i = 0; i < m_receiverSockets.size(); i++) {
                        Socket socket = m_receiverSockets.get(i);
                        try {
                            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                            dos.writeUTF(mess);
                        } catch (Exception e) {

                        }
                    }
                    Log.d(TAG, "sent " + mess);
                } catch (Exception e) {

                }
            }
        }
    }

    public void start(Context context, TouchesPool touchesPool, CastingMgr castingMgr) {
        m_castingMgr = castingMgr;
        m_context = context;
        m_touchesPool = touchesPool;
        m_receiverSockets = new ArrayList<>();
        DisplayMetrics display = context.getResources().getDisplayMetrics();
        m_screenW = display.widthPixels;
        m_screenH = display.heightPixels;

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