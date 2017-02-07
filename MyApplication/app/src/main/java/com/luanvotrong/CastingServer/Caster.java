package com.luanvotrong.CastingServer;


import android.os.SystemClock;
import android.util.Log;

import com.esotericsoftware.kryonet.Connection;
import com.luanvotrong.Utilities.Touch;
import com.luanvotrong.Utilities.TouchesPool;
import com.luanvotrong.touchcasting.MyApplication;

import java.io.DataOutputStream;
import java.net.Socket;

public class Caster {
    private String TAG = "Lulu Caster";
    private TouchesPool touchesPool;
    private float mScreenW;
    private float mScreenH;
    private Thread castingWorker;
    private Connection connection;


    public Caster() {
        touchesPool = new TouchesPool();
    }

    public void start(Connection connection) {
        touchesPool.Clear();

        this.connection = connection;
        mScreenW = MyApplication.getCastMgr().getScreenW();
        mScreenH = MyApplication.getCastMgr().getScreenH();
        castingWorker = new Thread(new CastingWorker());
        castingWorker.start();
    }

    public void stop() {
        try {
            castingWorker.interrupt();
            castingWorker = null;
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            castingWorker = null;
        }
    }

    public void addTouch(int id, float x, float y, int action) {
        synchronized (touchesPool) {
            touchesPool.addTouch(id, x, y, action);
        }
    }

    private class CastingWorker implements Runnable {

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                /*
                synchronized (touchesPool) {
                    if (touchesPool.GetSize() > 0) {
                        Touch touch = touchesPool.GetTouch();
                        //Send instruction;
                        double pX = touch.m_x / mScreenW;
                        double pY = touch.m_y / mScreenH;
                        String mess = touch.m_id + ":" + pX + ":" + pY + ":" + touch.m_type;
                        try {
                            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                            dos.writeUTF(mess);
                        } catch (Exception e) {
                            Log.e(TAG, e.toString());
                        }
                    }
                }
                */
                synchronized (touchesPool) {
                    if (touchesPool.GetSize() > 0) {
                        Touch touch = touchesPool.GetTouch();
                        //Send instruction;
                        try {
                            connection.sendTCP(touch);
                        } catch (Exception e) {
                            Log.e(TAG, e.toString());
                        }
                    }
                }
            }
        }
    }
}