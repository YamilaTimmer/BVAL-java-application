package nl.bioinf;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class MethylationFileReader {

    static List<String> data = new ArrayList<>(); // List because its resizable
    static String headerLine;
    static MethylationArray methylationData;

    public static void readCSV(Path filePath) throws IOException {

        try (BufferedReader br = Files.newBufferedReader(filePath)) {
            headerLine =  br.readLine();
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
            throw new NoSuchFileException("File not found: '" + filePath + "'");
        } catch (AccessDeniedException ex) {
            throw new AccessDeniedException("Permission denied: '" + filePath + "'");
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
