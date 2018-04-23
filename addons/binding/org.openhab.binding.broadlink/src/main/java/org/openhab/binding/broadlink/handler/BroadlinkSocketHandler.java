package org.openhab.binding.broadlink.handler;

import org.eclipse.smarthome.core.thing.Thing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BroadlinkSocketHandler extends BroadlinkBaseThingHandler {
    
		private final Logger logger = LoggerFactory.getLogger(BroadlinkSocketHandler.class);

    public BroadlinkSocketHandler(Thing thing) {
        super(thing);
    }
}
