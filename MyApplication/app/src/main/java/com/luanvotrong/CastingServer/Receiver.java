package com.luanvotrong.CastingServer;

import android.app.Activity;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.luanvotrong.Utilities.Define;
import com.luanvotrong.Utilities.HostInfo;
import com.luanvotrong.Utilities.Touch;
import com.luanvotrong.Utilities.TouchesPool;
import com.luanvotrong.touchcasting.MyApplication;

import java.io.DataInputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

public class Receiver {
    private int m_tcpPort = 63679;
    private String TAG = "Lulu Receiver";
    private CastMgr castMgr;
    private float mScreenW;
    private float mScreenH;
    private Activity activity;
    private Client client;
    private TouchInjector touchInjector;
    private Thread touchesInjectorThread;


    public void start(InetAddress inetAddress) {
        castMgr = MyApplication.getCastMgr();
        activity = castMgr.getMainActivity();
        mScreenW = MyApplication.getCastMgr().getScreenW();
        mScreenH = MyApplication.getCastMgr().getScreenH();

        touchInjector = new TouchInjector();
        touchesInjectorThread = new Thread(touchInjector);
        touchesInjectorThread.start();

        client = new Client();
        Kryo kryo = client.getKryo();
        kryo.register(Touch.class);
        kryo.register(String.class);
        client.start();
        client.addListener(new Listener() {
            public void received(Connection connection, Object object) {
                if (object instanceof Touch) {
                    Touch touch = (Touch) object;
                    touchInjector.addTouch(touch);
                }
            }

            public void connected(Connection connection) {
                Log.d(TAG, "Connected");
                client.sendTCP(new String("request"));
            }
        });
        try {
            client.connect(5000, inetAddress, Define.PORT_CASTING_TCP, Define.PORT_CASTING_UDP);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    public void stop() {
    }

    private class TouchInjector implements Runnable {
        TouchesPool touchesPool;
        public TouchInjector() {
            touchesPool = new TouchesPool();
        }

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                synchronized (touchesPool) {
                    Log.e(TAG, touchesPool.GetSize() + " eles");
                    if (touchesPool.GetSize() > 0) {
                        injectSingleTouch(touchesPool.GetTouch());
                    }
                }
            }
        }

        private void addTouches(ArrayList<Touch> touches) {
            synchronized (touchesPool) {
                touchesPool.addTouches(touches);
            }
        }

        private void addTouch(Touch touch) {
            synchronized (touchesPool) {
                touchesPool.addTouch(touch);
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