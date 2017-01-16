package com.luanvotrong.touchcasting;

import android.app.Activity;
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
    private static Activity activity;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        connectMgr = new ConnectMgr();
        castMgr = new CastMgr();
        uiWrapper = new Wrapper();
    }

    public static void setActivity(Activity activity) {
        MyApplication.activity = activity;
    }

    public static Activity getActivity() {
        return activity;
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


//public class MyApplication {
//    private static Context context;
//    private static ConnectMgr connectMgr;
//    private static CastMgr castMgr;
//    private static Wrapper uiWrapper;
//private static Activity activity;
//
//    public static void init(ConnectMgr connectMgr, CastMgr castMgr, Wrapper wrapper) {
//        MyApplication.connectMgr = connectMgr;
//        MyApplication.castMgr = castMgr;
//        MyApplication.uiWrapper = wrapper;
//    }

//    public static void setActivity(Activity activity) {
//        MyApplication.activity = activity;
//    }
//
//    public static Activity getActivity() {
//        return activity;
//    }
//
//    public static void setContext(Context context) {
//        MyApplication.context = context;
//    }
//
//    public static Context getContext() {
//        return context;
//    }
//
//    public static ConnectMgr getConnectMgr() {
//        return connectMgr;
//    }
//
//    public static CastMgr getCastMgr() {
//        return castMgr;
//    }
//
//    public static Wrapper getUIWrapper() {
//        return uiWrapper;
//    }
//}
