package nl.bioinf.processing;


public interface UserArgumentsCheck {
    /**
     * Checks if user provided arguments pass validity checks.
     */
    boolean pass() throws IllegalArgumentException;
}
