/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.broadlink.config;

/**
 * Device configuration for the supported Broadlink devices.
 *
 * @author John Marshall/Cato Sognen - Initial contribution
 */
public class BroadlinkDeviceConfiguration {
    private String ipAddress;
    private boolean staticIp;
    private int port;
    private String mac;
    private int pollingInterval;
    private String mapFilename;
    private String authorizationKey;
    private String iv;
    private int retries = 1;

    public BroadlinkDeviceConfiguration() {
        pollingInterval = 30;
	    staticIp = true;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public boolean isStaticIp () {
        return staticIp;
    }

    public void setStaticIp(boolean staticIp) {
        this.staticIp = staticIp;
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

    public String getMACAsString() {
		return mac;
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

    public void setRetries(int retries) {
        this.retries = retries;
    }

    public int getRetries() {
        return this.retries;
    }

    public String toString() {
        return (
		new StringBuilder("BroadlinkDeviceConfiguration [ipAddress="))
			.append(ipAddress)
			.append(" (static: ")
			.append(staticIp)
			.append("), port=")
			.append(port)
			.append(", mac=")
			.append(mac)
			.append(", pollingInterval=")
			.append(pollingInterval)
			.append(", mapFilename=")
			.append(mapFilename)
			.append(", authorizationKey=")
			.append(authorizationKey)
			.append(", iv=")
			.append(iv)
			.append("]").toString();
    }

}
