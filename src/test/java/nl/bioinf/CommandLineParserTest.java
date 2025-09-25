package nl.bioinf;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

import static org.junit.jupiter.api.Assertions.*;

class CommandLineParserTest {
    @Test
    @DisplayName("Summary usecase test")
    // assert that if user uses summary command with no arguments, usage help + message is shown
    void testSummaryWithoutArguments() {


    }
    // assert that if user gives wrong arguments, msg is printed
    void testIllegalArgument(){

    }
    // assert that if user gives >1 file, msg is printed
    void testTooManyFilesForArgument(){
        Summary summary = new Summary();
        CommandLine cmd = new CommandLine(summary);

        // two arguments for --file
        int exitCode = cmd.execute("--file", "data1.csv", "data2.csv");

        assertEquals(2, exitCode); // invalid input
    }
    void testTooManyFileArguments() {
        Summary summary = new Summary();
        CommandLine cmd = new CommandLine(summary);

        // two arguments for --file
        int exitCode = cmd.execute("--file", "data1.csv", "--file", "data2.csv");

        assertEquals(2, exitCode); // invalid input
        //assertTrue("Too many arguments for option '--file'");
    }
}