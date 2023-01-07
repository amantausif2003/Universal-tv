package com.remote.control.allsmarttv.utils;

import android.bluetooth.BluetoothAdapter;
import android.net.Uri;

import java.net.InetAddress;

public abstract class DevicesInfoUtil implements Comparable<DevicesInfoUtil> {
    DevicesInfoUtil mOtherInfo;

    public static DevicesInfoUtil fromUri(Uri uri) {
        try {
            if ("tcp".equals(uri.getScheme())) {
                try {
                    byte[] bArr = new byte[4];
                    String[] split = uri.getHost().split("\\.");
                    for (int i = 0; i < 4; i++) {
                        bArr[i] = (byte) ((byte) Integer.parseInt(split[i]));
                    }
                    InetAddress byAddress = InetAddress.getByAddress(bArr);
                    int port = uri.getPort();
                    if (6465 == port) {
                        port = 6466;
                    }
                    return new WifiInfoUtil(byAddress, port, uri.getPath(), uri.getFragment());
                } catch (Exception e) {
                    throw new IllegalArgumentException("Unsupported URI");
                }
            } else if (!"bt".equals(uri.getScheme())) {
                throw new IllegalArgumentException("Unsupported URI");
            } else {
                BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
                if (defaultAdapter != null) {
                    return new BtDevicesInfoUtil(defaultAdapter.getRemoteDevice(uri.getAuthority()), uri.getFragment());
                }
                throw new IllegalStateException("No bluetooth");
            }
        }
        catch (Exception e)
        {}

        return null;
    }

    public int compareTo(DevicesInfoUtil devicesInfoUtil) {
        return getUri().compareTo(devicesInfoUtil.getUri());
    }

    public boolean equals(Object obj) {
        if (this != obj) {
            return (obj instanceof DevicesInfoUtil) && getUri().equals(((DevicesInfoUtil) obj).getUri());
        }
        return true;
    }

    public abstract CharSequence getName();

    public abstract Uri getUri();

    public int hashCode() {
        return getUri().hashCode();
    }

    public void setOtherDeviceInfo(DevicesInfoUtil devicesInfoUtil) {
        this.mOtherInfo = devicesInfoUtil;
    }

    public String toString() {
        return String.format("%s (%s)", getName().toString(), getUri());
    }
}
