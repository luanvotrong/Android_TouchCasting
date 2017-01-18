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
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

public class Receiver {
    private String TAG = "Lulu Receiver";
    private CastMgr castMgr;
    private Thread receiverThread;
    private Socket socket;
    private DatagramSocket datagramSocket;
    private DatagramPacket datagramPacket;
    private float mScreenW;
    private float mScreenH;
    private Activity activity;

    public void start(InetAddress inetAddress) {
        try {
            socket = new Socket(inetAddress, Define.PORT_CASTING_TCP);
            datagramSocket = new DatagramSocket(Define.PORT_CASTING_UDP);
            datagramPacket = new DatagramPacket("".getBytes(), 0);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }

        castMgr = MyApplication.getCastMgr();
        activity = castMgr.getMainActivity();
        mScreenW = MyApplication.getCastMgr().getScreenW();
        mScreenH = MyApplication.getCastMgr().getScreenH();

        receiverThread = new Thread(new ReceiverWorker());
        receiverThread.start();
    }

    public void stop() {
        try {
            receiverThread.interrupt();
            receiverThread = null;
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            receiverThread = null;
        }
    }

    private class ReceiverWorker implements Runnable {
        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    //TODO: implement UDP receiving
                    byte[] message = new byte[1500];
                    datagramPacket.setData(message, 0,message.length);
                    datagramSocket.receive(datagramPacket);
                    String mess = new String(message, 0, datagramPacket.getLength());
                    injectSingleTouch(new Touch(mess));
                    Log.d(TAG, mess);
                } catch (Exception e) {
                    Log.d(TAG, e.toString());
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