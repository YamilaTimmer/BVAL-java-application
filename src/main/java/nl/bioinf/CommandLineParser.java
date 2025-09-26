package nl.bioinf;
import picocli.CommandLine.*;

import java.io.IOException;
import java.nio.file.Path;

import static nl.bioinf.FileReader.methylationData;

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
            System.err.println("Error: Could not read file: '" + filePath + "'. ");
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
            arity = "1",
            required = true)
    Path filePath;

    @ArgGroup(exclusive = true, multiplicity = "0..1")
    PosArguments posArguments;

    static class PosArguments {
        @Option(names = "-chr", description = "Positional argument to filter data on one or more chromosomes") String[] chr;
        @Option(names = "-gene", description = "Positional argument to filter data on one or more genes") String[] genes;
    }

    @Option(names = {"-s", "--sample"},
            description = "Name(s) of the sample(s) to filter on",
            arity = "0..*")
    String[] samples;

    @Option(names = {"-c", "--cutoff"},
            description = "Cutoff value to filter betavalues on, by default the values higher than the cutoff are kept")
    float cutoff;

    @Override
    public void run() {

        try {
            FileReader.readCSV(filePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("");
        System.out.println("Data before filtering: " + methylationData);
        System.out.println("");

        if (samples != null) {
            DataFilter.filterSamples(samples);
        }

        // Check if posArguments is not null, because user does not have to give
        // these arguments (otherwise nullpointerexception)
        if (posArguments != null) {
            if (posArguments.chr != null) {
                DataFilter.filterByChr(posArguments.chr);
            } else if (posArguments.genes != null) {
                DataFilter.filterByGene(posArguments.genes);
            }

        }
        else{
            System.out.println("Use -chr [chromosome] to filter on chromosome(s)");
            System.out.println("Use -gene [gene] to filter on gene(s)");
        }

        if (cutoff != 0.0){
            DataFilter.filterByCutOff(cutoff);
        }

        System.out.println("");
        System.out.println("Data after filtering: " + methylationData);

    }
}

// Compare use-case, compares 2 or more samples/regions
@Command(name = "compare",
        description = "Compare two or more samples/regions",
        mixinStandardHelpOptions = true)

class Compare implements Runnable {
    @Option(names = {"-f", "--file"},
            description = "Path to file containing the data",
            arity = "1",
            required = true)
    Path filePath;

    @Option(names = {"-s", "--sample"},
            description = "Name(s) of the sample(s) to compare",
            arity = "0..*")
    String[] samples;

    @ArgGroup(exclusive = true, multiplicity = "1")
    Filter.PosArguments posArguments;

    @Option(names = {"-c", "--cutoff"},
            description = "Cutoff value to filter betavalues on, by default the values higher than the cutoff are kept")
    float cutoff;

    @Override
    public void run() {
        //To be implemented
    }

}
