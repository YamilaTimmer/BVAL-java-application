package nl.bioinf.processing;

import nl.bioinf.model.MethylationArray;

public interface MethylationArrayFilter {
        /**
         * checks the given MethylationArray.
         */
        boolean pass(MethylationArray methylationArray);

}
