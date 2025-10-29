package nl.bioinf.io;

import nl.bioinf.model.DataIndexLocation;
import nl.bioinf.model.MethylationArray;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;

/**
 * MethylationFileReader holds methods for reading the input file containing the beta values and converting it to a
 * MethylationArray datatype.
 */
public class MethylationFileReader {
    private static final Logger logger = LogManager.getLogger(MethylationFileReader.class.getName());
    private MethylationArray methylationData;
    private int sampleIndex;

    public MethylationFileReader() {
        methylationData = new MethylationArray();

    }

    public void setSampleIndex(int sampleIndex){
        this.sampleIndex = sampleIndex;
    }
    /**
     * This method tries to read the file at the user-provided file path and converts it to a MethylationArray datatype.
     *
     * @param filePath which refers to the path for the input file
     */
    public void readCSV(Path filePath) throws IOException {

        try (BufferedReader br = Files.newBufferedReader(filePath)) {
            String headerLine = br.readLine();

            if (headerLine == null || headerLine.isBlank()) {
                logger.error("""
                        Provided file: '{}' is empty, please provide a file with beta values, gene/chr regions and samples.
                        """, filePath);
                throw new IOException(); //Error handling: Empty file
            }

            String line;
            methylationData = new MethylationArray();
            methylationData.setSampleIndex(this.sampleIndex);
            methylationData.setHeader(headerLine);
            methylationData.setSamples(getSamples(headerLine, this.sampleIndex));

            DataIndexLocation indexLocation = new DataIndexLocation(headerLine);
            methylationData.setIndexInformation(indexLocation);

            while ((line = br.readLine()) != null) {
                String[] lineSplit = line.split(",");
                ArrayList<Double> bValues = null;

                try {
                    bValues = getBValues(lineSplit, this.sampleIndex);
                    methylationData.addData(buildMethylationLocation(lineSplit, this.sampleIndex), bValues);
                } catch (IllegalArgumentException ex){
                    logger.error(ex.getMessage());
                    return;
                }
            }

        } catch (NoSuchFileException ex) {
            logger.error("""
                            Failed to find the provided file: '{}'.\s
                            Exception occurred: '{}'.\s
                            """,
                    ex.getMessage(), ex);
            throw new IOException("File not found: '" + filePath + "'. Please check the file path.");

        } catch (AccessDeniedException ex) {
            logger.error("""
                            Permission denied to open the provided file: '{}'.\s
                            Exception occurred: '{}'.\s
                            """,
                    ex.getMessage(), ex);
            throw new IOException("Please make sure the provided path:" + filePath + " is not a directory and that the file has appropriate permissions.");

        } catch (IOException ex) {
            logger.error("Unexpected IO error for provided file: '{}'. Please check the provided file path.",
                    ex.getMessage());
            throw ex;
        }
    }

    /**
     * This method parses the header line of the input file to retrieve what samples are present in the file
     *
     * @param header: contains first line of the input file, which is the header
     * @param sampleIndex: index of first sample column (int), passed by user
     * @return samples: ArrayList with samples, represented as strings
     */
    private static ArrayList<String> getSamples(String header, int sampleIndex) {

        ArrayList<String> samples = new ArrayList<>();
        String[] headerSplit = header.split(",");
        try {
            for (int i = sampleIndex; i < headerSplit.length; i++) {
                samples.add(headerSplit[i]);
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            logger.error("Sample Index: '{}' not valid, please specify using -si", sampleIndex);
            throw new IllegalArgumentException();
        }
        return samples;
    }

    public MethylationArray getData() {
        return methylationData;
    }

    /**
     * This method parses beta values from the lines of the input file to the format needed to make a
     * MethylationArray object.
     *
     * @param lineSplit: contains the individual lines of the input file
     * @param sampleIndex: index of first sample column (int), passed by user
     * @return betaValues: Arraylist containing the beta values per line, containing one beta value per sample
     */
    private static ArrayList<Double> getBValues(String[] lineSplit, int sampleIndex) throws NumberFormatException {
        ArrayList<Double> betaValues = new ArrayList<>();
        for (int i = sampleIndex; i < lineSplit.length; i++) {
            if (lineSplit[i].equalsIgnoreCase("na")) {
                betaValues.add((double) -1);
                continue;
            }

            try {
                betaValues.add(Double.parseDouble(lineSplit[i]));
            } catch (NumberFormatException ex){
                String msg = String.format(
                        "Invalid beta value '%s' at index %d — check your sample index [-si].", lineSplit[i], i
                );
                throw new IllegalArgumentException(msg, ex);
            }
        }
        return betaValues;
    }


    /**
     * @param lineSplit: contains the individual lines of the input file
     * @param sampleIndex: index of first sample column (int), passed by user
     * @return
     */
    private String buildMethylationLocation(String[] lineSplit, int sampleIndex) {
        StringBuilder methylationLocation = new StringBuilder();
        for (int i = 0; i < sampleIndex; i++) {
            methylationLocation.append(lineSplit[i]).append(",");
        }
        return methylationLocation.toString();
    }
}
