package com.luanvotrong.touchcasting;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

import com.luanvotrong.CastingServer.CastMgr;
import com.luanvotrong.ConnectMgr.ConnectMgr;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */

public class MainActivity extends AppCompatActivity {
    private String TAG = "Lulu MainActivity";
    private DrawingView drawingView;
    private Button mBtnServer;
    private Button mBtnClient;

    private ConnectMgr connectMgr;
    private CastMgr castMgr;

    private boolean isConfiguring;
    private boolean isDetectingGesture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.INTERNET)) {

            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, 1);
            }
        }

        isConfiguring = false;
        isDetectingGesture = false;

        drawingView = new DrawingView(this);
        drawingView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        drawingView.setEnabled(false);

        connectMgr = MyApplication.getConnectMgr();
        castMgr = MyApplication.getCastMgr();
        castMgr.setView(drawingView);
        castMgr.setMainActivity(this);

        mBtnServer = (Button) findViewById(R.id.Server);
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

        mBtnClient = (Button) findViewById(R.id.Client);
        mBtnClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectMgr.startFinder();
            }
        });
        mBtnClient.setVisibility(Button.INVISIBLE);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        // get pointer index from the event object
        int pointerIndex = motionEvent.getActionIndex();

        // get pointer ID
        //hdz add void to crash from google log
        if (pointerIndex < 0 || pointerIndex >= motionEvent.getPointerCount())
            return false;

        for (int size = motionEvent.getPointerCount(), i = 0; i < size; i++) {
            castMgr.onTouchEvent(motionEvent.getPointerId(i), motionEvent.getActionMasked(), motionEvent.getX(i), motionEvent.getY(i));
        }

        //handle touch gesture
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                DisplayMetrics display = MyApplication.getContext().getResources().getDisplayMetrics();
                if (motionEvent.getY() > display.heightPixels - 10) {
                    isDetectingGesture = true;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (isDetectingGesture) {
                    if (motionEvent.getY() < 10) {
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

        return false;
    }
}