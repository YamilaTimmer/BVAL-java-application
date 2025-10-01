package nl.bioinf;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FilterFileWriter{
    public static void writeData(MethylationArray data) throws IOException {
        try (BufferedWriter newFile = new BufferedWriter(new FileWriter("output.txt"))) {
            newFile.write(data.getHeader());
            for (MethylationData lineData : data.getData()) {
            newFile.write(lineData.toString());
            }
        }
    }
}
