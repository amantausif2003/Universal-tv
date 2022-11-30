package com.remote.control.allsmarttv.Utils;

import android.view.MotionEvent;

public class MotionEventUtil {
    private MotionEventUtil() {
    }

    public static byte[] encodeTheMotionEvent(MotionEvent motionEvent, PackEncoderClass packEncoderClass, float f) {
        int pointerCount = motionEvent.getPointerCount();
        int[] iArr = new int[pointerCount];
        for (int i = 0; i < pointerCount; i++) {
            iArr[i] = motionEvent.getPointerId(i);
        }
        int historySize = motionEvent.getHistorySize();
        TouchRecordUtil[] touchRecordUtilArr = new TouchRecordUtil[(historySize + 1)];
        for (int i2 = 0; i2 < historySize; i2++) {
            TouchRecordUtil.Location[] locationArr = new TouchRecordUtil.Location[pointerCount];
            for (int i3 = 0; i3 < pointerCount; i3++) {
                locationArr[i3] = new TouchRecordUtil.Location(motionEvent.getHistoricalX(i3, i2), motionEvent.getHistoricalY(i3, i2), f);
            }
            touchRecordUtilArr[i2] = new TouchRecordUtil(motionEvent.getHistoricalEventTime(i2), locationArr);
        }
        TouchRecordUtil.Location[] locationArr2 = new TouchRecordUtil.Location[pointerCount];
        for (int i4 = 0; i4 < pointerCount; i4++) {
            locationArr2[i4] = new TouchRecordUtil.Location(motionEvent.getX(i4), motionEvent.getY(i4), f);
        }
        touchRecordUtilArr[historySize] = new TouchRecordUtil(motionEvent.getEventTime(), locationArr2);
        return packEncoderClass.encodeMotionEvent(motionEvent.getAction(), iArr, touchRecordUtilArr);
    }
}
