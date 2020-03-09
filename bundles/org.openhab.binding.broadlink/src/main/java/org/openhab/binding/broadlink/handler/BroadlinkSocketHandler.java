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

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.types.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract superclass for power socket devices 
 *
 * @author John Marshall/Cato Sognen - Initial contribution
 */
@NonNullByDefault
public abstract class BroadlinkSocketHandler extends BroadlinkBaseThingHandler {
    
    public BroadlinkSocketHandler(Thing thing) {
        super(thing, LoggerFactory.getLogger(BroadlinkSocketHandler.class));
    }

    public BroadlinkSocketHandler(Thing thing, Logger logger) {
        super(thing, logger);
    }

    protected abstract void setStatusOnDevice(int state);

    public void handleCommand(ChannelUID channelUID, Command command) {
        if (channelUID.getId().equals("powerOn")) {
            if (command == OnOffType.ON) {
                setStatusOnDevice(1);
            } else if (command == OnOffType.OFF) {
                setStatusOnDevice(0);
            }
        }
    }
}
