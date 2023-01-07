package com.remote.control.allsmarttv.irtv

import androidx.appcompat.app.AppCompatActivity
import android.hardware.ConsumerIrManager
import android.os.Bundle
import com.remote.control.allsmarttv.utils.ir_utils.SupportedClass
import com.remote.control.allsmarttv.databinding.ActivitySonyRemoteBinding
import com.remote.control.allsmarttv.utils.Util
import java.lang.Exception
import java.util.*

class SonyRemote : AppCompatActivity() {

    private lateinit var irManager: ConsumerIrManager
    private lateinit var mainBinding: ActivitySonyRemoteBinding

    var onOff = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SupportedClass.loadLangLocale(baseContext)
        mainBinding = ActivitySonyRemoteBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)

        if (this.supportActionBar != null) {
            supportActionBar!!.hide()
        }


        irManager = getSystemService(CONSUMER_IR_SERVICE) as ConsumerIrManager


        mainBinding.tvPower.setOnClickListener {
            when (onOff) {
                true -> {
                    click(hex2ir(CMD_TV_POWER_ON))
                    onOff = false
                }
                false -> {
                    click(hex2ir(CMD_TV_POWER_OFF))
                    onOff = true
                }
            }
        }

        mainBinding.tvAv.setOnClickListener {
            click(hex2ir(CMD_TV_MENU))
        }

        mainBinding.tvMute.setOnClickListener {
            click(hex2ir(CMD_TV_MENU))
        }

        mainBinding.tvExit.setOnClickListener {
            click(hex2ir(CMD_TV_BACK))
        }

        mainBinding.tvChNext.setOnClickListener {
            click(hex2ir(CMD_TV_CH_UP))
        }

        mainBinding.tvChPrev.setOnClickListener {
            click(hex2ir(CMD_TV_CH_DOWN))
        }

        mainBinding.tvVolUp.setOnClickListener {
            click(hex2ir(CMD_TV_VOL_UP))
        }

        mainBinding.tvVolDown.setOnClickListener {
            click(hex2ir(CMD_TV_VOL_DOWN))
        }

        mainBinding.tvOk.setOnClickListener {
            click(hex2ir(CMD_TV_OK))
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

        //Power
        private val CMD_TV_POWER_ON: String =
            "0000 0066 0027 0000 005f 0018 0019 0018 0030 0018 0030 0018 0030 0018 0019 0018 0030 0018 0019 0018 0030 0018 0019 0018 0019 0018 0019 0018 0019 03f6 005f 0018 0019 0018 0030 0018 0030 0018 0030 0018 0019 0018 0030 0018 0019 0018 0030 0018 0019 0018 0019 0018 0019 0018 0019 03f6 005f 0018 0019 0018 0030 0018 0030 0018 0030 0018 0019 0018 0030 0018 0019 0018 0030 0018 0019 0018 0019 0018 0019 0018 0019 0018"

        //Source
        private val CMD_TV_MUTE: String =
            "0000 0067 0000 000d 0061 0018 0018 0018 0018 0018 0030 0018 0018 0018 0030 0018 0018 0018 0018 0018 0030 0018 0018 0018 0018 0018 0018 0018 0018 0425 0061 0018 0018 0018 0018 0018 0030 0018 0018 0018 0030 0018 0018 0018 0018 0018 0030 0018 0018 0018 0018 0018 0018 0018 0018 0425 0061 0018 0018 0018 0018 0018 0030 0018 0018 0018 0030 0018 0018 0018 0018 0018 0030 0018 0018 0018 0018 0018 0018 0018 0018 0425"

        //Channel Up
        private val CMD_TV_CH_UP: String =
            "0000 0067 0000 000d 0061 0018 0018 0018 0018 0018 0018 0018 0018 0018 0030 0018 0018 0018 0018 0018 0030 0018 0018 0018 0018 0018 0018 0018 0018 043d 0061 0018 0018 0018 0018 0018 0018 0018 0018 0018 0030 0018 0018 0018 0018 0018 0030 0018 0018 0018 0018 0018 0018 0018 0018 043d 0061 0018 0018 0018 0018 0018 0018 0018 0018 0018 0030 0018 0018 0018 0018 0018 0030 0018 0018 0018 0018 0018 0018 0018 0018 043d"

        //Channel Down
        private val CMD_TV_CH_DOWN: String =
            "0000 0067 0000 000d 0061 0018 0030 0018 0018 0018 0018 0018 0018 0018 0030 0018 0018 0018 0018 0018 0030 0018 0018 0018 0018 0018 0018 0018 0018 0425 0061 0018 0030 0018 0018 0018 0018 0018 0018 0018 0030 0018 0018 0018 0018 0018 0030 0018 0018 0018 0018 0018 0018 0018 0018 0425 0061 0018 0030 0018 0018 0018 0018 0018 0018 0018 0030 0018 0018 0018 0018 0018 0030 0018 0018 0018 0018 0018 0018 0018 0018 0425"

        //Volume Up
        private val CMD_TV_VOL_UP: String =
            "0000 0067 0000 000d 0061 0018 0018 0018 0030 0018 0018 0018 0018 0018 0030 0018 0018 0018 0018 0018 0030 0018 0018 0018 0018 0018 0018 0018 0018 0425 0061 0018 0018 0018 0030 0018 0018 0018 0018 0018 0030 0018 0018 0018 0018 0018 0030 0018 0018 0018 0018 0018 0018 0018 0018 0425 0061 0018 0018 0018 0030 0018 0018 0018 0018 0018 0030 0018 0018 0018 0018 0018 0030 0018 0018 0018 0018 0018 0018 0018 0018 0425"

        //Volume Down
        private val CMD_TV_VOL_DOWN: String =
            "0000 0067 0000 000d 0061 0018 0030 0018 0030 0018 0018 0018 0018 0018 0030 0018 0018 0018 0018 0018 0030 0018 0018 0018 0018 0018 0018 0018 0018 040e 0061 0018 0030 0018 0030 0018 0018 0018 0018 0018 0030 0018 0018 0018 0018 0018 0030 0018 0018 0018 0018 0018 0018 0018 0018 040e 0061 0018 0030 0018 0030 0018 0018 0018 0018 0018 0030 0018 0018 0018 0018 0018 0030 0018 0018 0018 0018 0018 0018 0018 0018 040e"

        //OK & Enter
        private val CMD_TV_OK: String =
            "0000 0067 0027 0000 0061 0018 0030 0018 0018 0018 0030 0018 0018 0018 0018 0018 0030 0018 0030 0018 0030 0018 0018 0018 0018 0018 0018 0018 0018 03f4 0061 0018 0030 0018 0018 0018 0030 0018 0018 0018 0018 0018 0030 0018 0030 0018 0030 0018 0018 0018 0018 0018 0018 0018 0018 03f4 0061 0018 0030 0018 0018 0018 0030 0018 0018 0018 0018 0018 0030 0018 0030 0018 0030 0018 0018 0018 0018 0018 0018 0018 0018 0018"

        //Menu
        private val CMD_TV_MENU: String =
            "0000 0067 0027 0000 0060 0018 0018 0018 0018 0018 0018 0018 0018 0018 0018 0018 0030 0018 0030 0018 0030 0018 0018 0018 0018 0018 0018 0018 0018 0410 0060 0018 0018 0018 0018 0018 0018 0018 0018 0018 0018 0018 0030 0018 0030 0018 0030 0018 0018 0018 0018 0018 0018 0018 0018 0410 0060 0018 0018 0018 0018 0018 0018 0018 0018 0018 0018 0018 0030 0018 0030 0018 0030 0018 0018 0018 0018 0018 0018 0018 0018 0018"

        //0
        private val CMD_TV_0: String =
            "0000 0068 0000 000D 0060 0018 0030 0018 0018 0018 0018 0018 0030 0018 0018 0018 0018 0018 0018 0018 0030 0018 0018 0018 0018 0018 0018 0018 0018 0420 0060 0018 0030 0018 0018 0018 0018 0018 0030 0018 0018 0018 0018 0018 0018 0018 0030 0018 0018 0018 0018 0018 0018 0018 0018 0420 0060 0018 0030 0018 0018 0018 0018 0018 0030 0018 0018 0018 0018 0018 0018 0018 0030 0018 0018 0018 0018 0018 0018 0018 0018 0420"

        //1
        private val CMD_TV_1: String =
            "0000 0067 0000 000d 0061 0018 0018 0018 0018 0018 0018 0018 0018 0018 0018 0018 0018 0018 0018 0018 0030 0018 0018 0018 0018 0018 0018 0018 0018 0455 0061 0018 0018 0018 0018 0018 0018 0018 0018 0018 0018 0018 0018 0018 0018 0018 0030 0018 0018 0018 0018 0018 0018 0018 0018 0455 0061 0018 0018 0018 0018 0018 0018 0018 0018 0018 0018 0018 0018 0018 0018 0018 0030 0018 0018 0018 0018 0018 0018 0018 0018 0455"

        //2
        private val CMD_TV_2: String =
            "0000 0067 0000 000d 0061 0018 0030 0018 0018 0018 0018 0018 0018 0018 0018 0018 0018 0018 0018 0018 0030 0018 0018 0018 0018 0018 0018 0018 0018 043d 0061 0018 0030 0018 0018 0018 0018 0018 0018 0018 0018 0018 0018 0018 0018 0018 0030 0018 0018 0018 0018 0018 0018 0018 0018 043d 0061 0018 0030 0018 0018 0018 0018 0018 0018 0018 0018 0018 0018 0018 0018 0018 0030 0018 0018 0018 0018 0018 0018 0018 0018 043d"

        //3
        private val CMD_TV_3: String =
            "0000 0067 0000 000d 0061 0018 0018 0018 0030 0018 0018 0018 0018 0018 0018 0018 0018 0018 0018 0018 0030 0018 0018 0018 0018 0018 0018 0018 0018 043d 0061 0018 0018 0018 0030 0018 0018 0018 0018 0018 0018 0018 0018 0018 0018 0018 0030 0018 0018 0018 0018 0018 0018 0018 0018 043d 0061 0018 0018 0018 0030 0018 0018 0018 0018 0018 0018 0018 0018 0018 0018 0018 0030 0018 0018 0018 0018 0018 0018 0018 0018 043d"

        //4
        private val CMD_TV_4: String =
            "0000 0067 0000 000d 0061 0018 0030 0018 0030 0018 0018 0018 0018 0018 0018 0018 0018 0018 0018 0018 0030 0018 0018 0018 0018 0018 0018 0018 0018 0425 0061 0018 0030 0018 0030 0018 0018 0018 0018 0018 0018 0018 0018 0018 0018 0018 0030 0018 0018 0018 0018 0018 0018 0018 0018 0425 0061 0018 0030 0018 0030 0018 0018 0018 0018 0018 0018 0018 0018 0018 0018 0018 0030 0018 0018 0018 0018 0018 0018 0018 0018 0425"

        //5
        private val CMD_TV_5: String =
            "0000 0067 0000 000d 0061 0018 0018 0018 0018 0018 0030 0018 0018 0018 0018 0018 0018 0018 0018 0018 0030 0018 0018 0018 0018 0018 0018 0018 0018 043d 0061 0018 0018 0018 0018 0018 0030 0018 0018 0018 0018 0018 0018 0018 0018 0018 0030 0018 0018 0018 0018 0018 0018 0018 0018 043d 0061 0018 0018 0018 0018 0018 0030 0018 0018 0018 0018 0018 0018 0018 0018 0018 0030 0018 0018 0018 0018 0018 0018 0018 0018 043d"

        //6
        private val CMD_TV_6: String =
            "0000 0068 0000 000D 0060 0018 0030 0018 0018 0018 0030 0018 0018 0018 0018 0018 0018 0018 0018 0018 0030 0018 0018 0018 0018 0018 0018 0018 0018 0420 0060 0018 0030 0018 0018 0018 0030 0018 0018 0018 0018 0018 0018 0018 0018 0018 0030 0018 0018 0018 0018 0018 0018 0018 0018 0420 0060 0018 0030 0018 0018 0018 0030 0018 0018 0018 0018 0018 0018 0018 0018 0018 0030 0018 0018 0018 0018 0018 0018 0018 0018 0420"

        //7
        private val CMD_TV_7: String =
            "0000 0067 0000 000d 0061 0018 0018 0018 0030 0018 0030 0018 0018 0018 0018 0018 0018 0018 0018 0018 0030 0018 0018 0018 0018 0018 0018 0018 0018 0425 0061 0018 0018 0018 0030 0018 0030 0018 0018 0018 0018 0018 0018 0018 0018 0018 0030 0018 0018 0018 0018 0018 0018 0018 0018 0425 0061 0018 0018 0018 0030 0018 0030 0018 0018 0018 0018 0018 0018 0018 0018 0018 0030 0018 0018 0018 0018 0018 0018 0018 0018 0425"

        //8
        private val CMD_TV_8: String =
            "0000 0067 0000 000d 0061 0018 0030 0018 0030 0018 0030 0018 0018 0018 0018 0018 0018 0018 0018 0018 0030 0018 0018 0018 0018 0018 0018 0018 0018 040e 0061 0018 0030 0018 0030 0018 0030 0018 0018 0018 0018 0018 0018 0018 0018 0018 0030 0018 0018 0018 0018 0018 0018 0018 0018 040e 0061 0018 0030 0018 0030 0018 0030 0018 0018 0018 0018 0018 0018 0018 0018 0018 0030 0018 0018 0018 0018 0018 0018 0018 0018 040e"

        //9
        private val CMD_TV_9: String =
            "0000 0068 0000 000D 0060 0018 0018 0018 0018 0018 0018 0018 0030 0018 0018 0018 0018 0018 0018 0018 0030 0018 0018 0018 0018 0018 0018 0018 0018 0438 0060 0018 0018 0018 0018 0018 0018 0018 0030 0018 0018 0018 0018 0018 0018 0018 0030 0018 0018 0018 0018 0018 0018 0018 0018 0438 0060 0018 0018 0018 0018 0018 0018 0018 0030 0018 0018 0018 0018 0018 0018 0018 0030 0018 0018 0018 0018 0018 0018 0018 0018 0438"

        //Up
        private val CMD_TV_UP: String =
            "0000 0067 0034 0000 0061 0018 0018 0018 0018 0018 0030 0018 0018 0018 0030 0018 0030 0018 0030 0018 0030 0018 0018 0018 0018 0018 0018 0018 0018 03f4 0061 0018 0018 0018 0018 0018 0030 0018 0018 0018 0030 0018 0030 0018 0030 0018 0030 0018 0018 0018 0018 0018 0018 0018 0018 03f4 0061 0018 0018 0018 0018 0018 0030 0018 0018 0018 0030 0018 0030 0018 0030 0018 0030 0018 0018 0018 0018 0018 0018 0018 0018 03fa 0061 0018 0018 0018 0018 0018 0030 0018 0018 0018 0030 0018 0030 0018 0030 0018 0030 0018 0018 0018 0018 0018 0018 0018 0018 0018"

        //Down
        private val CMD_TV_DOWN: String =
            "0000 0067 0027 0000 0061 0018 0030 0018 0018 0018 0030 0018 0018 0018 0030 0018 0030 0018 0030 0018 0030 0018 0018 0018 0018 0018 0018 0018 0018 03dc 0061 0018 0030 0018 0018 0018 0030 0018 0018 0018 0030 0018 0030 0018 0030 0018 0030 0018 0018 0018 0018 0018 0018 0018 0018 03dc 0061 0018 0030 0018 0018 0018 0030 0018 0018 0018 0030 0018 0030 0018 0030 0018 0030 0018 0018 0018 0018 0018 0018 0018 0018 0018"

        //Left
        private val CMD_TV_LEFT: String =
            "0000 0067 0027 0000 0061 0018 0018 0018 0018 0018 0030 0018 0018 0018 0030 0018 0030 0018 0018 0018 0030 0018 0018 0018 0018 0018 0018 0018 0018 040c 0061 0018 0018 0018 0018 0018 0030 0018 0018 0018 0030 0018 0030 0018 0018 0018 0030 0018 0018 0018 0018 0018 0018 0018 0018 040c 0061 0018 0018 0018 0018 0018 0030 0018 0018 0018 0030 0018 0030 0018 0018 0018 0030 0018 0018 0018 0018 0018 0018 0018 0018 0018"

        //Right
        private val CMD_TV_RIGHT: String =
            "0000 0067 0027 0000 0061 0018 0030 0018 0030 0018 0018 0018 0018 0018 0030 0018 0030 0018 0018 0018 0030 0018 0018 0018 0018 0018 0018 0018 0018 03f4 0061 0018 0030 0018 0030 0018 0018 0018 0018 0018 0030 0018 0030 0018 0018 0018 0030 0018 0018 0018 0018 0018 0018 0018 0018 03f4 0061 0018 0030 0018 0030 0018 0018 0018 0018 0018 0030 0018 0030 0018 0018 0018 0030 0018 0018 0018 0018 0018 0018 0018 0018 0018"

        //Power Off
        private val CMD_TV_POWER_OFF: String =
            "0000 0066 0027 0000 005f 0018 0030 0018 0030 0018 0030 0018 0030 0018 0019 0018 0030 0018 0019 0018 0030 0018 0019 0018 0019 0018 0019 0018 0019 03de 005f 0018 0030 0018 0030 0018 0030 0018 0030 0018 0019 0018 0030 0018 0019 0018 0030 0018 0019 0018 0019 0018 0019 0018 0019 03de 005f 0018 0030 0018 002f 0018 0030 0018 0030 0018 0019 0018 0030 0018 0019 0018 0030 0018 0019 0018 0019 0018 0019 0018 0019 0018"

        //Back
        private val CMD_TV_BACK: String =
            "0000 0067 0000 000d 0061 0018 0030 0018 0018 0018 0030 0018 0018 0018 0018 0018 0030 0018 0030 0018 0030 0018 0018 0018 0018 0018 0018 0018 0018 03ec 0061 0018 0030 0018 0018 0018 0030 0018 0018 0018 0018 0018 0030 0018 0030 0018 0030 0018 0018 0018 0018 0018 0018 0018 0018 03ec 0061 0018 0030 0018 0018 0018 0030 0018 0018 0018 0018 0018 0030 0018 0030 0018 0030 0018 0018 0018 0018 0018 0018 0018 0018 03ec"

        //HDMI 1
        private val CMD_TV_HDMI1: String =
            "0000 0066 0030 0000 005f 0018 0019 0018 0030 0018 0019 0018 0030 0018 0030 0018 0019 0018 0030 0018 0019 0018 0030 0018 0019 0018 0030 0018 0030 0018 0019 0018 0019 0018 0019 0335 005f 0018 0019 0018 0030 0018 0019 0018 0030 0018 0030 0018 0019 0018 0030 0018 0019 0018 0030 0018 0019 0018 0030 0018 0030 0018 0019 0018 0019 0018 0019 0335 005f 0018 0019 0018 0030 0018 0019 0018 0030 0018 0030 0018 0019 0018 0030 0018 0019 0018 0030 0018 0019 0018 0030 0018 0030 0018 0019 0018 0019 0018 0019 0018"

        //HDMI 2
        private val CMD_TV_HDMI2: String =
            "0000 0066 0030 0000 005f 0018 0030 0018 0030 0018 0019 0018 0030 0018 0030 0018 0019 0018 0030 0018 0019 0018 0030 0018 0019 0018 0030 0018 0030 0018 0019 0018 0019 0018 0019 031e 005f 0018 0030 0018 0030 0018 0019 0018 0030 0018 0030 0018 0019 0018 0030 0018 0019 0018 0030 0018 0019 0018 0030 0018 0030 0018 0019 0018 0019 0018 0019 031e 005f 0018 0030 0018 0030 0018 0019 0018 0030 0018 0030 0018 0019 0018 0030 0018 0019 0018 0030 0018 0019 0018 0030 0018 0030 0018 0019 0018 0019 0018 0019 0018"

        //HDMI 3
        private val CMD_TV_HDMI3: String =
            "0000 0066 0030 0000 005f 0018 0019 0018 0019 0018 0030 0018 0030 0018 0030 0018 0019 0018 0030 0018 0019 0018 0030 0018 0019 0018 0030 0018 0030 0018 0019 0018 0019 0018 0019 0335 005f 0018 0019 0018 0019 0018 0030 0018 0030 0018 0030 0018 0019 0018 0030 0018 0019 0018 0030 0018 0019 0018 0030 0018 0030 0018 0019 0018 0019 0018 0019 0335 005f 0018 0019 0018 0019 0018 0030 0018 0030 0018 0030 0018 0019 0018 0030 0018 0019 0018 0030 0018 0019 0018 0030 0018 0030 0018 0019 0018 0019 0018 0019 0018"

        //HDMI 4
        private val CMD_TV_HDMI4: String =
            "0000 0066 0030 0000 005f 0018 0030 0018 0019 0018 0030 0018 0030 0018 0030 0018 0019 0018 0030 0018 0019 0018 0030 0018 0019 0018 0030 0018 0030 0018 0019 0018 0019 0018 0019 031e 005f 0018 0030 0018 0019 0018 0030 0018 0030 0018 0030 0018 0019 0018 0030 0018 0019 0018 0030 0018 0019 0018 0030 0018 0030 0018 0019 0018 0019 0018 0019 031e 005f 0018 0030 0018 0019 0018 0030 0018 002f 0018 0030 0018 0019 0018 0030 0018 0019 0018 0030 0018 0019 0018 0030 0018 0030 0018 0019 0018 0019 0018 0019 0018"

        //Video 1
        private val CMD_TV_VIDEO1: String =
            "0000 0067 0000 000d 0061 0018 0018 0018 0018 0018 0018 0018 0018 0018 0018 0018 0018 0018 0030 0018 0030 0018 0018 0018 0018 0018 0018 0018 0018 043a 0061 0018 0018 0018 0018 0018 0018 0018 0018 0018 0018 0018 0018 0018 0030 0018 0030 0018 0018 0018 0018 0018 0018 0018 0018 043a 0061 0018 0018 0018 0018 0018 0018 0018 0018 0018 0018 0018 0018 0018 0030 0018 0030 0018 0018 0018 0018 0018 0018 0018 0018 043a"

        //Video 2
        private val CMD_TV_VIDEO2: String =
            "0000 0066 0027 0000 005f 0018 0030 0018 0019 0018 0019 0018 0019 0018 0019 0018 0019 0018 0030 0018 0030 0018 0019 0018 0019 0018 0019 0018 0019 0424 005f 0018 0030 0018 0019 0018 0019 0018 0019 0018 0019 0018 0019 0018 0030 0018 0030 0018 0019 0018 0019 0018 0019 0018 0019 0424 005f 0018 0030 0018 0019 0018 0019 0018 0019 0018 0019 0018 0019 0018 0030 0018 0030 0018 0019 0018 0019 0018 0019 0018 0019 0018"

        //Recall
        private val CMD_TV_JUMP: String =
            "0000 0067 0000 000d 0060 0018 0030 0018 0030 0018 0018 0018 0030 0018 0030 0018 0030 0018 0018 0018 0030 0018 0018 0018 0018 0018 0018 0018 0018 03df 0060 0018 0030 0018 0030 0018 0018 0018 0030 0018 0030 0018 0030 0018 0018 0018 0030 0018 0018 0018 0018 0018 0018 0018 0018 03df 0060 0018 0030 0018 0030 0018 0018 0018 0030 0018 0030 0018 0030 0018 0018 0018 0030 0018 0018 0018 0018 0018 0018 0018 0018 03df"

    }
}