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
import java.nio.file.AccessDeniedException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.*;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

// Reusable option for filepath
class FilePathInput {
    @Option(names = {"-f", "--file"},
            description = "Path to file containing the data",
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
    private static final Logger logger = LogManager.getLogger(Summary.class.getName());
    @Mixin
    FilePathInput filePathInput;

    @Override
    public void run() {

        try {
            MethylationFileReader.readCSV(filePathInput.filePath);

        } catch (NoSuchFileException ex) {
            logger.error("""
                    Failed to find the provided file: '{}'.\s
                    Exception occurred: '{}'.\s
                    Please check whether the correct file path was given.""",
                    ex.getMessage(), ex);
            System.exit(0);

        } catch (AccessDeniedException ex) {
            logger.error("""
                     Permission denied to open the provided file: '{}'.\s
                     Exception occurred: '{}'.\s
                     Please make sure the provided path is not a directory and that the file has appropriate permissions.""",
                    ex.getMessage(), ex);
            System.exit(0);

        } catch(IOException ex){
            logger.error("""
                    Unexpected IO error for provided file: '{}'.\s
                    Exception occurred: '{}'.\s
                    Please check the provided file path""",
                    ex.getMessage(), ex);
            System.exit(0);
        }

        MethylationArray data = MethylationFileReader.getData();
        SummaryGenerator.generateSummary(data);
    }
}

// Filter use-case, takes file, allows user to filter and returns overview to user
@Command(name = "filter",
        description = "@|bold Takes methylation beta value file and allows for filtering based on samples/chromosomes/genes/cutoff.|@",
        mixinStandardHelpOptions = true)
class Filter implements Runnable {
    private static final Logger logger = LogManager.getLogger(Filter.class.getName());

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

        MethylationArray methylationData = null;
        
        try {
            MethylationFileReader.readCSV(filePathInput.filePath);
            methylationData = MethylationFileReader.getData();

        } catch (NoSuchFileException ex) {
            logger.error("""
                    Failed to find the provided file: '{}'.\s
                    Exception occurred: '{}'.\s
                    Please check whether the correct file path was given.""",
                    ex.getMessage(), ex);
            System.exit(0);

        } catch (AccessDeniedException ex) {
            logger.error("""
                     Permission denied to open the provided file: '{}'.\s
                     Exception occurred: '{}'.\s
                     Please make sure the provided path is not a directory and that the file has appropriate permissions.""",
                     ex.getMessage(), ex);
            System.exit(0);

        } catch(IOException ex){
            logger.error("""
                    Unexpected IO error for provided file: '{}'.\s
                    Exception occurred: '{}'.\s
                    Please check the provided file path""",
                    ex.getMessage(), ex);
            System.exit(0);
        }
    
        // Make new MethylationArray object to store filtered values in and set same samples
        MethylationArray methylationArray = new MethylationArray();
        assert methylationData != null;
        methylationArray.setSamples(methylationData.getSamples());
        methylationArray.setData(methylationData.getData());

        if (sampleInput.samples != null){
            GeneArgumentCheck geneArgumentCheck = new GeneArgumentCheck(sampleInput.samples);
            if (geneArgumentCheck.pass(methylationData)){
                methylationArray = MethylationDataFilter.filterBySample(methylationData, sampleInput.samples);
            }
        }

        if (posArguments != null && posArguments.chr != null ) {
            ChrArgumentCheck chrArgumentCheck = new ChrArgumentCheck(posArguments.chr);
            if (chrArgumentCheck.pass(methylationData)){
                MethylationDataFilter.filterByChr(methylationArray, posArguments.chr);
            }
        }
        else if (posArguments != null && posArguments.genes != null ) {
            GeneArgumentCheck geneArgumentCheck = new GeneArgumentCheck(posArguments.genes);
            if (geneArgumentCheck.pass(methylationData)){
                MethylationDataFilter.filterByGene(methylationArray, posArguments.genes);
            }
        }

        // Cutoff filter is always ran with a default of 0.0 and 'hyper' for direction
        CutOffArgumentCheck cutOffArgumentCheck = new CutOffArgumentCheck(cutoff, cutoffType);
        if (cutOffArgumentCheck.pass(methylationData)){
            MethylationDataFilter.filterByCutOff(methylationArray, cutoff, cutoffType);
        }

        try {
            FilterFileWriter.writeData(methylationArray);
        } catch(IOException ex){
            logger.error("""
                    Unexpected IO error when writing to file: '{}'.\s
                    Exception occurred: '{}'.
                    """,
                    ex.getMessage(), ex);
            System.exit(0);
        }
    }
}

// Compare use-case, compares 2 or more samples/regions
@Command(name = "compare",
        description = "Compare two or more samples/regions",
        mixinStandardHelpOptions = true)
class Compare implements Runnable {
    private static final Logger logger = LogManager.getLogger(Compare.class.getName());

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
        try {
            MethylationFileReader.readCSV(filePathInput.filePath);
        } catch (NoSuchFileException ex) {
            logger.error("""
                    Failed to find the provided file: '{}'.\s
                    Exception occurred: '{}'.\s
                    Please check whether the correct file path was given.""",
                    ex.getMessage(), ex);
            System.exit(0);

        } catch (AccessDeniedException ex) {
            logger.error("""
                     Permission denied to open the provided file: '{}'.\s
                     Exception occurred: '{}'.\s
                     Please make sure the provided path is not a directory and that the file has appropriate permissions.""",
                    ex.getMessage(), ex);
            System.exit(0);

        } catch(IOException ex){
            logger.error("""
                    Unexpected IO error for provided file: '{}'.\s
                    Exception occurred: '{}'.\s
                    Please check the provided file path""",
                    ex.getMessage(), ex);
            System.exit(0);
        }

        MethylationArray data = MethylationFileReader.getData();
        SampleComparison corrData = MethylationArraySampleComparer.performStatisticalMethods(data, sampleInput.samples, methods);
        System.out.println(corrData);

        try {
            ComparingFileWriter.writeData(corrData);
        } catch(IOException ex){
            logger.error("""
                    Unexpected IO error when writing to file: '{}'.\s
                    Exception occurred: '{}'.
                    """,
                    ex.getMessage(), ex);
            System.exit(0);
        }
    }
}