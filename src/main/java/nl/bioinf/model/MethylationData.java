package nl.bioinf.model;

import java.util.ArrayList;

public record MethylationData(String methylationLocation, ArrayList<Double> betaValues) {

    public String getGene(DataIndexLocation location) {
        String[] split = methylationLocation.split(",");
        return split[location.getGeneIndex()].trim();
    }

    public String getChromosome(DataIndexLocation location) {
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
