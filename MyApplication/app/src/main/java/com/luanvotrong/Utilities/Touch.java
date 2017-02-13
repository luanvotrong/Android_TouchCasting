package com.luanvotrong.Utilities;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoSerializable;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class Touch
    implements KryoSerializable{
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

    @Override
    public void write(Kryo kryo, Output output) {
        output.writeInt(m_id, true);
        output.writeFloat(m_x);
        output.writeFloat(m_y);
        output.writeInt(m_type, true);
    }

    @Override
    public void read(Kryo kryo, Input input) {
        m_id = input.readInt(true);
        m_x = input.readFloat();
        m_y = input.readFloat();
        m_type = input.readInt(true);
    }
}