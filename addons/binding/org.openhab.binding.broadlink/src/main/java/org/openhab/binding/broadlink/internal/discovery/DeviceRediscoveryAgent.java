package org.openhab.binding.broadlink.internal.discovery;

import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.openhab.binding.broadlink.BroadlinkBindingConstants;
import org.openhab.binding.broadlink.internal.BroadlinkProtocol;
import org.openhab.binding.broadlink.internal.socket.BroadlinkSocket;
import org.openhab.binding.broadlink.internal.socket.BroadlinkSocketListener;
import org.openhab.binding.broadlink.internal.NetworkUtils;
import org.openhab.binding.broadlink.config.BroadlinkDeviceConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * This agent exploits the well-known Broadlink device discovery process
 * to attempt to "rediscover" a previously-discovered dynamically-addressed
 * Broadlink device that may have recently changed IP address. 
 *
 * This agent has NOTHING TO DO WITH the initial device discovery process.
 * It is explicitly initiated when a dynamically-addressed Broadlink device
 * appears to have dropped off the network.
 *
 * @author John Marshall
 */
public class DeviceRediscoveryAgent implements BroadlinkSocketListener, DiscoveryFinishedListener {

    private final Logger logger = LoggerFactory.getLogger(DeviceRediscoveryAgent.class);
	private final BroadlinkDeviceConfiguration missingThingConfig;
	private final DeviceRediscoveryListener drl;
	private boolean foundDevice = false;

    public DeviceRediscoveryAgent(BroadlinkDeviceConfiguration missingThingConfig, DeviceRediscoveryListener drl) {
		this.missingThingConfig = missingThingConfig;
		this.drl = drl;
    }

    public void attemptRediscovery() {
        logger.warn("DeviceRediscoveryAgent - Beginning Broadlink device scan for missing " + missingThingConfig.toString());
		DiscoveryProtocol.beginAsync(this, 5000L, this);
    }

    public void onDataReceived(String remoteAddress, int remotePort, String remoteMAC, ThingTypeUID thingTypeUID) {
        logger.trace("Data received during Broadlink device rediscovery: from " + remoteAddress + ":" + remotePort + "[" + remoteMAC + "]");
	
		// if this thing matches the missingThingConfig, we've found it!
		logger.trace("Comparing with desired mac: " + missingThingConfig.getMACAsString());

		if (missingThingConfig.getMACAsString().equals(remoteMAC)) {
			logger.info("We have a match for target MAC " + remoteMAC + " at " + remoteAddress + " - reassociate!");
			foundDevice = true;
			this.drl.onDeviceRediscovered(remoteAddress);
		}
    }

	public void onDiscoveryFinished() {
		if (!foundDevice) {
			this.drl.onDeviceRediscoveryFailure();
		}
	}
}
