package com.remote.control.allsmarttv.utils;

import android.content.Context;

import com.jaku.api.QueryRequests;
import com.jaku.model.Channel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class AppsUtil implements Callable {
    private Context context;

    public AppsUtil(final Context context) {
        this.context = context;
    }

    public List<Channel> call() {
        List<Channel> channels;

        try {
            channels = QueryRequests.queryAppsRequest(RokuCmds.getTv());
        } catch (IOException ex) {
            ex.printStackTrace();
            channels = new ArrayList<>();
        }

        return channels;
    }
}
