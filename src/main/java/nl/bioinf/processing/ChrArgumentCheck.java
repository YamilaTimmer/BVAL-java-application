package nl.bioinf.processing;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Checks validity of chromosome argument(s) passed by user
 */
public class ChrArgumentCheck implements UserArgumentsCheck {
    private final String[] filterChr;
    private final Logger logger = LogManager.getLogger(ChrArgumentCheck.class.getName());

    /**
     * Sets passed chromosome arguments as class variable.
     *
     * @param filterChr String array user argument, that should contain one or more chromosome
     */
    public ChrArgumentCheck(String[] filterChr) {
        this.filterChr = filterChr;
    }

    /**
     * Checks whether chromosome argument(s) are either a number between 1-23 or X/Y.
     *
     * @return boolean true, if the check passes
     * @throws IllegalArgumentException if the check fails (meaning (one of) the passed argument(s) is not a chromosome
     */
    @Override
    public boolean pass() throws IllegalArgumentException {
        logger.info("Starting validity check for chromosome filter...");

        for (String chr : filterChr) {
            logger.debug("Validity check for user provided chromosome: '{}'", chr);

            // Accept 'X' and 'Y' as argument, as they are valid input for chromosomes
            if (chr.equalsIgnoreCase("X") || chr.equalsIgnoreCase("Y")) {
                continue;
            }

            // If not 'X' or 'Y', try to parse to int to determine whether it lies in the range of [1-23]
            try {
                int chrInt = Integer.parseInt(chr);
                // Throw IllegalArgumentException for int < 1 or > 23
                if (chrInt < 1 || chrInt > 23) {
                    logger.error("Invalid input: '{}', number is either < 1 or > 23 please provide a number " +
                            "between 1-23 or X/Y for chromosome input.", chr);
                    throw new IllegalArgumentException("Invalid chromosome: " + chr + ". Must be 1-23 or X/Y.");
                }

                // Throw NumberFormatException if chr could not be parsed to int
            } catch (NumberFormatException ex) {
                logger.error("Invalid input: '{}', unknown character please provide a number between 1-23 or " +
                        "X/Y for chromosome input.", chr);
                throw ex;
            }
        }
        logger.info("Passed validity check for chromosome filter!");
        return true;
    }
}
