package nl.bioinf;

import java.util.ArrayList;

public class MethylationArray {
    private ArrayList<String> samples;
    private ArrayList<MethylationData> data;

    public MethylationArray() {
        this.data = new ArrayList<>();
    }

    public void setSamples(ArrayList<String> samples) {
        this.samples = samples;
    }

    public void setData(ArrayList<MethylationData> data) {
        this.data = data;
    }

    public void addData(String chromosome, String gene, ArrayList<Double> betaValues) throws IllegalArgumentException {
        if (betaValues.size() != samples.size()) {
            throw new IllegalArgumentException(("Number of betavalues does not match number of samples."));
        }
        data.add(new MethylationData(chromosome, gene, betaValues));
    }

    public ArrayList<MethylationData> getData() {
        return new ArrayList<>(data);
    }

    public ArrayList<String> getGenes() {
        ArrayList<String> genes = new ArrayList<>();
        for (MethylationData d : data) {
            genes.add(d.gene());
        }
        return genes;
    }

    public ArrayList<String> getSamples() {
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

record MethylationData(String chromosome, String gene, ArrayList<Double> betaValues) {}
