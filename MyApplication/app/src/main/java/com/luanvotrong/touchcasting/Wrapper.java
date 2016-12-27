package com.luanvotrong.touchcasting;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.luanvotrong.CastingServer.CastMgr;
import com.luanvotrong.ConnectMgr.ConnectMgr;
import com.luanvotrong.Utilities.Define;
import com.luanvotrong.Utilities.HostInfo;
import com.luanvotrong.Utilities.Utilities;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by luan.votrong on 12/26/2016.
 */

public class Wrapper implements WrapperCallback {
    private Activity mainAcitivity;

    private LinearLayout mainLayout;
    private ScrollView scrollView;
    private LinearLayout wrapperLayout;
    private DrawingView drawingView;

    private ConnectMgr connectMgr;
    private CastMgr castMgr;

    private Button mBtnServer;
    private Button mBtnClient;
    private Button mBtnCancel;
    private ArrayList<Button> mBtnServers;
    private boolean isConfiguring;

    private float screenW;
    private float screenH;

    private enum GESTURE_PHASE {
        NONE,
        PHASE1,//top left
        PHASE2,//bot right
        PHASE3,//top right
        PHASE4 //bot left
    }

    private GESTURE_PHASE gesturePhase;

    public void initUI(Activity mainActivity) {
        this.mainAcitivity = mainActivity;

        isConfiguring = false;
        gesturePhase = GESTURE_PHASE.NONE;
        DisplayMetrics display = MyApplication.getContext().getResources().getDisplayMetrics();
        screenW = display.widthPixels;
        screenH = display.heightPixels;

        drawingView = new DrawingView(mainAcitivity);
        drawingView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        drawingView.setEnabled(false);

        connectMgr = MyApplication.getConnectMgr();
        castMgr = MyApplication.getCastMgr();
        castMgr.setView(drawingView);
        castMgr.setMainActivity(mainAcitivity);

        scrollView = new ScrollView(mainAcitivity);
        wrapperLayout = new LinearLayout(mainAcitivity);
        wrapperLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout = (LinearLayout) mainAcitivity.findViewById(R.id.linear_layout);

        scrollView.addView(wrapperLayout);
        mainLayout.addView(scrollView);

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
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        wrapperLayout.addView(mBtnServer, params);

        mBtnClient = new Button(mainAcitivity);
        mBtnClient.setText("Client");
        mBtnClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectMgr.startFinder();
            }
        });
        wrapperLayout.addView(mBtnClient, params);

        mBtnCancel = new Button(mainAcitivity);
        mBtnCancel.setText("Cancel");
        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disableUI();
            }
        });
        wrapperLayout.addView(mBtnCancel, params);
        wrapperLayout.setVisibility(LinearLayout.GONE);

        mBtnServers = new ArrayList<>();
    }

    public void disableUI() {
        wrapperLayout.setVisibility(LinearLayout.GONE);
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

        float x = motionEvent.getX();
        float y = motionEvent.getY();
        //handle touch gesture
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (gesturePhase == GESTURE_PHASE.NONE) {
                    if (x < screenW / 2 && y > screenH / 2) {
                        gesturePhase = GESTURE_PHASE.PHASE1;
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                switch (gesturePhase) {
                    case PHASE1:
                        if (x > screenW / 2 && y < screenH / 2) {
                            gesturePhase = GESTURE_PHASE.PHASE2;
                        }
                        break;
                    case PHASE2:
                        if (x < screenW / 2 && y < screenH / 2) {
                            gesturePhase = GESTURE_PHASE.PHASE3;
                        }
                        break;
                    case PHASE3:
                        if (x > screenW / 2 && y > screenH / 2) {
                            gesturePhase = GESTURE_PHASE.PHASE4;
                        }
                        break;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (gesturePhase == GESTURE_PHASE.PHASE4) {
                    if (isConfiguring) {
                        isConfiguring = !isConfiguring;
                        wrapperLayout.setVisibility(LinearLayout.GONE);
                    } else {
                        isConfiguring = !isConfiguring;
                        wrapperLayout.setVisibility(LinearLayout.VISIBLE);
                    }
                }
                break;
        }
    }

    @Override
    public void onUpdateServerList() {
        for (int i = 0, size = mBtnServers.size(); i < size; i++) {
            wrapperLayout.removeView(mBtnServers.get(i));
        }
        mBtnServers.clear();

        ArrayList<HostInfo> hostInfos = MyApplication.getConnectMgr().getListBeacon();
        for (int i = 0, size = hostInfos.size(); i < size; i++) {
            HostInfo hostInfo = hostInfos.get(i);
            Button btn = new Button(mainAcitivity);
            btn.setText(hostInfo.getName());
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    disableUI();
                }
            });
            mBtnServers.add(btn);
            mainAcitivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    for (int j = 0, size = mBtnServers.size(); j < size; j++) {
                        wrapperLayout.addView(mBtnServers.get(j));
                    }
                }
            });
        }
    }
}
