package com.remote.control.allsmarttv.utils

import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.widget.Toast

object Util {

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