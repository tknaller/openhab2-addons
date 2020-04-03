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
package org.openhab.binding.broadlink.handler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.core.thing.*;
import org.eclipse.smarthome.core.thing.type.ChannelTypeUID;
import org.eclipse.smarthome.core.transform.*;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.RefreshType;
import org.openhab.binding.broadlink.config.BroadlinkDeviceConfiguration;
import org.openhab.binding.broadlink.internal.Hex;
import org.openhab.binding.broadlink.internal.Utils;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Remote blaster handler
 *
 * @author John Marshall/Cato Sognen - Initial contribution
 */
@NonNullByDefault
public class BroadlinkRemoteHandler extends BroadlinkBaseThingHandler {

    public BroadlinkRemoteHandler(Thing thing) {
        super(thing, LoggerFactory.getLogger(BroadlinkRemoteHandler.class));
    }

    public BroadlinkRemoteHandler(Thing thing, Logger logger) {
        super(thing, logger);
    }

    protected void sendCode(byte code[]) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        BroadlinkDeviceConfiguration thingConfig = (BroadlinkDeviceConfiguration) getConfigAs(
                BroadlinkDeviceConfiguration.class);
        byte abyte0[];
        try {
            abyte0 = new byte[4];
            abyte0[0] = 2;
            //https://github.com/mjg59/python-broadlink/blob/0.13.0/broadlink/__init__.py#L50 add RM4 list
            //FIXME extend BroadlinkRemoteHandler to new type rm4 and extend sendCode to receive correct abyte0 
            if (Arrays.asList(0x5f36, 0x5f36, 0x610f,0x610e,0x62be).contains(thingConfig.getDeviceType())) {
                abyte0 = new byte[6];
                abyte0[0] = (byte) 0xd0;
                abyte0[2] = 2;
            }
            outputStream.write(abyte0);
            outputStream.write(code);
        } catch (IOException e) {
            thingLogger.logError("Exception while sending code", e);
        }
        if (outputStream.size() % 16 == 0) {
            sendAndReceiveDatagram(buildMessage((byte) 106, outputStream.toByteArray()), "remote code");
        } else {
            thingLogger.logError(
                    "Will not send remote code because it has an incorrect length (" + outputStream.size() + ")");
        }

    }

    public void handleCommand(ChannelUID channelUID, Command command) {
        if (!Utils.isOnline(getThing())) {
            thingLogger.logDebug("Can't handle command {} because handler for thing {} is not ONLINE", command,
                    getThing().getLabel());
            return;
        }
        if (command instanceof RefreshType) {
            updateItemStatus();
            return;
        }
        Channel channel = thing.getChannel(channelUID.getId());
        if (channel == null) {
            thingLogger.logError("Unexpected null channel while handling command {}", command.toFullString());
            return;
        }
        ChannelTypeUID channelTypeUID = channel.getChannelTypeUID();
        if (channelTypeUID == null) {
            thingLogger.logError("Unexpected null channelTypeUID while handling command {}", command.toFullString());
            return;
        }
        String s;
        try {
            switch ((s = channelTypeUID.getId()).hashCode()) {
            case 950394699: // FIXME WTF?!?!
                if (s.equals("command")) {
                    thingLogger.logDebug("Handling ir/rf command {} on channel {} of thing {}",
                            new Object[] { command, channelUID.getId(), getThing().getLabel() });
                    byte code[] = lookupCode(command, channelUID);
                    if (code != null)
                        sendCode(code);
                    break;
                }
                // fall through

            default:
                thingLogger.logDebug("Thing {} has unknown channel type {}", getThing().getLabel(),
                        channelTypeUID.getId());
                break;
            }
        } catch (IOException e) {
            thingLogger.logError("Exception while trying to send code", e);
        }
    }

    private byte @Nullable [] lookupCode(Command command, ChannelUID channelUID) {
        if (command.toString() == null) {
            thingLogger.logDebug("Unable to perform transform on null command string");
            return null;
        }
        String mapFile = (String) thing.getConfiguration().get("mapFilename");
        if (StringUtils.isEmpty(mapFile)) {
            thingLogger.logDebug("MAP file is not defined in configuration of thing {}", getThing().getLabel());
            return null;
        }
        BundleContext bundleContext = FrameworkUtil.getBundle(BroadlinkRemoteHandler.class).getBundleContext();
        TransformationService transformService = TransformationHelper.getTransformationService(bundleContext, "MAP");
        if (transformService == null) {
            thingLogger.logError("Failed to get MAP transformation service for thing {}; is bundle installed?",
                    getThing().getLabel());
            return null;
        }
        byte code[] = null;
        String value;
        try {
            value = transformService.transform(mapFile, command.toString());
            code = Hex.convertHexToBytes(value);
        } catch (TransformationException e) {
            thingLogger.logError("Failed to transform {} for thing {} using map file '{}', exception={}",
                    new Object[] { command, getThing().getLabel(), mapFile, e.getMessage() });
            return null;
        }
        if (StringUtils.isEmpty(value)) {
            thingLogger.logError("No entry for {} in map file '{}' for thing {}",
                    new Object[] { command, mapFile, getThing().getLabel() });
            return null;
        }
        thingLogger.logDebug("Transformed {} for thing {} with map file '{}'",
                new Object[] { command, getThing().getLabel(), mapFile });
        return code;
    }

}
