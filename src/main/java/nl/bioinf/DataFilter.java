package nl.bioinf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import static nl.bioinf.FileReader.methylationData;

public class DataFilter {

    static void filterSamples(String[] samplesFilter) {
        //Retrieve data
        ArrayList<String> samples = methylationData.getSamples();
        ArrayList<MethylationData> dataRows = methylationData.getData();

        System.out.println("-------------------------------------");
        System.out.println("Filtering sample(s) " + Arrays.toString(samplesFilter));

        // Look at which betavalues columns need to be kept, based on filter samples
        ArrayList<Integer> columnsToKeep = new ArrayList<>();
        ArrayList<String> filteredSamples = new ArrayList<>();

        for (int i = 0; i < samples.size(); i++) {
            String sample = samples.get(i);

            for (String samplefilter : samplesFilter) {
                if (sample.equals(samplefilter)) {
                    columnsToKeep.add(i); // remember the index to keep
                    filteredSamples.add(sample);

                    break;
                }
            }
        }

        // Retrieve old data and initiate new arraylist
        for (MethylationData row : dataRows) {
            ArrayList<Double> oldBetaValues = row.betaValues();
            ArrayList<Double> filteredBetaValues = new ArrayList<>();

            // Keep the values that correspond to the filtered samples
            for (int index : columnsToKeep) {
                filteredBetaValues.add(oldBetaValues.get(index));
            }

            oldBetaValues.clear(); // Remove items from list
            oldBetaValues.addAll(filteredBetaValues); // Replace with filtered values
            }

        System.out.println("Succesfully filtered on sample(s): " + Arrays.toString(samplesFilter));
        System.out.println("");

        // Replace samples with the remaining samples
        samples.clear();
        samples.addAll(filteredSamples);
    }

    static void filterByGene(String[] genes){

        System.out.println("-------------------------------------");
        System.out.println("Filtering on gene(s): " + Arrays.toString(genes));

        ArrayList<MethylationData> dataRows = methylationData.getData();

            // Use iterator for removing rows from MethylationData, if user passed gene filter argument
            Iterator<MethylationData> iter = dataRows.iterator();

            // As long as there is a next row
            while (iter.hasNext()) {
                MethylationData row = iter.next();

                // Remove row if gene of that row is not in the genes to be filtered
                if (!Arrays.asList(genes).contains(row.gene())) {
                    iter.remove();

                }
            }
            System.out.println("Succesfully filtered on gene(s): " + Arrays.toString(genes));

    }

    static void filterByChr(String[] chromosomes){

        System.out.println("-------------------------------------");
        System.out.println("Filtering on chromosome(s): " + Arrays.toString(chromosomes));

        ArrayList<MethylationData> dataRows = methylationData.getData();

        // Use iterator for removing rows from MethylationData, if user passed chr filter argument
        Iterator<MethylationData> iter = dataRows.iterator();

        while (iter.hasNext()) {
            MethylationData row = iter.next();

            // Remove row if gene of that row is not in the chromosome(s) to be filtered
            if (!Arrays.asList(chromosomes).contains(row.chromosome())) {
                iter.remove();
            }
        }

        System.out.println("Succesfully filtered on chromosome(s) : " + Arrays.toString(chromosomes));

    }
    static void filterByCutOff(float cutoff){

        ArrayList<MethylationData> dataRows = methylationData.getData();

        // Retrieve rows and make new rows for filtered values
        for (MethylationData row : dataRows) {
            ArrayList<Double> oldBetaValues = row.betaValues();
            ArrayList<Double> filteredBetaValues = new ArrayList<>();

            // If betavalue >= cutoff, add to arraylist of filtered values
            for(Double betavalue : oldBetaValues){
                if (betavalue >= cutoff){
                    filteredBetaValues.add(betavalue);
                }
            }

            // Initiate new list of beta values, with only those that are >= to cutoff
            oldBetaValues.clear(); //Remove items from list
            oldBetaValues.addAll(filteredBetaValues); //Replace with filtered values
        }
    }
}
