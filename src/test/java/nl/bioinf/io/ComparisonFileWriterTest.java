package nl.bioinf.io;

import nl.bioinf.comparing.MethylationArraySampleComparer;
import nl.bioinf.model.ComparisonResults;
import nl.bioinf.model.MethylationArray;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class ComparisonFileWriterTest {
    @TempDir
    Path tempDir;

    MethylationArray data;
    ComparisonResults comparisonResults;

    @BeforeEach
    void setup() throws URISyntaxException, IOException {
        ClassLoader classloader = getClass().getClassLoader();
        Path filePath = Path.of(Objects.requireNonNull(
                classloader.getResource("correctData.csv")).toURI());

        MethylationFileReader methylationFileReader = new MethylationFileReader();
        methylationFileReader.readCSV(filePath, 6);
        data = methylationFileReader.getData();

        MethylationArraySampleComparer comparer = new MethylationArraySampleComparer(data,
                new String[] {"Sample2", "Sample3"},
                new String[] {"welch-test"});
        comparisonResults = comparer.performStatisticalMethods();
    }

    @Test
    @DisplayName("Tests the writing of filtered data with valid input")
    void testComparisonWriterValidInput() throws IOException {
        Path tempFile = tempDir.resolve("test-output.txt");

        new ComparisonFileWriter(comparisonResults, tempFile).writeData();

        assertTrue(Files.exists(tempFile));
    }

    @Test
    @DisplayName("Tests what happens when directory does not exist")
    void testComparisonWriterInvalidOutputDir() throws IOException {
        ComparisonFileWriter writer = new ComparisonFileWriter(comparisonResults, Path.of("/this/directory/does/not/exist.txt"));
        assertThrows(FileNotFoundException.class, writer::writeData);
    }

    @Test
    @DisplayName("Tests the file writer with directory that the user has no permission in.")
    void testComparisonWriterNoPermission() {
        ComparisonFileWriter writer = new ComparisonFileWriter(comparisonResults, Path.of("/bin/output.txt"));
        assertThrows(FileNotFoundException.class, writer::writeData);

    }

    @Test
    @DisplayName("Tests writer when CompareData is empty")
    void testComparisonWriterOnEmptyData() throws IOException {
        Path tempFile = tempDir.resolve("test-output-empty-results.txt");
        ComparisonResults results = new ComparisonResults(new String[] {"welch-test"});
        ComparisonFileWriter writer = new ComparisonFileWriter(results, tempFile);
        writer.writeData();
        assertTrue(Files.exists(tempFile));
    }
}