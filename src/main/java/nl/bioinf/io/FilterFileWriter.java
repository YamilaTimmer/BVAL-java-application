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
import java.util.Locale;

public class FilterFileWriter {
    private static final Logger logger = LogManager.getLogger();

    public static void writeFile(MethylationArray data, Path outputFilePath) throws IOException {
        File filePath = new File(outputFilePath.toUri());

        try (BufferedWriter newFile = new BufferedWriter(new FileWriter(filePath))) {
            String headerToWrite = data.getHeader();
            newFile.write(String.format("%s%n", headerToWrite));
            StringBuilder sb = writeData(data);

            newFile.write(sb.toString());

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

    private static StringBuilder writeData(MethylationArray data) {
        StringBuilder sb = new StringBuilder();

        for (MethylationData lineData : data.getData()) {
            sb.append(lineData.methylationLocation());

            for (Double val : lineData.betaValues()) {
                sb.append(',');
                if (val.isNaN()) {
                    sb.append(Double.NaN);
                } else {
                    // Changes commas in numbers to periods,
                    // to be able to distinguish from comma line seperator (csv)
                    sb.append(String.format(Locale.US, "%.2f", val));
                }
            }
            sb.append(System.lineSeparator());
        }
        return sb;
    }
}
