package nl.bioinf;

import jdk.jfr.Description;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MethylationDataFilterTest {

    MethylationArray methylationData;


    @BeforeEach
    public void setup() {
        // Simulate input data
        methylationData = new MethylationArray();

        methylationData.setSamples(new ArrayList<>(List.of("Sample1", "Sample2", "Sample3")));

        methylationData.addData("17", "TP53", new ArrayList<>(List.of(0.87, 0.85, 0.89)));
        methylationData.addData("16", "CDH5", new ArrayList<>(List.of(0.1, 0.0, 0.4)));
        methylationData.addData("X", "BRCA1", new ArrayList<>(List.of(0.0, 1.0, 0.45)));
    }


    @Test
    @Description("Tests filtering by sample")
    void filterBySample() {

        MethylationFileReader.methylationData = methylationData;
        String[] samples = {"Sample1", "Sample2"};

        MethylationDataFilter.filterBySample(methylationData, samples);

        MethylationArray expectedMethylationArray = new MethylationArray();

        expectedMethylationArray.setSamples(new ArrayList<>(List.of("Sample1", "Sample2")));
        expectedMethylationArray.addData("17", "TP53", new ArrayList<>(List.of(0.87, 0.85)));
        expectedMethylationArray.addData("16", "CDH5", new ArrayList<>(List.of(0.1, 0.0)));
        expectedMethylationArray.addData("X", "BRCA1", new ArrayList<>(List.of(0.0, 1.0)));

        assertEquals(expectedMethylationArray.toString(), methylationData.toString());

    }



    @Test
    @Description("Tests filtering by gene")
    void filterByGene() {

        MethylationFileReader.methylationData = methylationData;

        String[] genes = {"TP53", "BRCA1"};

        MethylationDataFilter.filterByGene(methylationData, genes);

        MethylationArray expectedMethylationArray = new MethylationArray();

        expectedMethylationArray.setSamples(new ArrayList<>(List.of("Sample1", "Sample2", "Sample3")));
        expectedMethylationArray.addData("17", "TP53", new ArrayList<>(List.of(0.87, 0.85, 0.89)));
        expectedMethylationArray.addData("X", "BRCA1", new ArrayList<>(List.of(0.0, 1.0, 0.45)));

        assertEquals(expectedMethylationArray.toString(), methylationData.toString());


    }

    @Test
    @Description("Tests filtering by chromosome")
    public void filterByChr() {

        MethylationFileReader.methylationData = methylationData;

        String[] chr = {"17", "X"};

        MethylationDataFilter.filterByChr(methylationData , chr);

        MethylationArray expectedMethylationArray = new MethylationArray();

        expectedMethylationArray.setSamples(new ArrayList<>(List.of("Sample1", "Sample2", "Sample3")));
        expectedMethylationArray.addData("17", "TP53", new ArrayList<>(List.of(0.87, 0.85, 0.89)));
        expectedMethylationArray.addData("X", "BRCA1", new ArrayList<>(List.of(0.0, 1.0, 0.45)));


        assertEquals(expectedMethylationArray.toString(), methylationData.toString());
    }

    @Test
    @Description("Tests filtering by cutoff, with directional argument 'hyper'")
    void filterByCutOffHyper() {

        MethylationFileReader.methylationData = methylationData;

        float cutoff = 0.7f;

        MethylationDataFilter.filterByCutOff(methylationData , cutoff, "hyper");

        MethylationArray expectedMethylationArray = new MethylationArray();

//        expectedMethylationArray.setSamples(new ArrayList<>(List.of("Sample1", "Sample2", "Sample3")));
//        expectedMethylationArray.addData("17", "TP53", new ArrayList<>(List.of(0.87, 0.85, 0.89)));
//        expectedMethylationArray.addData("16", "CDH5", new ArrayList<>(List.of()));
//        expectedMethylationArray.addData("X", "BRCA1", new ArrayList<>(List.of(1.0)));
//
//        assertEquals(expectedMethylationArray.toString(), methylationData.toString());

    }

    @Test
    @Description("Tests filtering by cutoff, with directional argument 'hypo'")
    void filterByCutOffHypo() {

        MethylationFileReader.methylationData = methylationData;

        float cutoff = 0.7f;

        MethylationDataFilter.filterByCutOff(methylationData , cutoff, "hypo");

//        MethylationArray expectedMethylationArray = new MethylationArray();
//
//        expectedMethylationArray.setSamples(new ArrayList<>(List.of("Sample1", "Sample2", "Sample3")));
//        expectedMethylationArray.addData("17", "TP53", new ArrayList<>(List.of()));
//        expectedMethylationArray.addData("16", "CDH5", new ArrayList<>(List.of(0.1, 0.0, 0.4)));
//        expectedMethylationArray.addData("X", "BRCA1", new ArrayList<>(List.of(0.0, 0.45)));

//        assertEquals(expectedMethylationArray.toString(), methylationData.toString());

    }
}