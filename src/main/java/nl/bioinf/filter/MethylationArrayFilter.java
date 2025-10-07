package nl.bioinf.filter;

import nl.bioinf.dataClasses.MethylationArray;

public interface MethylationArrayFilter {
        /**
         * checks the given MethylationArray.
         */
        boolean pass(MethylationArray methylationArray);

}
