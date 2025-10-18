package nl.bioinf.processing;

import nl.bioinf.model.MethylationArray;
import nl.bioinf.model.MethylationData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;


/**
 * Generates short summary for input file
 */
public class SummaryGenerator {
    private static final Logger logger = LogManager.getLogger(SummaryGenerator.class.getName());

    /**
     * @param methylationData parsed data from input file, including samples, chromosomes/genes and beta values
     */
    public static void generateSummary(MethylationArray methylationData) {
        logger.info("Generating summary...");

        List<String> samples = methylationData.getSamples();
        List<MethylationData> dataRows = methylationData.getData();

        int amountNAValues = 0;
        double betaVal = 0.0;

        // Add value of all beta values to calculate the average
        logger.debug("Calculating average beta values...");
        for (MethylationData row : dataRows) {
            ArrayList<Double> betas = row.betaValues();
            for (double beta : betas) {
                if (beta >= 0) {
                    betaVal += beta;
                    continue;
                }
                // If betaVal is not >1, add it to the amount of NA values (MethylationFileReader parses NA to -1)
                amountNAValues++;
            }
        }

        double avgBetaVal = betaVal / (dataRows.size() * samples.size());


        System.out.println("Summary for input file:");
        System.out.println("Number of samples: " + samples.size());
        System.out.println("Number of genes: " + dataRows.size());
        System.out.println("Avg beta value: " + Math.round(avgBetaVal * 100.0) / 100.0); //2 decimals
        System.out.println("Amount of NA values: " + amountNAValues);

        logger.debug("Summary generated successfully.");
    }
}
