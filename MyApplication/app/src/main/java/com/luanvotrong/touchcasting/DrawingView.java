package com.luanvotrong.touchcasting;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
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
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

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
public class DrawingView extends View {
    private String TAG = "Lulu MainActivity";
    private Paint m_paint;
    private float m_x = -100;
    private float m_y = -100;

    public DrawingView(Context ctx) {
        super(ctx);
        setWillNotDraw(false);
        m_paint = new Paint();
        m_paint.setColor(Color.BLUE);
        m_paint.setStyle(Paint.Style.FILL);
        m_paint.setStrokeWidth(10);
    }

    public void setPos(float x, float y) {
        TouchesPool.getInstance().AddTouch(x, y, 0);
    }

    @Override
    protected void onDraw(Canvas c) {
        super.onDraw(c);
        Log.d(TAG, "draw");
        TouchesPool.Touch touch = TouchesPool.getInstance().GetTouch();
        if(touch != null)
        {
            c.drawCircle(touch.m_x, touch.m_y, 50, m_paint);
        }

        invalidate();
    }
}
