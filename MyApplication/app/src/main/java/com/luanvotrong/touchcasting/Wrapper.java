package com.luanvotrong.touchcasting;

import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.luanvotrong.CastingServer.CastMgr;
import com.luanvotrong.ConnectMgr.ConnectMgr;
import com.luanvotrong.Utilities.Define;

/**
 * Created by luan.votrong on 12/26/2016.
 */

public class Wrapper {
    private MainActivity mainAcitivity;

    private LinearLayout linearLayout;
    private DrawingView drawingView;

    private ConnectMgr connectMgr;
    private CastMgr castMgr;

    private Button mBtnServer;
    private Button mBtnClient;
    private boolean isConfiguring;
    private boolean isDetectingGesture;

    public Wrapper(MainActivity activity) {
        this.mainAcitivity = activity;
    }

    public void initUI() {
        isConfiguring = false;
        isDetectingGesture = false;

        drawingView = new DrawingView(mainAcitivity);
        drawingView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        drawingView.setEnabled(false);

        connectMgr = MyApplication.getConnectMgr();
        castMgr = MyApplication.getCastMgr();
        castMgr.setView(drawingView);
        castMgr.setMainActivity(mainAcitivity);

        linearLayout = (LinearLayout) mainAcitivity.findViewById(R.id.linear_layout);

        mBtnServer = new Button(mainAcitivity);
        mBtnServer.setText("Server");
        mBtnServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectMgr.startBeacon();
                switch (castMgr.getType()) {
                    case NONE:
                        castMgr.startCaster();
                        break;
                    case CASTER:
                        break;
                    case RECEIVER:
                        castMgr.stopReceiver();
                        castMgr.startCaster();
                        break;
                }
            }
        });
        mBtnServer.setVisibility(Button.INVISIBLE);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        linearLayout.addView(mBtnServer, params);

        mBtnClient = new Button(mainAcitivity);
        mBtnClient.setText("Client");
        mBtnClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectMgr.startFinder();
            }
        });
        mBtnClient.setVisibility(Button.INVISIBLE);
        linearLayout.addView(mBtnClient, params);

        for(int i=0; i<5; i++) {

        }
    }

    public void handlingUITouchGesture(MotionEvent motionEvent) {
        // get pointer index from the event object
        int pointerIndex = motionEvent.getActionIndex();

        // get pointer ID
        //hdz add void to crash from google log
        if (pointerIndex < 0 || pointerIndex >= motionEvent.getPointerCount())
            return;

        for (int size = motionEvent.getPointerCount(), i = 0; i < size; i++) {
            castMgr.onTouchEvent(motionEvent.getPointerId(i), motionEvent.getActionMasked(), motionEvent.getX(i), motionEvent.getY(i));
        }

        //handle touch gesture
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                DisplayMetrics display = MyApplication.getContext().getResources().getDisplayMetrics();
                if (motionEvent.getY() > display.heightPixels - Define.GESTURE_OFFSET) {
                    isDetectingGesture = true;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (isDetectingGesture) {
                    if (motionEvent.getY() < Define.GESTURE_OFFSET) {
                        if (isConfiguring) {
                            isConfiguring = !isConfiguring;
                            mBtnServer.setVisibility(Button.INVISIBLE);
                            mBtnClient.setVisibility(Button.INVISIBLE);
                        } else {
                            isConfiguring = !isConfiguring;
                            mBtnServer.setVisibility(Button.VISIBLE);
                            mBtnClient.setVisibility(Button.VISIBLE);
                        }
                    }
                }
                break;
        }
    }
}
