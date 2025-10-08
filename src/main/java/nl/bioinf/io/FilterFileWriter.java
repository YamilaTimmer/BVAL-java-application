package nl.bioinf.io;

import nl.bioinf.model.MethylationArray;
import nl.bioinf.model.MethylationData;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class FilterFileWriter{
    public static void writeData(MethylationArray data) throws IOException {
        try (BufferedWriter newFile = new BufferedWriter(new FileWriter("output.txt"))) {
            String headerToWrite = data.getHeader();
            newFile.write(String.format("%s%n", headerToWrite));
            for (MethylationData lineData : data.getData()) {
            newFile.write(lineData.toString());
            }
        }
    }
}
