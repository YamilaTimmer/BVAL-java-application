package nl.bioinf.model;

import nl.bioinf.processing.MethylationArraySampleComparer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.List;

public class DataIndexLocation {
    private static final Logger logger = LogManager.getLogger(DataIndexLocation.class.getName());
    private int chrIndex;
    private int geneIndex;
    private String header;

    public DataIndexLocation(String header) {
        this.header = header;
        chrIndex = findChrIndex();
        geneIndex = findGeneIndex();
    }

    private int findChrIndex() {
        List<String> listHeader = Arrays.asList(header.split(","));
        int index = listHeader.indexOf("chr");
        if (index == -1) {
            logger.error("'chr' not found in header, invalid header");
            throw new IllegalArgumentException();
        }
        return index;
    }

    private int findGeneIndex() {
        List<String> listHeader = Arrays.asList(header.split(","));
        int index = listHeader.indexOf("gene");
        if (index == -1) {
            logger.error("'gene' not found in header, invalid header");
            throw new IllegalArgumentException();
        }
        return index;
    }

    public int getChrIndex() {
        return chrIndex;
    }

    public int getGeneIndex() {
        return geneIndex;
    }
}
