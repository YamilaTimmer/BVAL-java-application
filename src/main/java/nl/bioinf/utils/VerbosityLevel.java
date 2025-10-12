package nl.bioinf.utils;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

public class VerbosityLevel {
    // Verbosity settings

    private static final Logger logger = LogManager.getLogger(VerbosityLevel.class);

    public void applyVerbosity(int verbose) throws IllegalArgumentException {

        if (verbose < 0 || verbose > 2) {
            throw new IllegalArgumentException("Invalid verbosity level: " + verbose + ". Use 0 [error/warnings], 1 [info], " +
                    "or 2 [debug].");
        } else if (verbose == 2) {
            Configurator.setAllLevels(LogManager.getRootLogger().getName(), Level.DEBUG);
            logger.debug("Verbosity level changed to DEBUG");
        } else if (verbose == 1) {
            Configurator.setAllLevels(LogManager.getRootLogger().getName(), Level.INFO);
            logger.info("Verbosity level set to INFO");
        } else {
            Configurator.setAllLevels(LogManager.getRootLogger().getName(), Level.WARN);
            logger.warn("Verbosity level set to WARN");
        }
    }

}
