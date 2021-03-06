package com.luanvotrong.CastingServer;


import android.util.Log;

import com.luanvotrong.Utilities.Touch;
import com.luanvotrong.Utilities.TouchesPool;
import com.luanvotrong.touchcasting.MyApplication;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class Caster {
    private String TAG = "Lulu Caster";
    private TouchesPool touchesPool;
    private float mScreenW;
    private float mScreenH;
    private Socket socket;
    private DataOutputStream dataOutputStream;
    private Thread castingWorker;

    public Caster() {
        touchesPool = new TouchesPool();
    }

    public void start(Socket socket) {
        touchesPool.Clear();
        this.socket = socket;
        try {
            this.socket.setTcpNoDelay(true);
            this.socket.setSendBufferSize(50000);
            Log.e(TAG, "tcp size " + this.socket.getSendBufferSize());
        } catch(Exception e) {
            Log.e(TAG, e.toString());
        }
        try {
            dataOutputStream = new DataOutputStream((this.socket.getOutputStream()));
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }

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
        try {
            socket.close();
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            socket = null;
        }
    }

    public void addTouch(int id, float x, float y, int action) {
        synchronized (touchesPool) {
            touchesPool.addTouch(id, x, y, action);
        }
    }

    private int count = 0;
    private class CastingWorker implements Runnable {

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                synchronized (touchesPool) {
                    if (touchesPool.GetSize() > 0) {
                        Touch touch = touchesPool.GetTouch();
                        //Send instruction;
                        double pX = touch.m_x / mScreenW;
                        pX = Math.round(pX * 10000.0) / 10000.0;
                        double pY = touch.m_y / mScreenH;
                        pY = Math.round(pY * 10000.0) / 10000.0;
                        String mess = touch.m_id + ":" + pX + ":" + pY + ":" + touch.m_type;
                        byte[] b = mess.getBytes();
                        Log.e(TAG, "size " + b.length);
                        try {
                            dataOutputStream.writeUTF(mess);
                            dataOutputStream.flush();
                        } catch (Exception e) {
                            Log.e(TAG, e.toString());
                        }
                    }
                }
            }
        }
    }
}