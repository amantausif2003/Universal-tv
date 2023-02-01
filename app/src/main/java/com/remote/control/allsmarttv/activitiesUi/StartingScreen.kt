package com.remote.control.allsmarttv.activitiesUi

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.remote.control.allsmarttv.utils.ir_utils.SupportedClass
import com.google.firebase.FirebaseApp
import android.os.Looper
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import android.content.Intent
import android.os.Handler
import com.remote.control.allsmarttv.appUi.PermActivity
import com.remote.control.allsmarttv.appUi.FirstActivity
import com.remote.control.allsmarttv.databinding.ActivitySplashBinding
import com.remote.control.allsmarttv.utils.Util

class StartingScreen : AppCompatActivity() {

    lateinit var splashBinding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SupportedClass.loadLangLocale(baseContext)
        splashBinding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(splashBinding.root)

        FirebaseApp.initializeApp(this)

        Handler(Looper.getMainLooper()).postDelayed({

            Util.showBanner = Util.getSharedPreferences(Util.show_banner).toBoolean()
            Util.showInterstitial = Util.getSharedPreferences(Util.show_interstitial).toBoolean()

            if (ContextCompat.checkSelfPermission(
                    this@StartingScreen, Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_DENIED
            ) {
                val intent = Intent(this@StartingScreen, PermActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                val intent = Intent(this@StartingScreen, FirstActivity::class.java)
                startActivity(intent)
                finish()
            }

        }, 3000)
    }
}