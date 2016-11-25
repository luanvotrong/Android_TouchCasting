package com.luanvotrong.CastingServer;

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
public class TouchesPool {
    private String TAG = "Lulu CastingPool";
    private ArrayList<Touch> m_touches = new ArrayList<Touch>();
    private static TouchesPool m_instance = null;

    public static TouchesPool getInstance() {
        if(m_instance == null) {
            m_instance = new TouchesPool();
        }

        return m_instance;
    }

    public class Touch {
        public float m_x, m_y;
        public int m_type;

        public Touch(float x, float y, int type) {
            m_x = x;
            m_y = y;
            m_type = type;
        }
    }

    public void AddTouch(float x, float y, int type) {
        m_touches.add(new Touch(x, y, type));
    }

    public Touch GetTouch() {
        Touch res = null;

        if(m_touches.size() > 0) {
            res = m_touches.get(m_touches.size() - 1);
            m_touches.remove(m_touches.size() - 1);
        }

        return res;
    }
}
