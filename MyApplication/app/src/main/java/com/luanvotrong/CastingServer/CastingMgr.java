package com.luanvotrong.CastingServer;



/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class CastingMgr {
    private TouchesPool m_touchesPool;
    private ClientPool m_clientPool;

    public void initCaster() {
        TouchesPool m_touchesPool;
        m_clientPool = new ClientPool();
    }

    public void initReceiver() {
        TouchesPool m_touchesPool;
    }
}