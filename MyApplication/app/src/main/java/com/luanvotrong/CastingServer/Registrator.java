package com.luanvotrong.CastingServer;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.SystemClock;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Registrator {
    private String TAG = "Lulu Registrator";

    private int m_udpPort = 63678;
    private int m_tcpPort = 63679;
    private String m_serviceName = "TouchCasting";
    private Thread m_registratingThread;
    private Context m_context;

    public void startRegistration(Context context) {
        m_context = context;
        m_registratingThread = new Thread(new RegistatingWorker());
        m_registratingThread.start();
    }

    public void stopRegistration() {
        if (m_registratingThread != null) {
            m_registratingThread.interrupt();
            m_registratingThread = null;
        }
    }

    private class RegistatingWorker implements Runnable {
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

        public RegistatingWorker() {
            m_last = System.currentTimeMillis();
        }

        @Override
        public void run() {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    DatagramSocket s = new DatagramSocket();
                    InetAddress local = getBroadcastAddress();
                    while (!Thread.currentThread().isInterrupted()) {
                        if(System.currentTimeMillis() - m_last > 500) {
                            int msg_length = m_serviceName.length();
                            byte[] message = m_serviceName.getBytes();
                            DatagramPacket p = new DatagramPacket(message, msg_length, local, m_udpPort);
                            s.send(p);
                            m_last = System.currentTimeMillis();
                        }
                    }
                    s.close();
                }
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        }
    }
}