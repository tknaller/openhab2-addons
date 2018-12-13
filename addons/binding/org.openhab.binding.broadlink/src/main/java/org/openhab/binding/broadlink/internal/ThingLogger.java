package org.openhab.binding.broadlink.internal;

import org.openhab.binding.broadlink.internal.Utils;
import org.eclipse.smarthome.core.thing.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles logging on behalf of a given Thing.
 *
 * @author John Marshall/Cato Sognen - Initial contribution
 */
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

    private Object[] prependUID(Object... args) {
        Object[] allArgs = new Object[args.length + 2];
        allArgs[0] = thing.getUID();
        allArgs[1] = describeStatus();
        System.arraycopy(args, 0, allArgs, 2, args.length);
        return allArgs;
    }

    public void logDebug(String msg, Object... args) {
        if (logger.isDebugEnabled()) {
            logger.debug("{}[{}]: " + msg, prependUID(args == null ? new Object[0] : args));
        }
    }

    public void logError(String msg, Object... args) {
        logger.error("{}[{}]: " + msg, prependUID(args == null ? new Object[0] : args));
    }

    public void logInfo(String msg, Object... args) {
        logger.info("{}[{}]: " + msg, prependUID(args == null ? new Object[0] : args));
    }

    public void logTrace(String msg, Object... args) {
        if (logger.isTraceEnabled()) {
            logger.trace("{}[{}]: " + msg, prependUID(args == null ? new Object[0] : args));
        }
    }
}
