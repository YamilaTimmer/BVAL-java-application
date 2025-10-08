package nl.bioinf.processing;
import nl.bioinf.model.MethylationArray;
import nl.bioinf.model.MethylationData;
import nl.bioinf.io.MethylationFileReader;
import java.util.List;

public class SampleArgumentCheck implements UserArgumentsCheck {

    private static String[] filterSamples;
    public static List<String> samples;
    public static List<MethylationData> dataRows;
    public static MethylationArray methylationArray;
    

    public SampleArgumentCheck(String[] filterSamples) {
        SampleArgumentCheck.filterSamples = filterSamples;
        samples = MethylationFileReader.getData().getSamples();
        dataRows = MethylationFileReader.getData().getData();
        methylationArray = new MethylationArray();

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
