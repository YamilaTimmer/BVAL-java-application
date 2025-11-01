package nl.bioinf.model;

import org.apache.commons.math3.stat.correlation.SpearmansCorrelation;
import org.apache.commons.math3.stat.inference.TTest;
import org.apache.commons.math3.stat.inference.WilcoxonSignedRankTest;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * Model that holds the different statistic tests, and provides a way to run said tests
 */
public class StatisticalMethods {
    // Key: name of statistical tests
    // Value: Bifunction that can be used to run the statistical test
    private final Map<String, BiFunction<double[], double[], Double>> statisticalMethods = new HashMap<>();

    public StatisticalMethods() {
        statisticalMethods.put("spearman", RunStatisticalMethods.SPEARMAN::run);
        statisticalMethods.put("t-test", RunStatisticalMethods.TTEST::run);
        statisticalMethods.put("wilcoxon-test", RunStatisticalMethods.WILCOXONTEST::run);
        statisticalMethods.put("welch-test", RunStatisticalMethods.WELCH::run);

    }

    enum RunStatisticalMethods {
        TTEST {
            @Override
            public double run(double[] sample1, double[] sample2) {
                return new TTest().pairedTTest(sample1, sample2);
            }

        },
        SPEARMAN {
            @Override
            public double run(double[] sample1, double[] sample2) {
                return new SpearmansCorrelation().correlation(sample1, sample2);
            }
        },

        WELCH {
            @Override
            public double run(double[] sample1, double[] sample2) {
                return new TTest().tTest(sample1, sample2);
            }
        },

        WILCOXONTEST {
            @Override
            public double run(double[] sample1, double[] sample2) {
                return new WilcoxonSignedRankTest().wilcoxonSignedRank(sample1, sample2);
            }
        };


        public abstract double run(double[] sample1, double[] sample2);
    }

    /**
     *
     * @return
     */
    public Map<String, BiFunction<double[], double[], Double>> getStatisticalMethods() {
        return Collections.unmodifiableMap(statisticalMethods);
    }
}
