package com.remote.control.allsmarttv.utils;

import android.content.Context;
import android.os.Handler;

public final class LegacyNsdUtil extends DiscoveryAgent {

    private final Context mContext;
    private LegacyListener mLocalListener;
    private final MdnsScannerUtil mScanner;
    private final String mServiceType;

    public class LegacyListener implements DeviceScanner.Listener {
        private final Listener mListener;
        private final MdnsScannerUtil mScanner;

        public LegacyListener(Listener listener, MdnsScannerUtil mdnsDeviceScanner) {
            this.mListener = listener;
            this.mScanner = mdnsDeviceScanner;
        }

        private boolean validDevice(NetworkDeviceUtil networkDeviceUtil) {
            return (networkDeviceUtil == null && (networkDeviceUtil.getServiceType() == null || networkDeviceUtil.getServiceName() == null || networkDeviceUtil.getHost() == null)) ? false : true;
        }

        @Override
        public void onAllDevicesOffline() {
            for (NetworkDeviceUtil networkDeviceUtil : this.mScanner.getDevices()) {
                onDeviceOffline(networkDeviceUtil);
            }
        }

        @Override
        public void onDeviceOffline(NetworkDeviceUtil networkDeviceUtil) {
            if (validDevice(networkDeviceUtil)) {
                this.mListener.onDeviceLost(LegacyNsdUtil.convert(networkDeviceUtil));
            }
        }

        @Override
        public void onDeviceOnline(NetworkDeviceUtil networkDeviceUtil) {
            if (validDevice(networkDeviceUtil)) {
                this.mListener.onDeviceFound(LegacyNsdUtil.convert(networkDeviceUtil));
            }
        }

        @Override
        public void onDeviceStateChanged(NetworkDeviceUtil networkDeviceUtil) {
            if (validDevice(networkDeviceUtil)) {
                this.mListener.onDeviceFound(LegacyNsdUtil.convert(networkDeviceUtil));
            }
        }

        @Override
        public void onScanStateChanged(int i) {
            switch (i) {
                case 0:
                    this.mListener.onDiscoveryStopped();
                    return;
                case 1:
                    this.mListener.onDiscoveryStarted();
                    return;
                case 2:
                    LegacyNsdUtil.this.stopDiscovery();
                    return;
                default:
                    return;
            }
        }
    }

    LegacyNsdUtil(Context context, String str) {
        this.mContext = context;
        this.mServiceType = str + "local.";
        mScanner = new MdnsScannerUtil(this.mContext, this.mServiceType);
    }


    public static WifiInfoUtil convert(NetworkDeviceUtil networkDeviceUtil) {
        return new WifiInfoUtil(networkDeviceUtil.getHost(), networkDeviceUtil.getPort(), networkDeviceUtil.getServiceType(), networkDeviceUtil.getServiceName(), networkDeviceUtil.getTxtEntries());
    }

    @Override
    public final void startDiscovery(Listener listener, Handler handler) {
        if (this.mLocalListener != null) {
            stopDiscovery();
        }
        this.mLocalListener = new LegacyListener(listener, this.mScanner);
        this.mScanner.addListener(this.mLocalListener);
        this.mScanner.startScan();
    }

    @Override
    public final void stopDiscovery() {
        if (this.mLocalListener != null) {
            this.mScanner.stopScan();
            this.mScanner.removeListener(this.mLocalListener);
            this.mScanner.clearDevices();
            this.mLocalListener = null;
        }
    }
}
