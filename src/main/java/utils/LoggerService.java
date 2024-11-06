package utils;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoggerService {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Logger CONSOLE = LogManager.getLogger("ConsoleLogger");
    private static final Logger FILE = LogManager.getLogger("FileOnlyLogger");
    private static final Logger SOUT = LogManager.getLogger("Sout");

    public static void print(Object message) {
        SOUT.info(message);
    }

    public static void println(Object message) {
        SOUT.info("{}\n", message);
    }

    public static void println() {
        SOUT.info('\n');
    }

    public static void log(Level level, String message) {
        log(level, message, LOGGER);
    }

    public static void consoleLog(Level level, String message) {
        log(level, message, CONSOLE);
    }

    public static void fileLog(Level level, String message) {
        log(level, message, FILE);
    }

    private static void log(Level level, String message, Logger logger) {
        if (level.equals(Level.DEBUG)) {
            logger.debug(message);
        } else if (level.equals(Level.WARN)) {
            logger.warn(message);
        } else if (level.equals(Level.ERROR)) {
            logger.error(message);
        } else if (level.equals(Level.FATAL)) {
            logger.fatal(message);
        } else {
            logger.info(message);
        }
    }
}
