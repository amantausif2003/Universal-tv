package com.remote.control.allsmarttv.Utils;

public class ScanRokuUtil {
    private String IpAddr;
    private String hwaddr;
    private String device;
    private boolean isReachable;

    public ScanRokuUtil(String ip, String hw, String device, boolean isReachable) {
        super();
        this.IpAddr = ip;
        this.hwaddr = hw;
        this.device = device;
        this.isReachable = isReachable;
    }

    public String getIpAddr() {
        return IpAddr;
    }

    public void setIpAddr(String ipAddr) {
        IpAddr = ipAddr;
    }

    public String getHwaddr() {
        return hwaddr;
    }

    public void setHwaddr(String hWAddr) {
        hwaddr = hWAddr;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public boolean isReachable() {
        return isReachable;
    }

    public void setReachable(boolean isReachable) {
        this.isReachable = isReachable;
    }
}
