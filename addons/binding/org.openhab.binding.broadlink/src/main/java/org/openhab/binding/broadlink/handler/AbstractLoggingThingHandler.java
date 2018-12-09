package org.openhab.binding.broadlink.handler;

import org.eclipse.smarthome.core.thing.*;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract superclass of all Broadlink devices that handles logging.
 *
 * @author John Marshall/Cato Sognen - Initial contribution
 */
public abstract class AbstractLoggingThingHandler extends BaseThingHandler {

    private static final Logger logger = LoggerFactory.getLogger(AbstractLoggingThingHandler.class);

    public AbstractLoggingThingHandler(Thing thing) {
        super(thing);
    }

    protected boolean isOnline() {
        return thing.getStatus().equals(ThingStatus.ONLINE);
    }

    protected boolean isOffline() {
        return thing.getStatus().equals(ThingStatus.OFFLINE);
    }

    private String describeStatus() {
        if (isOnline()) {
            return "^";
        }
        if (isOffline()) {
            return "v";
        }
        return "?";
    }

    private Object[] prependUID(Object... args) {
        Object[] allArgs = new Object[args.length + 2];
        allArgs[0] = getThing().getUID();
        allArgs[1] = describeStatus();
        System.arraycopy(args, 0, allArgs, 2, args.length);
        return allArgs;
    }

    protected void logDebug(String msg, Object... args) {
        if (logger.isDebugEnabled()) {
            logger.debug("{}[{}]: " + msg, prependUID(args == null ? new Object[0] : args));
        }
    }

    protected void logError(String msg, Object... args) {
        logger.error("{}[{}]: " + msg, prependUID(args == null ? new Object[0] : args));
    }

    protected void logInfo(String msg, Object... args) {
        logger.info("{}[{}]: " + msg, prependUID(args == null ? new Object[0] : args));
    }

    protected void logTrace(String msg, Object... args) {
        if (logger.isTraceEnabled()) {
            logger.trace("{}[{}]: " + msg, prependUID(args == null ? new Object[0] : args));
        }
    }
}
