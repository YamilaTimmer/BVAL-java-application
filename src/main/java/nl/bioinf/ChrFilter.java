package nl.bioinf;

import java.util.ArrayList;

import static nl.bioinf.FileReader.methylationData;

public class ChrFilter implements MethylationArrayFilter {
    public static String[] filterChr;
    public static ArrayList<MethylationData> dataRows = methylationData.getData();

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
