/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.broadlink.handler;

import java.util.Map;
import javax.crypto.spec.IvParameterSpec;

import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.thing.*;
import org.openhab.binding.broadlink.internal.Hex;
import org.openhab.binding.broadlink.internal.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Smart power socket handler
 *
 * @author John Marshall/Cato Sognen - Initial contribution
 */
public class BroadlinkSocketModel2Handler extends BroadlinkSocketHandler {

    public BroadlinkSocketModel2Handler(Thing thing) {
        super(thing, LoggerFactory.getLogger(BroadlinkSocketModel2Handler.class));
    }

    public BroadlinkSocketModel2Handler(Thing thing, Logger logger) {
        super(thing, logger);
    }

    protected void setStatusOnDevice(int status) {
        byte payload[] = new byte[16];
        payload[0] = 2;
        payload[4] = (byte) status;
        byte message[] = buildMessage((byte) 106, payload);
        sendDatagram(message, "Setting SP2 status");
	    receiveDatagram("SP2 acknowledgment packet");
    }

    protected boolean getStatusFromDevice() {
        try {
            byte payload[] = new byte[16];
            payload[0] = 1;
            byte message[] = buildMessage((byte) 106, payload);
            sendDatagram(message, "status for socket");
            byte response[] = receiveDatagram("status for socket");
            if (response == null) {
                thingLogger.logError("null response from model 2 status request");
                return false;
            }
            int error = response[34] | response[35] << 8;
            if (error != 0) {
                thingLogger.logError("Error response from model 2 status request; code: " + error);
                return false;
            }
            IvParameterSpec ivSpec = new IvParameterSpec(Hex.convertHexToBytes(thingConfig.getIV()));
            Map properties = editProperties();
            byte decodedPayload[] = Utils.decrypt(Hex.fromHexString((String) properties.get("key")), ivSpec, Utils.slice(response, 56, 88));
            if (decodedPayload == null) {
                thingLogger.logError("Null payload in response from model 2 status request");
                return false;
            }
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
