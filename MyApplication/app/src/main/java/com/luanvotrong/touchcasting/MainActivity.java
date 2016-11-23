package com.luanvotrong.touchcasting;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.luanvotrong.CastingServer.ClientPool;
import com.luanvotrong.CastingServer.NsdHelper;

import static android.R.attr.onClick;
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
    private MainActivity m_self = this;

    private DrawingView m_View;
    private Button m_btnServer;
    private Button m_btnClient;
    private ClientPool m_clientPool;
    private NsdHelper m_nsdHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        m_View = new DrawingView(this);
        m_View.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        m_View.setEnabled(true);
        setContentView(m_View);

        m_nsdHelper = new NsdHelper();
        m_nsdHelper.init(this);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED)
        {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.INTERNET))
            {

            }
            else
            {
                ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.INTERNET }, 1);
            }
        }

        /*
        m_btnServer = (Button) findViewById(R.id.Server);
        m_btnServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_nsdHelper.registerService();
            }
        });
        m_btnClient = (Button) findViewById(R.id.Client);
        m_btnClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_nsdHelper.discoverServices();
            }
        });
        */
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
                MotionEvent motionEvent = MotionEvent.obtain(
                        downTime,
                        eventTime,
                        MotionEvent.ACTION_UP,
                        x,
                        y,
                        metaState
                );

// Dispatch touch event to view
                //m_self.dispatchTouchEvent(motionEvent);
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

    @Override
    protected void onDestroy() {
        m_nsdHelper.tearDown();

        super.onDestroy();
    }

    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        int pointerIndex = motionEvent.getActionIndex();
        if (pointerIndex < 0 || pointerIndex >= motionEvent.getPointerCount())
            return false;

        int pointerId = motionEvent.getPointerId(pointerIndex);
        float x = motionEvent.getX(pointerIndex);
        float y = motionEvent.getY(pointerIndex);

        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.d(TAG, pointerId + " Down " + x + " " + y);
                m_View.setPos(x, y );
                break;
            case MotionEvent.ACTION_MOVE:
                Log.d(TAG, pointerId + " Move " + x + " " + y);
                m_View.setPos(x, y );
                break;
            case MotionEvent.ACTION_UP:
                Log.d(TAG, pointerId + " Up " + x + " " + y);
                m_View.setPos(-100, -100);
                break;
            default:
                Log.d(TAG, motionEvent.getAction() + "");
        }

        return false;
    }
}
