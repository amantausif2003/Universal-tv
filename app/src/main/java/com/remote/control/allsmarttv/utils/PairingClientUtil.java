package com.remote.control.allsmarttv.utils;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;

import com.google.polo.exception.PoloException;
import com.google.polo.pairing.ClientPairingSession;
import com.google.polo.pairing.PairingContext;
import com.google.polo.pairing.PairingListener;
import com.google.polo.pairing.PairingSession;
import com.google.polo.pairing.message.EncodingOption;
import com.google.polo.ssl.DummySSLSocketFactory;
import com.google.polo.wire.WireFormat;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;

import javax.net.ssl.SSLSocket;


public class PairingClientUtil {
    private static final String TAG = "AtvRemote.PairingClient";
    private final Handler callbackHandler = new Handler(Looper.getMainLooper());
    private final String deviceName;
    private final InetAddress host;
    private final KeyStoreManager keyStoreManager;
    private final PairingClientListener pairingClientListener;
    private PairingThread pairingThread;
    private final int port;
    private final String serviceName;


    public interface PairingClientListener {
        void onPairingResult(PairingClientUtil pairingClientUtil, PairingResult pairingResult);

        void onPairingStarted(PairingClientUtil pairingClientUtil);
    }


    public enum PairingResult {
        SUCCEEDED,
        FAILED_CONNECTION,
        FAILED_CANCELED,
        FAILED_SECRET,
        ALREADY_PAIRING
    }


    public class PairingThread extends Thread {
        private boolean mIsCancelling;
        private Handler mNetHandler;
        private ClientPairingSession mPairingSession;
        private String mSecret;

        private PairingThread() {
            HandlerThread handlerThread = new HandlerThread("PairingClient.Network");
            handlerThread.start();
            this.mNetHandler = new Handler(handlerThread.getLooper());
        }


        private synchronized String getSecret() {
            String str = null;
            synchronized (this) {
                if (this.mIsCancelling) {
                    return null;
                }
                if (this.mSecret == null) {
                    try {
                        wait();
                        if (!this.mIsCancelling) {
                            str = this.mSecret;
                        }
                        return str;
                    } catch (InterruptedException e) {
                        Log.e(PairingClientUtil.TAG, "Exception occurred", e);
                        return null;
                    }
                } else {
                    return this.mSecret;
                }
            }
        }

        public synchronized void cancel() {
            this.mIsCancelling = true;
            notify();
            this.mNetHandler.post(new Runnable() {

                public void run() {
                    if (PairingThread.this.mPairingSession != null) {
                        PairingThread.this.mPairingSession.teardown();
                    }
                }
            });
        }

        public void run() {
            PairingResult pairingResult;
            final PairingResult pairingResult2 = PairingResult.FAILED_CONNECTION;
            try {
                try {
                    SSLSocket sSLSocket = (SSLSocket) DummySSLSocketFactory.fromKeyManagers(PairingClientUtil.this.keyStoreManager.getKeyManagers()).createSocket(PairingClientUtil.this.host, PairingClientUtil.this.port);
                    try {
                        PairingContext fromSslSocket = PairingContext.fromSslSocket(sSLSocket, false);
                        try {
                            this.mPairingSession = new ClientPairingSession(WireFormat.JSON.getWireInterface(fromSslSocket), fromSslSocket, PairingClientUtil.this.serviceName, PairingClientUtil.this.deviceName);
                            EncodingOption encodingOption = new EncodingOption(EncodingOption.EncodingType.ENCODING_HEXADECIMAL, 4);
                            this.mPairingSession.addInputEncoding(encodingOption);
                            this.mPairingSession.addOutputEncoding(encodingOption);
                            if (!this.mPairingSession.doPair(new PairingListener() {

                                @Override
                                public void onLogMessage(LogLevel logLevel, String str) {
                                }

                                @Override
                                public void onPerformInputDeviceRole(PairingSession pairingSession) {
                                    if (!PairingThread.this.mIsCancelling) {
                                        PairingClientUtil.this.callbackHandler.post(new Runnable() {

                                            public void run() {
                                                PairingClientUtil.this.pairingClientListener.onPairingStarted(PairingClientUtil.this);
                                            }
                                        });
                                    }
                                    String secret = PairingThread.this.getSecret();
                                    if (!PairingThread.this.mIsCancelling && secret != null) {
                                        try {
                                            pairingSession.setSecret(pairingSession.getEncoder().decodeToBytes(secret));
                                        } catch (IllegalArgumentException e) {
                                            pairingSession.teardown();
                                        } catch (IllegalStateException e2) {
                                            pairingSession.teardown();
                                        }
                                    } else {
                                        pairingSession.teardown();
                                    }
                                }

                                @Override
                                public void onPerformOutputDeviceRole(PairingSession pairingSession, byte[] bArr) {
                                }

                                @Override
                                public void onSessionCreated(PairingSession pairingSession) {
                                }

                                @Override
                                public void onSessionEnded(PairingSession pairingSession) {
                                }
                            })) {
                                pairingResult = !this.mIsCancelling ? PairingResult.FAILED_SECRET : PairingResult.FAILED_CANCELED;
                            } else {
                                PairingClientUtil.this.keyStoreManager.storeCertificate(fromSslSocket.getServerCertificate());
                                pairingResult = PairingResult.SUCCEEDED;
                            }
                            try {
                                sSLSocket.close();
                            } catch (IOException e) {
                            }
                        } finally {
                            PairingClientUtil.this.callbackHandler.post(new Runnable() {

                                public void run() {
                                    PairingClientUtil.this.pairingClientListener.onPairingResult(PairingClientUtil.this, pairingResult2);
                                }
                            });
                            PairingClientUtil.this.pairingThread = null;
                        }
                    } catch (PoloException e2) {
                        PairingClientUtil.this.callbackHandler.post(new Runnable() {

                            public void run() {
                                PairingClientUtil.this.pairingClientListener.onPairingResult(PairingClientUtil.this, PairingResult.FAILED_CONNECTION);
                            }
                        });
                        PairingClientUtil.this.callbackHandler.post(new Runnable() {

                            public void run() {
                                PairingClientUtil.this.pairingClientListener.onPairingResult(PairingClientUtil.this, pairingResult2);
                            }
                        });
                        PairingClientUtil.this.pairingThread = null;
                    } catch (IOException e3) {
                        PairingClientUtil.this.callbackHandler.post(new Runnable() {

                            public void run() {
                                PairingClientUtil.this.pairingClientListener.onPairingResult(PairingClientUtil.this, PairingResult.FAILED_CONNECTION);
                            }
                        });
                        PairingClientUtil.this.callbackHandler.post(new Runnable() {

                            public void run() {
                                PairingClientUtil.this.pairingClientListener.onPairingResult(PairingClientUtil.this, pairingResult2);
                            }
                        });
                        PairingClientUtil.this.pairingThread = null;
                    }
                } catch (UnknownHostException e4) {
                    PairingClientUtil.this.callbackHandler.post(new Runnable() {

                        public void run() {
                            PairingClientUtil.this.pairingClientListener.onPairingResult(PairingClientUtil.this, PairingResult.FAILED_CONNECTION);
                        }
                    });
                    PairingClientUtil.this.callbackHandler.post(new Runnable() {

                        public void run() {
                            PairingClientUtil.this.pairingClientListener.onPairingResult(PairingClientUtil.this, pairingResult2);
                        }
                    });
                    PairingClientUtil.this.pairingThread = null;
                } catch (IOException e5) {
                    PairingClientUtil.this.callbackHandler.post(new Runnable() {

                        public void run() {
                            PairingClientUtil.this.pairingClientListener.onPairingResult(PairingClientUtil.this, PairingResult.FAILED_CONNECTION);
                        }
                    });
                    PairingClientUtil.this.callbackHandler.post(new Runnable() {

                        public void run() {
                            PairingClientUtil.this.pairingClientListener.onPairingResult(PairingClientUtil.this, pairingResult2);
                        }
                    });
                    PairingClientUtil.this.pairingThread = null;
                }
            } catch (GeneralSecurityException e6) {
                throw new IllegalStateException("Cannot build socket factory", e6);
            }
        }

        public synchronized void setSecret(String str) {
            if (this.mSecret == null) {
                this.mSecret = str;
                notify();
            } else {
                throw new IllegalStateException("Secret already set: " + this.mSecret);
            }
        }
    }

    public PairingClientUtil(InetAddress inetAddress, int i, KeyStoreManager keyStoreManager, PairingClientListener pairingClientListener, String str, String str2) {
        this.host = inetAddress;
        this.port = i;
        this.keyStoreManager = keyStoreManager;
        this.pairingClientListener = pairingClientListener;
        this.serviceName = str;
        this.deviceName = str2;
    }

    public void cancel() {
        if (this.pairingThread != null) {
            this.pairingThread.cancel();
            this.pairingThread = null;
        }
    }

    public void setSecret(String str) {
        if (this.pairingThread != null) {
            this.pairingThread.setSecret(str);
        }
    }

    public void start() {
        if (this.pairingThread == null) {
            this.pairingThread = new PairingThread();
            this.pairingThread.start();
        }
    }
}
