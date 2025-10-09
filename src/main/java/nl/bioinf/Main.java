package nl.bioinf;

import nl.bioinf.utils.CommandLineParser;
import picocli.CommandLine;


public class Main {
    public static void main(String[] args) {

        CommandLine cmd = new CommandLine(new CommandLineParser())
                .setColorScheme(CommandLine.Help.defaultColorScheme(CommandLine.Help.Ansi.ON));

        int exitCode = cmd.execute(args);
        System.exit(exitCode);

        }
}


