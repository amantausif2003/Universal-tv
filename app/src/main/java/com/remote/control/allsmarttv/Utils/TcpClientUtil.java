package com.remote.control.allsmarttv.Utils;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.ExtractedText;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Map;

import javax.net.ssl.HandshakeCompletedEvent;
import javax.net.ssl.HandshakeCompletedListener;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;

public class TcpClientUtil {

    private static final String SSL_CONTEXT = "TLS";
    private static final String TAG = "AtvRemote.TcpClient";
    private static final String WIFI_LOCK = "AndroidTVRemote";
    private final InetAddress inetAddress;
    private final AssetHandler assetHandler;
    private final Handler.Callback mCallbackCallback = new Handler.Callback() {

        public boolean handleMessage(Message message) {
            switch (message.what) {
                case 1:
                    TcpClientUtil.this.listener.onConnecting();
                    return true;
                case 2:
                    TcpClientUtil.this.listener.onConnected();
                    return true;
                case 3:
                    TcpClientUtil.this.listener.onDisconnected();
                    return true;
                case 4:
                    TcpClientUtil.this.listener.onSslHandshakeCompleted();
                    return true;
                case 5:
                    int parse = TcpClientUtil.this.mParser.parse((byte[]) message.obj);
                    if (parse >= 0) {
                        return true;
                    }
                    TcpClientUtil.this.listener.onBadMessage(parse);
                    return true;
                case 6:
                    TcpClientUtil.this.listener.onBadMessage(message.arg1);
                    return true;
                case 7:
                    TcpClientUtil.this.listener.onSslHandshakeFailed((Exception) message.obj);
                    return true;
                case 8:
                    TcpClientUtil.this.listener.onConnectFailed((Exception) message.obj);
                    return true;
                case 9:
                    TcpClientUtil.this.listener.onException((Exception) message.obj);
                    return true;
                default:
                    return true;
            }
        }
    };
    private final Handler mCallbackHandler;
    private final ClientListener mCommandListener = new ClientListener() {

        @Override
        public void badPacket(String str) {
        }

        @Override
        public void badPacketVersion(byte b) {
            TcpClientUtil.this.mCallbackHandler.post(new Runnable() {
                final  byte val$receivedVersion;

                {
                    this.val$receivedVersion = (byte) b;
                }

                public void run() {
                    if (this.val$receivedVersion >= 1) {
                        TcpClientUtil.this.listener.onReceivePacketVersionTooLow(this.val$receivedVersion);
                    } else {
                        TcpClientUtil.this.listener.onReceivePacketVersionTooHigh(this.val$receivedVersion);
                    }
                }
            });
        }

        @Override
        public void configureFailure(final int i) {
            TcpClientUtil.this.mCallbackHandler.post(new Runnable() {

                public void run() {
                    TcpClientUtil.this.listener.onReceiveConfigureFailure(i);
                }
            });
        }

        @Override
        public void configureSuccess(final int i, final String str, final BuildInfo buildInfo) {
            TcpClientUtil.this.mCallbackHandler.post(new Runnable() {

                public void run() {
                    TcpClientUtil.this.listener.onReceiveConfigureSuccess(i, str, buildInfo);
                }
            });
        }

        @Override
        public void hideIme() {
            TcpClientUtil.this.mCallbackHandler.post(new Runnable() {

                public void run() {
                    TcpClientUtil.this.listener.onReceiveHideIme();
                }
            });
        }

        @Override
        public void onAssetChunk(String str, int i, int i2, byte[] bArr) {
            TcpClientUtil.this.assetHandler.onAssetChunk(str, i, i2, bArr);
        }

        @Override
        public void onAssetFooter(String str, int i) {
            TcpClientUtil.this.assetHandler.onAssetFooter(str, i);
        }

        @Override
        public void onAssetHeader(String str, String str2, int i, int i2, Map<String, String> map) {
            TcpClientUtil.this.assetHandler.onAssetHeader(str, str2, i, i2, map);
        }

        @Override
        public void onBugReportStatus(final int i) {
            TcpClientUtil.this.mCallbackHandler.post(new Runnable() {

                public void run() {
                    TcpClientUtil.this.listener.onBugReportStatus(i);
                }
            });
        }

        @Override
        public void onCapabilities(final CapabilitiesClass capabilitiesClass) {
            TcpClientUtil.this.mCallbackHandler.post(new Runnable() {

                public void run() {
                    TcpClientUtil.this.listener.onCapabilities(capabilitiesClass);
                }
            });
        }

        @Override
        public void onCompletionInfo(final CompletionInfo[] completionInfoArr) {
            TcpClientUtil.this.mCallbackHandler.post(new Runnable() {

                public void run() {
                    TcpClientUtil.this.listener.onReceiveCompletionInfo(completionInfoArr);
                }
            });
        }

        @Override
        public void onDeveloperStatus(final boolean z) {
            TcpClientUtil.this.mCallbackHandler.post(new Runnable() {

                public void run() {
                    TcpClientUtil.this.listener.onDeveloperStatus(z);
                }
            });
        }

        @Override
        public void onPing() {
            TcpClientUtil.this.mPingMissed = 0;
            TcpClientUtil.this.sendMessage(PackEncoderClass.ENCODED_PACKET_PONG);
        }

        @Override
        public void onReceivedBundle(final int i, final Bundle bundle) {
            TcpClientUtil.this.mCallbackHandler.post(new Runnable() {

                public void run() {
                    TcpClientUtil.this.listener.receivedBundle(i, bundle);
                }
            });
        }

        @Override
        public void onReplyGetCursorCapsMode(long j, int i) {
            TcpClientUtil.this.listener.onResponse(j, Integer.valueOf(i));
        }

        @Override
        public void onReplyGetExtractedText(long j, ExtractedText extractedText) {
            TcpClientUtil.this.listener.onResponse(j, extractedText);
        }

        @Override
        public void onReplyGetSelectedText(long j, CharSequence charSequence) {
            TcpClientUtil.this.listener.onResponse(j, charSequence);
        }

        @Override
        public void onReplyGetTextAfterCursor(long j, CharSequence charSequence) {
            TcpClientUtil.this.listener.onResponse(j, charSequence);
        }

        @Override
        public void onReplyGetTextBeforeCursor(long j, CharSequence charSequence) {
            TcpClientUtil.this.listener.onResponse(j, charSequence);
        }

        @Override
        public void packetVersionTooHigh(byte b) {
            TcpClientUtil.this.mCallbackHandler.post(new Runnable() {
                final  byte val$target;

                {
                    this.val$target = (byte) b;
                }

                public void run() {
                    TcpClientUtil.this.listener.onReceivePacketVersionTooHigh(this.val$target);
                }
            });
        }

        @Override
        public void packetVersionTooLow(byte b) {
            TcpClientUtil.this.mCallbackHandler.post(new Runnable() {
                final  byte val$target;

                {
                    this.val$target = (byte) b;
                }

                public void run() {
                    TcpClientUtil.this.listener.onReceivePacketVersionTooLow(this.val$target);
                }
            });
        }

        @Override
        public void showIme(final EditorInfo editorInfo, final boolean z, final ExtractedText extractedText, final boolean z2) {
            TcpClientUtil.this.mCallbackHandler.post(new Runnable() {

                public void run() {
                    TcpClientUtil.this.listener.onReceiveShowIme(editorInfo, z, extractedText, z2);
                }
            });
        }

        @Override
        public void startVoice() {
            TcpClientUtil.this.mCallbackHandler.post(new Runnable() {

                public void run() {
                    TcpClientUtil.this.listener.onReceiveStartVoice();
                }
            });
        }

        @Override
        public void stopVoice() {
            TcpClientUtil.this.mCallbackHandler.post(new Runnable() {

                public void run() {
                    TcpClientUtil.this.listener.onReceiveStopVoice();
                }
            });
        }
    };
    private final Context mContext;
    private InputStream inputStream;
    private final KeyStoreManager keyStoreManager;
    private final Client.Listener listener;
    private Thread thread;
    private final Handler.Callback callback = new Handler.Callback() {

        public boolean handleMessage(Message message) {
            switch (message.what) {
                case 1:
                    TcpClientUtil.this.connectImpl();
                    break;
                case 2:
                    TcpClientUtil.this.disconnectImpl(1 == message.arg1);
                    break;
                case 3:
                    try {
                        TcpClientUtil.this.mOutputStream.write((byte[]) message.obj);
                        TcpClientUtil.this.mOutputStream.flush();
                        break;
                    } catch (IOException e) {
                        TcpClientUtil.this.mCallbackHandler.sendMessage(TcpClientUtil.this.mCallbackHandler.obtainMessage(9, e));
                        break;
                    }
                case 4:
                    if (TcpClientUtil.access(TcpClientUtil.this) > 2) {
                        TcpClientUtil.this.disconnectImpl(true);
                        break;
                    } else {
                        TcpClientUtil.this.reschedulePingTimeout();
                        break;
                    }
            }
            return true;
        }
    };
    private final Handler mNetHandler;
    private final HandlerThread mNetThread;
    private OutputStream mOutputStream;
    private final ClientPackParser mParser;
    private int mPingMissed = 0;
    private final int mPort;
//    private final BroadcastReceiver mScreenReceiver = new BroadcastReceiver() {
//
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            if (!"android.intent.action.SCREEN_ON".equals(action)) {
//                if ("android.intent.action.SCREEN_OFF".equals(action)) {
//                    if (TcpClient.this.isConnected() || TcpClient.this.mWifiLock.isHeld()) {
//                        try {
//                            TcpClient.this.mWifiLock.release();
//                        }
//                        catch (Throwable throwable){}
//                    }
//                }
//            } else if (TcpClient.this.isConnected() && !TcpClient.this.mWifiLock.isHeld()) {
//                TcpClient.this.mWifiLock.acquire();
//            }
//        }
//    };
    private Socket mSocket;
    private final Runnable mSocketListener = new Runnable() {

        public void run() {
//            TcpClient.this.mWifiLock.acquire();
//            IntentFilter intentFilter = new IntentFilter("android.intent.action.SCREEN_ON");
//            intentFilter.addAction("android.intent.action.SCREEN_OFF");
//            TcpClient.this.mContext.registerReceiver(TcpClient.this.mScreenReceiver, intentFilter);
            byte[] bArr = new byte[65536];
            while (true) {
                if (TcpClientUtil.this.mSocket == null || !TcpClientUtil.this.mSocket.isConnected()) {
                    break;
                }
                try {
                    int readPacket = PacketParser.readPacket(TcpClientUtil.this.inputStream, bArr);
                    if (-5 == readPacket) {
                        TcpClientUtil.this.disconnect();
                        break;
                    } else if (readPacket >= 0) {
                        byte[] bArr2 = new byte[readPacket];
                        System.arraycopy(bArr, 0, bArr2, 0, readPacket);
                        if (TcpClientUtil.this.mParser.parse(bArr2) < 0) {
                            TcpClientUtil.this.mCallbackHandler.sendMessage(TcpClientUtil.this.mCallbackHandler.obtainMessage(6, readPacket, 0));
                        }
                    } else {
                        TcpClientUtil.this.mCallbackHandler.sendMessage(TcpClientUtil.this.mCallbackHandler.obtainMessage(6, readPacket, 0));
                    }
                } catch (Exception e) {
                    Log.e(TcpClientUtil.TAG, "Packet parser threw an exception", e);
                    TcpClientUtil.this.disconnect();
                }
            }
//            TcpClient.this.mContext.unregisterReceiver(TcpClient.this.mScreenReceiver);
//            if (TcpClient.this.mWifiLock.isHeld()) {
//                TcpClient.this.mWifiLock.release();
//            }
            TcpClientUtil.this.thread = null;
        }
    };
    private final String mTo;
//    private final WifiManager.WifiLock mWifiLock;

    public TcpClientUtil(Context context, InetAddress inetAddress, int i, Client.Listener listener, KeyStoreManager keyStoreManager, Handler handler) {
        this.mContext = context;
        this.inetAddress = inetAddress;
        this.mTo = this.inetAddress.getHostAddress();
        this.mPort = i;
        this.listener = listener;
//        this.mWifiLock = ((WifiManager) this.mContext.getSystemService(Context.WIFI_SERVICE)).createWifiLock(1, WIFI_LOCK);
        this.mParser = new ClientPackParser(this.mCommandListener);
        this.mNetThread = new HandlerThread("ATVRemote.Network");
        this.mNetThread.start();
        this.mNetHandler = new Handler(this.mNetThread.getLooper(), this.callback);
        this.mCallbackHandler = new Handler(handler.getLooper(), this.mCallbackCallback);
        this.keyStoreManager = keyStoreManager;
        this.assetHandler = new AssetHandler(this.mCallbackHandler, this.listener);
    }

    static  int access(TcpClientUtil tcpClientUtil) {
        int i = tcpClientUtil.mPingMissed + 1;
        tcpClientUtil.mPingMissed = i;
        return i;
    }


    private void connectImpl() {
        try {
            KeyManager[] keyManagers = this.keyStoreManager.getKeyManagers();
            TrustManager[] trustManagers = this.keyStoreManager.getTrustManagers();
            if (keyManagers.length != 0) {
                SSLContext instance = SSLContext.getInstance(SSL_CONTEXT);
                instance.init(keyManagers, trustManagers, new SecureRandom());
                SSLSocket sSLSocket = (SSLSocket) instance.getSocketFactory().createSocket(this.inetAddress, this.mPort);
                sSLSocket.setNeedClientAuth(true);
                sSLSocket.setUseClientMode(true);
                sSLSocket.setKeepAlive(true);
                sSLSocket.setTcpNoDelay(true);
                sSLSocket.addHandshakeCompletedListener(new HandshakeCompletedListener() {

                    public void handshakeCompleted(HandshakeCompletedEvent handshakeCompletedEvent) {
                        TcpClientUtil.this.onSslCompleted();
                    }
                });
                sSLSocket.startHandshake();
                this.mSocket = sSLSocket;
                try {
                    this.inputStream = this.mSocket.getInputStream();
                    this.mOutputStream = this.mSocket.getOutputStream();
                    this.thread = new Thread(this.mSocketListener);
                    this.thread.start();
                    this.mPingMissed = 0;
                    this.mCallbackHandler.sendEmptyMessage(2);
                } catch (Exception e) {
                    onConnectFailed(e);
                }
            } else {
                throw new IllegalStateException("No key managers");
            }
        } catch (SSLException e2) {
            onSslFailed(e2);
        } catch (GeneralSecurityException e3) {
            onConnectFailed(e3);
        } catch (IOException e4) {
            onConnectFailed(e4);
        } catch (RuntimeException e5) {
            onConnectFailed(e5);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void disconnectImpl(boolean z) {
        this.mNetHandler.removeCallbacksAndMessages(null);
        this.mNetThread.quit();
        if (this.inputStream != null) {
            try {
                this.inputStream.close();
            } catch (IOException e) {
            }
            this.inputStream = null;
        }
        if (this.mOutputStream != null) {
            try {
                this.mOutputStream.close();
            } catch (IOException e2) {
            }
            this.mOutputStream = null;
        }
        if (this.mSocket != null) {
            try {
                this.mSocket.close();
            } catch (IOException e3) {
            }
            this.mSocket = null;
        }
        if (z && !this.mCallbackHandler.hasMessages(3)) {
            this.mCallbackHandler.sendEmptyMessage(3);
        }
    }

    private void onConnectFailed(Exception exc) {
        Object[] objArr = new Object[1];
        objArr[0] = this.mTo == null ? "NULL" : this.mTo;
        Log.e(TAG, String.format("Failed to connect to %s", objArr), exc);
        disconnectImpl(false);
        this.mCallbackHandler.sendMessage(this.mCallbackHandler.obtainMessage(8, exc));
    }

    private void onSslCompleted() {
        this.mCallbackHandler.sendEmptyMessage(4);
    }

    private void onSslFailed(Exception exc) {
        Object[] objArr = new Object[2];
        objArr[0] = this.mTo == null ? "NULL" : this.mTo;
        objArr[1] = exc.getMessage();
        Log.e(TAG, String.format("SSL Handshake with %s failed: %s", objArr));
        disconnectImpl(false);
        this.mCallbackHandler.sendMessage(this.mCallbackHandler.obtainMessage(7, exc));
    }

    private void reschedulePingTimeout() {
        this.mNetHandler.removeMessages(4);
        this.mNetHandler.sendEmptyMessageDelayed(4, 15000);
    }

    public void connect(boolean z) {
        if (z) {
            this.mCallbackHandler.sendEmptyMessage(1);
        }
        this.mNetHandler.sendEmptyMessage(1);
    }

    public void disconnect() {
        this.mNetHandler.removeMessages(1);
        if (!this.mNetHandler.hasMessages(2)) {
            this.mNetHandler.sendMessage(this.mNetHandler.obtainMessage(2, 1, 0));
        }
    }

    public boolean isConnected() {
        return this.mSocket != null && this.mSocket.isConnected();
    }

    public void sendMessage(byte[] bArr) {
        if (bArr.length <= 65536) {
            reschedulePingTimeout();
            this.mNetHandler.sendMessage(this.mNetHandler.obtainMessage(3, bArr));
            return;
        }
        Log.e(TAG, String.format("Packet size %d exceeds host receive buffer size %d, dropping.", Integer.valueOf(bArr.length), 65536));
    }
}
