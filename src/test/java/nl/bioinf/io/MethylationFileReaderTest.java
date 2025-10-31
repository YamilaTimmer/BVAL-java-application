package nl.bioinf.io;

import nl.bioinf.model.MethylationArray;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MethylationFileReaderTest {

    @Test
    @DisplayName("Test incorrect file path")
    void incorrectFilePath() {

        // Make non-existing path
        String fileName = "9382jdn";
        Path path = Paths.get(fileName);

        MethylationFileReader methylationFileReader = new MethylationFileReader();
        // Assert that IOException is thrown when trying to access non-existent path
        assertThrows(IOException.class, () -> methylationFileReader.readCSV(path, 6));
    }

    @Test
    @DisplayName("Test path where permission is denied")
    void noPermissionFile() throws IOException {

        // Create tempDir
        Path tempDir = Files.createTempDirectory("tempDir");

        MethylationFileReader methylationFileReader = new MethylationFileReader();

        // Assert that IOException is thrown when trying to access a dir instead of a file
        assertThrows(IOException.class,
                () -> methylationFileReader.readCSV(tempDir, 6));
    }

    @Test
    @DisplayName("Test error handling for empty methylation file")
    void emptyMethylationFile() throws IOException {

        // Create temp file that simulates real expected input, with header row and 1 data row
        Path tempFile = Files.createTempFile("exampledata", ".csv");
        Files.writeString(tempFile, "");

        MethylationFileReader methylationFileReader = new MethylationFileReader();


        // Assert that IOException is thrown with empty file
        IOException exception = assertThrows(IOException.class,
                () -> methylationFileReader.readCSV(tempFile, 6));
        String expectedMessage = "File is empty: '" + tempFile + "'";

        // Check if expected message and actual message are equal
        assertEquals(expectedMessage, exception.getMessage());

    }

    @Test
    @DisplayName("Test creating the methylationArray")
    void creatingMethylationArrayFromFile() throws IOException {

        // Create temp file that simulates real expected input, with header row and 1 data row
        Path tempFile = Files.createTempFile("exampledata", ".csv");
        Files.writeString(tempFile, "id,gene,chr,fpos,tpos,strand,Sample1,Sample2,Sample3" + System.lineSeparator() +
                "cg00000029,TP53,17,7565097,7565097,+,0.87,0.85,0.89");


        // Create MethylationArray that is expected based on input
        MethylationArray expectedMethylationArray = new MethylationArray();
        expectedMethylationArray.setSamples(new ArrayList<>(List.of("Sample1", "Sample2", "Sample3")));

        expectedMethylationArray.addData("cg00000029,TP53,17,7565097,7565097,+,", new ArrayList<>(List.of(0.87, 0.85, 0.89)));

        MethylationFileReader methylationFileReader = new MethylationFileReader();

        methylationFileReader.readCSV(tempFile, 6);

        // Retrieve actual MethylationArray
        MethylationArray actualMethylationArray = methylationFileReader.getData();


        // Assert whether expected and actual MethylationArray are equal
        assertEquals(expectedMethylationArray.toString(), actualMethylationArray.toString());
    }
}