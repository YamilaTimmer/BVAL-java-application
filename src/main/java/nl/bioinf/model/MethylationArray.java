package nl.bioinf.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Datatype to hold methylation data containing beta values, also holds information on samples,
 * genes and chromosomes
 */
public class MethylationArray {
    private List<String> samples = new ArrayList<>();
    private List<MethylationData> data = new ArrayList<>();
    private String header;
    private DataIndexLocation indexInformation = null;

    public void setSamples(List<String> samples) {
        this.samples = samples;
    }

    public List<String> getSamples() {
        return new ArrayList<>(samples);
    }

    /**
     * Stores the file's header, without samples, in this class
     * @param header
     */
    public void setHeader(String header) {
        String[] headerSplit = header.split(",");
        StringBuilder headerString = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            headerString.append(headerSplit[i]).append(",");
        }
        this.header = headerString.toString();
    }

    public String getHeader() {
        return header + String.format("%s", String.join(",", this.samples));
    }

    public void setData(List<MethylationData> data) {
        this.data = data;
    }

    public void addData(String methylationLocation, ArrayList<Double> betaValues) throws IllegalArgumentException {
        if (betaValues.size() != samples.size()) {
            throw new IllegalArgumentException(("Number of beta values does not match number of samples."));
        }
        data.add(new MethylationData(methylationLocation, betaValues));
    }

    public List<MethylationData> getData() {
        return new ArrayList<>(data);
    }

    /**
     * Used for checking if user filter argument genes are present in the input data
     *
     * @return list of all genes present in data
     */
    public List<String> getGenes() {
        ArrayList<String> genes = new ArrayList<>();
        for (MethylationData d : data) {
            genes.add(d.getGene(indexInformation).toUpperCase());
        }
        return Collections.unmodifiableList(genes);
    }

    public DataIndexLocation getIndexInformation() {
        return indexInformation;
    }

    public void setIndexInformation(DataIndexLocation indexInformation) {
        this.indexInformation = indexInformation;
    }

    @Override
    public String toString() {
        return "MethylationArray{" +
                "samples=" + samples +
                ", data=" + data +
                '}';
    }
}

