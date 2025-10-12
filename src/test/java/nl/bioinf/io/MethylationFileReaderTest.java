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

        // Assert that NoSuchFileException is thrown when trying to access non-existent path
        NoSuchFileException exception = assertThrows(NoSuchFileException.class,
                () -> MethylationFileReader.readCSV(path));

        // Check if expected message and actual message are equal
        String expectedMessage = "File not found: '" + path + "'";
        assertEquals(expectedMessage, exception.getMessage());

    }

    @Test
    @DisplayName("Test path where permission is denied")
    void noPermissionFile() throws IOException {

     // Create tempDir
     Path tempDir = Files.createTempDirectory("tempDir");

        // Assert that AccessDeniedException is thrown when trying to access a dir instead of a file
        AccessDeniedException exception = assertThrows(AccessDeniedException.class,
                () -> MethylationFileReader.readCSV(tempDir));

        // Check if expected message and actual message are equal
        String expectedMessage = "Permission denied: '" + tempDir + "'";
        assertEquals(expectedMessage, exception.getMessage());

    }

    @Test
    @DisplayName("Test error handling for empty methylation file")
    void emptyMethylationFile() throws IOException {

        // Create temp file that simulates real expected input, with header row and 1 data row
        Path tempFile = Files.createTempFile("exampledata", ".csv");
        Files.writeString(tempFile, "");

        // Assert that IOException is thrown with empty file
        IOException exception = assertThrows(IOException.class,
                () -> MethylationFileReader.readCSV(tempFile));
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

        MethylationFileReader.readCSV(tempFile);

        // Create MethylationArray that is expected based on input
        MethylationArray expectedMethylationArray = new MethylationArray();
        expectedMethylationArray.setSamples(new ArrayList<>(List.of("Sample1", "Sample2", "Sample3")));
        expectedMethylationArray.addData("cg00000029,TP53,17,7565097,7565097,+", new ArrayList<>(List.of(0.87, 0.85, 0.89)));

        // Retrieve actual MethylationArray
        MethylationArray actualMethylationArray = MethylationFileReader.getData();

        // Assert whether expected and actual MethylationArray are equal
        assertEquals(actualMethylationArray.toString(), expectedMethylationArray.toString());

    }

}
