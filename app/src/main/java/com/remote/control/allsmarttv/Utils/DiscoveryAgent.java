package com.remote.control.allsmarttv.Utils;

import android.os.Handler;


public abstract class DiscoveryAgent {

    public static abstract class Listener {
        public abstract void onDeviceFound(DevicesInfoUtil devicesInfoUtil);

        public abstract void onDeviceLost(DevicesInfoUtil devicesInfoUtil);

        public abstract void onDiscoveryStarted();

        public abstract void onDiscoveryStopped();

        public abstract void onStartDiscoveryFailed(int i);
    }

    DiscoveryAgent() {
    }

    public void destroy() {
    }

    public abstract void startDiscovery(Listener listener, Handler handler);

    public abstract void stopDiscovery();
}
