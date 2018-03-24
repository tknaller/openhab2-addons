// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   BroadlinkSocketModel1Handler.java

package org.openhab.binding.broadlink.handler;

import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.types.Command;

// Referenced classes of package org.openhab.binding.broadlink.handler:
//            BroadlinkSocketHandler

public class BroadlinkSocketModel1Handler extends BroadlinkSocketHandler
{

    public BroadlinkSocketModel1Handler(Thing thing)
    {
        super(thing);
    }

    public void handleCommand(ChannelUID channelUID, Command command)
    {
        if(channelUID.getId().equals("powerOn"))
            if(command == OnOffType.ON)
                setStatusOnDevice(1);
            else
            if(command == OnOffType.OFF)
                setStatusOnDevice(0);
    }

    public void setStatusOnDevice(int state)
    {
        byte payload[] = new byte[16];
        payload[0] = (byte)state;
        byte message[] = buildMessage((byte)102, payload);
        sendDatagram(message);
    }
}
