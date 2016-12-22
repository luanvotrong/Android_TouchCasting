package com.luanvotrong.Utilities;

import android.content.Context;
import android.os.Build;

import com.luanvotrong.touchcasting.MyApplication;

/**
 * Created by luan.votrong on 12/22/2016.
 */

public class Utilities {

    public static String deviceName() {
        String manufacturer = Build.MANUFACTURER.toLowerCase();
        String model = Build.MODEL.toLowerCase();

        if(model.startsWith(manufacturer)) {
            return model;
        }
        else {
            return manufacturer + " " + model;
        }
    }

    public static Context getContext() {
        return MyApplication.getContext();
    }
}
