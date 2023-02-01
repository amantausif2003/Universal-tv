package com.remote.control.allsmarttv.utils

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.widget.Toast
import com.remote.control.allsmarttv.utils.ir_utils.App

object Util {

    //This variable show Force Update dialog
    const val show_banner: String = "show_banner"
    const val show_interstitial: String = "show_interstitial"

    @JvmStatic
    var showBanner: Boolean  = false

    @JvmStatic
    var showInterstitial: Boolean  = false

    @Suppress("USELESS_CAST")
    @JvmStatic
    fun showToast(c: Context, message: String) {
        try {
            if (!(c as Activity).isFinishing) {
                val activity = c as Activity
                activity.runOnUiThread { //show your Toast here..
                    Toast.makeText(c.applicationContext, message, Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    fun setSharedPreferences(keyShare: String, values: String?) {
        val editor: SharedPreferences.Editor = App.context.getSharedPreferences(
            "TV",
            Context.MODE_PRIVATE
        ).edit()
        editor.putString(keyShare, "${values}")
        editor.apply()
    }

    @JvmStatic
    fun getSharedPreferences(keyShare: String): String {
        val prefs: SharedPreferences = App.context.getSharedPreferences(
            "TV",
            Context.MODE_PRIVATE
        )
        val keyVal = prefs.getString(keyShare, "false") //"No name defined" is the default value.
        return keyVal.toString()
    }

    @Suppress("DEPRECATION")
    fun vibrateDevice(context: Context) {

        try {

            val vibrator: Vibrator? = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator


            vibrator?.let {

                if (it.hasVibrator()) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        // API 26 and above
                        //it.vibrate(VibrationEffect.createWaveform(pattern, 0))
                        var vibrationEffect: VibrationEffect? = null

                        try {
                            vibrationEffect = VibrationEffect.createOneShot(
                                150,
                                VibrationEffect.DEFAULT_AMPLITUDE
                            )
                        } catch (ex: java.lang.Exception) {
                            ex.printStackTrace()
                        }

                        if (vibrationEffect != null) {
                            it.vibrate(vibrationEffect)
                        }

                    } else {
                        // Below API 26
                        it.vibrate(150)
                        Log.d("myVibration", "no Effect")
                    }

                }

            }
        } catch (ex: java.lang.Exception) {
            ex.printStackTrace()
        }
    }
}