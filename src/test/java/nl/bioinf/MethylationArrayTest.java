package nl.bioinf;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MethylationArrayTest {
    @Test
    public void testMethylationArrayInit() {
    ArrayList<String> samples = new ArrayList<>(Arrays.asList("sample1", "sample2", "sample3"));
    ArrayList<Double> values = new ArrayList<>(Arrays.asList(1.0, 0.5, 0.0));
    MethylationArray tester = new MethylationArray();
    tester.setSamples(samples);
    tester.addData("probe1", "tp53", values);
    
    }
    
    @Test
    public void testReturnCopyOfData(){
        // Create MethylationArray
        ArrayList<String> samples = new ArrayList<>(Arrays.asList("sample1", "sample2", "sample3"));
        ArrayList<Double> values = new ArrayList<>(Arrays.asList(1.0, 0.5, 0.0));
        MethylationArray tester = new MethylationArray();
        tester.setSamples(samples);
        tester.addData("probe1", "tp53", values);

        // Assert if it returns a copy, or pointer to data
        ArrayList<MethylationData> data = tester.getData();
        assertNotSame(tester.getData(), data);


    }

    @Test
    public void testSamplesAndValuesNotSameSize() {
        ArrayList<String> samples = new ArrayList<>(Arrays.asList("sample1", "sample2", "sample3"));
        ArrayList<Double> values = new ArrayList<>(Arrays.asList(1.0, 0.5));
        MethylationArray tester = new MethylationArray();
        tester.setSamples(samples);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            tester.addData("probe1", "tp53", values);
        });


    }

}