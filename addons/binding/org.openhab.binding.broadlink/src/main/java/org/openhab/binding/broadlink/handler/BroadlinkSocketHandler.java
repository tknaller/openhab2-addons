package org.openhab.binding.broadlink.handler;

import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.thing.Thing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BroadlinkSocketHandler extends BroadlinkBaseThingHandler {
    
		private final Logger logger = LoggerFactory.getLogger(BroadlinkSocketHandler.class);

    public BroadlinkSocketHandler(Thing thing) {
        super(thing);
    }

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
