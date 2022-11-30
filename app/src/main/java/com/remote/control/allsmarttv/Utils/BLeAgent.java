package com.remote.control.allsmarttv.Utils;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.os.Handler;
import android.os.ParcelUuid;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

@TargetApi(21)
public class BLeAgent extends DiscoveryAgent {
    private static final long MAX_RESULTS_PER_SCAN = 10;
    private static final long REPEATED_DISCOVERY_DELAY = 5000;
    private static final String TAG = "AtvRemote.BleDiscoverer";
    private ScanCallback mCallback;
    private Handler mHandler = new Handler();
    private int mSeenResults = 0;
    private Runnable mStartDiscovery;

    static int access$008(BLeAgent bLeAgent) {
        int i = bLeAgent.mSeenResults;
        bLeAgent.mSeenResults = i + 1;
        return i;
    }

    private BluetoothAdapter getAdapter() {
        return BluetoothAdapter.getDefaultAdapter();
    }

    private BluetoothLeScanner getScanner() {
        if (getAdapter() != null) {
            return getAdapter().getBluetoothLeScanner();
        }
        return null;
    }

    private void postDelayedStartDiscovery(final Listener listener, final Handler handler) {
        if (this.mStartDiscovery != null) {
            this.mHandler.removeCallbacks(this.mStartDiscovery);
        }
        this.mStartDiscovery = new Runnable() {

            public void run() {
                BLeAgent.this.startDiscovery(listener, handler);
            }
        };
        this.mHandler.postDelayed(this.mStartDiscovery, REPEATED_DISCOVERY_DELAY);
    }

    @Override
    public  void destroy() {
        super.destroy();
    }

    @Override
    public void startDiscovery(final Listener listener, final Handler handler) {
        if (getScanner() == null) {
            Log.w(TAG, "Bluetooth LE scanner unavailable");
        } else if (!getAdapter().isEnabled()) {
            Log.w(TAG, "Bluetooth LE adapter not enabled");
        } else if (this.mCallback == null) {
            this.mSeenResults = 0;
            ArrayList arrayList = new ArrayList();
            arrayList.add(new ScanFilter.Builder().setServiceUuid(new ParcelUuid(BluetoothConstant.MY_UUID)).build());
            ScanSettings build = new ScanSettings.Builder().build();
            this.mCallback = new ScanCallback() {

                @Override
                public void onBatchScanResults(List<ScanResult> list) {
                    super.onBatchScanResults(list);
                }

                public void onScanFailed(int i) {
                    super.onScanFailed(i);
                    Log.e(BLeAgent.TAG, "LE Scan failed");
                }

                public void onScanResult(int i, final ScanResult scanResult) {
                    super.onScanResult(i, scanResult);
                    handler.post(new Runnable() {

                        public void run() {
                            listener.onDeviceFound(new BtDevicesInfoUtil(scanResult.getDevice()));
                        }
                    });
                    BLeAgent.access$008(BLeAgent.this);
                    if (!(((long) BLeAgent.this.mSeenResults) <= BLeAgent.MAX_RESULTS_PER_SCAN)) {
                        BLeAgent.this.stopDiscovery();
                        BLeAgent.this.postDelayedStartDiscovery(listener, handler);
                    }
                }
            };
            getScanner().startScan(arrayList, build, this.mCallback);
        }
    }

    @Override
    public void stopDiscovery() {
        if (getScanner() == null) {
            Log.w(TAG, "Bluetooth LE scanner unavailable");
        } else if (getAdapter().isEnabled()) {
            if (this.mCallback != null) {
                getScanner().stopScan(this.mCallback);
                this.mCallback = null;
            }
            if (this.mStartDiscovery != null) {
                this.mHandler.removeCallbacks(this.mStartDiscovery);
                this.mStartDiscovery = null;
            }
        } else {
            Log.w(TAG, "Bluetooth LE adapter not enabled");
        }
    }
}
