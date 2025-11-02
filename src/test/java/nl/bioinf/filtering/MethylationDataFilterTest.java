package nl.bioinf.filtering;

import jdk.jfr.Description;
import nl.bioinf.model.DataIndexLocation;
import nl.bioinf.model.MethylationArray;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
        methylationData.setHeader("id,gene,chr,fpos,tpos,strand,Sample1,Sample2,Sample3", 7);

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

        String headerLine = "id,gene,chr,fpos,tpos,strand,Sample1,Sample2,Sample3";
        methylationData.setHeader(headerLine, 7);

        DataIndexLocation indexLocation = new DataIndexLocation(headerLine);
        methylationData.setIndexInformation(indexLocation);

        MethylationDataFilter.filterByPos(methylationData, MethylationDataFilter.PosFilterType.GENE, genes);

        MethylationArray expectedMethylationArray = new MethylationArray();
        expectedMethylationArray.setSamples(new ArrayList<>(List.of("Sample1", "Sample2", "Sample3")));
        expectedMethylationArray.addData("cg00000029,TP53,17,7565097,7565097,+", new ArrayList<>(List.of(0.87, 0.85, 0.89)));
        expectedMethylationArray.addData("cg00000321,BRCA1,X,65678934,65678934,+", new ArrayList<>(List.of(0.0, 1.0, 0.45)));

        assertEquals(expectedMethylationArray.toString(), methylationData.toString());
    }

    @Test
    @Description("Tests filtering by chromosome")
    public void filterByChr() {

        String[] chr = {"17", "X"};

        String headerLine = "id,gene,chr,fpos,tpos,strand,Sample1,Sample2,Sample3";
        methylationData.setHeader(headerLine, 7);

        DataIndexLocation indexLocation = new DataIndexLocation(headerLine);
        methylationData.setIndexInformation(indexLocation);

        MethylationDataFilter.filterByPos(methylationData, MethylationDataFilter.PosFilterType.CHROMOSOME, chr);

        MethylationArray expectedMethylationArray = new MethylationArray();
        expectedMethylationArray.setSamples(new ArrayList<>(List.of("Sample1", "Sample2", "Sample3")));
        expectedMethylationArray.addData("cg00000029,TP53,17,7565097,7565097,+", new ArrayList<>(List.of(0.87, 0.85, 0.89)));
        expectedMethylationArray.addData("cg00000321,BRCA1,X,65678934,65678934,+", new ArrayList<>(List.of(0.0, 1.0, 0.45)));

        assertEquals(expectedMethylationArray.toString(), methylationData.toString());
    }

    @Test
    @Description("Tests filtering by cutoff, with directional argument 'upper'")
    void filterByCutOffHyper() {

        float cutoff = 0.7f;

        MethylationDataFilter.filterByCutOff(methylationData, cutoff, MethylationDataFilter.CutoffType.upper);

        MethylationArray expectedMethylationArray = new MethylationArray();

        expectedMethylationArray.setSamples(new ArrayList<>(List.of("Sample1", "Sample2", "Sample3")));
        expectedMethylationArray.addData("cg00000029,TP53,17,7565097,7565097,+", new ArrayList<>(List.of(0.87, 0.85, 0.89)));
        expectedMethylationArray.addData("cg00000236,CDH5,16,53468160,53468160,+", new ArrayList<>(List.of(Double.NaN, Double.NaN, Double.NaN)));
        expectedMethylationArray.addData("cg00000321,BRCA1,X,65678934,65678934,+", new ArrayList<>(List.of(Double.NaN, 1.0, Double.NaN)));

        assertEquals(expectedMethylationArray.toString(), methylationData.toString());
    }

    @Test
    @Description("Tests filtering by cutoff, with directional argument 'lower'")
    void filterByCutOffHypo() {

        float cutoff = 0.7f;

        MethylationDataFilter.filterByCutOff(methylationData, cutoff, MethylationDataFilter.CutoffType.lower);

        MethylationArray expectedMethylationArray = new MethylationArray();

        expectedMethylationArray.setSamples(new ArrayList<>(List.of("Sample1", "Sample2", "Sample3")));
        expectedMethylationArray.addData("cg00000029,TP53,17,7565097,7565097,+", new ArrayList<>(List.of(Double.NaN, Double.NaN, Double.NaN)));
        expectedMethylationArray.addData("cg00000236,CDH5,16,53468160,53468160,+", new ArrayList<>(List.of(0.1, 0.0, 0.4)));
        expectedMethylationArray.addData("cg00000321,BRCA1,X,65678934,65678934,+", new ArrayList<>(List.of(0.0, Double.NaN, 0.45)));

        assertEquals(expectedMethylationArray.toString(), methylationData.toString());
    }
}