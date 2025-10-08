package nl.bioinf.processing;

import nl.bioinf.model.MethylationArray;

public class GeneArgumentCheck implements UserArgumentsCheck {
    public static String[] filterGenes;

    public GeneArgumentCheck(String[] filterGenes) {
        GeneArgumentCheck.filterGenes = filterGenes;

    }

    @Override
    public boolean pass(MethylationArray methylationArray) {

        if (filterGenes.length > 30){
            System.err.println("Please pass a maximum of 30 genes.");
            return false;
        }

        return true;

    }
}
