/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.broadlink.handler;

import java.util.*;
import java.util.concurrent.TimeUnit;
import javax.crypto.spec.IvParameterSpec;
import java.util.concurrent.ScheduledFuture;

import org.eclipse.smarthome.core.thing.*;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.RefreshType;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;

import org.openhab.binding.broadlink.config.BroadlinkDeviceConfiguration;
import org.openhab.binding.broadlink.internal.BroadlinkProtocol;
import org.openhab.binding.broadlink.internal.Hex;
import org.openhab.binding.broadlink.internal.ThingLogger;
import org.openhab.binding.broadlink.internal.Utils;
import org.openhab.binding.broadlink.internal.NetworkUtils;
import org.openhab.binding.broadlink.internal.discovery.DeviceRediscoveryAgent;
import org.openhab.binding.broadlink.internal.discovery.DeviceRediscoveryListener;
import org.openhab.binding.broadlink.internal.socket.RetryableSocket;
import org.slf4j.Logger;

/**
 * Abstract superclass of all supported Broadlink devices.
 *
 * @author John Marshall/Cato Sognen - Initial contribution
 */
public abstract class BroadlinkBaseThingHandler extends BaseThingHandler implements DeviceRediscoveryListener {
    private RetryableSocket socket;
    private boolean authenticated = false;
    private int count;
    private String authenticationKey;
    private String iv;
    protected BroadlinkDeviceConfiguration thingConfig;
    protected final ThingLogger thingLogger;
    private ScheduledFuture<?> refreshHandle;

    public BroadlinkBaseThingHandler(Thing thing, Logger logger) {
        super(thing);
        this.thingLogger = new ThingLogger(thing, logger);
        count = 0;
    }

    private boolean hasAuthenticated() {
        return this.authenticated;
    }

    public void initialize() {
        thingLogger.logDebug("initializing");

        count = (new Random()).nextInt(65535);
        thingConfig = (BroadlinkDeviceConfiguration) getConfigAs(BroadlinkDeviceConfiguration.class);
        this.socket = new RetryableSocket(thingConfig, thingLogger);
        if (iv != thingConfig.getIV() || authenticationKey != thingConfig.getAuthorizationKey()) {
            iv = thingConfig.getIV();
            authenticationKey = thingConfig.getAuthorizationKey();
            setProperty("id", null);
            setProperty("key", null);
        }
        thingLogger.logDebug("initialization complete. Updating status.");

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
        } else {
            updateItemStatus();
        }
    }

    public void thingUpdated(Thing thing) {
        thingLogger.logDebug("thingUpdated");
        if (iv != thingConfig.getIV() || authenticationKey != thingConfig.getAuthorizationKey()) {
            thingLogger.logTrace("thing IV / Key has changed; re-authenticating");
            iv = thingConfig.getIV();
            authenticationKey = thingConfig.getAuthorizationKey();
            if (authenticate()) {
                updateStatus(ThingStatus.ONLINE);
            } else {
                updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR);
            }
        } else {
            thingLogger.logTrace("thing IV / Key has not changed; will not re-authenticate");
        }
        updateItemStatus();
    }

    public void dispose() {
        thingLogger.logDebug(getThing().getLabel() + " is being disposed");
        if (refreshHandle != null && !refreshHandle.isDone()) {
            thingLogger.logDebug("Cancelling refresh task");
            boolean cancelled = refreshHandle.cancel(true);
            thingLogger.logDebug("Cancellation successful: " + cancelled);
        }
        if (socket != null) {
            socket.close();
        }
        super.dispose();
    }

    protected boolean authenticate() {
        thingLogger.logDebug("Authenticating with packet count = {}", this.count);

        authenticated = false;
        byte authRequest[] = buildMessage((byte) 0x65, BroadlinkProtocol.buildAuthenticationPayload());
        byte response[] = sendAndReceiveDatagram(authRequest, "authentication");
        try {
            byte decryptResponse[] = BroadlinkProtocol.decodePacket(response, thingConfig, null);
            byte deviceId[] = Utils.getDeviceId(decryptResponse);
            byte deviceKey[] = Utils.getDeviceKey(decryptResponse);
            setProperty("id", Hex.toHexString(deviceId));
            setProperty("key", Hex.toHexString(deviceKey));
            thingLogger.logDebug(
                    "Authenticated with id '{}' and key '{}'.",
                    Hex.toHexString(deviceId),
                    Hex.toHexString(deviceKey)
            );
            authenticated = true;
            return true;
        } catch (Exception e) {
            thingLogger.logError("Authentication failed: {}", e);
            return false;
        }

    }

    protected byte[] sendAndReceiveDatagram(byte message[], String purpose) {
        return socket.sendAndReceive(message, purpose);
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
        thingLogger.logTrace("building message with count: {}, id: {}, key: {}", count, Hex.toHexString(id), Hex.toHexString(key));
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
        thingLogger.logDebug("handleCommand " + command.toString());
        if (command instanceof RefreshType) {
            thingLogger.logTrace("Refresh requested, updating item status ...");

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
        thingLogger.logTrace("updateItemStatus; checking host availability at {}", thingConfig.getIpAddress());
        if (NetworkUtils.hostAvailabilityCheck(thingConfig.getIpAddress(), 3000)) {
            if (!Utils.isOnline(getThing())) {
				transitionToOnline();
            } else {
                // Normal operation ...
                boolean gotStatusOk = getStatusFromDevice();
                if (!gotStatusOk) {
                    thingLogger.logError("Problem getting status. Marking as offline ...");
                    forceOffline();
                }
            }
        } else {
			if (thingConfig.isStaticIp()) {
				if (!Utils.isOffline(getThing())) {
                    thingLogger.logDebug("Statically-IP-addressed device not found at {}", thingConfig.getIpAddress());
            		forceOffline();
				}
			} else {
                thingLogger.logDebug("Dynamic IP device not found at {}, will search...", thingConfig.getIpAddress());
				DeviceRediscoveryAgent dra = new DeviceRediscoveryAgent(thingConfig, this);
				dra.attemptRediscovery();
                thingLogger.logDebug("Asynchronous dynamic IP device search initiated...");
			}
        }
    }

	public void onDeviceRediscovered(String newIpAddress) {
        thingLogger.logInfo("Rediscovered this device at IP {}", newIpAddress);
		thingConfig.setIpAddress(newIpAddress);
		transitionToOnline();
	}

	public void onDeviceRediscoveryFailure() {
		if (!Utils.isOffline(getThing())) {
            thingLogger.logDebug("Dynamically-IP-addressed device not found after network scan. Marking offline");
			forceOffline();
		}
	}

	private void transitionToOnline() {
		if (!hasAuthenticated()) {
            thingLogger.logDebug("We've never actually successfully authenticated with this device in this session. Doing so now");
			if (authenticate()) {
                thingLogger.logDebug("Authenticated with newly-detected device, will now get its status");
			} else {
                thingLogger.logError("Attempting to authenticate prior to getting device status FAILED. Will mark as offline");
                forceOffline();
                return;
			}
		}
		if (onBroadlinkDeviceBecomingReachable()) {
            thingLogger.logDebug("updateStatus: Offline -> Online");
			updateStatus(ThingStatus.ONLINE);
		} else {
            thingLogger.logError("Device became reachable but had trouble getting status. Marking as offline ...");
			forceOffline();
		}
	}

    private void forceOffline() {
        thingLogger.logError("updateItemStatus: Online -> Offline");
        this.authenticated = false; // This session is dead; we'll need to re-authenticate next time
        setProperty("id", null);
        setProperty("key", null);
        updateStatus(
			ThingStatus.OFFLINE,
			ThingStatusDetail.COMMUNICATION_ERROR,
			(new StringBuilder("Could not find device at IP address ")).append(thingConfig.getIpAddress()).toString()
        );
        if (socket != null) {
            socket.close();
        }
    }

    private void setProperty(String propName, String propValue) {
        Map<String, String> properties = editProperties();
        properties.put(propName, propValue);
        updateProperties(properties);
    }
}
