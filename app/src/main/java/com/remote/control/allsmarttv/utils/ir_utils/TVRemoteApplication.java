package com.remote.control.allsmarttv.utils.ir_utils;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.multidex.MultiDexApplication;

import com.google.android.gms.ads.MobileAds;

public class TVRemoteApplication extends MultiDexApplication {

    @SuppressLint("StaticFieldLeak")
    public static Context context;

    public static Context getContext() {
        return context;
    }

    public static void setContext(Context context) {
        TVRemoteApplication.context = context;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setContext(getApplicationContext());
        MobileAds.initialize(this);

    }
}
