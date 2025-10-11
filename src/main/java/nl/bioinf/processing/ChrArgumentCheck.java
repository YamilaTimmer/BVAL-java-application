package nl.bioinf.processing;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChrArgumentCheck implements UserArgumentsCheck {
    public static String[] filterChr;
    private static final Logger logger = LogManager.getLogger(ChrArgumentCheck.class.getName());


    public ChrArgumentCheck(String[] filterChr) {
        ChrArgumentCheck.filterChr = filterChr;

    }

    @Override
    public boolean pass() throws IllegalArgumentException{

        for (String chr : filterChr){
            if (chr.equalsIgnoreCase("X") || chr.equalsIgnoreCase("Y")){
                return true;
            }

            try {

                int chrInt = Integer.parseInt(chr);
                if (chrInt < 1 || chrInt > 23) {
                    logger.error("Invalid input: '{}', please provide a number between 1-23 or X/Y for " +
                            "chromosome input.", chr);
                    throw new IllegalArgumentException("Invalid chromosome: " + chr + ". Must be 1-23 or X/Y.");
                }
            }catch (NumberFormatException ex){
                logger.error("Invalid input: '{}', please provide a number between 1-23 or X/Y for " +
                        "chromosome input.", chr);
                System.err.println("Please provide chromosome(s) between 1-23 or X/Y. Input: '" + chr + "'");
                throw ex;
            }
        }
        return true;

    }

}
