package com.luanvotrong.CastingServer;

import android.util.Log;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;

public class Listener {
    private String TAG = "Lulu Listener";

    private int m_udpPort = 63678;
    private int m_tcpPort = 63679;
    private String m_serviceName = "TouchCasting";

    private ArrayList<InetAddress> m_waitingRegistrators;
    private Thread m_listeningThread;

    public void startListening() {
        m_waitingRegistrators = new ArrayList<InetAddress>();
        m_listeningThread = new Thread(new ListenWorker());
        m_listeningThread.start();
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
                        InetAddress address = p.getAddress();
                        m_waitingRegistrators.add(address);
                    }
                }
            }
            catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        }
    }
}