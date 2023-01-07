package com.remote.control.allsmarttv.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.ExtractedText;
import android.view.inputmethod.ExtractedTextRequest;

import java.util.Map;

public abstract class Device {

    final Context mContext;
    final DevicesInfoUtil mDevicesInfoUtil;

    public static abstract class Listener {
        public abstract void onAsset(Device device, String str, Map<String, String> map, byte[] bArr);

        public abstract void onBugReportStatus(Device device, int i);

        public abstract void onBundle(Device device, int i, Bundle bundle);

        public abstract void onCapabilities(Device device, CapabilitiesClass capabilitiesClass);

        public abstract void onCompletionInfo(Device device, CompletionInfo[] completionInfoArr);

        public abstract void onConfigureFailure(Device device, int i);

        public abstract void onConfigureSuccess(Device device);

        public abstract void onConnectFailed(Device device);

        public abstract void onConnected(Device device);

        public abstract void onConnecting(Device device);

        public abstract void onDeveloperStatus(Device device, boolean z);

        public abstract void onDisconnected(Device device);

        public abstract void onException(Device device, Exception exc);

        public abstract void onHideIme(Device device);

        public abstract void onPairingRequired(Device device);

        public abstract void onShowIme(Device device, EditorInfo editorInfo, boolean z, ExtractedText extractedText);

        public abstract void onStartVoice(Device device);

        public abstract void onStopVoice(Device device);

        public abstract void onVoiceSoundLevel(Device device, int i);
    }

    public class UnconfiguredException extends RuntimeException {
        public UnconfiguredException() {
            super("Client not configured");
        }
    }

    public class WaitTimedOutException extends RuntimeException {
        public WaitTimedOutException() {
            super("Wait on response timed out");
        }
    }

    Device(Context context, DevicesInfoUtil devicesInfoUtil) {
        this.mContext = context;
        this.mDevicesInfoUtil = devicesInfoUtil;
    }

    @Deprecated
    public static Device from(Context context, DevicesInfoUtil devicesInfoUtil, Listener listener) {
        return from(context, devicesInfoUtil, listener, new Handler(Looper.getMainLooper()));
    }

    public static Device from(Context context, DevicesInfoUtil devicesInfoUtil, Listener listener, Handler handler) {
        String scheme = devicesInfoUtil.getUri().getScheme();
        if ("tcp".equals(scheme)) {
            return new TcpDeviceImpl(context, devicesInfoUtil, listener, handler);
        }
        if (!"bt".equals(scheme)) {
            throw new IllegalArgumentException("Unsupported connection info");
        } else if (Build.VERSION.SDK_INT >= 15) {
            return new BluetoothDeviceImp(context, devicesInfoUtil, listener, handler);
        } else {
            throw new IllegalStateException("Unsupported DeviceInfo for SDK version " + Build.VERSION.SDK_INT);
        }
    }

    public abstract void beginBatchEdit();

    public abstract void cancelBugReport();

    public abstract void cancelPairing();

    public abstract void commitCompletion(CompletionInfo completionInfo);

    public abstract void commitText(CharSequence charSequence, int i);

    @Deprecated
    public abstract void configure(int i, int i2, float f);

    public abstract void deleteSurroundingText(int i, int i2);

    public abstract void disconnect();

    public abstract void endBatchEdit();

    public abstract void finishComposingText();

    public abstract BuildInfo getBuildInfo();

    public abstract int getControllerNumber();

    public abstract int getCursorCapsMode(int i);

    public abstract String getDescriptor();

    public DevicesInfoUtil getDeviceInfo() {
        return this.mDevicesInfoUtil;
    }

    public abstract ExtractedText getExtractedText(ExtractedTextRequest extractedTextRequest, int i);

    public abstract CharSequence getSelectedText(int i);

    public abstract CharSequence getTextAfterCursor(int i, int i2);

    public abstract CharSequence getTextBeforeCursor(int i, int i2);

    public abstract boolean isConfigured();

    public abstract boolean isConnected();

    public abstract void isInteractive(boolean z);

    public abstract boolean isVoiceRecording();

    public abstract void performEditorAction(int i);

    public abstract void requestCursorUpdates(int i);

    public abstract void sendBundle(int i, Bundle bundle);

    public abstract void sendIntent(Intent intent);

    public abstract void sendKeyEvent(int i, int i2);

    public abstract void sendMotionEvent(MotionEvent motionEvent);

    public abstract void setComposingRegion(int i, int i2);

    public abstract void setComposingText(CharSequence charSequence, int i);

    public abstract void setPairingSecret(String str);

    public abstract void setSelection(int i, int i2);

    public abstract void startVoice();

    public abstract void stopVoice();

    public abstract void takeBugReport();

    public String toString() {
        return this.mDevicesInfoUtil.toString();
    }
}
