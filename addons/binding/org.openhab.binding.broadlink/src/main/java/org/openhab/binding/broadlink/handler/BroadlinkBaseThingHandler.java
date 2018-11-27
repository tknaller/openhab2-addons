/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.broadlink.handler;

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
import org.openhab.binding.broadlink.config.BroadlinkDeviceConfiguration;
import org.openhab.binding.broadlink.internal.BroadlinkProtocol;
import org.openhab.binding.broadlink.internal.Hex;
import org.openhab.binding.broadlink.internal.Utils;
import org.openhab.binding.broadlink.internal.NetworkUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract superclass of all supported Broadlink devices.
 *
 * @author John Marshall/Cato Sognen - Initial contribution
 */
public abstract class BroadlinkBaseThingHandler extends BaseThingHandler {
    private static final Logger logger = LoggerFactory.getLogger(BroadlinkBaseThingHandler.class);
    private DatagramSocket socket = null;
    private boolean authenticated = false;
    private int count;
    private String authenticationKey;
    private String iv;
    protected BroadlinkDeviceConfiguration thingConfig;
    private ScheduledFuture<?> refreshHandle;

    public BroadlinkBaseThingHandler(Thing thing) {
        super(thing);
        count = 0;
    }

    private boolean hasAuthenticated() {
        return this.authenticated;
    }

    private Object[] prependUID(Object... args) {
        Object[] allArgs = new Object[args.length + 1];
        allArgs[0] = getThing().getUID();
        System.arraycopy(args, 0, allArgs, 1, args.length);
        return allArgs;
    }

    protected void logDebug(String msg, Object... args) {
        if (logger.isDebugEnabled()) {
            if (args.length > 0) {
                logger.debug("{}: " + msg, prependUID(args));
            } else {
                logger.debug("{}: {}", getThing().getUID(), msg);
            }
        }
    }

    protected void logError(String msg, Object... args) {
        if (args.length > 0) {
            logger.error("{}: " + msg, prependUID(args));
        } else {
            logger.error("{}: {}", getThing().getUID(), msg);
        }
    }

    protected void logInfo(String msg, Object... args) {
        if (args.length > 0) {
            logger.info("{}: " + msg, prependUID(args));
        } else {
            logger.info("{}: {}", getThing().getUID(), msg);
        }
    }

    protected void logTrace(String msg, Object... args) {
        if (logger.isTraceEnabled()) {
            if (args.length > 0) {
                logger.trace("{}: " + msg, prependUID(args));
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
            setProperty("id", null);
            setProperty("key", null);
            if (authenticate()) {
                updateStatus(ThingStatus.ONLINE);
            } else {
                updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR);
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
                updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR);
            }
        } else {
            logTrace("thing IV / Key has not changed; will not re-authenticate");
        }
        updateItemStatus();
    }

    public void dispose() {
        logDebug(getThing().getLabel() + " is being disposed");
        if (refreshHandle != null && !refreshHandle.isDone()) {
            logDebug("Cancelling refresh task");
            boolean cancelled = refreshHandle.cancel(true);
            logDebug("Cancellation successful: " + cancelled);
        }
        super.dispose();
    }

    protected boolean authenticate() {
        logDebug("Authenticating with packet count = {}", this.count);

        authenticated = false;
        if (!sendDatagram(buildMessage((byte) 0x65,  BroadlinkProtocol.buildAuthenticationPayload()), "authentication")) {
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
        byte decryptResponse[] = Utils.decrypt(
                Hex.convertHexToBytes(authenticationKey),
                new IvParameterSpec(Hex.convertHexToBytes(iv)), Utils.slice(response, 56, 88));
        byte deviceId[] = Utils.getDeviceId(decryptResponse);
        byte deviceKey[] = Utils.getDeviceKey(decryptResponse);
        setProperty("id", Hex.toHexString(deviceId));
        setProperty("key", Hex.toHexString(deviceKey));
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
                logTrace("No existing socket ... creating");
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
                return response;
            }
        } catch (SocketTimeoutException ste) {
            logDebug("No further " + purpose + " response received for device");
        } catch (Exception e) {
            logError("While {} - IO Exception: '{}'", purpose, e.getMessage());
        }

        return null;
    }

    protected byte[] buildMessage(byte command, byte payload[]) {
    	Map<String, String> properties = editProperties();
        byte id[];
        if (properties.get("id") == null) {
            id = new byte[4];
        } else {
            id = Hex.fromHexString(properties.get("id"));
        }
	byte key[];
	if (properties.get("key") == null || properties.get("id") == null) {
		key = Hex.convertHexToBytes(thingConfig.getAuthorizationKey());
	} else  {
		key = Hex.fromHexString(properties.get("key"));
	}
        count = count + 1 & 0xffff;
	return BroadlinkProtocol.buildMessage(
		command,
		payload,
		count,
        	thingConfig.getMAC(),
		id,
		Hex.convertHexToBytes(thingConfig.getIV()),
		key
       );	
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
        if (NetworkUtils.hostAvailabilityCheck(thingConfig.getIpAddress(), 3000)) {
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
                } else {
                    logError("Device became reachable but had trouble getting status. Marking as offline ...");
                    forceOffline();
                }
            } else {
                // Normal operation ...
                boolean gotStatusOk = getStatusFromDevice();
                if (!gotStatusOk) {
                    logError("Problem getting status. Marking as offline ...");
                    forceOffline();
                }
            }
        } else if (!isOffline()) {
            forceOffline();
        }
    }

    private void forceOffline() {
        logError("updateItemStatus: Online -> Offline");
        this.authenticated = false; // This session is dead; we'll need to re-authenticate next time
        setProperty("id", null);
        setProperty("key", null);
        updateStatus(
                ThingStatus.OFFLINE,
                ThingStatusDetail.COMMUNICATION_ERROR,
                (new StringBuilder("Could not control device at IP address ")).append(thingConfig.getIpAddress()).toString()
        );
    }

    private void setProperty(String propName, String propValue) {
        Map<String, String> properties = editProperties();
        properties.put(propName, propValue);
        updateProperties(properties);
    }

    protected boolean isOnline() {
        return thing.getStatus().equals(ThingStatus.ONLINE);
    }

    protected boolean isOffline() {
        return thing.getStatus().equals(ThingStatus.OFFLINE);
    }
}
