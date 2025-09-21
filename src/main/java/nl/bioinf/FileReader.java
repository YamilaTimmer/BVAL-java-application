package nl.bioinf;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileReader {

    static List<String> data = new ArrayList<>(); // List because its resizable
    static String headerLine;

    public static void readCSV(Path filePath) throws IOException {

        try (BufferedReader br = Files.newBufferedReader(filePath)) {
            headerLine =  br.readLine();
            String line;

            if (headerLine == null) {
                System.err.println("File is empty"); //Error handling: Empty file
            }

            while ((line = br.readLine()) != null) {
                data.add(line);
                System.out.println("line = " + Arrays.toString(line.split(",")));
            }

        } catch (NoSuchFileException ex) {
            System.err.println("File not found '" + filePath + "'"); // Error handling: File not found
        } catch (AccessDeniedException ex) {
            System.err.println("Permission denied '" + filePath + "'"); // Error handling: no permission to read file
        } // catch for unreadable file?
    }

    public static String[] getSamples() {

        // get samples from csv, assuming standard input, where samples start at fifth column
        String[] headerSplit = headerLine.split(",");
        String[] samples = new String[(headerSplit.length)-5];

        for (int i = 5; i < headerSplit.length; i++) {

            samples[i-5] = headerSplit[i];
        }


        return samples;

    }

    public static float[] getBValues(){


        return null;
    }
}
