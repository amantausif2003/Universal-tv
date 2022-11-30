package com.remote.control.allsmarttv.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

public class TvPreferences {
    public static final String CONNECTION_INFO_TAG = "-connection-info";
    public static final String CONTROL_MODE_KEY = "control_mode";
    public static final String LAST_HUB_SELECTED_KEY = "last_hub_selected.";
    public static final String PREF_KEY = "com.remote.tv.remote.android.tv.remote.PREFERENCES";
    public static boolean RETAIN_SERVICE_INSTANCE = true;
    public static final String TAG = "AtvRemote.RemotePrfrncs";

    public static String getHubKey(Context context) {
        WifiInfo connectionInfo = ((WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE)).getConnectionInfo();
        if (connectionInfo != null) {
            return LAST_HUB_SELECTED_KEY + connectionInfo.getSSID();
        }
        return LAST_HUB_SELECTED_KEY;
    }

    public static DevicesInfoUtil getDeviceInfo(Context context) {
        if (context == null) {
            return null;
        }
        String string = context.getSharedPreferences(PREF_KEY, 0).getString(getHubKey(context) + CONNECTION_INFO_TAG, null);
        if (string != null) {
            return DevicesInfoUtil.fromUri(Uri.parse(string));
        }
        return null;
    }

    public static void saveDeviceInfo(Context context, DevicesInfoUtil devicesInfoUtil) {
        if (context != null) {
            String hubKey = getHubKey(context);
            SharedPreferences.Editor edit = context.getSharedPreferences(PREF_KEY, 0).edit();
            if (devicesInfoUtil == null) {
                edit.putString(hubKey + CONNECTION_INFO_TAG, null);
            } else {
                edit.putString(hubKey + CONNECTION_INFO_TAG, devicesInfoUtil.getUri().toString());
            }
            if (!edit.commit()) {
                Log.e(TAG, "Failed to save device info!");
            }
        }
    }

    public static void setControl(Context context, int i) {
        if (context != null) {
            SharedPreferences.Editor edit = context.getSharedPreferences(PREF_KEY, 0).edit();
            edit.putInt(CONTROL_MODE_KEY, i);
            if (!edit.commit()) {
                Log.e(TAG, "Failed to save control mode");
            }
        }
    }

    public static int getControl(Context context) {
        if (context == null) {
            return 0;
        }
        return context.getSharedPreferences(PREF_KEY, 0).getInt(CONTROL_MODE_KEY, 0);
    }

}
