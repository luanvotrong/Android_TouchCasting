package com.luanvotrong.touchcasting;

import android.app.Application;
import android.content.Context;

import com.luanvotrong.CastingServer.CastMgr;
import com.luanvotrong.ConnectMgr.ConnectMgr;

/**
 * Created by luan.votrong on 12/22/2016.
 */

public class MyApplication extends Application {
    private static Context context;
    private static ConnectMgr connectMgr;
    private static CastMgr castMgr;
    private static Wrapper uiWrapper;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        connectMgr = new ConnectMgr();
        castMgr = new CastMgr();
        uiWrapper = new Wrapper();
    }

    public static Context getContext() {
        return context;
    }

    public static ConnectMgr getConnectMgr() {
        return connectMgr;
    }

    public static CastMgr getCastMgr() {
        return castMgr;
    }

    public static Wrapper getUIWrapper() {
        return uiWrapper;
    }
}
