package com.remote.control.allsmarttv.utils;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.ArrayList;

public class WifiManagerClass {
    private final WifiManager mWifiManager;
    private Context mContext;

    public WifiManagerClass(Context context) {
        this.mContext = context;
        mWifiManager = (WifiManager) this.mContext.getSystemService(Context.WIFI_SERVICE);
    }

    public wifi_constant.WIFI_AP_STATE getWfState() {
        try {
            Method method = mWifiManager.getClass().getMethod("getWifiApState");

            int tmp = ((Integer) method.invoke(mWifiManager));

            if (tmp >= 10) {
                tmp = tmp - 10;
            }

            return wifi_constant.WIFI_AP_STATE.class.getEnumConstants()[tmp];
        } catch (Exception e) {
            Log.e(this.getClass().toString(), "", e);
            return wifi_constant.WIFI_AP_STATE.WIFI_AP_STATE_FAILED;
        }
    }

    public boolean isWifiEnabled() {
        return getWfState() == wifi_constant.WIFI_AP_STATE.WIFI_AP_STATE_ENABLED;
    }

    public ArrayList<ScanRokuUtil> getTvList(final boolean onlyReachable, final int rTimeout) {
        BufferedReader bufferedReader = null;
        final ArrayList<ScanRokuUtil> result = new ArrayList<ScanRokuUtil>();

        try {
            bufferedReader = new BufferedReader(new FileReader("/proc/net/arp"));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] splitted = line.split(" +");

                if ((splitted != null) && (splitted.length >= 4)) {
                    // Basic sanity check
                    String mac = splitted[3];

                    if (mac.matches("..:..:..:..:..:..")) {
                        boolean isReachable = InetAddress.getByName(splitted[0]).isReachable(rTimeout);

                        if (!onlyReachable || isReachable) {
                            result.add(new ScanRokuUtil(splitted[0], splitted[3], splitted[5], isReachable));
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e(this.getClass().toString(), e.toString());
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (IOException e) {
                Log.e(this.getClass().toString(), e.getMessage());
            }
        }

        return result;
    }
}
