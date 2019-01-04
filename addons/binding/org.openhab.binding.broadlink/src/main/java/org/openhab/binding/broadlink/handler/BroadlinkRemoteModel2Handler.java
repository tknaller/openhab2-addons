/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.broadlink.handler;

import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.thing.*;
import org.openhab.binding.broadlink.internal.BroadlinkProtocol;
import org.openhab.binding.broadlink.internal.Hex;
import org.openhab.binding.broadlink.internal.Utils;
import org.slf4j.LoggerFactory;

/**
 * Remote blaster handler
 *
 * @author John Marshall/Cato Sognen - Initial contribution
 */
public class BroadlinkRemoteModel2Handler extends BroadlinkRemoteHandler {

    public BroadlinkRemoteModel2Handler(Thing thing) {
        super(thing, LoggerFactory.getLogger(BroadlinkRemoteModel2Handler.class));
    }

    protected boolean onBroadlinkDeviceBecomingReachable() {
        return getStatusFromDevice();
    }

    protected boolean getStatusFromDevice() {
        try {
            byte payload[] = new byte[16];
            payload[0] = 1;
            byte message[] = buildMessage((byte)106, payload);
            byte response[] = sendAndReceiveDatagram(message, "RM2 device status");
            byte decodedPayload[] = BroadlinkProtocol.decodePacket(response, thingConfig, editProperties());
            float temperature = (float)((double)(decodedPayload[4] * 10 + decodedPayload[5]) / 10D);
            updateState("temperature", new DecimalType(temperature));
            return true;
        } catch (Exception e) {
            thingLogger.logError("Could not get status: {}", e);
            return false;
        }
    }
}
