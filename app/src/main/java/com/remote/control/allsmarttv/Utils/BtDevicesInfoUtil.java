package com.remote.control.allsmarttv.Utils;

import android.bluetooth.BluetoothDevice;
import android.net.Uri;
import android.text.TextUtils;


public class BtDevicesInfoUtil extends DevicesInfoUtil {
    final BluetoothDevice mDevice;
    final String mName;

    BtDevicesInfoUtil(BluetoothDevice bluetoothDevice) {
        this(bluetoothDevice, bluetoothDevice.getName());
    }

    BtDevicesInfoUtil(BluetoothDevice bluetoothDevice, String str) {
        this.mDevice = bluetoothDevice;
        this.mName = str;
    }

    @Override
    public boolean equals(Object obj) {
        if (this != obj) {
            return obj != null && getClass() == obj.getClass() && super.equals(obj) && this.mDevice.equals(((BtDevicesInfoUtil) obj).mDevice);
        }
        return true;
    }

    public String getAddress() {
        return this.mDevice.getAddress();
    }

    @Override
    public CharSequence getName() {
        String name = this.mDevice.getName();
        return this.mOtherInfo == null ? (TextUtils.isEmpty(name) && TextUtils.isEmpty(this.mName)) ? this.mDevice.getAddress() : TextUtils.isEmpty(name) ? this.mName : name : this.mOtherInfo.getName();
    }

    @Override
    public Uri getUri() {
        return new Uri.Builder().scheme("bt").encodedAuthority(this.mDevice.getAddress()).fragment(getName().toString()).build();
    }

    @Override
    public int hashCode() {
        return (super.hashCode() * 31) + this.mDevice.hashCode();
    }
}
