package nl.bioinf.io;

import nl.bioinf.model.SampleComparison;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ComparingFileWriter {
    SampleComparison data;

    public ComparingFileWriter(SampleComparison data) {
        this.data = data;
    }

    public void writeData() throws IOException {
        try (BufferedWriter newFile = new BufferedWriter(new FileWriter("output.txt"))) {
            newFile.write(createHeader());
            newFile.write(createCompareFileBody());
        }
    }

    private String createHeader() {
        StringBuilder header = new StringBuilder();
        header.append("SampleVSSample");
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
                newFileBody.append(","+statisticsResults.get(method).get(sampleIndex));
            }
            newFileBody.append(String.format("%n"));
            sampleIndex++;
        }

        return newFileBody.toString();
    }
}
