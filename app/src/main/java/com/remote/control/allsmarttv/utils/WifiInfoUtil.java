package com.remote.control.allsmarttv.utils;

import android.net.Uri;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class WifiInfoUtil extends DevicesInfoUtil {
    private final InetAddress mHost;
    private final int mPort;
    private final String mServiceName;
    private final String mServiceType;
    private final Map<String, String> mTxtEntries;

    WifiInfoUtil(InetAddress inetAddress, int i, String str, String str2) {
        this(inetAddress, i, str, str2, null);
    }

    WifiInfoUtil(InetAddress inetAddress, int i, String str, String str2, List<String> list) {
        this.mHost = inetAddress;
        if (6465 != i) {
            this.mPort = i;
        } else {
            this.mPort = 6466;
        }
        this.mServiceType = str;
        this.mServiceName = str2;
        this.mTxtEntries = new HashMap();
        if (list != null) {
            for (String str3 : list) {
                int indexOf = str3.indexOf(61);
                if (indexOf >= 0) {
                    this.mTxtEntries.put(str3.substring(0, indexOf), str3.substring(indexOf + 1));
                }
            }
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof WifiInfoUtil)) {
            return false;
        }
        WifiInfoUtil wifiDeviceInfo = (WifiInfoUtil) obj;
        if (this.mHost != null && wifiDeviceInfo.mHost != null && !this.mHost.equals(wifiDeviceInfo.mHost)) {
            return false;
        }
        if (this.mServiceType == null || wifiDeviceInfo.mServiceType == null || this.mServiceType.equals(wifiDeviceInfo.mServiceType)) {
            return (this.mServiceName == null || wifiDeviceInfo.mServiceName == null || this.mServiceName.equals(wifiDeviceInfo.mServiceName)) && this.mPort == wifiDeviceInfo.mPort;
        }
        return false;
    }

    @Override
    public CharSequence getName() {
        return this.mServiceName;
    }

    public String getTxtEntry(String str) {
        return this.mTxtEntries.get(str);
    }

    @Override
    public Uri getUri() {
        return new Uri.Builder().scheme("tcp").encodedAuthority(this.mHost.getHostAddress() + ":" + this.mPort).encodedPath(this.mServiceType).fragment(this.mServiceName).build();
    }

    @Override
    public int hashCode() {
        int i = 0;
        if (this.mHost != null) {
            i = this.mHost.hashCode();
        }
        return i ^ this.mPort;
    }
}
