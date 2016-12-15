package com.luanvotrong.CastingServer;

import android.app.Activity;
import android.content.Context;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;

import org.json.*;

import com.luanvotrong.CastingServer.ConnectMgr.Caster;
import com.luanvotrong.CastingServer.ConnectMgr.Receiver;

import java.util.ArrayList;


public class CastingMgr {
    private Context m_context;
    private TouchesPool m_touchesPool;
    private float m_screenW;
    private float m_screenH;

    private Caster m_caster;
    private Receiver m_receiver;

    public CastingMgr(Context context, TouchesPool touchesPool) {
        m_context = context;
        m_touchesPool = touchesPool;

        DisplayMetrics display = context.getResources().getDisplayMetrics();
        m_screenW = display.widthPixels;
        m_screenH = display.heightPixels;
    }

    public void resetDimension() {
        DisplayMetrics display = m_context.getResources().getDisplayMetrics();
        m_screenW = display.widthPixels;
        m_screenH = display.heightPixels;
    }

    public void initCaster() {
        m_caster = new Caster();
        m_caster.start(m_context, m_touchesPool, this);
    }

    public void initReceiver() {
        m_receiver = new Receiver();
        m_receiver.start(this);

        m_touchesInjector = new Thread(new TouchesInjector());
        m_touchesInjector.start();
    }

    public void destroy() {
        if (m_touchesInjector != null) {
            m_touchesInjector.interrupt();
            m_touchesInjector = null;
        }
    }

    //////////////////////////////////////////////Touch Injector//////////////////////////////////////
    private class TouchesInjector implements Runnable {
        private String TAG = "Lulu TouchesInjector";

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                ArrayList<String> touches = m_receiver.getTouches();
                synchronized (touches) {
                    if (touches.size() > 0) {
                        String touch = touches.get(touches.size() - 1);
                        if (touch != null) {
                            Log.d(TAG, touch);
                            injectSingleTouch(new Touch(touch));
                        }
                        touches.remove(touches.size() - 1);
                    }
                }
            }
        }

        private void injectSingleTouch(Touch touch) {
            long downTime = SystemClock.uptimeMillis();
            MotionEvent.PointerProperties[] pointerProperties1 = new MotionEvent.PointerProperties[1];
            pointerProperties1[0] = new MotionEvent.PointerProperties();
            pointerProperties1[0].id = touch.m_id;
            MotionEvent.PointerCoords[] pointerCoordses1 = new MotionEvent.PointerCoords[1];
            pointerCoordses1[0] = new MotionEvent.PointerCoords();
            pointerCoordses1[0].x = touch.m_x * m_screenW;
            pointerCoordses1[0].y = touch.m_y * m_screenH;
            MotionEvent ev = MotionEvent.obtain(
                    downTime,
                    downTime,
                    touch.m_type,
                    1,
                    pointerProperties1,
                    pointerCoordses1,
                    0,
                    1,
                    1,
                    0,
                    0,
                    0,
                    0,
                    0
            );
            ((Activity) m_context).dispatchTouchEvent(ev);
        }
    }

    Thread m_touchesInjector;
}