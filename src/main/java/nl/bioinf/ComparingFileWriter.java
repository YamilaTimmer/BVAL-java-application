package nl.bioinf;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ComparingFileWriter {
    public static void writeData(SampleCompareDataClass data) throws IOException {
        try (BufferedWriter newFile = new BufferedWriter(new FileWriter("output.txt"))) {
            newFile.write(createHeader(data));
            newFile.write(createCompareFileBody(data));
        }
    }

    private static String createHeader(SampleCompareDataClass data) {
        StringBuilder header = new StringBuilder();
        header.append("SampleVSSample");
        for (String method : data.getStatisticMethods()) {
            header.append("," + method);
        }
        header.append(String.format("%n"));

        return header.toString();

    }

    private static String createCompareFileBody(SampleCompareDataClass data) {
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
