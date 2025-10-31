package nl.bioinf.processing;

import nl.bioinf.model.MethylationArray;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * Checks validity of sample argument(s) passed by user
 */
public class SampleArgumentCheck implements UserArgumentsCheck {
    private static final Logger logger = LogManager.getLogger();
    private final String[] filterSamples;
    private final List<String> samples;

    /**
     * Sets passed sample filter argument(s) and all samples present in dataset as class variable
     *
     * @param filterSamples    String array user argument, that should contain one or more sample
     * @param methylationArray contains parsed data from input file, including present samples
     */
    public SampleArgumentCheck(String[] filterSamples, MethylationArray methylationArray) {
        this.filterSamples = filterSamples;
        samples = methylationArray.getSamples();

    }

    /**
     * Checks whether filter sample argument(s) are valid, meaning they exist in the input data
     *
     * @return boolean true, if the check passes
     * @throws IllegalArgumentException if the check fails (meaning (one of) the passed argument(s) does not exist in
     *                                  the input data
     */
    public boolean pass() throws IllegalArgumentException {
        logger.info("Starting validity check for sample filter...");

        // Checks whether all user input samples to filter on are present in the data before continuing
        for (String sample : filterSamples) {
            logger.debug("Validity check for user provided sample '{}'", sample);

            if (!samples.contains(sample)) {
                logger.error("The following sample is not present in the data: '{}'. Please only pass samples " +
                        "that are present in the input file.", sample);
                throw new IllegalArgumentException();
            }
        }
        logger.info("Passed validity check for sample filter!");
        return true;
    }
}
