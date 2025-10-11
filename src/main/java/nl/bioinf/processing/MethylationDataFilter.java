package nl.bioinf.processing;

import nl.bioinf.model.MethylationArray;
import nl.bioinf.model.MethylationData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class MethylationDataFilter {

    private static final Logger logger = LogManager.getLogger(MethylationDataFilter.class.getName());

    public enum CutoffType {
        upper, lower
    }

    public enum PosFilterType {
        GENE, CHROMOSOME
    }

    public static void filterBySample(MethylationArray methylationArray, String[] samplesFilter) {

        logger.debug("Filtering sample(s) {}.", Arrays.toString(samplesFilter));

        //Retrieve data
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

        logger.debug("Successfully filtered on sample(s): {}.", filteredSamples);

        samples.clear();
        samples.addAll(filteredSamples);

        methylationArray.setData(dataRows);
        methylationArray.setSamples(samples);
        methylationArray.setHeader(methylationArray.getHeader());

        logger.debug("Saved filtered sample data.");


    }

    public static void filterByPos(MethylationArray methylationArray, PosFilterType posFilterType, String[] posFilter){

        logger.debug("Filtering on: {}{}.", posFilterType, Arrays.toString(posFilter));

        List<MethylationData> dataRows = methylationArray.getData();

        // Use iterator for removing rows from MethylationData, if user passed gene filter argument
        Iterator<MethylationData> iter = dataRows.iterator();
        String valueToCheck;

        // As long as there is a next row
        while (iter.hasNext()) {
            MethylationData row = iter.next();

            if (posFilterType == PosFilterType.GENE) {
                valueToCheck = row.gene();
            } else {
                valueToCheck = row.chromosome();
            }

            if (!Arrays.asList(posFilter).contains(valueToCheck)) {
                iter.remove();
            }
        }

        logger.debug("Successfully filtered on: {}{}.", posFilterType, Arrays.toString(posFilter));

        methylationArray.setData(dataRows);

        logger.debug("Saved filtered positional data.");

    }

    public static void filterByCutOff(MethylationArray methylationArray, float cutoff, CutoffType cutoffType){

        logger.debug("Filtering on cutoff/cutoff type: {} {}", cutoff, cutoffType);

        List<MethylationData> dataRows = methylationArray.getData();

        // Retrieve rows and make new rows for filtered values
        for (MethylationData row : dataRows) {
            ArrayList<Double> oldBetaValues = row.betaValues();
            ArrayList<Double> filteredBetaValues = getDoubles(cutoff, cutoffType, oldBetaValues);

            // Initiate new list of beta values, with only those that are >= to cutoff
            oldBetaValues.clear(); //Remove items from list
            oldBetaValues.addAll(filteredBetaValues); //Replace with filtered values
        }

        methylationArray.setData(dataRows);

        logger.debug("Successfully filtered on cutoff/cutoff type: {} {}", cutoff, cutoffType);
    }

    private static ArrayList<Double> getDoubles(float cutoff, CutoffType cutoffType, ArrayList<Double> oldBetaValues) {
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
        return filteredBetaValues;
    }
}