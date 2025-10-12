package nl.bioinf.processing;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Checks validity of all filter user arguments, using all ArgumentCheckers
 */
public class CompositeUserArgumentsCheck implements UserArgumentsCheck {
    private final List<UserArgumentsCheck> filters = new ArrayList<>();
    private static final Logger logger = LogManager.getLogger(CompositeUserArgumentsCheck.class.getName());


    /**
     * Add all ArgumentCheckers (filters) to the CompositeUserArgumentsChecker
     *
     * @param filter: one of the ArgumentCheckers, for samples, cutoff, genes or chromosomes
     */
    public void addFilter(UserArgumentsCheck filter) {
        filters.add(filter);
    }

    /**
     * Goes through all filter checks for all arguments
     *
     * @return boolean true, if all checks pas
     */
    public boolean pass() {
        logger.debug("Starting validity check for the following filter(s): {}",
                // For each filter, get the name of the class, so the output is readable for the user
                filters.stream()
                        .map(f -> f.getClass().getSimpleName())
                        .toList());

        for (UserArgumentsCheck filter : filters) {
            if (!filter.pass()) {
                return false;
            }
        }
        logger.info("All validity checks passed! Moving on to filtering data.");

        return true;
    }
}
