package com.remote.control.allsmarttv.irtv

import android.hardware.ConsumerIrManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.remote.control.allsmarttv.databinding.ActivityTclremoteBinding
import com.remote.control.allsmarttv.utils.Util
import com.remote.control.allsmarttv.utils.ir_utils.SupportedClass
import java.lang.Exception
import java.util.ArrayList

class TCLRemote : AppCompatActivity() {

    private lateinit var irManager: ConsumerIrManager
    private lateinit var mainBinding: ActivityTclremoteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SupportedClass.loadLangLocale(baseContext)
        mainBinding = ActivityTclremoteBinding.inflate(layoutInflater)
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


        var count: Int = 0

        val pattern = IntArray(list.size)
        val hexCodes: Int = list.size - 1
        for (i in 0..hexCodes) {
            count = Integer.parseInt(list[i], 16)
            pattern[i] = count * pulses
        }

        return IRCommand(frequency, pattern)
    }

    private class IRCommand(var freq: Int, var pattern: IntArray)

    private fun click(cmd: IRCommand) {

        // Check if the phone has an IR transmitter
        if (!irManager.hasIrEmitter()) {
            // Show an error message if the phone does not have an IR transmitter
            Util.showToast(this, "Your phone does not have an IR transmitter")
        } else {

            Util.vibrateDevice(this)

            try {
                irManager.transmit(cmd.freq, cmd.pattern)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }

    }

    companion object {
        private const val CMD_TV_POWER =
            "0000 006C 0000 001A 0098 0098 0013 004C 0013 004C 0013 004C 0013 004C 0013 0026 0013 0026 0013 004C 0013 0026 0013 004C 0013 0026 0013 004C 0013 0026 0013 0026 0013 0026 0013 0026 0013 0026 0013 004C 0013 004C 0013 0026 0013 004C 0013 0026 0013 004C 0013 0026 0013 004C 0013 0156"
        private const val CMD_TV_CH_NEXT =
            "0000 006C 0000 001A 0098 0098 0013 004C 0013 004C 0013 004C 0013 004C 0013 0026 0013 0026 0013 004C 0013 0026 0013 004C 0013 004C 0013 0026 0013 004C 0013 0026 0013 0026 0013 0026 0013 0026 0013 004C 0013 004C 0013 0026 0013 004C 0013 0026 0013 0026 0013 004C 0013 0026 0013 0159"
        private const val CMD_TV_CH_PREV =
            "0000 006C 0000 001A 0098 0098 0013 004C 0013 004C 0013 004C 0013 004C 0013 0026 0013 0026 0013 004C 0013 0026 0013 004C 0013 004C 0013 0026 0013 0026 0013 0026 0013 0026 0013 0026 0013 0026 0013 004C 0013 004C 0013 0026 0013 004C 0013 0026 0013 0026 0013 004C 0013 004C 0013 0154"
        private const val CMD_TV_UP =
            "0000 006C 0000 001A 0098 0098 0013 004C 0013 004C 0013 004C 0013 004C 0013 0026 0013 004C 0013 0026 0013 004C 0013 004C 0013 0026 0013 0026 0013 004C 0013 0026 0013 0026 0013 0026 0013 0026 0013 004C 0013 0026 0013 004C 0013 0026 0013 0026 0013 004C 0013 004C 0013 0026 0013 0152"
        private const val CMD_TV_DOWN =
            "0000 006C 0000 001A 0098 0098 0013 004C 0013 004C 0013 004C 0013 004C 0013 0026 0013 004C 0013 0026 0013 004C 0013 004C 0013 0026 0013 0026 0013 0026 0013 0026 0013 0026 0013 0026 0013 0026 0013 004C 0013 0026 0013 004C 0013 0026 0013 0026 0013 004C 0013 004C 0013 004C 0013 0154"
        private const val CMD_TV_MENU =
            "0000 006C 0000 001A 0098 0098 0013 004C 0013 004C 0013 004C 0013 004C 0013 004C 0013 0026 0013 004C 0013 0026 0013 0026 0013 0026 0013 004C 0013 004C 0013 0026 0013 0026 0013 0026 0013 0026 0013 0026 0013 004C 0013 0026 0013 004C 0013 004C 0013 004C 0013 0026 0013 0026 0013 0156"
        private const val CMD_TV_LEFT =
            "0000 006C 0000 001A 0098 0098 0013 004C 0013 004C 0013 004C 0013 004C 0013 0026 0013 004C 0013 0026 0013 004C 0013 0026 0013 004C 0013 004C 0013 0026 0013 0026 0013 0026 0013 0026 0013 0026 0013 004C 0013 0026 0013 004C 0013 0026 0013 004C 0013 0026 0013 0026 0013 004C 0013 0152"
        private const val CMD_TV_RIGHT =
            "0000 006C 0000 001A 0098 0098 0013 004C 0013 004C 0013 004C 0013 004C 0013 0026 0013 004C 0013 0026 0013 004C 0013 0026 0013 004C 0013 004C 0013 004C 0013 0026 0013 0026 0013 0026 0013 0026 0013 004C 0013 0026 0013 004C 0013 0026 0013 004C 0013 0026 0013 0026 0013 0026 0013 0154"
        private const val CMD_TV_ENTER =
            "0000 006C 0000 001A 0098 0098 0013 004C 0013 004C 0013 004C 0013 004C 0013 004C 0013 004C 0013 004C 0013 004C 0013 0026 0013 004C 0013 0026 0013 0026 0013 0026 0013 0026 0013 0026 0013 0026 0013 0026 0013 0026 0013 0026 0013 0026 0013 004C 0013 0026 0013 004C 0013 004C 0013 0152"
        private const val CMD_TV_MUTE =
            "0000 006C 0000 001A 0098 0098 0013 004C 0013 004C 0013 004C 0013 004C 0013 0026 0013 0026 0013 004C 0013 004C 0013 004C 0013 004C 0013 004C 0013 004C 0013 0026 0013 0026 0013 0026 0013 0026 0013 004C 0013 004C 0013 0026 0013 0026 0013 0026 0013 0026 0013 0026 0013 0026 0013 0152"
        private const val CMD_TV_EXIT =
            "0000 006C 0000 001A 0098 0098 0013 004C 0013 004C 0013 004C 0013 004C 0013 0026 0013 0026 0013 0026 0013 0026 0013 004C 0013 0026 0013 0026 0013 0026 0013 0026 0013 0026 0013 0026 0013 0026 0013 004C 0013 004C 0013 004C 0013 004C 0013 0026 0013 004C 0013 004C 0013 004C 0013 0152"
        private const val CMD_SB_VOLUP =
            "0000 006C 0000 001A 0098 0098 0013 004C 0013 004C 0013 004C 0013 004C 0013 0026 0013 0026 0013 004C 0013 0026 0013 004C 0013 004C 0013 004C 0013 004C 0013 0026 0013 0026 0013 0026 0013 0026 0013 004C 0013 004C 0013 0026 0013 004C 0013 0026 0013 0026 0013 0026 0013 0026 0013 0152"
        private const val CMD_SB_VOLDOWN =
            "0000 006C 0000 001A 0098 0098 0013 004C 0013 004C 0013 004C 0013 004C 0013 0026 0013 0026 0013 004C 0013 0026 0013 004C 0013 004C 0013 004C 0013 0026 0013 0026 0013 0026 0013 0026 0013 0026 0013 004C 0013 004C 0013 0026 0013 004C 0013 0026 0013 0026 0013 0026 0013 004C 0013 0152"
    }
}