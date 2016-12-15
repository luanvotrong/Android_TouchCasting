package com.luanvotrong.touchcasting;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

import com.luanvotrong.CastingServer.CastingMgr;
import com.luanvotrong.CastingServer.NsdHelper;
import com.luanvotrong.CastingServer.TouchesPool;

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

    private DrawingView m_View;
    private NsdHelper m_nsdHelper;
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

        m_nsdHelper = new NsdHelper();
        m_nsdHelper.init(this);

        m_touchesPool = new TouchesPool();
        m_castingMgr = new CastingMgr(this,m_touchesPool);
        m_btnServer = (Button) findViewById(R.id.Server);
        m_btnServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_castingMgr.initCaster();
                m_type = CAST_TYPE.CASTER;
                //m_nsdHelper.discoverServices();
            }
        });
        m_btnClient = (Button) findViewById(R.id.Client);
        m_btnClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_castingMgr.initReceiver();
                setContentView(m_View);
                m_type = CAST_TYPE.RECEIVER;
                //m_nsdHelper.registerService();
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

        m_castingMgr.destroy();
        m_nsdHelper.tearDown();
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        // get pointer index from the event object
        int pointerIndex = motionEvent.getActionIndex();

        // get pointer ID
        //hdz add void to crash from google log
        if(pointerIndex < 0 || pointerIndex >= motionEvent.getPointerCount())
            return false;

        for(int size = motionEvent.getPointerCount(), i = 0; i<size; i++) {
            int pointerId = motionEvent.getPointerId(i);
            float x = motionEvent.getX(i);
            float y = motionEvent.getY(i);

            switch (m_type) {
                case CASTER:
                    synchronized (m_touchesPool) {
                        m_touchesPool.AddTouch(pointerId, x, y, motionEvent.getAction(), motionEvent.getActionMasked());
                    }
                    break;
                case RECEIVER:
                    m_View.setTouch(pointerId, x, y, motionEvent.getActionMasked());
                    break;
            }
        }

        return false;
    }
}