package nl.bioinf;

import java.io.IOException;
import picocli.CommandLine;


public class Main {
    public static void main(String[] args) throws IOException {

        int exitCode = new CommandLine(new CommandLineParser()).execute(args);
        System.exit(exitCode);

        }

}


