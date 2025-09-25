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
        subcommands = {Summary.class,
                Filter.class,
                Compare.class})

public class CommandLineParser implements Runnable {

    @Override
    public void run() {
    }

}
// Summary use-case, takes file and returns summary of file
@Command(name = "summary",
        description = "Takes file and provides short summary on e.g. amount of samples and avg. beta-values",
        mixinStandardHelpOptions = true)

class Summary implements Runnable {
    @Option(names = {"-f", "--file"},
            description = "Path to file containing the data",
            required = true,
            arity = "1") // Makes it so that 1 file is required
    Path filePath;


    @Override
    public void run() {

        try {
            FileReader.readCSV(filePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        MethylationArray data = FileReader.getData();
        SummaryGenerator.summaryGenerator(data);

    }
}


// Filter use-case, takes file, allows user to filter and returns overview to user
@Command(name = "filter",
        description = "Takes file and allows user to filter it and provides an overview afterwards",
        mixinStandardHelpOptions = true)

class Filter implements Runnable {

    // Filter options:
    @Option(names = {"-f", "--file"},
            description = "Path to file containing the data",
            arity = "1")
    Path filePath;

//    @Option(names = "-pos",
//            description = "Positional argument to filter data, choose from: Chr (chromosome), Gene")
//    String pos;

    @ArgGroup(exclusive = true, multiplicity = "1")
    Exclusive exclusive;

    static class Exclusive {
        @Option(names = "-chr", description = "Positional argument to filter data on one or more chromosomes") int[] chr;
        @Option(names = "-gene", description = "Positional argument to filter data on one or more genes") String[] genes;
    }

    @Option(names = {"-s", "--sample"},
            description = "Name(s) of the sample(s) to filter on")
    String[] samples;

//        @Option(names = {"--hyper"}, description = "")
//        float cutoff;

//        @Option(names = {"--hypo"}, description = "")
//        float cutoff;

    //TODO: cut-off toevoegen
    //--hyper alleen b-waarden > cutoff
    //--hypo alleen b-waarden < cutoff
    @Override
    public void run() {

        try {
            FileReader.readCSV(filePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        MethylationArray data = FileReader.getData();
        DataFilter.filterSamples(data, samples);
        DataFilter.filterPos(data, exclusive.chr, exclusive.genes);
    }
}

// Compare use-case, compares 2 or more samples/regions
@Command(name = "compare",
        description = "Compare two or more samples/regions",
        mixinStandardHelpOptions = true)

class Compare implements Runnable {
    @Option(names = {"-f", "--file"},
            description = "Path to file containing the data",
            arity = "1")
    Path filePath;

    @Option(names = "-pos", description = "Positional argument to filter data, choose from: Chr (chromosome), Gene, fpos (starting position), tpos (end position)")
    String pos;

    @Option(names = {"-s", "--sample"}, description = "Name(s) of the sample(s) to compare")
    String[] samples;

    //TODO: cut-off toevoegen
    @Override
    public void run() {
        System.out.println("Comparing sample(s) " + Arrays.toString(samples) + "on " + pos);
    }

}
