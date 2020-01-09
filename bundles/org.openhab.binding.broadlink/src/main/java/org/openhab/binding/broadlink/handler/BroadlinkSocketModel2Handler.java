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
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.thing.*;
import org.openhab.binding.broadlink.internal.BroadlinkProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Smart power socket handler
 *
 * @author John Marshall/Cato Sognen - Initial contribution
 */
@NonNullByDefault
public class BroadlinkSocketModel2Handler extends BroadlinkSocketHandler {

    public BroadlinkSocketModel2Handler(Thing thing) {
        super(thing, LoggerFactory.getLogger(BroadlinkSocketModel2Handler.class));
    }

    public BroadlinkSocketModel2Handler(Thing thing, Logger logger) {
        super(thing, logger);
    }

    protected void setStatusOnDevice(int status) throws IOException {
        byte payload[] = new byte[16];
        payload[0] = 2;
        payload[4] = (byte) status;
        byte message[] = buildMessage((byte) 106, payload);
        sendAndReceiveDatagram(message, "Setting SP2/3 status");
    }

    protected boolean getStatusFromDevice() {
        try {
            byte payload[] = new byte[16];
            payload[0] = 1;
            byte message[] = buildMessage((byte) 106, payload);
            byte response[] = sendAndReceiveDatagram(message, "status for socket");
            byte decodedPayload[] = BroadlinkProtocol.decodePacket(response, thingConfig, editProperties());
            updateState("powerOn", deriveOnOffStateFromPayload(decodedPayload));
            return true;
        } catch (Exception ex) {
            thingLogger.logError("Exception while getting status from device", ex);
            return false;
        }
    }

    private OnOffType deriveOnOffStateFromPayload(byte[] payload) {
        // Credit to the Python Broadlink implementation for this:
        // https://github.com/mjg59/python-broadlink/blob/master/broadlink/__init__.py
        // Function check_power does more than just check if payload[4] == 1 !
        byte powerByte = payload[4];
        if (powerByte == 1 || powerByte == 3 || powerByte == 0xFD) {
            return OnOffType.ON;
        }
        return OnOffType.OFF;
    }

    protected boolean onBroadlinkDeviceBecomingReachable() {
        return getStatusFromDevice();
    }
}
