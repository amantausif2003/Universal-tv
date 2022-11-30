package com.remote.control.allsmarttv.Utils;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Handler;
import android.util.ArrayMap;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.util.Map;

@TargetApi(Build.VERSION_CODES.KITKAT)
public class AssetHandler {
    private static final boolean DEBUG = false;
    private static final String TAG = "AtvRemote.AssetHandler";
    private final Handler mCallbackHandler;
    private final Client.Listener mListener;
    private Map<String, Asset> mPendingAssets = new ArrayMap();

    static class Asset {
        String id;
        Map<String, String> metadata;
        int numberOfChunks;
        ByteArrayOutputStream out;
        int size;
        String type;

        Asset() {
        }
    }

    public AssetHandler(Handler handler, Client.Listener listener) {
        this.mCallbackHandler = handler;
        this.mListener = listener;
    }

    public void onAssetChunk(String str, int i, int i2, byte[] bArr) {
        Asset asset = this.mPendingAssets.get(str);
        if (asset != null) {
            asset.out.write(bArr, 0, bArr.length);
            asset.numberOfChunks--;
            return;
        }
        Log.w(TAG, "Never received asset header for " + str);
    }

    public void onAssetFooter(String str, int i) {
        Asset remove = this.mPendingAssets.remove(str);
        if (remove == null) {
            Log.w(TAG, "Asset " + str + " not found");
        } else if (i != 0) {
            Log.w(TAG, "Asset " + str + " not completed " + i);
        } else if (remove.numberOfChunks != 0) {
            Log.e(TAG, "Incomplete asset " + str + " " + remove.numberOfChunks);
        } else {
            final byte[] byteArray = remove.out.toByteArray();
            final String str2 = remove.type;
            final Map<String, String> map = remove.metadata;
            this.mCallbackHandler.post(new Runnable() {

                public void run() {
                    AssetHandler.this.mListener.onAsset(str2, map, byteArray);
                }
            });
        }
    }

    public void onAssetHeader(String str, String str2, int i, int i2, Map<String, String> map) {
        Asset asset = new Asset();
        asset.id = str;
        asset.type = str2;
        asset.metadata = map;
        asset.size = i;
        asset.numberOfChunks = i2;
        asset.out = new ByteArrayOutputStream(i);
        this.mPendingAssets.put(str, asset);
    }
}
