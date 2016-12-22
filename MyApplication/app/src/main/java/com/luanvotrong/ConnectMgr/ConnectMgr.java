package com.luanvotrong.ConnectMgr;

import android.util.Log;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class ConnectMgr implements FinderCallback {
    private String TAG = "Lulu ConnectMgr";
    private Map<InetAddress, String> listBeacon;

    private enum TYPE {
        NONE,
        SHOUTER,
        FINDER
    }

    private TYPE type = TYPE.NONE;
    private Beacon beacon;
    private Finder finder;

    public ConnectMgr() {
        listBeacon = new HashMap<>();
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
        listBeacon.put(inetAddress, beaconName);
        Log.d(TAG, "Found new ---------------------------------------------------");
        Set set = listBeacon.entrySet();
        Iterator it = set.iterator();
        while(it.hasNext()) {
            Map.Entry entry = (Map.Entry)it.next();
            Log.d(TAG, "key: " + entry.getKey() + " value: " + entry.getValue());
        }
    }
}