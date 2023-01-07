package com.remote.control.allsmarttv.utils;

public enum RokureqType {
    query_active_app("query/active-app"),
    query_device_info("query/device-info"),
    launch("launch"),
    keypress("keypress"),
    query_icon("query/icon");

    private final String method;

    RokureqType(String method) {
        this.method = method;
    }

    public String getValue() {
        return method;
    }
}