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
import org.openhab.binding.broadlink.internal.Hex;
import org.openhab.binding.broadlink.internal.Utils;

/**
 * Remote blaster handler
 *
 * @author John Marshall/Cato Sognen - Initial contribution
 */
public class BroadlinkRemoteModel2Handler extends BroadlinkRemoteHandler {

    public BroadlinkRemoteModel2Handler(Thing thing) {
        super(thing);
    }

    protected boolean onBroadlinkDeviceBecomingReachable() {
        return getStatusFromDevice();
    }

    public boolean getStatusFromDevice() {
        byte payload[] = new byte[16];
        payload[0] = 1;
        byte message[] = buildMessage((byte)106, payload);
        sendDatagram(message, "RM2 device status");
        byte response[] = receiveDatagram("RM2 device status");
        if (response != null) {
            int error = response[34] | response[35] << 8;
            if (error == 0) {
                IvParameterSpec ivSpec = new IvParameterSpec(Hex.convertHexToBytes(thingConfig.getIV()));
                Map properties = editProperties();
                byte decodedPayload[] = Utils.decrypt(Hex.fromHexString((String)properties.get("key")), ivSpec, Utils.slice(response, 56, 88));
                if (decodedPayload != null) {
                    float temperature = (float)((double)(decodedPayload[4] * 10 + decodedPayload[5]) / 10D);
                    updateState("temperature", new DecimalType(temperature));
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
}
