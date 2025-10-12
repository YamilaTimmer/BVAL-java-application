package nl.bioinf.processing;


public interface UserArgumentsCheck {
        /**
         * Checks the user passed arguments.
         */
        boolean pass() throws IllegalArgumentException;

}
