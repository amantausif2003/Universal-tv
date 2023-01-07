package com.remote.control.allsmarttv.utils;

import android.util.Log;

import java.nio.ByteBuffer;

public class CapabilitiesClass {
    private static final String TAG = "AtvRemote.Capabilities";
    private long mAble;

    public static class Builder {
        private final CapabilitiesClass mCapabilitiesClass = new CapabilitiesClass();

        public Builder() {
        }

        public Builder(ByteBuffer byteBuffer) {
            if (byteBuffer != null) {
                switch (Math.min(byteBuffer.getInt(), 1)) {
                    case 1:
                        this.mCapabilitiesClass.parseV1(byteBuffer);
                        return;
                    default:
                        return;
                }
            } else {
                Log.e(CapabilitiesClass.TAG, "Null ByteBuffer");
            }
        }

        public CapabilitiesClass build() {
            return this.mCapabilitiesClass;
        }
        
    }

    private CapabilitiesClass() {
        this.mAble = 0;
    }

    private static boolean bitState(long j, long j2) {
        return (j & j2) != 0;
    }

    private String bitToString(long j) {
        if (j == 1) {
            return "BUG_REPORT_SENDER";
        }
        if (j == 2) {
            return "SECOND_SCREEN_SETUP";
        }
        if (j == 4) {
            return "MEDIA_SESSION";
        }
        if (j == 8) {
            return "SECOND_SCREEN_RECOMMENDATION";
        }
        if (j == 16) {
            return "HDMI_POWER";
        }
        if (j == 32) {
            return "HDMI_VOLUME";
        }
        if (j == 64) {
            return "OPERATOR_LAUNCHER";
        }
        if (j == 128) {
            return "APP_SWITCH_KEY";
        }
        if (j == 256) {
            return "ASSIST_KEY";
        }
        return null;
    }


    private void parseV1(ByteBuffer byteBuffer) {
        this.mAble = byteBuffer.getLong();
    }

    public static long setBit(long j, long j2, boolean z) {
        return !z ? (-1 ^ j2) & j : j | j2;
    }

    private int size(int i) {
        switch (i) {
            case 1:
                return 12;
            default:
                return 0;
        }
    }

    public boolean secondScreenSetup() {
        return bitState(this.mAble, 2);
    }

    public ByteBuffer toByteBuffer() {
        ByteBuffer allocate = ByteBuffer.allocate(size(1));
        allocate.putInt(1);
        allocate.putLong(this.mAble);
        return allocate;
    }

    public String toString() {
        long[] jArr = {1, 2, 4, 8, 16, 32, 64, 128, 256};
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < jArr.length; i++) {
            stringBuffer.append(bitToString(jArr[i]));
            stringBuffer.append("=");
            stringBuffer.append(!bitState(this.mAble, jArr[i]) ? "false" : "true");
            if (i < jArr.length - 1) {
                stringBuffer.append(", ");
            }
        }
        return stringBuffer.toString();
    }
}
