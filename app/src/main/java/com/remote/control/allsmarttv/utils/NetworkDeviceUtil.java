package com.remote.control.allsmarttv.utils;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.List;

public class NetworkDeviceUtil {
    private final String mInstanceName;
    private final Inet4Address mIpAddress;
    private final int mServicePort;
    private final String mServiceType;
    private final List<String> mTxtEntries;

    NetworkDeviceUtil(String str, Inet4Address inet4Address, String str2, int i) {
        this(str, inet4Address, str2, i, null);
    }

    NetworkDeviceUtil(String str, Inet4Address inet4Address, String str2, int i, List<String> list) {
        this.mServiceType = str;
        this.mIpAddress = inet4Address;
        this.mInstanceName = str2;
        this.mServicePort = i;
        this.mTxtEntries = list;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof NetworkDeviceUtil)) {
            return false;
        }
        NetworkDeviceUtil networkDeviceUtil = (NetworkDeviceUtil) obj;
        if (this.mIpAddress != null && networkDeviceUtil.mIpAddress != null && !this.mIpAddress.equals(networkDeviceUtil.mIpAddress)) {
            return false;
        }
        if (this.mServiceType == null || networkDeviceUtil.mServiceType == null || this.mServiceType.equals(networkDeviceUtil.mServiceType)) {
            return (this.mInstanceName == null || networkDeviceUtil.mInstanceName == null || this.mInstanceName.equals(networkDeviceUtil.mInstanceName)) && this.mServicePort == networkDeviceUtil.mServicePort;
        }
        return false;
    }

    public InetAddress getHost() {
        return this.mIpAddress;
    }

    public int getPort() {
        return this.mServicePort;
    }

    public String getServiceName() {
        return this.mInstanceName;
    }

    public String getServiceType() {
        return this.mServiceType;
    }

    public List<String> getTxtEntries() {
        return this.mTxtEntries;
    }

    public int hashCode() {
        int i = 0;
        if (this.mIpAddress != null) {
            i = this.mIpAddress.hashCode();
        }
        return i ^ this.mServicePort;
    }

    public String toString() {
        return String.format("\"%s\"", this.mInstanceName);
    }
}
