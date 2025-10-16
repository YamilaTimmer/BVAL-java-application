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

public class ComparingFileWriter {

    private static final Logger logger = LogManager.getLogger(ComparingFileWriter.class.getName());
    SampleComparison data;
    Path pathFileOutput;

    public ComparingFileWriter(SampleComparison data, Path filePathOutput) {
        this.data = data;
        pathFileOutput = filePathOutput;
    }

    public void writeData() throws IOException {
        File filePath = new File(pathFileOutput.toUri());

        try (BufferedWriter newFile = new BufferedWriter(new FileWriter(filePath))) {

            newFile.write(createHeader());
            newFile.write(createCompareFileBody());

        } catch (IOException ex) {
            logger.error("""
                            Unexpected IO error when writing to file: '{}'.\s
                            Exception occurred: '{}'.
                            """,
                    ex.getMessage(), ex);
            throw new IOException(ex);
        }
        logger.debug("Output successfully written to: {}.", filePath);
        System.out.println("Output successfully written to: " + filePath);

    }

    private String createHeader() {
        StringBuilder header = new StringBuilder();
        header.append("Sample1,Sample2");
        for (String method : data.getStatisticMethods()) {
            header.append("," + method);
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
                newFileBody.append("," + statisticsResults.get(method).get(sampleIndex));
            }
            newFileBody.append(String.format("%n"));
            sampleIndex++;
        }

        return newFileBody.toString();
    }
}
