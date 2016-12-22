package com.luanvotrong.ConnectMgr;

import android.util.Log;

import com.luanvotrong.Utilities.HostInfo;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class ConnectMgr implements FinderCallback {
    private String TAG = "Lulu ConnectMgr";
    private ArrayList<HostInfo> listBeacon;

    private enum TYPE {
        NONE,
        SHOUTER,
        FINDER
    }

    private TYPE type = TYPE.NONE;
    private Beacon beacon;
    private Finder finder;

    public ConnectMgr() {
        listBeacon = new ArrayList<>();
        beacon = new Beacon();
        finder = new Finder(this);
    }

    public void startBeacon() {
        switch (type) {
            case NONE:
                finder.start();
                break;
            case SHOUTER:
                stopBeacon();
                finder.start();
                break;
            case FINDER:
                break;
        }

        type = TYPE.SHOUTER;
    }

    public void stopBeacon() {
        beacon.stop();
        type = TYPE.NONE;
    }

    public void startFinder() {
        switch (type) {
            case NONE:
                beacon.start();
                break;
            case SHOUTER:
                stopFinder();
                beacon.start();
                break;
            case FINDER:
                break;
        }

        type = TYPE.FINDER;
    }

    public void stopFinder() {
        finder.stop();
        type = TYPE.NONE;
    }

    @Override
    public void onFoundBeacon(String beaconName, InetAddress inetAddress) {
        Log.d(TAG, "Found new ---------------------------------------------------");
    }


    private class Updater implements Runnable {
        @Override
        public void run() {
            //Todo: implement check dead host
        }
    }
}