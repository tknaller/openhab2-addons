/**
 * Copyright (c) 2010-2020 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.broadlink.internal;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.smarthome.core.thing.*;
import org.slf4j.Logger;

/**
 * Handles logging on behalf of a given Thing.
 *
 * @author John Marshall/Cato Sognen - Initial contribution
 */
@NonNullByDefault
public final class ThingLogger  {

    private final Thing thing;
    private final Logger logger;

    public ThingLogger(Thing thing, Logger logger) {
        this.thing = thing;
        this.logger = logger;
    }

    private String describeStatus() {
        if (Utils.isOnline(thing)) {
            return "^";
        }
        if (Utils.isOffline(thing)) {
            return "v";
        }
        return "?";
    }

    private Object[] prependUID(String msg, Object... args) {
        Object[] allArgs = new Object[args.length + 3];
        allArgs[0] = thing.getUID().toString().replaceFirst("^broadlink:", "");;
        allArgs[1] = describeStatus();
        System.arraycopy(args, 0, allArgs, 2, args.length);
        allArgs[allArgs.length - 1] = msg;
        return allArgs;
    }

    public void logDebug(String msg, Object... args) {
        if (logger.isDebugEnabled()) {
            logger.debug("{}[{}]: {}", prependUID(msg, args == null ? new Object[0] : args));
        }
    }

    public void logError(String msg, Object... args) {
        logger.error("{}[{}]: {}", prependUID(msg, args == null ? new Object[0] : args));
    }

    public void logWarn(String msg, Object... args) {
        logger.warn("{}[{}]: {}", prependUID(msg, args == null ? new Object[0] : args));
    }

    public void logInfo(String msg, Object... args) {
        logger.info("{}[{}]: {}", prependUID(msg, args == null ? new Object[0] : args));
    }

    public void logTrace(String msg, Object... args) {
        if (logger.isTraceEnabled()) {
            logger.trace("{}[{}]: {}", prependUID(msg, args == null ? new Object[0] : args));
        }
    }
}
