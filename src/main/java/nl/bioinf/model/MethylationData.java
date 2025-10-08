package nl.bioinf.model;

import java.util.ArrayList;

public record MethylationData(String chromosome, String gene, ArrayList<Double> betaValues) {
    @Override
    public String toString() {
        StringBuilder stringToReturn = new StringBuilder();
        stringToReturn.append(String.format("%s,%s,", gene, chromosome));
        for (double value : betaValues) {
            stringToReturn.append(String.format("%.2f,", value));
        }
        stringToReturn.deleteCharAt(stringToReturn.length()-1);
        stringToReturn.append(String.format("%n"));
        return stringToReturn.toString();
    }
}
