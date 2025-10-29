package nl.bioinf.processing;

import nl.bioinf.model.MethylationArray;
import nl.bioinf.model.MethylationData;
import nl.bioinf.model.SampleComparison;
import nl.bioinf.model.StatisticalMethods;
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
    // Key: name of statistical tests
    // Value: Bifunction that can be used to run the statistical test
    private final Map<String, BiFunction<double[], double[], Double>> statisticalMethods = new StatisticalMethods().getStatisticalMethods();
    private final MethylationArray data;
    private final String[] samples;
    private final String[] methods;
    SampleComparison statisticalData;

    public MethylationArraySampleComparer(MethylationArray data, String[] samples, String[] methods) {
        this.samples = samples;
        this.methods = methods;
        statisticalData = new SampleComparison(methods);
        this.data = data;
    }


    public SampleComparison performStatisticalMethods() throws IllegalArgumentException {
        for (int i = 0; i < samples.length; i++) {
            for (int j = i + 1; j < samples.length; j++) {
                int sample1 = data.getSamples().indexOf(samples[i]);
                int sample2 = data.getSamples().indexOf(samples[j]);

                    if (sample1 == -1 || sample2 == -1) {
                        logger.error("Sample not found in the data, exiting code. Did not compare following samples: {} vs {}\n", samples[i], samples[j]);
                        throw new IllegalArgumentException();
                    }


                double[] sample1BetaValues = getBetaValues(sample1);
                double[] sample2BetaValues = getBetaValues(sample2);
                if (DoubleStream.of(sample1BetaValues).anyMatch(x -> x == -1) ||
                        DoubleStream.of(sample2BetaValues).anyMatch(x -> x == -1)) {
                    logger.warn("Found invalid values in 1 of the samples: (-1 / NA) {} vs {}, please compare " +
                            "samples without -1 or NA values", samples[i], samples[j]);
                    continue;
                }
                String sampleNames = String.format("%s,%s", samples[i], samples[j]);
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
