package com.luanvotrong.CastingServer;

import java.util.ArrayList;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class TouchesPool {
    private String TAG = "Lulu CastingPool";
    private ArrayList<Touch> m_touches = new ArrayList<Touch>();

    public void AddTouch(int id, float x, float y, int type) {
        m_touches.add(new Touch(id, x, y, type));
    }

    public Touch GetTouch() {
        Touch res = null;

        if(m_touches.size() > 0) {
            res = m_touches.get(m_touches.size() - 1);
            m_touches.remove(m_touches.size() - 1);
        }

        return res;
    }
}
