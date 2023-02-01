package com.remote.control.allsmarttv.adManager

import android.app.Activity
import android.util.Log
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.remote.control.allsmarttv.BuildConfig
import com.remote.control.allsmarttv.utils.ir_utils.App

object AdManager {

    //Note! this id must b change not a Sample id interstitial Ads
    private var interstitialAdsSample: String = "ca-app-pub-3940256099942544/1033173712"

    //This is default id
    private var interstitialAds: String = "ca-app-pub-3043189731871157/4832807745"

    private var interstitialAds1: String = "ca-app-pub-3043189731871157/4832807745"
    private var interstitialAds2: String = "ca-app-pub-3043189731871157/6026469914"

    var bannerAd1 :String = "ca-app-pub-3043189731871157/8652633255"
    var bannerAd2 :String = "ca-app-pub-3043189731871157/3550719502"

    private var mInterstitialAd: InterstitialAd? = null
    private val adRequest: AdRequest = AdRequest.Builder().build()
    private var callBackInterstitial: CallBackInterstitial? = null
    private const val myTag: String = "myInterstitialAds"
    private var InterstitialStatus: Boolean = true

    private var bannerStatusId: Boolean = false

    fun getBannerID(): String {
        return if (BuildConfig.DEBUG) {
            "ca-app-pub-3940256099942544/6300978111"
        } else {
            if (bannerStatusId) {
                bannerStatusId = false
                return "ca-app-pub-3043189731871157/8652633255"
            } else {
                bannerStatusId = true
                return "ca-app-pub-3043189731871157/3550719502"
            }
        }
    }

    @JvmStatic
    fun loadInterstitialAd() {

        if (BuildConfig.DEBUG) {
            interstitialAds = interstitialAdsSample
        } else {

            interstitialAds = if (InterstitialStatus) {
                interstitialAds1
            } else {
                interstitialAds2
            }

            InterstitialStatus = !InterstitialStatus

        }


        if (mInterstitialAd == null) {

            InterstitialAd.load(
                App.context,
                interstitialAds,
                adRequest,
                object : InterstitialAdLoadCallback() {
                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        Log.d(myTag, adError.message)
                        mInterstitialAd = null
                    }

                    override fun onAdLoaded(interstitialAd: InterstitialAd) {
                        Log.d(myTag, "Ad was loaded.")
                        mInterstitialAd = interstitialAd

                    }
                })

        } else {
            Log.d(myTag, "InterstitialAd is already loaded.")
        }

    }

    @JvmStatic
    fun isInterstialLoaded(): Boolean {
        return if (mInterstitialAd == null) {
            loadInterstitialAd()
            false
        } else {
            true
        }
    }

    @JvmStatic
    fun showInterstitial(activity: Activity, callBack: CallBackInterstitial) {
        if (mInterstitialAd != null) {
            callBackInterstitial = callBack
            mInterstitialAd?.show(activity)
            callBackInterstitialAd()
        } else {
            Log.d(myTag, "The interstitial ad  ready yet.")
            loadInterstitialAd()
        }
    }

    private fun callBackInterstitialAd() {

        mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                callBackInterstitial?.interstitialDismissedFullScreenContent()
                Log.d(myTag, "Ad was dismissed.")
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                callBackInterstitial?.interstitialFailedToShowFullScreenContent(adError)
                Log.d(myTag, "Ad failed to show.")
            }

            override fun onAdShowedFullScreenContent() {
                Log.d(myTag, "Ad showed fullscreen content.")
                callBackInterstitial?.interstitialShowedFullScreenContent()
                mInterstitialAd = null
                loadInterstitialAd()
            }
        }
    }

    interface CallBackInterstitial {
        fun interstitialDismissedFullScreenContent()
        fun interstitialFailedToShowFullScreenContent(adError: AdError?)
        fun interstitialShowedFullScreenContent()
    }

}