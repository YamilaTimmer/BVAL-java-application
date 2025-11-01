package nl.bioinf.model;

import nl.bioinf.io.MethylationFileReader;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MethylationArrayTest {

    @Test
    void testMethylationArrayInitFaultyData() throws URISyntaxException {
        ClassLoader classloader = getClass().getClassLoader();
        Path filePath = Path.of(Objects.requireNonNull(
                classloader.getResource("faultyData.csv")).toURI());

        MethylationFileReader methylationFileReader = new MethylationFileReader();

        assertThrows(IllegalArgumentException.class, () ->
                methylationFileReader.readCSV(filePath, 6));
    }


    @Test
    void testReturnCopyOfData() {
        // Create MethylationArray
        ArrayList<String> samples = new ArrayList<>(Arrays.asList("sample1", "sample2", "sample3"));
        ArrayList<Double> values = new ArrayList<>(Arrays.asList(1.0, 0.5, 0.0));
        MethylationArray tester = new MethylationArray();
        tester.setSamples(samples);
        tester.addData("cg00000029,TP53,17,7565097,7565097,+", values);

        // Assert if it returns a copy, or pointer to data
        ArrayList<MethylationData> data = (ArrayList<MethylationData>) tester.getData();
        assertNotSame(tester.getData(), data);

    }

    @Test
    void testSamplesAndValuesNotSameSize() {
        ArrayList<String> samples = new ArrayList<>(Arrays.asList("sample1", "sample2", "sample3"));
        ArrayList<Double> values = new ArrayList<>(Arrays.asList(1.0, 0.5));
        MethylationArray tester = new MethylationArray();
        tester.setSamples(samples);
        assertThrows(IllegalArgumentException.class, () -> tester.addData("cg00000029,TP53,17,7565097," +
                "7565097,+", values));


    }

}