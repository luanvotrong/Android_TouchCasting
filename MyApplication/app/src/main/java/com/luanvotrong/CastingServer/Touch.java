package com.luanvotrong.CastingServer;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class Touch {
    public int m_id;
    public float m_x, m_y;
    public int m_type;
    public int m_typeMasked;

    public Touch(int id, float x, float y, int type, int typeMasked) {
        m_id = id;
        m_x = x;
        m_y = y;
        m_type = type;
        m_typeMasked = typeMasked;
    }

    public Touch(String info) {
        String[] infos = info.split(":");
        m_id = Integer.parseInt(infos[0]);
        m_x = Float.parseFloat(infos[1]);
        m_y = Float.parseFloat(infos[2]);
        m_type = Integer.parseInt(infos[3]);
    }
}