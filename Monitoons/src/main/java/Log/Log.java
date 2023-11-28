package Log;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Log {
    private static final Logger LOGGER = Logger.getLogger(Log.class.getName());

    public static void logError(String message) {
        LOGGER.log(Level.SEVERE, message);
    }

    public static void logWarning(String message) {
        LOGGER.log(Level.WARNING, message);
    }

    public static void logInfo(String message) {
        LOGGER.log(Level.INFO, message);
    }

    public static void logDebug(String message) {
        LOGGER.log(Level.FINE, message);
    }

    public static void logTrace(String message) {
        LOGGER.log(Level.FINEST, message);
    }
}
