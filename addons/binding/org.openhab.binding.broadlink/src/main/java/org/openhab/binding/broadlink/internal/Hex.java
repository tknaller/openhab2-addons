// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Hex.java

package org.openhab.binding.broadlink.internal;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.bind.DatatypeConverter;

public class Hex {

    private static final Pattern validPattern = Pattern.compile("0000( +[0-9A-Fa-f][0-9A-Fa-f][0-9A-Fa-f][0-9A-Fa-f])+");
		private static final String HEXES = "0123456789ABCDEF";

    public static String decodeMAC(byte mac[]) {
        if (mac == null) return null;

        StringBuilder sb = new StringBuilder(18);
        for(int i = 5; i >= 0; i--)
        {
            if (sb.length() > 0)
                sb.append(':');
            sb.append(String.format("%02x", new Object[] {
                Byte.valueOf(mac[i])
            }));
        }

        return sb.toString();
    }

    public static boolean isHexCode(String code) {
        return validPattern.matcher(code).find();
    }

    public static byte[] convertHexToBytes(String code) {
        return DatatypeConverter.parseHexBinary(code);
    }

    public static byte[] fromHexString(String hex) {
        if (hex.length() % 2 != 0) throw new IllegalArgumentException("Input string must contain an even number of characters");

        byte result[] = new byte[hex.length() / 2];
        char bytes[] = hex.toCharArray();
        for(int i = 0; i < bytes.length; i += 2) {
            StringBuilder curr = new StringBuilder(2);
            curr.append(bytes[i]).append(bytes[i + 1]);
            result[i / 2] = (byte)Integer.parseInt(curr.toString(), 16);
        }

        return result;
    }

    public static String toHexString(byte raw[]) {
				if (raw == null) return null;
        StringBuilder hex = new StringBuilder(2 * raw.length);
        byte abyte0[];
        int j = (abyte0 = raw).length;
        for(int i = 0; i < j; i++) {
            byte b = abyte0[i];
            hex.append(HEXES.charAt((b & 0xf0) >> 4)).append(HEXES.charAt(b & 0xf));
        }

        return hex.toString();
    }
}
