package nl.bioinf.processing;

import nl.bioinf.model.MethylationArray;
import nl.bioinf.model.MethylationData;
import nl.bioinf.model.SampleComparison;
import org.apache.commons.math3.stat.correlation.SpearmansCorrelation;
import org.apache.commons.math3.stat.inference.TTest;
import org.apache.commons.math3.stat.inference.WilcoxonSignedRankTest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.DoubleStream;

public class MethylationArraySampleComparer {
    private static final Logger logger = LogManager.getLogger(MethylationArraySampleComparer.class.getName());
    private final Map<String, BiFunction<double[], double[], Double>> statisticalMethods = new HashMap<>();
    private final MethylationArray data;

    public MethylationArraySampleComparer(MethylationArray data) {
        this.data = data;
        statisticalMethods.put("spearman", MethylationArraySampleComparer::runSpearman);
        statisticalMethods.put("t-test", MethylationArraySampleComparer::runTTest);
        statisticalMethods.put("wilcoxon-test", MethylationArraySampleComparer::runWilcoxonTest);
    }

    public SampleComparison performStatisticalMethods(String[] samples, String[] methods) throws IllegalArgumentException {
        logger.info("Performing statistical-methods: {}.",  Arrays.toString(methods));
        SampleComparison statisticalData = new SampleComparison(methods);
        for (int i = 0; i < samples.length; i++) {
            for (int j = i + 1; j < samples.length; j++) {
                int sample1 = data.getSamples().indexOf(samples[i]);
                int sample2 = data.getSamples().indexOf(samples[j]);
                try {
                    if (sample1 == -1 || sample2 == -1) {
                        logger.error("Sample not found in the data, exiting code. Did not compare following samples: {} vs {}\n", samples[i], samples[j]);
                        throw new IndexOutOfBoundsException();
                    }
                } catch (IndexOutOfBoundsException e) {
                    System.exit(-1);
                }

                double[] sample1BetaValues = getBetaValues(sample1);
                double[] sample2BetaValues = getBetaValues(sample2);
                if (DoubleStream.of(sample1BetaValues).anyMatch(x -> x == -1) ||
                        DoubleStream.of(sample2BetaValues).anyMatch(x -> x == -1)) {
                    logger.warn("Found invalid values in 1 of the samples: (-1 / NA) {} vs {}, please compare " +
                            "samples without -1 or NA values", samples[i], samples[j]);
                    continue;
                }
                String sampleNames = String.format("%s vs %s", samples[i], samples[j]);
                statisticalData.addNewSampleVsSample(sampleNames);

                for (String statisticalMethod : methods) {
                    BiFunction<double[], double[], Double> func = statisticalMethods.get(statisticalMethod);
                    double methodOutput = func.apply(sample1BetaValues, sample2BetaValues);
                    statisticalData.addToData(statisticalMethod, methodOutput);

                }
            }
        }

        logger.info("Successfully performed statistical-methods.");
        return statisticalData;
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

    private double[] getBetaValues(int sample) {
        double[] betaValues = new double[data.getData().size()];
        int index = 0;
        for (MethylationData lineData : data.getData()) {
            betaValues[index] = lineData.betaValues().get(sample);
            index++;
        }
        return betaValues;
    }
}
