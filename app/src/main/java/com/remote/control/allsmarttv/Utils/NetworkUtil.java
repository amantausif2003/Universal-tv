package com.remote.control.allsmarttv.Utils;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;


public class NetworkUtil {
    public static boolean canDiscover(Context context) {
        if (context != null) {
            return isConnectedToNetwork(context) || isBluetoothAvailable(context);
        }
        return false;
    }

    public static BluetoothAdapter getBluetoothAdapter(Context context) {
        if (context == null || Build.VERSION.SDK_INT < 15 || context.checkCallingOrSelfPermission("android.permission.BLUETOOTH_ADMIN") != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        return BluetoothAdapter.getDefaultAdapter();
    }

    public static NetworkInfo getEthernetNetworkInfo(Context context) {
        if (context == null || Build.VERSION.SDK_INT < 13) {
            return null;
        }
        return NetworkHelperApi13.getEthernetNetworkInfo(context);
    }

    public static NetworkInfo getWifiNetworkInfo(Context context) {
        if (context != null) {
            return ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getNetworkInfo(1);
        }
        return null;
    }

    public static boolean isAvailable(NetworkInfo networkInfo) {
        return networkInfo != null && networkInfo.isAvailable();
    }

    public static boolean isBluetoothAvailable(Context context) {
        BluetoothAdapter bluetoothAdapter;
        return (context == null || (bluetoothAdapter = getBluetoothAdapter(context)) == null || !bluetoothAdapter.isEnabled()) ? false : true;
    }

    public static boolean isBluetoothPossible(Context context) {
        return (context == null || getBluetoothAdapter(context) == null) ? false : true;
    }

    public static boolean isConnected(NetworkInfo networkInfo) {
        return networkInfo != null && networkInfo.isConnected();
    }

    public static boolean isConnectedToEthernet(Context context) {
        if (context != null) {
            return isConnected(getEthernetNetworkInfo(context));
        }
        return false;
    }

    public static boolean isConnectedToNetwork(Context context) {
        if (context != null) {
            return isConnectedToWifi(context) || isConnectedToEthernet(context);
        }
        return false;
    }

    public static boolean isConnectedToWifi(Context context) {
        if (context != null) {
            return isConnected(getWifiNetworkInfo(context));
        }
        return false;
    }

    public static boolean isWifiAvailable(Context context) {
        if (context != null) {
            return isAvailable(getWifiNetworkInfo(context));
        }
        return false;
    }
}
