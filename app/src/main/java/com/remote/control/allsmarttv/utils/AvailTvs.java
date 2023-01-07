package com.remote.control.allsmarttv.utils;

import android.content.Context;
import android.util.Log;

import com.jaku.api.DeviceRequests;
import com.jaku.api.QueryRequests;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class AvailTvs implements Callable {

    private static final String TAG = AvailTvs.class.getName();

    private Context mContext;

    public AvailTvs(Context context) {
        mContext = context;
    }

    public List<Device_rokuTv> call() {

        List<Device_rokuTv> devices_roku = new ArrayList();

        List<Device_rokuTv> availabletvs = new ArrayList<Device_rokuTv>();

        try {
            List<Device_rokuTv> rokuDevices = new ArrayList<>();

            final WifiManagerClass wifiManagerClass = new WifiManagerClass(mContext);

            if (wifiManagerClass.isWifiEnabled()) {
                // Scan the mobile access point for devices
                rokuDevices.addAll(scanAccessPointForDevices());
            } else {
                List<com.jaku.model.Device> jakuDevices = DeviceRequests.discoverDevices();

                for (com.jaku.model.Device jakuDevice: jakuDevices) {
                    rokuDevices.add(Device_rokuTv.fromDevice(jakuDevice));
                }
            }

            for (Device_rokuTv device: rokuDevices) {
                boolean exists = false;

                for (int j = 0; j < devices_roku.size(); j++) {
                    if (devices_roku.get(j).getSerialNumber().equals(device.getSerialNumber())) {
                        exists = true;
                        break;
                    }
                }

                if (!exists) {
                    availabletvs.add(device);
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return availabletvs;
    }

    private ArrayList<Device_rokuTv> scanAccessPointForDevices() {
        ArrayList<Device_rokuTv> availableDevices = new ArrayList<Device_rokuTv>();

        final WifiManagerClass wifiManagerClass = new WifiManagerClass(mContext);

        if (wifiManagerClass.isWifiEnabled()) {
            ArrayList<ScanRokuUtil> clients = wifiManagerClass.getTvList(false, 3000);

            Log.d(TAG, "Access point scan completed.");

            if (clients != null) {
                Log.d(TAG, "Found " + clients.size() + " connected devices.");

                for (ScanRokuUtil scanRokuUtil : clients) {
                    Log.d(TAG, "Device: " + scanRokuUtil.getDevice() +
                            " HW Address: " + scanRokuUtil.getHwaddr() +
                            " IP Address:  " + scanRokuUtil.getIpAddr());

                    try {
                        Device_rokuTv device = Device_rokuTv.fromDevice(QueryRequests.queryDeviceInfo("http://" + scanRokuUtil.getIpAddr() + ":8060"));
                        device.setHost("http://" + scanRokuUtil.getIpAddr() + ":8060");
                        availableDevices.add(device);
                    } catch (IOException ex) {
                        Log.e(TAG, "Invalid device: " + ex.getMessage());
                    }
                }
            }
        }

        return availableDevices;
    }
}