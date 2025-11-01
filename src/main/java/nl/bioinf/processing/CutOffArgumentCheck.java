package nl.bioinf.processing;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Checks validity of cutoff argument passed by user
 */
public class CutOffArgumentCheck implements UserArgumentsCheck {
    private static final Logger logger = LogManager.getLogger();
    private final float cutoff;

    /**
     * Sets passed cutoff argument as class variable.
     *
     * @param cutoff: a float that sets a cutoff point on how to filter beta values
     */
    public CutOffArgumentCheck(float cutoff) {
        this.cutoff = cutoff;
    }

    /**
     * Checks whether cutoff argument lies within valid input
     *
     * @return boolean true, if the check passes
     * @throws IllegalArgumentException if the check fails, meaning the passed cutoff value does not lie within
     *                                  valid input range
     */
    @Override
    public boolean pass() throws IllegalArgumentException {
        double cutoffMin = 0.0;
        double cutoffMax = 1.0;
        logger.info("Starting validity check for cutoff filter...");

        // Check if passed cutoff is too high/low (beta-values have a range of [0.0-1.0]
        if (cutoff > cutoffMax | cutoff < cutoffMin) {

            logger.error("Provided cutoff value: '{}' is out of bounds, please provide a cutoff value within " +
                    "a range of [0.0-1.0].", cutoff);
            throw new IllegalArgumentException();
        }
        logger.info("Passed validity check for cutoff filter!");
        return true;
    }
}
