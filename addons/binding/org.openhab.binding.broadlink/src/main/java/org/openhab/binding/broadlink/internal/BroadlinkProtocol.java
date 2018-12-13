/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.broadlink.internal;

import java.net.*;
import java.util.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.openhab.binding.broadlink.config.BroadlinkDeviceConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Static methods for working with the Broadlink network prototcol.
 *
 * @author John Marshall/Cato Sognen - Initial contribution
 */
public class BroadlinkProtocol {

    private static final Logger logger = LoggerFactory.getLogger(Utils.class);

    public static byte[] buildMessage(byte command, 
		    		byte[] payload,
				int count,
				byte[] mac,
				byte[] id,
				byte[] iv,
				byte[] key) {
        byte packet[] = new byte[56];
        packet[0] = 0x5a;
        packet[1] = (byte) 0xa5;
        packet[2] = (byte) 0xaa;
        packet[3] = 0x55;
        packet[4] = 0x5a;
        packet[5] = (byte) 0xa5;
        packet[6] = (byte) 0xaa;
        packet[7] = 0x55;
        packet[36] = 42;
        packet[37] = 39;
        packet[38] = command;
        packet[40] = (byte) (count & 0xff);
        packet[41] = (byte) (count >> 8);
        packet[42] = mac[0];
        packet[43] = mac[1];
        packet[44] = mac[2];
        packet[45] = mac[3];
        packet[46] = mac[4];
        packet[47] = mac[5];
        packet[48] = id[0];
        packet[49] = id[1];
        packet[50] = id[2];
        packet[51] = id[3];
        int checksum = 0xBEAF;
        int i = 0;
        byte abyte0[];
        int k = (abyte0 = payload).length;
        for (int j = 0; j < k; j++) {
            byte b = abyte0[j];
            i = Byte.toUnsignedInt(b);
            checksum += i;
            checksum &= 0xffff;
        }

        packet[52] = (byte) (checksum & 0xff);
        packet[53] = (byte) (checksum >> 8);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            outputStream.write(packet);
            outputStream.write(Utils.encrypt(key, new IvParameterSpec(iv), payload));
        } catch (IOException e) {
            logger.error("IOException while building message", e);
            return null;
        }
        byte data[] = outputStream.toByteArray();
        checksum = 0xBEAF;
        byte abyte1[];
        int i1 = (abyte1 = data).length;
        for (int l = 0; l < i1; l++) {
            byte b = abyte1[l];
            i = Byte.toUnsignedInt(b);
            checksum += i;
            checksum &= 0xffff;
        }

        data[32] = (byte) (checksum & 0xff);
        data[33] = (byte) (checksum >> 8);
        return data;
    }

    public static byte[] buildAuthenticationPayload() {
    	// https://github.com/mjg59/python-broadlink/blob/master/protocol.md
        byte payload[] = new byte[80];
        payload[4] = 49;
        payload[5] = 49;
        payload[6] = 49;
        payload[7] = 49;
        payload[8] = 49;
        payload[9] = 49;
        payload[0x0a] = 49;
        payload[0x0b] = 49;
        payload[0x0c] = 49;
        payload[0x0d] = 49;
        payload[0x0e] = 49;
        payload[0x0f] = 49;
        payload[0x10] = 49;
        payload[0x11] = 49;
        payload[0x12] = 49;

        payload[0x13] = 0x01;


        payload[30] = 1;
        payload[45] = 1;
        payload[0x30] = 84;
        payload[0x31] = 101;
        payload[0x32] = 115;
        payload[0x33] = 116;
        payload[0x34] = 32;
        payload[0x35] = 32;
        payload[0x36] = 49;

	return payload;
	}

    public static byte[] buildDiscoveryPacket(String host, int port) {
        String localAddress[] = null;
        localAddress = host.toString().split("\\.");
        int ipAddress[] = new int[4];
        for (int i = 0; i < 4; i++)
            ipAddress[i] = Integer.parseInt(localAddress[i]);

        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(2);
        TimeZone timeZone = TimeZone.getDefault();
        int timezone = timeZone.getRawOffset() / 0x36ee80;
        byte packet[] = new byte[48];
        if (timezone < 0) {
            packet[8] = (byte) ((255 + timezone) - 1);
            packet[9] = -1;
            packet[10] = -1;
            packet[11] = -1;
        } else {
            packet[8] = 8;
            packet[9] = 0;
            packet[10] = 0;
            packet[11] = 0;
        }
        packet[12] = (byte) (calendar.get(1) & 0xff);
        packet[13] = (byte) (calendar.get(1) >> 8);
        packet[14] = (byte) calendar.get(12);
        packet[15] = (byte) calendar.get(11);
        packet[16] = (byte) (calendar.get(1) - 2000);
        packet[17] = (byte) (calendar.get(7) + 1);
        packet[18] = (byte) calendar.get(5);
        packet[19] = (byte) (calendar.get(2) + 1);
        packet[24] = (byte) ipAddress[0];
        packet[25] = (byte) ipAddress[1];
        packet[26] = (byte) ipAddress[2];
        packet[27] = (byte) ipAddress[3];
        packet[28] = (byte) (port & 0xff);
        packet[29] = (byte) (port >> 8);
        packet[38] = 6;
        int checksum = 0xBEAF;
        byte abyte0[];
        int k = (abyte0 = packet).length;
        for (int j = 0; j < k; j++) {
            byte b = abyte0[j];
            checksum += Byte.toUnsignedInt(b);
        }

        checksum &= 0xffff;
        packet[32] = (byte) (checksum & 0xff);
        packet[33] = (byte) (checksum >> 8);
        return packet;
    }

    public static byte[] decodePacket(byte[] packet, BroadlinkDeviceConfiguration thingConfig, Map<String, String> properties) throws IOException {
        // if a properties map is supplied, use it.
        // During initial thing startup we don't have one yet, so use the auth key from the config.
        final String key = (properties == null) ? thingConfig.getAuthorizationKey() : properties.get("key");

        if (packet == null) {
            throw new ProtocolException("Incoming packet from device is null.");
        }

        int error = packet[34] | packet[35] << 8;
        if (error != 0) {
            throw new ProtocolException("Response from device is not valid. (Error code " + error + " )");
        }

        try {
            IvParameterSpec ivSpec = new IvParameterSpec(Hex.convertHexToBytes(thingConfig.getIV()));
            return Utils.decrypt(
                Hex.fromHexString(key),
                ivSpec,
                Utils.slice(packet, 56, 88)
            );
        } catch (Exception ex) {
            throw new IOException("Failed while getting device status", ex);
        }

    }
}
