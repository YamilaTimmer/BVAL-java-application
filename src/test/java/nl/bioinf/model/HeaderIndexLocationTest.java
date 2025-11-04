package nl.bioinf.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HeaderIndexLocationTest {

    @Test
    @DisplayName("Tests if IllegalArgumentError is thrown if header does not contain 'chr'")
    void testDataIndexLocationInvalidHeader() {
        assertThrows(IllegalArgumentException.class, () ->
                new HeaderIndexLocation("id,gene,sample1,sample2"));
    }

}