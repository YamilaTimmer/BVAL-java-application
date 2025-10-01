package nl.bioinf;

import org.apache.commons.math3.stat.correlation.SpearmansCorrelation;
import org.apache.commons.math3.stat.inference.TTest;
import org.apache.commons.math3.stat.inference.WilcoxonSignedRankTest;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class MethylationArraySampleComparer {
    private static Map<String, BiConsumer<double[], double[]>> statisticalMethods = new HashMap<>();

    static {
        statisticalMethods.put("spearman", MethylationArraySampleComparer::runSpearman);
        statisticalMethods.put("t-test", MethylationArraySampleComparer::runTTest);
        statisticalMethods.put("wilcoxon-test", MethylationArraySampleComparer::runWilconsonTest);
    }

    public static SampleCompareDataClass performStatisticalMethods(MethylationArray data, String[] samples, String[] methods) {
        SampleCompareDataClass statisticalData = new SampleCompareDataClass(methods);
        for (int i = 0; i < samples.length; i++) {
            for (int j = i+1; j < samples.length; j++) {
                int sample1 = data.getSamples().indexOf(samples[i]);
                int sample2 = data.getSamples().indexOf(samples[j]);

                double[] sample1BetaValues = getBetaValues(data, sample1);
                double[] sample2BetaValues = getBetaValues(data, sample2);
                String sampleNames = String.format("%s vs %s", samples[i], samples[j]);
                statisticalData.addNewSampleVsSample(sampleNames);

                for (String statisticalMethod : methods) {

                    statisticalData.addToData(statisticalMethod, new SpearmansCorrelation().correlation(sample1BetaValues, sample2BetaValues));

                }


            }
        }
        return statisticalData;
    }

    private static double runSpearman(double[] sample1, double[] sample2) {
        return new SpearmansCorrelation().correlation(sample1, sample2);
    }
    private static double runTTest(double[] sample1, double[] sample2) {
        return new TTest().pairedTTest(sample1, sample2);
    }
    private static double runWilconsonTest(double[] sample1, double[] sample2) {
        return new WilcoxonSignedRankTest().wilcoxonSignedRank(sample1, sample2);
    }

    private static double[] getBetaValues(MethylationArray data, int sample) {
        double[] betaValues = new double[data.getData().size()];
        int index = 0;
        for (MethylationData lineData : data.getData()) {
            betaValues[index] = lineData.betaValues().get(sample);
            index++;
        }
        return betaValues;
    }
}
