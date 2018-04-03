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

public class BroadlinkControllerHandler extends BaseBridgeHandler {
    private final Logger logger = LoggerFactory.getLogger(BroadlinkControllerHandler.class);
    public static final Set SUPPORTED_THING_TYPES_UIDS;
    private final BroadlinkHandlerFactory factory;

    static {
        SUPPORTED_THING_TYPES_UIDS = Collections.singleton(BroadlinkBindingConstants.THING_TYPE_S1C);
    }

    public BroadlinkControllerHandler(Bridge bridge, BroadlinkHandlerFactory factory) {
        super(bridge);
        this.factory = factory;
    }

    public void handleCommand(ChannelUID channeluid, Command command1) {
    }

    public void initialize() {
    }

    public void dispose() {
    }

    protected void updateStatus(ThingStatus status, ThingStatusDetail detail, String comment) {
        super.updateStatus(status, detail, comment);
        logger.debug("Updating listeners with status {}", status);
    }

    public void addControllerStatusListener(ControllerStatusListener controllerstatuslistener) {
    }

    public void removeControllerStatusListener(ControllerStatusListener controllerstatuslistener) {
    }

}
