package com.remote.control.allsmarttv.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MdnsScannerUtil extends DeviceScanner {

    private static final String TAG = "AtvRemote.MdnsDvcScnner";
    private final Map<String, DeviceRecord> mDeviceRecords = new HashMap();
    private final Handler mHandler = new Handler() {
        /* class MdnsDeviceScanner.AnonymousClass1 */

        public void handleMessage(Message message) {
            switch (message.what) {
                case 1:
                    if (MdnsScannerUtil.this.mRefreshThread != null) {
                        MdnsScannerUtil.this.stopScanInternal();
                    }
                    MdnsScannerUtil.this.repeatStartScanInternal();
                    return;
                default:
                    return;
            }
        }
    };
    private final List<MdnsClientUtil> mMdnsClientUtils = new ArrayList();
    private Thread mRefreshThread;
    private final String mRemoteServiceType;
    private boolean mShouldStop;

    public class DeviceRecord {
        NetworkDeviceUtil device;
        boolean invalid;
        long lastUpdateTimestamp = SystemClock.elapsedRealtime();
        final long mExpirationInterval;
        long timeToLive;

        DeviceRecord(NetworkDeviceUtil networkDeviceUtil, long j) {
            this.device = networkDeviceUtil;
            this.timeToLive = j;
            this.mExpirationInterval = ((long) (new Random().nextDouble() * 15000.0d)) + Math.min(1000 * j, 30000L);
        }

        public boolean isExpired(long j) {
            return !(((j - this.lastUpdateTimestamp) > this.mExpirationInterval ? 1 : ((j - this.lastUpdateTimestamp) == this.mExpirationInterval ? 0 : -1)) < 0);
        }
    }

    public MdnsScannerUtil(Context context, String str) {
        super(context);
        this.mRemoteServiceType = str;
    }

    private void logResponse(MdnsResUtil mdnsResUtil) {
    }

    private void postRepeatScan() {
        if (this.mHandler.hasMessages(1)) {
            this.mHandler.removeMessages(1);
        }
        this.mHandler.sendEmptyMessageDelayed(1, 1000);
    }

    private void processResponse(MdnsResUtil mdnsResUtil) {
        NetworkDeviceUtil networkDeviceUtil;
        String serviceInstanceName = mdnsResUtil.getServiceInstanceName();
        synchronized (this.mDeviceRecords) {
            List<Inet4Address> inet4Addresses = mdnsResUtil.getInet4Addresses();
            if (inet4Addresses != null && !inet4Addresses.isEmpty()) {
                NetworkDeviceUtil networkDeviceUtil2 = new NetworkDeviceUtil(mdnsResUtil.getServiceName(), inet4Addresses.get(0), mdnsResUtil.getServiceInstanceName(), mdnsResUtil.getServicePort(), mdnsResUtil.getTextStrings());
                DeviceRecord deviceRecord = this.mDeviceRecords.get(serviceInstanceName);
                if (deviceRecord == null) {
                    networkDeviceUtil = null;
                } else if (!networkDeviceUtil2.equals(deviceRecord.device)) {
                    networkDeviceUtil = deviceRecord.device;
                    this.mDeviceRecords.remove(serviceInstanceName);
                    if (networkDeviceUtil == null) {
                    }
                } else {
                    if (!deviceRecord.invalid) {
                        deviceRecord.lastUpdateTimestamp = SystemClock.elapsedRealtime();
                    }
                    notifyDeviceStateChanged(networkDeviceUtil2);
                    return;
                }
                this.mDeviceRecords.put(serviceInstanceName, new DeviceRecord(networkDeviceUtil2, mdnsResUtil.getTimeToLive()));
            }
        }
    }

    private void refreshLoop() {
        while (!this.mShouldStop) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                if (this.mShouldStop) {
                    return;
                }
            }
            synchronized (this.mDeviceRecords) {
                long elapsedRealtime = SystemClock.elapsedRealtime();
                Iterator<Map.Entry<String, DeviceRecord>> it = this.mDeviceRecords.entrySet().iterator();
                while (it.hasNext()) {
                    DeviceRecord value = it.next().getValue();
                    if (value.isExpired(elapsedRealtime)) {
                        final NetworkDeviceUtil networkDeviceUtil = value.device;
                        getUiHandler().post(new Runnable() {

                            public void run() {
                                MdnsScannerUtil.this.notifyDeviceOffline(networkDeviceUtil);
                            }
                        });
                        it.remove();
                        postRepeatScan();
                    }
                }
            }
        }
    }

    @Override
    public void clearDevices() {
        boolean z = false;
        synchronized (this.mDeviceRecords) {
            if (!this.mDeviceRecords.isEmpty()) {
                this.mDeviceRecords.clear();
                z = true;
            }
        }
        if (z) {
            notifyAllDevicesOffline();
        }
    }

    @Override
    public List<NetworkDeviceUtil> getDevices() {
        ArrayList arrayList = new ArrayList();
        synchronized (this.mDeviceRecords) {
            for (DeviceRecord deviceRecord : this.mDeviceRecords.values()) {
                arrayList.add(deviceRecord.device);
            }
        }
        return arrayList;
    }

    @Override
    public void markDeviceInvalid(String str) {
        NetworkDeviceUtil networkDeviceUtil;
        synchronized (this.mDeviceRecords) {
            DeviceRecord deviceRecord = this.mDeviceRecords.get(str);
            if (deviceRecord == null) {
                networkDeviceUtil = null;
            } else {
                deviceRecord.lastUpdateTimestamp = SystemClock.elapsedRealtime();
                deviceRecord.invalid = true;
                networkDeviceUtil = deviceRecord.device;
            }
        }
        if (networkDeviceUtil != null) {
            notifyDeviceOffline(networkDeviceUtil);
        }
    }

    @Override
    public void startScanInternal(List<NetworkInterface> list) {
        if (!list.isEmpty()) {
            for (NetworkInterface networkInterface : list) {
                MdnsClientUtil r2 = new MdnsClientUtil(this.mRemoteServiceType, networkInterface) {

                    @Override
                    public void onResponseReceived(MdnsResUtil mdnsResponse) {
                        MdnsScannerUtil.this.processResponse(mdnsResponse);
                    }
                };
                try {
                    r2.start();
                    this.mMdnsClientUtils.add(r2);
                } catch (IOException e) {
                    Log.w(TAG, "Couldn't start MDNS client for " + networkInterface);
                }
            }
            this.mShouldStop = false;
            this.mRefreshThread = new Thread(new Runnable() {

                public void run() {
                    MdnsScannerUtil.this.refreshLoop();
                }
            });
            this.mRefreshThread.start();
        }
    }

    @Override
    public void stopScanInternal() {
        if (!this.mMdnsClientUtils.isEmpty()) {
            for (MdnsClientUtil mdnsClientUtil : this.mMdnsClientUtils) {
                mdnsClientUtil.stop();
            }
            this.mMdnsClientUtils.clear();
        }
        this.mShouldStop = true;
        if (this.mRefreshThread != null) {
            while (true) {
                try {
                    this.mRefreshThread.interrupt();
                    this.mRefreshThread.join();
                    break;
                } catch (InterruptedException e) {
                }
            }
            this.mRefreshThread = null;
        }
        if (this.mHandler.hasMessages(1)) {
            this.mHandler.removeMessages(1);
        }
    }
}
