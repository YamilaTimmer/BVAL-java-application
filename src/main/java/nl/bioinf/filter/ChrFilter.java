package nl.bioinf.filter;

import nl.bioinf.dataClasses.MethylationArray;


public class ChrFilter implements MethylationArrayFilter {
    public static String[] filterChr;

    public ChrFilter(String[] filterChr) {
        ChrFilter.filterChr = filterChr;

    }

    @Override
    public boolean pass(MethylationArray methylationArray) {

        for (String chr : filterChr){
            if (chr.equals("X") || chr.equals("Y")){
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
