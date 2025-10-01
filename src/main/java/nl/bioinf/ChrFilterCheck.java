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
                if (chrInt < 0 | chrInt > 23) {
                    return false;
                }
            }catch (NumberFormatException e){
                return false;
            }
        }
        return true;

    }
}
