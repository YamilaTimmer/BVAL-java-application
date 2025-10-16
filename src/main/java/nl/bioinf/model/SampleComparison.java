package nl.bioinf.model;

import java.util.*;

/**
 * Model that holds the results of statistics tests
 */
public class SampleComparison {
    private final List<String> sampleVersusSampleNames = new ArrayList<>();
    private final Map<String, List<Double>> statisticsData = new HashMap<>();

    public SampleComparison(String[] methods) {
        for (String method : methods) {
            statisticsData.put(method, new ArrayList<>());
        }

    }

    /**
     *
     * @return all statistical methods that need to be run.
     */
    public Set<String> getStatisticMethods() {
        return Collections.unmodifiableSet(statisticsData.keySet());
    }

    /**
     * Add variable names that where compared to each-other.
     * @param sample string that contains the variable names that where compared.
     */
    public void addNewSampleVsSample(String sample) {
        sampleVersusSampleNames.add(sample);
    }

    /**
     * Gets all of the variables that were compared to each-other.
     * @return List of all the compared variables.
     */
    public List<String> getSampleVersusSampleNames() {
        return Collections.unmodifiableList(sampleVersusSampleNames);
    }

    /**
     * Adds a new result of statistical test.
     * @param method name of statistical test.
     * @param dataPoint double, resulting from said test.
     */
    public void addToData(String method, double dataPoint) {
        statisticsData.get(method).add(dataPoint);
    }

    /**
     * Gets the statistical data
     * @return Map that contains the methods as keys, and List of doubles as value;
     */
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
