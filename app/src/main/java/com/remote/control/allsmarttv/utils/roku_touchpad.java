package com.remote.control.allsmarttv.utils;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.jaku.core.KeypressKeyValues;
import com.remote.control.allsmarttv.R;

public class roku_touchpad extends View {

    private int mCurrentRepeatPeriod = 0;
    private int mGamepadPointerId = -1;
    private int mIntervalLongMs;
    private int mIntervalNormalMs;
    private int mIntervalShortMs;
    private Listener mListener;
    private int mLongPressWaitMs;
    private boolean mLongPressingCenter = false;
    private int mLongSwipeDistance;
    private RepeatHandler mRepeatHandler;
    private int mShortSwipeDistance;
    private float mStartingX;
    private float mStartingY;
    private int mSwipeDirection = 0;
    private long mSwipeStartTime;
    private int mTapRadius;
    private int mTapRadiusSquared;
    private boolean mVibrationOngoing = false;
    private Vibrator mVibrator = ((Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE));

    public interface Listener {
        void onLongPressCenterDown();

        void onLongPressCenterUp();

        void onTrackpadEvent();

        void performKey(KeypressKeyValues keypressKeyValues);
    }

    public class RepeatHandler extends Handler {
        static final int MESSAGE_REPEAT = 0;

        RepeatHandler(roku_touchpad rokutouchpadView, RepeatHandler repeatHandler) {
            this();
        }

        private RepeatHandler() {
        }

        public void handleMessage(Message message) {
            switch (message.what) {
                case 0:
                    roku_touchpad.this.sendEvent(roku_touchpad.this.mSwipeDirection, false);
                    removeMessages(0);
                    if (roku_touchpad.this.mCurrentRepeatPeriod > 0) {
                        sendEmptyMessageDelayed(0, (long) roku_touchpad.this.mCurrentRepeatPeriod);
                        return;
                    }
                    return;
                default:
                    return;
            }
        }
    }

    public roku_touchpad(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        Resources resources = context.getResources();
        this.mTapRadius = resources.getDimensionPixelSize(R.dimen.touchpad_tap_rad);
        this.mTapRadiusSquared = this.mTapRadius * this.mTapRadius;
        this.mShortSwipeDistance = resources.getDimensionPixelSize(R.dimen.touchpad_short_swipe_dis);
        this.mLongSwipeDistance = resources.getDimensionPixelSize(R.dimen.touchpad_long_swipe_dis);
        this.mIntervalLongMs = resources.getInteger(R.integer.touchpad_interval_long);
        this.mIntervalNormalMs = resources.getInteger(R.integer.touchpad_interval_normal);
        this.mIntervalShortMs = resources.getInteger(R.integer.touchpad_interval_short);
        ViewConfiguration.get(context);
        this.mLongPressWaitMs = ViewConfiguration.getLongPressTimeout();
        this.mRepeatHandler = new RepeatHandler(this, null);
    }


    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        getParent().requestDisallowInterceptTouchEvent(true);
    }


    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getParent().requestDisallowInterceptTouchEvent(false);
    }


    public void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        int measuredHeight = getMeasuredHeight();
        int measuredWidth = getMeasuredWidth();
        if (measuredHeight >= measuredWidth) {
            measuredHeight = measuredWidth;
        }
        setMeasuredDimension(measuredHeight, measuredHeight);
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        switch (motionEvent.getActionMasked()) {
            case 0:
            case 5:
                handleActionDown(motionEvent);
                return true;
            case 1:
            case 6:
                handleActionUp(motionEvent);
                return true;
            case 2:
                handleActionMove(motionEvent);
                if (motionEvent.getX() < 0.0f || motionEvent.getY() < 0.0f || motionEvent.getX() >= ((float) getWidth()) || motionEvent.getY() >= ((float) getHeight())) {
                    vibrate();
                    return true;
                }
                this.mVibrationOngoing = false;
                return true;
            case 3:
            case 4:
            default:
                return true;
        }
    }

    public void reset() {
        this.mGamepadPointerId = -1;
        this.mSwipeDirection = 0;
        this.mCurrentRepeatPeriod = 0;
        this.mRepeatHandler.removeMessages(0);
        this.mLongPressingCenter = false;
    }

    private long getTime() {
        return System.currentTimeMillis();
    }

    private void handleActionDown(MotionEvent motionEvent) {
        if (this.mGamepadPointerId == -1) {
            int actionIndex = motionEvent.getActionIndex();
            float x = motionEvent.getX(actionIndex);
            float y = motionEvent.getY(actionIndex);
            this.mGamepadPointerId = motionEvent.getPointerId(actionIndex);
            this.mStartingX = x;
            this.mStartingY = y;
            this.mSwipeStartTime = getTime();
        }
    }

    private void handleActionUp(MotionEvent motionEvent) {
        if (motionEvent.getPointerId(motionEvent.getActionIndex()) == this.mGamepadPointerId) {
            if (this.mSwipeDirection == 0) {
                if (!this.mLongPressingCenter) {
                    this.mListener.performKey(KeypressKeyValues.SELECT);
                } else {
                    this.mListener.onLongPressCenterUp();
                }
            }
            reset();
        }
    }

    private void handleActionMove(MotionEvent motionEvent) {
        int pointerCount = motionEvent.getPointerCount();
        for (int i = 0; i < pointerCount; i++) {
            if (motionEvent.getPointerId(i) == this.mGamepadPointerId) {
                handleNewCoordinates(motionEvent.getX(i), motionEvent.getY(i));
            }
        }
    }

    private void handleNewCoordinates(float f, float f2) {
        if (this.mSwipeDirection == 0) {
            float f3 = f - this.mStartingX;
            float f4 = f2 - this.mStartingY;
            if ((f3 * f3) + (f4 * f4) > ((float) this.mTapRadiusSquared)) {
                if (Math.abs(f3) >= Math.abs(f4)) {
                    if (f3 >= 0.0f) {
                        this.mSwipeDirection = 2;
                    } else {
                        this.mSwipeDirection = 1;
                    }
                } else if (f4 >= 0.0f) {
                    this.mSwipeDirection = 4;
                } else {
                    this.mSwipeDirection = 3;
                }
            }
        }
        handleNewSwipeDistance(getSwipeDistance(f, f2));
    }

    private float getSwipeDistance(float f, float f2) {
        switch (this.mSwipeDirection) {
            case 1:
                if (this.mStartingX - f < 0.0f) {
                    this.mStartingX = f;
                }
                return this.mStartingX - f;
            case 2:
                if (f - this.mStartingX < 0.0f) {
                    this.mStartingX = f;
                }
                return f - this.mStartingX;
            case 3:
                if (this.mStartingY - f2 < 0.0f) {
                    this.mStartingY = f2;
                }
                return this.mStartingY - f2;
            case 4:
                if (f2 - this.mStartingY < 0.0f) {
                    this.mStartingY = f2;
                }
                return f2 - this.mStartingY;
            default:
                return 0.0f;
        }
    }

    private void setTimer(float f) {
        if (f < ((float) this.mTapRadius)) {
            this.mCurrentRepeatPeriod = 0;
            this.mRepeatHandler.removeMessages(0);
        } else if (f < ((float) this.mShortSwipeDistance)) {
            this.mCurrentRepeatPeriod = this.mIntervalLongMs;
            this.mRepeatHandler.sendEmptyMessageDelayed(0, (long) this.mCurrentRepeatPeriod);
        } else if (f < ((float) this.mLongSwipeDistance)) {
            this.mCurrentRepeatPeriod = this.mIntervalNormalMs;
            this.mRepeatHandler.sendEmptyMessageDelayed(0, (long) this.mCurrentRepeatPeriod);
        } else {
            this.mCurrentRepeatPeriod = this.mIntervalShortMs;
            this.mRepeatHandler.sendEmptyMessageDelayed(0, (long) this.mCurrentRepeatPeriod);
        }
    }

    private void handleNewSwipeDistance(float f) {
        boolean z = false;
        if (!this.mRepeatHandler.hasMessages(0)) {
            if (getTime() - this.mSwipeStartTime > ((long) this.mLongPressWaitMs)) {
                z = true;
            }
            sendEvent(this.mSwipeDirection, z);
        }
        setTimer(f);
    }

    private void sendEvent(int i, boolean z) {
        switch (i) {
            case 0:
                if (z && !this.mLongPressingCenter) {
                    this.mListener.onLongPressCenterDown();
                    this.mLongPressingCenter = true;
                    return;
                }
                return;
            case 1:
                this.mListener.performKey(KeypressKeyValues.LEFT);
                return;
            case 2:
                this.mListener.performKey(KeypressKeyValues.RIGHT);
                return;
            case 3:
                this.mListener.performKey(KeypressKeyValues.UP);
                return;
            case 4:
                this.mListener.performKey(KeypressKeyValues.DOWN);
                return;
            default:
                return;
        }
    }

    private void vibrate() {
        if (!this.mVibrationOngoing) {
            this.mVibrator.vibrate(100);
            this.mVibrationOngoing = true;
        }
    }

    public void setListener(Listener listener) {
        this.mListener = listener;
    }

}
