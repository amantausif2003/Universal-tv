package com.remote.control.allsmarttv.Utils;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.ExtractedText;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

public class BluetoothClient {
    private static final String TAG = "AtvRemote.BtClient";
    private final AssetHandler mAssetHandler;
    private final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private final Handler mCallbackHandler;
    private ClientThread mClientThread;
    private final ClientListener mCommandListener = new ClientListener() {

        @Override
        public void badPacket(String str) {
        }

        @Override
        public void badPacketVersion(byte b) {
            if (b >= 1) {
                BluetoothClient.this.mCallbackHandler.post(new Runnable() {
                    final byte val$receivedVersion;

                    {
                        this.val$receivedVersion = (byte) b;
                    }

                    public void run() {
                        BluetoothClient.this.mListener.onReceivePacketVersionTooLow(this.val$receivedVersion);
                    }
                });
            } else {
                BluetoothClient.this.mCallbackHandler.post(new Runnable() {
                    final byte val$receivedVersion;

                    {
                        this.val$receivedVersion = (byte) b;
                    }

                    public void run() {
                        BluetoothClient.this.mListener.onReceivePacketVersionTooHigh(this.val$receivedVersion);
                    }
                });
            }
        }

        @Override
        public void configureFailure(final int i) {
            BluetoothClient.this.mCallbackHandler.post(new Runnable() {

                public void run() {
                    BluetoothClient.this.mListener.onReceiveConfigureFailure(i);
                }
            });
        }

        @Override
        public void configureSuccess(final int i, final String str, final BuildInfo buildInfo) {
            BluetoothClient.this.mCallbackHandler.post(new Runnable() {

                public void run() {
                    BluetoothClient.this.mListener.onReceiveConfigureSuccess(i, str, buildInfo);
                }
            });
        }

        @Override
        public void hideIme() {
            BluetoothClient.this.mCallbackHandler.post(new Runnable() {

                public void run() {
                    BluetoothClient.this.mListener.onReceiveHideIme();
                }
            });
        }

        @Override
        public void onAssetChunk(String str, int i, int i2, byte[] bArr) {
            BluetoothClient.this.mAssetHandler.onAssetChunk(str, i, i2, bArr);
        }

        @Override
        public void onAssetFooter(String str, int i) {
            BluetoothClient.this.mAssetHandler.onAssetFooter(str, i);
        }

        @Override
        public void onAssetHeader(String str, String str2, int i, int i2, Map<String, String> map) {
            BluetoothClient.this.mAssetHandler.onAssetHeader(str, str2, i, i2, map);
        }

        @Override
        public void onBugReportStatus(final int i) {
            BluetoothClient.this.mCallbackHandler.post(new Runnable() {

                public void run() {
                    BluetoothClient.this.mListener.onBugReportStatus(i);
                }
            });
        }

        @Override
        public void onCapabilities(final CapabilitiesClass capabilitiesClass) {
            BluetoothClient.this.mCallbackHandler.post(new Runnable() {

                public void run() {
                    BluetoothClient.this.mListener.onCapabilities(capabilitiesClass);
                }
            });
        }

        @Override
        public void onCompletionInfo(final CompletionInfo[] completionInfoArr) {
            BluetoothClient.this.mCallbackHandler.post(new Runnable() {

                public void run() {
                    BluetoothClient.this.mListener.onReceiveCompletionInfo(completionInfoArr);
                }
            });
        }

        @Override
        public void onDeveloperStatus(final boolean z) {
            BluetoothClient.this.mCallbackHandler.post(new Runnable() {

                public void run() {
                    BluetoothClient.this.mListener.onDeveloperStatus(z);
                }
            });
        }

        @Override
        public void onPing() {
            BluetoothClient.this.sendMessage(PackEncoderClass.ENCODED_PACKET_PONG);
        }

        @Override
        public void onReceivedBundle(final int i, final Bundle bundle) {
            BluetoothClient.this.mCallbackHandler.post(new Runnable() {

                public void run() {
                    BluetoothClient.this.mListener.receivedBundle(i, bundle);
                }
            });
        }

        @Override
        public void onReplyGetCursorCapsMode(long j, int i) {
            BluetoothClient.this.mListener.onResponse(j, Integer.valueOf(i));
        }

        @Override
        public void onReplyGetExtractedText(long j, ExtractedText extractedText) {
            BluetoothClient.this.mListener.onResponse(j, extractedText);
        }

        @Override
        public void onReplyGetSelectedText(long j, CharSequence charSequence) {
            BluetoothClient.this.mListener.onResponse(j, charSequence);
        }

        @Override
        public void onReplyGetTextAfterCursor(long j, CharSequence charSequence) {
            BluetoothClient.this.mListener.onResponse(j, charSequence);
        }

        @Override
        public void onReplyGetTextBeforeCursor(long j, CharSequence charSequence) {
            BluetoothClient.this.mListener.onResponse(j, charSequence);
        }

        @Override
        public void packetVersionTooHigh(byte b) {
            BluetoothClient.this.mCallbackHandler.post(new Runnable() {
                final byte val$targetVersion;

                {
                    this.val$targetVersion = (byte) b;
                }

                public void run() {
                    BluetoothClient.this.mListener.onReceivePacketVersionTooHigh(this.val$targetVersion);
                }
            });
        }

        @Override
        public void packetVersionTooLow(byte b) {
            BluetoothClient.this.mCallbackHandler.post(new Runnable() {
                final byte val$targetVersion;

                {
                    this.val$targetVersion = (byte) b;
                }

                public void run() {
                    BluetoothClient.this.mListener.onReceivePacketVersionTooLow(this.val$targetVersion);
                }
            });
        }

        @Override
        public void showIme(final EditorInfo editorInfo, final boolean z, final ExtractedText extractedText, final boolean z2) {
            BluetoothClient.this.mCallbackHandler.post(new Runnable() {

                public void run() {
                    BluetoothClient.this.mListener.onReceiveShowIme(editorInfo, z, extractedText, z2);
                }
            });
        }

        @Override
        public void startVoice() {
            BluetoothClient.this.mCallbackHandler.post(new Runnable() {

                public void run() {
                    BluetoothClient.this.mListener.onReceiveStartVoice();
                }
            });
        }

        @Override
        public void stopVoice() {
            BluetoothClient.this.mCallbackHandler.post(new Runnable() {

                public void run() {
                    BluetoothClient.this.mListener.onReceiveStopVoice();
                }
            });
        }
    };
    private final BluetoothDevice mDevice;
    private final Client.Listener mListener;
    private final ClientPackParser mParser;


    public class ClientThread extends Thread {
        boolean mCancelling = false;
        private final BluetoothDevice mDevice;
        private InputStream mInputStream;
        private OutputStream mOutputStream;
        private BluetoothSocket mSocket;

        ClientThread(BluetoothDevice bluetoothDevice) {
            this.mDevice = bluetoothDevice;
        }

        public void cancel() {
            this.mCancelling = true;
            try {
                if (this.mSocket != null && this.mSocket.isConnected()) {
                    this.mSocket.close();
                }
            } catch (IOException e) {
            }
        }

        public void connect() throws IOException {
            BluetoothClient.this.mCallbackHandler.sendEmptyMessage(1);
            if (BluetoothClient.this.mBluetoothAdapter.isDiscovering()) {
                BluetoothClient.this.mBluetoothAdapter.cancelDiscovery();
            }
            try {
                this.mSocket = this.mDevice.createRfcommSocketToServiceRecord(BluetoothConstant.MY_UUID);
            } catch (IOException e) {
                Message obtainMessage = BluetoothClient.this.mCallbackHandler.obtainMessage(3);
                obtainMessage.obj = e;
                BluetoothClient.this.mCallbackHandler.sendMessage(obtainMessage);
                throw e;
            }
        }

        public boolean isConnected() {
            return this.mSocket != null && this.mSocket.isConnected();
        }

        public void run() {
            try {
                if (!this.mCancelling) {
                    if (BluetoothClient.this.mBluetoothAdapter.isDiscovering()) {
                        BluetoothClient.this.mBluetoothAdapter.cancelDiscovery();
                    }
                    try {
                        this.mSocket.connect();
                        if (!this.mCancelling) {
                            try {
                                this.mInputStream = this.mSocket.getInputStream();
                                this.mOutputStream = this.mSocket.getOutputStream();
                                BluetoothClient.this.mCallbackHandler.sendEmptyMessage(2);
                                byte[] bArr = new byte[65536];
                                while (!this.mCancelling && this.mSocket.isConnected()) {
                                    try {
                                        int readPacket = PacketParser.readPacket(this.mInputStream, bArr);
                                        if (-5 == readPacket) {
                                            break;
                                        } else if (readPacket >= 0) {
                                            byte[] bArr2 = new byte[readPacket];
                                            System.arraycopy(bArr, 0, bArr2, 0, readPacket);
                                            int parse = BluetoothClient.this.mParser.parse(bArr2);
                                            if (parse < 0) {
                                                Log.w(BluetoothClient.TAG, "Received invalid packet: " + parse);
                                                Message obtainMessage = BluetoothClient.this.mCallbackHandler.obtainMessage(4);
                                                obtainMessage.arg1 = parse;
                                                BluetoothClient.this.mCallbackHandler.sendMessage(obtainMessage);
                                            }
                                        } else {
                                            Message obtainMessage2 = BluetoothClient.this.mCallbackHandler.obtainMessage(4);
                                            obtainMessage2.arg1 = readPacket;
                                            BluetoothClient.this.mCallbackHandler.sendMessage(obtainMessage2);
                                        }
                                    } catch (IOException e) {
                                        Log.e(BluetoothClient.TAG, "Communication error", e);
                                    }
                                }
                                BluetoothClient.this.mCallbackHandler.sendEmptyMessage(5);
                            } catch (IOException e2) {
                                Log.e(BluetoothClient.TAG, "Failed to communicate with bluetooth device");
                                Message obtainMessage3 = BluetoothClient.this.mCallbackHandler.obtainMessage(3);
                                obtainMessage3.obj = e2;
                                BluetoothClient.this.mCallbackHandler.sendMessage(obtainMessage3);
                                BluetoothClient.this.mClientThread = null;
                            }
                        } else {
                            try {
                                this.mSocket.close();
                            } catch (IOException e3) {
                            }
                            BluetoothClient.this.mClientThread = null;
                        }
                    } catch (IOException e4) {
                        Log.e(BluetoothClient.TAG, "Failed to connect", e4);
                        Message obtainMessage4 = BluetoothClient.this.mCallbackHandler.obtainMessage(3);
                        obtainMessage4.obj = e4;
                        BluetoothClient.this.mCallbackHandler.sendMessage(obtainMessage4);
                        try {
                            this.mSocket.close();
                        } catch (IOException e5) {
                        }
                        BluetoothClient.this.mClientThread = null;
                    }
                } else {
                    BluetoothClient.this.mClientThread = null;
                }
            } finally {
                BluetoothClient.this.mClientThread = null;
            }
        }

        public void write(byte[] bArr) {
            try {
                this.mOutputStream.write(bArr);
                this.mOutputStream.flush();
            } catch (IOException e) {
                BluetoothClient.this.mCallbackHandler.post(new Runnable() {

                    public void run() {
                        BluetoothClient.this.mListener.onException(e);
                    }
                });
            }
        }
    }

    public BluetoothClient(String str, Client.Listener listener, Handler handler) {
        if (this.mBluetoothAdapter == null) {
            this.mDevice = null;
        } else {
            this.mDevice = this.mBluetoothAdapter.getRemoteDevice(str);
        }
        this.mListener = listener;
        this.mParser = new ClientPackParser(this.mCommandListener);
        this.mCallbackHandler = new Handler(handler.getLooper()) {

            public void handleMessage(Message message) {
                switch (message.what) {
                    case 1:
                        BluetoothClient.this.mListener.onConnecting();
                        return;
                    case 2:
                        BluetoothClient.this.mListener.onConnected();
                        return;
                    case 3:
                        BluetoothClient.this.mListener.onConnectFailed((Exception) message.obj);
                        return;
                    case 4:
                        BluetoothClient.this.mListener.onBadMessage(message.arg1);
                        return;
                    case 5:
                        BluetoothClient.this.mListener.onDisconnected();
                        return;
                    default:
                        return;
                }
            }
        };
        this.mAssetHandler = new AssetHandler(this.mCallbackHandler, this.mListener);
    }

    private void connectImpl() {
        if (this.mClientThread == null) {
            this.mClientThread = new ClientThread(this.mDevice);
            try {
                this.mClientThread.connect();
                this.mClientThread.start();
            } catch (IOException e) {
                this.mClientThread = null;
            }
        }
    }

    public void connect() {
        if (this.mDevice != null) {
            connectImpl();
            return;
        }
        Message obtainMessage = this.mCallbackHandler.obtainMessage(3);
        obtainMessage.obj = new RuntimeException("Bluetooth device not found");
        this.mCallbackHandler.sendMessage(obtainMessage);
    }

    public void disconnect() {
        if (this.mClientThread != null) {
            this.mClientThread.cancel();
        }
    }

    public boolean isConnected() {
        return this.mClientThread != null;
    }

    public void sendMessage(byte[] bArr) {
        if (!isConnected()) {
            Log.w(TAG, "Not connected, not sending");
        } else {
            this.mClientThread.write(bArr);
        }
    }
}
