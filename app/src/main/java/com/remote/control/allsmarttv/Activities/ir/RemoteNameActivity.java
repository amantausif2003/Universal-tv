package com.remote.control.allsmarttv.Activities.ir;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdError;
import com.remote.control.allsmarttv.R;
import com.remote.control.allsmarttv.utils.ir_utils.SupportedClass;
import com.remote.control.allsmarttv.adManager.AdManager;
import com.remote.control.allsmarttv.adapter.RemoteNameAdapter;
import com.remote.control.allsmarttv.irtv.SamsungRemoteActivity;
import com.remote.control.allsmarttv.models.RemoteNameModel;

import java.util.ArrayList;

public class RemoteNameActivity extends AppCompatActivity implements RemoteNameAdapter.RemoteClickListener, View.OnClickListener, AdManager.CallBackInterstitial {

    private int isBtnClicked = 0;
    private ArrayList<RemoteNameModel> remoteNameModelArrayList = new ArrayList();
    private RemoteNameAdapter remoteNameAdapter;
    private RecyclerView remoteNameRecycler;
    private ImageView backImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SupportedClass.loadLangLocale(getBaseContext());
        setContentView(R.layout.activity_remote_name);
        if (this.getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        remoteNameRecycler = findViewById(R.id.remote_name_recycler);
        backImg = findViewById(R.id.back);
        backImg.setOnClickListener(this);
        addItemRemoteList();
    }

    private void addItemRemoteList() {
        RemoteNameModel nameModel1 = new RemoteNameModel();
        nameModel1.setRemoteName(getResources().getString(R.string.samsung));

        RemoteNameModel nameModel2 = new RemoteNameModel();
        nameModel2.setRemoteName(getResources().getString(R.string.panasonic));

        RemoteNameModel nameModel3 = new RemoteNameModel();
        nameModel3.setRemoteName(getResources().getString(R.string.sony));

        RemoteNameModel nameModel4 = new RemoteNameModel();
        nameModel4.setRemoteName(getResources().getString(R.string.tcl));

        RemoteNameModel nameModel5 = new RemoteNameModel();
        nameModel5.setRemoteName(getResources().getString(R.string.jvc));

        RemoteNameModel nameModel6 = new RemoteNameModel();
        nameModel6.setRemoteName(getResources().getString(R.string.toshiba));

        RemoteNameModel nameModel7 = new RemoteNameModel();
        nameModel7.setRemoteName(getResources().getString(R.string.hitachi));

        RemoteNameModel nameModel8 = new RemoteNameModel();
        nameModel8.setRemoteName(getResources().getString(R.string.zenith));
        RemoteNameModel nameModel9 = new RemoteNameModel();
        nameModel9.setRemoteName(getResources().getString(R.string.haire));

        RemoteNameModel nameModel10 = new RemoteNameModel();
        nameModel10.setRemoteName(getResources().getString(R.string.lg));

        RemoteNameModel nameModel11 = new RemoteNameModel();
        nameModel11.setRemoteName(getResources().getString(R.string.philips));

        RemoteNameModel nameModel12 = new RemoteNameModel();
        nameModel12.setRemoteName(getResources().getString(R.string.mitsubishi));

        RemoteNameModel nameModel13 = new RemoteNameModel();
        nameModel13.setRemoteName(getResources().getString(R.string.insignia));

        remoteNameModelArrayList.add(0, nameModel1);
        remoteNameModelArrayList.add(1, nameModel2);
        remoteNameModelArrayList.add(2, nameModel3);
        remoteNameModelArrayList.add(3, nameModel4);
        remoteNameModelArrayList.add(4, nameModel5);
        remoteNameModelArrayList.add(5, nameModel6);
        remoteNameModelArrayList.add(6, nameModel7);
        remoteNameModelArrayList.add(7, nameModel8);
        remoteNameModelArrayList.add(8, nameModel9);
        remoteNameModelArrayList.add(9, nameModel10);
        remoteNameModelArrayList.add(10, nameModel11);
        remoteNameModelArrayList.add(11, nameModel12);
        remoteNameModelArrayList.add(12, nameModel13);
        setRemoteNameAdapter();

    }

    void setRemoteNameAdapter() {
        remoteNameAdapter = new RemoteNameAdapter(remoteNameModelArrayList, this);
        remoteNameRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        remoteNameRecycler.setAdapter(remoteNameAdapter);
    }

    private void moveToRequireActivity() {
        if (isBtnClicked == 0) {
            startActivity(new Intent(getApplicationContext(), SamsungRemoteActivity.class));
        }
        if (isBtnClicked == 1) {
            startActivity(new Intent(getApplicationContext(), PanasonicRemoteActivity.class));
        }
        if (isBtnClicked == 2) {
            startActivity(new Intent(getApplicationContext(), SonyRemote.class));
        }
        if (isBtnClicked == 3) {
            startActivity(new Intent(getApplicationContext(), RokuRemote.class));
        }
        if (isBtnClicked == 4) {
            startActivity(new Intent(getApplicationContext(), JVCRemoteActivity.class));
        }
        if (isBtnClicked == 5) {
            startActivity(new Intent(getApplicationContext(), ToshibaRemoteActivity.class));
        }
        if (isBtnClicked == 6) {
            startActivity(new Intent(getApplicationContext(), HitachiRemoteActivity.class));
        }
        if (isBtnClicked == 7) {
            startActivity(new Intent(getApplicationContext(), ZenithActivity.class));
        }
        if (isBtnClicked == 8) {
            startActivity(new Intent(getApplicationContext(), HaireRemoteActivity.class));
        }
        if (isBtnClicked == 9) {
            startActivity(new Intent(getApplicationContext(), LGIRRemoteActivity.class));
        }
        if (isBtnClicked == 10) {
            startActivity(new Intent(getApplicationContext(), PhilipsActivity.class));
        }
        if (isBtnClicked == 11) {
            startActivity(new Intent(getApplicationContext(), MitsubishiRemoteActivity.class));
        }
        if (isBtnClicked == 12) {
            startActivity(new Intent(getApplicationContext(), InsigniaRemoteActivity.class));
        }
    }

    @Override
    public void remoteItemClick(RemoteNameModel designModel, int position) {
        if (SupportedClass.checkConnection(this)) {
            if (position == 0) {
                isBtnClicked = 0;
                if (AdManager.isInterstialLoaded()) {
                    AdManager.showInterstitial(RemoteNameActivity.this, RemoteNameActivity.this);
                } else {
                    moveToRequireActivity();
                }
            }
            if (position == 1) {
                isBtnClicked = 1;
                if (AdManager.isInterstialLoaded()) {
                    AdManager.showInterstitial(RemoteNameActivity.this, RemoteNameActivity.this);
                } else {
                    moveToRequireActivity();
                }
            }
            if (position == 2) {
                isBtnClicked = 2;
                if (AdManager.isInterstialLoaded()) {
                    AdManager.showInterstitial(RemoteNameActivity.this, RemoteNameActivity.this);
                } else {
                    moveToRequireActivity();
                }
            }
            if (position == 3) {
                isBtnClicked = 3;
                if (AdManager.isInterstialLoaded()) {
                    AdManager.showInterstitial(RemoteNameActivity.this, RemoteNameActivity.this);
                } else {
                    moveToRequireActivity();
                }
            }
            if (position == 4) {
                isBtnClicked = 4;
                if (AdManager.isInterstialLoaded()) {
                    AdManager.showInterstitial(RemoteNameActivity.this, RemoteNameActivity.this);
                } else {
                    moveToRequireActivity();
                }
            }
            if (position == 5) {
                isBtnClicked = 5;
                if (AdManager.isInterstialLoaded()) {
                    AdManager.showInterstitial(RemoteNameActivity.this, RemoteNameActivity.this);
                } else {
                    moveToRequireActivity();
                }
            }
            if (position == 6) {
                isBtnClicked = 6;
                if (AdManager.isInterstialLoaded()) {
                    AdManager.showInterstitial(RemoteNameActivity.this, RemoteNameActivity.this);
                } else {
                    moveToRequireActivity();
                }
            }
            if (position == 7) {
                isBtnClicked = 7;
                if (AdManager.isInterstialLoaded()) {
                    AdManager.showInterstitial(RemoteNameActivity.this, RemoteNameActivity.this);
                } else {
                    moveToRequireActivity();
                }
            }
            if (position == 8) {
                isBtnClicked = 8;
                if (AdManager.isInterstialLoaded()) {
                    AdManager.showInterstitial(RemoteNameActivity.this, RemoteNameActivity.this);
                } else {
                    moveToRequireActivity();
                }
            }
            if (position == 9) {
                isBtnClicked = 9;
                if (AdManager.isInterstialLoaded()) {
                    AdManager.showInterstitial(RemoteNameActivity.this, RemoteNameActivity.this);
                } else {
                    moveToRequireActivity();
                }
            }
            if (position == 10) {
                isBtnClicked = 10;
                if (AdManager.isInterstialLoaded()) {
                    AdManager.showInterstitial(RemoteNameActivity.this, RemoteNameActivity.this);
                } else {
                    moveToRequireActivity();
                }
            }
            if (position == 11) {
                isBtnClicked = 11;
                if (AdManager.isInterstialLoaded()) {
                    AdManager.showInterstitial(RemoteNameActivity.this, RemoteNameActivity.this);
                } else {
                    moveToRequireActivity();
                }
            }
            if (position == 12) {
                isBtnClicked = 12;
                if (AdManager.isInterstialLoaded()) {
                    AdManager.showInterstitial(RemoteNameActivity.this, RemoteNameActivity.this);
                } else {
                    moveToRequireActivity();
                }
            }
        } else {
            Toast.makeText(this, R.string.check_internet, Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onClick(View v) {
        if (R.id.back == v.getId()) {
            finish();
        }
    }

    @Override
    public void interstitialDismissedFullScreenContent() {
        moveToRequireActivity();
    }

    @Override
    public void interstitialFailedToShowFullScreenContent(@Nullable AdError adError) {
        moveToRequireActivity();
    }

    @Override
    public void interstitialShowedFullScreenContent() {

    }
}