package org.openhab.binding.broadlink.internal.socket;

import org.eclipse.smarthome.core.thing.ThingTypeUID;

public interface BroadlinkSocketListener {

    public abstract void onDataReceived(String remoteAddress, int remotePort, String remoteMAC, ThingTypeUID thingTypeUID);
}
