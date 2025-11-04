package nl.bioinf.comparing;

import jdk.jfr.Description;
import nl.bioinf.filtering.MethylationDataFilter;
import nl.bioinf.io.MethylationFileReader;
import nl.bioinf.model.MethylationArray;
import nl.bioinf.model.ComparisonResults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class MethylationArrayPosComparerTest {
    MethylationArray methylationDataCorrect;

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
    @Description("Tests performStatisticalMethods with correct data on chromosome")
    void testPerformStatisticalMethodsOnChrCorrect() {

        MethylationDataFilter.filterBySample(methylationDataCorrect, new String[] {"Sample2", "Sample3"});
        MethylationArrayPosComparer posComparer = new MethylationArrayPosComparer(methylationDataCorrect,
                new String[] {"welch-test"},
                MethylationDataFilter.PosFilterType.CHROMOSOME,
                new String[] {"17", "X"});

        ComparisonResults result = posComparer.performStatisticalMethods();
        assertNotNull(result);
        assertTrue(result.toString().contains("17,X"));
    }

    @Test
    @Description("Tests performStatisticalMethods with correct data on genes")
    void testPerformStatisticalMethodsOnGeneCorrect() {
        MethylationDataFilter.filterBySample(methylationDataCorrect, new String[] {"Sample2", "Sample3"});
        MethylationArrayPosComparer posComparer = new MethylationArrayPosComparer(methylationDataCorrect,
                new String[] {"welch-test"},
                MethylationDataFilter.PosFilterType.GENE,
                new String[] {"APC", "TP53"});

        ComparisonResults result = posComparer.performStatisticalMethods();
        assertNotNull(result);
        assertTrue(result.toString().contains("APC,TP53"));

    }

    @Test
    @Description("Tests performStatisticalMethods data contains 2 double arrays with uneven size, can only be tested with a welch-test")
    void testPerformStatisticalMethodsUnevenBetaValueSizesWrongStatisticTest() {
        MethylationDataFilter.filterBySample(methylationDataCorrect, new String[] {"Sample2", "Sample3"});
        MethylationArrayPosComparer posComparer = new MethylationArrayPosComparer(methylationDataCorrect,
                new String[] {"spearman"},
                MethylationDataFilter.PosFilterType.CHROMOSOME,
                new String[] {"17", "X"});

        assertThrows(IllegalArgumentException.class, posComparer::performStatisticalMethods);

    }

    @Test
    @Description("Tests performStatisticalMethods with incorrect input, these chromosomes are not present in the data")
    void testPerformStatisticalMethodsInvalidInput() {
        MethylationDataFilter.filterBySample(methylationDataCorrect, new String[] {"Sample2", "Sample3"});
        MethylationArrayPosComparer posComparer = new MethylationArrayPosComparer(methylationDataCorrect,
                new String[] {"spearman"},
                MethylationDataFilter.PosFilterType.CHROMOSOME,
                new String[] {"17t", "420"});
        assertThrows(IllegalArgumentException.class, posComparer::performStatisticalMethods);
    }

}