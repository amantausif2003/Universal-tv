package com.remote.control.allsmarttv.Utils;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.ExtractedText;
import android.view.inputmethod.ExtractedTextRequest;

import java.util.Map;

public class ClientService extends Service implements OnRemoteListener {

    private static  int[] ints = null;
    private static boolean DEBUG = false;
    private static final String TAG = "AtvRemote.ClntLstnrSrvc";
    private Binder mBinder = new LocalBinder();
    private CapabilitiesClass capabilitiesClass;
    private DevicesInfoUtil connectionInfo;
    private Status currentStatus = Status.NO_CONNECTION;
    private Device device;
    private Handler handler;
    private HandlerThread handlerThread;
    private BroadcastReceiver mKillReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            if (ClientService.DEBUG) {
                Log.d(ClientService.TAG, "Service killed from intent.");
            }
            ClientService.this.stopSelf();
            ClientService.this.dismissNotification();
        }
    };
    private Device.Listener localRemoteListener = new Device.Listener() {

        @Override
        public void onConnecting(final Device device) {
            if (ClientService.DEBUG) {
                Log.v(ClientService.TAG, "onConnecting to " + device);
            }
            ClientService.this.currentStatus = Status.CONNECTING;
            ClientService.this.updateNotification();
            if (ClientService.this.mRemoteListener != null) {
                ClientService.this.runOnUiThread(new Runnable() {

                    public void run() {
                        ClientService.this.mRemoteListener.onConnecting(device);
                    }
                });
            }
        }

        @Override
        public void onConnected(final Device device) {
            if (ClientService.DEBUG) {
                Log.v(ClientService.TAG, "Connected to " + device);
            }
            ClientService.this.currentStatus = Status.CONNECTED;
            ClientService.this.updateNotification();
            ClientService.this.declareTVReachableCapability(true);
            if (ClientService.this.mRemoteListener != null) {
                ClientService.this.runOnUiThread(new Runnable() {

                    public void run() {
                        ClientService.this.mRemoteListener.onConnected(device);
                    }
                });
            }
        }

        @Override
        public void onConnectFailed(final Device device) {
            if (ClientService.DEBUG) {
                Log.v(ClientService.TAG, "Failed to connect to " + device);
            }
            ClientService.this.declareTVReachableCapability(false);
            ClientService.this.currentStatus = Status.NO_CONNECTION;
            if (ClientService.this.mRemoteListener != null) {
                ClientService.this.runOnUiThread(new Runnable() {

                    public void run() {
                        ClientService.this.mRemoteListener.onConnectFailed(device);
                    }
                });
            }
            ClientService.this.stopSelf();
        }

        @Override
        public void onDisconnected(final Device device) {
            if (ClientService.DEBUG) {
                Log.v(ClientService.TAG, "Disconnected from " + device);
            }
            ClientService.this.currentStatus = Status.DISCONNECTED;
            ClientService.this.device = null;
            ClientService.this.connectionInfo = null;
            ClientService.this.capabilitiesClass = null;
            ClientService.this.updateNotification();
            ClientService.this.declareTVReachableCapability(false);
            if (ClientService.this.mRemoteListener != null) {
                ClientService.this.runOnUiThread(new Runnable() {

                    public void run() {
                        ClientService.this.mRemoteListener.onDisconnected(device);
                    }
                });
            }
        }

        @Override
        public void onPairingRequired(final Device device) {
            if (ClientService.this.mRemoteListener != null) {
                if (ClientService.DEBUG) {
                    Log.v(ClientService.TAG, "Pairing required for " + device);
                }
                ClientService.this.runOnUiThread(new Runnable() {

                    public void run() {
                        ClientService.this.mRemoteListener.onPairingRequired(device);
                    }
                });
                return;
            }
            if (ClientService.DEBUG) {
                Log.v(ClientService.TAG, "Ignoring pairing request while headless for " + device);
            }
            TvPreferences.saveDeviceInfo(ClientService.this.getApplicationContext(), null);
            ClientService.this.cancelPairing();
        }

        @Override
        public void onShowIme(final Device device, final EditorInfo editorInfo, final boolean z, final ExtractedText extractedText) {
            if (ClientService.DEBUG) {
                Log.v(ClientService.TAG, "Show IME " + editorInfo);
            }
            if (ClientService.this.mRemoteListener != null) {
                ClientService.this.runOnUiThread(new Runnable() {

                    public void run() {
                        ClientService.this.mRemoteListener.onShowIme(device, editorInfo, z, extractedText);
                    }
                });
            }
        }

        @Override
        public void onHideIme(final Device device) {
            if (ClientService.DEBUG) {
                Log.v(ClientService.TAG, "Hide IME");
            }
            if (ClientService.this.mRemoteListener != null) {
                ClientService.this.runOnUiThread(new Runnable() {

                    public void run() {
                        ClientService.this.mRemoteListener.onHideIme(device);
                    }
                });
            }
        }

        @Override
        public void onCompletionInfo(final Device device, final CompletionInfo[] completionInfoArr) {
            if (ClientService.this.mRemoteListener != null) {
                ClientService.this.runOnUiThread(new Runnable() {

                    public void run() {
                        ClientService.this.mRemoteListener.onCompletionInfo(device, completionInfoArr);
                    }
                });
            }
        }

        @Override
        public void onStartVoice(final Device device) {
            if (ClientService.this.mRemoteListener != null) {
                ClientService.this.runOnUiThread(new Runnable() {

                    public void run() {
                        ClientService.this.mRemoteListener.onStartVoice(device);
                    }
                });
            }
        }

        @Override
        public void onVoiceSoundLevel(final Device device, final int i) {
            if (ClientService.this.mRemoteListener != null) {
                ClientService.this.runOnUiThread(new Runnable() {

                    public void run() {
                        ClientService.this.mRemoteListener.onVoiceSoundLevel(device, i);
                    }
                });
            }
        }

        @Override
        public void onStopVoice(final Device device) {
            if (ClientService.this.mRemoteListener != null) {
                ClientService.this.runOnUiThread(new Runnable() {

                    public void run() {
                        ClientService.this.mRemoteListener.onStopVoice(device);
                    }
                });
            }
        }

        @Override
        public void onConfigureSuccess(final Device device) {
            if (ClientService.DEBUG) {
                Log.v(ClientService.TAG, "Configuration accepted for " + device);
            }
            if (ClientService.this.mRemoteListener != null) {
                ClientService.this.runOnUiThread(new Runnable() {

                    public void run() {
                        ClientService.this.mRemoteListener.onConfigureSuccess(device);
                    }
                });
            }
        }

        @Override
        public void onConfigureFailure(final Device device, final int i) {
            if (ClientService.DEBUG) {
                Log.v(ClientService.TAG, "Configuration rejected for " + device);
            }
            ClientService.this.currentStatus = Status.NO_CONNECTION;
            if (ClientService.this.mRemoteListener != null) {
                ClientService.this.runOnUiThread(new Runnable() {

                    public void run() {
                        ClientService.this.mRemoteListener.onConfigureFailure(device, i);
                    }
                });
            }
        }

        @Override
        public void onException(final Device device, final Exception exc) {
            Log.w(ClientService.TAG, "Exception for " + device, exc);
            if (ClientService.this.mRemoteListener != null) {
                ClientService.this.runOnUiThread(new Runnable() {

                    public void run() {
                        ClientService.this.mRemoteListener.onException(device, exc);
                    }
                });
                if (exc instanceof Device.UnconfiguredException) {
                    Log.e(ClientService.TAG, "We tried to use an unconfigured device, fall back to NO_CONNECTION state");
                    onConnectFailed(device);
                }
                if (exc instanceof Device.WaitTimedOutException) {
                    Log.e(ClientService.TAG, "Waiting on input timed out");
                }
            }
        }

        @Override
        public void onDeveloperStatus(final Device device, final boolean z) {
            if (ClientService.DEBUG) {
                Log.w(ClientService.TAG, "onDeveloperStatus " + z);
            }
            if (ClientService.this.mRemoteListener != null) {
                ClientService.this.runOnUiThread(new Runnable() {

                    public void run() {
                        ClientService.this.mRemoteListener.onDeveloperStatus(device, z);
                    }
                });
            }
        }

        @Override
        public void onAsset(Device device, String str, Map<String, String> map, byte[] bArr) {
            if (ClientService.DEBUG) {
                Log.w(ClientService.TAG, "onAsset " + str + " " + bArr.length);
            }
        }

        @Override
        public void onBugReportStatus(Device device, int i) {

        }

        @Override
        public void onBundle(final Device device, final int i, final Bundle bundle) {
            if (ClientService.DEBUG) {
                Log.v(ClientService.TAG, "mLocalRemoteListener:: onReceivedBundle " + i + ", bundle " + bundle);
            }
            if (ClientService.this.mRemoteListener != null) {
                ClientService.this.runOnUiThread(new Runnable() {

                    public void run() {
                        ClientService.this.mRemoteListener.onBundle(device, i, bundle);
                    }
                });
            }
        }

        @Override
        public void onCapabilities(final Device device, final CapabilitiesClass capabilitiesClass) {
            if (ClientService.DEBUG) {
                Log.v(ClientService.TAG, "mLocalRemoteListener:: onCapabilities");
            }
            ClientService.this.capabilitiesClass = capabilitiesClass;
            if (ClientService.this.mRemoteListener != null) {
                ClientService.this.runOnUiThread(new Runnable() {

                    public void run() {
                        ClientService.this.mRemoteListener.onCapabilities(device, capabilitiesClass);
                    }
                });
            }
        }
    };
    private boolean mNotificationDisplayed = false;
    private NotificationManager mNotificationManager;
    private Listener mRemoteListener = null;
    private boolean mTakingBugReport;
    private Handler mUiHandler = new Handler(Looper.getMainLooper());

    public static abstract class Listener extends Device.Listener {
        public abstract void onServiceDeath();
    }

    public enum Status {
        CONNECTED,
        CONNECTING,
        DISCONNECTED,
        NO_CONNECTION
    }

    private static int[] statuses() {
        if (ints != null) {
            return ints;
        }
        int[] iArr = new int[Status.values().length];
        try {
            iArr[Status.CONNECTED.ordinal()] = 1;
        } catch (NoSuchFieldError e) {
        }
        try {
            iArr[Status.CONNECTING.ordinal()] = 2;
        } catch (NoSuchFieldError e2) {
        }
        try {
            iArr[Status.DISCONNECTED.ordinal()] = 3;
        } catch (NoSuchFieldError e3) {
        }
        try {
            iArr[Status.NO_CONNECTION.ordinal()] = 4;
        } catch (NoSuchFieldError e4) {
        }
        ints = iArr;
        return iArr;
    }

    public class LocalBinder extends Binder {
        public LocalBinder() {
        }

        public ClientService getService() {
            return ClientService.this;
        }
    }

    private class OnDestroyTask extends AsyncTask<Void, Void, Void> {
        OnDestroyTask(ClientService clientService, OnDestroyTask onDestroyTask) {
            this();
        }

        private OnDestroyTask() {
        }


        public Void doInBackground(Void... voidArr) {
            ClientService.this.declareTVReachableCapability(false);
            return null;
        }


        public void onPostExecute(Void r4) {
        }
    }

    public void onCreate() {
        super.onCreate();
        if (DEBUG) {
            Log.i(TAG, "Creating Virtual Remote Client Service");
        }
        this.handlerThread = new HandlerThread("AtvRemote.ClntLstnrSrvc.Background");
        this.handlerThread.start();
        this.handler = new Handler(this.handlerThread.getLooper()) {

            public void handleMessage(Message message) {
                switch (message.what) {
                    case 1:
                        if (ClientService.DEBUG) {
                            Log.v(ClientService.TAG, "Initializing");
                        }
                        ClientService.this.registerGoogleApiClient();
                        return;
                    case 2:
                        if (ClientService.DEBUG) {
                            Log.v(ClientService.TAG, "Start client");
                        }
                        ClientService.this.startClient();
                        return;
                    default:
                        return;
                }
            }
        };
        this.handler.sendEmptyMessage(1);
        this.mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (TvPreferences.RETAIN_SERVICE_INSTANCE) {
            IntentFilter intentFilter = new IntentFilter();
            registerReceiver(this.mKillReceiver, intentFilter);
        }
    }

    public int onStartCommand(Intent intent, int i, int i2) {
        if (DEBUG) {
            Log.v(TAG, "onStartCommand " + intent + " " + i);
        }
        boolean z = false;
        if (intent != null) {
            String stringExtra = intent.getStringExtra("com.remote.tv.remote.tv.remote.bug_report");
            if (TextUtils.equals("cancel", stringExtra)) {
                if (DEBUG) {
                    Log.v(TAG, "Cancelling bug report");
                }
                z = true;
            } else if (TextUtils.equals("send", stringExtra)) {
                z = true;
            }
        }
        if (!z && (this.currentStatus == Status.NO_CONNECTION || this.currentStatus == Status.DISCONNECTED)) {
            this.currentStatus = Status.CONNECTING;
        }
        this.handler.sendEmptyMessage(2);
        return Service.START_STICKY;
    }

    public void onDestroy() {
        if (DEBUG) {
            Log.d(TAG, "onDestroy");
        }
        if (TvPreferences.RETAIN_SERVICE_INSTANCE) {
            unregisterReceiver(this.mKillReceiver);
            dismissNotification();
        }
        if (this.mRemoteListener != null) {
            runOnUiThread(new Runnable() {

                public void run() {
                    ClientService.this.mRemoteListener.onServiceDeath();
                }
            });
        }
        disconnect();
        new OnDestroyTask(this, null).execute(new Void[0]);
        this.handlerThread.quit();
        super.onDestroy();
    }

    public IBinder onBind(Intent intent) {
        return this.mBinder;
    }

    public boolean onUnbind(Intent intent) {
        if (!TvPreferences.RETAIN_SERVICE_INSTANCE) {
            disconnect();
        }
        if (!DEBUG) {
            return true;
        }
        Log.d(TAG, "onUnbind Service reports status: " + this.currentStatus);
        return true;
    }

    public void disconnect() {
        if (this.device != null) {
            if (DEBUG) {
                Log.v(TAG, "disconnect");
            }
            this.device.disconnect();
            this.connectionInfo = null;
            this.device = null;
            this.capabilitiesClass = null;
        }
    }

    public void dismissNotification() {
        stopForeground(true);
        this.mNotificationDisplayed = false;
    }

    public void displayNotification() {
        if (TvPreferences.RETAIN_SERVICE_INSTANCE) {
            this.mNotificationDisplayed = true;
        }
    }

    public Status getStatus() {
        return this.currentStatus;
    }

    public void interactive(boolean z) {
        if (this.device == null || !this.device.isConnected()) {
            Log.w(TAG, "Not connected, cannot send interactive " + z);
            return;
        }
        if (DEBUG) {
            Log.i(TAG, "interactive " + z);
        }
        this.device.isInteractive(z);
    }

    @Override
    public void sendKeyEvent(int i, int i2) {
        if (this.device == null || !this.device.isConnected()) {
            Log.w(TAG, "Not connected, cannot send key event " + i + " " + i2);
            return;
        }
        if (DEBUG) {
            Log.i(TAG, "sendKeyEvent " + i + " " + i2);
        }
        this.device.sendKeyEvent(i, i2);
    }

    @Override
    public void startVoice() {
        if (this.device == null || !this.device.isConnected()) {
            Log.w(TAG, "Not connected, cannot send start voice");
            return;
        }
        if (DEBUG) {
            Log.i(TAG, "startVoice");
        }
        this.device.startVoice();
    }

    @Override
    public void stopVoice() {
        if (this.device == null || !this.device.isConnected()) {
            Log.w(TAG, "Not connected, cannot send stop voice");
            return;
        }
        if (DEBUG) {
            Log.i(TAG, "stopVoice");
        }
        this.device.stopVoice();
    }

    @Override
    public boolean performEditorAction(int i) {
        if (this.device == null || !this.device.isConnected()) {
            Log.w(TAG, "Not connected, cannot send perform editor action");
            return false;
        }
        if (DEBUG) {
            Log.i(TAG, "performEditorAction " + i);
        }
        this.device.performEditorAction(i);
        return true;
    }

    @Override
    public boolean setComposingText(CharSequence charSequence, int i) {
        if (this.device == null || !this.device.isConnected()) {
            Log.w(TAG, "Not connected, cannot send set composing text");
            return false;
        }
        if (DEBUG) {
            Log.i(TAG, "setComposingText " + charSequence + " " + i);
        }
        this.device.setComposingText(charSequence, i);
        return true;
    }

    @Override
    public boolean commitText(CharSequence charSequence, int i) {
        if (this.device == null || !this.device.isConnected()) {
            Log.w(TAG, "Not connected, cannot send commit text");
            return false;
        }
        if (DEBUG) {
            Log.i(TAG, "commitText " + charSequence + " " + i);
        }
        this.device.commitText(charSequence, i);
        return true;
    }

    @Override
    public boolean deleteSurroundingText(int i, int i2) {
        if (this.device == null || !this.device.isConnected()) {
            Log.w(TAG, "Not connected, cannot send delete surrounding text");
            return false;
        }
        if (DEBUG) {
            Log.i(TAG, "deleteSurroundingText " + i + " " + i2);
        }
        this.device.deleteSurroundingText(i, i2);
        return true;
    }

    @Override
    public CharSequence getTextBeforeCursor(int i, int i2) {
        if (this.device == null || !this.device.isConnected()) {
            Log.w(TAG, "Not connected, cannot getTextBeforeCursor");
            return null;
        }
        if (DEBUG) {
            Log.i(TAG, "getTextBeforeCursor " + i + " " + i2);
        }
        return this.device.getTextBeforeCursor(i, i2);
    }

    @Override
    public CharSequence getTextAfterCursor(int i, int i2) {
        if (this.device == null || !this.device.isConnected()) {
            Log.w(TAG, "Not connected, cannot getTextAfterCursor");
            return null;
        }
        if (DEBUG) {
            Log.i(TAG, "getTextAfterCursor " + i + " " + i2);
        }
        return this.device.getTextAfterCursor(i, i2);
    }

    @Override
    public int getCursorCapsMode(int i) {
        if (this.device == null || !this.device.isConnected()) {
            Log.w(TAG, "Not connected, cannot getCursorCapsMode");
            return 0;
        }
        if (DEBUG) {
            Log.i(TAG, "getCursorCapsMode " + i);
        }
        return this.device.getCursorCapsMode(i);
    }

    @Override
    public CharSequence getSelectedText(int i) {
        if (this.device == null || !this.device.isConnected()) {
            Log.w(TAG, "Not connected, cannot getSelectedText");
            return null;
        }
        if (DEBUG) {
            Log.i(TAG, "getSelectedText " + i);
        }
        return this.device.getSelectedText(i);
    }

    @Override
    public ExtractedText getExtractedText(ExtractedTextRequest extractedTextRequest, int i) {
        if (this.device == null || !this.device.isConnected()) {
            Log.w(TAG, "Not connected, cannot getExtractedText");
            return null;
        }
        if (DEBUG) {
            Log.i(TAG, "getExtractedText " + extractedTextRequest + " " + i);
        }
        return this.device.getExtractedText(extractedTextRequest, i);
    }

    @Override
    public boolean beginBatchEdit() {
        if (this.device == null || !this.device.isConnected()) {
            Log.w(TAG, "Not connected, cannot send beginBatchEdit");
            return false;
        }
        if (DEBUG) {
            Log.i(TAG, "beginBatchEdit");
        }
        this.device.beginBatchEdit();
        return true;
    }

    @Override
    public boolean setComposingRegion(int i, int i2) {
        if (this.device == null || !this.device.isConnected()) {
            Log.w(TAG, "Not connected, cannot send setComposingRegion");
            return false;
        }
        if (DEBUG) {
            Log.i(TAG, String.format("setComposingRegion %d %d", Integer.valueOf(i), Integer.valueOf(i2)));
        }
        this.device.setComposingRegion(i, i2);
        return true;
    }

    @Override
    public boolean finishComposingText() {
        if (this.device == null || !this.device.isConnected()) {
            Log.w(TAG, "Not connected, cannot send finishComposingText");
            return false;
        }
        if (DEBUG) {
            Log.i(TAG, "finishComposingText");
        }
        this.device.finishComposingText();
        return true;
    }

    @Override
    public boolean commitCompletion(CompletionInfo completionInfo) {
        if (this.device == null || !this.device.isConnected()) {
            Log.w(TAG, "Not connected, cannot send commitCompletion");
            return false;
        }
        if (DEBUG) {
            Log.i(TAG, String.format("commitCompletion %s", completionInfo));
        }
        this.device.commitCompletion(completionInfo);
        return true;
    }

    @Override
    public boolean setSelection(int i, int i2) {
        if (this.device == null || !this.device.isConnected()) {
            Log.w(TAG, "Not connected, cannot send setSelection");
            return false;
        }
        if (DEBUG) {
            Log.i(TAG, String.format("setSelection %d %d", Integer.valueOf(i), Integer.valueOf(i2)));
        }
        this.device.setSelection(i, i2);
        return true;
    }

    @Override
    public boolean endBatchEdit() {
        if (this.device == null || !this.device.isConnected()) {
            Log.w(TAG, "Not connected, cannot send endBatchEdit");
            return false;
        }
        if (DEBUG) {
            Log.i(TAG, "endBatchEdit");
        }
        this.device.endBatchEdit();
        return true;
    }

    @Override
    public boolean requestCursorUpdates(int i) {
        if (this.device == null || !this.device.isConnected()) {
            Log.w(TAG, "Not connected, cannot send requestCursorUpdates");
            return false;
        }
        if (DEBUG) {
            Log.i(TAG, String.format("requestCursorUpdates %d", Integer.valueOf(i)));
        }
        this.device.requestCursorUpdates(i);
        return true;
    }

    @Override
    public void takeBugReport() {
        if (this.device == null || !this.device.isConnected()) {
            Log.w(TAG, "Not connected, cannot send takeBugReport");
            return;
        }
        if (DEBUG) {
            Log.i(TAG, "takeBugReport");
        }
        this.device.takeBugReport();
    }

    @Override
    public void sendBundle(int i, Bundle bundle) {
        if (this.device == null || !this.device.isConnected()) {
            Log.w(TAG, "Not connected, cannot send bundle");
            return;
        }
        if (DEBUG) {
            Log.i(TAG, "ClientListenerService: transmitBundle");
        }
        this.device.sendBundle(i, bundle);
    }

    @Override
    public boolean isRecording() {
        if (this.device == null || !this.device.isConnected()) {
            return false;
        }
        return this.device.isVoiceRecording();
    }

    public void setPairingSecret(String str) {
        if (this.device != null) {
            this.device.setPairingSecret(str);
        } else {
            Log.w(TAG, "Not connected, cannot set pairing secret");
        }
    }

    public void cancelPairing() {
        if (this.device != null) {
            this.device.cancelPairing();
        } else {
            Log.w(TAG, "Not connected, cannot cancel pairing");
        }
    }

    public int getControllerNumber() {
        if (this.device != null) {
            return this.device.getControllerNumber();
        }
        return 0;
    }

    public String getDescriptor() {
        if (this.device != null) {
            return this.device.getDescriptor();
        }
        return null;
    }

    public void setRemoteListener(Listener listener) {
        this.mRemoteListener = listener;
    }

    public static boolean isRunning(Context context) {

        for (ActivityManager.RunningServiceInfo runningServiceInfo : ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getRunningServices(250)) {
            if (ClientService.class.getName().equals(runningServiceInfo.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    private void startClient() {
        DevicesInfoUtil devicesInfoUtil = TvPreferences.getDeviceInfo(getApplicationContext());
        if (this.device != null) {
            if (this.device.isConnected()) {
                if (this.device.isConfigured()) {
                    if (this.connectionInfo == null || !this.connectionInfo.equals(devicesInfoUtil)) {
                        if (DEBUG) {
                            Log.v(TAG, "startClient(): disconnecting from another device " + this.connectionInfo);
                        }
                        disconnect();
                    } else {
                        if (DEBUG) {
                            Log.v(TAG, "startClient(): already connected to " + devicesInfoUtil);
                        }
                        if (this.mRemoteListener != null) {
                            this.mRemoteListener.onConfigureSuccess(this.device);
                        }
                        this.currentStatus = Status.CONNECTED;
                        return;
                    }
                } else if (this.currentStatus != Status.CONNECTING) {
                    if (DEBUG) {
                        Log.v(TAG, "startClient(): device if not configured and not connecting.");
                    }
                    disconnect();
                } else if (DEBUG) {
                    Log.v(TAG, "startClient(): device if not configured but connecting.");
                    return;
                } else {
                    return;
                }
            } else if (this.currentStatus != Status.CONNECTING) {
                if (DEBUG) {
                    Log.v(TAG, "startClient(): device is not connected.");
                }
                disconnect();
            } else if (DEBUG) {
                Log.v(TAG, "startClient(): device is still connecting");
                return;
            } else {
                return;
            }
        }
        this.currentStatus = Status.CONNECTING;
        this.connectionInfo = devicesInfoUtil;
        if (this.connectionInfo != null) {
            if (DEBUG) {
                Log.d(TAG, "Connecting to " + this.connectionInfo);
            }
            this.device = Device.from(getApplicationContext(), this.connectionInfo, this.localRemoteListener, this.mUiHandler);
            if (DEBUG) {
                Log.d(TAG, "Client is connected (" + this.device.isConnected() + ") to " + this.connectionInfo);
            }
        } else if (DEBUG) {
            Log.d(TAG, "No connection info " + this.connectionInfo);
        }
    }

    private void updateNotification() {
        if (this.mNotificationDisplayed) {
            displayNotification();
        }
    }

    private void registerGoogleApiClient() {
    }

    private void declareTVReachableCapability(boolean z) {
    }

    private void runOnUiThread(Runnable runnable) {
        this.mUiHandler.post(runnable);
    }

}
