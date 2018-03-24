// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   BroadlinkDeviceDiscoveryService.java

package org.openhab.binding.broadlink.internal.discovery;

import java.util.*;

import org.eclipse.smarthome.config.discovery.AbstractDiscoveryService;
import org.eclipse.smarthome.config.discovery.DiscoveryResultBuilder;
import org.eclipse.smarthome.core.thing.*;
import org.openhab.binding.broadlink.BroadlinkBindingConstants;
import org.openhab.binding.broadlink.handler.BroadlinkControllerHandler;
import org.openhab.binding.broadlink.handler.ControllerStatusListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BroadlinkDeviceDiscoveryService extends AbstractDiscoveryService
        implements ControllerStatusListener {

    public BroadlinkDeviceDiscoveryService(BroadlinkControllerHandler controller) {
        super(SUPPORTED_THING_TYPES_UIDS, 10, true);
        logger = LoggerFactory.getLogger(BroadlinkDeviceDiscoveryService.class);
        logger.debug("BroadlinkDeviceDiscoveryService {}", controller);
        this.controller = controller;
        this.controller.addControllerStatusListener(this);
    }

    protected void startScan() {
        discoverDevices();
    }

    protected void startBackgroundDiscovery() {
        discoverDevices();
    }

    public void controllerStatusChanged(ThingStatus status) {
        if (status.equals(ThingStatus.ONLINE))
            discoverDevices();
    }

    protected void deactivate() {
        super.deactivate();
        controller.removeControllerStatusListener(this);
    }

    private void discoverDevices() {
        String serial = null;
        String name = null;
        String type = null;
        ThingUID controllerUID = controller.getThing().getUID();
        ThingUID thingUID = new ThingUID(type, controllerUID, serial);
        Map properties = new HashMap(2);
        properties.put("serial", serial);
        properties.put("name", name);
        org.eclipse.smarthome.config.discovery.DiscoveryResult discoveryResult = DiscoveryResultBuilder.create(thingUID).withProperties(properties).withBridge(controllerUID).withLabel(name).build();
        thingDiscovered(discoveryResult);
    }

    private static final int DISCOVERY_TIMEOUT_SEC = 10;
    private final BroadlinkControllerHandler controller;
    Logger logger;
    public static final Set SUPPORTED_THING_TYPES_UIDS;

    static {
        SUPPORTED_THING_TYPES_UIDS = Collections.singleton(BroadlinkBindingConstants.THING_TYPE_S1C);
    }
}
