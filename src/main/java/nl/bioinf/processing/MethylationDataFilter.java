package nl.bioinf.processing;

import nl.bioinf.model.MethylationArray;
import nl.bioinf.model.MethylationData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class MethylationDataFilter {

    public enum CutoffType {
        upper, lower
    }

    public static MethylationArray filterBySample(MethylationArray methylationArray, String[] samplesFilter) {
        //Retrieve data

        System.out.println("-------------------------------------");
        System.out.println("Filtering sample(s) " + Arrays.toString(samplesFilter));

        List<String> samples = methylationArray.getSamples();
        List<MethylationData> dataRows = methylationArray.getData();

        // Look at which beta values columns need to be kept, based on filter samples
        ArrayList<Integer> columnsToKeep = new ArrayList<>();
        ArrayList<String> filteredSamples = new ArrayList<>();

        for (int i = 0; i < samples.size(); i++) {
            String sample = samples.get(i);

            for (String sampleFilter : samplesFilter) {
                if (sample.equals(sampleFilter)) {
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

        System.out.println("Succesfully filtered on sample(s): " + filteredSamples);

        samples.clear();
        samples.addAll(filteredSamples);

        methylationArray.setData(dataRows);
        methylationArray.setSamples(samples);

        return methylationArray;
    }

   public static MethylationArray filterByGene(MethylationArray methylationArray, String[] genes){

        System.out.println("-------------------------------------");
        System.out.println("Filtering on gene(s): " + Arrays.toString(genes));

       List<MethylationData> dataRows = methylationArray.getData();

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

        methylationArray.setData(dataRows);
        System.out.println("Succesfully filtered on gene(s): " + Arrays.toString(genes));

        return methylationArray;
    }

   public static MethylationArray filterByChr(MethylationArray methylationArray, String[] chromosomes){

        System.out.println("-------------------------------------");
        System.out.println("Filtering on chromosome(s): " + Arrays.toString(chromosomes));

       List<MethylationData> dataRows = methylationArray.getData();

       // Use iterator for removing rows from MethylationData, if user passed chr filter argument
        Iterator<MethylationData> iter = dataRows.iterator();

        while (iter.hasNext()) {
            MethylationData row = iter.next();

            // Remove row if gene of that row is not in the chromosome(s) to be filtered
            if (!Arrays.asList(chromosomes).contains(row.chromosome())) {
                iter.remove();
            }
        }

        System.out.println("\u001B[32mSuccesfully filtered on chromosome(s) : \u001B[0m" + Arrays.toString(chromosomes));

        methylationArray.setData(dataRows);

        return methylationArray;

    }

   public static MethylationArray filterByCutOff(MethylationArray methylationArray, float cutoff, CutoffType cutoffType){

       List<MethylationData> dataRows = methylationArray.getData();

       // Retrieve rows and make new rows for filtered values
        for (MethylationData row : dataRows) {
            ArrayList<Double> oldBetaValues = row.betaValues();
            ArrayList<Double> filteredBetaValues = new ArrayList<>();

            // Filter on cutoff, depending on hypo/hyper
            for (Double betaValue : oldBetaValues) {
                if (cutoffType == CutoffType.lower) {
                    if (betaValue <= cutoff) {
                        filteredBetaValues.add(betaValue);
                    }
                }
                else {
                    if (betaValue >= cutoff) {
                        filteredBetaValues.add(betaValue);
                    }
                }
            }

            // Initiate new list of beta values, with only those that are >= to cutoff
            oldBetaValues.clear(); //Remove items from list
            oldBetaValues.addAll(filteredBetaValues); //Replace with filtered values
        }

        methylationArray.setData(dataRows);

        return methylationArray;
    }
}