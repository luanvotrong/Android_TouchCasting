package com.luanvotrong.CastingServer;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.luanvotrong.Utilities.Define;
import com.luanvotrong.touchcasting.MyApplication;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by luan.votrong on 12/23/2016.
 */

public class CasterMgr {
    private String TAG = "Lulu CasterMgr";
    private CastMgr m_castMgr;
    private ServerSocket serverSocket;
    private ArrayList<Caster> casters;
    private Thread mServerSocketThread;

    private class ServerSocketWorker implements Runnable {

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Socket socket = serverSocket.accept();
                    Caster caster = new Caster();
                    caster.start(socket);
                    casters.add(caster);
                    MyApplication.getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MyApplication.getActivity(), casters.size() + " casters", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (Exception e) {
                    Log.d(TAG, e.toString());
                }
            }
        }
    }

    public CasterMgr() {
        casters = new ArrayList<>();
    }

    public void start() {
        try {
            serverSocket = new ServerSocket(Define.PORT_CASTING_TCP);
            mServerSocketThread = new Thread(new ServerSocketWorker());
            mServerSocketThread.start();
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }
    }

    public void stop() {
        try {
            mServerSocketThread.interrupt();
            mServerSocketThread = null;
        } catch (Exception e) {
            mServerSocketThread = null;
        }

        try {
            serverSocket.close();
            serverSocket = null;
        } catch (Exception e) {
            serverSocket = null;
        }

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
