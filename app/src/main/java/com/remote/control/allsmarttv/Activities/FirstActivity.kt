package com.remote.control.allsmarttv.Activities

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
import android.widget.FrameLayout
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.remote.control.allsmarttv.Activities.ir.RemoteNameActivity
import com.remote.control.allsmarttv.Activities.ir.RokuRemote
import com.remote.control.allsmarttv.Activities.ir.SamsungRemoteActivity
import com.remote.control.allsmarttv.R
import com.remote.control.allsmarttv.Utils.ir_utils.SupportedClass
import com.remote.control.allsmarttv.adManager.AdManager
import com.remote.control.allsmarttv.adManager.AdManager.CallBackInterstitial
import com.remote.control.allsmarttv.adManager.AdManager.isInterstialLoaded
import com.remote.control.allsmarttv.adManager.AdManager.loadInterstitialAd
import com.remote.control.allsmarttv.adManager.AdManager.showInterstitial
import com.remote.control.allsmarttv.databinding.ActivityMainBinding

class FirstActivity : AppCompatActivity(), CallBackInterstitial {
    private var isBtnClicked = 0
    var drawerToggle: ActionBarDrawerToggle? = null
    var activityMainBinding: ActivityMainBinding? = null
    var button_privacy: RelativeLayout? = null
    var button_rate: RelativeLayout? = null
    var button_more: RelativeLayout? = null
    var button_share: RelativeLayout? = null
    var button_feedback: RelativeLayout? = null
    private var adLayout: FrameLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SupportedClass.loadLangLocale(baseContext)
        activityMainBinding = ActivityMainBinding.inflate(
            layoutInflater
        )
        setContentView(activityMainBinding!!.root)

        adLayout = findViewById(R.id.adLayout)

        loadInterstitialAd()
        loadBanner()

        button_privacy = findViewById(R.id.btn_privacy)
        button_rate = findViewById(R.id.btn_rate)
        button_share = findViewById(R.id.btn_share)
        button_feedback = findViewById(R.id.btn_feed)
        button_more = findViewById(R.id.btn_more)

        button_privacy?.setOnClickListener(View.OnClickListener {
            try {
                val uri =
                    Uri.parse("https://docs.google.com/document/d/1bYIMnAVAwPrN0vGs9pZ8mSYP6MWgb-zhH7uQpLgyS8U/edit")
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = uri
                startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        })
        button_rate?.setOnClickListener(View.OnClickListener { SupportedClass.rateApp(this@FirstActivity) })
        activityMainBinding!!.language.setOnClickListener {
            try {
                SupportedClass.langDialog(this@FirstActivity, this@FirstActivity)
            } catch (ex: Exception) {
                Log.i("exception is::", ex.toString())
            }
        }
        button_more?.setOnClickListener(View.OnClickListener {
            try {
                //replace &quot;Unified+Apps&quot; with your developer name
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("market://search?q=pub:SerpSkills")
                    )
                )
            } catch (anfe: ActivityNotFoundException) {
                //replace &quot;Unified+Apps&quot; with your developer name
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/developer?id=SerpSkills")
                    )
                )
            }
        })
        button_share?.setOnClickListener(View.OnClickListener { SupportedClass.shareApp(this@FirstActivity) })
        button_feedback?.setOnClickListener(View.OnClickListener { SupportedClass.feedback(this@FirstActivity) })
        drawerToggle = object : ActionBarDrawerToggle(
            this,
            activityMainBinding!!.drawerLayout,
            activityMainBinding!!.toolbarMain,
            R.string.app_name,
            R.string.app_name
        ) {
            override fun onDrawerClosed(view: View) {
                supportInvalidateOptionsMenu()
            }

            override fun onDrawerOpened(drawerView: View) {
                supportInvalidateOptionsMenu()
            }
        }
        drawerToggle?.setDrawerIndicatorEnabled(true)
        activityMainBinding!!.drawerLayout.setDrawerListener(drawerToggle)
        activityMainBinding!!.toolbarMain.setNavigationIcon(R.drawable.draw_icon)
        activityMainBinding!!.samsungTv.setOnClickListener {
            if (SupportedClass.checkConnection(this@FirstActivity)) {
                isBtnClicked = 0
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
        activityMainBinding!!.sony.setOnClickListener {
            if (SupportedClass.checkConnection(this@FirstActivity)) {
                isBtnClicked = 1
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
        activityMainBinding!!.addRemote.setOnClickListener {
            if (SupportedClass.checkConnection(this@FirstActivity)) {
                isBtnClicked = 2
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
        activityMainBinding!!.androidTv.setOnClickListener {
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
        activityMainBinding!!.rokuTv.setOnClickListener {
            if (SupportedClass.checkConnection(this@FirstActivity)) {
                isBtnClicked = 4
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
        activityMainBinding!!.lgTv.setOnClickListener {
            if (SupportedClass.checkConnection(this@FirstActivity)) {
                isBtnClicked = 5
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
        activityMainBinding!!.irRemote.setOnClickListener {
            if (SupportedClass.checkConnection(this@FirstActivity)) {
                isBtnClicked = 6
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
    }

    /**************************************Banner Ads *********************************/
    fun bannerAds() {
        adLayout?.visibility = View.VISIBLE
        adLayout?.post { loadBanner() }
    }

    private fun loadBanner() {
        Log.e("myTag", "BannerAds")
        val adView = AdView(this)
        adView.adUnitId = AdManager.getBannerID()
        val adSize = adSize
        adView.setAdSize(adSize)
        adLayout?.addView(adView)
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
            var adWidthPixels = adLayout!!.width.toFloat()
            // If the ad hasn't been laid out, default to the full screen width.
            if (adWidthPixels == 0f) {
                adWidthPixels = outMetrics.widthPixels.toFloat()
            }
            val adWidth = (adWidthPixels / density).toInt()
            return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth)
        }

    override fun onBackPressed() {
        finishAffinity()
    }

    private fun moveToRequireActivity() {
        if (isBtnClicked == 0) {
            startActivity(
                Intent(this@FirstActivity, SamsungRemoteActivity::class.java)
                    .putExtra("tv", "sam")
            )

        }
        if (isBtnClicked == 1) {
            startActivity(
                Intent(this@FirstActivity, LgWifiRemoteActivity::class.java)
                    .putExtra("tv", "sony")
            )
        }
        if (isBtnClicked == 2) {
//            startActivity(Intent(this@FirstActivity, Tv_list::class.java))
            startActivity(Intent(this@FirstActivity, RokuRemote::class.java))
        }

        if (isBtnClicked == 3) {
            startActivity(Intent(this@FirstActivity, RemoteActivity::class.java))
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
                if (irManager != null) {
                    if (irManager.hasIrEmitter()) {
                        startActivity(Intent(this@FirstActivity, RemoteNameActivity::class.java))
                    } else {
                        showIrDialog()
                        //Log.i("IR_Testing", "Cannot found IR Emitter on the device");
                    }
                } else {
                    Toast.makeText(
                        this,
                        "Cannot found IR Emitter on the device",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

    fun showIrDialog() {
        val irDialog = Dialog(this)
        irDialog.setContentView(R.layout.ir_dialog)
        irDialog.setCancelable(false)
        irDialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        val goBack = irDialog.findViewById<Button>(R.id.go_back)
        goBack.setOnClickListener { v: View? ->
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