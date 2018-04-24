/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.broadlink.handler;

import org.eclipse.smarthome.core.thing.ThingStatus;

/**
 * Interface for something that is interested in being informed when a Thing's status changes 
 *
 * @author John Marshall/Cato Sognen - Initial contribution
 */
public interface ControllerStatusListener {

    public abstract void controllerStatusChanged(ThingStatus thingstatus);
}
