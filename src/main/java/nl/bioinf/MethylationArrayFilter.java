package nl.bioinf;

public interface MethylationArrayFilter {
        /**
         * checks the given MethylationArray.
         */
        boolean pass(MethylationArray methylationArray);

}
