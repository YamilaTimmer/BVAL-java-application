package nl.bioinf.model;

import nl.bioinf.processing.MethylationArraySampleComparer;
import org.apache.commons.math3.stat.correlation.SpearmansCorrelation;
import org.apache.commons.math3.stat.inference.TTest;
import org.apache.commons.math3.stat.inference.WilcoxonSignedRankTest;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class StatisticalMethods {
    private final Map<String, BiFunction<double[], double[], Double>> statisticalMethods = new HashMap<>();

    public StatisticalMethods() {
        statisticalMethods.put("spearman", StatisticalMethods::runSpearman);
        statisticalMethods.put("t-test", StatisticalMethods::runTTest);
        statisticalMethods.put("wilcoxon-test", StatisticalMethods::runWilcoxonTest);
    }

    private static double runSpearman(double[] sample1, double[] sample2) {
        return new SpearmansCorrelation().correlation(sample1, sample2);
    }

    private static double runTTest(double[] sample1, double[] sample2) {
        return new TTest().pairedTTest(sample1, sample2);
    }

    private static double runWilcoxonTest(double[] sample1, double[] sample2) {
        return new WilcoxonSignedRankTest().wilcoxonSignedRank(sample1, sample2);
    }

    public Map<String, BiFunction<double[], double[], Double>> getStatisticalMethods() {
        return Collections.unmodifiableMap(statisticalMethods);
    }
}
