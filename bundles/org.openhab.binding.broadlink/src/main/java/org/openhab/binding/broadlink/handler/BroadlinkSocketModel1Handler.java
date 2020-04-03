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
import org.eclipse.smarthome.core.thing.Thing;

import java.io.IOException;

/**
 * Smart power socket handler 
 *
 * @author John Marshall/Cato Sognen - Initial contribution
 */
@NonNullByDefault
public class BroadlinkSocketModel1Handler extends BroadlinkSocketHandler {

    public BroadlinkSocketModel1Handler(Thing thing) {
        super(thing);
    }

    public void setStatusOnDevice(int state) throws IOException {
        byte payload[] = new byte[16];
        payload[0] = (byte) state;
        byte message[] = buildMessage((byte) 102, payload);
        sendAndReceiveDatagram(message, "Setting SP1 status");
    }
}
