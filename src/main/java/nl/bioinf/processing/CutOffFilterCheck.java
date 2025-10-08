package nl.bioinf.processing;

import nl.bioinf.model.MethylationArray;

public class CutOffFilterCheck implements MethylationArrayFilter {
    public static float cutoff;
    public static MethylationDataFilter.CutoffType cutoffType;


    public CutOffFilterCheck(float cutoff, MethylationDataFilter.CutoffType cutoffType) {
        CutOffFilterCheck.cutoff = cutoff;
        CutOffFilterCheck.cutoffType = cutoffType;

    }

    @Override
    public boolean pass(MethylationArray methylationArray) {

        // Cutoff too low/high
        if (cutoff > 1.0 | cutoff < 0.0) {
            System.out.println("\u001B[31mError: Please provide a cutoff value between 0.0 and 1.0 \u001B[0m");
            return false;
        }

        return true;
    }




}
