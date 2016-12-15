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

import java.lang.reflect.Array;
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
        private ArrayList<Touch> m_touches = new ArrayList<>();

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                ArrayList<String> touches = m_receiver.getTouches();
                synchronized (touches) {
                    while (touches.size() > 0) {
                        String touch = touches.get(0);
                        if (touch != null) {
                            preproccessTouches(touch);
                            injectTouch2();
                            touches.remove(0);
                        }
                    }
                }
                postInjectProccess();
            }
        }

        private void preproccessTouches(String touch) {
            String[] infos = touch.split(":");
            int id = Integer.parseInt(infos[0]);
            float x = Float.parseFloat(infos[1]);
            float y = Float.parseFloat(infos[2]);
            int touchType = Integer.parseInt(infos[4]);
            switch (touchType) {
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
                        m_touches.add(tempTouch);
                    }
                    tempTouch.m_id = id;
                    tempTouch.m_x = x;
                    tempTouch.m_y = y;
                    tempTouch.m_type = MotionEvent.ACTION_DOWN;
                    Log.e(TAG, "added");
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
                            tempTouch.m_type = touchType;
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
            int size = m_touches.size();
            int multiTouchCount = 0;
            ArrayList<MotionEvent.PointerProperties> pointerProperties = new ArrayList<>();
            ArrayList<MotionEvent.PointerCoords> pointerCoordses = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                Touch tempTouch = m_touches.get(i);
                if (tempTouch.m_type == MotionEvent.ACTION_MOVE) {
                    multiTouchCount++;
                    MotionEvent.PointerProperties pp = new MotionEvent.PointerProperties();
                    pp.id = tempTouch.m_id;
                    pointerProperties.add(pp);

                    MotionEvent.PointerCoords pc = new MotionEvent.PointerCoords();
                    pc.x = tempTouch.m_x * m_screenW;
                    pc.y = tempTouch.m_y * m_screenH;
                    pointerCoordses.add(pc);
                } else {
                    injectSingleTouch(tempTouch);
                }
            }

            if (multiTouchCount > 0) {
                injectMultiTouch(pointerProperties, pointerCoordses, multiTouchCount);
            }
        }

        private void injectTouch2() {
            int size = m_touches.size();
            for (int i = 0; i < size; i++) {
                Touch tempTouch = m_touches.get(i);
                injectSingleTouch(tempTouch);
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

        private void injectMultiTouch(ArrayList<MotionEvent.PointerProperties> pp, ArrayList<MotionEvent.PointerCoords> pc, int size) {
            MotionEvent.PointerProperties[] pointerProperties = new MotionEvent.PointerProperties[size];
            MotionEvent.PointerCoords[] pointerCoordses = new MotionEvent.PointerCoords[size];
            for(int i=0; i<size; i++){
                pointerProperties[i] = pp.get(i);
                pointerCoordses[i] = pc.get(i);
            }

            long downTime = SystemClock.uptimeMillis();
            MotionEvent motionEvent = MotionEvent.obtain(
                    downTime,
                    downTime,
                    MotionEvent.ACTION_MOVE,
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

        private void postInjectProccess() {
            for (int i = 0, size = m_touches.size(); i < size; i++) {
                Touch tempTouch = m_touches.get(i);
                switch (tempTouch.m_type) {
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_POINTER_UP:
                    case MotionEvent.ACTION_CANCEL: {
                        m_touches.remove(i);
                        size = m_touches.size();
                        i--;
                        break;
                    }
                }
            }
        }
    }

    Thread m_touchesInjector;
}