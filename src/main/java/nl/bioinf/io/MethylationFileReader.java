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

    public static void readCSV(Path filePath) {

        try (BufferedReader br = Files.newBufferedReader(filePath)) {
            headerLine = br.readLine();
            String line;
            methylationData = new MethylationArray();
            if (headerLine == null) {
                throw new IOException("File is empty: '" + filePath + "'"); //Error handling: Empty file
            }
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
                    Please check whether the correct file path was given.""",
                    ex.getMessage(), ex);
            System.exit(0);

        } catch (AccessDeniedException ex) {
            logger.error("""
                     Permission denied to open the provided file: '{}'.\s
                     Exception occurred: '{}'.\s
                     Please make sure the provided path is not a directory and that the file has appropriate permissions.""",
                    ex.getMessage(), ex);
            System.exit(0);

        } catch(IOException ex){
            logger.error("""
                    Unexpected IO error for provided file: '{}'.\s
                    Exception occurred: '{}'.\s
                    Please check the provided file path""",
                    ex.getMessage(), ex);
            System.exit(0);
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
