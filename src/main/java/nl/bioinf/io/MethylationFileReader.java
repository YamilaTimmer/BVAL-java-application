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
    private static final Logger logger = LogManager.getLogger();
    private MethylationArray methylationData;

    public MethylationFileReader() {
        methylationData = new MethylationArray();

    }

    /**
     * This method tries to read the file at the user-provided file path and converts it to a MethylationArray datatype.
     *
     * @param filePath which refers to the path for the input file
     */
    public void readCSV(Path filePath, int sampleIndex) throws IOException {

        try (BufferedReader br = Files.newBufferedReader(filePath)) {
            String headerLine = br.readLine();

            if (headerLine == null || headerLine.isBlank()) {
                logger.error("""
                        Provided file: '{}' is empty, please provide a file with beta values, gene/chr regions and samples.
                        """, filePath);
                throw new IOException("File is empty: '" + filePath + "'"); //Error handling: Empty file
            }

            String line;
            methylationData = new MethylationArray();
            methylationData.setHeader(headerLine);
            methylationData.setSamples(getSamples(headerLine, sampleIndex));

            DataIndexLocation indexLocation = new DataIndexLocation(headerLine);
            methylationData.setIndexInformation(indexLocation);

            while ((line = br.readLine()) != null) {
                String[] lineSplit = line.split(",");
                ArrayList<Double> bValues;

                try {
                    bValues = getBValues(lineSplit, sampleIndex);
                } catch (NumberFormatException ex){
                    System.out.println(ex.getMessage());
                    throw ex;
                }

                try {
                    assert bValues != null;
                    methylationData.addData(buildMethylationLocation(lineSplit, sampleIndex), bValues);
                }catch(IllegalArgumentException ex){
                    System.out.println(ex.getMessage());
                    throw ex;
                }
            }

        } catch (NoSuchFileException ex) {
            logger.error("""
                            Failed to find the provided file: '{}'.\s
                            Exception occurred: '{}'.\s
                            """,
                    ex.getMessage(), ex);
            throw new IOException();

        } catch (IOException ex) {
            logger.error("Unexpected IO error for provided file: {}. Provided file path might be a directory " +
                    "or might not have proper permissions, file path: {}.",
                    ex.getMessage(), filePath);
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
        for (int i = sampleIndex; i < headerSplit.length; i++) {
            samples.add(headerSplit[i]);
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
                logger.error("Invalid beta value: '{}', please check if the correct sample index [-si] " +
                        "was passed.", lineSplit[i]);
                return null;
            }
        }
        return betaValues;
    }


    /**
     * @param lineSplit: contains the individual lines of the input file
     * @param sampleIndex: index of first sample column (int), passed by user
     * @return String containing all data of one row, excluding the beta values
     */
    private String buildMethylationLocation(String[] lineSplit, int sampleIndex) {
        StringBuilder methylationLocation = new StringBuilder();
        for (int i = 0; i < sampleIndex; i++) {
            methylationLocation.append(lineSplit[i]).append(",");
        }

        return methylationLocation.toString();
    }
}
