package nl.bioinf.utils;
import nl.bioinf.dataClasses.MethylationArray;
import nl.bioinf.dataClasses.SampleCompareDataClass;
import nl.bioinf.filter.ChrFilterCheck;
import nl.bioinf.filter.CutOffFilterCheck;
import nl.bioinf.filter.GeneFilterCheck;
import nl.bioinf.filter.MethylationDataFilter;
import nl.bioinf.io.ComparingFileWriter;
import nl.bioinf.io.FilterFileWriter;
import nl.bioinf.io.MethylationFileReader;
import picocli.CommandLine.*;
import picocli.CommandLine.Model.CommandSpec;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.*;

// TODO: print tips for user?
//            System.out.println("\u001B[34mInfo: Use -chr [chromosome] to filter on chromosome(s) \u001B[0m");
//            System.out.println("\u001B[34mInfo: Use -g [gene] to filter on gene(s)\u001B[0m");


// Reusable option for filepath
class FilePathInput {
    @Option(names = {"-f", "--file"},
            description = "Path to file containing the data",
            arity = "1",
            required = true)
    Path filePath;
}

class SampleInput{
    @Option(names = {"-s", "--sample"},
            description = "Name(s) of the sample(s) to filter on",
            arity = "0..*")
    String[] samples;
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
    FilePathInput filePathInput;

    @Override
    public void run() {

        try {
            MethylationFileReader.readCSV(filePathInput.filePath);
        } catch (IOException e) {
            System.err.println("Error: Could not read file: '" + filePathInput.filePath + "'. ");
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
    FilePathInput filePathInput;

    @Mixin
    SampleInput sampleInput;

    @ArgGroup()
    PosArguments posArguments;

    static class PosArguments {
        @Option(names = {"-chr", "--chromosome"},
                description = "Positional argument to filter data on @|bold,underline one or more|@ chromosomes")
        String[] chr;
        @Option(names = {"-g", "--gene"},
                description = "Positional argument to filter data on @|bold,underline one or more|@ genes")
        String[] genes;
    }


    @Option(names = {"-c", "--cutoff"},
            defaultValue = "0.0",
            description = "Cutoff value to filter beta values on [range = 0.0-1.0], by default the values higher than the cutoff are kept. Default: ${DEFAULT-VALUE}")
    float cutoff;

    @Option(names = {"-ct", "--cutofftype"},
            defaultValue = "upper",
            description = "Select whether to filter above or below cutoff. Default: ${DEFAULT-VALUE}. Valid values: ${COMPLETION-CANDIDATES}")
    MethylationDataFilter.CutoffType cutoffType;


    @Override
    public void run() {
        MethylationArray methylationData;
        try {
            MethylationFileReader.readCSV(filePathInput.filePath);
            methylationData = MethylationFileReader.getData();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Make new MethylationArray object to store filtered values in and set same samples
        MethylationArray methylationArray = new MethylationArray();
        methylationArray.setSamples(methylationData.getSamples());
        methylationArray.setData(methylationData.getData());

        System.out.println("Data before filtering: " + methylationData);

        if (sampleInput.samples != null){
            GeneFilterCheck geneFilterCheck = new GeneFilterCheck(sampleInput.samples);
            if (geneFilterCheck.pass(methylationData)){
                methylationArray = MethylationDataFilter.filterBySample(methylationData, sampleInput.samples);
            }
        }

        if (posArguments != null && posArguments.chr != null ) {
            ChrFilterCheck chrFilterCheck = new ChrFilterCheck(posArguments.chr);
            if (chrFilterCheck.pass(methylationData)){
                MethylationDataFilter.filterByChr(methylationArray, posArguments.chr);
            }
        }
        else if (posArguments != null && posArguments.genes != null ) {
            GeneFilterCheck geneFilterCheck = new GeneFilterCheck(posArguments.genes);
            if (geneFilterCheck.pass(methylationData)){
                MethylationDataFilter.filterByGene(methylationArray, posArguments.genes);
            }
        }

        // Cutoff filter is always ran with a default of 0.0 and 'hyper' for direction
        CutOffFilterCheck cutOffFilterCheck = new CutOffFilterCheck(cutoff, cutoffType);
        if (cutOffFilterCheck.pass(methylationData)){
            MethylationDataFilter.filterByCutOff(methylationArray, cutoff, cutoffType);
        }

        System.out.println("Data after filtering on cutoff: "+ methylationArray);


        try {
            FilterFileWriter.writeData(methylationArray);
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
    @Mixin
    FilePathInput filePathInput;

    @Mixin
    SampleInput sampleInput;

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
        Collections.addAll(validMethods, "t-test", "spearman", "wilcoxon-test");

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
            MethylationFileReader.readCSV(filePathInput.filePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        MethylationArray data = MethylationFileReader.getData();
        SampleCompareDataClass corrData = MethylationArraySampleComparer.performStatisticalMethods(data, sampleInput.samples, methods);
        System.out.println(corrData);

        try {
            ComparingFileWriter.writeData(corrData);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

}