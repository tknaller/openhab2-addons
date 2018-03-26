// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   BroadlinkRemoteHandler.java

package org.openhab.binding.broadlink.handler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.eclipse.smarthome.config.core.Configuration;
import org.eclipse.smarthome.core.thing.*;
import org.eclipse.smarthome.core.thing.type.ChannelTypeUID;
import org.eclipse.smarthome.core.transform.*;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.RefreshType;
import org.openhab.binding.broadlink.internal.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Referenced classes of package org.openhab.binding.broadlink.handler:
//            BroadlinkBaseThingHandler

public class BroadlinkRemoteHandler extends BroadlinkBaseThingHandler {

    public BroadlinkRemoteHandler(Thing thing) {
        super(thing);
        logger = LoggerFactory.getLogger(BroadlinkRemoteHandler.class);
    }

    protected void sendCode(byte code[]) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte abyte0[];
        try {
            abyte0 = new byte[4];
            abyte0[0] = 2;
            outputStream.write(abyte0);
            outputStream.write(code);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (outputStream.size() % 16 == 0)
            sendDatagram(buildMessage((byte) 106, outputStream.toByteArray()));
    }

    public void handleCommand(ChannelUID channelUID, Command command) {
        if (command == null) {
            if (logger.isDebugEnabled())
                logger.debug("Command passed to handler for thing {} is null");
            return;
        }
        if (!isOnline()) {
            if (logger.isDebugEnabled())
                logger.debug("Can't handle command {} because handler for thing {} is not ONLINE", command, getThing().getLabel());
            return;
        }
        if (command instanceof RefreshType) {
            updateItemStatus();
            return;
        }
        Channel channel = thing.getChannel(channelUID.getId());
        String s;
        switch ((s = channel.getChannelTypeUID().getId()).hashCode()) {
            case 950394699:
                if (s.equals("command")) {
                    if (logger.isDebugEnabled())
                        logger.debug("Handling ir/rf command {} on channel {} of thing {}", new Object[]{
                                command, channelUID.getId(), getThing().getLabel()
                        });
                    byte code[] = lookupCode(command, channelUID);
                    if (code != null)
                        sendCode(code);
                    break;
                }
                // fall through

            default:
                if (logger.isDebugEnabled())
                    logger.debug("Thing {} has unknown channel type {}", getThing().getLabel(), channel.getChannelTypeUID().getId());
                break;
        }
    }

    private byte[] lookupCode(Command command, ChannelUID channelUID) {
        if (command.toString() == null) {
            if (logger.isDebugEnabled())
                logger.debug("Unable to perform transform on null command string");
            return null;
        }
        String mapFile = (String) thing.getConfiguration().get("mapFilename");
        if (StringUtils.isEmpty(mapFile)) {
            if (logger.isDebugEnabled())
                logger.debug("MAP file is not defined in configuration of thing {}", getThing().getLabel());
            return null;
        }
        TransformationService transformService = TransformationHelper.getTransformationService(bundleContext, "MAP");
        if (transformService == null) {
            logger.error("Failed to get MAP transformation service for thing {}; is bundle installed?", getThing().getLabel());
            return null;
        }
        byte code[] = null;
        String value;
        try {
            value = transformService.transform(mapFile, command.toString());
            code = Hex.convertHexToBytes(value);
        } catch (TransformationException e) {
            logger.error("Failed to transform {} for thing {} using map file '{}', exception={}", new Object[]{
                    command, getThing().getLabel(), mapFile, e.getMessage()
            });
            return null;
        }
        if (StringUtils.isEmpty(value)) {
            logger.error("No entry for {} in map file '{}' for thing {}", new Object[]{
                    command, mapFile, getThing().getLabel()
            });
            return null;
        }
        if (logger.isDebugEnabled())
            logger.debug("Transformed {} for thing {} with map file '{}'", new Object[]{
                    command, getThing().getLabel(), mapFile
            });
        return code;
    }

    private Logger logger;
}
