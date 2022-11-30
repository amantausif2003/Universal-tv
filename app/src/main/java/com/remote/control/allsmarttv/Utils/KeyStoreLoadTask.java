package com.remote.control.allsmarttv.Utils;

import android.content.Context;
import android.os.AsyncTask;

public class KeyStoreLoadTask extends AsyncTask<Context, Void, KeyStoreManager> {
    static KeyStoreManager sKeyStoreManager;

    public KeyStoreManager doInBackground(Context... contextArr) {
        if (sKeyStoreManager == null) {
            sKeyStoreManager = new KeyStoreManager(contextArr[0]);
            if (!sKeyStoreManager.hasServerIdentityAlias()) {
                sKeyStoreManager.initializeKeyStore();
            }
        }
        return sKeyStoreManager;
    }
}
