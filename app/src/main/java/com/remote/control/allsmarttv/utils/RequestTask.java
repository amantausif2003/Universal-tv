package com.remote.control.allsmarttv.utils;

import android.os.AsyncTask;

import com.jaku.core.JakuRequest;
import com.jaku.core.JakuResponse;
import com.jaku.model.Channel;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class RequestTask extends AsyncTask<RokureqType, Void, RequestTask.Result> {

    private RokuRequest rokuRequest;

    private JakuRequest request;
    private RokureqType rokureqType;

    public RequestTask(JakuRequest request, RokuRequest callback) {
        this.request = request;
        setCallback(callback);
    }

    void setCallback(RokuRequest callback) {
        rokuRequest = callback;
    }

    public static class Result {
        public Object mResultValue;
        public Exception mException;
        public Result(Object resultValue) {
            mResultValue = resultValue;
        }
        public Result(Exception exception) {
            mException = exception;
        }
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected Result doInBackground(RokureqType... requestTypes) {
        Result result = null;
        if (!isCancelled() && requestTypes != null && requestTypes.length > 0) {
            RokureqType requestType = requestTypes[0];
            try {
                if (requestType.equals(RokureqType.query_active_app)) {
                    JakuResponse response = request.send();
                    List<Channel> channels = (List<Channel>) response.getResponseData();
                    result = new Result(channels);
                } else if (requestType.equals(RokureqType.query_device_info)) {
                    JakuResponse response = request.send();
                    Device_rokuTv device = Device_rokuTv.fromDevice((com.jaku.model.Device) response.getResponseData());
                    result = new Result(device);
                } else if (requestType.equals(RokureqType.query_icon)) {
                    JakuResponse response = request.send();
                    byte [] data = ((ByteArrayOutputStream) response.getResponseData()).toByteArray();
                    result = new Result(data);
                } else {
                    request.send();
                }
            } catch(Exception e) {
                e.printStackTrace();
                result = new Result(e);
            }
        }
        return result;
    }

    @Override
    protected void onPostExecute(Result result) {
        if (result != null && rokuRequest != null) {
            if (result.mException != null) {
                rokuRequest.onErrorResponse(result);
            } else if (result.mResultValue != null) {
                rokuRequest.requestResult(rokureqType, result);
            }
        }
    }

    @Override
    protected void onCancelled(Result result) {
    }
}
