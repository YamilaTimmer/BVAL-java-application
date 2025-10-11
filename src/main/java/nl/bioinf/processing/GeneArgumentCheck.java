package nl.bioinf.processing;

import nl.bioinf.model.MethylationArray;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class GeneArgumentCheck implements UserArgumentsCheck {
    public static String[] filterGenes;
    public static List<String> genes;
    private static final Logger logger = LogManager.getLogger(GeneArgumentCheck.class.getName());



    public GeneArgumentCheck(String[] filterGenes, MethylationArray methylationArray) {
        GeneArgumentCheck.filterGenes = filterGenes;
        GeneArgumentCheck.genes = methylationArray.getGenes();
    }

    @Override
    public boolean pass() {

        for (String gene : filterGenes) {
            if (!genes.contains(gene)) {
                logger.error("""
                        The following gene is not present in the data: '{}'. Please only pass genes that are present in the input file."
                        """, gene);
                throw new IllegalArgumentException("\u001B[31mError: Given gene was not found in input file. \u001B[0m");
            }
        }

        return true;

    }
}
