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
import com.luanvotrong.Utilities.Touch;
import com.luanvotrong.touchcasting.MyApplication;

import java.io.DataInputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

public class Receiver {
    private int m_tcpPort = 63679;
    private String TAG = "Lulu Receiver";
    private CastMgr castMgr;
    private ArrayList<String> mTouches;
    private float mScreenW;
    private float mScreenH;
    private Activity activity;
    private Client client;

    public ArrayList<String> getTouches() {
        return mTouches;
    }

    public void start(InetAddress inetAddress) {
        client = new Client();
        Kryo kryo = client.getKryo();
        kryo.register(Touch.class);
        kryo.register(String.class);
        client.start();
        client.addListener(new Listener() {
            public void received(Connection connection, Object object) {
                if (object instanceof Touch) {
                    Touch touch = (Touch) object;
                    injectSingleTouch(touch);
                }
            }

            public void connected(Connection connection) {
                Log.d(TAG, "Connected");
                client.sendTCP(new String("request"));
            }
        });
        try {
            client.connect(5000, inetAddress, Define.PORT_CASTING_TCP);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }

        castMgr = MyApplication.getCastMgr();
        activity = castMgr.getMainActivity();
        mTouches = new ArrayList<String>();
        mScreenW = MyApplication.getCastMgr().getScreenW();
        mScreenH = MyApplication.getCastMgr().getScreenH();
    }

    public void stop() {
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