package nl.bioinf.io;

import nl.bioinf.model.MethylationArray;
import nl.bioinf.model.MethylationData;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

public class FilterFileWriter{
    public static void writeData(MethylationArray data, Path outputFilePath) throws IOException {
        try (BufferedWriter newFile = new BufferedWriter(new FileWriter(outputFilePath.toString()))) {
            String headerToWrite = data.getHeader();
            newFile.write(String.format("%s%n", headerToWrite));
            for (MethylationData lineData : data.getData()) {
            newFile.write(lineData.toString());
            }

        } catch (IOException e) {
            throw new IOException(e);
        }
    }
}
