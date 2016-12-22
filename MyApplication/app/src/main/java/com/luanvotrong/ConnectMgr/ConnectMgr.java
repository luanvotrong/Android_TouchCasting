package com.luanvotrong.ConnectMgr;

import android.util.Log;

public class ConnectMgr {
    private String TAG = "Lulu ConnectMgr";
    private enum TYPE {
        NONE,
        SHOUTER,
        FINDER
    }
    private TYPE type = TYPE.NONE;
    private Beacon beacon;
    private Finder finder;

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
}