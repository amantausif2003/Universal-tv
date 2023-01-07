package com.remote.control.allsmarttv.Activities.ir

import androidx.appcompat.app.AppCompatActivity
import android.hardware.ConsumerIrManager
import android.media.MediaPlayer
import android.os.Vibrator
import android.os.Bundle
import com.remote.control.allsmarttv.utils.ir_utils.SupportedClass
import com.remote.control.allsmarttv.R
import android.os.Build
import android.os.VibrationEffect
import android.util.Log
import android.view.View
import java.lang.Exception
import java.util.*

class SonyRemote : AppCompatActivity() {
    private val TAG = "SonyRemote"
    private var irManager: ConsumerIrManager? = null
    private var vibe: Vibrator? = null
    var mp: MediaPlayer? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SupportedClass.loadLangLocale(baseContext)
        setContentView(R.layout.activity_sony_remote)
        if (this.supportActionBar != null) {
            supportActionBar!!.hide()
        }
        irManager = getSystemService(CONSUMER_IR_SERVICE) as ConsumerIrManager
        vibe = getSystemService(VIBRATOR_SERVICE) as Vibrator
        mp = MediaPlayer.create(this, R.raw.beep)
        findViewById<View>(R.id.tvpower).setOnClickListener(ClickListener(hex2ir(CMD_TV_POWER)))
        findViewById<View>(R.id.tvchnext).setOnClickListener(ClickListener(hex2ir(CMD_TV_CH_NEXT)))
        findViewById<View>(R.id.tv_up).setOnClickListener(ClickListener(hex2ir(CMD_TV_CH_NEXT)))
        findViewById<View>(R.id.tvchprev).setOnClickListener(ClickListener(hex2ir(CMD_TV_CH_PREV)))
        findViewById<View>(R.id.tv_bottom).setOnClickListener(ClickListener(hex2ir(CMD_TV_CH_PREV)))
        findViewById<View>(R.id.tvleft).setOnClickListener(ClickListener(hex2ir(CMD_TV_LEFT)))
        findViewById<View>(R.id.tvright).setOnClickListener(ClickListener(hex2ir(CMD_TV_RIGHT)))
        findViewById<View>(R.id.tvok).setOnClickListener(ClickListener(hex2ir(CMD_TV_ENTER)))
        findViewById<View>(R.id.txt_ok).setOnClickListener(ClickListener(hex2ir(CMD_TV_ENTER)))
        findViewById<View>(R.id.sbvoldown).setOnClickListener(ClickListener(hex2ir(CMD_SB_VOLDOWN)))
        findViewById<View>(R.id.sbvolup).setOnClickListener(ClickListener(hex2ir(CMD_SB_VOLUP)))
        findViewById<View>(R.id.menu_img).setOnClickListener(ClickListener(hex2ir(CMD_TV_MENU)))
        findViewById<View>(R.id.mute_img).setOnClickListener(ClickListener(hex2ir(CMD_TV_MUTE)))
        findViewById<View>(R.id.tv_up).setOnClickListener(ClickListener(hex2ir(CMD_TV_UP)))
        findViewById<View>(R.id.tv_bottom).setOnClickListener(ClickListener(hex2ir(CMD_TV_DOWN)))
        findViewById<View>(R.id.home_img).setOnClickListener(ClickListener(hex2ir(CMD_TV_EXIT)))
    }

    private fun hex2ir(irData: String): IRCommand {
        //val list: java.util.List<String> = java.util.ArrayList<String>(java.util.Arrays.asList(irData.split(" ")))
        val list = ArrayList<String>(irData.split(" "))
        list.removeAt(0) //Remove zeroes
        var frequency: Int = Integer.parseInt(list.removeAt(0), 16)
        list.removeAt(0) //Not needed
        list.removeAt(0) //Not needed


        frequency = (1000000 / (frequency * 0.241246)).toInt()
        val pulses: Int = 1000000 / frequency
        var count: Int = 0

        val pattern = IntArray(list.size)
        val hexCodes: Int = list.size - 1
        for (i in 0..hexCodes) {
            count = Integer.parseInt(list.get(i), 16)
            pattern[i] = count * pulses
        }

        return IRCommand(frequency, pattern)
    }

    private inner class ClickListener(private val cmd: IRCommand) : View.OnClickListener {
        override fun onClick(view: View) {
            mp!!.start()

//            ToneGenerator toneGenerator = new ToneGenerator(1, 100);
//            toneGenerator.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200); //200 is duration in ms
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibe!!.vibrate(
                    VibrationEffect.createOneShot(
                        150,
                        VibrationEffect.DEFAULT_AMPLITUDE
                    )
                )
            } else {
                //deprecated in API 26
                vibe!!.vibrate(150)
            }
            try {
                Log.d("Remote", "frequency: " + cmd.freq)
                Log.d("Remote", "pattern: " + Arrays.toString(cmd.pattern))
                irManager!!.transmit(cmd.freq, cmd.pattern)
            } catch (e: Exception) {
                Log.e(TAG, e.message!!)
            }
        }
    }

    private class IRCommand(freq: Int, pattern: IntArray) {
        var freq: Int = freq
        var pattern: IntArray = pattern
    }

    companion object {

        private const val CMD_TV_POWER =
            "0000 0068 0000 000d 0060 0018 0030 0018 0018 0018 0030 0018 0018 0018 0030 0018 0018 0018 0018 0018 0030 0018 0018 0018 0018 0018 0018 0018 0018 040b"
        private const val CMD_TV_CH_NEXT =
            "0000 0068 0000 000D 0060 0018 0018 0018 0018 0018 0018 0018 0018 0018 0030 0018 0018 0018 0018 0018 0030 0018 0018 0018 0018 0018 0018 0018 0018 0438"
        private const val CMD_TV_CH_PREV =
            "0000 0068 0000 000D 0060 0018 0030 0018 0018 0018 0018 0018 0018 0018 0030 0018 0018 0018 0018 0018 0030 0018 0018 0018 0018 0018 0018 0018 0018 0422"
        private const val CMD_TV_UP =
            "0000 0068 0000 000d 0060 0018 0018 0018 0018 0018 0030 0018 0018 0018 0030 0018 0030 0018 0030 0018 0030 0018 0018 0018 0018 0018 0018 0018 0018 03f5"
        private const val CMD_TV_DOWN =
            "0000 0068 0000 000d 0060 0018 0030 0018 0018 0018 0030 0018 0018 0018 0030 0018 0030 0018 0030 0018 0030 0018 0018 0018 0018 0018 0018 0018 0018 03de"
        private const val CMD_TV_MENU =
            "0000 0068 0000 000d 0060 0018 0018 0018 0018 0018 0018 0018 0018 0018 0018 0018 0030 0018 0030 0018 0030 0018 0018 0018 0018 0018 0018 0018 0018 0422"
        private const val CMD_TV_LEFT =
            "0000 0068 0000 000d 0060 0018 0018 0018 0018 0018 0030 0018 0018 0018 0030 0018 0030 0018 0018 0018 0030 0018 0018 0018 0018 0018 0018 0018 0018 040b"
        private const val CMD_TV_RIGHT =
            "0000 0068 0000 000d 0060 0018 0030 0018 0030 0018 0018 0018 0018 0018 0030 0018 0030 0018 0018 0018 0030 0018 0018 0018 0018 0018 0018 0018 0018 03f5"
        private const val CMD_TV_ENTER =
            "0000 0068 0000 000D 0060 0018 0030 0018 0018 0018 0030 0018 0018 0018 0018 0018 0030 0018 0030 0018 0030 0018 0018 0018 0018 0018 0018 0018 0018 03E9"
        private const val CMD_TV_MUTE =
            "0000 0068 0000 000d 0060 0018 0018 0018 0018 0018 0030 0018 0018 0018 0030 0018 0018 0018 0018 0018 0030 0018 0018 0018 0018 0018 0018 0018 0018 0422"
        private const val CMD_TV_EXIT =
            "0000 0067 0000 000D 0060 0018 0030 0018 0030 0018 0018 0018 0018 0018 0018 0018 0030 0018 0030 0018 0030 0018 0018 0018 0018 0018 0018 0018 0018 0450"
        private const val CMD_SB_VOLUP =
            "0000 0068 0000 000d 0060 0018 0018 0018 0030 0018 0018 0018 0018 0018 0030 0018 0018 0018 0018 0018 0030 0018 0018 0018 0018 0018 0018 0018 0018 0422"
        private const val CMD_SB_VOLDOWN =
            "0000 0068 0000 000d 0060 0018 0030 0018 0030 0018 0018 0018 0018 0018 0030 0018 0018 0018 0018 0018 0030 0018 0018 0018 0018 0018 0018 0018 0018 040b"
    }
}