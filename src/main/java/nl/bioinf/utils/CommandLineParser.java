package nl.bioinf.utils;

import nl.bioinf.model.MethylationArray;
import nl.bioinf.model.SampleComparison;
import nl.bioinf.processing.*;
import nl.bioinf.io.ComparingFileWriter;
import nl.bioinf.io.FilterFileWriter;
import nl.bioinf.io.MethylationFileReader;
import picocli.CommandLine;
import picocli.CommandLine.*;
import picocli.CommandLine.Model.CommandSpec;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.*;

/**
 *
 */

// Reusable option for filepath
class FilePathInput {
    @Option(names = {"-f", "--file"},
            description = "Path to file containing the input methylation data file containing beta values, genes, " +
                    "chromosomes and one or more samples",
            arity = "1",
            required = true)
    Path filePath;
}

// Reusable option for sample
class SampleInput{
    @Option(names = {"-s", "--sample"},
            description = "Name(s) of the sample(s) to filter on",
            arity = "0..*")
    String[] samples;
}

class OutputPath{
    @Option(names = {"-o", "--output"},
            description = "Path where output should be generated. Default: ${DEFAULT-VALUE}",
            arity = "0..1")
    Path outputPath = Path.of("output.txt");
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
        CommandLine.usage(this, System.out);
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

        MethylationArray data = new MethylationArray();

        try {
            data = MethylationFileReader.readCSV(filePathInput.filePath);
        } catch (IOException ex) {
            // User-friendly output only
            System.out.println(ex.getMessage());
            System.exit(1);
        }

        SummaryGenerator.generateSummary(data);
    }
}

// Filter use-case, takes file, allows user to filter and returns overview to user
@Command(name = "filter",
        description = "@|bold Takes input file containing beta values and allows for filtering based on samples, " +
                "chromosomes or genes and a cutoff.|@",
        mixinStandardHelpOptions = true)
class Filter implements Runnable {

    @Mixin
    FilePathInput filePathInput;

    @Mixin
    SampleInput sampleInput;

    @Mixin
    OutputPath outputFilePath;

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
            description = "Cutoff value to filter beta values on [range = 0.0-1.0], by default the values higher than " +
                    "the cutoff are kept. Default: ${DEFAULT-VALUE}")
    float cutoff = 0.0f;

    @Option(names = {"-ct", "--cutofftype"},
            description = "Select whether to filter above or below cutoff. Default: ${DEFAULT-VALUE}. " +
                    "Valid values: ${COMPLETION-CANDIDATES}")
    MethylationDataFilter.CutoffType cutoffType = MethylationDataFilter.CutoffType.upper;


    @Override
    public void run() {

        MethylationArray data = new MethylationArray();

        try {
            data = MethylationFileReader.readCSV(filePathInput.filePath);
        } catch (IOException ex) {

            // User-friendly output only
            System.out.println(ex.getMessage());
            System.exit(1);
        }

        // Make new MethylationArray object to store filtered values in and set same samples
        MethylationArray filteredData = new MethylationArray();
        assert data != null;
        filteredData.setSamples(data.getSamples());
        filteredData.setData(data.getData());
        filteredData.setHeader(data.getHeader());

        if (sampleInput.samples != null){
            GeneArgumentCheck geneArgumentCheck = new GeneArgumentCheck(sampleInput.samples);
            if (geneArgumentCheck.pass()){
                MethylationDataFilter.filterBySample(filteredData, sampleInput.samples);
            }
        }

        if (posArguments != null && posArguments.chr != null ) {
            ChrArgumentCheck chrArgumentCheck = new ChrArgumentCheck(posArguments.chr);
            if (chrArgumentCheck.pass()){
                MethylationDataFilter.filterByChr(filteredData, posArguments.chr);
            }
        }
        else if (posArguments != null && posArguments.genes != null ) {
            GeneArgumentCheck geneArgumentCheck = new GeneArgumentCheck(posArguments.genes);
            if (geneArgumentCheck.pass()){
                MethylationDataFilter.filterByGene(filteredData, posArguments.genes);
            }
        }

        // Cutoff filter is always ran with a default of 0.0 and 'hyper' for direction
        CutOffArgumentCheck cutOffArgumentCheck = new CutOffArgumentCheck(cutoff, cutoffType);

        if (cutOffArgumentCheck.pass()){
            MethylationDataFilter.filterByCutOff(filteredData, cutoff, cutoffType);
        }

        FilterFileWriter.writeData(filteredData, outputFilePath.outputPath);
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

        MethylationArray data = new MethylationArray();

        try {
            data = MethylationFileReader.readCSV(filePathInput.filePath);
        } catch (IOException ex) {
            // User-friendly output only
            System.out.println(ex.getMessage());
            System.exit(1);
        }


        //MethylationArray data = MethylationArray.getData();
        SampleComparison corrData = MethylationArraySampleComparer.performStatisticalMethods(data, sampleInput.samples, methods);
        System.out.println(corrData);

        ComparingFileWriter.writeData(corrData);
    }
}