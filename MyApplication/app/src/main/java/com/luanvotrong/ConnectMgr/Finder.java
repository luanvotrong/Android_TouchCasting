package com.luanvotrong.ConnectMgr;

import android.util.Log;

import com.luanvotrong.Utilities.Define;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Finder {
    private String TAG = "Lulu Finder";
    private String m_serviceName = "TouchCasting";
    private InetAddress m_shouterAddress;
    private Thread m_listenThread;

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
        m_listenThread = new Thread(new Listener());
        m_listenThread.start();

        setState(STATE.LISTENING);
    }

    public void stopListening() {
        if(m_listenThread != null) {
            m_listenThread.interrupt();
            m_listenThread = null;
        }
    }

    private class Listener implements Runnable {
        private String TAG = "Lulu ListenWorker";

        @Override
        public void run() {
            byte[] message = new byte[1500];
            try {
                while(!Thread.currentThread().isInterrupted()) {
                    DatagramSocket s = new DatagramSocket(Define.PORT_SHOUTING_UDP);
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