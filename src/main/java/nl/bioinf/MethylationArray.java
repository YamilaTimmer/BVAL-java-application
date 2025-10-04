package nl.bioinf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MethylationArray {
    private List<String> samples= new ArrayList<>();
    private List<MethylationData> data = new ArrayList<>();
    private String header;

    public void setSamples(List<String> samples) {
        this.samples = samples;
    }

    public void setHeader(String header) {
        String[] headerSplit = header.split(",");
        StringBuilder headerString = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            headerString.append(headerSplit[i] + ",");
        }
        this.header = headerString.toString();
    }

    public String getHeader() {
        StringBuilder headerSamplesCombined = new StringBuilder();
        headerSamplesCombined.append(header).append(String.format("%s%n", this.samples));
        return header;
    }

    public void setData(List<MethylationData> data) {
        this.data = data;
    }

    public void addData(String chromosome, String gene, ArrayList<Double> betaValues) throws IllegalArgumentException {
        if (betaValues.size() != samples.size()) {
            throw new IllegalArgumentException(("Number of beta values does not match number of samples."));
        }
        data.add(new MethylationData(chromosome, gene, betaValues));
    }

    public List<MethylationData> getData() {
        return new ArrayList<>(data);
    }

    public List<String> getGenes() {
        ArrayList<String> genes = new ArrayList<>();
        for (MethylationData d : data) {
            genes.add(d.gene());
        }
        return genes;
    }

    public List<String> getSamples() {
        return new ArrayList<>(samples);
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
            stringToReturn.append(String.format("%.2f,", value));
        }
        stringToReturn.append(String.format("%n"));
        return stringToReturn.toString();
    }
}
