package com.luanvotrong.CastingServer;


import android.util.Log;

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
    private Socket socket;
    private Thread castingWorker;

    public Caster() {
        touchesPool = new TouchesPool();
    }

    public void start(Socket socket) {
        touchesPool.Clear();
        this.socket = socket;

        mScreenW = MyApplication.getCastMgr().getScreenW();
        mScreenH = MyApplication.getCastMgr().getScreenH();
        castingWorker = new Thread( new CastingWorker() );
    }

    public void stop() {
        try {
            castingWorker.interrupt();
            castingWorker = null;
        }catch (Exception e) {
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

    private class CastingWorker implements Runnable {

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {

                try {
                    synchronized (touchesPool) {
                        Touch touch = touchesPool.GetTouch();
                        //Send instruction;
                        float pX = touch.m_x / mScreenW;
                        float pY = touch.m_y / mScreenH;
                        String mess = touch.m_id + ":" + pX + ":" + pY + ":" + touch.m_type;
                        try {
                            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                            dos.writeUTF(mess);
                        } catch (Exception e) {

                        }
                    }
                } catch (Exception e) {

                }
            }
        }
    }
}