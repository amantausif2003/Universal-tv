package com.remote.control.allsmarttv.utils.ir_utils

import androidx.multidex.MultiDexApplication
import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.androidstudy.networkmanager.Tovuti
import com.google.firebase.FirebaseApp
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.remote.control.allsmarttv.utility.FirebaseRemoteConfigUtils
import com.remote.control.allsmarttv.utils.Util

class App : MultiDexApplication() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        context = this
        FirebaseApp.initializeApp(this)

        Tovuti.from(context).monitor { _: Int, isConnected: Boolean, _: Boolean ->

            if (isConnected) {

                FirebaseRemoteConfigUtils(object : FirebaseRemoteConfigUtils.RemoteConfigCallBacks {
                    override fun remoteConfigInitilized(firebaseRemoteConfig: FirebaseRemoteConfig) {

                        Log.d("adsControl","${firebaseRemoteConfig.getBoolean(Util.show_banner)} -- ${firebaseRemoteConfig.getBoolean(Util.show_interstitial)}")

                        Util.setSharedPreferences(
                            Util.show_banner,
                            "${firebaseRemoteConfig.getBoolean(Util.show_banner)}"
                        )

                        Util.setSharedPreferences(
                            Util.show_interstitial,
                            "${firebaseRemoteConfig.getBoolean(Util.show_interstitial)}"
                        )

                    }
                })
            }
        }
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
    }

}