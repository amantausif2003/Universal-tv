package com.remote.control.allsmarttv.Utils;

public class Device_rokuTv extends com.jaku.model.Device {

    private String customUserDeviceName;

    public static Device_rokuTv fromDevice (com.jaku.model.Device jakuDevice)
    {
        Device_rokuTv device = new Device_rokuTv();
        device.setHost(jakuDevice.getHost());
        device.setUdn(jakuDevice.getUdn());
        device.setSerialNumber(jakuDevice.getSerialNumber());
        device.setDeviceId(jakuDevice.getDeviceId());
        device.setVendorName(jakuDevice.getVendorName());
        device.setModelNumber(jakuDevice.getModelNumber());
        device.setModelName(jakuDevice.getModelName());
        device.setWifiMac(jakuDevice.getWifiMac());
        device.setEthernetMac(jakuDevice.getEthernetMac());
        device.setNetworkType(jakuDevice.getNetworkType());
        device.setUserDeviceName(jakuDevice.getUserDeviceName());
        device.setSoftwareVersion(jakuDevice.getSoftwareVersion());
        device.setSoftwareBuild(jakuDevice.getSoftwareBuild());
        device.setSecureDevice(jakuDevice.getSecureDevice());
        device.setLanguage(jakuDevice.getLanguage());
        device.setCountry(jakuDevice.getCountry());
        device.setLocale(jakuDevice.getLocale());
        device.setTimeZone(jakuDevice.getTimeZone());
        device.setTimeZoneOffset(jakuDevice.getTimeZoneOffset());
        device.setPowerMode(jakuDevice.getPowerMode());
        device.setSupportsSuspend(jakuDevice.getSupportsSuspend());
        device.setSupportsFindRemote(jakuDevice.getSupportsFindRemote());
        device.setSupportsAudioGuide(jakuDevice.getSupportsAudioGuide());
        device.setDeveloperEnabled(jakuDevice.getDeveloperEnabled());
        device.setKeyedDeveloperId(jakuDevice.getKeyedDeveloperId());
        device.setSearchEnabled(jakuDevice.getSearchEnabled());
        device.setVoiceSearchEnabled(jakuDevice.getVoiceSearchEnabled());
        device.setNotificationsEnabled(jakuDevice.getNotificationsEnabled());
        device.setNotificationsFirstUse(jakuDevice.getNotificationsFirstUse());
        device.setSupportsPrivateListening(jakuDevice.getSupportsPrivateListening());
        device.setHeadphonesConnected(jakuDevice.getHeadphonesConnected());
        device.setIsTv(jakuDevice.getIsTv());
        device.setIsStick(jakuDevice.getIsStick());

        return device;
    }

    public String getCustomUserDeviceName(){
        return customUserDeviceName;
    }

    public void setCustomUserDeviceName(String customUserDeviceName) {
        this.customUserDeviceName = customUserDeviceName;
    }
}
