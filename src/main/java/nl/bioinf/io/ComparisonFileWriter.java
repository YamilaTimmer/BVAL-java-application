package nl.bioinf.io;

import nl.bioinf.model.SampleComparison;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class ComparisonFileWriter {

    private static final Logger logger = LogManager.getLogger();
    SampleComparison data;
    Path pathFileOutput;

    public ComparisonFileWriter(SampleComparison data, Path filePathOutput) {
        this.data = data;
        pathFileOutput = filePathOutput;
    }

    public void writeData() throws IOException {
        File filePath = new File(pathFileOutput.toUri());
        logger.info("Writing data to file: {}.", filePath);

        try (BufferedWriter newFile = new BufferedWriter(new FileWriter(filePath))) {

            newFile.write(createHeader());
            newFile.write(createCompareFileBody());

        } catch (IOException ex) {
            logger.error("""
                            Unexpected IO error when writing to file: '{}'.\s
                            Exception occurred: '{}'.
                            """,
                    ex.getMessage(), ex);
            throw ex;
        }
        logger.info("Output generated at: {}", filePath);

    }

    private String createHeader() {
        StringBuilder header = new StringBuilder();
        header.append("Variable,Variable");
        for (String method : data.getStatisticMethods()) {
            header.append(",").append(method);
        }
        header.append(String.format("%n"));

        return header.toString();
    }

    private String createCompareFileBody() {
        int sampleIndex = 0;
        Map<String, List<Double>> statisticsResults = data.getStatisticsData();
        StringBuilder newFileBody = new StringBuilder();
        for (String sample : data.getSampleVersusSampleNames()) {
            newFileBody.append(sample);
            for (String method : data.getStatisticMethods()) {
                newFileBody.append(",").append(statisticsResults.get(method).get(sampleIndex));
            }
            newFileBody.append(String.format("%n"));
            sampleIndex++;
        }

        return newFileBody.toString();
    }
}
