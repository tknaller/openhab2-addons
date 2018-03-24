/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.broadlink;

import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.jdt.annotation.NonNullByDefault;
import java.util.HashSet;
import java.util.Set;

/**
 * The {@link BroadlinkBindingConstants} class defines common constants, which are
 * used across the whole binding.
 *
 * @author John Marshall/Cato Sognen - Initial contribution
 */
 @NonNullByDefault
public class BroadlinkBindingConstants
{
    // Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
    // Jad home page: http://www.kpdus.com/jad.html
    // Decompiler options: packimports(3)
    // Source File Name:   BroadlinkBindingConstants.java

    public BroadlinkBindingConstants()
    {
    }

    public static String getErrorMessage(int code)
    {
        if(code == -1)
            return "\u8BBE\u5907\u6240\u5728\u7F51\u7EDC\u5DF2\u6539\u53D8\u6216\u7F51\u7EDC\u5BC6\u7801\u5DF2\u7ECF\u4FEE\u6539";
        if(code == -2)
            return "\u8BBE\u5907\u5DF2\u5728\u5176\u4ED6\u5730\u65B9\u767B\u5F55,\u5982\u9700\u7EE7\u7EED\u63A7\u5236,\u8BF7\u91CD\u65B0\u767B\u5F55(\u9488\u5BF9rm1/sp1)";
        if(code == -3)
            return "\u8BBE\u5907\u4E0D\u5728\u7EBF";
        if(code == -4)
            return "\u4E0D\u652F\u6301\u7684\u64CD\u4F5C";
        if(code == -5)
            return "\u7A7A\u95F4\u6EE1";
        if(code == -6)
            return "\u6570\u636E\u7ED3\u6784\u5F02\u5E38";
        if(code == -7)
            return "\u8BBE\u5907\u5DF2\u7ECF\u590D\u4F4D,\u9700\u8FDB\u5165\u5C40\u57DF\u7F51\u91CD\u65B0\u8BA4\u8BC1\u3002(\u9488\u5BF9sp1/rm1\u4EE5\u5916\u7684\u8BBE\u5907)";
        if(code == -100)
            return "\u8D85\u65F6";
        if(code == -101)
            return "\u7F51\u7EDC\u7EBF\u7A0B\u627E\u4E0D\u5230\u8BE5\u8BBE\u5907";
        if(code == -102)
            return "\u5185\u5B58\u4E0D\u8DB3";
        if(code == -103)
            return "\u8BBE\u5907\u672A\u521D\u59CB\u5316";
        if(code == -104)
            return "\u7F51\u7EDC\u7EBF\u7A0B\u5DF2\u6682\u505C";
        if(code == -105)
            return "\u8FD4\u56DE\u6D88\u606F\u7C7B\u578B\u9519\u8BEF";
        if(code == -106)
            return "\u64CD\u4F5C\u8FC7\u4E8E\u9891\u7E41";
        if(code == -107)
            return "\u670D\u52A1\u5668\u5DF2\u62D2\u7EDD\u8BE5license\u64CD\u4F5C,\u8BF7\u8054\u7CFB\u5BA2\u670D\u5904\u7406";
        if(code == -108)
            return "\u8BBE\u5907\u4E0D\u5728\u5C40\u57DF\u7F51\u4E2D";
        if(code == -10000)
            return "\u672A\u77E5\u9519\u8BEF";
        else
            return (new StringBuilder("Unknown error code: ")).append(code).toString();
    }

    public static final String BINDING_ID = "broadlink";
    public static final String CAST_ADDRESS = "255.255.255.255";
    public static final int CAST_PORT = 80;
    public static final int BUFFER_LENGTH = 1024;
    public static final int TIMEOUT = 5000;
    public static final int DISCOVERY_TIMEOUT = 10;
    public static final int POLLING_TIME = 30;
    public static final ThingTypeUID THING_TYPE_RM;
    public static final ThingTypeUID THING_TYPE_RM2;
    public static final ThingTypeUID THING_TYPE_RM3;
    public static final ThingTypeUID THING_TYPE_A1;
    public static final ThingTypeUID THING_TYPE_MP1;
    public static final ThingTypeUID THING_TYPE_MP2;
    public static final ThingTypeUID THING_TYPE_SP1;
    public static final ThingTypeUID THING_TYPE_SP2;
    public static final ThingTypeUID THING_TYPE_SP3;
    public static final ThingTypeUID THING_TYPE_S1C;
    public static final ThingTypeUID THING_TYPE_PIR;
    public static final ThingTypeUID THING_TYPE_MAGNET;
    public static final Set SUPPORTED_THING_TYPES_UIDS;
    public static final String CHANNEL_COMMAND = "command";
    public static final String CHANNEL_TEMPERATURE = "temperature";
    public static final String CHANNEL_HUMIDITY = "humidity";
    public static final String CHANNEL_NOISE = "noise";
    public static final String CHANNEL_LIGHT = "light";
    public static final String CHANNEL_AIR = "air";
    public static final String CHANNEL_POWER_ON = "powerOn";
    public static final String CHANNEL_POWER_CONSUMPTION = "powerConsumption";
    public static final String CHANNEL_S1_POWER_ON = "s1powerOn";
    public static final String CHANNEL_S2_POWER_ON = "s2powerOn";
    public static final String CHANNEL_S3_POWER_ON = "s3powerOn";
    public static final String CHANNEL_S4_POWER_ON = "s4powerOn";
    public static final String CHANNEL_MOTION = "motion";
    public static final String CHANNEL_MOTION_OFF_TIMER = "motionOffTimer";
    public static final String CHANNEL_LAST_MOTION = "lastMotion";
    public static final String CHANNEL_IS_OPEN = "isOpen";
    public static final String CHANNEL_LAST_OPENED = "lastOpened";
    public static final String CHANNEL_OPEN_ALARM = "isOpenAlarm";
    public static final String CHANNEL_OPEN_ALARM_TIMER = "isOpenAlarmTimer";
    public static final String HOST = "ipAddress";
    public static final String PORT = "port";
    public static final String MAC = "mac";
    public static final String KEY = "key";
    public static final String ID = "id";
    public static final String POLLING_INTERVAL = "pollingInterval";
    public static final String CONFIG_MAP_FILENAME = "mapFilename";
    public static final String AUTHORIZATION_KEY = "authorizationKey";
    public static final String IV = "iv";
    public static final String PROPERTY_KEY = "key";
    public static final String PROPERTY_ID = "id";
    public static final String API_ID = "api_id";
    public static final String COMMAND = "command";
    public static final String LICENSE = "license";
    public static final String CODE = "code";
    public static final String MSG = "msg";
    public static final String STATUS = "status";
    public static final String TEMPERATURE = "temperature";
    public static final float INVALID_TEMPERATURE = -999F;
    public static final String RM = "Broadlink RM";
    public static final String RM1 = "Broadlink RM1";
    public static final String RM2 = "Broadlink RM2";
    public static final String RM3 = "Broadlink RM3";
    public static final String RMProPhicomm = "RMProPhicomm";
    public static final String RM2HomePlus = "RM2HomePlus";
    public static final String RM2HomePlusGDT = "RM2HomePlusGDT";
    public static final String RM2ProPlus = "RM2ProPlus";
    public static final String RM2ProPlus2 = "RM2ProPlus2";
    public static final String RM2ProPlusBL = "RM2ProPlusBL";
    public static final String RMMiniShate = "RMMiniShate";
    public static final String A1 = "Broadlink A1";
    public static final String TC1 = "Broadlink TC1";
    public static final String TC2 = "Broadlink TC2";
    public static final String MP1 = "Broadlink MP1";
    public static final String MP2 = "Broadlink MP2";
    public static final String SPMini = "SPMini";
    public static final String SP1 = "SP1";
    public static final String SP2 = "SP2";
    public static final String SP3 = "SP3";
    public static final String SPMini2 = "SPMini2";
    public static final String OEMSPMini = "OEMSPMini";
    public static final String OEMSPMini2 = "OEMSPMini2";
    public static final String SPMiniPlus = "SPMiniPlus";
    public static final String S1C = "Smart One Controller";
    public static final String S1P = "Smart One PIR Sensor";
    public static final String S1M = "Smart One Magnet Sensor";
    public static final String NOT_SUPPORTED = "Not_supported";
    public static final byte CMD_COMMAND = 106;
    public static final byte CMD_AUTHORIZE = 101;
    public static final byte CMD_DISCOVER = 26;
    public static final String CMD_NETWORK_INIT = "network_init";
    public static final String CMD_SDK_VERSION = "SDK_version";
    public static final String CMD_PROBE_LIST = "probe_list";
    public static final String CMD_DEVICE_ADD = "device_add";
    public static final String CMD_DEVICE_UPDATE = "device_update";
    public static final String CMD_DEVICE_DELETE = "device_delete";
    public static final String CMD_DEVICE_LAN_IP = "device_lan_ip";
    public static final String CMD_DEVICE_STATE = "device_state";
    public static final String CMD_SP2_REFRESH = "sp2_refresh";
    public static final String CMD_SP2_CONTROL = "sp2_control";
    public static final String CMD_SP2_CURRENT_POWER = "sp2_current_power";
    public static final String CMD_RM1_AUTH = "rm1_auth";
    public static final String CMD_RM1_STUDY = "rm1_study";
    public static final String CMD_RM1_CODE = "rm1_code";
    public static final String CMD_RM1_SEND = "rm1_send";
    public static final String CMD_RM2_REFRESH = "rm2_refresh";
    public static final String CMD_RM2_STUDY = "rm2_study";
    public static final String CMD_RM2_CODE = "rm2_code";
    public static final String CMD_RM2_SEND = "rm2_send";
    public static final String CMD_EASY_CONFIG = "easyconfig";
    public static final int CMD_NETWORK_INIT_ID = 1;
    public static final int CMD_SDK_VERSION_ID = 2;
    public static final int CMD_PROBE_LIST_ID = 11;
    public static final int CMD_DEVICE_ADD_ID = 12;
    public static final int CMD_DEVICE_UPDATE_ID = 13;
    public static final int CMD_DEVICE_DELETE_ID = 14;
    public static final int CMD_DEVICE_LAN_IP_ID = 15;
    public static final int CMD_DEVICE_STATE_ID = 16;
    public static final int CMD_SP2_REFRESH_ID = 71;
    public static final int CMD_SP2_CONTROL_ID = 72;
    public static final int CMD_SP2_CURRENT_POWER_ID = 74;
    public static final int CMD_RM1_AUTH_ID = 101;
    public static final int CMD_RM1_STUDY_ID = 102;
    public static final int CMD_RM1_CODE_ID = 103;
    public static final int CMD_RM1_SEND_ID = 104;
    public static final int CMD_RM2_REFRESH_ID = 131;
    public static final int CMD_RM2_STUDY_ID = 132;
    public static final int CMD_RM2_CODE_ID = 133;
    public static final int CMD_RM2_SEND_ID = 134;
    public static final int CMD_EASY_CONFIG_ID = 10000;

    static
    {
        THING_TYPE_RM = new ThingTypeUID("broadlink", "rm");
        THING_TYPE_RM2 = new ThingTypeUID("broadlink", "rm2");
        THING_TYPE_RM3 = new ThingTypeUID("broadlink", "rm3");
        THING_TYPE_A1 = new ThingTypeUID("broadlink", "a1");
        THING_TYPE_MP1 = new ThingTypeUID("broadlink", "mp1");
        THING_TYPE_MP2 = new ThingTypeUID("broadlink", "mp2");
        THING_TYPE_SP1 = new ThingTypeUID("broadlink", "sp1");
        THING_TYPE_SP2 = new ThingTypeUID("broadlink", "sp2");
        THING_TYPE_SP3 = new ThingTypeUID("broadlink", "sp3");
        THING_TYPE_S1C = new ThingTypeUID("broadlink", "s1c");
        THING_TYPE_PIR = new ThingTypeUID("broadlink", "s1p");
        THING_TYPE_MAGNET = new ThingTypeUID("broadlink", "s1m");
        SUPPORTED_THING_TYPES_UIDS = new HashSet();
        SUPPORTED_THING_TYPES_UIDS.add(THING_TYPE_RM);
        SUPPORTED_THING_TYPES_UIDS.add(THING_TYPE_RM2);
        SUPPORTED_THING_TYPES_UIDS.add(THING_TYPE_RM3);
        SUPPORTED_THING_TYPES_UIDS.add(THING_TYPE_A1);
        SUPPORTED_THING_TYPES_UIDS.add(THING_TYPE_MP1);
        SUPPORTED_THING_TYPES_UIDS.add(THING_TYPE_MP2);
        SUPPORTED_THING_TYPES_UIDS.add(THING_TYPE_SP1);
        SUPPORTED_THING_TYPES_UIDS.add(THING_TYPE_SP2);
        SUPPORTED_THING_TYPES_UIDS.add(THING_TYPE_SP3);
        SUPPORTED_THING_TYPES_UIDS.add(THING_TYPE_S1C);
        SUPPORTED_THING_TYPES_UIDS.add(THING_TYPE_PIR);
        SUPPORTED_THING_TYPES_UIDS.add(THING_TYPE_MAGNET);
    }
}

