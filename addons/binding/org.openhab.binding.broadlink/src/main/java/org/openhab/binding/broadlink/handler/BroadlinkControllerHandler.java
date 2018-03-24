// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   BroadlinkControllerHandler.java

package org.openhab.binding.broadlink.handler;

import java.util.Collections;
import java.util.Set;
import org.eclipse.smarthome.core.thing.*;
import org.eclipse.smarthome.core.thing.binding.BaseBridgeHandler;
import org.eclipse.smarthome.core.types.Command;
import org.openhab.binding.broadlink.BroadlinkBindingConstants;
import org.openhab.binding.broadlink.internal.BroadlinkHandlerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Referenced classes of package org.openhab.binding.broadlink.handler:
//            ControllerStatusListener

public class BroadlinkControllerHandler extends BaseBridgeHandler
{

    public BroadlinkControllerHandler(Bridge bridge, BroadlinkHandlerFactory factory)
    {
        super(bridge);
        logger = LoggerFactory.getLogger(BroadlinkControllerHandler.class);
        this.factory = factory;
    }

    public void handleCommand(ChannelUID channeluid, Command command1)
    {
    }

    public void initialize()
    {
    }

    public void dispose()
    {
    }

    protected void updateStatus(ThingStatus status, ThingStatusDetail detail, String comment)
    {
        super.updateStatus(status, detail, comment);
        logger.debug("Updating listeners with status {}", status);
    }

    public void addControllerStatusListener(ControllerStatusListener controllerstatuslistener)
    {
    }

    public void removeControllerStatusListener(ControllerStatusListener controllerstatuslistener)
    {
    }

    private Logger logger;
    public static final Set SUPPORTED_THING_TYPES_UIDS;
    private BroadlinkHandlerFactory factory;

    static 
    {
        SUPPORTED_THING_TYPES_UIDS = Collections.singleton(BroadlinkBindingConstants.THING_TYPE_S1C);
    }
}
