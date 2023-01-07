package com.remote.control.allsmarttv.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.util.Log;
import android.widget.ImageView;

import androidx.fragment.app.FragmentManager;

import com.remote.control.allsmarttv.BuildConfig;

import java.lang.ref.WeakReference;

public abstract class ImageWorkUtil {
    private static final String TAG = "ImageWorker";
    private static final int FADE_IN_TIME = 200;

    private ImageClass imageCacheClass;
    private ImageClass.ImageCacheParams imageCacheParams;
    private Bitmap bitmap;
    private boolean fadeInBitmap = true;
    private boolean exitTasksEarly = false;
    protected boolean pauseWork = false;
    private final Object pauseWorkLock = new Object();

    protected Resources resources;

    private static final int MESSAGE_CLEAR = 0;
    private static final int MESSAGE_INIT_DISK_CACHE = 1;
    private static final int MESSAGE_FLUSH = 2;
    private static final int MESSAGE_CLOSE = 3;

    protected ImageWorkUtil(Context context) {
        resources = context.getResources();
    }

    public void loadImage(Object data, ImageView imageView, OnImageLoadedListener listener) {
        if (data == null) {
            return;
        }

        BitmapDrawable value = null;

        if (imageCacheClass != null) {
            value = imageCacheClass.getBitmapFromMemCache(String.valueOf(data));
        }

        if (value != null) {
            // Bitmap found in memory cache
            imageView.setImageDrawable(value);
            if (listener != null) {
                listener.onImageLoaded(true);
            }
        } else if (cancelPotentialWork(data, imageView)) {

            final BitmapWorkerTaskClass task = new BitmapWorkerTaskClass(data, imageView, listener);
            final AsyncDrawable asyncDrawable =
                    new AsyncDrawable(resources, bitmap, task);
            imageView.setImageDrawable(asyncDrawable);

            task.executeOnExecutor(AsyncTaskClass.DUAL_THREAD_EXECUTOR);
        }
    }


    public void loadImage(Object data, ImageView imageView) {
        loadImage(data, imageView, null);
    }

    public void setLoadingImage(int resId) {
        bitmap = BitmapFactory.decodeResource(resources, resId);
    }

    public void addImageCache(FragmentManager fragmentManager,
                              ImageClass.ImageCacheParams cacheParams) {
        imageCacheParams = cacheParams;
        imageCacheClass = ImageClass.getInstance(fragmentManager, imageCacheParams);
        new CacheAsyncTaskClass().execute(MESSAGE_INIT_DISK_CACHE);
    }

    public void setExitTasksEarly(boolean exitTasksEarly) {
        this.exitTasksEarly = exitTasksEarly;
        setPauseWork(false);
    }

    protected abstract Bitmap processBitmap(Object data);

    protected ImageClass getImageCache() {
        return imageCacheClass;
    }

    public static boolean cancelPotentialWork(Object data, ImageView imageView) {

        final BitmapWorkerTaskClass bitmapWorkerTask = getBitmapWorkerTask(imageView);

        if (bitmapWorkerTask != null) {
            final Object bitmapData = bitmapWorkerTask.mData;
            if (bitmapData == null || !bitmapData.equals(data)) {
                bitmapWorkerTask.cancel(true);
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "cancelPotentialWork - cancelled work for " + data);
                }
            } else {
                // The same work is already in progress.
                return false;
            }
        }
        return true;
    }

    private static BitmapWorkerTaskClass getBitmapWorkerTask(ImageView imageView) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getBitmapWorkerTask();
            }
        }
        return null;
    }

    private class BitmapWorkerTaskClass extends AsyncTaskClass<Void, Void, BitmapDrawable> {
        private Object mData;
        private final WeakReference<ImageView> imageViewReference;
        private final OnImageLoadedListener mOnImageLoadedListener;

        public BitmapWorkerTaskClass(Object data, ImageView imageView) {
            mData = data;
            imageViewReference = new WeakReference<ImageView>(imageView);
            mOnImageLoadedListener = null;
        }

        public BitmapWorkerTaskClass(Object data, ImageView imageView, OnImageLoadedListener listener) {
            mData = data;
            imageViewReference = new WeakReference<ImageView>(imageView);
            mOnImageLoadedListener = listener;
        }

        @Override
        protected BitmapDrawable doInBackground(Void... params) {

            if (BuildConfig.DEBUG) {
                Log.d(TAG, "doInBackground - starting work");
            }

            final String dataString = String.valueOf(mData);
            Bitmap bitmap = null;
            BitmapDrawable drawable = null;

            // Wait here if work is paused and the task is not cancelled
            synchronized (pauseWorkLock) {
                while (pauseWork && !isCancelled()) {
                    try {
                        pauseWorkLock.wait();
                    } catch (InterruptedException e) {}
                }
            }

            if (imageCacheClass != null && !isCancelled() && getAttachedImageView() != null
                    && !exitTasksEarly) {
                bitmap = imageCacheClass.getBitmapFromDiskCache(dataString);
            }

            if (bitmap == null && !isCancelled() && getAttachedImageView() != null
                    && !exitTasksEarly) {
                bitmap = processBitmap(mData);
            }

            if (bitmap != null) {
                bitmap = getRoundedCornerBitmap(bitmap, 16);

                if (VersionUtil.hasHoneycomb()) {
                    drawable = new BitmapDrawable(resources, bitmap);
                } else {

                    drawable = new RecyclingBitmapDrawable(resources, bitmap);
                }

                if (imageCacheClass != null) {
                    imageCacheClass.addBitmapToCache(dataString, drawable);
                }
            }

            if (BuildConfig.DEBUG) {
                Log.d(TAG, "doInBackground - finished work");
            }

            return drawable;
        }

        private Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
            Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                    .getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(output);

            final int color = 0xff424242;
            final Paint paint = new Paint();
            final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
            final RectF rectF = new RectF(rect);
            final float roundPx = pixels;

            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(color);
            canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(bitmap, rect, rect, paint);

            return output;
        }

        @Override
        protected void onPostExecute(BitmapDrawable value) {
            //BEGIN_INCLUDE(complete_background_work)
            boolean success = false;
            // if cancel was called on this task or the "exit early" flag is set then we're done
            if (isCancelled() || exitTasksEarly) {
                value = null;
            }

            final ImageView imageView = getAttachedImageView();
            if (value != null && imageView != null) {
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "onPostExecute - setting bitmap");
                }
                success = true;
                setImageDrawable(imageView, value);
            }
            if (mOnImageLoadedListener != null) {
                mOnImageLoadedListener.onImageLoaded(success);
            }
            //END_INCLUDE(complete_background_work)
        }

        @Override
        protected void onCancelled(BitmapDrawable value) {
            super.onCancelled(value);
            synchronized (pauseWorkLock) {
                pauseWorkLock.notifyAll();
            }
        }

        private ImageView getAttachedImageView() {
            final ImageView imageView = imageViewReference.get();
            final BitmapWorkerTaskClass bitmapWorkerTask = getBitmapWorkerTask(imageView);

            if (this == bitmapWorkerTask) {
                return imageView;
            }

            return null;
        }
    }

    public interface OnImageLoadedListener {

        void onImageLoaded(boolean success);
    }

    private static class AsyncDrawable extends BitmapDrawable {
        private final WeakReference<BitmapWorkerTaskClass> bitmapWorkerTaskReference;

        public AsyncDrawable(Resources res, Bitmap bitmap, BitmapWorkerTaskClass bitmapWorkerTask) {
            super(res, bitmap);
            bitmapWorkerTaskReference =
                new WeakReference<BitmapWorkerTaskClass>(bitmapWorkerTask);
        }

        public BitmapWorkerTaskClass getBitmapWorkerTask() {
            return bitmapWorkerTaskReference.get();
        }
    }

    private void setImageDrawable(ImageView imageView, Drawable drawable) {
        if (fadeInBitmap) {
            // Transition drawable with a transparent drawable and the final drawable
            final TransitionDrawable td =
                    new TransitionDrawable(new Drawable[] {
                            new ColorDrawable(Color.parseColor("#00000000")),
                            drawable
                    });
            // Set background to loading bitmap
            imageView.setBackgroundDrawable(
                    new BitmapDrawable(resources, bitmap));

            imageView.setImageDrawable(td);
            td.startTransition(FADE_IN_TIME);
        } else {
            imageView.setImageDrawable(drawable);
        }
    }

    public void setPauseWork(boolean pauseWork) {
        synchronized (pauseWorkLock) {
            this.pauseWork = pauseWork;
            if (!this.pauseWork) {
                pauseWorkLock.notifyAll();
            }
        }
    }

    protected class CacheAsyncTaskClass extends AsyncTaskClass<Object, Void, Void> {

        @Override
        protected Void doInBackground(Object... params) {
            switch ((Integer)params[0]) {
                case MESSAGE_CLEAR:
                    clearCacheInternal();
                    break;
                case MESSAGE_INIT_DISK_CACHE:
                    initDiskCacheInternal();
                    break;
                case MESSAGE_FLUSH:
                    flushCacheInternal();
                    break;
                case MESSAGE_CLOSE:
                    closeCacheInternal();
                    break;
            }
            return null;
        }
    }

    protected void initDiskCacheInternal() {
        if (imageCacheClass != null) {
            imageCacheClass.initDiskCache();
        }
    }

    protected void clearCacheInternal() {
        if (imageCacheClass != null) {
            imageCacheClass.clearCache();
        }
    }

    protected void flushCacheInternal() {
        if (imageCacheClass != null) {
            imageCacheClass.flush();
        }
    }

    protected void closeCacheInternal() {
        if (imageCacheClass != null) {
            imageCacheClass.close();
            imageCacheClass = null;
        }
    }

    public void flushCache() {
        new CacheAsyncTaskClass().execute(MESSAGE_FLUSH);
    }

    public void closeCache() {
        new CacheAsyncTaskClass().execute(MESSAGE_CLOSE);
    }
}
