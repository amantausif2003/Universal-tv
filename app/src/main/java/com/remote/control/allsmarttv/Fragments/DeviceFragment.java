package com.remote.control.allsmarttv.Fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.remote.control.allsmarttv.R;
import com.remote.control.allsmarttv.Utils.DevicesInfoUtil;
import com.remote.control.allsmarttv.Utils.Discoverer;
import com.remote.control.allsmarttv.Utils.NetworkUtil;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;

public class DeviceFragment extends Fragment {

    private Handler handler = new Handler(Looper.getMainLooper());
    private ArrayAdapter<DevicesInfoUtil> hubAdapter;
    private ArrayList<DevicesInfoUtil> devicesList = new ArrayList<>();
    private ListView devices_list;
    private long startTime = System.currentTimeMillis();
    private WifiManager wifiManager;
    private TextView wifiSetting, tv_name, noTvState;
    private RelativeLayout list_lay;
    public ImageView device_pic;
    ProgressBar progress;
    private Listener listener;
    private View noTvlay, noWifilay;
    private static final long ERROR_DELAY = 250;
    private static final long IGNORE_INTENT_DELAY = 200;

    private final Comparator<DevicesInfoUtil> mComparator = new Comparator<DevicesInfoUtil>() {
        Collator mCollator = Collator.getInstance();

        public int compare(DevicesInfoUtil devicesInfoUtil, DevicesInfoUtil devicesInfoUtil2) {
            return this.mCollator.compare(devicesInfoUtil.getName(), devicesInfoUtil2.getName());
        }
    };
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        public void onReceive(Context context, Intent intent) {
            DeviceFragment.this.restartSearching();
            if (!(System.currentTimeMillis() - DeviceFragment.this.startTime <= DeviceFragment.IGNORE_INTENT_DELAY)) {
                DeviceFragment.this.updateError();
            }
        }
    };
    private Discoverer mDiscoverer;
    private final HubDiscoveryListener mDiscoveryListener = new HubDiscoveryListener();
    private Runnable errorTimer = new Runnable() {

        public void run() {
            DeviceFragment.this.updateError();
        }
    };

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof Listener) {
            this.listener = (Listener) activity;
            this.hubAdapter = new ArrayAdapter<DevicesInfoUtil>(activity, -1, this.devicesList) {

                public View getView(int i, View view, ViewGroup viewGroup) {
                    View inflate = view != null ? view : LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.device_item, viewGroup, false);

                    device_pic = inflate.findViewById(R.id.image_tv);

                    tv_name = inflate.findViewById(R.id.tv_name);
                    tv_name.setText(((DevicesInfoUtil) getItem(i)).getName());

                    return inflate;
                }
            };
            this.wifiManager = (WifiManager) activity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            return;
        }

        throw new ClassCastException(activity.toString() + " must implement OnSelectListener");
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {

        View inflate = layoutInflater.inflate(R.layout.fragment_device, viewGroup, false);
        this.devices_list = (ListView) inflate.findViewById(R.id.smart_tvs_list);
        this.progress = inflate.findViewById(R.id.progress);
        this.devices_list.setAdapter((ListAdapter) this.hubAdapter);
        this.devices_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {


                        if (devicesList.size() != 0) {

                            DevicesInfoUtil devicesInfoUtil = (DevicesInfoUtil) DeviceFragment.this.hubAdapter.getItem(i);
                            if (DeviceFragment.this.listener != null) {
                                progress.setVisibility(View.VISIBLE);
                                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        progress.setVisibility(View.GONE);
                                    }
                                }, 3000);
                                DeviceFragment.this.listener.onDeviceSelected(devicesInfoUtil);

                            }
                        }
            }
        });

        this.noWifilay = inflate.findViewById(R.id.wifi_dis_layout);
        this.list_lay = inflate.findViewById(R.id.smart_devices_layout);
        this.noTvlay = inflate.findViewById(R.id.no_Tv_layout);
        this.wifiSetting = (TextView) inflate.findViewById(R.id.btn_noWifi);
        this.wifiSetting.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                DeviceFragment.this.startActivity(new Intent("android.settings.WIFI_SETTINGS"));
            }
        });
        this.noTvState = (TextView) inflate.findViewById(R.id.noTv_status);

        return inflate;
    }


    @Override
    public void onDetach() {
        this.listener = null;
        super.onDetach();
    }

    @Override
    public void onPause() {
        try {
        this.mDiscoverer.stopDiscovery();
        getActivity().unregisterReceiver(this.broadcastReceiver);
        this.handler.removeCallbacks(this.errorTimer);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        this.startTime = System.currentTimeMillis();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.wifi.supplicant.CONNECTION_CHANGE");
        intentFilter.addAction("android.net.wifi.supplicant.STATE_CHANGE");
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        intentFilter.addAction("android.bluetooth.adapter.action.STATE_CHANGED");
        getActivity().registerReceiver(this.broadcastReceiver, intentFilter);
        dismissErrorScreen();
        restartSearching();
    }

    @Override
    public void onStart() {
        super.onStart();
        try {

        this.mDiscoverer = new Discoverer(getActivity());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onStop() {
        try {
        this.mDiscoverer.destroy();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        this.mDiscoverer = null;
        super.onStop();
    }



    public class HubDiscoveryListener extends Discoverer.DiscoveryListener {
        private HubDiscoveryListener() {
        }

        @Override
        public void onDeviceFound(DevicesInfoUtil devicesInfoUtil) {
            if (!DeviceFragment.this.devicesList.contains(devicesInfoUtil)) {
                DeviceFragment.this.hubAdapter.add(devicesInfoUtil);
                DeviceFragment.this.hubAdapter.sort(DeviceFragment.this.mComparator);
                DeviceFragment.this.hubAdapter.notifyDataSetChanged();
                DeviceFragment.this.updateError();
                if (DeviceFragment.this.listener != null) {
                    DeviceFragment.this.listener.onDevicesUpdated();
                }
            }
        }

        @Override
        public void onDeviceLost(DevicesInfoUtil devicesInfoUtil) {
            if (DeviceFragment.this.devicesList.remove(devicesInfoUtil)) {
                DeviceFragment.this.hubAdapter.notifyDataSetChanged();
                DeviceFragment.this.updateError();
                if (DeviceFragment.this.listener != null) {
                    DeviceFragment.this.listener.onDevicesUpdated();
                }
            }
        }

        @Override
        public void onDeviceReplace(DevicesInfoUtil devicesInfoUtil, DevicesInfoUtil devicesInfoUtil2) {
            if (DeviceFragment.this.devicesList.remove(devicesInfoUtil)) {
                DeviceFragment.this.hubAdapter.add(devicesInfoUtil2);
                DeviceFragment.this.hubAdapter.sort(DeviceFragment.this.mComparator);
                DeviceFragment.this.hubAdapter.notifyDataSetChanged();
                DeviceFragment.this.updateError();
                if (DeviceFragment.this.listener != null) {
                    DeviceFragment.this.listener.onDevicesUpdated();
                }
            }
        }

        @Override
        public void onDiscoveryStarted() {
        }

        @Override
        public void onDiscoveryStopped() {
        }

        @Override
        public void onStartDiscoveryFailed(int i) {
        }
    }


    public interface Listener {
        void onDeviceSelected(DevicesInfoUtil devicesInfoUtil);

        void onDevicesUpdated();

        void onNoConnectivity();

        void onNoDevices();
    }

    private void dismissErrorScreen() {

        this.list_lay.setVisibility(View.VISIBLE);
        this.devices_list.setVisibility(View.VISIBLE);
        this.noTvlay.setVisibility(View.GONE);
        this.noWifilay.setVisibility(View.GONE);
    }

    private static String removeDoubleQuotes(String str) {
        if (str == null) {
            return null;
        }
        int length = str.length();
        return (length > 1 && str.charAt(0) == '\"' && str.charAt(length + -1) == '\"') ? str.substring(1, length - 1) : str;
    }

    public void showNoDeviceLay(Context context) {
        String string;
        String str2;
        WifiInfo connectionInfo;
        String str3 = null;
        if (context != null) {

            this.list_lay.setVisibility(View.GONE);
            this.devices_list.setVisibility(View.GONE);
            this.noTvlay.setVisibility(View.VISIBLE);
            this.noWifilay.setVisibility(View.GONE);
            if (NetworkUtil.isConnectedToNetwork(context)) {
                if (!NetworkUtil.isConnectedToWifi(context)) {
                    if (NetworkUtil.isConnectedToEthernet(context)) {
                        str3 = context.getResources().getString(R.string.search_internet);
                    }
                } else if (!(this.wifiManager == null || (connectionInfo = this.wifiManager.getConnectionInfo()) == null)) {
                    str3 = removeDoubleQuotes(connectionInfo.getSSID());
                }
                if (TextUtils.isEmpty(str3)) {
                    str3 = context.getResources().getString(R.string.search_unknown);
                }
                if (!NetworkUtil.isBluetoothAvailable(context)) {
                    string = context.getResources().getString(R.string.search_network) + " " + str3;
                } else {
                    string = context.getResources().getString(R.string.search_network) + " " + str3 + " " +
                            context.getResources().getString(R.string.search_Both);
                }
            } else if (!NetworkUtil.isBluetoothAvailable(context)) {
                showNoWifiLay(context);
                return;
            } else {
                string = context.getResources().getString(R.string.search_bluetooth);
            }
            this.noTvState.setText(string);
        }
    }


    public void showNoWifiLay(Context context) {
        if (context != null) {

            this.list_lay.setVisibility(View.GONE);
            this.devices_list.setVisibility(View.GONE);
            this.noTvlay.setVisibility(View.GONE);
            this.noWifilay.setVisibility(View.VISIBLE);

            if (NetworkUtil.isBluetoothPossible(context) && !NetworkUtil.isBluetoothAvailable(context)) {

            }
            int i = R.string.enable;
            if (this.wifiManager != null && this.wifiManager.isWifiEnabled()) {
                i = R.string.con_wf;
            }
            this.wifiSetting.setText(context.getResources().getString(i));
        }
    }

    private void updateError() {
        FragmentActivity activity = getActivity();
        if (activity != null) {
            if (!NetworkUtil.canDiscover(activity)) {
                showNoWifiLay(activity);
                if (this.listener != null) {
                    this.listener.onNoConnectivity();
                }
            } else if (this.hubAdapter != null && this.hubAdapter.getCount() > 0) {
                dismissErrorScreen();
            } else {
                showNoDeviceLay(activity);
                if (this.listener != null) {
                    this.listener.onNoDevices();
                }
            }
        }
    }

    public void restartSearching() {

        try {
            DeviceFragment.this.devicesList.clear();
            DeviceFragment.this.hubAdapter.notifyDataSetChanged();
            if (DeviceFragment.this.listener != null) {
                DeviceFragment.this.listener.onDevicesUpdated();
            }
            DeviceFragment.this.mDiscoverer.stopDiscovery();
            DeviceFragment.this.mDiscoverer.startDiscovery(DeviceFragment.this.mDiscoveryListener, DeviceFragment.this.handler);
            DeviceFragment.this.handler.removeCallbacks(DeviceFragment.this.errorTimer);
            DeviceFragment.this.handler.postDelayed(DeviceFragment.this.errorTimer, ERROR_DELAY);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }
}