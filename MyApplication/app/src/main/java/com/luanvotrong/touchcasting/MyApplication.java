package com.luanvotrong.touchcasting;

import android.app.Application;
import android.content.Context;

/**
 * Created by luan.votrong on 12/22/2016.
 */

public class MyApplication extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    public static Context getContext() {
        return context;
    }
}
