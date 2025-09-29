package nl.bioinf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MethylationArray {
    private List<String> samples= new ArrayList<>();
    private List<MethylationData> data = new ArrayList<>();

    public void setSamples(ArrayList<String> samples) {
        this.samples = samples;
    }

    public void addData(String chromosome, String gene, ArrayList<Double> betaValues) throws IllegalArgumentException {
        if (betaValues.size() != samples.size()) {
            throw new IllegalArgumentException(("Number of betavalues does not match number of samples."));
        }
        data.add(new MethylationData(chromosome, gene, betaValues));
    }

    public List<MethylationData> getData() {
        return Collections.unmodifiableList(data);
    }

    public List<String> getSamples() {
        return Collections.unmodifiableList(samples);
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
