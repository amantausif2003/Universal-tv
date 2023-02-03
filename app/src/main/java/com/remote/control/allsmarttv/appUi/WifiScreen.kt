package com.remote.control.allsmarttv.appUi

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import com.remote.control.allsmarttv.databinding.ActivityWifiScreenBinding
import com.remote.control.allsmarttv.utils.ClientService
import com.remote.control.allsmarttv.utils.DevicesInfoUtil
import com.remote.control.allsmarttv.utils.Discoverer
import com.remote.control.allsmarttv.utils.TvPreferences
import java.text.Collator

class WifiScreen : AppCompatActivity() {

    //Ok key: 23,66,96,100
    //backPress key: 97
    //volup key: 24
    //voldown key: 25
    //number powerOff key:26
    //searching key:84

    //cursorUp key: 19
    //cursorDown key: 20
    //cursorLeft key: 21
    //cursorRight key: 22
    //home key: 3
    //per-ch key: 4
    //ch_up key: 166
    //ch_down key: 167
    //mute key: 164,91
    //number Press 0 key:7
    //number Press 1 key:8
    //number Press 2 key:9
    //number Press 3 key:10
    //number Press 4 key:11
    //number Press 5 key:12
    //number Press 6 key:13
    //number Press 7 key:14
    //number Press 8 key:15
    //number Press 9 key:16


    lateinit var binding:ActivityWifiScreenBinding

    private var mDiscoverer: Discoverer? = null
    private val devicesList = ArrayList<DevicesInfoUtil>()
    private val hubAdapter: ArrayAdapter<DevicesInfoUtil>? = null
    private val listener: Listener? = null
    private val handler = Handler(Looper.getMainLooper())
    private var clientService: ClientService? = null
    private var configured = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWifiScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mDiscoverer = Discoverer(this@WifiScreen)

    }

    override fun onResume() {
        super.onResume()
        val intentFilter = IntentFilter()
        intentFilter.addAction("android.net.wifi.supplicant.CONNECTION_CHANGE")
        intentFilter.addAction("android.net.wifi.supplicant.STATE_CHANGE")
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE")
        intentFilter.addAction("android.bluetooth.adapter.action.STATE_CHANGED")
        registerReceiver(broadcastReceiver, intentFilter)
        restartSearching()
    }

    private val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Log.d("myBroadcastReceiver", "onReceive")
            restartSearching()
        }
    }

    fun restartSearching() {
        try {
            mDiscoverer!!.stopDiscovery()
            mDiscoverer!!.startDiscovery(mDiscoveryListener, handler)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private val mDiscoveryListener = HubDiscoveryListener()

    inner class HubDiscoveryListener : Discoverer.DiscoveryListener() {
        override fun onDeviceFound(devicesInfoUtil: DevicesInfoUtil) {
            Log.d("myTag", "onDeviceFound")
            if (!devicesList.contains(devicesInfoUtil)) {
                TvPreferences.saveDeviceInfo(this@WifiScreen, devicesInfoUtil)
            }
        }

        override fun onDeviceLost(devicesInfoUtil: DevicesInfoUtil) {
            Log.d("myTag", "onDeviceLost")
            if (devicesList.remove(devicesInfoUtil)) {
                hubAdapter!!.notifyDataSetChanged()
                updateError()
                listener?.onDevicesUpdated()
            }
        }

        override fun onDeviceReplace(
            devicesInfoUtil: DevicesInfoUtil,
            devicesInfoUtil2: DevicesInfoUtil
        ) {
            Log.d("myTag", "onDeviceReplace")
            if (devicesList.remove(devicesInfoUtil)) {
                hubAdapter!!.add(devicesInfoUtil2)
                hubAdapter.sort(mComparator)
                hubAdapter.notifyDataSetChanged()
                updateError()
                listener?.onDevicesUpdated()
            }
        }

        override fun onDiscoveryStarted() {
            Log.d("myTag", "onDiscoveryStarted")
        }

        override fun onDiscoveryStopped() {
            Log.d("myTag", "onDiscoveryStopped")
        }

        override fun onStartDiscoveryFailed(i: Int) {
            Log.d("myTag", "onStartDiscoveryFailed")
        }
    }

    private val mComparator: Comparator<DevicesInfoUtil> = object : Comparator<DevicesInfoUtil> {
        val mCollator = Collator.getInstance()
        override fun compare(
            devicesInfoUtil: DevicesInfoUtil,
            devicesInfoUtil2: DevicesInfoUtil
        ): Int {
            return mCollator.compare(devicesInfoUtil.name, devicesInfoUtil2.name)
        }
    }

    private fun updateError() {
        Toast.makeText(this, "Update Error", Toast.LENGTH_SHORT).show()
    }

    interface Listener {
        fun onDeviceSelected(devicesInfoUtil: DevicesInfoUtil?)
        fun onDevicesUpdated()
        fun onNoConnectivity()
        fun onNoDevices()
    }
}