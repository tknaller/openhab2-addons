/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.broadlink.internal;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.smarthome.config.discovery.DiscoveryService;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandlerFactory;
import org.eclipse.smarthome.core.thing.binding.ThingHandler;
import org.eclipse.smarthome.core.thing.binding.ThingHandlerFactory;
import org.openhab.binding.broadlink.BroadlinkBindingConstants;
import org.openhab.binding.broadlink.handler.*;
import org.osgi.service.component.annotations.Component;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link BroadlinkHandlerFactory} is responsible for creating things and thing
 * handlers.
 *
 * @author John Marshall/Cato Sognen - Initial contribution
 */
@Component(service = ThingHandlerFactory.class, immediate = true, configurationPid = "binding.broadlink")
@NonNullByDefault
public class BroadlinkHandlerFactory extends BaseThingHandlerFactory {

    private final Logger logger = LoggerFactory.getLogger(BroadlinkHandlerFactory.class);
    private Map discoveryServiceRegs;
    private List channelTypes;
    private List channelGroupTypes;

    public BroadlinkHandlerFactory() {
        discoveryServiceRegs = new HashMap();
        channelTypes = new CopyOnWriteArrayList();
        channelGroupTypes = new CopyOnWriteArrayList();
    }

    public boolean supportsThingType(ThingTypeUID thingTypeUID) {
        return BroadlinkBindingConstants.SUPPORTED_THING_TYPES_UIDS_TO_NAME_MAP.keySet().contains(thingTypeUID);
    }

    protected ThingHandler createHandler(Thing thing) {
        ThingTypeUID thingTypeUID = thing.getThingTypeUID();
        if (logger.isDebugEnabled()) logger.debug("Creating Thing handler for '{}'", thingTypeUID.getAsString());
        if (thingTypeUID.equals(BroadlinkBindingConstants.THING_TYPE_RM)) return new BroadlinkRemoteModel2Handler(thing);
        if (thingTypeUID.equals(BroadlinkBindingConstants.THING_TYPE_RM2)) {
            if (logger.isDebugEnabled()) logger.debug("RM 2 handler requested created");
            return new BroadlinkRemoteModel2Handler(thing);
        }
        if (thingTypeUID.equals(BroadlinkBindingConstants.THING_TYPE_RM3)) {
            if (logger.isDebugEnabled()) logger.debug("RM 3 handler requested created");
            return new BroadlinkRemoteHandler(thing);
        }
        if (thingTypeUID.equals(BroadlinkBindingConstants.THING_TYPE_A1)) {
            if (logger.isDebugEnabled()) logger.debug("A1 handler requested created");
            return new BroadlinkA1Handler(thing);
        }
        if (thingTypeUID.equals(BroadlinkBindingConstants.THING_TYPE_MP1)) return new BroadlinkStripModel1Handler(thing);
        if (thingTypeUID.equals(BroadlinkBindingConstants.THING_TYPE_SP1)) return new BroadlinkSocketModel1Handler(thing);
        if (thingTypeUID.equals(BroadlinkBindingConstants.THING_TYPE_SP2)) return new BroadlinkSocketModel2Handler(thing);
        if (thingTypeUID.equals(BroadlinkBindingConstants.THING_TYPE_SP3)) return new BroadlinkSocketModel3Handler(thing);
        if (thingTypeUID.equals(BroadlinkBindingConstants.THING_TYPE_MP1)) return new BroadlinkStripModel1Handler(thing);
        if (thingTypeUID.equals(BroadlinkBindingConstants.THING_TYPE_MP2)) {
            return new BroadlinkStripModel1Handler(thing);
        } else {
//            thingTypeUID.equals(BroadlinkBindingConstants.THING_TYPE_S1C);
//            thingTypeUID.equals(BroadlinkBindingConstants.THING_TYPE_PIR);
//            thingTypeUID.equals(BroadlinkBindingConstants.THING_TYPE_MAGNET);
            return null;
        }
    }

    protected synchronized void removeHandler(ThingHandler thingHandler) {
        if(thingHandler instanceof BroadlinkControllerHandler) {
            ServiceRegistration serviceReg = (ServiceRegistration)discoveryServiceRegs.get(thingHandler.getThing().getUID());
            if(serviceReg != null) {
                serviceReg.unregister();
                discoveryServiceRegs.remove(thingHandler.getThing().getUID());
            }
        }
    }

}
