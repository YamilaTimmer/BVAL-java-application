package nl.bioinf;

import java.util.ArrayList;

public class SummaryGenerator {

    public static void summaryGenerator(MethylationArray methylationData){
        System.out.println("Generating summary...");
        System.out.println("---------------------");

        // Get methylation data/sample data
        ArrayList<String> samples = methylationData.getSamples();
        ArrayList<MethylationData> dataRows = methylationData.getData();
        int amountNAValues = 0;

        double betaVal = 0.0;

        for (MethylationData row : dataRows) {
            ArrayList<Double> betas = row.betaValues();
            for (double beta : betas) {
                if (beta > 0) {
                    betaVal += beta;
                    continue;
                }
                amountNAValues++;

            }
        }

        betaVal = betaVal / (dataRows.size() * samples.size());

        // Print general info on file
        System.out.println("Number of samples: " + samples.size());
        System.out.println("Number of genes: " + dataRows.size());
        System.out.println("Avg beta value: " + Math.round(betaVal* 100.0) / 100.0); //2 decimals
        System.out.println("Amount of NA values: " + amountNAValues);
    }

}
