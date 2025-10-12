package nl.bioinf.model;

import java.util.ArrayList;

public record MethylationData(String methylationLocation, ArrayList<Double> betaValues) {


    public String getGene() {
        String[] split = methylationLocation.split(",");
        String gene = split[1];
        return gene;
    }

    public String getChromosome() {
        String[] split = methylationLocation.split(",");
        String gene = split[2];
        return gene;
    }

    @Override
    public String toString() {
        StringBuilder stringToReturn = new StringBuilder();
        stringToReturn.append( methylationLocation);
        for (double value : betaValues) {
            stringToReturn.append(String.format("%.2f,", value));
        }
        stringToReturn.deleteCharAt(stringToReturn.length()-1);
        stringToReturn.append(String.format("%n"));
        return stringToReturn.toString();
    }
}
