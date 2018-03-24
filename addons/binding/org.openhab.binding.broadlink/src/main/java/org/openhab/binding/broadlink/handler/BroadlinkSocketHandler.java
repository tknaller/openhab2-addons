// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   BroadlinkSocketHandler.java

package org.openhab.binding.broadlink.handler;

import org.eclipse.smarthome.core.thing.Thing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Referenced classes of package org.openhab.binding.broadlink.handler:
//            BroadlinkBaseThingHandler

public class BroadlinkSocketHandler extends BroadlinkBaseThingHandler
{

    public BroadlinkSocketHandler(Thing thing)
    {
        super(thing);
        logger = LoggerFactory.getLogger(BroadlinkSocketHandler.class);
    }

    private Logger logger;
}
