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



    public void addData(String probe, String gene, ArrayList<Double> betaValues) throws IllegalArgumentException {
        if (betaValues.size() != samples.size()) {
            throw new IllegalArgumentException(("Number of betavalues does not match number of samples."));
        }
        data.add(new MethylationData(probe, gene, betaValues));
    }

    public ArrayList<MethylationData> getData() {
        return new ArrayList<>(data);
    }

    public ArrayList<String> getSamples() {
        return new ArrayList<String>(samples);
    }

    @Override
    public String toString() {
        return "MethylationArray{" +
                "samples=" + samples +
                ", data=" + data +
                '}';
    }
}

record MethylationData(String probe, String gene, ArrayList<Double> betaValues) {}
