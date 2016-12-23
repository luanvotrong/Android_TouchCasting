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

    public Touch GetTouch() throws Exception {
        try {
            Touch res = touches.get(touches.size() - 1);
            touches.remove(touches.size() - 1);
            return res;
        } catch (Exception e) {
            throw new Exception();
        }
    }
}
