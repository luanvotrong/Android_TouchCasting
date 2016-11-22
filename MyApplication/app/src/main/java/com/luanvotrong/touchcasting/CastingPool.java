package com.luanvotrong.touchcasting;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.SystemClock;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import static android.R.attr.radius;
import static android.R.attr.x;
import static android.R.attr.y;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class CastingPool  implements Parcelable {
    private String TAG = "Lulu CastingPool";
    private ArrayList<String> m_ips = new ArrayList<String>();

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(m_ips);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public CastingPool createFromParcel(Parcel in) {
            return new CastingPool();
        }

        public CastingPool[] newArray(int size) {
            return new CastingPool[size];
        }
    };
}
