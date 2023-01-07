package com.remote.control.allsmarttv.utils;

import android.content.Context;
import android.os.Build;
import android.os.Handler;

import java.net.InetAddress;
import java.net.UnknownHostException;

public final class TcpDeviceImpl extends AbsDeviceImpl {
    private static final String REMOTE_NAME = (Build.MANUFACTURER + " " + Build.MODEL);
    private TcpClientUtil mClient;
    private KeyStoreManager mKeyStoreManager;
    private PairingClientUtil mPairingClientUtil;
    private String mServiceName;
    private KeyStoreLoadTask mTask = new KeyStoreLoadTask() {
        /* class TcpDeviceImpl.AnonymousClass1 */

        /* access modifiers changed from: protected */
        public void onPostExecute(KeyStoreManager keyStoreManager) {
            if (!isCancelled()) {
                TcpDeviceImpl.this.mKeyStoreManager = keyStoreManager;
                TcpDeviceImpl.this.connectImpl(TcpDeviceImpl.this.mListener, true);
                TcpDeviceImpl.this.mTask = null;
            }
        }
    };

    TcpDeviceImpl(Context context, DevicesInfoUtil devicesInfoUtil, Listener listener, Handler handler) {
        super(context, devicesInfoUtil, listener, handler);
        String className = new Throwable().fillInStackTrace().getStackTrace()[1].getClassName();
        this.mServiceName = className.substring(0, className.lastIndexOf(46));
        this.mTask.execute(this.mContext);
    }

    private void connectImpl(Listener listener, boolean z) {
        this.mClient = new TcpClientUtil(this.mContext, getHost(), getPort(), new AbsClient(this, listener, this.mSpeechInput, this.mEncoder) {

            @Override
            public void onSslHandshakeCompleted() {
            }

            @Override
            public void onSslHandshakeFailed(Exception exc) {
                TcpDeviceImpl.this.mClient = null;
                TcpDeviceImpl.this.mConfiguration = null;
                TcpDeviceImpl.this.mConfigured = false;
                TcpDeviceImpl.this.mControllerNumber = 0;
                TcpDeviceImpl.this.mDescriptor = null;
                PairingClientUtil.PairingClientListener r4 = new PairingClientUtil.PairingClientListener() {

                    @Override
                    public void onPairingResult(PairingClientUtil pairingClient, PairingClientUtil.PairingResult pairingResult) {
                        if (PairingClientUtil.PairingResult.SUCCEEDED != pairingResult) {
                            TcpDeviceImpl.this.runOnUiThread(new Runnable() {

                                public void run() {
                                    TcpDeviceImpl.this.mListener.onConnectFailed(TcpDeviceImpl.this);
                                }
                            });
                        } else {
                            TcpDeviceImpl.this.connectImpl(TcpDeviceImpl.this.mListener, false);
                        }
                        TcpDeviceImpl.this.mPairingClientUtil = null;
                    }

                    @Override
                    public void onPairingStarted(PairingClientUtil pairingClient) {
                        TcpDeviceImpl.this.runOnUiThread(new Runnable() {

                            public void run() {
                                TcpDeviceImpl.this.mListener.onPairingRequired(TcpDeviceImpl.this);
                            }
                        });
                    }
                };
                TcpDeviceImpl.this.mPairingClientUtil = new PairingClientUtil(TcpDeviceImpl.this.getHost(), TcpDeviceImpl.this.getPort() + 1, TcpDeviceImpl.this.mKeyStoreManager, r4, TcpDeviceImpl.this.mServiceName, TcpDeviceImpl.REMOTE_NAME);
                TcpDeviceImpl.this.mPairingClientUtil.start();
            }
        }, this.mKeyStoreManager, this.mCallbackHandler);
        this.mClient.connect(z);
    }

    private InetAddress getHost() {
        try {
            return InetAddress.getByName(this.mDevicesInfoUtil.getUri().getHost());
        } catch (UnknownHostException e) {
            return null;
        }
    }

    private int getPort() {
        return this.mDevicesInfoUtil.getUri().getPort();
    }

    @Override
    public void cancelPairing() {
        if (this.mPairingClientUtil != null) {
            this.mPairingClientUtil.cancel();
        }
    }

    @Override
    public void disconnect() {
        if (this.mTask != null) {
            this.mTask.cancel(true);
        }
        if (this.mClient != null) {
            this.mClient.disconnect();
            this.mClient = null;
        }
        if (this.mPairingClientUtil != null) {
            this.mPairingClientUtil.cancel();
            this.mPairingClientUtil = null;
        }
    }

    @Override
    public boolean isConnected() {
        if (this.mTask == null) {
            if (this.mClient != null) {
                return this.mPairingClientUtil != null || this.mClient.isConnected();
            }
            return false;
        }
        else
        {
            return true;
        }
    }

    @Override
    public void sendMessage(byte[] bArr) {
        if (isConnected()) {
            this.mClient.sendMessage(bArr);
        }
    }

    @Override
    public void setPairingSecret(String str) {
        if (this.mPairingClientUtil != null) {
            this.mPairingClientUtil.setSecret(str);
        }
    }
}
