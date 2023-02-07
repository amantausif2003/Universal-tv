package com.remote.control.allsmarttv.appUi

import android.content.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.CompletionInfo
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.ExtractedText
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.remote.control.allsmarttv.R
import com.remote.control.allsmarttv.adapter.SearchingTvAdapter
import com.remote.control.allsmarttv.databinding.ActivityWifiScreenBinding
import com.remote.control.allsmarttv.dialog.NewPairDialog
import com.remote.control.allsmarttv.utils.*
import java.text.Collator

class WifiScreen : AppCompatActivity(), SearchingTvAdapter.SearchingTvAdapterCallBack {

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


    lateinit var mainBinding: ActivityWifiScreenBinding

    private var mDiscoverer: Discoverer? = null
    private val devicesList = ArrayList<DevicesInfoUtil>()
    private val hubAdapter: ArrayList<DevicesInfoUtil> = ArrayList()
    private val listener: Listener? = null
    private val handler = Handler(Looper.getMainLooper())
    private var clientService: ClientService? = null
    private var configured = false
    private var onRemoteListener: OnRemoteListener? = null

    private var workerHandler = Handler(Looper.getMainLooper())
    private var searchingTvAdapter: SearchingTvAdapter? = SearchingTvAdapter(this)

    private var newPairDialog: NewPairDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityWifiScreenBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)

        mDiscoverer = Discoverer(this@WifiScreen)

        setUpUiClick()

        newPairDialog =
            NewPairDialog(this@WifiScreen, object : NewPairDialog.NewDeleteDialogCallback {

                override fun yesPair(string: String?) {
                    if (string != null) {
                        clientService!!.setPairingSecret(string)
                    } else {
                        Util.showToast(this@WifiScreen, "Enter null Code")
                    }
                }

                override fun noPair() {

                }

            })

    }

    private fun setUpListOfDevices() {
        mainBinding.reSearchTv.adapter = searchingTvAdapter
        searchingTvAdapter?.updateDeviceList(hubAdapter)
    }

    private fun setUpUiClick() {

        mainBinding.btnConnect.setOnClickListener {
            startClientListenerService()
        }

        mainBinding.tvPower.setOnClickListener {
            setUpClick(26, true)
        }

        mainBinding.tvAv.setOnClickListener {
            setUpClick(4, true)
        }

        mainBinding.tvMute.setOnClickListener {
            setUpClick(91, true)
        }

        mainBinding.tvExit.setOnClickListener {
            setUpClick(3, true)
        }

        mainBinding.tvChNext.setOnClickListener {
            setUpClick(166, true)
        }

        mainBinding.tvChPrev.setOnClickListener {
            setUpClick(167, true)

        }

        mainBinding.tvVolUp.setOnClickListener {
            setUpClick(24, true)

        }

        mainBinding.tvVolDown.setOnClickListener {
            setUpClick(25, true)

        }

        mainBinding.tvOk.setOnClickListener {
            setUpClick(23, true)
        }

        mainBinding.tvUp.setOnClickListener {
            setUpClick(19, true)

        }

        mainBinding.tvDown.setOnClickListener {
            setUpClick(20, true)

        }

        mainBinding.tvLeft.setOnClickListener {
            setUpClick(21, true)

        }

        mainBinding.tvRight.setOnClickListener {
            setUpClick(22, true)
        }

        mainBinding.btnBack.setOnClickListener {
            finish()
        }

    }

    private fun setUpClick(keyCode: Int, isClick: Boolean) {

        if (isClick) {
            Log.d("mySetUpClick", "Click if ${keyCode}")
            onRemoteListener?.sendKeyEvent(keyCode, 0)
            setUpClick(keyCode, false)
        } else {
            Log.d("mySetUpClick", "Click else ${keyCode}")
            onRemoteListener?.sendKeyEvent(keyCode, 1)
        }

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
                hubAdapter.clear()
                hubAdapter.add(devicesInfoUtil)
                setUpListOfDevices()

                mainBinding.progressRoot.visibility = View.GONE
                mainBinding.searchTvRoot.visibility = View.VISIBLE
            }
        }

        override fun onDeviceLost(devicesInfoUtil: DevicesInfoUtil) {
            Log.d("myTag", "onDeviceLost")
            if (devicesList.remove(devicesInfoUtil)) {
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
                hubAdapter.add(devicesInfoUtil2)
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
            Log.d("myTag", "onStartDiscoveryFailed ${i}")
        }
    }

    private fun startClientListenerService() {
        val serviceIntent = Intent(this, ClientService::class.java)
        startService(serviceIntent)
        bindService(serviceIntent, serviceConnection, BIND_AUTO_CREATE)
    }

    private val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(componentName: ComponentName) {
            Log.d("myRemovte", "onServiceDisconnected")
        }

        override fun onServiceConnected(componentName: ComponentName, iBinder: IBinder) {
            Log.d("myRemovte", "onServiceConnected")
            clientService = (iBinder as ClientService.LocalBinder).service
            clientService?.setRemoteListener(clientListener)
            onRemoteListener = clientService
        }
    }

    private val clientListener: ClientService.Listener = object : ClientService.Listener() {

        private var hasSentSound = false

        override fun onConnecting(device: Device) {
            configured = false
            Log.d("myClientListener", "onConnecting")
        }

        override fun onConnected(device: Device) {
            Log.d("myClientListener", "onConnected")
            mainBinding.searchTvRoot.visibility = View.GONE
            mainBinding.removeUiRoot.visibility = View.VISIBLE
        }

        override fun onConnectFailed(device: Device) {
            forgetDevice()
            Log.d("myClientListener", "onConnected")
        }

        override fun onDisconnected(device: Device) {
            configured = false
            Log.d("myClientListener", "onConnected")
        }

        override fun onPairingRequired(device: Device) {
            Log.d("myClientListener", "onPairingRequired")
            newPairDialog?.showDialog()
        }

        override fun onShowIme(
            device: Device,
            editorInfo: EditorInfo,
            z: Boolean,
            extractedText: ExtractedText
        ) {
        }

        override fun onHideIme(device: Device) {}
        override fun onStartVoice(device: Device) {
            hasSentSound = false
        }

        override fun onVoiceSoundLevel(device: Device, i: Int) {
            if (!hasSentSound) {
                hasSentSound = true
            }
        }

        override fun onStopVoice(device: Device) {
            hasSentSound = false
        }

        override fun onCompletionInfo(device: Device, completionInfoArr: Array<CompletionInfo>) {}
        override fun onConfigureSuccess(device: Device) {
            configured = true
            logAtvBuildInfo(device)
        }

        private fun logAtvBuildInfo(device: Device) {}
        override fun onConfigureFailure(device: Device, i: Int) {
            configured = false
            forgetDevice()
        }

        override fun onException(device: Device, exc: Exception) {}
        override fun onServiceDeath() {
            serviceConnection.onServiceDisconnected(null)
        }

        override fun onDeveloperStatus(device: Device, z: Boolean) {}
        override fun onAsset(
            device: Device,
            str: String,
            map: Map<String, String>,
            bArr: ByteArray
        ) {
        }

        override fun onBundle(device: Device, i: Int, bundle: Bundle) {}
        override fun onCapabilities(device: Device, capabilitiesClass: CapabilitiesClass) {
            capabilitiesClass.secondScreenSetup()
        }

        override fun onBugReportStatus(device: Device, i: Int) {
            when (i) {
                1, 3 -> return
                2 -> {}
                else -> {}
            }
        }
    }

    fun forgetDevice() {
        TvPreferences.saveDeviceInfo(applicationContext, null)
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

    override fun selectTv(itemPosition: Int) {

        startClientListenerService()

    }
}