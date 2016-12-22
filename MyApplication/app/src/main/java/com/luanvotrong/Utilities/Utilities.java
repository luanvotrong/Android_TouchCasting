package com.luanvotrong.Utilities;

import android.os.Build;

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

    public static void sendUdp() {};
    public static void receiveUdp() {};

}
