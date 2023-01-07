package com.remote.control.allsmarttv.utils;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

public class BluetoothAgent extends DiscoveryAgent {

    private static final String TAG = "AtvRemote.BluetoothAgnt";
    private final BluetoothAdapter mBluetoothAdapter;
    private Set<BluetoothDevice> mCached = new HashSet();
    private final Handler mCallbackHandler = new Handler(Looper.getMainLooper());
    private final Context mContext;
    private boolean mDiscovering = false;
    private final Handler mHandler;
    private final HandlerThread mHandlerThread = new HandlerThread(TAG);
    private Set<BluetoothDevice> mKnown = new HashSet();
    private CountDownLatch mLatch;
    private Listener mListener;
    private List<BluetoothDevice> mPending;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            BluetoothDevice bluetoothDevice;
            Parcelable[] parcelableArrayExtra;
            String action = intent.getAction();
            if ("android.bluetooth.adapter.action.DISCOVERY_STARTED".equals(action)) {
                BluetoothAgent.this.mCallbackHandler.post(new Runnable() {

                    public void run() {
                        if (BluetoothAgent.this.mListener != null) {
                            BluetoothAgent.this.mListener.onDiscoveryStarted();
                        }
                    }
                });
            } else if ("android.bluetooth.adapter.action.DISCOVERY_FINISHED".equals(action)) {
                BluetoothAgent.this.mDiscovering = false;
                BluetoothAgent.this.mHandler.removeMessages(1);
                BluetoothAgent.this.mHandler.sendEmptyMessage(2);
            } else if ("android.bluetooth.device.action.FOUND".equals(action)) {
                BluetoothDevice bluetoothDevice2 = (BluetoothDevice) intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
                if (bluetoothDevice2 == null) {
                    Log.e(BluetoothAgent.TAG, "Device is null for ACTION_FOUND");
                } else if (bluetoothDevice2.getBluetoothClass() != null) {
                    if (bluetoothDevice2.getBluetoothClass().getMajorDeviceClass() == 1024) {
                        if (!BluetoothAgent.this.mCached.contains(bluetoothDevice2)) {
                            BluetoothAgent.this.mPending.add(bluetoothDevice2);
                        } else {
                            BluetoothAgent.this.mKnown.add(bluetoothDevice2);
                            final BtDevicesInfoUtil bluetoothDeviceInfo = new BtDevicesInfoUtil(bluetoothDevice2);
                            BluetoothAgent.this.mCallbackHandler.post(new Runnable() {

                                public void run() {
                                    if (BluetoothAgent.this.mListener != null) {
                                        BluetoothAgent.this.mListener.onDeviceFound(bluetoothDeviceInfo);
                                    }
                                }
                            });
                        }
                    }
                    BluetoothAgent.this.mHandler.removeMessages(1);
                    BluetoothAgent.this.mHandler.sendEmptyMessageDelayed(1, 5000);
                } else {
                    Log.e(BluetoothAgent.TAG, "Device's Bluetooth class is null");
                }
            } else if ("android.bluetooth.device.action.UUID".equals(action) && (bluetoothDevice = (BluetoothDevice) intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE")) != null && BluetoothAgent.this.mResolving != null && TextUtils.equals(bluetoothDevice.getAddress(), BluetoothAgent.this.mResolving.getAddress())) {
                if (!BluetoothAgent.this.mDiscovering && (parcelableArrayExtra = intent.getParcelableArrayExtra("android.bluetooth.device.extra.UUID")) != null) {
                    for (Parcelable parcelable : parcelableArrayExtra) {
                        if (BluetoothConstant.MY_UUID.equals(UUID.fromString(parcelable.toString().toUpperCase()))) {
                            BluetoothAgent.this.mKnown.add(bluetoothDevice);
                            final BtDevicesInfoUtil bluetoothDeviceInfo2 = new BtDevicesInfoUtil(bluetoothDevice);
                            BluetoothAgent.this.mCallbackHandler.post(new Runnable() {

                                public void run() {
                                    if (BluetoothAgent.this.mListener != null) {
                                        BluetoothAgent.this.mListener.onDeviceFound(bluetoothDeviceInfo2);
                                    }
                                }
                            });
                        }
                    }
                }
                BluetoothAgent.this.mLatch.countDown();
            }
        }
    };
    private BluetoothDevice mResolving;

    public BluetoothAgent(Context context) {
        this.mContext = context;
        this.mPending = new ArrayList();
        IntentFilter intentFilter = new IntentFilter("android.bluetooth.device.action.FOUND");
        intentFilter.addAction("android.bluetooth.device.action.UUID");
        intentFilter.addAction("android.bluetooth.adapter.action.DISCOVERY_STARTED");
        intentFilter.addAction("android.bluetooth.adapter.action.DISCOVERY_FINISHED");
        this.mContext.registerReceiver(this.mReceiver, intentFilter);
        this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.mHandlerThread.start();
        this.mHandler = new Handler(this.mHandlerThread.getLooper()) {

            public void handleMessage(Message message) {
                switch (message.what) {
                    case 1:
                        if (BluetoothAgent.this.mBluetoothAdapter.isDiscovering()) {
                            BluetoothAgent.this.mBluetoothAdapter.cancelDiscovery();
                        }
                        BluetoothAgent.this.mDiscovering = false;
                        return;
                    case 2:
                        if (BluetoothAgent.this.mDiscovering) {
                            return;
                        }
                        if (!BluetoothAgent.this.mPending.isEmpty()) {
                            BluetoothAgent.this.mLatch = new CountDownLatch(1);
                            BluetoothAgent.this.mResolving = (BluetoothDevice) BluetoothAgent.this.mPending.remove(0);
                            if (!BluetoothAgent.this.mKnown.contains(BluetoothAgent.this.mResolving)) {
                                BluetoothAgent.this.mResolving.fetchUuidsWithSdp();
                                try {
                                    BluetoothAgent.this.mLatch.await();
                                    if (!BluetoothAgent.this.mDiscovering) {
                                        BluetoothAgent.this.mHandler.sendEmptyMessage(2);
                                        return;
                                    } else if (!BluetoothAgent.this.mBluetoothAdapter.isDiscovering()) {
                                        BluetoothAgent.this.mBluetoothAdapter.startDiscovery();
                                        BluetoothAgent.this.mHandler.sendEmptyMessageDelayed(1, 5000);
                                        return;
                                    } else {
                                        return;
                                    }
                                } catch (InterruptedException e) {
                                    Log.w(BluetoothAgent.TAG, "Interrupted while waiting on UUIDs");
                                    return;
                                }
                            } else {
                                final BtDevicesInfoUtil bluetoothDeviceInfo = new BtDevicesInfoUtil(BluetoothAgent.this.mResolving);
                                BluetoothAgent.this.mCallbackHandler.post(new Runnable() {

                                    public void run() {
                                        if (BluetoothAgent.this.mListener != null) {
                                            BluetoothAgent.this.mListener.onDeviceFound(bluetoothDeviceInfo);
                                        }
                                    }
                                });
                                BluetoothAgent.this.mHandler.sendEmptyMessage(2);
                                return;
                            }
                        } else {
                            BluetoothAgent.this.mResolving = null;
                            return;
                        }
                    default:
                        return;
                }
            }
        };
    }

    @Override
    public void destroy() {
        this.mContext.unregisterReceiver(this.mReceiver);
        this.mHandler.removeCallbacksAndMessages(null);
        this.mHandlerThread.quit();
    }

    @Override
    public void startDiscovery(Listener listener, Handler handler) {
        this.mListener = listener;
        if (this.mBluetoothAdapter == null) {
            this.mCallbackHandler.post(new Runnable() {

                public void run() {
                    if (BluetoothAgent.this.mListener != null) {
                        BluetoothAgent.this.mListener.onStartDiscoveryFailed(-1);
                    }
                }
            });
        } else if (this.mBluetoothAdapter.isEnabled()) {
            if (this.mBluetoothAdapter.isDiscovering()) {
                this.mBluetoothAdapter.cancelDiscovery();
            }
            this.mDiscovering = true;
            this.mHandler.removeMessages(2);
            this.mCached.addAll(this.mKnown);
            this.mKnown.clear();
            this.mPending.clear();
            if (this.mResolving == null) {
                this.mBluetoothAdapter.startDiscovery();
                this.mHandler.sendEmptyMessageDelayed(1, 5000);
            }
        } else {
            this.mCallbackHandler.post(new Runnable() {

                public void run() {
                    if (BluetoothAgent.this.mListener != null) {
                        BluetoothAgent.this.mListener.onStartDiscoveryFailed(-2);
                    }
                }
            });
        }
    }

    @Override
    public void stopDiscovery() {
        if (this.mBluetoothAdapter != null) {
            if (this.mBluetoothAdapter.isDiscovering()) {
                this.mBluetoothAdapter.cancelDiscovery();
            }
            this.mDiscovering = false;
            final Listener listener = this.mListener;
            this.mListener = null;
            this.mCallbackHandler.post(new Runnable() {

                public void run() {
                    if (listener != null) {
                        listener.onDiscoveryStopped();
                    }
                }
            });
        }
    }
}
