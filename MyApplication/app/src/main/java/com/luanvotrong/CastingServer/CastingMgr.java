package com.luanvotrong.CastingServer;

import android.graphics.Point;
import android.os.SystemClock;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;

import com.luanvotrong.CastingServer.ConnectMgr.Caster;
import com.luanvotrong.CastingServer.ConnectMgr.Receiver;
import com.luanvotrong.touchcasting.MainActivity;

import java.util.ArrayList;


public class CastingMgr {
    private int m_udpPort = 63678;
    private int m_tcpPort = 63677;
    private MainActivity m_context;
    private TouchesPool m_touchesPool;
    private float m_screenW;
    private float m_screenH;

    private Caster m_caster;
    private Receiver m_receiver;

    public CastingMgr(MainActivity context, TouchesPool touchesPool) {
        m_context = context;
        m_touchesPool = touchesPool;

        Display display = m_context.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        m_screenW = size.x;
        m_screenH = size.y;
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
        if(m_touchesInjector != null) {
            m_touchesInjector.interrupt();
            m_touchesInjector = null;
        }
    }

    //////////////////////////////////////////////Touch Injector//////////////////////////////////////
    private class TouchesInjector implements Runnable {
        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                ArrayList<String> touches = m_receiver.getTouches();
                if (touches.size() > 0) {
                    String touch = touches.get(touches.size() - 1);
                    if(touch != null) {
                        String[] infos = touch.split(":");
                        long downTime = SystemClock.uptimeMillis();
                        long eventTime = SystemClock.uptimeMillis() + 0;
                        int metaState = 0;
                        MotionEvent motionEvent = MotionEvent.obtain(
                                downTime,
                                eventTime,
                                Integer.parseInt(infos[2]),
                                Float.parseFloat(infos[0]) * m_screenW,
                                Float.parseFloat(infos[1]) * m_screenH,
                                metaState
                        );

                        m_context.dispatchTouchEvent(motionEvent);
                    }
                    touches.remove(touches.size() - 1);
                }
            }
        }
    }

    Thread m_touchesInjector;
}