package com.luanvotrong.Utilities;

import java.net.InetAddress;

/**
 * Created by luan.votrong on 12/22/2016.
 */

public class HostInfo {
    public InetAddress inetAddress;
    public String name;
    public long countdown;

    public HostInfo(InetAddress inetAddress, String name) {
        this.inetAddress = inetAddress;
        this.name = name;
        resetCountdown();
    }

    public void update(float dt) {
        countdown -= dt;
    }

    public void resetCountdown() {
        countdown = System.currentTimeMillis();
    }

    public void setInetAddress(InetAddress inetAddress) {
        this.inetAddress = inetAddress;
    }

    public InetAddress getInetAddress() {
        return this.inetAddress;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public boolean isTimeout() {
        return countdown < 0;
    }
}