package nl.bioinf.utils;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

/**
 * Class for verbosity settings in CommandLineParser, which can be passed as user argument
 */
public class VerbosityLevel {
    private static final Logger logger = LogManager.getLogger(VerbosityLevel.class);


    /**
     * @param verbose int as passed by user, followed by -v/--verbose
     * @throws IllegalArgumentException if verbose is out of reach <0 or >2 will throw exception
     */
    public void applyVerbosity(int verbose) throws IllegalArgumentException {

        if (verbose < 0 || verbose > 2) {
            throw new IllegalArgumentException("Invalid verbosity level: " + verbose + ". Use 0 [error/warnings], 1 [info], " +
                    "or 2 [debug].");
        } else if (verbose == 2) {
            Configurator.setAllLevels(LogManager.getRootLogger().getName(), Level.DEBUG);
            logger.debug("Verbosity level set to DEBUG");
        } else if (verbose == 1) {
            Configurator.setAllLevels(LogManager.getRootLogger().getName(), Level.INFO);
            logger.info("Verbosity level set to INFO");
        } else {
            Configurator.setAllLevels(LogManager.getRootLogger().getName(), Level.WARN);
        }
    }

}
