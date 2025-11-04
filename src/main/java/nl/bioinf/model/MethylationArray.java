package nl.bioinf.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

/**
 * Datatype to hold methylation data containing beta values, also holds information on samples,
 * genes and chromosomes
 */
public class MethylationArray {
    private static final Logger logger = LogManager.getLogger();
    private List<String> samples = new ArrayList<>();
    private List<MethylationData> data = new ArrayList<>();
    private String header;
    private HeaderIndexLocation indexInformation = null;
    private int sampleIndex;

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
        return this.header + String.format("%s", String.join(",", this.samples));
    }

    /**
     * Stores the file's header, without samples, in this class
     *
     * @param header string containing entire first line of input file
     */
    public void setHeader(String header, int sampleIndex) {
        String[] headerSplit = header.split(",");
        StringBuilder headerString = new StringBuilder();
        for (int i = 0; i < sampleIndex; i++) {
            headerString.append(headerSplit[i]).append(",");
        }
        this.header = headerString.toString();
    }


    /**
     * Adds the beta values location in the genome and beta values.
     *
     * @param methylationLocation String that contains the position in the genome of the beta values
     * @param betaValues          List of doubles that contain all the beta values of that specific location
     * @throws IllegalArgumentException whenever the size of the samples and beta values are not the same
     */
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

    /**
     * Gets the beta values that belong to a specific genomic region (chr / gene)
     *
     * @param posArg String that either contains a gene or a chromosome, used to extract beta values
     * @return array of doubles that contain the beta values
     */
    public double[] getPosBetaValues(String posArg) {
        List<Double> betaValues = new ArrayList<>();

        for (MethylationData row : data) {
            if (row.methylationLocation().toUpperCase().contains(posArg.toUpperCase())) {
                betaValues.addAll(row.betaValues());
            }
        }
        if (betaValues.isEmpty()) {
            logger.error("Variable not found in data: {}", posArg);
            throw new IllegalArgumentException();
        }
        return betaValues.stream().mapToDouble(Double::doubleValue).toArray();
    }

    /**
     * @return List containing {@link MethylationData}
     */
    public List<MethylationData> getData() {
        return new ArrayList<>(data);
    }

    /**
     * @param data List of {@link MethylationData}
     */
    public void setData(List<MethylationData> data) {
        this.data = data;
    }

    /**
     * Used for checking if user filter argument genes are present in the input data
     *
     * @return Set of all genes present in data
     */
    public Set<String> getGenes() {
        Set<String> genes = new HashSet<>();
        for (MethylationData d : data) {
            genes.add(d.getGene(indexInformation).toUpperCase());
        }
        return Collections.unmodifiableSet(genes);
    }

    /**
     * Gets all of the chromosomes found in the data
     *
     * @return Set of all chromosomes found in data
     */
    public Set<String> getChromosomes() {
        Set<String> chromosomes = new HashSet<>();
        for (MethylationData d : data) {
            chromosomes.add(d.getChromosome(indexInformation).toUpperCase());
        }
        return Collections.unmodifiableSet(chromosomes);
    }

    /**
     *
     * @return {@link HeaderIndexLocation} containing the index of chr and gene in the header
     */
    public HeaderIndexLocation getIndexInformation() {
        return indexInformation;
    }

    /**
     *
     * @param indexInformation {@link HeaderIndexLocation} containing the index of chr and gene in the header
     */
    public void setIndexInformation(HeaderIndexLocation indexInformation) {
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

