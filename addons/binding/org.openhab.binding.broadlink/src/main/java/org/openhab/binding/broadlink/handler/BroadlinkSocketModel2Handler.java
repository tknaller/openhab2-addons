// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   BroadlinkSocketModel2Handler.java

package org.openhab.binding.broadlink.handler;

import java.util.Map;
import javax.crypto.spec.IvParameterSpec;

import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.thing.*;
import org.eclipse.smarthome.core.types.Command;
import org.openhab.binding.broadlink.internal.Hex;
import org.openhab.binding.broadlink.internal.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BroadlinkSocketModel2Handler extends BroadlinkSocketHandler {

    private final Logger logger = LoggerFactory.getLogger(BroadlinkSocketModel2Handler.class);

    public BroadlinkSocketModel2Handler(Thing thing) {
        super(thing);
    }

    public void handleCommand(ChannelUID channelUID, Command command) {
        if (channelUID.getId().equals("powerOn"))
            if (command == OnOffType.ON)
                setStatusOnDevice(1);
            else if (command == OnOffType.OFF)
                setStatusOnDevice(0);
    }

    private void setStatusOnDevice(int status) {
        byte payload[] = new byte[16];
        payload[0] = 2;
        payload[4] = (byte) status;
        byte message[] = buildMessage((byte) 106, payload);
        sendDatagram(message);
    }

    public boolean getStatusFromDevice() {
        label0:
        {
            label1:
            {
                label2:
                {
                    byte payload[] = new byte[16];
                    payload[0] = 1;
                    try {
                        byte message[] = buildMessage((byte) 106, payload);
                        sendDatagram(message, "status for socket");
                        byte response[] = receiveDatagram("status for socket");
                        if (response == null)
                            break label0;
                        int error = response[34] | response[35] << 8;
                        if (error != 0)
                            break label1;
                        IvParameterSpec ivSpec = new IvParameterSpec(Hex.convertHexToBytes(thingConfig.getIV()));
                        Map properties = editProperties();
                        byte decodedPayload[] = Utils.decrypt(Hex.fromHexString((String) properties.get("key")), ivSpec, Utils.slice(response, 56, 88));
                        if (decodedPayload == null)
                            break label2;
                        if (payload[4] == 1)
                            updateState("powerOn", OnOffType.ON);
                        else
                            updateState("powerOn", OnOffType.OFF);
                    } catch (Exception ex) {
                        logger.error("Exception while getting status from device", ex);
                        return false;
                    }
                    return true;
                }
                return false;
            }
            return false;
        }
        return false;
    }

    public void updateItemStatus() {
        if (getStatusFromDevice()) {
            if (!isOnline())
                updateStatus(ThingStatus.ONLINE);
        } else if (!isOffline())
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR, (new StringBuilder("Could not control device at IP address ")).append(thingConfig.getIpAddress()).toString());
    }

    boolean powerON;
}
