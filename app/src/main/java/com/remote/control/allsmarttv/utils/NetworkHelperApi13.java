package com.remote.control.allsmarttv.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkHelperApi13 {
    public static NetworkInfo getEthernetNetworkInfo(Context context) {
        if (context != null) {
            return ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getNetworkInfo(9);
        }
        return null;
    }
}
