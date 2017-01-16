package com.luanvotrong.CastingServer;

import android.app.Activity;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;

import com.luanvotrong.Utilities.Define;
import com.luanvotrong.Utilities.Touch;
import com.luanvotrong.touchcasting.MyApplication;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

public class Receiver {
    private String TAG = "Lulu Receiver";
    private CastMgr castMgr;
    private Thread receiverThread;
    private Thread touchInjectThread;
    private Socket socket;
    private DataInputStream dataInputStream;
    private ArrayList<String> mTouches;
    private float mScreenW;
    private float mScreenH;
    private Activity activity;

    public void start(InetAddress inetAddress) {
        try {
            socket = new Socket(inetAddress, Define.PORT_CASTING_UDP);
            dataInputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }

        castMgr = MyApplication.getCastMgr();
        activity = castMgr.getMainActivity();
        mTouches = new ArrayList<String>();
        mScreenW = MyApplication.getCastMgr().getScreenW();
        mScreenH = MyApplication.getCastMgr().getScreenH();

        receiverThread = new Thread(new ReceiverWorker());
        receiverThread.start();

        touchInjectThread = new Thread(new TouchesInjector());
        touchInjectThread.start();
    }

    public void stop() {
        try {
            receiverThread.interrupt();
            receiverThread = null;
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            receiverThread = null;
        }
        try {
            touchInjectThread.interrupt();
            touchInjectThread = null;
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            touchInjectThread = null;
        }
    }

    private class ReceiverWorker implements Runnable {
        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    String mess = dataInputStream.readUTF();
                    String[] infos = mess.split(":");
                    synchronized (mTouches) {
                        mTouches.add(mess);
                        Log.d(TAG, mess);
                    }
                } catch (Exception e) {
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
                String touch = null;
                synchronized (mTouches) {
                    if (mTouches.size() > 0) {
                        touch = mTouches.get(mTouches.size() - 1);
                        mTouches.remove(mTouches.size() - 1);
                    }
                }
                if (touch != null) {
                    Log.d(TAG, touch);
                    injectSingleTouch(new Touch(touch));
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