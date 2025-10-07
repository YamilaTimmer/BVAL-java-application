package nl.bioinf.filter;

import nl.bioinf.dataClasses.MethylationArray;

public class GeneFilterCheck implements MethylationArrayFilter {
    public static String[] filterGenes;

    public GeneFilterCheck(String[] filterGenes) {
        GeneFilterCheck.filterGenes = filterGenes;

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
