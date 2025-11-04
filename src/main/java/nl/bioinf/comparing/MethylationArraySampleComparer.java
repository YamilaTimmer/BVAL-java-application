package nl.bioinf.comparing;

import nl.bioinf.model.MethylationArray;
import nl.bioinf.model.MethylationData;
import nl.bioinf.model.ComparisonResults;
import nl.bioinf.model.StatisticalMethods;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.DoubleStream;

/**
 * Compare class that will compare the beta values of samples via statistical tests
 */
public class MethylationArraySampleComparer {
    private static final Logger logger = LogManager.getLogger();

    // Key: name of statistical tests
    // Value: Bifunction that can be used to run the statistical test
    private final Map<String, BiFunction<double[], double[], Double>> statisticalMethods = new StatisticalMethods().getStatisticalMethods();
    private final MethylationArray data;
    private final String[] samples;
    private final String[] methods;
    ComparisonResults statisticalData;

    /**
     *
     * @param data    {@link MethylationArray}
     * @param samples Array of samples that will be compared to eachother
     * @param methods Statistical methods used to compare the beta values
     */
    public MethylationArraySampleComparer(MethylationArray data, String[] samples, String[] methods) {
        this.samples = samples;
        this.methods = methods;
        statisticalData = new ComparisonResults(methods);
        this.data = data;
    }

    /**
     * Performs statistical methods on the beta values ({@link MethylationArray}) it will do this for all samples
     * given in the constructor.
     *
     * @return {@link ComparisonResults} Model that will store the comparison results
     * @throws IllegalArgumentException Whenever samples are not found in the data
     */
    public ComparisonResults performStatisticalMethods() throws IllegalArgumentException {
        int invalidSample = -1;

        for (int i = 0; i < samples.length; i++) {
            for (int j = i + 1; j < samples.length; j++) {
                int sample1 = data.getSamples().indexOf(samples[i]);
                int sample2 = data.getSamples().indexOf(samples[j]);

                if (sample1 == invalidSample || sample2 == invalidSample) {
                    logger.error("Sample not found in the data, exiting code. Did not compare following " +
                            "samples: '{}' vs '{}'.", samples[i], samples[j]);
                    throw new IllegalArgumentException();
                }

                double[] sample1BetaValues = getBetaValues(sample1);
                double[] sample2BetaValues = getBetaValues(sample2);
                if (DoubleStream.of(sample1BetaValues).anyMatch(Double::isNaN) ||
                        DoubleStream.of(sample2BetaValues).anyMatch(Double::isNaN)) {
                    logger.warn("Found invalid value(s) (missing value/NaN) in 1 of the samples in the comparison: {} vs {}, " +
                                    "please compare samples without missing values. Continuing comparisons, " +
                                    "excluding {} vs {}. Run with -NA or --remove-na to remove all NA values.",
                            samples[i], samples[j], samples[i], samples[j]);
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

        logger.info("Successfully performed statistical-methods: {}, on samples {}.", methods, samples);
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
