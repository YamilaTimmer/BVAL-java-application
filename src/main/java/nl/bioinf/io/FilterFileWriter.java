package nl.bioinf.io;

import nl.bioinf.model.MethylationArray;
import nl.bioinf.model.MethylationData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

public class FilterFileWriter {
    private static final Logger logger = LogManager.getLogger(FilterFileWriter.class.getName());

    public static void writeData(MethylationArray data, Path outputFilePath) throws IOException {
        File filePath = new File(outputFilePath.toUri());

        try (BufferedWriter newFile = new BufferedWriter(new FileWriter(filePath))) {
            String headerToWrite = data.getHeader();
            newFile.write(String.format("%s%n", headerToWrite));
            for (MethylationData lineData : data.getData()) {
                newFile.write(lineData.toString());
            }

            logger.debug("Output successfully written to: {}.", filePath);
            System.out.println("Output file generated at: '" + filePath + "'");


        } catch (IOException ex) {
            logger.error("""
                            Unexpected IO error when writing to file: '{}'.\s
                            Exception occurred: '{}'.
                            """,
                    ex.getMessage(), ex);
            throw ex;
        }
    }
}
