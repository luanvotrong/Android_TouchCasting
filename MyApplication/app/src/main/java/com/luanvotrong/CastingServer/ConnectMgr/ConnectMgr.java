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

public class ConnectMgr {
    private int m_udpPort = 63678;
    private int m_tcpPort = 63679;
    private String TAG = "Lulu ConnectMgr";
    private String m_serviceName = "TouchCasting";
    private ServerSocket m_serverSocket;
    private ArrayList<Socket> m_waitingClients = new ArrayList<Socket>();

}