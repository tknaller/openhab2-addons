// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Utils.java

package org.openhab.binding.broadlink.internal;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Utils
{

    public Utils()
    {
    }

    public static byte[] getDeviceId(byte response[])
    {
        return slice(response, 0, 4);
    }

    public static byte[] getDeviceKey(byte response[])
    {
        return slice(response, 4, 20);
    }

    public static byte[] slice(byte source[], int from, int to)
    {
        if(from > to)
            return null;
        if(to - from > source.length)
            return null;
        if(to == from)
        {
            byte sliced[] = new byte[1];
            sliced[0] = source[from];
            return sliced;
        } else
        {
            byte sliced[] = new byte[to - from];
            System.arraycopy(source, from, sliced, 0, to - from);
            return sliced;
        }
    }

    public static byte[] encrypt(byte key[], IvParameterSpec ivSpec, byte data[])
    {
        SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
        try
        {
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            cipher.init(1, secretKey, ivSpec);
            return cipher.doFinal(data);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] decrypt(byte key[], IvParameterSpec ivSpec, byte data[])
    {
        SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
        try
        {
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            cipher.init(2, secretKey, ivSpec);
            return cipher.doFinal(data);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
