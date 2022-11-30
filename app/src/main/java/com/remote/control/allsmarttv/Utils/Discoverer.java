package com.remote.control.allsmarttv.Utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import android.util.ArrayMap;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public final class Discoverer {

    private static final String FORCE_LEGACY_PROP = "prop.android.tv.force_legacy_discoverer";
    private static final String FORCE_SYSTEM_PROP = "prop.android.tv.force_system_discoverer";
    private static final String SERVICE_TYPE = "_androidtvremote._tcp.";
    private final ArrayMap<String, DevicesInfoUtil> mBluetoothDevices = new ArrayMap<>();
    private final Context context;
    private final List<DiscoveryAgent> mDiscoveryAgents = new ArrayList();
    private final ArrayMap<String, WifiInfoUtil> mIpToWifiDevices = new ArrayMap<>();
    private DiscoveryAgent.Listener mLocalListener;
    private final ArrayMap<String, DevicesInfoUtil> mWifiDevices = new ArrayMap<>();

    public static abstract class DiscoveryListener {
        public abstract void onDeviceFound(DevicesInfoUtil devicesInfoUtil);

        public abstract void onDeviceLost(DevicesInfoUtil devicesInfoUtil);

        public abstract void onDeviceReplace(DevicesInfoUtil devicesInfoUtil, DevicesInfoUtil devicesInfoUtil2);

        public abstract void onDiscoveryStarted();

        public void onDiscoveryStopped() {
        }

        public abstract void onStartDiscoveryFailed(int i);
    }

    public Discoverer(Context context) {
        this.context = context;
        this.mDiscoveryAgents.addAll(getAgents(this.context));
    }

    static List<DiscoveryAgent> getAgents(Context context) {
        ArrayList arrayList = new ArrayList();
        boolean booleanValue = Boolean.valueOf(System.getProperty(FORCE_LEGACY_PROP, "false")).booleanValue();
        boolean booleanValue2 = Boolean.valueOf(System.getProperty(FORCE_SYSTEM_PROP, "false")).booleanValue();
        if (booleanValue && booleanValue2) {
            throw new IllegalStateException("You cannot force both Legacy and System Resolvers");
        }
        if (booleanValue) {
            arrayList.add(new LegacyNsdUtil(context, SERVICE_TYPE));
        } else if (booleanValue2) {
            arrayList.add(new LegacyNsdUtil(context, SERVICE_TYPE));
        } else {
            arrayList.add(new SystemNsdClass(context, SERVICE_TYPE));
        }
        if (Build.VERSION.SDK_INT >= 15 && context.checkCallingOrSelfPermission("android.permission.BLUETOOTH_ADMIN") == PackageManager.PERMISSION_GRANTED) {
            arrayList.add(new BluetoothAgent(context));
            if (Build.VERSION.SDK_INT >= 21) {
                arrayList.add(new BLeAgent());
            }
        }
        return arrayList;
    }

    private WifiInfoUtil replaceOldWifiWithNew(DevicesInfoUtil devicesInfoUtil) {
        WifiInfoUtil wifiDeviceInfo = (WifiInfoUtil) devicesInfoUtil;
        return this.mIpToWifiDevices.put(wifiDeviceInfo.getUri().getAuthority(), wifiDeviceInfo);
    }

    public void destroy() {
        for (DiscoveryAgent discoveryAgent : this.mDiscoveryAgents) {
            discoveryAgent.destroy();
        }
    }

    public int onDevice(DevicesInfoUtil devicesInfoUtil, boolean z) {
        if (devicesInfoUtil instanceof WifiInfoUtil) {
            WifiInfoUtil wifiDeviceInfo = (WifiInfoUtil) devicesInfoUtil;
            String txtEntry = wifiDeviceInfo.getTxtEntry("bt");
            if (!TextUtils.isEmpty(txtEntry)) {
                if (this.mBluetoothDevices.containsKey(txtEntry)) {
                    this.mBluetoothDevices.get(txtEntry).setOtherDeviceInfo(devicesInfoUtil);
                    return 0;
                } else if (!z) {
                    this.mWifiDevices.remove(txtEntry);
                } else {
                    this.mWifiDevices.put(txtEntry, devicesInfoUtil);
                }
            }
            String authority = wifiDeviceInfo.getUri().getAuthority();
            WifiInfoUtil wifiDeviceInfo2 = this.mIpToWifiDevices.get(authority);
            if (wifiDeviceInfo2 != null) {
                boolean equals = wifiDeviceInfo2.equals(devicesInfoUtil);
                if (z && equals) {
                    return 0;
                }
                if (z && !equals) {
                    return 3;
                }
                if (!z && equals) {
                    this.mIpToWifiDevices.remove(authority);
                }
            } else if (z) {
                this.mIpToWifiDevices.put(authority, wifiDeviceInfo);
            }
        } else if (devicesInfoUtil instanceof BtDevicesInfoUtil) {
            String address = ((BtDevicesInfoUtil) devicesInfoUtil).getAddress();
            if (this.mWifiDevices.containsKey(address)) {
                this.mWifiDevices.get(address).setOtherDeviceInfo(devicesInfoUtil);
                return 0;
            } else if (!z) {
                this.mBluetoothDevices.remove(address);
            } else {
                this.mBluetoothDevices.put(address, devicesInfoUtil);
            }
        }
        return !z ? 2 : 1;
    }

    public void startDiscovery(final DiscoveryListener discoveryListener, final Handler handler) {
        if (this.mLocalListener != null) {
            stopDiscovery();
        }
        this.mLocalListener = new DiscoveryAgent.Listener() {
            private int mStartedCount = 0;
            private final Object mStartedCountLock = new Object();

            @Override
            public void onDeviceFound(final DevicesInfoUtil devicesInfoUtil) {
                int onDevice = Discoverer.this.onDevice(devicesInfoUtil, true);
                if (onDevice == 1) {
                    handler.post(new Runnable() {

                        public void run() {
                            discoveryListener.onDeviceFound(devicesInfoUtil);
                        }
                    });
                } else if (onDevice == 3) {
                    handler.post(new Runnable() {

                        public void run() {
                            discoveryListener.onDeviceReplace(Discoverer.this.replaceOldWifiWithNew(devicesInfoUtil), devicesInfoUtil);
                        }
                    });
                }
            }

            @Override
            public void onDeviceLost(final DevicesInfoUtil devicesInfoUtil) {
                if (Discoverer.this.onDevice(devicesInfoUtil, false) == 2) {
                    handler.post(new Runnable() {

                        public void run() {
                            discoveryListener.onDeviceLost(devicesInfoUtil);
                        }
                    });
                }
            }

            @Override
            public void onDiscoveryStarted() {
                synchronized (this.mStartedCountLock) {
                    int i = this.mStartedCount + 1;
                    this.mStartedCount = i;
                    if (1 == i) {
                        handler.post(new Runnable() {

                            public void run() {
                                discoveryListener.onDiscoveryStarted();
                            }
                        });
                    }
                }
            }

            @Override
            public void onDiscoveryStopped() {
                synchronized (this.mStartedCountLock) {
                    int i = this.mStartedCount - 1;
                    this.mStartedCount = i;
                    if (i == 0) {
                        handler.post(new Runnable() {

                            public void run() {
                                discoveryListener.onDiscoveryStopped();
                            }
                        });
                    }
                }
            }

            @Override
            public void onStartDiscoveryFailed(final int i) {
                handler.post(new Runnable() {

                    public void run() {
                        discoveryListener.onStartDiscoveryFailed(i);
                    }
                });
            }
        };
        for (DiscoveryAgent discoveryAgent : this.mDiscoveryAgents) {
            discoveryAgent.startDiscovery(this.mLocalListener, handler);
        }
    }

    public void stopDiscovery() {
        if (this.mLocalListener != null) {
            for (DiscoveryAgent discoveryAgent : this.mDiscoveryAgents) {
                discoveryAgent.stopDiscovery();
            }
            this.mLocalListener = null;
        }
        this.mWifiDevices.clear();
        this.mBluetoothDevices.clear();
        this.mIpToWifiDevices.clear();
    }
}
