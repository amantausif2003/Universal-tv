package com.remote.control.allsmarttv.Activities.ir;

import android.content.Context;
import android.hardware.ConsumerIrManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.remote.control.allsmarttv.R;
import com.remote.control.allsmarttv.Utils.ir_utils.ParentActvity;
import com.remote.control.allsmarttv.Utils.ir_utils.SupportedClass;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JVCRemoteActivity extends AppCompatActivity {

    private final static String CMD_TV_POWER =
            "0000 006D 0012 0011 013F 00A0 0014 003C 0014 003C 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 003C 0014 003C 0014 003C 0014 0014 0014 003C 0014 0014 0014 0014 0014 0014 0014 0382 0014 003C 0014 003C 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 003C 0014 003C 0014 003C 0014 0014 0014 003C 0014 0014 0014 0014 0014 0014 0014 0382";
    private final static String CMD_TV_CH_NEXT =
            "0000 006D 0012 0011 013F 00A0 0014 003C 0014 003C 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 003C 0014 0014 0014 0014 0014 003C 0014 003C 0014 0014 0014 0014 0014 0014 0014 0382 0014 003C 0014 003C 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 003C 0014 0014 0014 0014 0014 003C 0014 003C 0014 0014 0014 0014 0014 0014 0014 0382";
    private final static String CMD_TV_CH_PREV =
            "0000 006D 0012 0011 013F 00A0 0014 003C 0014 003C 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 003C 0014 003C 0014 0014 0014 0014 0014 0014 0014 0382 0014 003C 0014 003C 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 003C 0014 003C 0014 0014 0014 0014 0014 0014 0014 0382";
    private final static String CMD_TV_UP =
            "0000 006D 0012 0011 013F 00A0 0014 003C 0014 003C 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 003C 0014 003C 0014 003C 0014 003C 0014 003C 0014 0014 0014 0382 0014 003C 0014 003C 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 003C 0014 003C 0014 003C 0014 003C 0014 003C 0014 0014 0014 0382";
    private final static String CMD_TV_DOWN =
            "0000 006D 0012 0011 013F 00A0 0014 003C 0014 003C 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 003C 0014 003C 0014 0014 0014 003C 0014 003C 0014 003C 0014 003C 0014 0014 0014 0382 0014 003C 0014 003C 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 003C 0014 003C 0014 0014 0014 003C 0014 003C 0014 003C 0014 003C 0014 0014 0014 0382";
    private final static String CMD_TV_MENU =
            "0000 006D 0012 0011 013F 00A0 0014 003C 0014 003C 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 003C 0014 0014 0014 003C 0014 003C 0014 003C 0014 003C 0014 0014 0014 0382 0014 003C 0014 003C 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 003C 0014 0014 0014 003C 0014 003C 0014 003C 0014 003C 0014 0014 0014 0382";
    private String TAG = "JVCRemoteActivity";

    private final static String CMD_TV_LEFT =
            "0000 006D 0012 0011 013F 00A0 0014 003C 0014 003C 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 003C 0014 003C 0014 0014 0014 003C 0014 003C 0014 0014 0014 003C 0014 0014 0014 0382 0014 003C 0014 003C 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 003C 0014 003C 0014 0014 0014 003C 0014 003C 0014 0014 0014 003C 0014 0014 0014 0382";
    private final static String CMD_TV_RIGHT =
            "0000 006D 0012 0011 013F 00A0 0014 003C 0014 003C 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 003C 0014 0014 0014 003C 0014 003C 0014 0014 0014 003C 0014 0014 0014 0382 0014 003C 0014 003C 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 003C 0014 0014 0014 003C 0014 003C 0014 0014 0014 003C 0014 0014 0014 0382";
    private final static String CMD_TV_ENTER =
            "0000 006D 0012 0011 013F 00A0 0014 003C 0014 003C 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 003C 0014 0014 0014 003C 0014 0014 0014 0014 0014 0014 0014 0014 0014 0382 0014 003C 0014 003C 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 003C 0014 0014 0014 003C 0014 0014 0014 0014 0014 0014 0014 0014 0014 0382";
    private final static String CMD_TV_MUTE =
            "0000 006D 0012 0011 013F 00A0 0014 003C 0014 003C 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 003C 0014 003C 0014 003C 0014 0014 0014 0014 0014 0014 0014 0382 0014 003C 0014 003C 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 003C 0014 003C 0014 003C 0014 0014 0014 0014 0014 0014 0014 0382";
    private final static String CMD_TV_EXIT =
            "0000 006D 0012 0011 013F 00A0 0014 003C 0014 003C 0014 003C 0014 003C 0014 0014 0014 0014 0014 0014 0014 0014 0014 003C 0014 0014 0014 003C 0014 003C 0014 003C 0014 0014 0014 0014 0014 0014 0014 0382 0014 003C 0014 003C 0014 003C 0014 003C 0014 0014 0014 0014 0014 0014 0014 0014 0014 003C 0014 0014 0014 003C 0014 003C 0014 003C 0014 0014 0014 0014 0014 0014 0014 0382";
    private final static String CMD_SB_VOLUP =
            "0000 006D 0012 0011 013F 00A0 0014 003C 0014 003C 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 003C 0014 003C 0014 003C 0014 003C 0014 0014 0014 0014 0014 0014 0014 0382 0014 003C 0014 003C 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 003C 0014 003C 0014 003C 0014 003C 0014 0014 0014 0014 0014 0014 0014 0382";
    private final static String CMD_SB_VOLDOWN =
            "0000 006D 0012 0011 013F 00A0 0014 003C 0014 003C 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 003C 0014 003C 0014 003C 0014 003C 0014 003C 0014 0014 0014 0014 0014 0014 0014 0382 0014 003C 0014 003C 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 0014 003C 0014 003C 0014 003C 0014 003C 0014 003C 0014 0014 0014 0014 0014 0014 0014 0382";
    private ConsumerIrManager irManager;
    private Vibrator vibe;
    MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SupportedClass.loadLangLocale(getBaseContext());
        setContentView(R.layout.activity_jvc_remote);
        if (this.getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        /*RelativeLayout mAdView = findViewById(R.id.adView);
        loadBannerAds(mAdView);*/
        irManager = (ConsumerIrManager) getSystemService(CONSUMER_IR_SERVICE);

        vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        mp = MediaPlayer.create(this, R.raw.beep);

        findViewById(R.id.tvpower).setOnClickListener(new ClickListener(hex2ir(CMD_TV_POWER)));
        findViewById(R.id.tvchnext).setOnClickListener(new ClickListener(hex2ir(CMD_TV_CH_NEXT)));
        findViewById(R.id.tv_up).setOnClickListener(new ClickListener(hex2ir(CMD_TV_CH_NEXT)));

        findViewById(R.id.tvchprev).setOnClickListener(new ClickListener(hex2ir(CMD_TV_CH_PREV)));
        findViewById(R.id.tv_bottom).setOnClickListener(new ClickListener(hex2ir(CMD_TV_CH_PREV)));
        findViewById(R.id.tvleft).setOnClickListener(new ClickListener(hex2ir(CMD_TV_LEFT)));
        findViewById(R.id.tvright).setOnClickListener(new ClickListener(hex2ir(CMD_TV_RIGHT)));
        findViewById(R.id.tvok).setOnClickListener(new ClickListener(hex2ir(CMD_TV_ENTER)));
        findViewById(R.id.txt_ok).setOnClickListener(new ClickListener(hex2ir(CMD_TV_ENTER)));

        findViewById(R.id.sbvoldown).setOnClickListener(new ClickListener(hex2ir(CMD_SB_VOLDOWN)));
        findViewById(R.id.sbvolup).setOnClickListener(new ClickListener(hex2ir(CMD_SB_VOLUP)));
        findViewById(R.id.menu_img).setOnClickListener(new ClickListener(hex2ir(CMD_TV_MENU)));
        findViewById(R.id.mute_img).setOnClickListener(new ClickListener(hex2ir(CMD_TV_MUTE)));

        findViewById(R.id.tv_up).setOnClickListener(new ClickListener(hex2ir(CMD_TV_UP)));
        findViewById(R.id.tv_bottom).setOnClickListener(new ClickListener(hex2ir(CMD_TV_DOWN)));
        findViewById(R.id.home_img).setOnClickListener(new ClickListener(hex2ir(CMD_TV_EXIT)));
    }

    private IRCommand hex2ir(final String irData) {
        List<String> list = new ArrayList<String>(Arrays.asList(irData.split(" ")));
        list.remove(0); // dummy
        int frequency = Integer.parseInt(list.remove(0), 16); // frequency
        list.remove(0); // seq1
        list.remove(0); // seq2

        frequency = (int) (1000000 / (frequency * 0.241246));
        int pulses = 1000000 / frequency;
        int count;

        int[] pattern = new int[list.size()];
        for (int i = 0; i < list.size(); i++) {
            count = Integer.parseInt(list.get(i), 16);
            pattern[i] = count * pulses;
        }

        return new IRCommand(frequency, pattern);
    }


    private class ClickListener implements View.OnClickListener {
        private final IRCommand cmd;

        public ClickListener(final IRCommand cmd) {
            this.cmd = cmd;
        }

        @Override
        public void onClick(final View view) {

            mp.start();

//            ToneGenerator toneGenerator = new ToneGenerator(1, 100);
//            toneGenerator.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200); //200 is duration in ms

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibe.vibrate(VibrationEffect.createOneShot(150, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                //deprecated in API 26
                vibe.vibrate(150);
            }
            try {
                Log.d("Remote", "frequency: " + cmd.freq);
                Log.d("Remote", "pattern: " + Arrays.toString(cmd.pattern));
                irManager.transmit(cmd.freq, cmd.pattern);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        }
    }

    private class IRCommand {
        private final int freq;
        private final int[] pattern;

        private IRCommand(int freq, int[] pattern) {
            this.freq = freq;
            this.pattern = pattern;
        }
    }
}