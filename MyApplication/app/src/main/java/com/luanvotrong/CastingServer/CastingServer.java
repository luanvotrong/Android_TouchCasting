package com.luanvotrong.CastingServer;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.app.Service;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.net.sip.SipAudioCall;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.SystemClock;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

import static android.R.attr.radius;
import static android.R.attr.x;
import static android.R.attr.y;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class CastingServer {
    private String TAG = "Lulu CastingServer";

    public void addTouch() {

    }
}
