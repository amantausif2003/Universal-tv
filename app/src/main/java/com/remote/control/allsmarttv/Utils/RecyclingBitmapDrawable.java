package com.remote.control.allsmarttv.Utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;

import com.remote.control.allsmarttv.BuildConfig;

public class RecyclingBitmapDrawable extends BitmapDrawable {

    static final String TAG = "CountingBitmapDrawable";

    private int cacheRefCount = 0;
    private int displayRefCount = 0;

    private boolean hasBeenDisplayed;

    public RecyclingBitmapDrawable(Resources res, Bitmap bitmap) {
        super(res, bitmap);
    }

    public void setIsDisplayed(boolean isDisplayed) {

        synchronized (this) {
            if (isDisplayed) {
                displayRefCount++;
                hasBeenDisplayed = true;
            } else {
                displayRefCount--;
            }
        }

        checkState();
    }

    public void setIsCached(boolean isCached) {
        synchronized (this) {
            if (isCached) {
                cacheRefCount++;
            } else {
                cacheRefCount--;
            }
        }

        checkState();
    }

    private synchronized void checkState() {

        if (cacheRefCount <= 0 && displayRefCount <= 0 && hasBeenDisplayed
                && hasValidBit()) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "No longer being used or cached so recycling. "
                        + toString());
            }

            getBitmap().recycle();
        }
    }

    private synchronized boolean hasValidBit() {
        Bitmap bitmap = getBitmap();
        return bitmap != null && !bitmap.isRecycled();
    }

}
