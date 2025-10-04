package nl.bioinf;

import java.util.ArrayList;
import static nl.bioinf.FileReader.methylationData;

public class SampleFilter implements MethylationArrayFilter{

    public static ArrayList<String> samples = methylationData.getSamples();
    public static ArrayList<MethylationData> dataRows = methylationData.getData();
    public static String[] filterSamples;

    public SampleFilter(String[] filterSamples) {
        SampleFilter.filterSamples = filterSamples;

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
