package nl.bioinf;
import picocli.CommandLine.*;

import java.io.IOException;
import java.nio.file.Path;


import static nl.bioinf.MethylationFileReader.methylationData;

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
            MethylationFileReader.readCSV(fileOptions.filePath);
        } catch (IOException e) {
            System.err.println("Error: Could not read file: '" + fileOptions.filePath + "'. ");
        }
        MethylationArray data = MethylationFileReader.getData();
        SummaryGenerator.summaryGenerator(data);
    }
}

// Filter use-case, takes file, allows user to filter and returns overview to user
@Command(name = "filter",
        description = "@|bold Takes methylation beta value file and allows for filtering based on samples/chromosomes/genes/cutoff.|@",
        mixinStandardHelpOptions = true)

class Filter implements Runnable {

    @Mixin
    FileOption fileOptions;

    @ArgGroup(exclusive = true, multiplicity = "0..1")
    PosArguments posArguments;

    static class PosArguments {
        @Option(names = {"-chr", "--chromosome"},
                description = "Positional argument to filter data on @|bold,underline one or more|@ chromosomes")
        String[] chr;
        @Option(names = {"-g", "--gene"},
                description = "Positional argument to filter data on @|bold,underline one or more|@ genes")
        String[] genes;
    }

    @Option(names = {"-s", "--sample"},
            description = "Name(s) of the sample(s) to filter on",
            arity = "0..*")
    String[] samples;


    @Option(names = {"-c", "--cutoff"},
            defaultValue = "0.0",
            description = "Cutoff value to filter beta values on [range = 0.0-1.0], by default the values higher than the cutoff are kept. Default: ${DEFAULT-VALUE}")
    float cutoff;

    @Parameters(index = "0",
            defaultValue = "hyper",
            arity = "0..1",
            description = "Filter above or below cutoff: 'hypo' = below cutoff, 'hyper' = above cutoff. Default: ${DEFAULT-VALUE}")
    String direction;


    @Override
    public void run() {

        try {
            MethylationFileReader.readCSV(fileOptions.filePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Make new MethylationArray object to store filtered values in and set same samples
        MethylationArray methylationArray = new MethylationArray();
        methylationArray.setSamples(methylationData.getSamples());

        System.out.println("Data before filtering: " + methylationData);

        // Build composite filter, which holds all filters
        CompositeMethylationArrayFilter compositeMethylationArrayFilter = new CompositeMethylationArrayFilter();
        if (samples != null){
            compositeMethylationArrayFilter.addFilter(new SampleFilterCheck(samples));
        }
        if (posArguments != null && posArguments.chr != null ) {
            compositeMethylationArrayFilter.addFilter(new ChrFilterCheck(posArguments.chr));
        }
        if (posArguments != null && posArguments.genes != null ) {
            compositeMethylationArrayFilter.addFilter(new GeneFilterCheck(posArguments.genes));
        }

        // Cutoff filter is always ran with a default of 0.0 and 'hyper' for direction
        compositeMethylationArrayFilter.addFilter(new CutOffFilterCheck(cutoff, direction));

        if (compositeMethylationArrayFilter.pass(methylationArray)){
            MethylationDataFilter.filterSamples(methylationArray, samples);
            MethylationDataFilter.filterByChr(methylationArray, posArguments.chr);
            MethylationDataFilter.filterByCutOff(methylationArray, cutoff, direction);

        }

        System.out.println("Data after filtering on cutoff: "+ methylationArray);

        // TODO: print tips for user?
//            System.out.println("\u001B[34mInfo: Use -chr [chromosome] to filter on chromosome(s) \u001B[0m");
//            System.out.println("\u001B[34mInfo: Use -g [gene] to filter on gene(s)\u001B[0m");

    }
}

// Compare use-case, compares 2 or more samples/regions
@Command(name = "compare",
        description = "Compare two or more samples/regions",
        mixinStandardHelpOptions = true)

class Compare implements Runnable {

//    @Mixin
//    FileOption fileOptions;
//
//    @Option(names = {"-s", "--sample"},
//            description = "Name(s) of the sample(s) to compare",
//            arity = "0..*")
//    String[] samples;
//
//    @ArgGroup(exclusive = true, multiplicity = "1")
//    Filter.PosArguments posArguments;
//
//    @Option(names = {"-c", "--cutoff"},
//            description = "Cutoff value to filter beta values on, by default the values higher than the cutoff are kept")
//    float cutoff;

    @Override
    public void run() {
        //To be implemented
    }

}
