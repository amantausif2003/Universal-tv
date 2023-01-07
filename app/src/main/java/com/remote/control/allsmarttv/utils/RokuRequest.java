package com.remote.control.allsmarttv.utils;


public abstract class RokuRequest {
    public abstract void requestResult(RokureqType rokureqType, RequestTask.Result result);
    public abstract void onErrorResponse(RequestTask.Result result);
}
