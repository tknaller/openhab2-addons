// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   BroadlinkRemoteModel2Handler.java

package org.openhab.binding.broadlink.handler;

import java.util.Map;
import javax.crypto.spec.IvParameterSpec;
import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.thing.*;
import org.openhab.binding.broadlink.config.BroadlinkDeviceConfiguration;
import org.openhab.binding.broadlink.internal.Hex;
import org.openhab.binding.broadlink.internal.Utils;

// Referenced classes of package org.openhab.binding.broadlink.handler:
//            BroadlinkRemoteHandler

public class BroadlinkRemoteModel2Handler extends BroadlinkRemoteHandler
{

    public BroadlinkRemoteModel2Handler(Thing thing)
    {
        super(thing);
    }

    public void updateItemStatus()
    {
        if(getStatusFromDevice())
        {
            if(!isOnline())
                updateStatus(ThingStatus.ONLINE);
        } else
        if(!isOffline())
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR, (new StringBuilder("Could not control device at IP address ")).append(thingConfig.getIpAddress()).toString());
    }

    public boolean getStatusFromDevice()
    {
        byte payload[] = new byte[16];
        payload[0] = 1;
        byte message[] = buildMessage((byte)106, payload);
        sendDatagram(message);
        byte response[] = receiveDatagram();
        if(response != null)
        {
            int error = response[34] | response[35] << 8;
            if(error == 0)
            {
                IvParameterSpec ivSpec = new IvParameterSpec(Hex.convertHexToBytes(thingConfig.getIV()));
                Map properties = editProperties();
                byte decodedPayload[] = Utils.decrypt(Hex.fromHexString((String)properties.get("key")), ivSpec, Utils.slice(response, 56, 88));
                if(decodedPayload != null)
                {
                    float temperature = (float)((double)(decodedPayload[4] * 10 + decodedPayload[5]) / 10D);
                    updateState("temperature", new DecimalType(temperature));
                    return true;
                } else
                {
                    return false;
                }
            } else
            {
                return false;
            }
        } else
        {
            return false;
        }
    }
}
