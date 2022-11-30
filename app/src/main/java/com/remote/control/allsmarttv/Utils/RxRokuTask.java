package com.remote.control.allsmarttv.Utils;

import android.content.Context;

import com.jaku.core.JakuRequest;
import com.jaku.core.JakuResponse;
import com.jaku.model.Channel;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.concurrent.Callable;

public class RxRokuTask implements Callable {

    private Context mContext;
    private JakuRequest jakuRequest;
    private RokureqType rokureqType;

    public RxRokuTask(final Context mContext, final JakuRequest jakuRequest, final RokureqType rokureqType) {
        this.mContext = mContext;
        this.jakuRequest = jakuRequest;
        this.rokureqType = rokureqType;
    }

    public class Result {
        Object mResultValue;
        Exception mException;

        public Result(Object resultValue) {
            mResultValue = resultValue;
        }

        public Result(Exception exception) {
            mException = exception;
        }
    }

    public Result call() {
        Result result;

        try {
            if (rokureqType.equals(RokureqType.query_active_app)) {
                JakuResponse response = jakuRequest.send();
                List<Channel> channels = (List<Channel>) response.getResponseData();
                result = new Result(channels);
            } else if (rokureqType.equals(RokureqType.query_device_info)) {
                JakuResponse response = jakuRequest.send();
                Device device = (Device) response.getResponseData();
                result = new Result(device);
            } else if (rokureqType.equals(RokureqType.query_icon)) {
                JakuResponse response = jakuRequest.send();
                byte[] data = ((ByteArrayOutputStream) response.getResponseData()).toByteArray();
                result = new Result(data);
            } else {
                JakuResponse response = jakuRequest.send();
                result = new Result(response.getResponseData());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            result = new Result(ex);
        }

        return result;
    }

}