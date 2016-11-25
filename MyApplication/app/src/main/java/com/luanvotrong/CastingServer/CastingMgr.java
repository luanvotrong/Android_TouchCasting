package com.luanvotrong.CastingServer;


import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.luanvotrong.touchcasting.MainActivity;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class CastingMgr {
    private int m_udpPort = 63678;
    private int m_tcpPort = 63677;
    private MainActivity m_context;
    private TouchesPool m_touchesPool;

    public CastingMgr(MainActivity context, TouchesPool touchesPool) {
        m_context = context;
        m_touchesPool = touchesPool;
    }

    public void initCaster() {
        m_broadReceiver = new Thread(new Broadcaster());
        m_broadReceiver.start();
    }

    public void initReceiver() {

    }

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
                    TouchesPool.Touch touch = m_touchesPool.GetTouch();
                    if(touch != null) {
                        String mess = "" + touch.m_x + ":" + touch.m_y;
                        int msg_length = mess.length();
                        byte[] message = mess.getBytes();

                        DatagramPacket p = new DatagramPacket(message, msg_length, local, m_udpPort);
                        s.send(p);
                    }
                }
            } catch (Exception e) {
                Log.d(TAG, e.toString());
            }
        }
    }
    private Thread m_broadReceiver;
}