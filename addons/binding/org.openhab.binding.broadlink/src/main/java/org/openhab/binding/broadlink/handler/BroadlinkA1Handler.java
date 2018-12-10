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

import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.thing.*;
import org.openhab.binding.broadlink.internal.*;
import org.slf4j.LoggerFactory;

/**
 * Handles the A1 environmental sensor.
 *
 * @author John Marshall/Cato Sognen - Initial contribution
 */
public class BroadlinkA1Handler extends BroadlinkBaseThingHandler {

    public BroadlinkA1Handler(Thing thing) {
        super(thing, LoggerFactory.getLogger(BroadlinkA1Handler.class));
    }

    protected boolean getStatusFromDevice() {
        thingLogger.logTrace("A1 getStatusFromDevice");
        byte payload[];
        payload = new byte[16];
        payload[0] = 1;
        byte message[] = buildMessage((byte) 0x6a, payload);
        if (!sendDatagram(message, "A1 device status")) {
            thingLogger.logError("Sending packet to device failed.");
            return false;
        }
        byte response[];
        response = receiveDatagram("A1 device status");
        if (response == null) {
            thingLogger.logDebug("Incoming packet from device is null.");
            return false;
        }

        int error = response[34] | response[35] << 8;
        if (error != 0) {
            thingLogger.logError("Response from device is not valid. (Error code {})", error);
            return false;
        }

        try {
            IvParameterSpec ivSpec = new IvParameterSpec(Hex.convertHexToBytes(thingConfig.getIV()));
            Map properties = editProperties();
            byte decryptResponse[] = Utils.decrypt(Hex.fromHexString((String) properties.get("key")), ivSpec, Utils.slice(response, 56, 88));
            float temperature = (float) ((double) (decryptResponse[4] * 10 + decryptResponse[5]) / 10D);
            thingLogger.logTrace("A1 getStatusFromDevice got temperature " + temperature);

            updateState("temperature", new DecimalType(temperature));
            updateState("humidity", new DecimalType((double) (decryptResponse[6] * 10 + decryptResponse[7]) / 10D));
            updateState("light", ModelMapper.getLightValue(decryptResponse[8]));
            updateState("air", ModelMapper.getAirValue(decryptResponse[10]));
            updateState("noise", ModelMapper.getNoiseValue(decryptResponse[12]));
            return true;
        } catch (Exception ex) {
            thingLogger.logError("Failed while getting device status", ex);
            return false;
        }
    }

        protected boolean onBroadlinkDeviceBecomingReachable() {
            return getStatusFromDevice();
        }
}
