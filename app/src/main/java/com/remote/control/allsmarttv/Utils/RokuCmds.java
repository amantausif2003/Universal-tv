package com.remote.control.allsmarttv.Utils;

import static com.remote.control.allsmarttv.Activities.RokuPair.deviceRoku;

public class RokuCmds {


    public static String getPic(String channelId) {
        String url = "";

        try {
            Device_rokuTv device_rokuTv = deviceRoku;

            url = device_rokuTv.getHost() + "/query/icon/" + channelId;
        } catch (Exception ex) {
        }

        return url;
    }

    public static String getTv() {
        String url = "";

        try {

            Device_rokuTv device_rokuTv = deviceRoku;

            url = device_rokuTv.getHost();
        } catch (Exception ex) {
        }

        return url;
    }


}
