package nl.bioinf;
import picocli.CommandLine.*;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;


// TODO: replace reused options like file with mixins
// TODO: add colors for --help usage


// Parent class that will be called in main
@Command(name = "BVAL",
        version = "0.1",
        mixinStandardHelpOptions = true,
        subcommands = {CommandLineParser.Summary.class,
                       CommandLineParser.Filter.class,
                       CommandLineParser.Compare.class})

public class CommandLineParser implements Runnable{


    // Summary use-case, takes file and returns summary of file
    @Command(name = "summary",
            description = "Takes file and provides short summary on e.g. amount of samples and avg. beta-values",
            mixinStandardHelpOptions = true)

    static class Summary implements Runnable {

        @Option(names = {"-file", "--f"},
                description = "Path to file containing the data",
                arity = "1") // Makes it so that 1 file is required
        Path filePath;


        @Override
        public void run() {
            System.out.println("Generating Summary file...");

            try {
                FileReader.readCSV(filePath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            String[] samples = FileReader.getSamples();
            SummaryGenerator.summaryGenerator(samples);

        }
    }


    // Filter use-case, takes file, allows user to filter and returns overview to user
    @Command(name = "filter",
            description = "Takes file and allows user to filter it and provides an overview afterwards",
            mixinStandardHelpOptions = true)

    static class Filter implements Runnable {

        // Filter options:
        @Option(names = {"-file", "-f"},
                description = "Path to file containing the data",
                arity = "1")
        Path filePath;

        @Option(names = "-pos", description = "Positional argument to filter data, choose from: Chr (chromosome), Gene, fpos (starting position), tpos (end position)")
        String pos;

        @Option(names = {"-sample", "-s"}, description = "Name(s) of the sample(s) to filter on")
        String[] samples;

        @Override
        public void run() {
            System.out.println("Filtering sample(s) " + Arrays.toString(samples) + " on " + pos);
            DataFilter.filterPos(pos);

            try {
                FileReader.readCSV(filePath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    // Compare use-case, compares 2 or more samples/regions
    @Command(name = "compare",
            description = "Compare two or more samples/regions",
            mixinStandardHelpOptions = true)

    static class Compare implements Runnable {

        @Option(names = {"-file", "-f"},
                description = "Path to file containing the data",
                arity = "1")
        Path filePath;

        @Option(names = "-pos", description = "Positional argument to filter data, choose from: Chr (chromosome), Gene, fpos (starting position), tpos (end position)")
        String pos;

        @Option(names = {"-sample", "-s"}, description = "Name(s) of the sample(s) to compare")
        String[] samples;

        @Override
        public void run() {
            System.out.println("Comparing sample(s) " + Arrays.toString(samples) + "on " + pos);
        }

    }

    @Override
    public void run() {
    }

}
