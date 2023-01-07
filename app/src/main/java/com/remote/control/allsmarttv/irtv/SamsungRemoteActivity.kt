package com.remote.control.allsmarttv.irtv

import androidx.appcompat.app.AppCompatActivity
import android.hardware.ConsumerIrManager
import android.os.Bundle
import com.remote.control.allsmarttv.utils.ir_utils.SupportedClass
import com.remote.control.allsmarttv.databinding.ActivitySamsungRemoteBinding
import com.remote.control.allsmarttv.utils.Util
import java.lang.Exception
import java.util.*

class SamsungRemoteActivity : AppCompatActivity() {

    private lateinit var irManager: ConsumerIrManager
    private lateinit var mainBinding: ActivitySamsungRemoteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SupportedClass.loadLangLocale(baseContext)
        mainBinding = ActivitySamsungRemoteBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)
        if (this.supportActionBar != null) {
            supportActionBar!!.hide()
        }

        irManager = getSystemService(CONSUMER_IR_SERVICE) as ConsumerIrManager

        mainBinding.tvPower.setOnClickListener {
            click(hex2ir(CMD_TV_POWER))
        }

        mainBinding.tvAv.setOnClickListener {
            click(hex2ir(CMD_TV_MENU))
        }

        mainBinding.tvMute.setOnClickListener {
            click(hex2ir(CMD_TV_MUTE))
        }

        mainBinding.tvExit.setOnClickListener {
            click(hex2ir(CMD_TV_EXIT))
        }

        mainBinding.tvChNext.setOnClickListener {
            click(hex2ir(CMD_TV_CH_NEXT))
        }

        mainBinding.tvChPrev.setOnClickListener {
            click(hex2ir(CMD_TV_CH_PREV))
        }

        mainBinding.tvVolUp.setOnClickListener {
            click(hex2ir(CMD_SB_VOLUP))
        }

        mainBinding.tvVolDown.setOnClickListener {
            click(hex2ir(CMD_SB_VOLDOWN))
        }

        mainBinding.tvOk.setOnClickListener {
            click(hex2ir(CMD_TV_ENTER))
        }

        mainBinding.tvUp.setOnClickListener {
            click(hex2ir(CMD_TV_UP))
        }

        mainBinding.tvDown.setOnClickListener {
            click(hex2ir(CMD_TV_DOWN))
        }

        mainBinding.tvLeft.setOnClickListener {
            click(hex2ir(CMD_TV_LEFT))
        }

        mainBinding.tvRight.setOnClickListener {
            click(hex2ir(CMD_TV_RIGHT))
        }
    }

    private fun hex2ir(irData: String): IRCommand {
        val list = ArrayList(irData.split(" "))
        list.removeAt(0) //Remove zeroes
        var frequency: Int = Integer.parseInt(list.removeAt(0), 16)
        list.removeAt(0) //Not needed
        list.removeAt(0) //Not needed

        frequency = (1000000 / (frequency * 0.241246)).toInt()
        val pulses: Int = 1000000 / frequency

        var count: Int

        val pattern = IntArray(list.size)
        val hexCodes: Int = list.size - 1
        for (i in 0..hexCodes) {
            count = Integer.parseInt(list[i], 16)
            pattern[i] = count * pulses
        }

        return IRCommand(frequency, pattern)
    }

    private class IRCommand(var freq: Int, var pattern: IntArray)

    companion object {

        private const val CMD_TV_POWER =
            "0000 006d 0022 0003 00a9 00a8 0015 003f 0015 003f 0015 003f 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 003f 0015 003f 0015 003f 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 003f 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0040 0015 0015 0015 003f 0015 003f 0015 003f 0015 003f 0015 003f 0015 003f 0015 0702 00a9 00a8 0015 0015 0015 0e6e"
        private const val CMD_TV_CH_NEXT =
            "0000 006D 0000 0022 00ac 00ac 0016 0040 0016 0040 0016 0040 0016 0015 0016 0015 0016 0015 0016 0015 0016 0015 0016 0040 0016 0040 0016 0040 0016 0015 0016 0015 0016 0015 0016 0015 0016 0015 0016 0015 0016 0040 0016 0015 0016 0015 0016 0040 0016 0015 0016 0015 0016 0015 0016 0040 0016 0015 0016 0040 0016 0040 0016 0015 0016 0040 0016 0040 0016 0040 0016 071c"
        private const val CMD_TV_CH_PREV =
            "0000 006D 0000 0022 00ac 00ac 0016 0040 0016 0040 0016 0040 0016 0015 0016 0015 0016 0015 0016 0015 0016 0015 0016 0040 0016 0040 0016 0040 0016 0015 0016 0015 0016 0015 0016 0015 0016 0015 0016 0015 0016 0015 0016 0015 0016 0015 0016 0040 0016 0015 0016 0015 0016 0015 0016 0040 0016 0040 0016 0040 0016 0040 0016 0015 0016 0040 0016 0040 0016 0040 0016 071c"
        private const val CMD_TV_UP =
            "0000 006D 0000 0022 00ac 00ac 0016 0040 0016 0040 0016 0040 0016 0015 0016 0015 0016 0015 0016 0015 0016 0015 0016 0040 0016 0040 0016 0040 0016 0015 0016 0015 0016 0015 0016 0015 0016 0015 0016 0015 0016 0015 0016 0015 0016 0015 0016 0015 0016 0040 0016 0040 0016 0015 0016 0040 0016 0040 0016 0040 0016 0040 0016 0040 0016 0015 0016 0015 0016 0040 0016 071c"
        private const val CMD_TV_DOWN =
            "0000 006D 0000 0022 00ac 00ac 0016 0040 0016 0040 0016 0040 0016 0015 0016 0015 0016 0015 0016 0015 0016 0015 0016 0040 0016 0040 0016 0040 0016 0015 0016 0015 0016 0015 0016 0015 0016 0015 0016 0040 0016 0015 0016 0015 0016 0015 0016 0015 0016 0040 0016 0040 0016 0015 0016 0015 0016 0040 0016 0040 0016 0040 0016 0040 0016 0015 0016 0015 0016 0040 0016 071c"
        private const val CMD_TV_MENU =
            "0000 006c 0000 0022 00ad 00ad 0014 0040 0014 0040 0014 0040 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0040 0014 0040 0014 0040 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0040 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0040 0014 0040 0014 0040 0014 0040 0014 0040 0014 0040 0014 0040 0014 06bc"
        private const val CMD_TV_LEFT =
            "0000 006D 0000 0022 00ac 00ac 0016 0040 0016 0040 0016 0040 0016 0015 0016 0015 0016 0015 0016 0015 0016 0015 0016 0040 0016 0040 0016 0040 0016 0015 0016 0015 0016 0015 0016 0015 0016 0015 0016 0040 0016 0015 0016 0040 0016 0015 0016 0015 0016 0040 0016 0040 0016 0015 0016 0015 0016 0040 0016 0015 0016 0040 0016 0040 0016 0015 0016 0015 0016 0040 0016 071c"
        private const val CMD_TV_RIGHT =
            "0000 006D 0000 0022 00ac 00ac 0016 0040 0016 0040 0016 0040 0016 0015 0016 0015 0016 0015 0016 0015 0016 0015 0016 0040 0016 0040 0016 0040 0016 0015 0016 0015 0016 0015 0016 0015 0016 0015 0016 0015 0016 0040 0016 0015 0016 0015 0016 0015 0016 0040 0016 0040 0016 0015 0016 0040 0016 0015 0016 0040 0016 0040 0016 0040 0016 0015 0016 0015 0016 0040 0016 071c"
        private const val CMD_TV_ENTER =
            "0000 006D 0000 0022 00AC 00AC 0017 003E 0017 003E 0017 003E 0017 0013 0017 0013 0017 0013 0017 0013 0017 0013 0017 003E 0017 003E 0017 003E 0017 0013 0017 0013 0017 0013 0017 0013 0017 0013 0017 0013 0017 0013 0017 0013 0017 003E 0017 0013 0017 003E 0017 003E 0017 0013 0017 003E 0017 003E 0017 003E 0017 0013 0017 003E 0017 0013 0017 0013 0017 003E 0017 0746"
        private const val CMD_TV_MUTE =
            "0000 006D 0000 0022 00ac 00ac 0016 0040 0016 0040 0016 0040 0016 0015 0016 0015 0016 0015 0016 0015 0016 0015 0016 0040 0016 0040 0016 0040 0016 0015 0016 0015 0016 0015 0016 0015 0016 0015 0016 0040 0016 0040 0016 0040 0016 0040 0016 0015 0016 0015 0016 0015 0016 0015 0016 0015 0016 0015 0016 0015 0016 0015 0016 0040 0016 0040 0016 0040 0016 0040 0016 071c"
        private const val CMD_TV_EXIT =
            "0000 006d 0022 0003 00a9 00a8 0015 003f 0015 003f 0015 003f 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 003f 0015 003f 0015 003f 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 003f 0015 0015 0015 003f 0015 003f 0015 0015 0015 0015 0015 0015 0015 003f 0015 0015 0015 003f 0015 0015 0015 0015 0015 003f 0015 003f 0015 003f 0015 0703 00a9 00a8 0015 0015 0015 0e6e"
        private const val CMD_SB_VOLUP =
            "0000 006D 0000 0022 00ac 00ac 0016 0040 0016 0040 0016 0040 0016 0015 0016 0015 0016 0015 0016 0015 0016 0015 0016 0040 0016 0040 0016 0040 0016 0015 0016 0015 0016 0015 0016 0015 0016 0015 0016 0040 0016 0040 0016 0040 0016 0015 0016 0015 0016 0015 0016 0015 0016 0015 0016 0015 0016 0015 0016 0015 0016 0040 0016 0040 0016 0040 0016 0040 0016 0040 0016 071c"
        private const val CMD_SB_VOLDOWN =
            "0000 006D 0000 0022 00ac 00ac 0016 0040 0016 0040 0016 0040 0016 0015 0016 0015 0016 0015 0016 0015 0016 0015 0016 0040 0016 0040 0016 0040 0016 0015 0016 0015 0016 0015 0016 0015 0016 0015 0016 0040 0016 0040 0016 0015 0016 0040 0016 0015 0016 0015 0016 0015 0016 0015 0016 0015 0016 0015 0016 0040 0016 0015 0016 0040 0016 0040 0016 0040 0016 0040 0016 071c"
    }


    private fun click(cmd: IRCommand) {

        // Check if the phone has an IR transmitter
        if (!irManager.hasIrEmitter()) {
            // Show an error message if the phone does not have an IR transmitter
           Util.showToast(this,"Your phone does not have an IR transmitter")
        } else {

            Util.vibrateDevice(this)

           /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibe!!.vibrate(
                    VibrationEffect.createOneShot(
                        150,
                        VibrationEffect.DEFAULT_AMPLITUDE
                    )
                )
            } else {
                //deprecated in API 26
                vibe!!.vibrate(150)
            }*/

            try {
                irManager.transmit(cmd.freq, cmd.pattern)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }

    }

}