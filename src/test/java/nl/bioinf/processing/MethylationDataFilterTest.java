package nl.bioinf.processing;

import jdk.jfr.Description;
import nl.bioinf.model.MethylationArray;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MethylationDataFilterTest {

    MethylationArray methylationData;


    @BeforeEach
    public void setup() {
        // Simulate input data, with samples and datarows (chr, gene, betavalue)
        methylationData = new MethylationArray();

        methylationData.setSamples(new ArrayList<>(List.of("Sample1", "Sample2", "Sample3")));
        methylationData.addData("cg00000029,TP53,17,7565097,7565097,+", new ArrayList<>(List.of(0.87, 0.85, 0.89)));
        methylationData.addData("cg00000236,CDH5,16,53468160,53468160,+", new ArrayList<>(List.of(0.1, 0.0, 0.4)));
        methylationData.addData("cg00000321,BRCA1,X,65678934,65678934,+", new ArrayList<>(List.of(0.0, 1.0, 0.45)));
    }


    @Test
    @Description("Tests filtering by sample")
    void filterBySample() {

        String[] samples = {"Sample1", "Sample2"};
        methylationData.setHeader("id,gene,chr,fpos,tpos,strand,Sample1,Sample2,Sample3");

        // Call method filterBySample
        MethylationDataFilter.filterBySample(methylationData, samples);

        // Make array for expected data
        MethylationArray expectedMethylationArray = new MethylationArray();
        expectedMethylationArray.setSamples(new ArrayList<>(List.of("Sample1", "Sample2")));
        expectedMethylationArray.addData("cg00000029,TP53,17,7565097,7565097,+", new ArrayList<>(List.of(0.87, 0.85)));
        expectedMethylationArray.addData("cg00000236,CDH5,16,53468160,53468160,+", new ArrayList<>(List.of(0.1, 0.0)));
        expectedMethylationArray.addData("cg00000321,BRCA1,X,65678934,65678934,+", new ArrayList<>(List.of(0.0, 1.0)));

        // Assert if expected equals actual result
        assertEquals(expectedMethylationArray.toString(), methylationData.toString());

    }

    @Test
    @Description("Tests filtering by gene")
    void filterByGene() {

        String[] genes = {"TP53", "BRCA1"};


        // Call method filterByGene
        MethylationDataFilter.filterByPos(methylationData, MethylationDataFilter.PosFilterType.GENE, genes);

        // Make array for expected data
        MethylationArray expectedMethylationArray = new MethylationArray();
        expectedMethylationArray.setSamples(new ArrayList<>(List.of("Sample1", "Sample2", "Sample3")));
        expectedMethylationArray.addData("cg00000029,TP53,17,7565097,7565097,+", new ArrayList<>(List.of(0.87, 0.85, 0.89)));
        expectedMethylationArray.addData("cg00000321,BRCA1,X,65678934,65678934,+", new ArrayList<>(List.of(0.0, 1.0, 0.45)));

        // Assert if expected equals actual result
        assertEquals(expectedMethylationArray.toString(), methylationData.toString());


    }

    @Test
    @Description("Tests filtering by chromosome")
    public void filterByChr() {

        String[] chr = {"17", "X"};

        // Call method filterByChr
        MethylationDataFilter.filterByPos(methylationData, MethylationDataFilter.PosFilterType.CHROMOSOME, chr);

        // Make array for expected data
        MethylationArray expectedMethylationArray = new MethylationArray();
        expectedMethylationArray.setSamples(new ArrayList<>(List.of("Sample1", "Sample2", "Sample3")));
        expectedMethylationArray.addData("cg00000029,TP53,17,7565097,7565097,+", new ArrayList<>(List.of(0.87, 0.85, 0.89)));
        expectedMethylationArray.addData("cg00000321,BRCA1,X,65678934,65678934,+", new ArrayList<>(List.of(0.0, 1.0, 0.45)));

        // Assert if expected equals actual result
        assertEquals(expectedMethylationArray.toString(), methylationData.toString());
    }
}


    // TODO: tests for cutoff values, cannot add data with missing beta values
//    @Test
//    @Description("Tests filtering by cutoff, with directional argument 'hyper'")
//    void filterByCutOffHyper() {
//
//        MethylationFileReader.methylationData = methylationData;
//
//        float cutoff = 0.7f;
//
//        MethylationDataFilter.filterByCutOff(methylationData , cutoff, "hyper");
//
//        MethylationArray expectedMethylationArray = new MethylationArray();

//        expectedMethylationArray.setSamples(new ArrayList<>(List.of("Sample1", "Sample2", "Sample3")));
//        expectedMethylationArray.addData("17", "TP53", new ArrayList<>(List.of(0.87, 0.85, 0.89)));
//        expectedMethylationArray.addData("16", "CDH5", new ArrayList<>(List.of()));
//        expectedMethylationArray.addData("X", "BRCA1", new ArrayList<>(List.of(1.0)));
//
//        assertEquals(expectedMethylationArray.toString(), methylationData.toString());

//}

//
//    @Test
//    @Description("Tests filtering by cutoff, with directional argument 'hypo'")
//    void filterByCutOffHypo() {
//
//        MethylationFileReader.methylationData = methylationData;
//
//        float cutoff = 0.7f;
//
//        MethylationDataFilter.filterByCutOff(methylationData , cutoff, "hypo");
//
//        MethylationArray expectedMethylationArray = new MethylationArray();
//
//        expectedMethylationArray.setSamples(new ArrayList<>(List.of("Sample1", "Sample2", "Sample3")));
//        expectedMethylationArray.addData("17", "TP53", new ArrayList<>(List.of()));
//        expectedMethylationArray.addData("16", "CDH5", new ArrayList<>(List.of(0.1, 0.0, 0.4)));
//        expectedMethylationArray.addData("X", "BRCA1", new ArrayList<>(List.of(0.0, 0.45)));
//
//        assertEquals(expectedMethylationArray.toString(), methylationData.toString());
    //}
//}