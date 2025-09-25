package nl.bioinf;

import java.util.ArrayList;

public class SummaryGenerator {

    public static void summaryGenerator(MethylationArray methylationData){
        System.out.println("Generating Summary file...");

        // Get methylationdata/sampledata
        ArrayList<String> samples = methylationData.getSamples();
        ArrayList<MethylationData> dataRows = methylationData.getData();

        System.out.println(dataRows);
        double betaVal = 0.0;

        for (MethylationData row : dataRows) {
            ArrayList<Double> betas = row.betaValues();
            for (double beta : betas) {

                betaVal += beta;

            }
        }


        betaVal = betaVal / (dataRows.size() * samples.size());

        // Print general info on file
        System.out.println("Number of samples: " + samples.size());
        System.out.println("Number of genes: " + dataRows.size());
        System.out.println("Avg beta value: " + Math.round(betaVal* 100.0) / 100.0); //2 decimals


        //System.out.println("Number of NAs found: " + numNA);
    }

}
