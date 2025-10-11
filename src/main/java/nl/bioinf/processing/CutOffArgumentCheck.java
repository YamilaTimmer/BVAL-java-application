package nl.bioinf.processing;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CutOffArgumentCheck implements UserArgumentsCheck {
    public static float cutoff;
    public static MethylationDataFilter.CutoffType cutoffType;
    private static final Logger logger = LogManager.getLogger(CutOffArgumentCheck.class.getName());


    public CutOffArgumentCheck(float cutoff, MethylationDataFilter.CutoffType cutoffType) {
        CutOffArgumentCheck.cutoff = cutoff;
        CutOffArgumentCheck.cutoffType = cutoffType;

    }

    @Override
    public boolean pass() throws IllegalArgumentException {

        // Cutoff too low/high
        if (cutoff > 1.0 | cutoff < 0.0) {

            logger.error("""
                 Provided cutoff value: '{}' is out of bounds, please provide a cutoff value within a range of [0.0-1.0].
                 """, cutoff);
            throw new IllegalArgumentException("\u001B[31mError: Please provide a cutoff value between 0.0 and 1.0 \u001B[0m");
        }

        return true;
    }
}
