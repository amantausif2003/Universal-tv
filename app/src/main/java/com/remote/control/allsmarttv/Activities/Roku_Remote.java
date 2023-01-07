package com.remote.control.allsmarttv.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.jaku.core.JakuRequest;
import com.jaku.core.KeypressKeyValues;
import com.jaku.request.KeypressRequest;
import com.remote.control.allsmarttv.R;
import com.remote.control.allsmarttv.utils.KeyboardUtil;
import com.remote.control.allsmarttv.utils.RokuCmds;
import com.remote.control.allsmarttv.utils.RokureqType;
import com.remote.control.allsmarttv.utils.RxRokuTask;
import com.remote.control.allsmarttv.utils.ir_utils.SupportedClass;
import com.remote.control.allsmarttv.utils.roku_touchpad;
import com.remote.control.allsmarttv.databinding.RokuActivityBinding;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


public class Roku_Remote extends AppCompatActivity {

    RokuActivityBinding activityBinding;
    Vibrator vibrator;
    int toogle = 0;
    private roku_touchpad touchpad;


    private roku_touchpad.Listener trackpadListener = new roku_touchpad.Listener() {

        @Override
        public void onLongPressCenterDown() {

            performKey(KeypressKeyValues.SELECT);
        }

        @Override
        public void onLongPressCenterUp() {

            performKey(KeypressKeyValues.SELECT);

        }

        @Override
        public void onTrackpadEvent() {

        }

        @Override
        public void performKey(KeypressKeyValues keypressKeyValues) {

            vibrator.vibrate(50);

            String url = RokuCmds.getTv();

            KeypressRequest keypressRequest = new KeypressRequest(url, keypressKeyValues.getValue());
            JakuRequest request = new JakuRequest(keypressRequest, null);

            performReq(request, RokureqType.keypress);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SupportedClass.loadLangLocale(getBaseContext());
        activityBinding = RokuActivityBinding.inflate(getLayoutInflater());
        setContentView(activityBinding.getRoot());

        this.touchpad = (roku_touchpad) findViewById(R.id.roku_touch_pad);
        this.touchpad.setListener(this.trackpadListener);

        vibrator = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);

        activityBinding.rokuTrackpad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(toogle == 0)
                {
                    touchpad.setVisibility(View.VISIBLE);
                    activityBinding.layout3.setVisibility(View.INVISIBLE);
                    toogle = 1;
                }
                else
                {
                    touchpad.setVisibility(View.INVISIBLE);
                    activityBinding.layout3.setVisibility(View.VISIBLE);
                    toogle = 0;
                }
            }
        });

        activityBinding.rokuBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                performAct(KeypressKeyValues.BACK);
            }
        });

        activityBinding.rokuKeyboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentManager fragmentManager = getSupportFragmentManager();

                KeyboardUtil fragment = new KeyboardUtil();
                fragment.show(fragmentManager, KeyboardUtil.class.getName());

            }
        });

        activityBinding.rokuRemoteBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                        Intent intent = new Intent(Roku_Remote.this, FirstActivity.class);
                        startActivity(intent);
                        finish();

            }
        });

        activityBinding.rokuHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                performAct(KeypressKeyValues.HOME);
            }
        });

        activityBinding.rokuUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                performAct(KeypressKeyValues.UP);
            }
        });


        activityBinding.rokuDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                performAct(KeypressKeyValues.DOWN);
            }
        });



        activityBinding.rokuRewind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                performAct(KeypressKeyValues.REV);
            }
        });

        activityBinding.rokuForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                performAct(KeypressKeyValues.FWD);
            }
        });

        activityBinding.rokuRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                performAct(KeypressKeyValues.RIGHT);
            }
        });

        activityBinding.rokuLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                performAct(KeypressKeyValues.LEFT);
            }
        });

        activityBinding.rokuOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                performAct(KeypressKeyValues.SELECT);
            }
        });

        activityBinding.rokuFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                activityBinding.ringTextview.setVisibility(View.VISIBLE);
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        activityBinding.ringTextview.setVisibility(View.GONE);
                    }
                },15000);

                performAct(KeypressKeyValues.FIND_REMOTE);
            }
        });

        activityBinding.rokuOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                performAct(KeypressKeyValues.INFO);
            }
        });

        activityBinding.rokuOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                performAct(KeypressKeyValues.POWER_OFF);
            }
        });

        activityBinding.rokuPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                performAct(KeypressKeyValues.PLAY);
            }
        });

        activityBinding.rokuApps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                        Intent intent = new Intent(Roku_Remote.this, Roku_Apps.class);
                        startActivity(intent);
                        finish();

            }
        });


    }

    private void performAct(KeypressKeyValues keypressKeyValue) {

        vibrator.vibrate(50);

        String url = RokuCmds.getTv();

        KeypressRequest keypressRequest = new KeypressRequest(url, keypressKeyValue.getValue());
        JakuRequest request = new JakuRequest(keypressRequest, null);

        performReq(request, RokureqType.keypress);
    }

    private void performReq(final JakuRequest request, final RokureqType rokuRequestType) {
        Observable.fromCallable(new RxRokuTask(this.getApplicationContext(), request, rokuRequestType))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> { });
    }

    @Override
    public void onBackPressed() {

                Intent intent = new Intent(Roku_Remote.this, FirstActivity.class);
                startActivity(intent);
                finish();
    }
}
