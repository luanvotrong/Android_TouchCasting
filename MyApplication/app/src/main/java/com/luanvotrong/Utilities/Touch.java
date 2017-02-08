package com.luanvotrong.Utilities;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class Touch
    implements java.io.Serializable{
    public int m_id;
    public float m_x, m_y;
    public int m_type;

    public Touch() {

    }

    public Touch(int id, float x, float y, int type) {
        m_id = id;
        m_x = x;
        m_y = y;
        m_type = type;
    }

    public Touch(String info) {
        String[] infos = info.split(":");
        m_id = Integer.parseInt(infos[0]);
        m_x = Float.parseFloat(infos[1]);
        m_y = Float.parseFloat(infos[2]);
        m_type = Integer.parseInt(infos[3]);
    }
}