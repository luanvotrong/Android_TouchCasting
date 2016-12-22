package com.luanvotrong.ConnectMgr;


import android.content.Context;
import android.util.Log;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;

import java.io.IOException;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;


public class ConnectMgr {
    private String TAG = "Lulu ConnectMgr";
    private String m_serviceName = "TouchCasting";
    private enum TYPE {
        NONE,
        SHOUTER,
        FINDER
    }
    private TYPE m_type = TYPE.NONE;

    public void startShouter() {
        switch (m_type) {
            case NONE:
                break;
            case SHOUTER:
                break;
            case FINDER:
                break;
        }

        m_type = TYPE.SHOUTER;
    }

    public void initListener() {
        switch (m_type) {
            case NONE:
                break;
            case SHOUTER:
                break;
            case FINDER:
                break;
        }

        m_type = TYPE.FINDER;
    }
}