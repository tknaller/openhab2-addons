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
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.RefreshType;
import org.openhab.binding.broadlink.internal.BroadlinkProtocol;
import org.openhab.binding.broadlink.internal.Hex;
import org.openhab.binding.broadlink.internal.Utils;
import org.slf4j.LoggerFactory;

/**
 * Multiple power socket plus USB strip device  - 3 AC outlets and 2 USB outlets
 *
 * @author John Marshall/Cato Sognen - Initial contribution
 */
public class BroadlinkStripModel11K3S2UHandler extends BroadlinkBaseThingHandler {

    public BroadlinkStripModel11K3S2UHandler(Thing thing) {
        super(thing, LoggerFactory.getLogger(BroadlinkStripModel11K3S2UHandler.class));
    }

    public void handleCommand(ChannelUID channelUID, Command command) {
        if (command instanceof RefreshType) {
            updateItemStatus();
            return;
        }

        switch (channelUID.getId()) {
            case "s1powerOn":
                interpretCommandForSocket(1, command);
                break;
            case "s2powerOn":
                interpretCommandForSocket(2, command);
                break;
            case "s3powerOn":
                interpretCommandForSocket(3, command);
                break;
            case "usbPowerOn":
                interpretCommandForSocket(4, command);
                break;
            default:
                break;

        }
    }

    private void interpretCommandForSocket(int sid, Command command) {
        if (command == OnOffType.ON) {
            setStatusOnDevice((byte) sid, (byte) 1);
        } else if (command == OnOffType.OFF) {
            setStatusOnDevice((byte) sid, (byte) 0);
        }
    }

    private void setStatusOnDevice(byte sid, byte state) {
        int sid_mask = 1 << sid - 1;
        byte payload[] = new byte[16];
        payload[0] = 13;
        payload[2] = -91;
        payload[3] = -91;
        payload[4] = 90;
        payload[5] = 90;
        if (state == 1)
            payload[6] = (byte) (178 + (sid_mask << 1));
        else
            payload[6] = (byte) (byte) (178 + sid_mask);
        payload[7] = -64;
        payload[8] = 2;
        payload[10] = 3;
        payload[13] = (byte) sid_mask;
        if (state == 1)
            payload[14] = (byte) sid_mask;
        else
            payload[14] = 0;
        byte message[] = buildMessage((byte) 106, payload);
        sendAndReceiveDatagram(message, "Setting MP13K2U status");
    }

    protected boolean getStatusFromDevice() {
        byte payload[] = new byte[16];
        payload[0] = 10;
        payload[2] = -91;
        payload[3] = -91;
        payload[4] = 90;
        payload[5] = 90;
        payload[6] = -82;
        payload[7] = -64;
        payload[8] = 1;
        try {
            byte message[] = buildMessage((byte) 106, payload);
            byte response[] = sendAndReceiveDatagram(message, "status for MP13K2U strip");
            byte decodedPayload[] = BroadlinkProtocol.decodePacket(response, thingConfig, editProperties());
            final int status = decodedPayload[14];

            this.updateState("s1powerOn", (status & 0x01) == 0x01 ? OnOffType.ON : OnOffType.OFF);
            this.updateState("s2powerOn", (status & 0x02) == 0x02 ? OnOffType.ON : OnOffType.OFF);
            this.updateState("s3powerOn", (status & 0x04) == 0x04 ? OnOffType.ON : OnOffType.OFF);
            this.updateState("usbPowerOn", (status & 0x08) == 0x08 ? OnOffType.ON : OnOffType.OFF);
        } catch (Exception ex) {
            thingLogger.logError("Exception while getting status from device", ex);
            return false;
        }
        return true;
    }

    protected boolean onBroadlinkDeviceBecomingReachable() {
        return getStatusFromDevice();
    }
}
