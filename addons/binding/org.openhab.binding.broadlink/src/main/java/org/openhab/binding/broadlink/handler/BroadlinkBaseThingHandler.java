/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.broadlink.handler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.crypto.spec.IvParameterSpec;
import java.util.concurrent.ScheduledFuture;

import org.eclipse.smarthome.core.thing.*;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.RefreshType;
import org.openhab.binding.broadlink.BroadlinkBindingConstants;
import org.openhab.binding.broadlink.config.BroadlinkDeviceConfiguration;
import org.openhab.binding.broadlink.internal.Hex;
import org.openhab.binding.broadlink.internal.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract superclass of all supported Broadlink devices.
 *
 * @author John Marshall/Cato Sognen - Initial contribution
 */
public abstract class BroadlinkBaseThingHandler extends BaseThingHandler {
    public static final Set SUPPORTED_THING_TYPES;
    private static final Logger logger = LoggerFactory.getLogger(BroadlinkBaseThingHandler.class);
    private DatagramSocket socket = null;
    private boolean authenticated = false;
    private int count;
    private String authenticationKey;
    private String iv;
    protected BroadlinkDeviceConfiguration thingConfig;
    private ScheduledFuture<?> refreshHandle;

    static {
        SUPPORTED_THING_TYPES = new HashSet(Arrays.asList(new ThingTypeUID[]{
                BroadlinkBindingConstants.THING_TYPE_A1,
                BroadlinkBindingConstants.THING_TYPE_RM,
                BroadlinkBindingConstants.THING_TYPE_RM2,
                BroadlinkBindingConstants.THING_TYPE_RM3,
                BroadlinkBindingConstants.THING_TYPE_SP1,
                BroadlinkBindingConstants.THING_TYPE_SP2,
                BroadlinkBindingConstants.THING_TYPE_MP1,
                BroadlinkBindingConstants.THING_TYPE_MP2,
                BroadlinkBindingConstants.THING_TYPE_SP3
        }));
    }

    public BroadlinkBaseThingHandler(Thing thing) {
        super(thing);
        count = 0;
    }

    private boolean hasAuthenticated() {
        return this.authenticated;
    }

    protected void logDebug(String msg, Object... args) {
        if (logger.isDebugEnabled()) {
            if (args.length > 0) {
                logger.debug("{}: {} {}", getThing().getUID(), msg, args);
            } else {
                logger.debug("{}: {}", getThing().getUID(), msg);
            }
        }
    }

    protected void logError(String msg, Object... args) {
        if (args.length > 0) {
            logger.error("{}: {} {}", getThing().getUID(), msg, args);
        } else {
            logger.error("{}: {}", getThing().getUID(), msg);
        }
    }

    protected void logTrace(String msg, Object... args) {
        if (logger.isTraceEnabled()) {
            if (args.length > 0) {
                logger.trace("{}: {} {}", getThing().getUID(), msg, args);
            } else {
                logger.trace("{}: {}", getThing().getUID(), msg);
            }
        }
    }

    public void initialize() {
        logDebug("initializing");

        count = (new Random()).nextInt(65535);
        thingConfig = (BroadlinkDeviceConfiguration) getConfigAs(BroadlinkDeviceConfiguration.class);
        if (iv != thingConfig.getIV() || authenticationKey != thingConfig.getAuthorizationKey()) {
            iv = thingConfig.getIV();
            authenticationKey = thingConfig.getAuthorizationKey();
            Map properties = editProperties();
            properties.put("id", null);
            properties.put("key", null);
            updateProperties(properties);
            if (authenticate()) {
                updateStatus(ThingStatus.ONLINE);
            } else {
                resetPacketCounter();
                updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR);
            }
        }
        logDebug("initialization complete");

        if (thingConfig.getPollingInterval() != 0) {
            refreshHandle = scheduler.scheduleWithFixedDelay(
                new Runnable() {

                public void run() {
                    updateItemStatus();
                }
            },
                1L,
                thingConfig.getPollingInterval(),
                TimeUnit.SECONDS
            );
        }
    }

    public void thingUpdated(Thing thing) {
        logDebug("thingUpdated");
        if (iv != thingConfig.getIV() || authenticationKey != thingConfig.getAuthorizationKey()) {
            logTrace("thing IV / Key has changed; re-authenticating");
            iv = thingConfig.getIV();
            authenticationKey = thingConfig.getAuthorizationKey();
            if (authenticate()) {
                updateStatus(ThingStatus.ONLINE);
            } else {
                resetPacketCounter();
                updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR);
            }
        } else {
            logTrace("thing IV / Key has not changed; will not re-authenticate");
        }
        updateItemStatus();
    }

    public void dispose() {
        logError(getThing().getLabel() + " is being disposed");
        if (refreshHandle != null && !refreshHandle.isDone()) {
            logDebug("Cancelling refresh task");
            boolean cancelled = refreshHandle.cancel(true);
            logDebug("Refresh successful: " + cancelled);
        }
        super.dispose();
    }

    // https://github.com/mjg59/python-broadlink/blob/master/protocol.md
    protected boolean authenticate() {
        byte payload[] = new byte[80];
        payload[4] = 49;
        payload[5] = 49;
        payload[6] = 49;
        payload[7] = 49;
        payload[8] = 49;
        payload[9] = 49;
        payload[10] = 49;
        payload[11] = 49;
        payload[12] = 49;
        payload[13] = 49;
        payload[14] = 49;
        payload[15] = 49;
        payload[16] = 49;
        payload[17] = 49;
        payload[18] = 49;
        payload[30] = 1;
        payload[45] = 1;
        payload[48] = 84;
        payload[49] = 101;
        payload[50] = 115;
        payload[51] = 116;
        payload[52] = 32;
        payload[53] = 32;
        payload[54] = 49;

        authenticated = false;
        if (!sendDatagram(buildMessage((byte) 101, payload), "authentication")) {
            logError("Authenticate - failed to send.");
            return false;
        }
        byte response[] = receiveDatagram("authentication");
        if (response == null) {
            logError("Authenticate - failed to receive.");
            return false;
        }
        int error = response[34] | response[35] << 8;
        if (error != 0) {
            logError("Authenticated -received error '{}'", String.valueOf(error));
            return false;
        }
        byte decryptResponse[] = Utils.decrypt(Hex.convertHexToBytes(authenticationKey), new IvParameterSpec(Hex.convertHexToBytes(iv)), Utils.slice(response, 56, 88));
        byte deviceId[] = Utils.getDeviceId(decryptResponse);
        byte deviceKey[] = Utils.getDeviceKey(decryptResponse);
        Map properties = editProperties();
        properties.put("key", Hex.toHexString(deviceKey));
        properties.put("id", Hex.toHexString(deviceId));
        updateProperties(properties);
        thingConfig = (BroadlinkDeviceConfiguration) getConfigAs(BroadlinkDeviceConfiguration.class);
        logDebug(
            "Authenticated with id '{}' and key '{}'.",
            Hex.toHexString(deviceId),
            Hex.toHexString(deviceKey)
        );
        authenticated = true;
        return true;
    }

    public boolean sendDatagram(byte message[]) {
        return sendDatagram(message, "Normal Operation");
    }


    public boolean sendDatagram(byte message[], String purpose) {
        try {
            logTrace("Sending " + purpose + " to " + thingConfig.getIpAddress() + ":" + thingConfig.getPort());
            if (socket == null || socket.isClosed()) {
                socket = new DatagramSocket();
                socket.setBroadcast(true);
                socket.setReuseAddress(true);
                socket.setSoTimeout(5000);
            }
            InetAddress host = InetAddress.getByName(thingConfig.getIpAddress());
            int port = thingConfig.getPort();
            DatagramPacket sendPacket = new DatagramPacket(message, message.length, new InetSocketAddress(host, port));
            socket.send(sendPacket);
        } catch (IOException e) {
            logger.error("IO error for device '{}' during UDP command sending: {}", getThing().getUID(), e.getMessage());
            return false;
        }
        logTrace("Sending " + purpose + " complete");
        return true;
    }

    public byte[] receiveDatagram(String purpose) {
        logTrace("Receiving " + purpose);

        try {
            if (socket == null) {
                logError("receiveDatagram " + purpose + " for socket was unexpectedly null");
            } else {
                byte response[] = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(response, response.length);
                socket.receive(receivePacket);
                response = receivePacket.getData();
                logTrace("Receiving " + purpose + " complete (OK)");
                return response;
            }
        } catch (SocketTimeoutException ste) {
            logDebug("No further " + purpose + " response received for device");
        } catch (Exception e) {
            logger.error("While {} - IO Exception: '{}'", purpose, e.getMessage());
        }

        return null;
    }

    /** If initial auth fails, don't advance the counter */
    private void resetPacketCounter() {
        logDebug("Resetting packet counter to 0");
        count = 0x0000;
    }

    protected byte[] buildMessage(byte command, byte payload[]) {
        count = count + 1 & 0xffff;
        byte packet[] = new byte[56];
        byte mac[] = thingConfig.getMAC();
        Map properties = editProperties();
        byte id[];
        if (properties.get("id") == null) {
            id = new byte[4];
        } else {
            id = Hex.fromHexString((String) properties.get("id"));
        }
        packet[0] = 0x5a;
        packet[1] = (byte) 0xa5;
        packet[2] = (byte) 0xaa;
        packet[3] = 0x55;
        packet[4] = 0x5a;
        packet[5] = (byte) 0xa5;
        packet[6] = (byte) 0xaa;
        packet[7] = 0x55;
        packet[36] = 42;
        packet[37] = 39;
        packet[38] = command;
        packet[40] = (byte) (count & 0xff);
        packet[41] = (byte) (count >> 8);
        packet[42] = mac[0];
        packet[43] = mac[1];
        packet[44] = mac[2];
        packet[45] = mac[3];
        packet[46] = mac[4];
        packet[47] = mac[5];
        packet[48] = id[0];
        packet[49] = id[1];
        packet[50] = id[2];
        packet[51] = id[3];
        int checksum = 0xBEAF;
        int i = 0;
        byte abyte0[];
        int k = (abyte0 = payload).length;
        for (int j = 0; j < k; j++) {
            byte b = abyte0[j];
            i = Byte.toUnsignedInt(b);
            checksum += i;
            checksum &= 0xffff;
        }

        packet[52] = (byte) (checksum & 0xff);
        packet[53] = (byte) (checksum >> 8);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            outputStream.write(packet);
            if (properties.get("key") == null || properties.get("id") == null) {
                outputStream.write(Utils.encrypt(Hex.convertHexToBytes(thingConfig.getAuthorizationKey()), new IvParameterSpec(Hex.convertHexToBytes(thingConfig.getIV())), payload));
            } else  {
                outputStream.write(Utils.encrypt(Hex.fromHexString((String) properties.get("key")), new IvParameterSpec(Hex.convertHexToBytes(thingConfig.getIV())), payload));
            }
        } catch (IOException e) {
            logError("IOException while building message", e);
            return null;
        }
        byte data[] = outputStream.toByteArray();
        checksum = 0xBEAF;
        byte abyte1[];
        int i1 = (abyte1 = data).length;
        for (int l = 0; l < i1; l++) {
            byte b = abyte1[l];
            i = Byte.toUnsignedInt(b);
            checksum += i;
            checksum &= 0xffff;
        }

        data[32] = (byte) (checksum & 0xff);
        data[33] = (byte) (checksum >> 8);
        return data;
    }

    public void handleCommand(ChannelUID channelUID, Command command) {
        logDebug("handleCommand " + command.toString());
        if (command instanceof RefreshType) {
            logTrace("Refresh requested, updating item status ...");

            updateItemStatus();
        }
    }

    // Can be implemented by devices that should do something on being found; e.g. perform a first status query
    protected boolean onBroadlinkDeviceBecomingReachable() {
        return true;
    }

    // Implemented by devices that can update the openHAB state
    // model. Return false if something went wrong that requires
    // a change in the device's online state
    protected boolean getStatusFromDevice() {
        return true;
    }

    public void updateItemStatus() {
        logTrace("updateItemStatus; checking host availability at {}", thingConfig.getIpAddress());
        if (hostAvailabilityCheck(thingConfig.getIpAddress(), 3000)) {
            if (!isOnline()) {
                if (!hasAuthenticated()) {
                    logDebug("We've never actually successfully authenticated with this device in this session. Doing so now");
                    if (authenticate()) {
                        logDebug("Authenticated with newly-detected device, will now get its status");
                    } else {
                        logError("Attempting to authenticate prior to getting device status FAILED");
                        return;
                    }
                }
                if (onBroadlinkDeviceBecomingReachable()) {
                    logDebug("updateItemStatus: Offline -> Online");
                    updateStatus(ThingStatus.ONLINE);
                }
            } else {
                getStatusFromDevice();
            }
        } else if (!isOffline()) {
            logError("updateItemStatus: Online -> Offline");
            this.authenticated = false; // This session is dead; we'll need to re-authenticate next time
            updateStatus(
                ThingStatus.OFFLINE,
                ThingStatusDetail.COMMUNICATION_ERROR,
                (new StringBuilder("Could not control device at IP address ")).append(thingConfig.getIpAddress()).toString()
            );
        }
    }

    protected static boolean hostAvailabilityCheck(String host, int timeout) {
        try {
            InetAddress address = InetAddress.getByName(host);
            return address.isReachable(timeout);
        } catch (Exception e) {
            logger.error("Exception while trying to determine reachability of {}", host, e);
        }
        return false;
    }

    protected boolean isOnline() {
        return thing.getStatus().equals(ThingStatus.ONLINE);
    }

    protected boolean isOffline() {
        return thing.getStatus().equals(ThingStatus.OFFLINE);
    }
}
