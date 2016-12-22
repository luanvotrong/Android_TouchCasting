package com.luanvotrong.ConnectMgr;

import android.content.Context;
import android.util.Log;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;

import com.luanvotrong.Utilities.Define;
import com.luanvotrong.Utilities.Utilities;

import java.io.IOException;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;


public class Beacon {
    private String TAG = "Lulu Shouter";
    private String m_serviceName = Define.SERVICE_NAME;

    private Thread m_shoutThread;
    private Context m_context;
    private DatagramSocket m_datagramSocket;

    public void start() {
        m_context = Utilities.getContext();
        try {
            m_datagramSocket = new DatagramSocket();
            m_shoutThread = new Thread(new Shouter());
            m_shoutThread.start();
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    public void stop() {
        try {
            m_datagramSocket.close();
            m_shoutThread.interrupt();
            m_shoutThread = null;
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    private class Shouter implements Runnable {
        private String TAG = "Lulu RegistratingWorker";
        private long m_last = 0;

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

        public Shouter() {
            m_last = System.currentTimeMillis();
        }

        @Override
        public void run() {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    InetAddress local = getBroadcastAddress();
                    while (!Thread.currentThread().isInterrupted()) {
                        if (System.currentTimeMillis() - m_last > Define.INTERVAL_SHOUTING) {
                            int msg_length = m_serviceName.length();
                            byte[] message = m_serviceName.getBytes();
                            DatagramPacket p = new DatagramPacket(message, msg_length, local, Define.PORT_SHOUTING_UDP);
                            m_datagramSocket.send(p);
                            m_last = System.currentTimeMillis();
                        }
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        }
    }
}