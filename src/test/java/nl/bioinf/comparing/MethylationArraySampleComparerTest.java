package nl.bioinf.comparing;

import jdk.jfr.Description;
import nl.bioinf.io.MethylationFileReader;
import nl.bioinf.model.MethylationArray;
import nl.bioinf.model.SampleComparison;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class MethylationArraySampleComparerTest {
    MethylationArray methylationDataCorrect;
    ClassLoader classloader = getClass().getClassLoader();

    @BeforeEach
    public void setup() throws URISyntaxException, IOException {
        ClassLoader classloader = getClass().getClassLoader();
        Path filePath = Path.of(Objects.requireNonNull(
                classloader.getResource("correctData.csv")).toURI());

        MethylationFileReader methylationFileReader = new MethylationFileReader();
        methylationFileReader.readCSV(filePath, 6);
        methylationDataCorrect = methylationFileReader.getData();

    }

    @Test
    @Description("Tests performStatisticalMethods with correct data")
    void testPerformStatisticalMethodsCorrect() {
        MethylationArraySampleComparer sampleComparer = new MethylationArraySampleComparer(methylationDataCorrect,
                                                            new String[] {"Sample2", "Sample3"},
                                                            new String[] {"t-test"});

        SampleComparison result = sampleComparer.performStatisticalMethods();
        assertNotNull(result, "Resulting SampleComparison should not be null");
        assertTrue(result.toString().contains("Sample2,Sample3"), "toString should contain the samples that were compared");
    }

    @Test
    @Description("Tests performStatisticalMethods with invalid samples, should throw illegal argument exception")
    void testPerformStatisticalMethodsInvalidSamples() {
        MethylationArraySampleComparer sampleComparer = new MethylationArraySampleComparer(methylationDataCorrect,
                new String[] {"SampleX", "SampleY"},
                new String[] {"t-test"});

        assertThrows(IllegalArgumentException.class, sampleComparer::performStatisticalMethods);
    }

    @Test
    @Description("Tests performStatisticalMethods with data that contains NA values")
    void testPerformStatisticalMethodsNaValue() {
        MethylationArraySampleComparer sampleComparer = new MethylationArraySampleComparer(methylationDataCorrect,
                new String[] {"Sample1", "Sample2"},
                new String[] {"t-test"});
        SampleComparison result = sampleComparer.performStatisticalMethods();
        assertNotNull(result, "result should never be null");
        assertTrue(result.getSampleVersusSampleNames().isEmpty());
    }

    @Test
    @Description("Tests performStatisticalMethods with multiple samples and methods")
    void testPerformStatisticalMethodsMultipleSamplesAndMethods() {
        MethylationArraySampleComparer sampleComparer = new MethylationArraySampleComparer(methylationDataCorrect,
                new String[] {"Sample1", "Sample2", "Sample3"},
                new String[] {"t-test", "spearman"});

        SampleComparison result = sampleComparer.performStatisticalMethods();
        assertNotNull(result, "result should never be null");
        assertFalse(result.toString().contains("Sample1"), "Should never contain a result that contained a NA value");
        assertTrue(result.toString().contains("Sample2,Sample3"));
        assertTrue(result.getStatisticMethods().contains("t-test"));
        assertFalse(result.getStatisticMethods().contains("welch-test"));
    }
}