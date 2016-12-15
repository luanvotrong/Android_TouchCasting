package com.luanvotrong.touchcasting;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Random;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class DrawingView extends SurfaceView {
    private String TAG = "Lulu MainActivity";
    private Paint m_paint;
    private float m_x, m_y;

    private class Circle {
        public int m_id;
        public float m_x, m_y;
        public int m_color;

        public Circle(int id, float x, float y) {
            m_id = id;
            m_x = x;
            m_y = y;
            m_color = Color.rgb(0 + (int)(Math.random() * 255), 0 + (int)(Math.random() * 255), 0 + (int)(Math.random() * 255));
        }
    }

    private ArrayList<Circle> m_circles = new ArrayList<Circle>();

    public DrawingView(Context ctx) {
        super(ctx);
        setWillNotDraw(false);
        m_paint = new Paint();
        m_paint.setColor(Color.BLUE);
        m_paint.setStyle(Paint.Style.FILL);
        m_paint.setStrokeWidth(10);
    }

    public void setTouch(int id, float x, float y, int type) {
        switch (type) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN: {
                Circle circle = null;
                for (int i = 0; i < m_circles.size(); i++) {
                    Circle temp = m_circles.get(i);
                    if (temp.m_id == id) {
                        circle = temp;
                        break;
                    }
                }

                if(circle == null) {
                    circle = new Circle(id, x, y);
                    m_circles.add(circle);
                }
                circle.m_x = x;
                circle.m_y = y;
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                for (int i = 0; i < m_circles.size(); i++) {
                    Circle circle = m_circles.get(i);
                    if (circle.m_id == id) {
                        circle.m_x = x;
                        circle.m_y = y;
                        break;
                    }
                }
                break;
            }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP: {
                for (int i = 0; i < m_circles.size(); i++) {
                    if (m_circles.get(i).m_id == id) {
                        m_circles.remove(i);
                        break;
                    }
                }
                break;
            }
            case MotionEvent.ACTION_CANCEL: {
                for (int i = 0; i < m_circles.size(); i++) {
                    if (m_circles.get(i).m_id == id) {
                        m_circles.remove(i);
                        break;
                    }
                }
                break;
            }
            default: {
                break;
            }
        }
    }

    @Override
    protected void onDraw(Canvas c) {
        super.onDraw(c);
        for (int i = 0; i < m_circles.size(); i++) {
            Circle circle = m_circles.get(i);
            m_paint.setColor(circle.m_color);
            c.drawCircle(circle.m_x, circle.m_y, 50, m_paint);
        }

        invalidate();
    }
}
