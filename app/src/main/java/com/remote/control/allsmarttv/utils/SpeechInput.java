package com.remote.control.allsmarttv.utils;

import android.media.AudioRecord;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.concurrent.Semaphore;

final class SpeechInput {
    private static final int SAMPLE_RATE = 8000;
    private static final String TAG = "AtvRemote.VoiceInput";
    private int mBufferSize = (AudioRecord.getMinBufferSize(SAMPLE_RATE, 16, 2) * 2);
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private VoiceInputListener mListener;
    private AudioRecord mRecorder;
    private boolean mRecording = false;
    private final Object mRecordingLock = new Object();
    private Thread mRecordingThread;

    public interface VoiceInputListener {
        void onPacket(byte[] bArr);

        void onRecord(int i, int i2, int i3);

        void onStop();
    }

    SpeechInput() {
    }

    public boolean isRecording() {
        return this.mRecording;
    }

    public void start(VoiceInputListener voiceInputListener) {
        synchronized (this.mRecordingLock) {
            if (!this.mRecording) {
                this.mListener = voiceInputListener;
                try {
                    this.mRecorder = new AudioRecord(1, SAMPLE_RATE, 16, 2, this.mBufferSize * 2);
                    if (this.mRecorder.getState() != 0) {
                        this.mRecorder.startRecording();
                        this.mRecording = true;
                        this.mHandler.post(new Runnable() {

                            public void run() {
                                SpeechInput.this.mListener.onRecord(SpeechInput.SAMPLE_RATE, 16, 2);
                            }
                        });
                        this.mRecordingThread = new Thread(new Runnable() {

                            public void run() {
                                final byte[] bArr = new byte[SpeechInput.this.mBufferSize];
                                final Semaphore semaphore = new Semaphore(1);
                                while (true) {
                                    if (!SpeechInput.this.mRecording) {
                                        break;
                                    }
                                    try {
                                        semaphore.acquire();
                                        synchronized (SpeechInput.this.mRecordingLock) {
                                            if (!SpeechInput.this.mRecording) {
                                                break;
                                            }
                                            final int read = SpeechInput.this.mRecorder.read(bArr, 0, SpeechInput.this.mBufferSize);
                                            if (read < 0) {
                                                SpeechInput.this.stop();
                                                break;
                                            }
                                            SpeechInput.this.mHandler.post(new Runnable() {

                                                public void run() {
                                                    SpeechInput.this.mListener.onPacket(bArr);
                                                    semaphore.release();
                                                }
                                            });
                                        }
                                    } catch (InterruptedException e) {
                                        synchronized (SpeechInput.this.mRecordingLock) {
                                            SpeechInput.this.mRecording = false;
                                        }
                                    }
                                }
                                SpeechInput.this.mHandler.post(new Runnable() {

                                    public void run() {
                                        SpeechInput.this.mListener.onStop();
                                    }
                                });
                            }
                        });
                        this.mRecordingThread.start();
                        return;
                    }
                    Log.e(TAG, "Voice input failed because AudioRecord is uninitialized");
                } catch (IllegalArgumentException e) {
                    Log.e(TAG, "Initializing AudioRecord with illegal arguments.");
                }
            }
        }
    }

    public void stop() {
        synchronized (this.mRecordingLock) {
            if (this.mRecording) {
                this.mRecording = false;
                this.mRecorder.stop();
                this.mRecorder.release();
                this.mRecorder = null;
                this.mRecordingThread = null;
            }
        }
    }
}
