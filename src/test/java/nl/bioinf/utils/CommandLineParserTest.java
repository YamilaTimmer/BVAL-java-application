package nl.bioinf.utils;

import jdk.jfr.Description;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CommandLineParserTest {

    final PrintStream originalOut = System.out;
    final PrintStream originalErr = System.err;
    final ByteArrayOutputStream out = new ByteArrayOutputStream();
    final ByteArrayOutputStream err = new ByteArrayOutputStream();


    @BeforeEach
    public void setUpStreams() {
        out.reset();
        err.reset();
        System.setOut(new PrintStream(out));
        System.setErr(new PrintStream(err));

    }

    @AfterEach
    public void restoreStreams() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }


    @Test
    @Description("Summary command without arguments prints usage")
    void testSummaryWithoutArguments() {
        Summary summary = new Summary();
        CommandLine cmd = new CommandLine(summary);

        int exitCode = cmd.execute(""); // no arguments

        // Picocli should return usage info, when arguments are missing
        assertEquals(CommandLine.ExitCode.USAGE, exitCode);
    }

    @Test
    @Description("Summary command with expected parameters prints summary output")
    void testSummaryWithArguments() throws IOException {
        Summary summary = new Summary();
        CommandLine cmd = new CommandLine(summary);

        // Create temp file that simulates real expected input, with header row and 1 data row
        Path tempFile = Files.createTempFile("exampledata", ".csv");
        Files.writeString(tempFile, "id,gene,chr,fpos,tpos,strand,Sample1,Sample2,Sample3" + System.lineSeparator() +
                "cg00000029,TP53,17,7565097,7565097,+,0.87,0.85,0.89");

        int exitCode = cmd.execute("-f", tempFile.toString(), "-si", "7");
        String output = out.toString();

        assertEquals(CommandLine.ExitCode.OK, exitCode); // ExitCode should be OK (0)

        String expectedOutput =
                """
                Summary for input file:
                Number of samples: 3
                Number of genes: 1
                Avg beta value: 0.87
                Amount of NA values: 0
                """.replace("\n", System.lineSeparator());

        assertEquals(expectedOutput, output);

    }

    @Test
    @Description("Usage info should be printed when user passes undefined arguments")
    void testIllegalArgument() {
        CommandLineParser commandLineParser = new CommandLineParser();
        CommandLine cmd = new CommandLine(commandLineParser);

        int exitCode = cmd.execute("test 12");
        assertEquals(CommandLine.ExitCode.USAGE, exitCode); // Check if Picocli returns usage info

    }

    @Test
    @Description("Usage help/warning should be printed if user passes more than the max amount of files (1)")
    void testTooManyFiles() {
        Summary summary = new Summary();
        CommandLine cmd = new CommandLine(summary);

        // Pass Two arguments for --file
        int exitCode = cmd.execute("--file data1.csv data2.csv");

        assertEquals(CommandLine.ExitCode.USAGE, exitCode); // Check if Picocli returns usage info
    }

    @Test
    @Description("Usage help/warning should be printed if user passes both -chr and -g options (they are mutually exclusive)")
    void bothChromosomeAndGeneArePassedFilter() {

        Filter filter = new Filter();
        CommandLine cmd = new CommandLine(filter);

        // Pass Two arguments for --file
        int exitCode = cmd.execute("--file data1.csv -chr 10 -gene TP53");

        assertEquals(CommandLine.ExitCode.USAGE, exitCode); // Check if Picocli returns usage info
    }

    @Test
    @Description("Usage help/warning should be printed if user passes both hypo and hyper (they are mutually exclusive)")
    void bothDirectionArgsArePassedFilter() {

        Filter filter = new Filter();
        CommandLine cmd = new CommandLine(filter);

        // Pass Two arguments for --file
        int exitCode = cmd.execute("--file data1.csv -chr 10 hypo hyper");

        assertEquals(CommandLine.ExitCode.USAGE, exitCode); // Check if Picocli returns usage info
    }

    @Test
    @Description("Usage help/warning should be printed if user passes both -chr and -g options (they are mutually exclusive)")
    void bothChromosomeAndGeneComparing() {
        Compare compare = new Compare();
        CommandLine cmd = new CommandLine(compare);

        // Pass Two arguments for --file
        int exitCode = cmd.execute("--file data1.csv -chr 10 -gene TP53");

        assertEquals(CommandLine.ExitCode.USAGE, exitCode); // Check if Picocli returns usage info
    }


}