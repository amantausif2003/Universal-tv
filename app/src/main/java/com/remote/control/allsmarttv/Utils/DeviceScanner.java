package com.remote.control.allsmarttv.Utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class DeviceScanner {
    private static final String TAG = "AtvRemote.DvcScanner";
    private ConnectivityChangeReceiver connectivityChangeReceiver;
    private final ConnectivityManager connectivityManager;
    private final Context context;
    private String mLatestBssid;
    private final List<Listener> mListeners;
    private boolean scanActive;
    private final AtomicBoolean scanErrorLatch;
    private boolean mScanErrorState;
    private boolean mScanning;
    private volatile boolean shouldStopScan;
    private int state = 0;
    private final Handler uiHandler;
    private final WifiManager wifiManager;

    public class ConnectivityChangeReceiver extends BroadcastReceiver {
        private ConnectivityChangeReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            boolean z = false;
            NetworkInfo activeNetworkInfo = DeviceScanner.this.connectivityManager.getActiveNetworkInfo();
            if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
                z = true;
            }
            boolean updateBssid = DeviceScanner.this.updateBssid();
            if (!z) {
                DeviceScanner.this.clearDevices();
            }
            if (DeviceScanner.this.scanActive && updateBssid) {
                DeviceScanner.this.stopScanInit();
            }
            if (z) {
                DeviceScanner.this.startScanInit();
            } else if (!DeviceScanner.this.mScanErrorState) {
                DeviceScanner.this.reportNetworkErrorAsync();
            }
        }
    }

    public interface Listener {
        void onAllDevicesOffline();

        void onDeviceOffline(NetworkDeviceUtil networkDeviceUtil);

        void onDeviceOnline(NetworkDeviceUtil networkDeviceUtil);

        void onDeviceStateChanged(NetworkDeviceUtil networkDeviceUtil);

        void onScanStateChanged(int i);
    }

    protected DeviceScanner(Context context) {
        this.context = context;
        this.uiHandler = new Handler(Looper.getMainLooper());
        this.mListeners = new ArrayList();
        this.scanErrorLatch = new AtomicBoolean();
        this.connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        this.wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    }

    private void registerForNetworkConnectivityChanges() {
        if (this.connectivityChangeReceiver == null) {
            this.connectivityChangeReceiver = new ConnectivityChangeReceiver();
            this.context.registerReceiver(this.connectivityChangeReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
        }
    }

    private List<NetworkInterface> selectNetworkInterfaces() {
        ArrayList arrayList = new ArrayList();
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            if (networkInterfaces == null) {
                return arrayList;
            }
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface nextElement = networkInterfaces.nextElement();
                if (nextElement.isUp() && !nextElement.isLoopback() && !nextElement.isPointToPoint() && nextElement.supportsMulticast()) {
                    for (InterfaceAddress interfaceAddress : nextElement.getInterfaceAddresses()) {
                        if (interfaceAddress.getAddress() instanceof Inet4Address) {
                            arrayList.add(nextElement);
                        }
                    }
                }
            }
            return arrayList;
        } catch (IOException e) {
            Log.d(TAG, "Exception while selecting network interface", e);
            return null;
        }
    }


    private void startScanInit() {
        this.mScanErrorState = false;
        this.shouldStopScan = false;
        if (updateBssid() || !this.scanActive) {
            this.scanActive = true;
            startScanInternal(selectNetworkInterfaces());
        }
    }


    private void stopScanInit() {
        this.shouldStopScan = true;
        this.scanActive = false;
        stopScanInternal();
    }

    private void unregisterForNetworkConnectivityChanges() {
        if (this.connectivityChangeReceiver != null) {
            try {
                this.context.unregisterReceiver(this.connectivityChangeReceiver);
            } catch (IllegalArgumentException e) {
            }
            this.connectivityChangeReceiver = null;
        }
    }


    private boolean updateBssid() {
        boolean z = false;
        String str = null;
        WifiInfo connectionInfo = this.wifiManager.getConnectionInfo();
        if (connectionInfo != null) {
            str = connectionInfo.getBSSID();
        }
        if (this.mLatestBssid == null || str == null || !this.mLatestBssid.equals(str)) {
            z = true;
        }
        if (z) {
            clearDevices();
        }
        this.mLatestBssid = str;
        return z;
    }

    public final void addListener(Listener listener) throws IllegalArgumentException {
        if (listener != null) {
            synchronized (this.mListeners) {
                if (!this.mListeners.contains(listener)) {
                    this.mListeners.add(listener);
                } else {
                    throw new IllegalArgumentException("the same listener cannot be added twice");
                }
            }
            return;
        }
        throw new IllegalArgumentException("listener cannot be null");
    }

    public abstract void clearDevices();


    public final List<Listener> cloneListenerList() {
        ArrayList arrayList = null;
        synchronized (this.mListeners) {
            if (!this.mListeners.isEmpty()) {
                arrayList = new ArrayList(this.mListeners);
            }
        }
        return arrayList;
    }


    public final Context getContext() {
        return this.context;
    }

    public abstract List<NetworkDeviceUtil> getDevices();


    public final Handler getUiHandler() {
        return this.uiHandler;
    }

    public abstract void markDeviceInvalid(String str);


    public final void notifyAllDevicesOffline() {
        final List<Listener> cloneListenerList = cloneListenerList();
        if (cloneListenerList != null) {
            this.uiHandler.post(new Runnable() {

                public void run() {
                    for (Listener listener : cloneListenerList) {
                        listener.onAllDevicesOffline();
                    }
                }
            });
        }
    }


    public final void notifyDeviceOffline(final NetworkDeviceUtil networkDeviceUtil) {
        final List<Listener> cloneListenerList = cloneListenerList();
        if (cloneListenerList != null) {
            this.uiHandler.post(new Runnable() {

                public void run() {
                    for (Listener listener : cloneListenerList) {
                        listener.onDeviceOffline(networkDeviceUtil);
                    }
                }
            });
        }
    }


    public final void notifyDeviceOnline(final NetworkDeviceUtil networkDeviceUtil) {
        final List<Listener> cloneListenerList = cloneListenerList();
        if (cloneListenerList != null) {
            this.uiHandler.post(new Runnable() {

                public void run() {
                    for (Listener listener : cloneListenerList) {
                        listener.onDeviceOnline(networkDeviceUtil);
                    }
                }
            });
        }
    }


    public final void notifyDeviceStateChanged(final NetworkDeviceUtil networkDeviceUtil) {
        final List<Listener> cloneListenerList = cloneListenerList();
        if (cloneListenerList != null) {
            this.uiHandler.post(new Runnable() {

                public void run() {
                    for (Listener listener : cloneListenerList) {
                        listener.onDeviceStateChanged(networkDeviceUtil);
                    }
                }
            });
        }
    }


    public final void notifyStateChanged(final int i) {
        if (this.state != i) {
            this.state = i;
            final List<Listener> cloneListenerList = cloneListenerList();
            if (cloneListenerList != null) {
                this.uiHandler.post(new Runnable() {

                    public void run() {
                        for (Listener listener : cloneListenerList) {
                            listener.onScanStateChanged(i);
                        }
                    }
                });
            }
        }
    }

    public final void removeAllListeners() {
        synchronized (this.mListeners) {
            this.mListeners.clear();
        }
    }

    public final void removeListener(Listener listener) throws IllegalArgumentException {
        if (listener != null) {
            synchronized (this.mListeners) {
                this.mListeners.remove(listener);
            }
            return;
        }
        throw new IllegalArgumentException("listener cannot be null");
    }


    public void repeatStartScanInternal() {
        startScanInternal(selectNetworkInterfaces());
    }


    public final void reportNetworkError() {
        if (!this.mScanErrorState) {
            this.mScanErrorState = true;
            clearDevices();
            notifyStateChanged(2);
        }
    }


    public final void reportNetworkErrorAsync() {
        if (!this.scanErrorLatch.getAndSet(true)) {
            this.uiHandler.post(new Runnable() {

                public void run() {
                    DeviceScanner.this.reportNetworkError();
                }
            });
        }
    }


    public final boolean shouldStopScan() {
        return this.shouldStopScan;
    }

    public final void startScan() {
        if (!this.mScanning) {
            this.mScanning = true;
            notifyStateChanged(1);
            registerForNetworkConnectivityChanges();
            startScanInit();
        }
    }


    public abstract void startScanInternal(List<NetworkInterface> list);

    public final void stopScan() {
        if (this.mScanning) {
            unregisterForNetworkConnectivityChanges();
            stopScanInit();
            this.uiHandler.removeCallbacksAndMessages(null);
            this.mScanning = false;
            notifyStateChanged(0);
        }
    }


    public abstract void stopScanInternal();
}
