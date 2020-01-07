/**
 * Copyright (c) 2010-2020 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.broadlink.config;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

/**
 * Device configuration for the supported Broadlink devices.
 *
 * @author John Marshall/Cato Sognen - Initial contribution
 */
@NonNullByDefault
public class BroadlinkDeviceConfiguration {
    @Nullable private String ipAddress;
    private boolean staticIp;
    private int port;
    @Nullable private String mac;
    private int pollingInterval;
    @Nullable private String mapFilename;
    @Nullable private String authorizationKey;
    @Nullable private String iv;
    private int retries = 1;
    private boolean ignoreFailedUpdates = false;

    public BroadlinkDeviceConfiguration() {
        pollingInterval = 30;
        staticIp = true;
    }

    public @Nullable String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public boolean isStaticIp() {
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
        for (int i = 0; i < 6; i++) {
            String element = elements[i];
            configMac[i] = (byte) Integer.parseInt(element, 16);
        }

        return configMac;
    }

    public @Nullable String getMACAsString() {
        return mac;
    }

    public int getPollingInterval() {
        return pollingInterval;
    }

    public void setPollingInterval(int pollingInterval) {
        this.pollingInterval = pollingInterval;
    }

    public @Nullable String getMapFilename() {
        return mapFilename;
    }

    public void setMapFilename(String mapFilename) {
        this.mapFilename = mapFilename;
    }

    public @Nullable String getAuthorizationKey() {
        return authorizationKey;
    }

    public void setAuthorizationKey(String authorizationKey) {
        this.authorizationKey = authorizationKey;
    }

    public @Nullable String getIV() {
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

    public void setIgnoreFailedUpdates(boolean ignore) {
        this.ignoreFailedUpdates = ignore;
    }

    public boolean isIgnoreFailedUpdates() {
        return this.ignoreFailedUpdates;
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
