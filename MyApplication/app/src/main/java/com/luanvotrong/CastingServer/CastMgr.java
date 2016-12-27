package com.luanvotrong.CastingServer;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;

import com.luanvotrong.touchcasting.DrawingView;
import com.luanvotrong.touchcasting.MyApplication;

import java.net.InetAddress;


public class CastMgr {
    private String TAG = "Lulu CastMgr";

    private Context context;
    private float mScreenW;
    private float mScreenH;
    private Activity mainActivity;

    private CasterMgr casterMgr;
    private Receiver receiver;

    private DrawingView view;

    public enum CAST_TYPE {
        NONE,
        CASTER,
        RECEIVER
    }

    private CAST_TYPE m_type;

    public CastMgr() {
        context = MyApplication.getContext();

        DisplayMetrics display = context.getResources().getDisplayMetrics();
        mScreenW = display.widthPixels;
        mScreenH = display.heightPixels;

        m_type = CAST_TYPE.NONE;
        view = null;
    }

    public CAST_TYPE getType() {
        return m_type;
    }

    public void setView(DrawingView view) {
        this.view = view;
    }

    public DrawingView getView() {
        return this.view;
    }

    public void setMainActivity(Activity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public Activity getMainActivity() {
        return this.mainActivity;
    }

    public float getScreenW() {
        return mScreenW;
    }

    public float getScreenH() {
        return mScreenH;
    }

    public void resetDimension() {
        DisplayMetrics display = context.getResources().getDisplayMetrics();
        mScreenW = display.widthPixels;
        mScreenH = display.heightPixels;
    }

    public void startCaster() {
        resetDimension();
        casterMgr = new CasterMgr();
        casterMgr.start();
    }

    public void stopCaster() {
        casterMgr.stop();
        casterMgr = null;
    }

    public void startReceiver(InetAddress inetAddress) {
        resetDimension();
        receiver = new Receiver();
        receiver.start(inetAddress);
    }

    public void stopReceiver() {
        receiver.stop();
        receiver = null;
    }

    public void onTouchEvent(int id, int touchType, float x, float y) {
        switch (m_type) {
            case CASTER:
                casterMgr.addTouch(id, x, y, touchType);
                break;
            case RECEIVER:
                view.setTouch(id, x, y, touchType);
                break;
        }
    }
}