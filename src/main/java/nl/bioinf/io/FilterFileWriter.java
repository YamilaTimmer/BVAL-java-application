package nl.bioinf.io;

import nl.bioinf.model.MethylationArray;
import nl.bioinf.model.MethylationData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class FilterFileWriter{
    private static final Logger logger = LogManager.getLogger(FilterFileWriter.class.getName());

    public static void writeData(MethylationArray data) {
        try (BufferedWriter newFile = new BufferedWriter(new FileWriter("output.txt"))) {
            String headerToWrite = data.getHeader();
            newFile.write(String.format("%s%n", headerToWrite));
            for (MethylationData lineData : data.getData()) {
                newFile.write(lineData.toString());
            }
        }catch(IOException ex) {
            logger.error("""
                            Unexpected IO error when writing to file: '{}'.\s
                            Exception occurred: '{}'.
                            """,
                    ex.getMessage(), ex);
            System.exit(0);
        }
    }
}
