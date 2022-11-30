package com.remote.control.allsmarttv.Activities;


import android.app.Dialog;
import android.content.Intent;
import android.hardware.ConsumerIrManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.ads.AdError;
import com.remote.control.allsmarttv.Activities.ir.RemoteNameActivity;
import com.remote.control.allsmarttv.R;
import com.remote.control.allsmarttv.Utils.ir_utils.SupportedClass;
import com.remote.control.allsmarttv.adManager.AdManager;
import com.remote.control.allsmarttv.databinding.ActivityMainBinding;

public class FirstActivity extends AppCompatActivity implements AdManager.CallBackInterstitial {

    private int isBtnClicked = 0;
    ActionBarDrawerToggle drawerToggle;
    ActivityMainBinding activityMainBinding;
    RelativeLayout button_privacy, button_rate, button_more, button_share, button_feedback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SupportedClass.loadLangLocale(getBaseContext());
        activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(activityMainBinding.getRoot());

        AdManager.loadInterstitialAd();

        button_privacy = findViewById(R.id.btn_privacy);
        button_rate = findViewById(R.id.btn_rate);
        button_share = findViewById(R.id.btn_share);
        button_feedback = findViewById(R.id.btn_feed);
        button_more = findViewById(R.id.btn_more);

        button_privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    Uri uri = Uri.parse("https://docs.google.com/document/d/1bYIMnAVAwPrN0vGs9pZ8mSYP6MWgb-zhH7uQpLgyS8U/edit");
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(uri);
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        button_rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SupportedClass.rateApp(FirstActivity.this);

            }
        });
        activityMainBinding.language.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    SupportedClass.langDialog(FirstActivity.this, FirstActivity.this);
                } catch (Exception ex) {
                    Log.i("exception is::", ex.toString());
                }
            }
        });

        button_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    //replace &quot;Unified+Apps&quot; with your developer name
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=pub:SerpSkills")));
                } catch (android.content.ActivityNotFoundException anfe) {
                    //replace &quot;Unified+Apps&quot; with your developer name
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/developer?id=SerpSkills")));
                }

            }
        });

        button_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SupportedClass.shareApp(FirstActivity.this);

            }
        });

        button_feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SupportedClass.feedback(FirstActivity.this);

            }
        });


        drawerToggle = new ActionBarDrawerToggle(this, activityMainBinding.drawerLayout, activityMainBinding.toolbarMain, R.string.app_name, R.string.app_name) {
            public void onDrawerClosed(View view) {
                supportInvalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                supportInvalidateOptionsMenu();
            }
        };

        drawerToggle.setDrawerIndicatorEnabled(true);
        activityMainBinding.drawerLayout.setDrawerListener(drawerToggle);
        activityMainBinding.toolbarMain.setNavigationIcon(R.drawable.draw_icon);

        activityMainBinding.samsungTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SupportedClass.checkConnection(FirstActivity.this)) {

                    isBtnClicked = 0;

                    if (AdManager.isInterstialLoaded()) {
                        AdManager.showInterstitial(FirstActivity.this, FirstActivity.this);
                    } else {
                        moveToRequireActivity();
                    }

                } else {
                    Toast.makeText(FirstActivity.this, R.string.check_internet, Toast.LENGTH_SHORT).show();
                }
            }
        });

        activityMainBinding.sony.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SupportedClass.checkConnection(FirstActivity.this)) {

                    isBtnClicked = 1;

                    if (AdManager.isInterstialLoaded()) {
                        AdManager.showInterstitial(FirstActivity.this, FirstActivity.this);
                    } else {
                        moveToRequireActivity();
                    }

                } else {
                    Toast.makeText(FirstActivity.this, R.string.check_internet, Toast.LENGTH_SHORT).show();
                }
            }
        });

        activityMainBinding.addRemote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SupportedClass.checkConnection(FirstActivity.this)) {
                    isBtnClicked = 2;
                    if (AdManager.isInterstialLoaded()) {
                        AdManager.showInterstitial(FirstActivity.this, FirstActivity.this);
                    } else {
                        moveToRequireActivity();
                    }
                } else {
                    Toast.makeText(FirstActivity.this, R.string.check_internet, Toast.LENGTH_SHORT).show();
                }
            }
        });

        activityMainBinding.androidTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SupportedClass.checkConnection(FirstActivity.this)) {
                    isBtnClicked = 3;
                    if (AdManager.isInterstialLoaded()) {
                        AdManager.showInterstitial(FirstActivity.this, FirstActivity.this);
                    } else {
                        moveToRequireActivity();
                    }
                } else {
                    Toast.makeText(FirstActivity.this, R.string.check_internet, Toast.LENGTH_SHORT).show();
                }
            }
        });

        activityMainBinding.rokuTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SupportedClass.checkConnection(FirstActivity.this)) {
                    isBtnClicked = 4;
                    if (AdManager.isInterstialLoaded()) {
                        AdManager.showInterstitial(FirstActivity.this, FirstActivity.this);
                    } else {
                        moveToRequireActivity();
                    }
                } else {
                    Toast.makeText(FirstActivity.this, R.string.check_internet, Toast.LENGTH_SHORT).show();
                }
            }
        });

        activityMainBinding.lgTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SupportedClass.checkConnection(FirstActivity.this)) {
                    isBtnClicked = 5;
                    if (AdManager.isInterstialLoaded()) {
                        AdManager.showInterstitial(FirstActivity.this, FirstActivity.this);
                    } else {
                        moveToRequireActivity();
                    }
                } else {
                    Toast.makeText(FirstActivity.this, R.string.check_internet, Toast.LENGTH_SHORT).show();
                }
            }
        });

        activityMainBinding.irRemote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SupportedClass.checkConnection(FirstActivity.this)) {
                    isBtnClicked = 6;
                    if (AdManager.isInterstialLoaded()) {
                        AdManager.showInterstitial(FirstActivity.this, FirstActivity.this);
                    } else {
                        moveToRequireActivity();
                    }
                } else {
                    Toast.makeText(FirstActivity.this, R.string.check_internet, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }

    private void moveToRequireActivity() {
        if (isBtnClicked == 0) {
            startActivity(new Intent(FirstActivity.this, LgWifiRemoteActivity.class)
                    .putExtra("tv", "sam"));
        }
        if (isBtnClicked == 1) {
            startActivity(new Intent(FirstActivity.this, LgWifiRemoteActivity.class)
                    .putExtra("tv", "sony"));
        }
        if (isBtnClicked == 2) {
            startActivity(new Intent(FirstActivity.this, Tv_list.class));
        }
        if (isBtnClicked == 3) {
            startActivity(new Intent(FirstActivity.this, RemoteActivity.class));
        }
        if (isBtnClicked == 4) {
            startActivity(new Intent(FirstActivity.this, RokuPair.class));
        }
        if (isBtnClicked == 5) {
            startActivity(new Intent(FirstActivity.this, LgWifiRemoteActivity.class)
                    .putExtra("tv", "lg"));
        }
        if (isBtnClicked == 6) {
            try {
                ConsumerIrManager irManager = (ConsumerIrManager) getSystemService(CONSUMER_IR_SERVICE);
                if (irManager != null) {
                    if (irManager.hasIrEmitter()) {
                        startActivity(new Intent(FirstActivity.this, RemoteNameActivity.class));
                    } else {
                        showIrDialog();
                        //Log.i("IR_Testing", "Cannot found IR Emitter on the device");
                    }
                } else {
                    Toast.makeText(this, "Cannot found IR Emitter on the device", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void showIrDialog() {
        final Dialog irDialog = new Dialog(this);
        irDialog.setContentView(R.layout.ir_dialog);
        irDialog.setCancelable(false);
        irDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        Button goBack = irDialog.findViewById(R.id.go_back);
        goBack.setOnClickListener(v -> {
            if (irDialog.isShowing() && !isFinishing()) {
                irDialog.dismiss();
            }
        });

        irDialog.show();
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