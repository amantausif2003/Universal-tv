package com.remote.control.allsmarttv.utils;

import android.os.Bundle;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.ExtractedText;

import java.util.Map;

public abstract class AbsClient implements Client.Listener {
    private final AbsDeviceImpl mDevice;
    private final PackEncoderClass mEncoder;
    private final Device.Listener mListener;
    private float mNoiseLevel = 0.75f;
    private final SpeechInput mSpeechInput;

    protected AbsClient(AbsDeviceImpl absDeviceImpl, Device.Listener listener, SpeechInput speechInput, PackEncoderClass packEncoderClass) {
        this.mDevice = absDeviceImpl;
        this.mListener = listener;
        this.mSpeechInput = speechInput;
        this.mEncoder = packEncoderClass;
    }

    @Override
    public void onAsset(final String str, final Map<String, String> map, final byte[] bArr) {
        this.mDevice.runOnUiThread(new Runnable() {
            public void run() {
                AbsClient.this.mListener.onAsset(AbsClient.this.mDevice, str, map, bArr);
            }
        });
    }

    @Override
    public final void onBadMessage(int i) {
    }

    @Override
    public void onBugReportStatus(final int i) {
        this.mDevice.runOnUiThread(new Runnable() {

            public void run() {
                AbsClient.this.mListener.onBugReportStatus(AbsClient.this.mDevice, i);
            }
        });
    }

    @Override
    public void onCapabilities(final CapabilitiesClass capabilitiesClass) {
        this.mDevice.runOnUiThread(new Runnable() {

            public void run() {
                AbsClient.this.mListener.onCapabilities(AbsClient.this.mDevice, capabilitiesClass);
            }
        });
    }

    @Override
    public final void onConnectFailed(Exception exc) {
        this.mDevice.runOnUiThread(new Runnable() {

            public void run() {
                AbsClient.this.mListener.onConnectFailed(AbsClient.this.mDevice);
            }
        });
    }

    @Override
    public final void onConnected() {
        this.mDevice.runOnUiThread(new Runnable() {

            public void run() {
                AbsClient.this.mListener.onConnected(AbsClient.this.mDevice);
                AbsClient.this.mDevice.internalConfigure();
            }
        });
    }

    @Override
    public final void onConnecting() {
        this.mDevice.runOnUiThread(new Runnable() {

            public void run() {
                AbsClient.this.mListener.onConnecting(AbsClient.this.mDevice);
            }
        });
    }

    @Override
    public void onDeveloperStatus(final boolean z) {
        this.mDevice.runOnUiThread(new Runnable() {

            public void run() {
                AbsClient.this.mListener.onDeveloperStatus(AbsClient.this.mDevice, z);
            }
        });
    }

    @Override
    public final void onDisconnected() {
        this.mDevice.runOnUiThread(new Runnable() {

            public void run() {
                AbsClient.this.mListener.onDisconnected(AbsClient.this.mDevice);
            }
        });
    }

    @Override
    public final void onException(final Exception exc) {
        this.mDevice.runOnUiThread(new Runnable() {

            public void run() {
                AbsClient.this.mListener.onException(AbsClient.this.mDevice, exc);
            }
        });
    }

    @Override
    public final void onReceiveCompletionInfo(final CompletionInfo[] completionInfoArr) {
        this.mDevice.runOnUiThread(new Runnable() {

            public void run() {
                AbsClient.this.mListener.onCompletionInfo(AbsClient.this.mDevice, completionInfoArr);
            }
        });
    }

    @Override
    public final void onReceiveConfigureFailure(final int i) {
        this.mDevice.onConfigureFailed();
        this.mDevice.runOnUiThread(new Runnable() {

            public void run() {
                AbsClient.this.mListener.onConfigureFailure(AbsClient.this.mDevice, i);
            }
        });
    }

    @Override
    public final void onReceiveConfigureSuccess(int i, String str, BuildInfo buildInfo) {
        this.mDevice.onConfigureSuccess(i, str, buildInfo);
        this.mDevice.runOnUiThread(new Runnable() {

            public void run() {
                AbsClient.this.mListener.onConfigureSuccess(AbsClient.this.mDevice);
            }
        });
    }

    @Override
    public final void onReceiveHideIme() {
        this.mDevice.runOnUiThread(new Runnable() {

            public void run() {
                AbsClient.this.mListener.onHideIme(AbsClient.this.mDevice);
            }
        });
    }

    @Override
    public final void onReceivePacketVersionTooHigh(byte b) {
    }

    @Override
    public final void onReceivePacketVersionTooLow(byte b) {
    }

    @Override
    public final void onReceiveShowIme(final EditorInfo editorInfo, final boolean z, final ExtractedText extractedText, boolean z2) {
        this.mDevice.mHasExtendedIMEAPI = z2;
        this.mDevice.runOnUiThread(new Runnable() {

            public void run() {
                AbsClient.this.mListener.onShowIme(AbsClient.this.mDevice, editorInfo, z, extractedText);
            }
        });
    }

    @Override
    public final void onReceiveStartVoice() {
        this.mSpeechInput.start(new SpeechInput.VoiceInputListener() {

            @Override
            public void onPacket(byte[] bArr) {
                AbsClient.this.mDevice.sendMessage(AbsClient.this.mEncoder.encodeVoicePacket(bArr));
                float calculateRms = Rms.calculateRms(bArr, 0, bArr.length);
                if (AbsClient.this.mNoiseLevel < calculateRms) {
                    AbsClient.this.mNoiseLevel = (AbsClient.this.mNoiseLevel * 0.999f) + (0.001f * calculateRms);
                } else {
                    AbsClient.this.mNoiseLevel = (AbsClient.this.mNoiseLevel * 0.95f) + (0.05f * calculateRms);
                }
                float f = -120.0f;
                if (((double) AbsClient.this.mNoiseLevel) > 0.0d && ((double) (calculateRms / AbsClient.this.mNoiseLevel)) > 1.0E-6d) {
                    f = ((float) Math.log10((double) (calculateRms / AbsClient.this.mNoiseLevel))) * 10.0f;
                }
                final int convertRmsDbToVolume = Rms.convertRmsDbToVolume(f);
                AbsClient.this.mDevice.runOnUiThread(new Runnable() {

                    public void run() {
                        AbsClient.this.mListener.onVoiceSoundLevel(AbsClient.this.mDevice, convertRmsDbToVolume);
                    }
                });
            }

            @Override
            public void onRecord(int i, int i2, int i3) {
                AbsClient.this.mDevice.sendMessage(AbsClient.this.mEncoder.encodeVoiceConfig(i, i2, i3));
                AbsClient.this.mDevice.runOnUiThread(new Runnable() {

                    public void run() {
                        AbsClient.this.mListener.onStartVoice(AbsClient.this.mDevice);
                    }
                });
            }

            @Override
            public void onStop() {
                AbsClient.this.mDevice.sendMessage(AbsClient.this.mEncoder.encodeStopVoice());
                AbsClient.this.mDevice.runOnUiThread(new Runnable() {

                    public void run() {
                        AbsClient.this.mListener.onStopVoice(AbsClient.this.mDevice);
                    }
                });
            }
        });
    }

    @Override
    public final void onReceiveStopVoice() {
        this.mSpeechInput.stop();
    }

    @Override
    public void onResponse(long j, Object obj) {
        this.mDevice.onResponse(j, obj);
    }

    @Override
    public abstract void onSslHandshakeCompleted();

    @Override
    public abstract void onSslHandshakeFailed(Exception exc);

    @Override
    public void receivedBundle(final int i, final Bundle bundle) {
        this.mDevice.runOnUiThread(new Runnable() {
            public void run() {
                AbsClient.this.mListener.onBundle(AbsClient.this.mDevice, i, bundle);
            }
        });
    }
}
