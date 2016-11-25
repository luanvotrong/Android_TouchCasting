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

import com.luanvotrong.CastingServer.CastingMgr;
import com.luanvotrong.CastingServer.ClientPool;
import com.luanvotrong.CastingServer.NsdHelper;
import com.luanvotrong.CastingServer.TouchesPool;

import static android.R.attr.onClick;
import static android.R.attr.radius;
import static android.R.attr.x;
import static android.R.attr.y;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class MainActivity extends AppCompatActivity {
    public enum CAST_TYPE {
        NONE,
        CASTER,
        RECEIVER
    }
    private CAST_TYPE m_type = CAST_TYPE.NONE;

    private String TAG = "Lulu MainActivity";
    private Paint m_paint = new Paint();
    private MainActivity m_self = this;

    private DrawingView m_View;
    private Button m_btnServer;
    private Button m_btnClient;

    private TouchesPool m_touchesPool;
    private CastingMgr m_castingMgr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);

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

        m_View = new DrawingView(this);
        m_View.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        m_View.setEnabled(false);

        m_touchesPool = new TouchesPool();
        m_castingMgr = new CastingMgr(this,m_touchesPool);
        m_btnServer = (Button) findViewById(R.id.Server);
        m_btnServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_castingMgr.initCaster();
                m_type = CAST_TYPE.CASTER;
            }
        });
        m_btnClient = (Button) findViewById(R.id.Client);
        m_btnClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_castingMgr.initReceiver();
                setContentView(m_View);
                m_type = CAST_TYPE.RECEIVER;
            }
        });
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

        switch(m_type) {
            case CASTER:
                m_touchesPool.AddTouch(x, y, motionEvent.getAction());
                break;
            case RECEIVER:
                m_View.setTouch(x, y);
                break;
        }

        return false;
    }
}
