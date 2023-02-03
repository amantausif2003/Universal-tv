package com.remote.control.allsmarttv.appUi

import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.hardware.ConsumerIrManager
import android.net.Uri
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.remote.control.allsmarttv.activitiesUi.LgWifiRemoteActivity
import com.remote.control.allsmarttv.activitiesUi.RemoteActivity
import com.remote.control.allsmarttv.activitiesUi.RokuPair
import com.remote.control.allsmarttv.activitiesUi.ir.RemoteNameActivity
import com.remote.control.allsmarttv.irtv.SamsungRemoteActivity
import com.remote.control.allsmarttv.irtv.SonyRemote
import com.remote.control.allsmarttv.R
import com.remote.control.allsmarttv.utils.ir_utils.SupportedClass
import com.remote.control.allsmarttv.adManager.AdManager
import com.remote.control.allsmarttv.adManager.AdManager.CallBackInterstitial
import com.remote.control.allsmarttv.adManager.AdManager.isInterstialLoaded
import com.remote.control.allsmarttv.adManager.AdManager.loadInterstitialAd
import com.remote.control.allsmarttv.adManager.AdManager.showInterstitial
import com.remote.control.allsmarttv.databinding.ActivityMainBinding
import com.remote.control.allsmarttv.irtv.TCLRemote
import com.remote.control.allsmarttv.utils.Util
import com.remote.control.allsmarttv.utils.Util.showToast

class FirstActivity : AppCompatActivity(), CallBackInterstitial {

    private var isBtnClicked = 0
    private lateinit var mainBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SupportedClass.loadLangLocale(baseContext)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)

        mainBinding.menuBtn.setOnClickListener {
            mainBinding.drawerLayout.openDrawer(GravityCompat.START)
        }

        mainBinding.navigationView.setNavigationItemSelectedListener {

            when (it.itemId) {

                R.id.nav_privacy -> {
                    try {
                        val uri =
                            Uri.parse("https://docs.google.com/document/d/1bYIMnAVAwPrN0vGs9pZ8mSYP6MWgb-zhH7uQpLgyS8U/edit")
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.data = uri
                        startActivity(intent)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                R.id.nav_share -> {
                    SupportedClass.shareApp(this@FirstActivity)
                }
                R.id.nav_rate -> {
                    SupportedClass.rateApp(this@FirstActivity)
                }
                R.id.nav_feed_back -> {
                    SupportedClass.feedback(this@FirstActivity)
                }
                R.id.nav_more_app -> {
                    try {
                        //replace &quot;Unified+Apps&quot; with your developer name
                        startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://play.google.com/store/apps/developer?id=Game_Arcade")
                            )
                        )
                    } catch (ex: ActivityNotFoundException) {
                        ex.printStackTrace()
                    }
                }
            }
            true
        }

        mainBinding.language.setOnClickListener {
            try {
                SupportedClass.langDialog(this@FirstActivity, this@FirstActivity)
            } catch (ex: Exception) {
                Log.i("exception is::", ex.toString())
            }
        }

        Log.d("myInterstitial","${Util.showInterstitial}")

        if (Util.showInterstitial) {
            loadInterstitialAd()
        }

        if (Util.showBanner) {
            bannerAds()
        } else {
            mainBinding.adLayout.visibility = View.GONE
        }

        mainBinding.samsungTv.setOnClickListener {
            if (SupportedClass.checkConnection(this@FirstActivity)) {
                isBtnClicked = 0
                if (Util.showInterstitial && isInterstialLoaded()) {
                    showInterstitial(this@FirstActivity, this@FirstActivity)
                } else {
                    moveToRequireActivity()
                }
            } else {
                Toast.makeText(this@FirstActivity, R.string.check_internet, Toast.LENGTH_SHORT)
                    .show()
            }
        }

        mainBinding.sony.setOnClickListener {
            if (SupportedClass.checkConnection(this@FirstActivity)) {
                isBtnClicked = 1
                if (Util.showInterstitial && isInterstialLoaded()) {
                    showInterstitial(this@FirstActivity, this@FirstActivity)
                } else {
                    moveToRequireActivity()
                }
            } else {
                Toast.makeText(this@FirstActivity, R.string.check_internet, Toast.LENGTH_SHORT)
                    .show()
            }
        }
        mainBinding.addRemote.setOnClickListener {
            if (SupportedClass.checkConnection(this@FirstActivity)) {
                isBtnClicked = 2
                if (Util.showInterstitial && isInterstialLoaded()) {
                    showInterstitial(this@FirstActivity, this@FirstActivity)
                } else {
                    moveToRequireActivity()
                }
            } else {
                Toast.makeText(this@FirstActivity, R.string.check_internet, Toast.LENGTH_SHORT)
                    .show()
            }
        }
        mainBinding.androidTv.setOnClickListener {
            if (SupportedClass.checkConnection(this@FirstActivity)) {
                isBtnClicked = 3
                if (isInterstialLoaded()) {
                    showInterstitial(this@FirstActivity, this@FirstActivity)
                } else {
                    moveToRequireActivity()
                }
            } else {
                Toast.makeText(this@FirstActivity, R.string.check_internet, Toast.LENGTH_SHORT)
                    .show()
            }
        }

        mainBinding.rokuTv.setOnClickListener {

            showToast(this, "Up Coming")

            /*if (SupportedClass.checkConnection(this@FirstActivity)) {
                isBtnClicked = 4
                if (isInterstialLoaded()) {
                    showInterstitial(this@FirstActivity, this@FirstActivity)
                } else {
                    moveToRequireActivity()
                }
            } else {
                Toast.makeText(this@FirstActivity, R.string.check_internet, Toast.LENGTH_SHORT)
                    .show()
            }*/

        }

        mainBinding.lgTv.setOnClickListener {

            showToast(this, "Up Coming")

            /*if (SupportedClass.checkConnection(this@FirstActivity)) {
                isBtnClicked = 5
                if (isInterstialLoaded()) {
                    showInterstitial(this@FirstActivity, this@FirstActivity)
                } else {
                    moveToRequireActivity()
                }
            } else {
                Toast.makeText(this@FirstActivity, R.string.check_internet, Toast.LENGTH_SHORT)
                    .show()
            }*/
        }

        mainBinding.irRemote.setOnClickListener {

            showToast(this, "Up Coming")

            /*if (SupportedClass.checkConnection(this@FirstActivity)) {
                isBtnClicked = 6
                if (isInterstialLoaded()) {
                    showInterstitial(this@FirstActivity, this@FirstActivity)
                } else {
                    moveToRequireActivity()
                }
            } else {
                Toast.makeText(this@FirstActivity, R.string.check_internet, Toast.LENGTH_SHORT)
                    .show()
            }*/
        }

    }


    /**************************************Banner Ads *********************************/
    private fun bannerAds() {
        mainBinding.adLayout.visibility = View.VISIBLE
        mainBinding.adLayout.post { loadBanner() }
    }

    private fun loadBanner() {
        Log.e("myTag", "BannerAds")
        val adView = AdView(this)
        adView.adUnitId = AdManager.getBannerID()
        val adSize = adSize
        adView.setAdSize(adSize)
        mainBinding.adLayout.addView(adView)
        val adRequest = AdRequest.Builder().build()
        // Start loading the ad in the background.
        try {
            adView.loadAd(adRequest)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    // Determine the screen width (less decorations) to use for the ad width.
    @Suppress("DEPRECATION")
    private val adSize: AdSize
        get() {
            // Determine the screen width (less decorations) to use for the ad width.
            val display = windowManager.defaultDisplay
            val outMetrics = DisplayMetrics()
            display.getMetrics(outMetrics)
            val density = outMetrics.density
            var adWidthPixels = mainBinding.adLayout.width.toFloat()
            // If the ad hasn't been laid out, default to the full screen width.
            if (adWidthPixels == 0f) {
                adWidthPixels = outMetrics.widthPixels.toFloat()
            }
            val adWidth = (adWidthPixels / density).toInt()
            return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth)
        }

    override fun onBackPressed() {
        if (mainBinding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            mainBinding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            finishAffinity()
        }
    }

    private fun moveToRequireActivity() {
        if (isBtnClicked == 0) {
            startActivity(
                Intent(this@FirstActivity, SamsungRemoteActivity::class.java)
            )

        }
        if (isBtnClicked == 1) {
            startActivity(
                Intent(this@FirstActivity, SonyRemote::class.java)
            )
        }

        if (isBtnClicked == 2) {
            startActivity(Intent(this@FirstActivity, TCLRemote::class.java))
        }

        if (isBtnClicked == 3) {
            startActivity(Intent(this@FirstActivity, WifiScreen::class.java))
        }
        if (isBtnClicked == 4) {
            startActivity(Intent(this@FirstActivity, RokuPair::class.java))
        }
        if (isBtnClicked == 5) {
            startActivity(
                Intent(this@FirstActivity, LgWifiRemoteActivity::class.java)
                    .putExtra("tv", "lg")
            )
        }
        if (isBtnClicked == 6) {
            try {
                val irManager = getSystemService(CONSUMER_IR_SERVICE) as ConsumerIrManager
                if (irManager.hasIrEmitter()) {
                    startActivity(Intent(this@FirstActivity, RemoteNameActivity::class.java))
                } else {
                    showIrDialog()
                    //Log.i("IR_Testing", "Cannot found IR Emitter on the device");
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

    private fun showIrDialog() {
        val irDialog = Dialog(this)
        irDialog.setContentView(R.layout.ir_dialog)
        irDialog.setCancelable(false)
        irDialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        val goBack = irDialog.findViewById<Button>(R.id.go_back)
        goBack.setOnClickListener {
            if (irDialog.isShowing && !isFinishing) {
                irDialog.dismiss()
            }
        }
        irDialog.show()
    }

    override fun interstitialDismissedFullScreenContent() {
        moveToRequireActivity()
    }

    override fun interstitialFailedToShowFullScreenContent(adError: AdError?) {
        moveToRequireActivity()
    }

    override fun interstitialShowedFullScreenContent() {}
}