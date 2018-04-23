package org.openhab.binding.broadlink.handler;

import java.util.Map;
import javax.crypto.spec.IvParameterSpec;

import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.thing.*;
import org.openhab.binding.broadlink.internal.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BroadlinkA1Handler extends BroadlinkBaseThingHandler {

    private final Logger logger = LoggerFactory.getLogger(BroadlinkA1Handler.class);

    public BroadlinkA1Handler(Thing thing) {
        super(thing);
    }

    private boolean getStatusFromDevice() {
        byte payload[];
        payload = new byte[16];
        payload[0] = 1;
        byte message[] = buildMessage((byte) 0x6a, payload);
        if (!sendDatagram(message, "A1 device status")) {
            logError("Sending packet to device failed.");
            return false;
        }
        byte response[];
        response = receiveDatagram("A1 device status");
        if (response == null) {
            logDebug("Incoming packet from device is null.");
            return false;
        }

        int error = response[34] | response[35] << 8;
        if (error != 0) {
            logError("Response from device is not valid. (Error code {})", error);
            return false;
        }

        try {
            IvParameterSpec ivSpec = new IvParameterSpec(Hex.convertHexToBytes(thingConfig.getIV()));
            Map properties = editProperties();
            byte decryptResponse[] = Utils.decrypt(Hex.fromHexString((String) properties.get("key")), ivSpec, Utils.slice(response, 56, 88));
            float temperature = (float) ((double) (decryptResponse[4] * 10 + decryptResponse[5]) / 10D);
            updateState("temperature", new DecimalType(temperature));
            updateState("humidity", new DecimalType((double) (decryptResponse[6] * 10 + decryptResponse[7]) / 10D));
            updateState("light", ModelMapper.getLightValue(decryptResponse[8]));
            updateState("air", ModelMapper.getAirValue(decryptResponse[10]));
            updateState("noise", ModelMapper.getNoiseValue(decryptResponse[12]));
        } catch (Exception ex) {
            logError("Failed while getting device status", ex);
            return false;
        }
        return true;
    }

		protected boolean onBroadlinkDeviceBecomingReachable() {
        return getStatusFromDevice();
		}
}
