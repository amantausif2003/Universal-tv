package com.remote.control.allsmarttv.utility

import android.util.Log
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.remote.control.allsmarttv.BuildConfig
import com.remote.control.allsmarttv.R

class FirebaseRemoteConfigUtils(callback: RemoteConfigCallBacks?) {

    var firebaseRemoteConfig: FirebaseRemoteConfig? = null
    var callback: RemoteConfigCallBacks? = null

    init {
        initialize(callback)
    }

    fun initialize(callback: RemoteConfigCallBacks?) {
        callback?.let {
            this.callback = callback
        }
        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(if (BuildConfig.DEBUG) 60 else 1000)
            .build()
        firebaseRemoteConfig!!.setConfigSettingsAsync(configSettings)
        firebaseRemoteConfig!!.fetchAndActivate().addOnCompleteListener { p0 ->
            if (p0.isSuccessful) {
                Log.d(
                    "remote config",
                    "${firebaseRemoteConfig?.getBoolean("getTemplatesByRewarded")}"
                )
                firebaseRemoteConfig?.getBoolean("getTemplatesByRewarded")
                callback?.remoteConfigInitilized(firebaseRemoteConfig!!)
                //all values fetched from server and stored in local object
            }
        }
        firebaseRemoteConfig!!.setDefaultsAsync(R.xml.remote_config_defaults)
    }

    fun getBoolean(key: String): Boolean? {
        this.firebaseRemoteConfig?.getBoolean(key)?.let {
            return this.firebaseRemoteConfig?.getBoolean(key)
        }
        return false
    }

    fun getString(key: String): String? {
        return this.firebaseRemoteConfig?.getString(key)
    }

    fun getLong(key: String): Long? {
        return this.firebaseRemoteConfig?.getLong(key)
    }

    interface RemoteConfigCallBacks {
        fun remoteConfigInitilized(firebaseRemoteConfig: FirebaseRemoteConfig)
    }
}