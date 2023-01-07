package com.remote.control.allsmarttv.utils;

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.os.Handler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


final class SystemNsdClass extends DiscoveryAgent {

    private final ExecutorService mExecutorService = Executors.newSingleThreadExecutor();
    private NsdDiscoveryListener mLocalListener;
    private final NsdManager mNsdManager;
    private final String mServiceType;

    public class NsdDiscoveryListener implements NsdManager.DiscoveryListener {
        private final Listener mListener;

        private NsdDiscoveryListener(Listener listener) {
            this.mListener = listener;
        }

        public void onDiscoveryStarted(String str) {
            this.mListener.onDiscoveryStarted();
        }

        public void onDiscoveryStopped(String str) {
            this.mListener.onDiscoveryStopped();
        }

        public void onServiceFound(final NsdServiceInfo nsdServiceInfo) {
            SystemNsdClass.this.mExecutorService.submit(new Runnable() {

                public void run() {
                    SystemNsdClass.this.resolveService(nsdServiceInfo, new ResolveListener() {

                        @Override
                        public void onResolveFailed(DevicesInfoUtil devicesInfoUtil, int i) {
                        }

                        @Override
                        public void onServiceResolved(DevicesInfoUtil devicesInfoUtil) {
                            NsdDiscoveryListener.this.mListener.onDeviceFound(devicesInfoUtil);
                        }
                    });
                }
            });
        }

        public void onServiceLost(final NsdServiceInfo nsdServiceInfo) {
            SystemNsdClass.this.mExecutorService.submit(new Runnable() {

                public void run() {
                    SystemNsdClass.this.resolveService(nsdServiceInfo, new ResolveListener() {

                        @Override
                        public void onResolveFailed(DevicesInfoUtil devicesInfoUtil, int i) {
                        }

                        @Override
                        public void onServiceResolved(DevicesInfoUtil devicesInfoUtil) {
                            NsdDiscoveryListener.this.mListener.onDeviceLost(devicesInfoUtil);
                        }
                    });
                }
            });
        }

        public void onStartDiscoveryFailed(String str, int i) {
            this.mListener.onStartDiscoveryFailed(i);
        }

        public void onStopDiscoveryFailed(String str, int i) {
        }
    }

    public class NsdResolveListener implements NsdManager.ResolveListener {
        private final ResolveListener mListener;

        private NsdResolveListener(ResolveListener resolveListener) {
            this.mListener = resolveListener;
        }

        public void onResolveFailed(NsdServiceInfo nsdServiceInfo, int i) {
            this.mListener.onResolveFailed(null, i);
        }

        public void onServiceResolved(NsdServiceInfo nsdServiceInfo) {
            this.mListener.onServiceResolved(SystemNsdClass.this.convert(nsdServiceInfo));
        }
    }

    public interface ResolveListener {
        void onResolveFailed(DevicesInfoUtil devicesInfoUtil, int i);

        void onServiceResolved(DevicesInfoUtil devicesInfoUtil);
    }

    public SystemNsdClass(Context context, String str) {
        this.mServiceType = str;
        this.mNsdManager = (NsdManager) context.getSystemService(Context.NSD_SERVICE);
    }

    private WifiInfoUtil convert(NsdServiceInfo nsdServiceInfo) {
        return new WifiInfoUtil(nsdServiceInfo.getHost(), nsdServiceInfo.getPort(), nsdServiceInfo.getServiceType(), nsdServiceInfo.getServiceName());
    }

    public final void resolveService(NsdServiceInfo nsdServiceInfo, ResolveListener resolveListener) {
        this.mNsdManager.resolveService(nsdServiceInfo, new NsdResolveListener(resolveListener));
    }

    @Override
    public void startDiscovery(Listener listener, Handler handler) {
        if (this.mLocalListener != null) {
            stopDiscovery();
        }
        this.mLocalListener = new NsdDiscoveryListener(listener);
        this.mNsdManager.discoverServices(this.mServiceType, 1, this.mLocalListener);
    }

    @Override
    public void stopDiscovery() {
        if (this.mLocalListener != null) {
            this.mNsdManager.stopServiceDiscovery(this.mLocalListener);
            this.mLocalListener = null;
        }
    }
}
