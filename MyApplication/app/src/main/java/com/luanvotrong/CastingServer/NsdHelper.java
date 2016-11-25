package com.luanvotrong.CastingServer;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.net.ServerSocket;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class NsdHelper {
    private String TAG = "Lulu NsdHelper";
    private String SERVICE_TYPE = "_http._tcp.";
    private String SERVICE_NAME = "TouchCasting";
    private ServerSocket m_serverSocket;

    private NsdManager m_nsdManager;
    private NsdServiceInfo m_serviceInfo;
    private NsdManager.RegistrationListener m_registrationListener;
    private NsdManager.ResolveListener m_resolveListener;
    private NsdManager.DiscoveryListener m_discoveryListener;

    private Context m_context;

    private ClientPool m_clientPool;

    public void setClientPool(ClientPool clientPool) {
        m_clientPool = clientPool;
    }

    public void init(Context ctx) {
        m_context = ctx;
        m_nsdManager = (NsdManager) m_context.getSystemService(Context.NSD_SERVICE);
    }

    //////////////////////////////////////////////////////////Listener/////////////////////////////////////////////////////////////////////////////
    public void registerService() {
        initRegistrationListener();

        try {
            m_serverSocket = new ServerSocket(0);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }

        if(m_serverSocket != null)
        {
            m_serviceInfo = new NsdServiceInfo();
            m_serviceInfo.setServiceType(SERVICE_TYPE);
            m_serviceInfo.setServiceName(SERVICE_NAME);
            m_serviceInfo.setPort(m_serverSocket.getLocalPort());

            m_nsdManager.registerService(m_serviceInfo, NsdManager.PROTOCOL_DNS_SD, m_registrationListener);
            Log.e(TAG, "Started serice");
            Toast.makeText(m_context, "Started listening", Toast.LENGTH_LONG);
        }
    }

    private void initRegistrationListener() {
        m_registrationListener = new NsdManager.RegistrationListener() {
            @Override
            public void onServiceRegistered(NsdServiceInfo NsdServiceInfo) {
                // Save the service name.  Android may have changed it in order to
                // resolve a conflict, so update the name you initially requested
                // with the name Android actually used.
                //mServiceName = NsdServiceInfo.getServiceName();
                Log.e(TAG, "Registration succeed");
            }

            @Override
            public void onRegistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
                // Registration failed!  Put debugging code here to determine why.
                Log.e(TAG, "Registration Failed: " + errorCode);
            }

            @Override
            public void onServiceUnregistered(NsdServiceInfo arg0) {
                // Service has been unregistered.  This only happens when you call
                // NsdManager.unregisterService() and pass in this listener.
            }

            @Override
            public void onUnregistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
                // Unregistration failed.  Put debugging code here to determine why.
            }
        };
    }

    //////////////////////////////////////////////////////////Discoverer//////////////////////////////////////////////////////////
    public void discoverServices() {
        initResolveListener();
        initDiscoveryListener();

        m_nsdManager.discoverServices(SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, m_discoveryListener);
    }

    private void initDiscoveryListener() {
        m_discoveryListener = new NsdManager.DiscoveryListener() {
            @Override
            public void onStartDiscoveryFailed(String serviceType, int errorCode) {
                Log.d(TAG, "Service start discovery failed");
                m_nsdManager.stopServiceDiscovery(this);
            }

            @Override
            public void onStopDiscoveryFailed(String serviceType, int errorCode) {
                Log.d(TAG, "Service stop discovery failed");
                m_nsdManager.stopServiceDiscovery(this);
            }

            @Override
            public void onDiscoveryStarted(String serviceType) {
                Log.d(TAG, "Service discovery started");
            }

            @Override
            public void onDiscoveryStopped(String serviceType) {
                Log.d(TAG, "Service discovery stopped");
            }

            @Override
            public void onServiceFound(NsdServiceInfo serviceInfo) {
                Log.d(TAG, "Service discovered: " + serviceInfo);
                if (!serviceInfo.getServiceType().equals(SERVICE_TYPE)) {
                    Log.d(TAG, "Unknown Service Type: " + serviceInfo.getServiceType());
                } else if (serviceInfo.getServiceName().equals(SERVICE_NAME)) {
                    Log.d(TAG, "Same machine: " + SERVICE_NAME);
                } else if (serviceInfo.getServiceName().contains(SERVICE_NAME)){
                    m_nsdManager.resolveService(serviceInfo, m_resolveListener);
                }
            }

            @Override
            public void onServiceLost(NsdServiceInfo serviceInfo) {
                Log.d(TAG, "Service discovery lost");
            }
        };
    }

    private void initResolveListener() {
        m_resolveListener = new NsdManager.ResolveListener() {

            @Override
            public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
                Log.e(TAG, "Resolve failed: " + errorCode);
            }

            @Override
            public void onServiceResolved(NsdServiceInfo serviceInfo) {
                m_serviceInfo = serviceInfo;
                Log.e(TAG, "Resoved done: " + serviceInfo.getHost() + ":" + serviceInfo.getPort());
            }
        };
    }

    public void tearDown() {
        if(m_registrationListener != null) {
            m_nsdManager.unregisterService(m_registrationListener);
        }
        if(m_discoveryListener != null) {
            m_nsdManager.stopServiceDiscovery(m_discoveryListener);
        }
    }
}
