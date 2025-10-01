package nl.bioinf;
import picocli.CommandLine.*;

import java.io.IOException;
import java.nio.file.Path;

import static nl.bioinf.FileReader.methylationData;

// classes for options that are reused in multiple subcommands
class FileOption {
    @Option(names = {"-f", "--file"},
            description = "Path to file containing the data",
            arity = "1",
            required = true)
    Path filePath;
}

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
        description = "Takes 1 file and provides short summary on e.g. amount of samples and avg. beta-values",
        mixinStandardHelpOptions = true)

class Summary implements Runnable {
    @Mixin
    FileOption fileOptions;


    @Override
    public void run() {

        try {
            FileReader.readCSV(fileOptions.filePath);
        } catch (IOException e) {
            System.err.println("Error: Could not read file: '" + fileOptions.filePath + "'. ");
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

    @Mixin
    FileOption fileOptions;

    @ArgGroup(exclusive = true, multiplicity = "0..1")
    PosArguments posArguments;

    static class PosArguments {
        @Option(names = {"-chr", "--chromosome"}, description = "Positional argument to filter data on one or more chromosomes") String[] chr;
        @Option(names = {"-g", "--gene"}, description = "Positional argument to filter data on one or more genes") String[] genes;
    }

    @Option(names = {"-s", "--sample"},
            description = "Name(s) of the sample(s) to filter on",
            arity = "0..*")
    String[] samples;

    @Option(names = {"-c", "--cutoff"},
            description = "Cutoff value [0.0-1.0] to filter betavalues on, by default the values higher than the cutoff are kept")
    float cutoff;

    @Parameters(index = "0", defaultValue = "hyper", arity = "0..1", description = "Filter above or below cutoff: 'hypo' = below cutoff, 'hyper' = above cutoff")
    String direction;


    @Override
    public void run() {

        try {
            FileReader.readCSV(fileOptions.filePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        MethylationArray methylationArray = new MethylationArray();

        System.out.println("");
        System.out.println("Data before filtering: " + methylationData);
        System.out.println("");


        // Check before passing, otherwise NullPointerException
        if (samples != null){
            SampleFilterCheck sampleFilter = new SampleFilterCheck(samples);
            if (sampleFilter.pass(methylationArray)){
                methylationArray = DataFilter.filterSamples(samples);
            }

        }

        if (posArguments != null && posArguments.chr != null ) {
            ChrFilterCheck chrFilterCheck = new ChrFilterCheck(posArguments.chr);

            if (chrFilterCheck.pass(methylationArray)){
                methylationArray = DataFilter.filterByChr(posArguments.chr);
            }
        }

        if (posArguments != null && posArguments.genes != null ) {
            GeneFilterCheck geneFilterCheck = new GeneFilterCheck(posArguments.chr);

            if (geneFilterCheck.pass(methylationArray)){
                methylationArray = DataFilter.filterByChr(posArguments.chr);
            }
        }

        CutOffFilterCheck cutOffFilterCheck = new CutOffFilterCheck(cutoff, direction);

        if (cutOffFilterCheck.pass(methylationArray)){
            methylationArray = DataFilter.filterByCutOff(cutoff, direction);
        }


        // TODO: print tips for user?
//            System.out.println("\u001B[34mInfo: Use -chr [chromosome] to filter on chromosome(s) \u001B[0m");
//            System.out.println("\u001B[34mInfo: Use -g [gene] to filter on gene(s)\u001B[0m");




        System.out.println("");
        System.out.println("Data after filtering: "+ methylationArray);

    }
}

// Compare use-case, compares 2 or more samples/regions
@Command(name = "compare",
        description = "Compare two or more samples/regions",
        mixinStandardHelpOptions = true)

class Compare implements Runnable {

    @Mixin
    FileOption fileOptions;

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
