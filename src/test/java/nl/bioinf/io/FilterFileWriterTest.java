package nl.bioinf.io;

import nl.bioinf.model.MethylationArray;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class FilterFileWriterTest {
    @TempDir
    Path tempDir;

    MethylationArray data;

    @BeforeEach
    void setup() throws URISyntaxException, IOException {
        ClassLoader classloader = getClass().getClassLoader();
        Path filePath = Path.of(Objects.requireNonNull(
                classloader.getResource("correctData.csv")).toURI());

        MethylationFileReader methylationFileReader = new MethylationFileReader();
        methylationFileReader.readCSV(filePath, 6);
        data = methylationFileReader.getData();
    }

    @Test
    @DisplayName("Tests the writing of filtered data with valid input")
    void testFilterWriterValidInput() throws IOException {
        Path tempFile = tempDir.resolve("test-output.txt");

        FilterFileWriter.writeFile(data, tempFile.toAbsolutePath());

        assertTrue(Files.exists(tempFile));
    }

    @Test
    @DisplayName("Tests what happens when directory does not exist")
    void testFilterWriterInvalidOutputDir() throws IOException {

        assertThrows(FileNotFoundException.class, () ->
                FilterFileWriter.writeFile(data, Path.of("/this/directory/does/not/exist.txt")));
    }

    @Test
    @DisplayName("Tests the file writer with directory that the user has no permission in.")
    void testFilterWriterNoPermission() {
        assertThrows(FileNotFoundException.class, () ->
                FilterFileWriter.writeFile(data, Path.of("/bin/output.txt")));

    }

}