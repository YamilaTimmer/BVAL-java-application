package nl.bioinf.processing;

import nl.bioinf.model.MethylationArray;
import nl.bioinf.model.SampleComparison;
import nl.bioinf.model.StatisticalMethods;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.stream.DoubleStream;


/**
 * Compare class that will compare the beta values of either genes OR chromosomes via statistical tests.
 */
public class MethylationArrayPosComparer {
    private static final Logger logger = LogManager.getLogger(MethylationArrayPosComparer.class.getName());
    // Key: name of statistical tests
    // Value: Bifunction that can be used to run the statistical test
    private final StatisticalMethods statisticalMethods;
    private final MethylationArray data;
    private final String[] methods;
    private final String[] posArguments;
    MethylationDataFilter.PosFilterType posFilterType;
    SampleComparison statisticalData;

    /**
     *
     * @param data Methylation array.
     * @param methods Statistical methods acquired through CLI.
     * @param posFilterType Used to log weather chromosomes or genes are compared.
     * @param posArguments Array if either chromosomes or genes to compare
     */
    public MethylationArrayPosComparer(MethylationArray data, String[] methods,
                                       MethylationDataFilter.PosFilterType posFilterType, String[] posArguments) {
        this.data = data;
        this.methods = methods;
        statisticalData = new SampleComparison(methods);
        this.posFilterType = posFilterType;
        statisticalMethods = new StatisticalMethods();
        this.posArguments = posArguments;
    }

    /**
     * Compare the beta values of either genes OR chromosomes, based on user passed argument(s),
     * via different statistical tests
     * @return {@link SampleComparison}, that holds the data of the compared action.
     */
    public SampleComparison performStatisticalMethods() {
        logger.info("Starting comparing on {}", posFilterType.getName());
        for (int i = 0; i < posArguments.length-1; i++) {
            for (int j = i+1; j < posArguments.length; j++) {
                double[] betaValues1 = data.getPosBetaValues(posArguments[i]);
                double[] betaValues2 = data.getPosBetaValues(posArguments[j]);
                logger.debug("Sizes of beta values: {} = {}, {} = {}",
                        posArguments[i], betaValues1.length,
                        posArguments[j], betaValues2.length);
                validateValuesAndStatistics(betaValues2, betaValues1);
                String compareName = String.format("%s,%s", posArguments[i], posArguments[j]);
                statisticalData.addNewSampleVsSample(compareName);

                for (String method : methods) {
                    try {
                        BiFunction<double[], double[], Double> func = statisticalMethods.getStatisticalMethods().get(method);
                        double methodOutput = func.apply(betaValues1, betaValues2);
                        statisticalData.addToData(method, methodOutput);
                    } catch (NumberIsTooSmallException e) {

                        logger.error("Invalid {} found: {} or {}. Exiting application!",
                                posFilterType.getName(), posArguments[1], posArguments[j]);
                        System.exit(1);
                    }
                }
            }
        }
        logger.info("Succesfully compared the different {}s", posFilterType.getName());
        return statisticalData;
    }

    private void validateValuesAndStatistics(double[] betaValues2, double[] betaValues1) {
        try {

            if (betaValues2.length != betaValues1.length &&
                    !Arrays.stream(methods).allMatch(s -> s.equals("welch-test"))) {
                logger.error("Used a statistical method that requires the same sample size in data. " +
                        "Please use the [welch-test] to go around this");
                throw new IllegalArgumentException();

            }

            if (DoubleStream.of(betaValues1).anyMatch(x -> x == -1) ||
                    DoubleStream.of(betaValues2).anyMatch(x -> x == -1)) {
                logger.warn("Found invalid values in 1 of the samples: (-1 / NA), please compare " +
                        "samples without -1 or NA values");
                throw new IllegalArgumentException();
            }
        } catch (IllegalArgumentException ex) {
            System.exit(1);
        }
    }
}
