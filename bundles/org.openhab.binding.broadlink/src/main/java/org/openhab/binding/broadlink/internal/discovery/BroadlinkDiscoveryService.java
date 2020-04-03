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
package org.openhab.binding.broadlink.internal.discovery;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.smarthome.config.discovery.AbstractDiscoveryService;
import org.eclipse.smarthome.config.discovery.DiscoveryResult;
import org.eclipse.smarthome.config.discovery.DiscoveryResultBuilder;
import org.eclipse.smarthome.config.discovery.DiscoveryService;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.openhab.binding.broadlink.BroadlinkBindingConstants;
import org.openhab.binding.broadlink.internal.socket.BroadlinkSocketListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

// https://www.eclipse.org/smarthome/documentation/development/bindings/discovery-services.html
import org.osgi.service.component.annotations.Component;

/**
 * Broadlink discovery implementation.
 *
 * @author John Marshall/Cato Sognen - Initial contribution
 */
@NonNullByDefault
@Component(service = DiscoveryService.class, immediate = true, configurationPid = "discovery.broadlink")
public class BroadlinkDiscoveryService extends AbstractDiscoveryService
        implements BroadlinkSocketListener, DiscoveryFinishedListener {

    private final Logger logger = LoggerFactory.getLogger(BroadlinkDiscoveryService.class);
    private int foundCount = 0;

    public BroadlinkDiscoveryService() {
        super(BroadlinkBindingConstants.SUPPORTED_THING_TYPES_UIDS_TO_NAME_MAP.keySet(), 10, true);
        logger.info("BroadlinkDiscoveryService - Constructed");
    }

    public void startScan() {
        foundCount = 0;
        logger.warn("BroadlinkDiscoveryService - Beginning Broadlink device scan...");
        DiscoveryProtocol.beginAsync(this, 10000L, this);
    }


    public void onDiscoveryFinished() {
        logger.info("Discovery complete. Found {} Broadlink devices", foundCount);
    }

    protected synchronized void stopScan() {
        super.stopScan();
        removeOlderResults(getTimestampOfLastScan());
    }

    public void onDataReceived(String remoteAddress, int remotePort, String remoteMAC, ThingTypeUID thingTypeUID, int model) {
        logger.info("Data received during Broadlink device discovery: from {}:{} [{}]", remoteAddress, remotePort, remoteMAC);
        foundCount++;
        discoveryResultSubmission(remoteAddress, remotePort, remoteMAC, thingTypeUID, model);
    }

    private void discoveryResultSubmission(String remoteAddress, int remotePort, String remoteMAC, ThingTypeUID thingTypeUID, int model) {
        if (logger.isDebugEnabled()) {
            logger.debug("Adding new Broadlink device on {} with mac '{}' to Smarthome inbox", remoteAddress, remoteMAC);
        }
        Map<String, Object> properties = new HashMap<String, Object>(6);
        properties.put("ipAddress", remoteAddress);
        properties.put("port", Integer.valueOf(remotePort));
        properties.put("mac", remoteMAC);
        properties.put("deviceType", model);
        ThingUID thingUID = new ThingUID(thingTypeUID, remoteMAC.replace(":", "-"));
        if (logger.isDebugEnabled()) {
            logger.debug("Device '{}' discovered at '{}'.", thingUID, remoteAddress);
        }

        if (BroadlinkBindingConstants.SUPPORTED_THING_TYPES_UIDS_TO_NAME_MAP.containsKey(thingTypeUID)) {
            notifyThingDiscovered(thingTypeUID, thingUID, remoteAddress, properties);
        } else {
            logger.error("Discovered a {} but do not know how to support it at this time :-(", thingTypeUID);
        }
    }

    private void notifyThingDiscovered(ThingTypeUID thingTypeUID, ThingUID thingUID, String remoteAddress, Map<String, Object> properties) {
        String deviceHumanName = BroadlinkBindingConstants.SUPPORTED_THING_TYPES_UIDS_TO_NAME_MAP.get(thingTypeUID);
        String label = deviceHumanName + " [" + remoteAddress + "]";
        DiscoveryResult result = DiscoveryResultBuilder
                .create(thingUID)
                .withThingType(thingTypeUID)
                .withProperties(properties)
                .withLabel(label)
                .build();

        thingDiscovered(result);
    }
}
