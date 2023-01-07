package com.remote.control.allsmarttv.utils;

import android.content.Context;
import android.os.Handler;

public class BluetoothDeviceImp extends AbsDeviceImpl {
    private final BluetoothClient mClient;
    private final Client.Listener mLocalListener;

    public BluetoothDeviceImp(Context context, DevicesInfoUtil devicesInfoUtil, Listener listener, Handler handler) {
        super(context, devicesInfoUtil, listener, handler);
        this.mLocalListener = new AbsClient(this, listener, this.mSpeechInput, this.mEncoder) {

            @Override
            public void onSslHandshakeCompleted() {
            }

            @Override
            public void onSslHandshakeFailed(Exception exc) {
            }
        };
        this.mClient = new BluetoothClient(devicesInfoUtil.getUri().getAuthority(), this.mLocalListener, handler);
        this.mClient.connect();
    }

    @Override
    public void cancelPairing() {
    }

    @Override
    public void disconnect() {
        this.mClient.disconnect();
    }

    @Override
    public boolean isConnected() {
        return this.mClient.isConnected();
    }

    public void sendMessage(byte[] bArr) {
        this.mClient.sendMessage(bArr);
    }

    @Override
    public void setPairingSecret(String str) {
    }
}
