package nl.bioinf.model;

import java.util.*;

public class SampleComparison {
    private List<String> sampleVersusSampleNames = new ArrayList<>();
    private Map<String, List<Double>> statisticsData = new HashMap<>();

    public SampleComparison(String[] methods) {
        for (String method : methods) {
            statisticsData.put(method, new ArrayList<>());
        }

    }

    public Set<String> getStatisticMethods() {return Collections.unmodifiableSet(statisticsData.keySet());}

    public void addNewSampleVsSample(String sample) {
        sampleVersusSampleNames.add(sample);
    }

    public List<String> getSampleVersusSampleNames() {
        return Collections.unmodifiableList(sampleVersusSampleNames);
    }

    public void addToData(String method, double dataPoint) {
        statisticsData.get(method).add(dataPoint);
    }

    public Map<String, List<Double>> getStatisticsData() {
        return Collections.unmodifiableMap(statisticsData);
    }

    @Override
    public String toString() {
        return "SampleCompareDataClass{" +
                "sampleVersusSampleNames=" + sampleVersusSampleNames +
                ", statisticsData=" + statisticsData +
                '}';
    }
}
