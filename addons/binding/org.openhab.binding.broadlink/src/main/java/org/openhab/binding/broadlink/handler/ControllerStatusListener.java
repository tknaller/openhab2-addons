package org.openhab.binding.broadlink.handler;

import org.eclipse.smarthome.core.thing.ThingStatus;

public interface ControllerStatusListener {

    public abstract void controllerStatusChanged(ThingStatus thingstatus);
}
