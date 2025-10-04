package nl.bioinf;

public class CutOffFilterCheck implements MethylationArrayFilter {
    public static float cutoff;
    public static String direction;


    public CutOffFilterCheck(float cutoff, String direction) {
        CutOffFilterCheck.cutoff = cutoff;
        CutOffFilterCheck.direction = direction;

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
