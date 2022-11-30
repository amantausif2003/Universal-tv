package com.remote.control.allsmarttv.Utils;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.util.ArrayList;
import java.util.List;

public final class MdnsResUtil {
    private String mFqdn;
    private List<Inet4Address> mInet4Addresses;
    private List<Inet6Address> mInet6Addresses;
    private String mServiceHost;
    private String mServiceInstanceName;
    private String mServiceName;
    private int mServicePort;
    private int mServicePriority;
    private int mServiceProtocol;
    private int mServiceWeight;
    private List<String> mTextStrings;
    private long mTimeToLive;

    public static class Builder {
        private MdnsResUtil mResponse = new MdnsResUtil();

        public Builder addInet4Address(Inet4Address inet4Address) {
            this.mResponse.addInet4Address(inet4Address);
            return this;
        }

        public Builder addInet6Address(Inet6Address inet6Address) {
            this.mResponse.addInet6Address(inet6Address);
            return this;
        }

        public Builder addTextString(String str) {
            this.mResponse.addTextString(str);
            return this;
        }

        public MdnsResUtil build() {
            return this.mResponse;
        }

        public Builder setFqdn(String str) {
            this.mResponse.setFqdn(str);
            return this;
        }

        public Builder setServiceHost(String str) {
            this.mResponse.setServiceHost(str);
            return this;
        }

        public Builder setServiceInstanceName(String str) {
            this.mResponse.setServiceInstanceName(str);
            return this;
        }

        public Builder setServiceName(String str) {
            this.mResponse.setServiceName(str);
            return this;
        }

        public Builder setServicePort(int i) {
            this.mResponse.setServicePort(i);
            return this;
        }

        public Builder setServicePriority(int i) {
            this.mResponse.setServicePriority(i);
            return this;
        }

        public Builder setServiceProtocol(int i) {
            this.mResponse.setServiceProtocol(i);
            return this;
        }

        public Builder setServiceWeight(int i) {
            this.mResponse.setServiceWeight(i);
            return this;
        }

        public Builder updateTimeToLive(long j) {
            this.mResponse.updateTimeToLive(j);
            return this;
        }
    }

    private MdnsResUtil() {
        this.mServiceProtocol = 0;
        this.mTimeToLive = -1;
    }


    private void addInet4Address(Inet4Address inet4Address) {
        if (this.mInet4Addresses == null) {
            this.mInet4Addresses = new ArrayList();
        }
        this.mInet4Addresses.add(inet4Address);
    }


    private void addInet6Address(Inet6Address inet6Address) {
        if (this.mInet6Addresses == null) {
            this.mInet6Addresses = new ArrayList();
        }
        this.mInet6Addresses.add(inet6Address);
    }


    private void setFqdn(String str) {
        this.mFqdn = str;
    }


    private void setServiceHost(String str) {
        this.mServiceHost = str;
    }


    private void setServiceInstanceName(String str) {
        this.mServiceInstanceName = str;
    }


    private void setServiceName(String str) {
        this.mServiceName = str;
    }


    private void setServicePort(int i) {
        this.mServicePort = i;
    }


    private void setServicePriority(int i) {
        this.mServicePriority = i;
    }


    private void setServiceProtocol(int i) {
        this.mServiceProtocol = i;
    }


    private void setServiceWeight(int i) {
        this.mServiceWeight = i;
    }

    public void addTextString(String str) {
        if (this.mTextStrings == null) {
            this.mTextStrings = new ArrayList();
        }
        this.mTextStrings.add(str);
    }

    public String getFqdn() {
        return this.mFqdn;
    }

    public List<Inet4Address> getInet4Addresses() {
        return this.mInet4Addresses;
    }

    public List<Inet6Address> getInet6Addresses() {
        return this.mInet6Addresses;
    }

    public String getServiceHost() {
        return this.mServiceHost;
    }

    public String getServiceInstanceName() {
        return this.mServiceInstanceName;
    }

    public String getServiceName() {
        return this.mServiceName;
    }

    public int getServicePort() {
        return this.mServicePort;
    }

    public int getServicePriority() {
        return this.mServicePriority;
    }

    public int getServiceProtocol() {
        return this.mServiceProtocol;
    }

    public int getServiceWeight() {
        return this.mServiceWeight;
    }

    public List<String> getTextStrings() {
        return this.mTextStrings;
    }

    public long getTimeToLive() {
        return this.mTimeToLive;
    }

    public void updateTimeToLive(long j) {
        boolean z = true;
        if (!(this.mTimeToLive < 0)) {
            if (j < this.mTimeToLive) {
                z = false;
            }
            if (z) {
                return;
            }
        }
        this.mTimeToLive = j;
    }
}
