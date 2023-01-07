package com.remote.control.allsmarttv.utils.ir_utils;

import android.content.Context;
import android.media.MediaScannerConnection;
import androidx.appcompat.app.AppCompatActivity;

public class ParentActvity extends AppCompatActivity {

    static public void notifyMediaScannerService(Context context, String path) {
        MediaScannerConnection.scanFile(context, new String[]{path}, new String[]{"image/jpeg"}, null);
    }

}