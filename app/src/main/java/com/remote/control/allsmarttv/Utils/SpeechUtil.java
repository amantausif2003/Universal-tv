package com.remote.control.allsmarttv.Utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.remote.control.allsmarttv.R;


public class SpeechUtil extends FrameLayout {
    private int currentLevel;
    private final float focusedZoom;
    private ImageView icon;
    private boolean listening;
    private final int notRecordingColor;
    private final int recordingColor;
    private final float soundLevelMaxZoom;
    private final float speechOrbElevationListening;
    private final float speechOrbElevationNotListening;
    private View speechOrb;

    public SpeechUtil(Context context) {
        this(context, null);
    }

    public SpeechUtil(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public SpeechUtil(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.currentLevel = 0;
        this.listening = false;
        View inflate = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.speech_view, (ViewGroup) this, true);
        this.speechOrb = inflate.findViewById(R.id.speech_orb);
        this.icon = (ImageView) inflate.findViewById(R.id.speech_icon);
        setFocusable(true);
        setClipChildren(false);
        Resources resources = context.getResources();
        this.focusedZoom = resources.getFraction(R.fraction.speech_orb_focused_zoom_value, 1, 1);
        this.soundLevelMaxZoom = resources.getFraction(R.fraction.speech_orb_max_level_zoom_value, 1, 1);
        this.notRecordingColor = resources.getColor(R.color.not_recording);
        this.recordingColor = resources.getColor(R.color.recording);
        this.speechOrbElevationListening = (float) resources.getDimensionPixelSize(R.dimen.button_elevation_activated);
        this.speechOrbElevationNotListening = (float) resources.getDimensionPixelSize(R.dimen.button_elevation);
        showNotListening();
    }

    public void onFocusChanged(boolean z, int i, Rect rect) {
        super.onFocusChanged(z, i, rect);
        float f = z ? this.focusedZoom : 1.0f;
        this.speechOrb.animate().scaleX(f).scaleY(f).setDuration(200).start();
        if (z) {
            this.icon.setImageResource(R.drawable.voice);
        }
    }

    public void showListening() {
        this.icon.setImageResource(R.drawable.voice);
        setOrbColor(this.recordingColor);
        this.speechOrb.setScaleX(0.5f);
        this.speechOrb.setScaleY(0.5f);
        updateSpeechOrbViewElevation(this.speechOrbElevationListening);
        this.listening = true;
    }

    public void showNotListening() {
        this.icon.setImageResource(R.drawable.voice);
        setOrbColor(this.notRecordingColor);
        this.speechOrb.setScaleX(0.5f);
        this.speechOrb.setScaleY(0.5f);
        updateSpeechOrbViewElevation(this.speechOrbElevationNotListening);
        this.listening = false;
    }

    public void setSoundLevel(int i) {
        if (this.listening) {
            if (i > this.currentLevel) {
                this.currentLevel += (i - this.currentLevel) / 2;
            } else {
                this.currentLevel = (int) (((float) this.currentLevel) * 0.8f);
            }
            float f = this.focusedZoom + (((this.soundLevelMaxZoom - this.focusedZoom) * ((float) this.currentLevel)) / 100.0f);
            this.speechOrb.setScaleX(f);
            this.speechOrb.setScaleY(f);
        }
    }

    public void setOrbColor(int i) {
        if (this.speechOrb.getBackground() instanceof GradientDrawable) {
            ((GradientDrawable) this.speechOrb.getBackground()).setColor(i);
        }
    }

    @TargetApi(21)
    private void updateSpeechOrbViewElevation(float f) {
        if (Build.VERSION.SDK_INT >= 21) {
            this.icon.setElevation(f);
            this.speechOrb.setElevation(f);
        }
    }
}
