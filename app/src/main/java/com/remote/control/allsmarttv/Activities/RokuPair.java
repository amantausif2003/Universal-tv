package com.remote.control.allsmarttv.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.remote.control.allsmarttv.R;
import com.remote.control.allsmarttv.Utils.AvailTvs;
import com.remote.control.allsmarttv.Utils.Device_rokuTv;
import com.remote.control.allsmarttv.Utils.ir_utils.SupportedClass;
import com.remote.control.allsmarttv.databinding.RokuPairingActivityBinding;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class RokuPair extends AppCompatActivity {

     RokuPairingActivityBinding pairingActivityBinding;

    boolean isWiFiAvailable;
    Handler handler;
    public static Device_rokuTv deviceRoku;
    CompositeDisposable disposable = new CompositeDisposable();
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SupportedClass.loadLangLocale(getBaseContext());
        pairingActivityBinding = RokuPairingActivityBinding.inflate(getLayoutInflater());
        setContentView(pairingActivityBinding.getRoot());

        handler = new Handler();

        pairingActivityBinding.rokuPairBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                        Intent intent = new Intent(RokuPair.this, FirstActivity.class);
                        startActivity(intent);
                        finish();

            }
        });

        pairingActivityBinding.restartSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                        setListShown(false);
                        loadAvailableTv();
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
            startActivity(new Intent(RokuPair.this, Roku_Remote.class));
            finish();
        }
    }

    public void setListShown(boolean shown) {
        if (shown) {
            pairingActivityBinding.rokuProgressBar.setVisibility(View.GONE);
            pairingActivityBinding.loadingLayout.setVisibility(View.GONE);
            pairingActivityBinding.rokuInformation.setVisibility(View.GONE);
        } else {
            pairingActivityBinding.rokuProgressBar.setVisibility(View.VISIBLE);
            pairingActivityBinding.loadingLayout.setVisibility(View.VISIBLE);
            pairingActivityBinding.rokuInformation.setVisibility(View.VISIBLE);
        }
    }

    private boolean containDevice(Device_rokuTv device) {
        boolean found = false;

        for (int i = 0; i < pairingActivityBinding.rokuTvsList.getChildCount(); i++) {
            Device_rokuTv existingDevice = (Device_rokuTv) pairingActivityBinding.rokuTvsList.getChildAt(i).getTag();

            if (device.getSerialNumber().equals(existingDevice.getSerialNumber())) {
                found = true;
                break;
            }
        }

        return found;
    }



    private String getWifiName(Context context) {
        String networkName = "";

        final ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager == null) {
            return networkName;
        }

        final NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();

        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        if(activeNetwork != null) {

            isWiFiAvailable = activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;
        }
        else {
            try {

                Toast.makeText(this, "Connect to a wifi", Toast.LENGTH_SHORT).show();

                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (isConnected && isWiFiAvailable) {
            final WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            final WifiInfo connectionInfo = wifiManager.getConnectionInfo();

            networkName = connectionInfo.getSSID();
        }

        return networkName;
    }

    private void loadAvailableTv() {
        disposable.add(Observable.fromCallable(new AvailTvs(this))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(devices -> onLoadFinished((List<Device_rokuTv>) devices)));
    }

    private void onLoadFinished(List<Device_rokuTv> devices) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                setListShown(false);
                loadAvailableTv();
            }
        }, 5000);

        if (devices.size() == 0) {
            setListShown(true);
            return;
        }

        for (int i = 0; i < devices.size(); i++) {
            if (!containDevice(devices.get(i))) {
                LinearLayout view = (LinearLayout) this.getLayoutInflater().inflate(R.layout.roku_item, null, false);

                textView = view.findViewById(R.id.roku_name);
                TextView text2 = view.findViewById(R.id.roku_state);

                String deviceName = devices.get(i).getModelName();
                String friendlyName = devices.get(i).getUserDeviceName();

                if (friendlyName != null && !friendlyName.isEmpty()) {
                    deviceName = friendlyName + " (" + deviceName + ")";
                }

                textView.setText(deviceName);
                text2.setText("SN: " + devices.get(i).getSerialNumber());

                view.setTag(devices.get(i));
                view.setOnClickListener(mClickListener);

                pairingActivityBinding.rokuTvsList.addView(view);
            }
        }

        setListShown(true);

    }

    final private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

                    deviceRoku = (Device_rokuTv) v.getTag();

                    Toast.makeText(RokuPair.this, "Connected to " + textView.getText().toString(), Toast.LENGTH_SHORT).show();

                    startActivity(new Intent(RokuPair.this, Roku_Remote.class));
                    finish();

        }
    };

    @Override
    public void onResume() {
        super.onResume();

        pairingActivityBinding.rokuDevice.setText(getWifiName(this));

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                setListShown(false);
                loadAvailableTv();
            }
        }, 1000);
    }

    @Override
    public void onPause() {
        super.onPause();

        handler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disposable.dispose();
    }

    @Override
    public void onBackPressed() {

                Intent intent = new Intent(RokuPair.this, FirstActivity.class);
                startActivity(intent);
                finish();


    }
}
