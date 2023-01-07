package com.remote.control.allsmarttv.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.connectsdk.device.ConnectableDevice;
import com.connectsdk.discovery.DiscoveryManager;
import com.connectsdk.discovery.DiscoveryManagerListener;
import com.connectsdk.service.capability.KeyControl;
import com.connectsdk.service.capability.KeyControl.KeyCode;
import com.connectsdk.service.capability.Launcher;
import com.connectsdk.service.capability.MediaControl;
import com.connectsdk.service.capability.PowerControl;
import com.connectsdk.service.capability.TVControl;
import com.connectsdk.service.capability.VolumeControl;
import com.connectsdk.service.command.ServiceCommandError;
import com.connectsdk.service.sessions.LaunchSession;
import com.remote.control.allsmarttv.R;
import com.remote.control.allsmarttv.utils.TestResponseClass;
import com.remote.control.allsmarttv.utils.ir_utils.SupportedClass;
import com.remote.control.allsmarttv.utils.lg_touchpad;
import com.remote.control.allsmarttv.utils.sonyPicker;
import com.remote.control.allsmarttv.databinding.ActivityLgWifiRemoteBinding;

public class LgWifiRemoteActivity extends AppCompatActivity implements DiscoveryManagerListener {

    ActivityLgWifiRemoteBinding activityLgWifiRemoteBinding;
    private Vibrator vibrator;
    ConnectableDevice connectableDevice;
    TestResponseClass testResponse;
    DiscoveryManager discoveryManager;
    PowerControl powerControl;
    MediaControl mediaControl;
    VolumeControl volumeControl;
    String text = "LG Devices";
    AlertDialog dialog;
    sonyPicker sonyPicker;
    LaunchSession runningAppSession;
    KeyControl keyControl;
    TVControl tvControl;
    Launcher launcher;
    private int mute_switch = 0, pad_switch = 0, play_switch = 0, toogle = 0;

    private lg_touchpad lg_touchpad;
    private lg_touchpad.Listener trackpadListener = new lg_touchpad.Listener() {
        @Override
        public void onLongPressCenterDown() {
            doKey(KeyCode.ENTER);
        }

        @Override
        public void onLongPressCenterUp() {
            doKey(KeyCode.ENTER);
        }

        @Override
        public void onTrackpadEvent() {

        }

        @Override
        public void doKey(KeyControl.KeyCode keyCode) {
            vibrator.vibrate(50);

            if (getKeyControl() != null) {
                getKeyControl().sendKeyCode(keyCode, null);
            }
        }

        @Override
        public void doKeyLeft() {

            vibrator.vibrate(50);
            if (getKeyControl() != null) {
                getKeyControl().left(null);
                testResponse = new TestResponseClass(true, TestResponseClass.SuccessCode, TestResponseClass.LeftClicked);
            }
        }

        @Override
        public void doKeyRight() {

            vibrator.vibrate(50);
            if (getKeyControl() != null) {
                getKeyControl().right(null);
                testResponse = new TestResponseClass(true, TestResponseClass.SuccessCode, TestResponseClass.RightClicked);
            }
        }

        @Override
        public void doKeyUp() {

            vibrator.vibrate(50);
            if (getKeyControl() != null) {
                getKeyControl().up(null);
                testResponse = new TestResponseClass(true, TestResponseClass.SuccessCode, TestResponseClass.UpClicked);
            }
        }

        @Override
        public void doKeyDown() {

            vibrator.vibrate(50);
            if (getKeyControl() != null) {
                getKeyControl().down(null);
                testResponse = new TestResponseClass(true, TestResponseClass.SuccessCode, TestResponseClass.DownClicked);
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SupportedClass.loadLangLocale(getBaseContext());
        try {
            activityLgWifiRemoteBinding = ActivityLgWifiRemoteBinding.inflate(getLayoutInflater());
            setContentView(activityLgWifiRemoteBinding.getRoot());

            if (getIntent().getExtras() != null) {
                if (getIntent().getStringExtra("tv").equals("sony")) {
                    text = "Sony Devices";
                } else if (getIntent().getStringExtra("tv").equals("lg")) {
                    text = "LG Devices";
                } else if (getIntent().getStringExtra("tv").equals("sam")) {
                    text = "Samsung Devices";
                }
            }

            vibrator = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);

            this.lg_touchpad = (lg_touchpad) findViewById(R.id.lg_touchpad);
            this.lg_touchpad.setListener(this.trackpadListener);

            DiscoveryManager.init(this);

            showDialog(text);

            discoveryManager = DiscoveryManager.getInstance();
            discoveryManager.registerDefaultDeviceTypes();
            discoveryManager.addListener(this);
            DiscoveryManager.getInstance().start();

            init();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private void init() {

        activityLgWifiRemoteBinding.buttonDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(LgWifiRemoteActivity.this, FirstActivity.class);
                startActivity(intent);
                finish();
            }
        });

        activityLgWifiRemoteBinding.buttonNumpad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (toogle == 0) {
                    vibrator.vibrate(50);

                    lg_touchpad.setVisibility(View.INVISIBLE);
                    lg_touchpad.reset();
                    activityLgWifiRemoteBinding.volumeLay.setVisibility(View.VISIBLE);
                    activityLgWifiRemoteBinding.channelLay.setVisibility(View.VISIBLE);

                    activityLgWifiRemoteBinding.cursorLay.setVisibility(View.INVISIBLE);
                    activityLgWifiRemoteBinding.numberLay.setVisibility(View.VISIBLE);

                    pad_switch = 0;

                    toogle = 1;
                } else {
                    vibrator.vibrate(50);


                    lg_touchpad.setVisibility(View.INVISIBLE);
                    lg_touchpad.reset();
                    activityLgWifiRemoteBinding.volumeLay.setVisibility(View.VISIBLE);
                    activityLgWifiRemoteBinding.channelLay.setVisibility(View.VISIBLE);

                    pad_switch = 0;

                    toogle = 0;

                    activityLgWifiRemoteBinding.cursorLay.setVisibility(View.VISIBLE);
                    activityLgWifiRemoteBinding.numberLay.setVisibility(View.INVISIBLE);

                }
            }
        });

        activityLgWifiRemoteBinding.buttonTouchpad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (pad_switch == 0) {

                    vibrator.vibrate(50);

                    activityLgWifiRemoteBinding.volumeLay.setVisibility(View.INVISIBLE);
                    activityLgWifiRemoteBinding.channelLay.setVisibility(View.INVISIBLE);
                    activityLgWifiRemoteBinding.cursorLay.setVisibility(View.INVISIBLE);
                    activityLgWifiRemoteBinding.numberLay.setVisibility(View.INVISIBLE);

                    lg_touchpad.setVisibility(View.VISIBLE);
                    lg_touchpad.reset();

                    pad_switch = 1;
                } else {
                    vibrator.vibrate(50);

                    lg_touchpad.reset();
                    activityLgWifiRemoteBinding.volumeLay.setVisibility(View.VISIBLE);
                    activityLgWifiRemoteBinding.channelLay.setVisibility(View.VISIBLE);
                    activityLgWifiRemoteBinding.cursorLay.setVisibility(View.VISIBLE);
                    activityLgWifiRemoteBinding.numberLay.setVisibility(View.INVISIBLE);

                    lg_touchpad.setVisibility(View.INVISIBLE);

                    pad_switch = 0;
                }
            }
        });

        activityLgWifiRemoteBinding.lgRemoteBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(LgWifiRemoteActivity.this, FirstActivity.class);
                startActivity(intent);
                finish();

            }
        });

        activityLgWifiRemoteBinding.buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                vibrator.vibrate(50);

                if (getKeyControl() != null) {
                    getKeyControl().back(null);
                }

            }
        });


        activityLgWifiRemoteBinding.button0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                vibrator.vibrate(50);
                if (getKeyControl() != null) {
                    getKeyControl().sendKeyCode(KeyCode.NUM_0, null);
                }
            }
        });

        activityLgWifiRemoteBinding.button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                vibrator.vibrate(50);
                if (getKeyControl() != null) {
                    getKeyControl().sendKeyCode(KeyCode.NUM_1, null);
                }
            }
        });


        activityLgWifiRemoteBinding.button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                vibrator.vibrate(50);
                if (getKeyControl() != null) {
                    getKeyControl().sendKeyCode(KeyCode.NUM_2, null);
                }
            }
        });

        activityLgWifiRemoteBinding.button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                vibrator.vibrate(50);
                if (getKeyControl() != null) {
                    getKeyControl().sendKeyCode(KeyCode.NUM_3, null);
                }
            }
        });

        activityLgWifiRemoteBinding.button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                vibrator.vibrate(50);
                if (getKeyControl() != null) {
                    getKeyControl().sendKeyCode(KeyCode.NUM_4, null);
                }
            }
        });

        activityLgWifiRemoteBinding.button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                vibrator.vibrate(50);
                if (getKeyControl() != null) {
                    getKeyControl().sendKeyCode(KeyCode.NUM_5, null);
                }
            }
        });

        activityLgWifiRemoteBinding.button6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                vibrator.vibrate(50);
                if (getKeyControl() != null) {
                    getKeyControl().sendKeyCode(KeyCode.NUM_6, null);
                }
            }
        });

        activityLgWifiRemoteBinding.button7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                vibrator.vibrate(50);
                if (getKeyControl() != null) {
                    getKeyControl().sendKeyCode(KeyCode.NUM_7, null);
                }
            }
        });

        activityLgWifiRemoteBinding.button8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                vibrator.vibrate(50);
                if (getKeyControl() != null) {
                    getKeyControl().sendKeyCode(KeyCode.NUM_8, null);
                }
            }
        });


        activityLgWifiRemoteBinding.button9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                vibrator.vibrate(50);
                if (getKeyControl() != null) {
                    getKeyControl().sendKeyCode(KeyCode.NUM_9, null);
                }
            }
        });

        activityLgWifiRemoteBinding.buttonCursorok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                vibrator.vibrate(50);
                if (getKeyControl() != null) {
                    getKeyControl().sendKeyCode(KeyCode.ENTER, null);
                }
            }
        });

        activityLgWifiRemoteBinding.buttonHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                vibrator.vibrate(50);
                if (getKeyControl() != null) {
                    getKeyControl().home(null);
                    testResponse = new TestResponseClass(true, TestResponseClass.SuccessCode, TestResponseClass.HomeClicked);
                }
            }
        });

        activityLgWifiRemoteBinding.buttonPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                vibrator.vibrate(50);
                if (getMediaControl() != null) {
                    getMediaControl().previous(null);
                    testResponse = new TestResponseClass(true, TestResponseClass.SuccessCode, TestResponseClass.Previous);
                }
            }
        });

        activityLgWifiRemoteBinding.buttonRewind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                vibrator.vibrate(50);
                if (getMediaControl() != null) {
                    getMediaControl().previous(null);
                    testResponse = new TestResponseClass(true, TestResponseClass.SuccessCode, TestResponseClass.Rewind_Media);
                }
            }
        });

        activityLgWifiRemoteBinding.buttonForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                vibrator.vibrate(50);
                if (getMediaControl() != null) {
                    getMediaControl().fastForward(null);
                    testResponse = new TestResponseClass(true, TestResponseClass.SuccessCode, TestResponseClass.FastForward_Media);
                }
            }
        });

        activityLgWifiRemoteBinding.buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                vibrator.vibrate(50);
                if (getMediaControl() != null) {
                    getMediaControl().next(null);
                    testResponse = new TestResponseClass(true, TestResponseClass.SuccessCode, TestResponseClass.Next);
                }
            }
        });


        activityLgWifiRemoteBinding.buttonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (play_switch == 0) {
                    activityLgWifiRemoteBinding.buttonPlay.setImageResource(R.drawable.pause);
                    if (getMediaControl() != null) {
                        getMediaControl().play(null);
                        testResponse = new TestResponseClass(true, TestResponseClass.SuccessCode, TestResponseClass.Played_Media);
                    }

                    play_switch = 1;
                } else {
                    activityLgWifiRemoteBinding.buttonPlay.setImageResource(R.drawable.play);
                    if (getMediaControl() != null) {
                        getMediaControl().pause(null);
                        testResponse = new TestResponseClass(true, TestResponseClass.SuccessCode, TestResponseClass.Paused_Media);
                    }
                    play_switch = 0;
                }
            }
        });

        activityLgWifiRemoteBinding.buttonTurnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (getPowerControl() != null) {
                    getPowerControl().powerOff(null);
                    testResponse = new TestResponseClass(true, TestResponseClass.SuccessCode, TestResponseClass.Power_OFF);
                }
            }
        });

        activityLgWifiRemoteBinding.buttonMute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                vibrator.vibrate(50);

                if (mute_switch == 0) {
                    if (getVolumeControl() != null) {
                        getVolumeControl().setMute(true, null);
                        testResponse = new TestResponseClass(true, TestResponseClass.SuccessCode, TestResponseClass.Muted_Media);
                    }
                    mute_switch = 1;
                } else {
                    if (getVolumeControl() != null) {
                        getVolumeControl().setMute(false, null);
                        testResponse = new TestResponseClass(true, TestResponseClass.SuccessCode, TestResponseClass.UnMuted_Media);
                    }
                    mute_switch = 0;
                }

            }
        });

        activityLgWifiRemoteBinding.buttonChup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                vibrator.vibrate(50);
                if (getTvControl() != null) {
                    getTvControl().channelUp(null);
                }

            }
        });

        activityLgWifiRemoteBinding.buttonChdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                vibrator.vibrate(50);
                if (getTvControl() != null) {
                    getTvControl().channelDown(null);
                }
            }
        });

        activityLgWifiRemoteBinding.buttonVolup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                vibrator.vibrate(50);
                if (getVolumeControl() != null) {
                    getVolumeControl().volumeUp(null);
                    testResponse = new TestResponseClass(true, TestResponseClass.SuccessCode, TestResponseClass.VolumeUp);

                }
            }
        });

        activityLgWifiRemoteBinding.buttonVoldown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                vibrator.vibrate(50);
                if (getVolumeControl() != null) {
                    getVolumeControl().volumeDown(null);
                    testResponse = new TestResponseClass(true, TestResponseClass.SuccessCode, TestResponseClass.VolumeDown);

                }
            }
        });

        activityLgWifiRemoteBinding.buttonCursorup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                vibrator.vibrate(50);
                if (getKeyControl() != null) {
                    getKeyControl().up(null);
                    testResponse = new TestResponseClass(true, TestResponseClass.SuccessCode, TestResponseClass.UpClicked);
                }
            }
        });

        activityLgWifiRemoteBinding.buttonCursordown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                vibrator.vibrate(50);
                if (getKeyControl() != null) {
                    getKeyControl().down(null);
                    testResponse = new TestResponseClass(true, TestResponseClass.SuccessCode, TestResponseClass.DownClicked);
                }
            }
        });

        activityLgWifiRemoteBinding.buttonCursorright.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                vibrator.vibrate(50);
                if (getKeyControl() != null) {
                    getKeyControl().right(null);
                    testResponse = new TestResponseClass(true, TestResponseClass.SuccessCode, TestResponseClass.RightClicked);
                }
            }
        });

        activityLgWifiRemoteBinding.buttonCursorleft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                vibrator.vibrate(50);
                if (getKeyControl() != null) {
                    getKeyControl().left(null);
                    testResponse = new TestResponseClass(true, TestResponseClass.SuccessCode, TestResponseClass.LeftClicked);
                }
            }
        });

    }

    private void showDialog(String message) {

        sonyPicker = new sonyPicker(this);
        dialog = sonyPicker.getPickerDialog(message, new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

                connectableDevice = (ConnectableDevice) arg0.getItemAtPosition(arg2);

                connectableDevice.connect();
                Toast.makeText(LgWifiRemoteActivity.this, connectableDevice.getFriendlyName(), Toast.LENGTH_SHORT).show();

                sonyPicker.pickDevice(connectableDevice);

                launcher = connectableDevice.getCapability(Launcher.class);
                keyControl = connectableDevice.getCapability(KeyControl.class);
                powerControl = connectableDevice.getCapability(PowerControl.class);
                mediaControl = connectableDevice.getCapability(MediaControl.class);
                volumeControl = connectableDevice.getCapability(VolumeControl.class);
                tvControl = connectableDevice.getCapability(TVControl.class);

            }
        });

        dialog.show();
    }

    public void setRunningAppInfo(LaunchSession session) {
        runningAppSession = session;
    }

    public Launcher getLauncher() {
        return launcher;
    }

    public ConnectableDevice getTv() {
        return connectableDevice;
    }

    public KeyControl getKeyControl() {
        return keyControl;
    }

    public PowerControl getPowerControl() {
        return powerControl;
    }

    public MediaControl getMediaControl() {
        return mediaControl;
    }

    public VolumeControl getVolumeControl() {
        return volumeControl;
    }

    public TVControl getTvControl() {
        return tvControl;
    }

    @Override
    public void onDeviceAdded(DiscoveryManager manager, ConnectableDevice device) {

    }

    @Override
    public void onDeviceUpdated(DiscoveryManager manager, ConnectableDevice device) {

    }

    @Override
    public void onDeviceRemoved(DiscoveryManager manager, ConnectableDevice device) {

    }

    @Override
    public void onDiscoveryFailed(DiscoveryManager manager, ServiceCommandError error) {

    }

    @Override
    protected void onPause() {
        super.onPause();

        this.lg_touchpad.reset();
    }

    @Override
    protected void onResume() {
        super.onResume();

        lg_touchpad.setVisibility(View.INVISIBLE);
        lg_touchpad.reset();
        activityLgWifiRemoteBinding.channelLay.setVisibility(View.VISIBLE);
        activityLgWifiRemoteBinding.volumeLay.setVisibility(View.VISIBLE);

        activityLgWifiRemoteBinding.cursorLay.setVisibility(View.VISIBLE);
        activityLgWifiRemoteBinding.numberLay.setVisibility(View.INVISIBLE);

        toogle = 0;
        pad_switch = 0;
    }


    @Override
    public void onBackPressed() {

        Intent intent = new Intent(LgWifiRemoteActivity.this, FirstActivity.class);
        startActivity(intent);
        finish();
    }

}