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

    public static ThingTypeUID getThingType(int model) {
        if (model == 0)
            return BroadlinkBindingConstants.THING_TYPE_SP1;
        if (model == 10001)
            return BroadlinkBindingConstants.THING_TYPE_SP2;
        if (model == 10009 || model == 31001 || model == 10010 || model == 31002)
            return BroadlinkBindingConstants.THING_TYPE_SP2;
        if (model == 10016)
            return BroadlinkBindingConstants.THING_TYPE_SP2;
        if (model == 30014)
            return BroadlinkBindingConstants.THING_TYPE_SP2;
        if (model == 10024)
            return BroadlinkBindingConstants.THING_TYPE_SP2;
        if (model == 10035 || model == 10046)
            return BroadlinkBindingConstants.THING_TYPE_SP2;
        if (model >= 30000 && model <= 31000)
            return BroadlinkBindingConstants.THING_TYPE_SP2;
        if (model == 10038)
            return BroadlinkBindingConstants.THING_TYPE_SP2;
        if (model == 30014)
            return BroadlinkBindingConstants.THING_TYPE_SP3;
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

    public static StringType getAirValue(byte b) {
        int air = Byte.toUnsignedInt(b);
        if (air == 0)
            return new StringType("PERFECT");
        if (air == 1)
            return new StringType("GOOD");
        if (air == 2)
            return new StringType("NORMAL");
        if (air == 3)
            return new StringType("BAD");
        else
            return new StringType("UNKNOWN");
    }

    public static StringType getLightValue(byte b) {
        int light = Byte.toUnsignedInt(b);
        if (light == 0)
            return new StringType("DARK");
        if (light == 1)
            return new StringType("DIM");
        if (light == 2)
            return new StringType("NORMAL");
        if (light == 3)
            return new StringType("BRIGHT");
        else
            return new StringType("UNKNOWN");
    }

    public static StringType getNoiseValue(byte b) {
        int noise = Byte.toUnsignedInt(b);
        if (noise == 0)
            return new StringType("QUIET");
        if (noise == 1)
            return new StringType("NORMAL");
        if (noise == 2)
            return new StringType("NOISY");
        if (noise == 3)
            return new StringType("EXTREME");
        else
            return new StringType("UNKNOWN");
    }
}
