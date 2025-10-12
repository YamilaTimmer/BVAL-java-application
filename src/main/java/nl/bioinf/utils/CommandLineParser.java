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

class FilePathOutput {
    @Option(names = {"-o", "--output"},
            description = "Path to where output will be written to, DEFAULT: ${DEFAULT-VALUE}",
            arity = "0..1")
    Path outputFilePath = Path.of("output.txt");
}

class SampleInput {
    @Option(names = {"-s", "--sample"},
            description = "Name(s) of the sample(s) to filter on",
            arity = "0..*")
    String[] samples;
}

class Verbosity {
    @Option(names = "--verbose",
            description = "Verbosity level. Default: ${DEFAULT-VALUE}",
            arity = "0..1")
    int verbose = 0;
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

    @Mixin
    Verbosity verbosity;

    @Override
    public void run() {
        VerbosityLevel verbosityLevel = new VerbosityLevel();
        try {
            verbosityLevel.applyVerbosity(verbosity.verbose);
        } catch (IllegalArgumentException ex) {
            System.out.println(ex.getMessage());
            System.exit(1);
        }

        MethylationFileReader fileReader = new MethylationFileReader();

        try {

            fileReader.readCSV(filePathInput.filePath);
        } catch (IOException ex) {
            // User-friendly output only
            System.out.println(ex.getMessage());
            System.exit(1);
        }

        MethylationArray data = fileReader.getData();

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
    Verbosity verbosity;

    @Mixin
    FilePathOutput filePathOutput;

    @ArgGroup()
    PosArguments posArguments;

    static class PosArguments {
        @Option(names = {"-chr", "--chromosome"},
                description = "Positional argument to filter data on @|bold,underline one or more|@ chromosomes",
                arity = "0..*")
        String[] chr;
        @Option(names = {"-g", "--gene"},
                description = "Positional argument to filter data on @|bold,underline one or more|@ genes",
                arity = "0..*")
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

        VerbosityLevel verbosityLevel = new VerbosityLevel();
        try {
            verbosityLevel.applyVerbosity(verbosity.verbose);
        } catch (IllegalArgumentException ex) {
            System.out.println(ex.getMessage());
            System.exit(1);

        }

        MethylationFileReader fileReader = null;

        try {
            fileReader = new MethylationFileReader();
            fileReader.readCSV(filePathInput.filePath);
        } catch (IOException ex) {

            System.out.println(ex.getMessage());
            System.exit(1);
        }

        MethylationArray data = fileReader.getData();

        // Make new MethylationArray object (copy of MethylationArray generated by MethylationFileReader),
        // to store filtered values
        MethylationArray filteredData = new MethylationArray();
        filteredData.setHeader(data.getHeader());
        filteredData.setSamples(data.getSamples());
        filteredData.setData(data.getData());

        // List to hold methods to be run based on user arguments
        List<Runnable> filtersToRun = new ArrayList<>();
        CompositeUserArgumentsCheck checker = new CompositeUserArgumentsCheck();

        try {

            if (sampleInput.samples != null) {
                SampleArgumentCheck sampleArgumentCheck = new SampleArgumentCheck(sampleInput.samples, data);
                checker.addFilter(sampleArgumentCheck);

                // Lambda for adding filter method to run after all checks are done
                filtersToRun.add(() -> MethylationDataFilter.filterBySample(filteredData, sampleInput.samples));

            }

            if (posArguments != null && posArguments.chr != null) {
                MethylationDataFilter.PosFilterType posFilterType = MethylationDataFilter.PosFilterType.CHROMOSOME;

                ChrArgumentCheck chrArgumentCheck = new ChrArgumentCheck(posArguments.chr);
                checker.addFilter(chrArgumentCheck);

                // Lambda for adding filter method to be run after all checks are done
                filtersToRun.add(() -> MethylationDataFilter.filterByPos(filteredData, posFilterType, posArguments.chr));

            } else if (posArguments != null && posArguments.genes != null) {
                MethylationDataFilter.PosFilterType posFilterType = MethylationDataFilter.PosFilterType.GENE;

                // Convert to uppercase, so that gene is still recognized if user passes it in lowercase
                String[] genes = Arrays.stream(posArguments.genes)
                        .map(String::toUpperCase)
                        .toArray(String[]::new);

                GeneArgumentCheck geneArgumentCheck = new GeneArgumentCheck(genes, data);
                checker.addFilter(geneArgumentCheck);

                // Lambda for adding filter method to be run after all checks are done
                filtersToRun.add(() -> MethylationDataFilter.filterByPos(filteredData, posFilterType, genes));

            }

            // Cutoff filter is always ran with a default of 0.0 and 'hyper' for direction
            CutOffArgumentCheck cutOffArgumentCheck = new CutOffArgumentCheck(cutoff);
            checker.addFilter(cutOffArgumentCheck);

            // Lambda for adding filter method to be run after all checks are done
            filtersToRun.add(() -> MethylationDataFilter.filterByCutOff(filteredData, cutoff, cutoffType));

            // Check all arguments using the ArgumentChecks, to see if passed arguments are valid
            if (checker.pass()) {
                // Run all filters for which the user has passed arguments
                for (Runnable filter : filtersToRun) {
                    filter.run();
                }
            }
        } catch (IllegalArgumentException ex) {
            System.out.println(ex.getMessage());
            System.exit(1);
        }

        // Try writing to output
        try {
            FilterFileWriter.writeData(filteredData, filePathOutput.outputFilePath);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            System.exit(1);

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

    @Mixin
    Verbosity verbosity;

    @Mixin
    FilePathOutput filePathOutput;

    @Spec
    CommandSpec spec;
    @Option(names = {"-m", "--methods"},
            defaultValue = "t-test,spearman,wilcoxon-test",
            split = ",",
            description = "Name(s) of the different statistic methods, default values: ${DEFAULT-VALUE}",
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

        VerbosityLevel verbosityLevel = new VerbosityLevel();

        try {
            verbosityLevel.applyVerbosity(verbosity.verbose);
        } catch (IllegalArgumentException ex) {
            System.out.println(ex.getMessage());
            System.exit(1);
        }

        validateMethodInput();
        MethylationFileReader fileReader = null;

        try {
            fileReader = new MethylationFileReader();
            fileReader.readCSV(filePathInput.filePath);
        } catch (IOException ex) {
            // User-friendly output only
            System.out.println(ex.getMessage());
            System.exit(1);
        }

        MethylationArray data = fileReader.getData();

        SampleComparison corrData = new MethylationArraySampleComparer(data).performStatisticalMethods(sampleInput.samples, methods);

        try {
            new ComparingFileWriter(corrData, filePathOutput.outputFilePath).writeData();
        } catch (IOException e) {
            throw new RuntimeException(e);

        }
    }
}