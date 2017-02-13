package com.luanvotrong.Utilities;

import java.util.ArrayList;

public class TouchesPool {
    private String TAG = "Lulu CastingPool";
    private ArrayList<Touch> touches = new ArrayList<Touch>();

    public void Clear() {
        touches.clear();
    }

    public void addTouch(int id, float x, float y, int type) {
        touches.add(new Touch(id, x, y, type));
    }

    public void addTouch(Touch touch) {
        touches.add(touch);
    }

    public void addTouches(ArrayList<Touch> _touches) {
        touches.addAll(_touches);
    }

    public Touch GetTouch() {
        try {
            Touch res = touches.get(touches.size() - 1);
            touches.remove(touches.size() - 1);
            return res;
        } catch (Exception e) {
            throw e;
        }
    }

    public ArrayList<Touch> GetTouches() {
        ArrayList<Touch> res = new ArrayList<>(touches);
        touches.clear();
        return res;
    }

    public int GetSize() {
        return touches.size();
    }
}
