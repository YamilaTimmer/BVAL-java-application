package nl.bioinf.model;

import java.util.ArrayList;

/**
 * Stores beta values and genomic information, line of methylation array file
 *
 * @param methylationLocation String that contains genomic information
 * @param betaValues          ArrayList that contains beta values
 */
public record MethylationData(String methylationLocation, ArrayList<Double> betaValues) {

    /**
     * Gets the gene name from the genomic information string
     *
     * @param location {@link HeaderIndexLocation} containing the index of the gene
     * @return String that contains the name of the gene
     */
    public String getGene(HeaderIndexLocation location) {
        String[] split = methylationLocation.split(",");
        return split[location.getGeneIndex()].trim();
    }

    /**
     * Gets the gene name from the genomic information string
     *
     * @param location {@link HeaderIndexLocation} containing the index of the chromosome
     * @return String that contains the name of the chromosome
     */
    public String getChromosome(HeaderIndexLocation location) {
        String[] split = methylationLocation.split(",");
        return split[location.getChrIndex()].trim();
    }

    @Override
    public String toString() {
        StringBuilder stringToReturn = new StringBuilder();
        stringToReturn.append(methylationLocation);
        for (double value : betaValues) {
            stringToReturn.append(String.format("%.2f,", value));
        }
        stringToReturn.deleteCharAt(stringToReturn.length() - 1);
        stringToReturn.append(String.format("%n"));
        return stringToReturn.toString();
    }
}
