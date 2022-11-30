package com.remote.control.allsmarttv.Utils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowInsets;

import androidx.drawerlayout.widget.DrawerLayout;

import com.remote.control.allsmarttv.R;

public class TvDrawer extends DrawerLayout {
    public TvDrawer(Context context) {
        this(context, null);
    }

    public TvDrawer(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @SuppressLint({"NewApi"})
    public void onFinishInflate() {
        super.onFinishInflate();
        View findViewById = findViewById(R.id.rem_drawer);
        if (Build.VERSION.SDK_INT >= 21) {
            findViewById.setOnApplyWindowInsetsListener(new OnApplyWindowInsetsListener() {

                @TargetApi(20)
                public WindowInsets onApplyWindowInsets(View view, WindowInsets windowInsets) {
                    return windowInsets.consumeSystemWindowInsets();
                }
            });
        }
    }
}
