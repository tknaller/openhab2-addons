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
public class BroadlinkSocketModel3Handler extends BroadlinkSocketModel2Handler {
    private final Logger logger = LoggerFactory.getLogger(BroadlinkSocketModel3Handler.class);

    public BroadlinkSocketModel3Handler(Thing thing) {
        super(thing);
    }

    protected boolean getStatusFromDevice() {
        try {
            byte payload[] = new byte[16];
            payload[0] = 1;
            byte message[] = buildMessage((byte) 106, payload);
            sendDatagram(message, "status for SP3 socket");
            byte response[] = receiveDatagram("status for SP3 socket");
            if (response == null) {
                    logError("null response from model 3 status request");
                    return false;
            }
            int error = response[34] | response[35] << 8;
            if (error != 0) {
                    logError("Error response from model 3 status request; code: " + error);
                    return false;
            }
            IvParameterSpec ivSpec = new IvParameterSpec(Hex.convertHexToBytes(thingConfig.getIV()));
            Map properties = editProperties();
            byte decodedPayload[] = Utils.decrypt(Hex.fromHexString((String) properties.get("key")), ivSpec, Utils.slice(response, 56, 88));
            if (decodedPayload == null) {
                logError("Null payload in response from model 2 status request");
                return false;
            }
	    logInfo("Model 3 status byte is: " + payload[4]);
            if (payload[4] == 1) {
                updateState("powerOn", OnOffType.ON);
            } else {
                updateState("powerOn", OnOffType.OFF);
            }
            return true;
        } catch (Exception ex) {
                logError("Exception while getting status from SP3 device", ex);
                return false;
        }
    }

}
