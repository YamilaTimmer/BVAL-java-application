package nl.bioinf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class DataFilter {

    static void filterSamples(MethylationArray methylationData, String[] samplesFilter){
        ArrayList<String> samples = methylationData.getSamples();
        ArrayList<MethylationData> dataRows = methylationData.getData();

        System.out.println("Filtering sample(s) " + Arrays.toString(samplesFilter));

        // Look at which betavalues columns need to be kept, based on filter samples
        ArrayList<Integer> columnsToKeep = new ArrayList<>();
        for (int i = 0; i < samples.size(); i++) {
            String sample = samples.get(i);

            for (String samplefilter : samplesFilter) {
                if (sample.equals(samplefilter)) {
                    columnsToKeep.add(i); // remember the index to keep
                    break;
                }
            }
        }

        for (MethylationData row : dataRows) {
            ArrayList<Double> oldBetaValues = row.betaValues();
            ArrayList<Double> filteredBetaValues = new ArrayList<>();

            //Keep the values that correspond to the filtered samples
            for (int index : columnsToKeep) {
                filteredBetaValues.add(oldBetaValues.get(index));
            }
            oldBetaValues.clear(); //Remove items from list
            oldBetaValues.addAll(filteredBetaValues); //Replace with filtered values
        }

        System.out.println("Succesfully filtered on samples: " + Arrays.toString(samplesFilter));

    }

    static void filterPos(MethylationArray methylationData, int[] chr, String[] genes){

        System.out.println("Filtering on: " + Arrays.toString(genes));

        ArrayList<String> samples = methylationData.getSamples();
        ArrayList<MethylationData> dataRows = methylationData.getData();

        if (genes != null) {
            // Use iterator for removing rows from MethylationData, if user passed gene filter argument
            Iterator<MethylationData> iter = dataRows.iterator();

            while (iter.hasNext()) {
                MethylationData row = iter.next();

                // Remove row if gene of that row is not in the genes to be filtered
                if (!Arrays.asList(genes).contains(row.gene())) {
                    iter.remove();

                }
            }
            System.out.println("Succesfully filtered on : " + Arrays.toString(genes));

        }

        if (chr != null){
            // Chr filter, to be implemented
            System.out.println("Succesfully filtered on : " + Arrays.toString(chr));

        }

    }
}
