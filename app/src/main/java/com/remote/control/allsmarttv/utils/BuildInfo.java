package com.remote.control.allsmarttv.utils;

public class BuildInfo {
    String fingerprint;
    String id;
    String manufacturer;
    String model;
    int sdk;

    public String toString() {
        return "BuildInfo{fingerprint='" + this.fingerprint + '\'' + ", id='" + this.id + '\'' + ", manufacturer='" + this.manufacturer + '\'' + ", model='" + this.model + '\'' + ", sdk=" + this.sdk + '}';
    }
}
