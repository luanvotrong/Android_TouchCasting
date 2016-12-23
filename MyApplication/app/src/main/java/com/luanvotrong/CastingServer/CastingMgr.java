package com.luanvotrong.CastingServer;

import android.app.Activity;
import android.content.Context;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;

import com.luanvotrong.Utilities.Touch;
import com.luanvotrong.Utilities.TouchesPool;
import com.luanvotrong.touchcasting.DrawingView;
import com.luanvotrong.touchcasting.MyApplication;

import java.util.ArrayList;


public class CastingMgr {
    private String TAG = "Lulu CastingMgr";

    private Context m_context;
    private TouchesPool m_touchesPool;
    private float m_screenW;
    private float m_screenH;

    private Caster m_caster;
    private Receiver m_receiver;

    private DrawingView view;

    public enum CAST_TYPE {
        NONE,
        CASTER,
        RECEIVER
    }

    private CAST_TYPE m_type;

    public CastingMgr() {
        m_context = MyApplication.getContext();
        m_touchesPool = new TouchesPool();

        DisplayMetrics display = m_context.getResources().getDisplayMetrics();
        m_screenW = display.widthPixels;
        m_screenH = display.heightPixels;

        m_type = CAST_TYPE.NONE;
        view = null;
    }

    public void setView(DrawingView view) {
        this.view = view;
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
        try {
            m_touchesInjector.interrupt();
            m_touchesInjector = null;
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            m_touchesInjector = null;
        }
    }

    public void onTouchEvent(int id, int touchType, float x, float y) {
        switch (m_type) {
            case CASTER:
                break;
            case RECEIVER:
                view.setTouch(id, x, y, touchType);
                break;
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