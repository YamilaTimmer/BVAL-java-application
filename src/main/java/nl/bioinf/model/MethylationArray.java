package nl.bioinf.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Datatype to hold methylation data containing beta values, also holds information on samples,
 * genes and chromosomes
 */
public class MethylationArray {
    private static final Logger logger = LogManager.getLogger();
    private List<String> samples = new ArrayList<>();
    private List<MethylationData> data = new ArrayList<>();
    private String header;
    private DataIndexLocation indexInformation = null;
    private int sampleIndex = 0;

    public List<String> getSamples() {
        return new ArrayList<>(samples);
    }

    public void setSamples(List<String> samples) {
        this.samples = samples;
    }

    /**
     * Returns the full header, including the samples, used for writing to output
     *
     * @return full header line
     */
    public String getHeader() {
        return header + String.format("%s", String.join(",", this.samples));
    }

    /**
     * Stores the file's header, without samples, in this class
     *
     * @param header string containing entire first line of input file
     */
    public void setHeader(String header) {
        String[] headerSplit = header.split(",");
        StringBuilder headerString = new StringBuilder();
        for (int i = 0; i < this.sampleIndex; i++) {
            headerString.append(headerSplit[i]).append(",");
        }
        this.header = headerString.toString();

    }

    public void addData(String methylationLocation, ArrayList<Double> betaValues) throws IllegalArgumentException {
        if (betaValues.size() != samples.size()) {
            logger.error("""
                    Unmatched argument lengths, n. of beta values: '{}', n. of samples '{}'.
                    """, betaValues.size(), samples.size());
            throw new IllegalArgumentException();
        } else {
            data.add(new MethylationData(methylationLocation, betaValues));
        }
    }

    public double[] getPosBetaValues(String posArg) {
        List<Double> betaValues = new ArrayList<>();

        for (MethylationData row : data) {
            if (row.methylationLocation().toUpperCase().contains(posArg.toUpperCase())) {
                betaValues.addAll(row.betaValues());
            }
        }
        return betaValues.stream().mapToDouble(Double::doubleValue).toArray();
    }

    public List<MethylationData> getData() {
        return new ArrayList<>(data);
    }

    public void setData(List<MethylationData> data) {
        this.data = data;
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

    public List<String> getChromosomes() {
        ArrayList<String> chromosomes = new ArrayList<>();
        for (MethylationData d : data) {
            chromosomes.add(d.getChromosome(indexInformation).toUpperCase());
        }
        return Collections.unmodifiableList(chromosomes);
    }

    public DataIndexLocation getIndexInformation() {
        return indexInformation;
    }

    public void setIndexInformation(DataIndexLocation indexInformation) {
        this.indexInformation = indexInformation;
    }

    public int getSampleIndex() {
        return this.sampleIndex;
    }

    public void setSampleIndex(int sampleIndex) {
        this.sampleIndex = sampleIndex;
    }

    @Override
    public String toString() {
        return "MethylationArray{" +
                "samples=" + samples +
                ", data=" + data +
                '}';
    }
}

