package com.luanvotrong.touchcasting;

import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.luanvotrong.CastingServer.CastMgr;
import com.luanvotrong.ConnectMgr.ConnectMgr;
import com.luanvotrong.Utilities.Define;

/**
 * Created by luan.votrong on 12/26/2016.
 */

public class Wrapper {
    private MainActivity mainAcitivity;

    private LinearLayout mainLayout;
    private ScrollView scrollView;
    private LinearLayout wrapperLayout;
    private DrawingView drawingView;

    private ConnectMgr connectMgr;
    private CastMgr castMgr;

    private Button mBtnServer;
    private Button mBtnClient;
    private boolean isConfiguring;
    private boolean isDetectingGesture;

    public void initUI(MainActivity mainActivity) {
        this.mainAcitivity = mainActivity;

        isConfiguring = false;
        isDetectingGesture = false;

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
                disableUI();
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
                disableUI();
            }
        });
        wrapperLayout.addView(mBtnClient, params);

        for(int i=0; i<20; i++) {
            Button button = new Button(mainAcitivity);
            button.setText("fasfsdaf fdsafsdafdsa fdsafsdf fdasfad");
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    disableUI();
                }
            });
            wrapperLayout.addView(button);
        }

        wrapperLayout.setVisibility(LinearLayout.GONE);
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
                            wrapperLayout.setVisibility(LinearLayout.GONE);
                        } else {
                            isConfiguring = !isConfiguring;
                            wrapperLayout.setVisibility(LinearLayout.VISIBLE);
                        }
                    }
                }
                break;
        }
    }
}
