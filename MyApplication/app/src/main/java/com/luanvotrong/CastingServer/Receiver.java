package com.luanvotrong.CastingServer;

import android.app.Activity;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;

import com.luanvotrong.Utilities.Touch;
import com.luanvotrong.touchcasting.DrawingView;
import com.luanvotrong.touchcasting.MainActivity;
import com.luanvotrong.touchcasting.MyApplication;

import java.io.DataInputStream;
import java.net.Socket;
import java.util.ArrayList;

public class Receiver {
    private int m_tcpPort = 63679;
    private String TAG = "Lulu Receiver";
    private String m_serviceName = "TouchCasting";
    private CastMgr castMgr;
    private Thread m_connectThread;
    private Thread m_receiverThread;
    private Socket m_socket;
    private ArrayList<String> mTouches;
    private float mScreenW;
    private float mScreenH;
    private MainActivity activity;

    public ArrayList<String> getTouches() {
        return mTouches;
    }

    public void start() {
        castMgr = MyApplication.getCastMgr();
        activity = castMgr.getMainActivity();
        mTouches = new ArrayList<String>();
    }


    private class ReceiverWorker implements Runnable {
        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    DataInputStream dis = new DataInputStream(m_socket.getInputStream());
                    String mess = dis.readUTF();
                    String[] infos = mess.split(":");
                    synchronized (mTouches) {
                        mTouches.add(mess);
                        Log.d(TAG, mess);
                    }
                } catch(Exception e) {
                    Log.d(TAG, e.toString());
                }
            }
        }
    }
    private class TouchesInjector implements Runnable {
        private String TAG = "Lulu TouchesInjector";

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                synchronized (mTouches) {
                    if (mTouches.size() > 0) {
                        String touch = mTouches.get(mTouches.size() - 1);
                        if (touch != null) {
                            Log.d(TAG, touch);
                            injectSingleTouch(new Touch(touch));
                        }
                        mTouches.remove(mTouches.size() - 1);
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
            pointerCoordses1[0].x = touch.m_x * mScreenW;
            pointerCoordses1[0].y = touch.m_y * mScreenH;
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
            activity.dispatchTouchEvent(ev);
        }
    }
}