/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.broadlink.handler;

import org.eclipse.smarthome.core.thing.Thing;

/**
 * Smart power socket handler 
 *
 * @author John Marshall/Cato Sognen - Initial contribution
 */
public class BroadlinkSocketModel1Handler extends BroadlinkSocketHandler {

    public BroadlinkSocketModel1Handler(Thing thing) {
        super(thing);
    }

    public void setStatusOnDevice(int state) {
        byte payload[] = new byte[16];
        payload[0] = (byte) state;
        byte message[] = buildMessage((byte) 102, payload);
        sendAndReceiveDatagram(message, "Setting SP1 status");
    }
}
