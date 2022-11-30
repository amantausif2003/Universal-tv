package com.remote.control.allsmarttv.Utils;

class Rms {
    private static final float AUDIO_METER_MAX_DB = 10.0f;
    private static final float AUDIO_METER_MIN_DB = -2.0f;

    Rms() {
    }

    public static float calculateRms(byte[] bArr, int i, int i2) {
        long j = 0;
        int i3 = i2 / 2;
        long j2 = 0;
        for (int i4 = i + i2; i4 >= i + 2; i4 -= 2) {
            short s = (short) ((bArr[i4 - 1] << 8) + (bArr[i4 - 2] & 255));
            j2 += (long) s;
            j += (long) (s * s);
        }
        return (float) Math.sqrt((double) (((j * ((long) i3)) - (j2 * j2)) / ((long) (i3 * i3))));
    }

    public static int convertRmsDbToVolume(float f) {
        int min = (int) (((Math.min(Math.max(f, (float) AUDIO_METER_MIN_DB), (float) AUDIO_METER_MAX_DB) - AUDIO_METER_MIN_DB) * 100.0f) / 12.0f);
        if (min >= 30) {
            return (min / 10) * 10;
        }
        return 0;
    }
}
