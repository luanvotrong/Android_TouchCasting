package com.luanvotrong.CastingServer;

import android.util.Log;

import java.io.DataInputStream;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */


public class Client {
    public enum CONNECTION_STATE {
        LISTENING,
        REQUESTING,
        CONNECTED
    }

    private CONNECTION_STATE m_stateConnection;

    private int m_udpPort = 63678;
    private int m_tcpPort = 63677;
    private InetAddress m_serverAddress = null;

    private class UDPBroadcastListener implements Runnable {
        @Override
        public void run() {
            byte[] message = new byte[1500];

            try {
                DatagramSocket s = new DatagramSocket(m_udpPort);
                s.setBroadcast(true);

                try {
                    Log.d("Lulu", "Receivings mess");
                    DatagramPacket p = new DatagramPacket(message, message.length);
                    s.receive(p);
                    String mess = new String(message, 0, p.getLength());
                    m_serverAddress = p.getAddress();
                    Log.d("Lulu", "Ip server: " + m_serverAddress.getHostAddress());
                    Log.d("Lulu", "Received: " + mess);
                    s.close();

                    if (mess.length() > 0) {
                        setState(CONNECTION_STATE.REQUESTING);
                    }
                } catch (Exception e) {
                    Log.d("Lulu", e.toString());
                }
            } catch (Exception e) {
                Log.d("Lulu", e.toString());
            }
        }
    }

    Socket m_socket = null;

    public Socket getSocket() {
        return m_socket;
    }

    private class ClientThread implements Runnable {
        @Override
        public void run() {
            try {
                m_socket = new Socket(m_serverAddress, m_tcpPort);
                setState(CONNECTION_STATE.CONNECTED);
            } catch (Exception e) {
                Log.d("Lulu", e.toString());
            }
        }
    }

    private class ClientReceiveThread implements Runnable {
        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    InputStream in = m_socket.getInputStream();
                    DataInputStream dis = new DataInputStream(in);

                    int len = dis.readInt();
                    byte[] data = new byte[len];
                    if (len > 0) {
                        dis.readFully(data);
                    }
                } catch (Exception e) {
                    Log.d("Lulu", e.toString());
                }
            }
        }
    }

    public void setState(CONNECTION_STATE state) {
        m_stateConnection = state;
        switch (m_stateConnection) {
            case LISTENING:
                Log.d("Lulu", "Listening");
                m_serverAddress = null;
                //new UDPBroadcastListener().execute();
                new Thread(new UDPBroadcastListener()).start();
                break;
            case REQUESTING:
                Log.d("Lulu", "Requesting");
                new Thread(new ClientThread()).start();
                break;
            case CONNECTED:
                Log.d("Lulu", "Connected");
                new Thread(new ClientReceiveThread()).start();
                break;
        }
    }

    public void disconnect() {
        try {
            m_socket.close();
        } catch (Exception e) {
        }
    }
}