package nl.bioinf;
import picocli.CommandLine.*;
import picocli.CommandLine.Model.CommandSpec;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.*;

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

    @Parameters(index = "0", arity = "0..1", description = "Filter above or below cutoff: 'hypo' = below cutoff, 'hyper' = above cutoff")
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
        System.out.println("Data before filtering: " + methylationArray);
        System.out.println("");



        // Check before passing, otherwise NullPointerException
        if (samples != null){
            SampleFilter sampleFilter = new SampleFilter(samples);
            if (sampleFilter.pass(methylationArray) == true){
                methylationArray = DataFilter.filterSamples(samples);
            }

        }

        if (posArguments != null && posArguments.chr != null ) {
            ChrFilter chrFilter = new ChrFilter(posArguments.chr);

            if (chrFilter.pass(methylationArray) == true){
                methylationArray = DataFilter.filterByChr(posArguments.chr);
            }
        }

        // Check if posArguments is not null, because user does not have to give
        // these arguments (otherwise nullpointerexception)
//        if (posArguments != null) {
//            if (posArguments.chr != null) {
//                methylationArray = DataFilter.filterByChr(posArguments.chr);
//            } else if (posArguments.genes != null) {
//                methylationArray = DataFilter.filterByGene(posArguments.genes);
//            }
//
//        }
//        else{
//            System.out.println("\u001B[34mInfo: Use -chr [chromosome] to filter on chromosome(s) \u001B[0m");
//            System.out.println("\u001B[34mInfo: Use -g [gene] to filter on gene(s)\u001B[0m");
//        }


        if (cutoff <= 1 && cutoff >= 0.0){
            if (direction == null){
                direction = "hyper";
            }
            methylationArray = DataFilter.filterByCutOff(cutoff, direction);
        }
        else if (cutoff > 1.0 | cutoff < 0.0){
            System.out.println("\u001B[31mError: Please provide a cutoff value between 0.0 and 1.0 \u001B[0m");
        }
        else if(cutoff == 0.0 && direction != null){
            System.out.println("\u001B[34mInfo:Specify a cutoff value with -c\u001B[0m");
        }

        System.out.println("");
        System.out.println("Data after filtering: "+ methylationArray);

        try {
            FilterFileWriter.writeData(methylationData);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

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

//    @ArgGroup(exclusive = true, multiplicity = "1")
//    Filter.PosArguments posArguments;
//
//    @Option(names = {"-c", "--cutoff"},
//            description = "Cutoff value to filter betavalues on, by default the values higher than the cutoff are kept")
//    float cutoff;
    @Spec CommandSpec spec;
    @Option(names = {"-m", "--methods"},
            defaultValue = "t-test,spearman,wilcoxon-test",
            split = ",",
            description = "Name(s) of the different statistic methods, acceptable values: t-test spearman wilcoxon-test",
            arity = "0..*")
    String[] methods;

    private void validateMethodInput() {
        List<String> validMethods = new ArrayList<>();
        Collections.addAll(validMethods, new String[]{"t-test", "spearman", "wilcoxon-test"});

        for (String method : methods) {
            if (!validMethods.contains(method)) {
                throw new ParameterException(spec.commandLine(),
                        String.format("Invalid value '%s' for option '--method'", method));
            }
        }
    }


    @Override
    public void run() {
        validateMethodInput();
        try {
            FileReader.readCSV(filePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        MethylationArray data = FileReader.getData();
        SampleCompareDataClass corrData = MethylationArraySampleComparer.performStatisticalMethods(data, samples, methods);
        System.out.println(corrData);


    }

}
