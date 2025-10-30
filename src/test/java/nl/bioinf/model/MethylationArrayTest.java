package nl.bioinf.model;

import nl.bioinf.io.MethylationFileReader;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class MethylationArrayTest {
    @Test
    public void testMethylationArrayInitFaultyData() {
        ClassLoader classloader = getClass().getClassLoader();
        File file = new File(Objects.requireNonNull(classloader.getResource("faultyData.csv")).getFile());

        MethylationFileReader methylationFileReader = new MethylationFileReader();
        assertThrows(IllegalArgumentException.class, () -> {
            methylationFileReader.readCSV(Path.of(file.getPath()), 6);
        });
    }


    
    @Test
    public void testReturnCopyOfData(){
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
    public void testSamplesAndValuesNotSameSize() {
        ArrayList<String> samples = new ArrayList<>(Arrays.asList("sample1", "sample2", "sample3"));
        ArrayList<Double> values = new ArrayList<>(Arrays.asList(1.0, 0.5));
        MethylationArray tester = new MethylationArray();
        tester.setSamples(samples);
        assertThrows(IllegalArgumentException.class, () -> {
            tester.addData("cg00000029,TP53,17,7565097,7565097,+", values);
        });


    }

}