package nl.bioinf.processing;
import nl.bioinf.model.MethylationArray;
import nl.bioinf.model.MethylationData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.List;

public class SampleArgumentCheck implements UserArgumentsCheck{

    private static String[] filterSamples;
    public static List<String> samples;
    public static List<MethylationData> dataRows;
    private static final Logger logger = LogManager.getLogger(SampleArgumentCheck.class.getName());


    public SampleArgumentCheck(String[] filterSamples, MethylationArray methylationArray) {
        SampleArgumentCheck.filterSamples = filterSamples;
        samples = methylationArray.getSamples();
        dataRows = methylationArray.getData();

    }

    public boolean pass() throws IllegalArgumentException {

        // Checks whether all user input samples are present before continuing
        for (String sample : filterSamples) {
            if (!samples.contains(sample)) {
                logger.error("""
                        The following sample is not present in the data: '{}'. Please only pass samples that are present in the input file."
                        """, sample);
                throw new IllegalArgumentException("\u001B[31mError: Given sample was not found in input file. \u001B[0m");
            }
        }
        return true;
    }

}
