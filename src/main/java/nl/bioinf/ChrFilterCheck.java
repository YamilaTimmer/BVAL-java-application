package nl.bioinf;

public class ChrFilterCheck implements MethylationArrayFilter {
    public static String[] filterChr;

    public ChrFilterCheck(String[] filterChr) {
        ChrFilterCheck.filterChr = filterChr;

    }

    @Override
    public boolean pass(MethylationArray methylationArray) {

        for (String chr : filterChr){
            if (chr.equalsIgnoreCase("X") || chr.equalsIgnoreCase("Y")){
                continue;
            }

            try {
                int chrInt = Integer.parseInt(chr);
                if (chrInt < 1 | chrInt > 23) {
                    System.err.println("Please provide chromosome(s) between 1-23 or X/Y. Input: '" + chr + "'");

                    return false;
                }
            }catch (NumberFormatException e){
                System.err.println("Please provide chromosome(s) between 1-23 or X/Y. Input: '" + chr + "'");
                return false;
            }
        }
        return true;

    }

}
