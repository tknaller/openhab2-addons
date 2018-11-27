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
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utilities for working with the Broadlink devices.
 *
 * @author John Marshall/Cato Sognen - Initial contribution
 */
public class Utils {

    private static final Logger logger = LoggerFactory.getLogger(Utils.class);
    public static byte[] getDeviceId(byte response[]) {
        return slice(response, 0, 4);
    }

    public static byte[] getDeviceKey(byte response[]) {
        return slice(response, 4, 20);
    }

    public static byte[] slice(byte source[], int from, int to) {
        if (from > to) {
            return null;
        }
        if (to - from > source.length) {
            return null;
        }
        if (to == from) {
            byte sliced[] = new byte[1];
            sliced[0] = source[from];
            return sliced;
        } else {
            byte sliced[] = new byte[to - from];
            System.arraycopy(source, from, sliced, 0, to - from);
            return sliced;
        }
    }

    public static byte[] encrypt(byte key[], IvParameterSpec ivSpec, byte data[]) {
        SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            cipher.init(1, secretKey, ivSpec);
            return cipher.doFinal(data);
        } catch (Exception e) {
            logger.error("Exception while encrypting", e);
        }
        return null;
    }

    public static byte[] decrypt(byte key[], IvParameterSpec ivSpec, byte data[]) {
        SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            cipher.init(2, secretKey, ivSpec);
            return cipher.doFinal(data);
        } catch (Exception e) {
            logger.error("Exception while decrypting", e);
        }
        return null;
    }
}
