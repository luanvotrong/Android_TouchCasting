package com.luanvotrong.CastingServer;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.luanvotrong.Utilities.Define;
import com.luanvotrong.Utilities.Touch;
import com.luanvotrong.touchcasting.MyApplication;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by luan.votrong on 12/23/2016.
 */

public class CasterMgr {
    private String TAG = "Lulu CasterMgr";
    private ArrayList<Caster> casters;
    private Server server;

    public CasterMgr() {
        casters = new ArrayList<>();
    }

    public void start() {
        server = new Server();
        server.start();
        Kryo kryo = server.getKryo();
        kryo.register(Touch.class);
        kryo.register(String.class);
        server.addListener(new Listener() {
            public void received(Connection connection, Object object) {
                if (object instanceof String) {
                    String mess = (String) object;
                    if (mess.equals("request")) {
                        Log.d(TAG, "Connected");
                        Caster caster = new Caster();
                        caster.start(connection);
                        casters.add(caster);
                    }
                }
            }
        });

        try {
            server.bind(Define.PORT_CASTING_TCP);
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }
    }

    public void stop() {
        for (int i = 0, size = casters.size(); i < size; i++) {
            casters.get(i).stop();
        }
        casters.clear();
    }

    public void addTouch(int id, float x, float y, int action) {
        for (int i = 0, size = casters.size(); i < size; i++) {
            casters.get(i).addTouch(id, x, y, action);
        }
    }
}
