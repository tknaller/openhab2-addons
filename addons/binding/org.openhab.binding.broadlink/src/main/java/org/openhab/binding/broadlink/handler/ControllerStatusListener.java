// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ControllerStatusListener.java

package org.openhab.binding.broadlink.handler;

import org.eclipse.smarthome.core.thing.ThingStatus;

public interface ControllerStatusListener {

    public abstract void controllerStatusChanged(ThingStatus thingstatus);
}
