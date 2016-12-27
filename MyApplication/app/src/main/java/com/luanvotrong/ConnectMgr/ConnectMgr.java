package com.luanvotrong.ConnectMgr;

import android.content.AbstractThreadedSyncAdapter;
import android.util.Log;

import com.luanvotrong.Utilities.HostInfo;
import com.luanvotrong.touchcasting.MyApplication;
import com.luanvotrong.touchcasting.WrapperCallback;

import java.net.InetAddress;
import java.util.ArrayList;

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
    private Thread updateThread;

    public ConnectMgr() {
        listBeacon = new ArrayList<>();
        beacon = new Beacon();
        finder = new Finder(this);
    }

    public void startBeacon() {
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

        type = TYPE.SHOUTER;
    }

    public void stopBeacon() {
        beacon.stop();
        type = TYPE.NONE;
    }

    public void startFinder() {
        switch (type) {
            case NONE:
                updateThread = new Thread(new Updater());
                updateThread.start();
                finder.start();
                break;
            case SHOUTER:
                stopBeacon();
                updateThread = new Thread(new Updater());
                updateThread.start();
                finder.start();
                break;
            case FINDER:
                break;
        }

        type = TYPE.FINDER;
    }

    public ArrayList<HostInfo> getListBeacon() {
        return this.listBeacon;
    }

    public void stopFinder() {
        finder.stop();
        updateThread.interrupt();
        updateThread = null;
        type = TYPE.NONE;
    }

    @Override
    public void onFoundBeacon(String beaconName, InetAddress inetAddress) {
        HostInfo info = null;
        for (int i = 0, size = listBeacon.size(); i < size; ++i) {
            if (listBeacon.get(i).getInetAddress().getHostAddress().equals(inetAddress.getHostAddress())) {
                info = listBeacon.get(i);
            }
        }

        if (info == null) {
            info = new HostInfo(inetAddress, beaconName);
            listBeacon.add(info);
        } else {
            info.setName(beaconName);
            info.resetCountdown();
        }

        Log.d(TAG, "Found new ---------------------------------------------------");
        for (int i = 0, size = listBeacon.size(); i < size; ++i) {
            info = listBeacon.get(i);
            Log.d(TAG, "Name: " + info.getName() + " IP: " + info.getInetAddress().getHostAddress());
        }
        MyApplication.getUIWrapper().onUpdateServerList();
    }


    private class Updater implements Runnable {
        private long last;

        public Updater() {
            last = System.currentTimeMillis();
        }

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                long dt = System.currentTimeMillis() - last;
                if (dt > 1000) {
                    for (int i = 0, size = listBeacon.size(); i < size; i++) {
                        HostInfo info = listBeacon.get(i);
                        info.update(dt / 1000);
                        if(info.isTimeout()) {
                            listBeacon.remove(i);
                            i--;
                            size = listBeacon.size();
                            MyApplication.getUIWrapper().onUpdateServerList();
                        }
                    }
                    last += dt;
                }
            }
        }
    }
}