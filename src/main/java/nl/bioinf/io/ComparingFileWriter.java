package nl.bioinf.io;

import nl.bioinf.model.SampleComparison;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ComparingFileWriter {
    private static final Logger logger = LogManager.getLogger(ComparingFileWriter.class.getName());

    public static void writeData(SampleComparison data) {
        try (BufferedWriter newFile = new BufferedWriter(new FileWriter("output.txt"))) {
            newFile.write(createHeader(data));
            newFile.write(createCompareFileBody(data));
        } catch(IOException ex) {
            logger.error("""
                            Unexpected IO error when writing to file: '{}'.\s
                            Exception occurred: '{}'.
                            """,
                    ex.getMessage(), ex);
            System.exit(0);
        }
    }

    private static String createHeader(SampleComparison data) {
        StringBuilder header = new StringBuilder();
        header.append("SampleVSSample");
        for (String method : data.getStatisticMethods()) {
            header.append("," + method);
        }
        header.append(String.format("%n"));

        return header.toString();
    }

    private static String createCompareFileBody(SampleComparison data) {
        int sampleIndex = 0;
        Map<String, List<Double>> statisticsResults = data.getStatisticsData();
        StringBuilder newFileBody = new StringBuilder();
        for (String sample : data.getSampleVersusSampleNames()) {
            newFileBody.append(sample);
            for (String method : data.getStatisticMethods()) {
                newFileBody.append(","+statisticsResults.get(method).get(sampleIndex));
            }
            newFileBody.append(String.format("%n"));
            sampleIndex++;
        }

        return newFileBody.toString();
    }
}
