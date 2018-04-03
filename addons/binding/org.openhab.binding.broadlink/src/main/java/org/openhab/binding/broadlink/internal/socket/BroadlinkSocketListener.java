package org.openhab.binding.broadlink.internal.socket;

import org.eclipse.smarthome.core.thing.ThingTypeUID;

public interface BroadlinkSocketListener {

    public abstract void onDataReceived(String s, int i, String s1, ThingTypeUID thingtypeuid);
}
