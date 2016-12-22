package com.luanvotrong.Utilities;

import java.net.InetAddress;

/**
 * Created by luan.votrong on 12/22/2016.
 */

public class HostInfo {
    public InetAddress inetAddress;
    public String name;
    public float countdown;

    public HostInfo(InetAddress inetAddress, String name) {
        this.inetAddress = inetAddress;
        this.name = name;
        resetCountdown();
    }

    public void update(float dt) {
        countdown -= dt;
    }

    public void resetCountdown() {
        countdown = 10;
    }
}