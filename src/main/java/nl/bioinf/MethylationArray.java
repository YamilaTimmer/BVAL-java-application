package nl.bioinf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MethylationArray {
    private List<String> samples= new ArrayList<>();
    private List<MethylationData> data = new ArrayList<>();
    private String header;

    public void setSamples(ArrayList<String> samples) {
        this.samples = samples;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getHeader() {
        return header;
    }

    public void addData(String chromosome, String gene, ArrayList<Double> betaValues) throws IllegalArgumentException {
        if (betaValues.size() != samples.size()) {
            throw new IllegalArgumentException(("Number of betavalues does not match number of samples."));
        }
        data.add(new MethylationData(chromosome, gene, betaValues));
    }

    public List<MethylationData> getData() {
        return new ArrayList<>(data); // Collections.unmodifiableList(data);
    }

    public List<String> getSamples() {
        return new ArrayList<>(samples); // Collections.unmodifiableList(samples);
    }

    @Override
    public String toString() {
        return "MethylationArray{" +
                "samples=" + samples +
                ", data=" + data +
                '}';
    }
}

record MethylationData(String chromosome, String gene, ArrayList<Double> betaValues) {
    @Override
    public String toString() {
        StringBuilder stringToReturn = new StringBuilder();
        stringToReturn.append(String.format("%s,%s,", gene, chromosome));
        for (double value : betaValues) {
            System.out.println("value = " + value);
            stringToReturn.append(String.format("%.2f,", value));
        }
        stringToReturn.append(String.format("%n"));
        return stringToReturn.toString();
    }
}
