package org.openhab.binding.broadlink.config;

public class BroadlinkDeviceConfiguration {
    private String ipAddress;
    private int port;
    private String mac;
    private int pollingInterval;
    private String mapFilename;
    private String authorizationKey;
    private String iv;

    public BroadlinkDeviceConfiguration() {
        pollingInterval = 30;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    public void setMAC(String mac) {
        this.mac = mac;
    }

    public byte[] getMAC() {
        byte configMac[] = new byte[6];
        String elements[] = mac.split(":");
        for(int i = 0; i < 6; i++)
        {
            String element = elements[i];
            configMac[i] = (byte)Integer.parseInt(element, 16);
        }

        return configMac;
    }

    public int getPollingInterval() {
        return pollingInterval;
    }

    public void setPollingInterval(int pollingInterval) {
        this.pollingInterval = pollingInterval;
    }

    public String getMapFilename() {
        return mapFilename;
    }

    public void setMapFilename(String mapFilename) {
        this.mapFilename = mapFilename;
    }

    public String getAuthorizationKey() {
        return authorizationKey;
    }

    public void setAuthorizationKey(String authorizationKey) {
        this.authorizationKey = authorizationKey;
    }

    public String getIV() {
        return iv;
    }

    public void setIV(String iv) {
        this.iv = iv;
    }

    public String toString() {
        return (new StringBuilder("BroadlinkDeviceConfiguration [ipAddress=")).append(ipAddress).append(", port=").append(port).append(", mac=").append(mac).append(", pollingInterval=").append(pollingInterval).append(", mapFilename=").append(mapFilename).append(", authorizationKey=").append(authorizationKey).append(", iv=").append(iv).append("]").toString();
    }

}
