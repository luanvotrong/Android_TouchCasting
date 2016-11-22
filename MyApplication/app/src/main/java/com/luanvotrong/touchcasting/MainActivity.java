package com.luanvotrong.touchcasting;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.SystemClock;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import static android.R.attr.radius;
import static android.R.attr.x;
import static android.R.attr.y;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class MainActivity extends AppCompatActivity {
    private String TAG = "Lulu MainActivity";
    private Paint m_paint = new Paint();
    private SurfaceView m_surfaceView;
    private MainActivity m_self = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_main);

        m_surfaceView = (SurfaceView) findViewById(R.id.surfaceView);

        // Set up the user interaction to manually show or hide the system UI.
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                long downTime = SystemClock.uptimeMillis();
                long eventTime = SystemClock.uptimeMillis() + 0;
                float x = 10.0f;
                float y = 10.0f;

                int metaState = 0;
                Log.d(TAG, "fuck");
                MotionEvent motionEvent = MotionEvent.obtain(
                        downTime,
                        eventTime,
                        MotionEvent.ACTION_UP,
                        x,
                        y,
                        metaState
                );

// Dispatch touch event to view
                m_self.dispatchTouchEvent(motionEvent);
                handler.postDelayed(this, 500); // set time here to refresh textView
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
    }

    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {

        float x = motionEvent.getX();
        float y = motionEvent.getY();

        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.d(TAG, "Down " + x + " " + y);
                break;
            case MotionEvent.ACTION_MOVE:
                Log.d(TAG, "Move " + x + " " + y);
                break;
            case MotionEvent.ACTION_UP:
                Log.d(TAG, "Up " + x + " " + y);
                break;
            default:
                Log.d(TAG, motionEvent.getAction() + "");
        }

        return false;
    }
}
