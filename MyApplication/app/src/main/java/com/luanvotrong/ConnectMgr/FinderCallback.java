package com.luanvotrong.ConnectMgr;

import java.net.InetAddress;

/**
 * Created by luan.votrong on 12/22/2016.
 */

public interface FinderCallback {
    public void onFoundBeacon(String beaconName, InetAddress inetAddress);
}
