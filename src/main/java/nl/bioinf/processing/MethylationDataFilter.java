package nl.bioinf.processing;

import nl.bioinf.model.MethylationArray;
import nl.bioinf.model.MethylationData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Filter class that contains methods to remove columns/rows based on user passed filter arguments e.g. chromosomes/genes
 */
public class MethylationDataFilter {
    private static final Logger logger = LogManager.getLogger(MethylationDataFilter.class.getName());

    /**
     * Enum for positional filter, determined by whether user wants to filter on gene/chromosome (mutually exclusive)
     */
    public enum PosFilterType {
        GENE, CHROMOSOME
    }

    /**
     * Enum for cutoff type, determined by whether user wants to filter beta values above or below the specified cutoff
     */
    public enum CutoffType {
        upper, lower
    }

    /**
     * Filter data based on user passed argument(s) for samples, removing all columns that do not pass the filter
     *
     * @param methylationArray contains parsed data from input file, including present genes
     * @param samplesFilter String array that user has provided, containing sample names that correspond with
     *                      columns in the data
     */
    public static void filterBySample(MethylationArray methylationArray, String[] samplesFilter) {
        logger.info("Starting filtering on sample(s)");
        logger.debug("Filtering sample(s) {}.", Arrays.toString(samplesFilter));

        //Retrieve data
        List<String> samples = methylationArray.getSamples();
        List<MethylationData> dataRows = methylationArray.getData();

        // Look at which beta values columns need to be kept, based on filter samples
        ArrayList<Integer> columnsToKeep = new ArrayList<>();
        ArrayList<String> filteredSamples = new ArrayList<>();

        logger.debug("Determining what sample columns to keep...");
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
        logger.debug("Creating new data object to save filtered beta values in.");
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

        logger.debug("Saving filtered sample data.");

        samples.clear();
        samples.addAll(filteredSamples);

        methylationArray.setData(dataRows);
        methylationArray.setSamples(samples);
        methylationArray.setHeader(methylationArray.getHeader());

        logger.info("Successfully filtered on sample(s)");
    }

    /**
     * Filter data based on user passed argument(s) for genes OR chromosomes, removing all rows that do not pass the filter
     *
     * @param methylationArray contains parsed data from input file, including present genes
     * @param posFilterType enum, either CHROMOSOME or GENE
     * @param posFilter String array that user has provided, containing either chromosome- or gene names
     */
    public static void filterByPos(MethylationArray methylationArray, PosFilterType posFilterType, String[] posFilter){
        logger.info("Starting filtering on {}(s)", posFilterType);
        logger.debug("Filtering on: {} {}.", posFilterType, Arrays.toString(posFilter));

        List<MethylationData> dataRows = methylationArray.getData();

        // Use iterator for removing rows from MethylationData, if user passed gene filter argument
        Iterator<MethylationData> iter = dataRows.iterator();
        String valueToCheck;

        logger.debug("Removing rows that don't contain given {} arguments", posFilterType);
        while (iter.hasNext()) { // As long as there is a next row
            MethylationData row = iter.next();

            // Determine positional variable to filter on, either GENE or CHROMOSOME
            if (posFilterType == PosFilterType.GENE) {
                valueToCheck = row.gene();

            } else {
                valueToCheck = row.chromosome();
            }

            // remove rows that don't contain the positional variable
            if (!Arrays.asList(posFilter).contains(valueToCheck)) {
                iter.remove();
            }
        }

        logger.debug("Saving filtered {} data", posFilterType);
        methylationArray.setData(dataRows);

        logger.info("Successfully filtered on {}", posFilterType);
    }

    /**
     * Filter data based on user passed argument(s) for cutoff/cutoff type, removing all individual beta values that
     * do not pass the filter
     *
     * @param methylationArray contains parsed data from input file, including present genes
     * @param cutoff a float that sets a cutoff point on how to filter beta values
     * @param cutoffType enum, either upper or lower, determined by what direction the user wants to filter
     *                   beta values, based on the cutoff
     */
    public static void filterByCutOff(MethylationArray methylationArray, float cutoff, CutoffType cutoffType){
        logger.info("Starting filtering on cutoff/cutoff type: {} {}", cutoff, cutoffType);

        List<MethylationData> dataRows = methylationArray.getData();

        logger.debug("Iterating through rows to filter on [{} {}]", cutoff, cutoffType);
        // Retrieve rows and make new rows for filtered values
        for (MethylationData row : dataRows) {
            ArrayList<Double> oldBetaValues = row.betaValues();
            ArrayList<Double> filteredBetaValues = getDoubles(cutoff, cutoffType, oldBetaValues);

            // Initiate new list of beta values, with only those that are >= to cutoff
            oldBetaValues.clear(); //Remove items from list
            oldBetaValues.addAll(filteredBetaValues); //Replace with filtered values
        }

        logger.debug("Saving filtered cutoff data");
        methylationArray.setData(dataRows);

        logger.info("Successfully filtered on cutoff!");
    }

    /**
     * Filtering logic as used in filterByCutOff
     *
     * @param cutoff a float that sets a cutoff point on how to filter beta values
     * @param cutoffType enum, either upper or lower, determined by what direction the user wants to filter
     *      *                   beta values, based on the cutoff
     * @param oldBetaValues ArrayList containing all doubles of the MethylationArray before filtering
     * @return filteredBetaValues, ArrayList of doubles containing only the beta values that pass the filter
     */
    private static ArrayList<Double> getDoubles(float cutoff, CutoffType cutoffType, ArrayList<Double> oldBetaValues) {
        ArrayList<Double> filteredBetaValues = new ArrayList<>();

        // Filter on cutoff, depending on hypo/hyper
        for (Double betaValue : oldBetaValues) {
            if (cutoffType == CutoffType.lower) {
                if (betaValue <= cutoff) {
                    filteredBetaValues.add(betaValue);
                }
            }
            else { // if CutoffType = 'upper'
                if (betaValue >= cutoff) {
                    filteredBetaValues.add(betaValue);
                }
            }
        }
        return filteredBetaValues;
    }
}