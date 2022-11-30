package com.remote.control.allsmarttv.Utils;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.ExtractedText;
import android.view.inputmethod.ExtractedTextRequest;

import androidx.collection.ArrayMap;
import androidx.collection.SimpleArrayMap;

import java.security.SecureRandom;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public abstract class AbsDeviceImpl extends Device {
    private static final String IDENTIFIER_PREF = "identifier";
    private static final byte MAX_POINTERS = 32;
    private static final String PREFERENCES_NAME = "ATVRemoteSDK";
    private static final String TAG = "AtvRemote.Device";
    protected BuildInfo mBuildInfo;
    protected final Handler mCallbackHandler;
    protected Pair<Integer, Integer> mConfiguration;
    protected boolean mConfigured = false;
    protected int mControllerNumber;
    protected float mDensity;
    protected String mDescriptor;
    protected final PackEncoderClass mEncoder;
    protected boolean mHasExtendedIMEAPI = false;
    protected final Listener mListener;
    private final AtomicLong mSequence = new AtomicLong(1);
    protected final SpeechInput mSpeechInput;
    private final SimpleArrayMap<Long, Responder<Object>> responders = new ArrayMap();

    public class Responder<T> {
        final CountDownLatch mLatch = new CountDownLatch(1);
        T mResult;

        public Responder() {
        }

        public T get() throws InterruptedException {
            if (this.mLatch.await(2000, TimeUnit.MILLISECONDS)) {
                return this.mResult;
            }
            AbsDeviceImpl.this.onException(new WaitTimedOutException());
            return null;
        }

        public void setResult(T t) {
            this.mResult = t;
            this.mLatch.countDown();
        }
    }

    protected AbsDeviceImpl(Context context, DevicesInfoUtil devicesInfoUtil, Listener listener, Handler handler) {
        super(context, devicesInfoUtil);
        this.mListener = listener;
        this.mEncoder = new PackEncoderClass();
        this.mSpeechInput = new SpeechInput();
        this.mCallbackHandler = handler;
    }

    private static String randomMACAddress() {
        byte[] bArr = new byte[6];
        new SecureRandom().nextBytes(bArr);
        bArr[0] = (byte) ((byte) ((bArr[0] | 2) & -2));
        StringBuilder sb = new StringBuilder(18);
        for (byte b : bArr) {
            if (sb.length() > 0) {
                sb.append(":");
            }
            sb.append(String.format("%02x", Byte.valueOf(b)));
        }
        return sb.toString();
    }

    @Override
    public void beginBatchEdit() {
        if (this.mConfigured) {
            sendMessage(this.mEncoder.encodeBeginBatchEdit());
        } else {
            onException(new UnconfiguredException());
        }
    }

    @Override
    public void cancelBugReport() {
        if (this.mConfigured) {
            sendMessage(this.mEncoder.encodeCancelBugReport());
        } else {
            onException(new UnconfiguredException());
        }
    }

    @Override
    public void commitCompletion(CompletionInfo completionInfo) {
        if (this.mConfigured) {
            sendMessage(this.mEncoder.encodeCommitCompletion(completionInfo));
        } else {
            onException(new UnconfiguredException());
        }
    }

    @Override
    public final void commitText(CharSequence charSequence, int i) {
        if (this.mConfigured) {
            sendMessage(this.mEncoder.encodeCommitText(charSequence, i));
        } else {
            onException(new UnconfiguredException());
        }
    }

    @Override
    public final void configure(int i, int i2, float f) {
    }

    @Override
    public final void deleteSurroundingText(int i, int i2) {
        if (this.mConfigured) {
            sendMessage(this.mEncoder.encodeDeleteSurroundingText(i, i2));
        } else {
            onException(new UnconfiguredException());
        }
    }

    @Override
    public void endBatchEdit() {
        if (this.mConfigured) {
            sendMessage(this.mEncoder.encodeEndBatchEdit());
        } else {
            onException(new UnconfiguredException());
        }
    }

    @Override
    public void finishComposingText() {
        if (this.mConfigured) {
            sendMessage(this.mEncoder.encodeFinishComposingText());
        } else {
            onException(new UnconfiguredException());
        }
    }

    @Override
    public final BuildInfo getBuildInfo() {
        return this.mBuildInfo;
    }

    @Override
    public final int getControllerNumber() {
        return this.mControllerNumber;
    }

    @Override
    public int getCursorCapsMode(int i) {
        if (!this.mConfigured) {
            onException(new UnconfiguredException());
            return -1;
        } else if (!this.mHasExtendedIMEAPI) {
            return -1;
        } else {
            long andIncrement = this.mSequence.getAndIncrement();
            sendMessage(this.mEncoder.encodeGetCursorCapsMode(andIncrement, i));
            return ((Integer) waitOnResponse(andIncrement)).intValue();
        }
    }

    @Override
    public final String getDescriptor() {
        return this.mDescriptor;
    }

    @Override
    public ExtractedText getExtractedText(ExtractedTextRequest extractedTextRequest, int i) {
        if (!this.mConfigured) {
            onException(new UnconfiguredException());
            return null;
        } else if (!this.mHasExtendedIMEAPI) {
            return null;
        } else {
            long andIncrement = this.mSequence.getAndIncrement();
            sendMessage(this.mEncoder.encodeGetExtractedText(andIncrement, extractedTextRequest, i));
            return (ExtractedText) waitOnResponse(andIncrement);
        }
    }

    public final String getIdentifier() {
        if (Build.VERSION.SDK_INT <= 22) {
            return this.mContext.checkCallingOrSelfPermission("android.permission.BLUETOOTH") != PackageManager.PERMISSION_GRANTED ? ((WifiManager) this.mContext.getSystemService(Context.WIFI_SERVICE)).getConnectionInfo().getMacAddress() : BluetoothAdapter.getDefaultAdapter().getAddress();
        }
        SharedPreferences sharedPreferences = this.mContext.getSharedPreferences(PREFERENCES_NAME, 0);
        String string = sharedPreferences.getString(IDENTIFIER_PREF, null);
        if (string != null) {
            return string;
        }
        String randomMACAddress = randomMACAddress();
        Log.v(TAG, "Generating a unique identifier " + randomMACAddress);
        sharedPreferences.edit().putString(IDENTIFIER_PREF, randomMACAddress).apply();
        return randomMACAddress;
    }

    @Override
    public CharSequence getSelectedText(int i) {
        if (!this.mConfigured) {
            onException(new UnconfiguredException());
            return null;
        } else if (!this.mHasExtendedIMEAPI) {
            return null;
        } else {
            long andIncrement = this.mSequence.getAndIncrement();
            sendMessage(this.mEncoder.encodeGetSelectedText(andIncrement, i));
            return (CharSequence) waitOnResponse(andIncrement);
        }
    }

    @Override
    public CharSequence getTextAfterCursor(int i, int i2) {
        if (!this.mConfigured) {
            onException(new UnconfiguredException());
            return null;
        } else if (!this.mHasExtendedIMEAPI) {
            return null;
        } else {
            long andIncrement = this.mSequence.getAndIncrement();
            sendMessage(this.mEncoder.encodeGetTextAfterCursor(andIncrement, i, i2));
            return (CharSequence) waitOnResponse(andIncrement);
        }
    }

    @Override
    public CharSequence getTextBeforeCursor(int i, int i2) {
        if (!this.mConfigured) {
            onException(new UnconfiguredException());
            return null;
        } else if (!this.mHasExtendedIMEAPI) {
            return null;
        } else {
            long andIncrement = this.mSequence.getAndIncrement();
            sendMessage(this.mEncoder.encodeGetTextBeforeCursor(andIncrement, i, i2));
            return (CharSequence) waitOnResponse(andIncrement);
        }
    }

    public final void internalConfigure() {
        if (isConnected()) {
            Pair<Integer, Integer> pair = new Pair<>(1, 1);
            if (this.mConfigured && this.mConfiguration.equals(pair)) {
                onConfigureSuccess(this.mControllerNumber, this.mDescriptor, this.mBuildInfo);
                runOnUiThread(new Runnable() {

                    public void run() {
                        AbsDeviceImpl.this.mListener.onConfigureSuccess(AbsDeviceImpl.this);
                    }
                });
                return;
            }
            this.mConfigured = false;
            this.mConfiguration = pair;
            this.mDensity = 1.0f;
            String identifier = getIdentifier();
            Log.i(TAG, "Device identifier: " + identifier);
            sendMessage(this.mEncoder.encodeConfigure(1, 1, MAX_POINTERS, (byte) 3, identifier));
        }
    }

    @Override
    public final boolean isConfigured() {
        return this.mConfigured;
    }

    @Override
    public final void isInteractive(boolean z) {
        if (this.mConfigured) {
            sendMessage(this.mEncoder.encodeInteractive(z));
        } else {
            onException(new UnconfiguredException());
        }
    }

    @Override
    public final boolean isVoiceRecording() {
        return this.mSpeechInput.isRecording();
    }


    public final void onConfigureFailed() {
        this.mConfigured = false;
        this.mControllerNumber = 0;
        this.mDescriptor = null;
    }


    public final void onConfigureSuccess(int i, String str, BuildInfo buildInfo) {
        this.mConfigured = true;
        this.mControllerNumber = i;
        this.mDescriptor = str;
        this.mBuildInfo = buildInfo;
        Log.v(TAG, "onConfigureSuccess: " + this.mControllerNumber + " " + this.mDescriptor + " " + this.mBuildInfo);
    }


    public final void onException(final Exception exc) {
        runOnUiThread(new Runnable() {

            public void run() {
                AbsDeviceImpl.this.mListener.onException(AbsDeviceImpl.this, exc);
            }
        });
    }


    public void onResponse(long j, Object obj) {
        Responder<Object> remove = this.responders.remove(Long.valueOf(j));
        if (remove == null) {
            Log.w("ATVRemote.Responder", "Could not find caller for sequence " + j);
        } else {
            remove.setResult(obj);
        }
    }

    @Override
    public final void performEditorAction(int i) {
        if (!this.mConfigured) {
            onException(new UnconfiguredException());
        }
        sendMessage(this.mEncoder.encodePerformEditorAction(i));
    }

    @Override
    public void requestCursorUpdates(int i) {
        if (this.mConfigured) {
            sendMessage(this.mEncoder.encodeRequestCursorUpdates(i));
        } else {
            onException(new UnconfiguredException());
        }
    }


    public final void runOnUiThread(Runnable runnable) {
        this.mCallbackHandler.post(runnable);
    }

    @Override
    public void sendBundle(int i, Bundle bundle) {
        if (this.mConfigured) {
            sendMessage(this.mEncoder.encodeTransmitBundle(i, bundle));
        } else {
            onException(new UnconfiguredException());
        }
    }

    @Override
    public final void sendIntent(Intent intent) {
        if (this.mConfigured) {
            sendMessage(this.mEncoder.encodeIntent(intent));
        } else {
            onException(new UnconfiguredException());
        }
    }

    @Override
    public final void sendKeyEvent(int i, int i2) {
        if (this.mConfigured) {
            sendMessage(this.mEncoder.encodeKeyEvent(i2, i));
        } else {
            onException(new UnconfiguredException());
        }
    }


    public abstract void sendMessage(byte[] bArr);

    @Override
    public final void sendMotionEvent(android.view.MotionEvent motionEvent) {
        if (this.mConfigured) {
            sendMessage(MotionEventUtil.encodeTheMotionEvent(motionEvent, this.mEncoder, this.mDensity));
        } else {
            onException(new UnconfiguredException());
        }
    }

    @Override
    public void setComposingRegion(int i, int i2) {
        if (this.mConfigured) {
            sendMessage(this.mEncoder.encodeSetComposingRegion(i, i2));
        } else {
            onException(new UnconfiguredException());
        }
    }

    @Override
    public final void setComposingText(CharSequence charSequence, int i) {
        if (this.mConfigured) {
            sendMessage(this.mEncoder.encodeSetComposingText(charSequence, i));
        } else {
            onException(new UnconfiguredException());
        }
    }

    @Override
    public void setSelection(int i, int i2) {
        if (this.mConfigured) {
            sendMessage(this.mEncoder.encodeSetSelection(i, i2));
        } else {
            onException(new UnconfiguredException());
        }
    }

    @Override
    public final void startVoice() {
        if (this.mConfigured) {
            sendMessage(this.mEncoder.encodeStartVoice());
        } else {
            onException(new UnconfiguredException());
        }
    }

    @Override
    public final void stopVoice() {
        if (this.mConfigured) {
            sendMessage(this.mEncoder.encodeStopVoice());
        } else {
            onException(new UnconfiguredException());
        }
    }

    @Override
    public void takeBugReport() {
        if (this.mConfigured) {
            sendMessage(this.mEncoder.encodeTakeBugReport());
        } else {
            onException(new UnconfiguredException());
        }
    }


    public Object waitOnResponse(long j) {
        Responder<Object> responder = new Responder<>();
        this.responders.put(Long.valueOf(j), responder);
        try {
            Log.d(TAG, "Starting to wait for response on sequence id " + j);
            Object obj = responder.get();
            Log.d(TAG, "Finished waiting for response on sequence id " + j);
            return obj;
        } catch (InterruptedException e) {
            Log.w("ATVRemote.Responder", "Interrupted while waiting " + j, e);
            return null;
        }
    }
}
