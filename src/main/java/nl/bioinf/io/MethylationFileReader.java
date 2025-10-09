package nl.bioinf.io;

import nl.bioinf.model.MethylationArray;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class MethylationFileReader {

    private static List<String> data = new ArrayList<>(); // List because its resizable
    private static String headerLine;
    private static MethylationArray methylationData;
    private static final Logger logger = LogManager.getLogger(MethylationFileReader.class.getName());

    public static void readCSV(Path filePath) throws IOException {

        try (BufferedReader br = Files.newBufferedReader(filePath)) {

            headerLine = br.readLine();

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
    }

    private static ArrayList<String> getSamples(String header) {

        ArrayList<String> samples = new ArrayList<>();
        String[] headerSplit = header.split(",");
        for (int i = 6; i < headerSplit.length; i++) {
            samples.add(headerSplit[i]);
        }

        return samples;
    }

    public static MethylationArray getData() {
        return methylationData;
    }

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