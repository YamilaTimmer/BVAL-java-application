package nl.bioinf.model;

import nl.bioinf.processing.MethylationArraySampleComparer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

/**
 * Model to hold index locations of different important values like:
 * chr, gene, etc
 * This will be used to adapt to different possible orders in the header
 */
public class DataIndexLocation {
    private static final Logger logger = LogManager.getLogger(DataIndexLocation.class.getName());
    private final Map<String, Integer> indexes;
    private final String header;

    public DataIndexLocation(String header) {
        this.header = header;
        indexes = new HashMap<>();
        findIndexes();
    }

    private void findIndexes() {
        String[] indexesToFind = new String[] {"chr", "gene"};
        List<String> listHeader = Arrays.asList(header.split(","));
        for (String indexToFind : indexesToFind) {
            int index = listHeader.indexOf(indexToFind);
            if (index == -1) {
                logger.error("'{}' not found in header, invalid header", indexToFind);
                System.exit(-1);
            }
            indexes.put(indexToFind, index);
        }
    }

    public int getChrIndex() {
        return indexes.get("chr");
    }

    public int getGeneIndex() {
        return indexes.get("gene");
    }

    @Override
    public String toString() {
        return "DataIndexLocation{" +
                "indexes=" + indexes +
                '}';
    }
}
