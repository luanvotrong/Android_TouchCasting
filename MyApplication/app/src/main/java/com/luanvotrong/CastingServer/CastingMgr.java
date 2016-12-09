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

    public void initCaster() {
        m_caster = new Caster();
        m_caster.start(m_context, m_touchesPool);
    }

    public void initReceiver() {
        m_receiver = new Receiver();
        m_receiver.start();

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
        private ArrayList<Touch> m_touches = new ArrayList<>();
        private int m_touchType = -1;

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                ArrayList<String> touches = m_receiver.getTouches();
                while (touches.size() > 0) {
                    String touch = touches.get(touches.size() - 1);
                    if (touch != null) {
                        Log.d(TAG, touch);
                        preproccessTouches(touch);
                        injectTouch();
                    }
                    touches.remove(touches.size() - 1);
                }
                postInjectProccess();
            }
        }

        private void preproccessTouches(String touch) {
            String[] infos = touch.split(":");
            int id = Integer.parseInt(infos[0]);
            float x = Float.parseFloat(infos[1]);
            float y = Float.parseFloat(infos[2]);
            m_touchType = Integer.parseInt(infos[3]);
            switch (m_touchType) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_POINTER_DOWN: {
                    Touch tempTouch = null;
                    for (int i = 0, size = m_touches.size(); i < size; i++) {
                        if (m_touches.get(i).m_id == id) {
                            tempTouch = m_touches.get(i);
                        }
                    }
                    if (tempTouch == null) {
                        tempTouch = new Touch(touch);
                    }
                    tempTouch.m_id = id;
                    tempTouch.m_x = x;
                    tempTouch.m_y = y;
                    tempTouch.m_type = m_touchType;
                    m_touches.add(tempTouch);
                }
                case MotionEvent.ACTION_MOVE:
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_POINTER_UP:
                case MotionEvent.ACTION_CANCEL: {
                    for (int i = 0, size = m_touches.size(); i < size; i++) {
                        Touch tempTouch = m_touches.get(i);
                        if (tempTouch.m_id == id) {
                            tempTouch.m_x = x;
                            tempTouch.m_y = y;
                            tempTouch.m_type = m_touchType;
                        }
                    }
                    break;
                }
                default: {
                    break;
                }
            }
        }

        private void injectTouch() {
            if (m_touches.size() > 0) {
                int size = m_touches.size();
                MotionEvent.PointerProperties[] pointerProperties = new MotionEvent.PointerProperties[size];
                MotionEvent.PointerCoords[] pointerCoordses = new MotionEvent.PointerCoords[size];
                for (int i = 0; i < size; i++) {
                    pointerProperties[i] = new MotionEvent.PointerProperties();
                    pointerProperties[i].id = m_touches.get(i).m_id;

                    pointerCoordses[i] = new MotionEvent.PointerCoords();
                    pointerCoordses[i].x = m_touches.get(i).m_x * m_screenW;
                    pointerCoordses[i].y = m_touches.get(i).m_y * m_screenH;
                }

                long downTime = SystemClock.uptimeMillis();
                long eventTime = SystemClock.uptimeMillis() + 0;
                MotionEvent motionEvent = MotionEvent.obtain(
                        downTime,
                        eventTime,
                        m_touchType,
                        size,
                        pointerProperties,
                        pointerCoordses,
                        0,
                        1,
                        1,
                        0,
                        0,
                        0,
                        0,
                        0
                );

                ((Activity) m_context).dispatchTouchEvent(motionEvent);
            }
        }

        private void postInjectProccess() {
            for (int i = 0, size = m_touches.size(); i < size; i++) {
                Touch tempTouch = m_touches.get(i);
                switch (tempTouch.m_type) {
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_POINTER_UP:
                    case MotionEvent.ACTION_CANCEL: {
                        m_touches.remove(i);
                        break;
                    }
                }
                break;
            }
        }
    }

    Thread m_touchesInjector;
}