package nl.bioinf.utils;

import nl.bioinf.argumentvaliditycheck.*;
import nl.bioinf.comparing.MethylationArrayPosComparer;
import nl.bioinf.comparing.MethylationArraySampleComparer;
import nl.bioinf.filtering.MethylationDataFilter;
import nl.bioinf.io.ComparisonFileWriter;
import nl.bioinf.io.FilterFileWriter;
import nl.bioinf.io.MethylationFileReader;
import nl.bioinf.model.MethylationArray;
import nl.bioinf.model.ComparisonResults;
import nl.bioinf.summarizing.SummaryGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import picocli.CommandLine;
import picocli.CommandLine.*;
import picocli.CommandLine.Model.CommandSpec;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Reusable option for filepath
 */
class FilePathInput {
    @Option(names = {"-f", "--file"},
            description = "Path to file containing the input methylation data file containing beta values, genes, " +
                    "chromosomes and one or more samples",
            arity = "1",
            required = true)
    Path filePath;
}

/**
 * Reusable option for file output path
 */
class FilePathOutput {
    @Option(names = {"-o", "--output"},
            description = "Path to where output will be written to, DEFAULT: ${DEFAULT-VALUE}",
            arity = "1")
    Path outputFilePath = Path.of("BVAL-output.csv");
}

/**
 * Reusable option for sample input
 */
class SampleInput {
    @Option(names = {"-s", "--sample"},
            description = "Name(s) of the sample(s) to filter on",
            arity = "1..*")
    String[] samples;
}

/**
 * Reusable option for verbosity
 */
class Verbosity {
    @Option(names = {"-v", "--verbose"},
            description = "Verbosity level. Default: ${DEFAULT-VALUE}",
            arity = "1")
    int verbose = 0;
}

class SampleIndex {
    @Option(names = {"-si", "--sample-index"},
            description = "Starting index for sample locations, should be the number of the column containing the first sample.",
            arity = "1",
            required = true)
    int sampleIndex;
}

class NaRemover {
    @Option(names = {"-NA", "--remove-na"},
            description = "Whether to remove all data rows that contain one or more NA " +
                    "Default: ${DEFAULT-VALUE}. Valid values: [true/false]",
            arity = "1")
    boolean removeNa = false;
}

/**
 * Parent class that is called in main
 */
@Command(name = "BVAL",
        version = "Current version of BVAL: 0.0.1",
        mixinStandardHelpOptions = true,
        subcommands = {Summary.class,
                Filter.class,
                Compare.class})
public class CommandLineParser implements Runnable {

    static MethylationArray readFile(FilePathInput filePathInput, int sampleIndex) {
        MethylationFileReader fileReader;

        try {
            fileReader = new MethylationFileReader();
            fileReader.readCSV(filePathInput.filePath, sampleIndex);
        } catch (IOException | IllegalArgumentException ex) {
            return null;
        }

        return fileReader.getData();

    }

    /**
     * Run method of parent class, only runs when user passes no subcommand (summary/filter/compare)
     * and outputs usage help
     */
    @Override
    public void run() {

        CommandLine.usage(this, System.out);
    }
}

/**
 * Subcommand for generating summary, takes file and returns summary of file
 */
@Command(name = "summary",
        version = "Current version of BVAL: 0.0.1",
        description = "Takes 1 file and provides short summary on e.g. amount of samples and avg. beta-values",
        mixinStandardHelpOptions = true)
class Summary implements Runnable {
    private static final Logger logger = LogManager.getLogger();
    @Mixin
    FilePathInput filePathInput;
    @Mixin
    Verbosity verbosity;
    @Mixin
    SampleIndex sampleIndex;

    /**
     * Run method of summary subcommand, runs when user passes subcommand summary and outputs a summary to the terminal
     */
    @Override
    public void run() {

        VerbosityLevelProcessor verbosityLevelProcessor = new VerbosityLevelProcessor();

        try {
            verbosityLevelProcessor.applyVerbosity(verbosity.verbose);
        } catch (IllegalArgumentException ex) {
            return;
        }

        MethylationArray data;

        if (sampleIndex.sampleIndex > 1) {
            try {
                data = CommandLineParser.readFile(filePathInput, sampleIndex.sampleIndex - 1);
            } catch (Exception e) {
                return;
            }

            assert data != null;
            SummaryGenerator.generateSummary(data);
        } else {
            logger.error("Invalid sample index: '{}'. Please provide the number that marks the start of the sample " +
                    "columns, using -si [index].", sampleIndex.sampleIndex);
        }

    }
}

/**
 * Subcommand for filtering, takes file, allows user to filter and writes filtered file to output path
 */
@Command(name = "filter",
        description = "@|bold Takes input file containing beta values and allows for filtering based on samples, " +
                "chromosomes or genes and a cutoff.|@",
        version = "Current version of BVAL: 0.0.1",
        mixinStandardHelpOptions = true)
class Filter implements Runnable {

    private static final Logger logger = LogManager.getLogger();
    @Mixin
    FilePathInput filePathInput;
    @Mixin
    SampleIndex sampleIndex;
    @Mixin
    SampleInput sampleInput;
    @Mixin
    Verbosity verbosity;
    @Mixin
    FilePathOutput filePathOutput;
    @Mixin
    NaRemover naRemover;
    @ArgGroup()
    PosArguments posArguments;
    @Option(names = {"-c", "--cutoff"},
            description = "Cutoff value to filter beta values on [range = 0.0-1.0], by default the values higher than " +
                    "the cutoff are kept. Default: ${DEFAULT-VALUE}")
    float cutoff = 0.0f;

    @Option(names = {"-ct", "--cutofftype"},
            description = "Select whether to filter above or below cutoff. Default: ${DEFAULT-VALUE}. " +
                    "Valid values: ${COMPLETION-CANDIDATES}",
            arity = "1")
    MethylationDataFilter.CutoffType cutoffType = MethylationDataFilter.CutoffType.upper;

    /**
     * Run method of filter subcommand, runs when user passes subcommand filter.
     * Checks are performed on argument input and input file is filtered on those arguments if they are valid
     * (removing rows/columns or individual beta values), output  file is then written to output path.
     */
    @Override
    public void run() {

        VerbosityLevelProcessor verbosityLevelProcessor = new VerbosityLevelProcessor();
        try {
            verbosityLevelProcessor.applyVerbosity(verbosity.verbose);
        } catch (IllegalArgumentException ex) {
            return;

        }

        MethylationArray data = null;

        if (sampleIndex.sampleIndex > 1) {
            try {
                data = CommandLineParser.readFile(filePathInput, sampleIndex.sampleIndex - 1);
            } catch (Exception ex) {
                return;
            }
        } else {
            logger.error("Invalid sample index: '{}'. Please provide the number that marks the start of the sample " +
                    "columns, using -si [index].", sampleIndex.sampleIndex);
        }

        // Make new MethylationArray object (copy of MethylationArray generated by MethylationFileReader),
        // to store filtered values
        MethylationArray filteredData = new MethylationArray();

        if (data != null) {
            filteredData.setSampleIndex(data.getSampleIndex());
            filteredData.setHeader(data.getHeader(), data.getSampleIndex());
            filteredData.setSamples(data.getSamples());
            filteredData.setData(data.getData());
            filteredData.setIndexInformation(data.getIndexInformation());

        } else {
            return;
        }
        // List to hold methods to be run based on user arguments
        List<Runnable> filtersToRun = new ArrayList<>();
        CompositeUserArgumentsCheck checker = new CompositeUserArgumentsCheck();

        if (naRemover.removeNa) {
            MethylationDataFilter.removeNA(filteredData);
        }

        try {

            if (sampleInput.samples != null) {
                SampleArgumentCheck sampleArgumentCheck = new SampleArgumentCheck(sampleInput.samples, data);
                checker.addFilter(sampleArgumentCheck);

                filtersToRun.add(() -> MethylationDataFilter.filterBySample(filteredData, sampleInput.samples));

            }

            if (posArguments != null && posArguments.chr != null) {
                MethylationDataFilter.PosFilterType posFilterType = MethylationDataFilter.PosFilterType.CHROMOSOME;

                String[] chromosomes = Arrays.stream(posArguments.chr)
                        .map(String::toUpperCase)
                        .toArray(String[]::new);

                ChrArgumentCheck chrArgumentCheck = new ChrArgumentCheck(chromosomes, data);
                checker.addFilter(chrArgumentCheck);

                filtersToRun.add(() -> MethylationDataFilter.filterByPos(filteredData, posFilterType, chromosomes));

            } else if (posArguments != null && posArguments.genes != null) {
                MethylationDataFilter.PosFilterType posFilterType = MethylationDataFilter.PosFilterType.GENE;

                // Convert to uppercase, so that gene is still recognized if user passes it in lowercase
                String[] genes = Arrays.stream(posArguments.genes)
                        .map(String::toUpperCase)
                        .toArray(String[]::new);

                GeneArgumentCheck geneArgumentCheck = new GeneArgumentCheck(genes, data);
                checker.addFilter(geneArgumentCheck);

                filtersToRun.add(() -> MethylationDataFilter.filterByPos(filteredData, posFilterType, genes));

            }

            // Cutoff filter is always ran with a default of 0.0 and 'hyper' for direction, as it cannot be checked for
            // being 'null', like the other arguments
            CutOffArgumentCheck cutOffArgumentCheck = new CutOffArgumentCheck(cutoff);
            checker.addFilter(cutOffArgumentCheck);

            if (cutoff != 0.0) {
                filtersToRun.add(() -> MethylationDataFilter.filterByCutOff(filteredData, cutoff, cutoffType));
            }


            if (checker.pass()) {
                // Run all filters for which the user has passed arguments
                for (Runnable filter : filtersToRun) {
                    filter.run();
                }
            }
        } catch (IllegalArgumentException ex) {
            return;
        }

        try {
            FilterFileWriter.writeFile(filteredData, filePathOutput.outputFilePath);
        } catch (IOException ex) {
        }
    }

    /**
     * Class to make PosArguments gene/chr mutually exclusive as gene locations are tied to chromosome
     */
    static class PosArguments {
        @Option(names = {"-chr", "--chromosome"},
                description = "Positional argument to filter data on @|bold,underline one or more|@ chromosomes",
                arity = "1..*")
        String[] chr;
        @Option(names = {"-g", "--gene"},
                description = "Positional argument to filter data on @|bold,underline one or more|@ genes",
                arity = "1..*")
        String[] genes;
    }
}

/**
 * Subcommand for compare, takes file, allows user to compare samples using statistical methods.
 */
@Command(name = "compare",
        description = "Compare two or more samples/regions",
        version = "Current version of BVAL: 0.0.1",
        mixinStandardHelpOptions = true)
class Compare implements Runnable {

    private static final Logger logger = LogManager.getLogger();
    @Mixin
    FilePathInput filePathInput;
    @Mixin
    Verbosity verbosity;
    @Mixin
    FilePathOutput filePathOutput;
    @Mixin
    NaRemover naRemover;
    @ArgGroup()
    PosArguments posArguments;
    @Mixin
    SampleIndex sampleIndex;

    @Option(names = {"-s", "--sample"},
            description = "Name(s) of the sample(s) to compare with each other. default value: all samples in the file",
            arity = "2..*")
    String[] samples;

    @Spec
    CommandSpec spec;
    @Option(names = {"-m", "--methods"},
            defaultValue = "t-test,spearman,wilcoxon-test,welch-test",
            split = ",",
            description = "Name(s) of the different statistic methods to perform on the data. " +
                    "Default value: ${DEFAULT-VALUE} (all tests are performed by default). ",
            arity = "1..*")
    String[] methods;

    private void validateMethodInput() {

        for (String method : methods) {
            if (!ValidMethods.isValid(method)) {
                throw new ParameterException(spec.commandLine(),
                        String.format("Invalid value '%s' for option '--methods'. Valid values: %s",
                                method, String.join(", ", ValidMethods.validNames())));
            }
        }
    }

    /**
     * Run method of compare subcommand, runs when user passes compare filter.
     * Input arguments are validated, statistical methods are applied based on user input and output is written to
     * output path.
     */
    @Override
    public void run() {
        VerbosityLevelProcessor verbosityLevelProcessor = new VerbosityLevelProcessor();

        try {
            verbosityLevelProcessor.applyVerbosity(verbosity.verbose);
        } catch (IllegalArgumentException ex) {
            return;
        }

        validateMethodInput();
        MethylationArray data = null;

        if (sampleIndex.sampleIndex > 0) {
            try {
                data = CommandLineParser.readFile(filePathInput, sampleIndex.sampleIndex - 1);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            logger.error("Invalid sample index: '{}'. Please provide the number that marks the start of the sample " +
                    "columns, using -si [index].", sampleIndex.sampleIndex);
        }

        if (samples == null & data != null) {
            samples = data.getSamples().toArray(String[]::new);
        }

        ComparisonResults corrData = null;
        MethylationArray filteredData;

        if (posArguments != null & data != null) {
            filteredData = new MethylationArray();
            filteredData.setSampleIndex(data.getSampleIndex());
            filteredData.setHeader(data.getHeader(), data.getSampleIndex());
            filteredData.setSamples(data.getSamples());
            filteredData.setData(data.getData());
            filteredData.setIndexInformation(data.getIndexInformation());
            MethylationDataFilter.PosFilterType posFilterType;

            CompositeUserArgumentsCheck checker = new CompositeUserArgumentsCheck();

            if (naRemover.removeNa) {
                MethylationDataFilter.removeNA(filteredData);
            }

            try {
                if (posArguments.chr != null) {
                    String[] chromosomes = Arrays.stream(posArguments.chr)
                            .map(String::toUpperCase)
                            .toArray(String[]::new);

                    ChrArgumentCheck chrArgumentCheck = new ChrArgumentCheck(chromosomes, data);
                    checker.addFilter(chrArgumentCheck);
                    posFilterType = MethylationDataFilter.PosFilterType.CHROMOSOME;


                    if (checker.pass()) {
                        MethylationDataFilter.filterByPos(filteredData, posFilterType, chromosomes);
                        MethylationDataFilter.filterBySample(filteredData, samples);
                        corrData = new MethylationArrayPosComparer(filteredData, methods, posFilterType,
                                chromosomes).performStatisticalMethods();
                    }
                } else if (posArguments.genes != null) {
                    String[] genes = Arrays.stream(posArguments.genes)
                            .map(String::toUpperCase)
                            .toArray(String[]::new);

                    GeneArgumentCheck geneArgumentCheck = new GeneArgumentCheck(genes, data);
                    checker.addFilter(geneArgumentCheck);
                    posFilterType = MethylationDataFilter.PosFilterType.GENE;

                    if (checker.pass()) {

                        MethylationDataFilter.filterByPos(filteredData, posFilterType, genes);
                        MethylationDataFilter.filterBySample(filteredData, samples);
                        corrData = new MethylationArrayPosComparer(filteredData, methods, posFilterType,
                                genes).performStatisticalMethods();
                    }
                }
            } catch (IllegalArgumentException ex) {
                return;
            }

        } else {
            try {
                corrData = new MethylationArraySampleComparer(data, samples, methods).performStatisticalMethods();
            } catch (IllegalArgumentException ex) {
                return;
            }
        }

        try {
            new ComparisonFileWriter(corrData, filePathOutput.outputFilePath).writeData();
        } catch (IOException ex) {
        }
    }

    enum ValidMethods {
        TTEST("t-test"),
        SPEARMAN("spearman"),
        WILCOXONTEST("wilcoxon-test"),
        WELCH("welch-test");

        private final String name;

        ValidMethods(String name) {
            this.name = name;
        }

        public static boolean isValid(String value) {
            return Arrays.stream(values()).anyMatch(v -> v.name.equals(value));
        }

        public static List<String> validNames() {
            return Arrays.stream(values())
                    .map(ValidMethods::getName)
                    .toList();
        }

        public String getName() {
            return name;
        }
    }

    static class PosArguments {
        @Option(names = {"-chr", "--chromosome"},
                description = "Positional argument to filter data on @|bold,underline one or more|@ chromosomes",
                arity = "2..*")
        String[] chr;
        @Option(names = {"-g", "--gene"},
                description = "Positional argument to filter data on @|bold,underline one or more|@ genes",
                arity = "2..*")
        String[] genes;
    }
}