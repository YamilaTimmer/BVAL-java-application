package nl.bioinf;

import java.util.ArrayList;
import static nl.bioinf.MethylationFileReader.methylationData;
import java.util.List;

import static nl.bioinf.MethylationFileReader.methylationData;

public class SampleFilterCheck implements MethylationArrayFilter{

    public static List<String> samples = methylationData.getSamples();
    public static List<MethylationData> dataRows = methylationData.getData();
    public static String[] filterSamples;

    public SampleFilterCheck(String[] filterSamples) {
        SampleFilterCheck.filterSamples = filterSamples;

    }

    @Override
    public boolean pass(MethylationArray methylationArray) {

        // Checks whether the user has not passed > 15 samples
        if (dataRows.size() > 15) {
            System.err.println("Please filter on a maximum of 15 samples.");
            return false;
        }

        // Checks whether all user input samples are present before continuing
        for (String sample : filterSamples){
            if (!samples.contains(sample)){
                System.err.println("The following sample is not present in the data: '" + sample +
                        "'. Please only insert samples that are present.");
                return false;
            }
        }

        return true;
    }

}
