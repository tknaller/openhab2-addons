/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.broadlink.internal;

import org.eclipse.smarthome.core.library.types.StringType;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.openhab.binding.broadlink.BroadlinkBindingConstants;

/**
 * Mappings of internal values to user-visible ones.
 *
 * @author John Marshall/Cato Sognen - Initial contribution
 */
public class ModelMapper {

    private static final StringType UNKNOWN = new StringType("UNKNOWN");

    public static ThingTypeUID getThingType(int model) {
        if (model == 0)
            return BroadlinkBindingConstants.THING_TYPE_SP1;
        if (model == 0x2711)
            return BroadlinkBindingConstants.THING_TYPE_SP2;
        if (model == 0x2719 || model == 0x7919 || model == 0x271a || model == 0x791a) 
            return BroadlinkBindingConstants.THING_TYPE_SP2; // Honeywell SP2
        if (model == 0x2720)
            return BroadlinkBindingConstants.THING_TYPE_SP2; // SPMini
        if (model == 0x753e)
            return BroadlinkBindingConstants.THING_TYPE_SP3; // SP3
        if (model == 0x7d00)
            return BroadlinkBindingConstants.THING_TYPE_SP3; // OEM-branded SP3
        if (model == 0x947a || model == 0x9479)
            return BroadlinkBindingConstants.THING_TYPE_SP3; // NB: this is ACTUALLY an SP3S - see https://github.com/mjg59/python-broadlink/blob/master/broadlink/__init__.py
        if (model == 0x2728)
            return BroadlinkBindingConstants.THING_TYPE_SP2; // SPMini2
        if (model == 0x2733 || model == 0x273e)
            return BroadlinkBindingConstants.THING_TYPE_SP2; // OEM-branded SPMini
        if (model >= 0x7530 && model <= 0x7918)
            return BroadlinkBindingConstants.THING_TYPE_SP2; // OEM-branded SPMini2
        if (model == 0x2736)
            return BroadlinkBindingConstants.THING_TYPE_SP2; // SPMiniPlus
        if (model == 10002)
            return BroadlinkBindingConstants.THING_TYPE_RM2;
        if (model == 10039)
            return BroadlinkBindingConstants.THING_TYPE_RM3;
        if (model == 10045)
            return BroadlinkBindingConstants.THING_TYPE_RM;
        if (model == 10115)
            return BroadlinkBindingConstants.THING_TYPE_RM2;
        if (model == 10108)
            return BroadlinkBindingConstants.THING_TYPE_RM2;
        if (model == 10026)
            return BroadlinkBindingConstants.THING_TYPE_RM2;
        if (model == 10119)
            return BroadlinkBindingConstants.THING_TYPE_RM2;
        if (model == 10123)
            return BroadlinkBindingConstants.THING_TYPE_RM2;
        if (model == 10127)
            return BroadlinkBindingConstants.THING_TYPE_RM;
        if (model == 10004)
            return BroadlinkBindingConstants.THING_TYPE_A1;
        if (model == 20149)
            return BroadlinkBindingConstants.THING_TYPE_MP1;
        if (model == 20251)
            return BroadlinkBindingConstants.THING_TYPE_MP2;
        if (model == 20215)
            return BroadlinkBindingConstants.THING_TYPE_MP1;
        if (model == 10018)
            return BroadlinkBindingConstants.THING_TYPE_S1C;
        if (model == 20045)
            return null;
        else
            return null;
    }

    private static StringType lookup(StringType[] values, byte b) {
        int index = Byte.toUnsignedInt(b);
        if (index < values.length) {
            return values[index];
        } else {
            return UNKNOWN;
        }
    }

    private static final StringType[] airValues = {
            new StringType("PERFECT"),
            new StringType("GOOD"),
            new StringType("NORMAL"),
            new StringType("BAD")
    };

    public static StringType getAirValue(byte b) {
        return lookup(airValues, b);
    }

    private static final StringType[] lightValues = {
            new StringType("DARK"),
            new StringType("DIM"),
            new StringType("NORMAL"),
            new StringType("BRIGHT")
    };

    public static StringType getLightValue(byte b) {
        return lookup(lightValues, b);
    }

    private static final StringType[] noiseValues = {
            new StringType("QUIET"),
            new StringType("NORMAL"),
            new StringType("NOISY"),
            new StringType("EXTREME")
    };

    public static StringType getNoiseValue(byte b) {
        return lookup(noiseValues, b);
    }
}
