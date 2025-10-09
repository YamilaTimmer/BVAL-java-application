package nl.bioinf.io;

import nl.bioinf.model.MethylationArray;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

/**
 * MethylationFileReader holds methods for reading the input file containing the betavalues and converting it to a
 * MethylationArray datatype.
 */
public class MethylationFileReader {

    private static final List<String> data = new ArrayList<>(); // List because its resizable
    private static final Logger logger = LogManager.getLogger(MethylationFileReader.class.getName());

    /**
     * This method tries to read the file at the user-provided file path and converts it to a MethylationArray datatype.
     *
     * @param filePath which refers to the path for the input file
     */
    public static MethylationArray readCSV(Path filePath) throws IOException {

        MethylationArray methylationData;
        try (BufferedReader br = Files.newBufferedReader(filePath)) {

            String headerLine = br.readLine();

            if (headerLine == null || headerLine.isBlank()) {
                logger.error("""
                 Provided file: '{}' is empty, please provide a file with beta values, gene/chr regions and samples.
                 """, filePath);
                throw new IOException ("File is empty: '" + filePath + "'"); //Error handling: Empty file
            }

                String line;
                methylationData = new MethylationArray();

                methylationData.setHeader(headerLine);
                methylationData.setSamples(getSamples(headerLine));

                while ((line = br.readLine()) != null) {
                    data.add(line);
                    String[] lineSplit = line.split(",");
                    ArrayList<Double> bValues = getBValues(lineSplit);
                    methylationData.addData(lineSplit[2], lineSplit[1], bValues);
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

        } catch(IOException ex){
            logger.error("Unexpected IO error for provided file: '{}'. Please check the provided file path.",
                    ex.getMessage());
            throw ex;
        }
        return methylationData;
    }

    /**
     * This method parses the header line of the input file to retrieve what samples are present in the file
     *
     * @param header: contains first line of the input file, which is the header
     * @return samples: ArrayList with samples, represented as strings
     */
    private static ArrayList<String> getSamples(String header) {

        ArrayList<String> samples = new ArrayList<>();
        String[] headerSplit = header.split(",");
        for (int i = 6; i < headerSplit.length; i++) {
            samples.add(headerSplit[i]);
        }

        return samples;
    }

    /**
     * This method parses beta values from the lines of the input file to the format needed to make a
     * MethylationArray object.
     *
     * @param lineSplit: contains the individual lines of the input file
     * @return betaValues: Arraylist containing the betavalues per line, containing one betavalue per sample
     */
    private static ArrayList<Double> getBValues(String[] lineSplit){
        ArrayList<Double> betaValues = new ArrayList<>();
        for (int i = 6; i < lineSplit.length; i++) {
            if (lineSplit[i].equalsIgnoreCase("na")) {
                betaValues.add((double) -1); continue;
            }

            betaValues.add(Double.parseDouble(lineSplit[i]));
        }
        return betaValues;
    }
}