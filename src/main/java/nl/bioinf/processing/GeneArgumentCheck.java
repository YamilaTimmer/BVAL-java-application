package nl.bioinf.processing;

import nl.bioinf.model.MethylationArray;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * Checks validity of gene argument(s) passed by user
 */
public class GeneArgumentCheck implements UserArgumentsCheck {
    private final String[] filterGenes;
    private final List<String> genes;
    private final Logger logger = LogManager.getLogger(GeneArgumentCheck.class.getName());

    /**
     * Sets passed gene argument(s) and all genes in the dataset as class variable
     *
     * @param filterGenes      String array user argument, that should contain one or more gene
     * @param methylationArray contains parsed data from input file, including present genes
     */
    public GeneArgumentCheck(String[] filterGenes, MethylationArray methylationArray) {
        this.filterGenes = filterGenes;
        this.genes = methylationArray.getGenes();
    }

    /**
     * Checks whether filter gene argument(s) are valid, meaning they exist in the input data
     *
     * @return boolean true, if the check passes
     * @throws IllegalArgumentException if the check fails (meaning (one of) the passed argument(s) does not exist in
     *                                  the input data
     */
    @Override
    public boolean pass() {
        logger.info("Starting validity check for gene filter...");

        // Throw error if one of the genes to filter on does not exist in the input data
        for (String gene : filterGenes) {
            logger.debug("Validity check for user provided gene '{}'", gene);

            if (!genes.contains(gene)) {
                logger.error("The following gene is not present in the data: '{}'. Please only pass genes that " +
                        "are present in the input file.", gene);
                throw new IllegalArgumentException("\u001B[31mError: Given gene was not found in input file. \u001B[0m");
            }
        }
        logger.info("Passed validity check for gene filter!");
        return true;
    }
}
