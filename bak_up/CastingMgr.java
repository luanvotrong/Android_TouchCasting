package com.luanvotrong.CastingServer;


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
import java.util.ArrayList;


public class CastingMgr {
    private int m_udpPort = 63678;
    private int m_tcpPort = 63677;
    private MainActivity m_context;
    private TouchesPool m_touchesPool;
    private ArrayList<String> m_touches = new ArrayList<String>();
    private float m_screenW;
    private float m_screenH;

    public CastingMgr(MainActivity context, TouchesPool touchesPool) {
        m_context = context;
        m_touchesPool = touchesPool;

        Display display = m_context.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        m_screenW = size.x;
        m_screenH = size.y;
    }

    public void initCaster() {
        m_broadReceiver = new Thread(new Broadcaster());
        m_broadReceiver.start();
    }

    public void initReceiver() {
        m_receiver = new Thread(new Receiver());
        m_receiver.start();

        m_touchesInjector = new Thread(new TouchesInjector());
        m_touchesInjector.start();
    }

    public void destroy() {
        if(m_broadReceiver != null) {
            m_broadReceiver.interrupt();
            m_broadReceiver = null;
        }

        if(m_touchesInjector != null) {
            m_touchesInjector.interrupt();
            m_touchesInjector = null;
        }

        if(m_receiver != null) {
            m_receiver.interrupt();
            m_receiver = null;
        }
    }

    //////////////////////////////////////////////Touch Injector//////////////////////////////////////
    private class TouchesInjector implements Runnable {
        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                if (m_touches.size() > 0) {
                    String touch = m_touches.get(m_touches.size() - 1);
                    if(touch != null) {
                        String[] infos = touch.split(":");
                        long downTime = SystemClock.uptimeMillis();
                        long eventTime = SystemClock.uptimeMillis() + 0;
                        int metaState = 0;
                        MotionEvent motionEvent = MotionEvent.obtain(
                                downTime,
                                eventTime,
                                Integer.parseInt(infos[2]),
                                Float.parseFloat(infos[0]) * m_screenW,
                                Float.parseFloat(infos[1]) * m_screenH,
                                metaState
                        );

                        m_context.dispatchTouchEvent(motionEvent);
                    }
                    m_touches.remove(m_touches.size() - 1);
                }
            }
        }
    }

    Thread m_touchesInjector;

    //////////////////////////////////////////////Touch Broadcaster//////////////////////////////////////
    private class Broadcaster implements Runnable {
        private String TAG = "Lulu BroadCaster";

        private InetAddress getBroadcastAddress() throws IOException {
            WifiManager wifi = (WifiManager) m_context.getSystemService(Context.WIFI_SERVICE);
            DhcpInfo dhcp = wifi.getDhcpInfo();
            if (dhcp == null) {
                Log.d(TAG, "fuck no DHCP");
            }

            int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
            byte[] quads = new byte[4];
            for (int k = 0; k < 4; k++) {
                quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
            }

            return InetAddress.getByAddress(quads);
        }

        @Override
        public void run() {
            try {
                DatagramSocket s = new DatagramSocket();
                InetAddress local = getBroadcastAddress();
                while (!Thread.currentThread().isInterrupted()) {
                    Touch touch = m_touchesPool.GetTouch();
                    if (touch != null) {
                        float pX = touch.m_x / m_screenW;
                        float pY = touch.m_y /m_screenH;
                        String mess = "" + pX + ":" + pY + ":" + touch.m_type;
                        int msg_length = mess.length();
                        byte[] message = mess.getBytes();
                        DatagramPacket p = new DatagramPacket(message, msg_length, local, m_udpPort);
                        s.send(p);
                        Log.d(TAG, "sent");
                    }
                }
                s.close();
            } catch (Exception e) {
                Log.d(TAG, e.toString());
            }
        }
    }

    private Thread m_broadReceiver;

    //////////////////////////////////////////////Touch Broadcaster//////////////////////////////////////
    private class Receiver implements Runnable {
        private String TAG = "Lulu Receiver";

        @Override
        public void run() {
            try {
                try {
                    DatagramSocket s = new DatagramSocket(m_udpPort);
                    s.setBroadcast(true);
                    while (!Thread.currentThread().isInterrupted()) {
                        byte[] message = new byte[1500];
                        DatagramPacket p = new DatagramPacket(message, message.length);
                        s.receive(p);
                        String mess = new String(message, 0, p.getLength());
                        m_touches.add(mess);
                        Log.d(TAG, mess);
                    }
                    s.close();
                } catch (Exception e) {
                    Log.d("Lulu", e.toString());
                }
            } catch (Exception e) {
                Log.d("Lulu", e.toString());
            }
        }
    }

    private Thread m_receiver;
}