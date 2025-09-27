package nl.bioinf;

import java.io.IOException;
import picocli.CommandLine;


public class Main {
    public static void main(String[] args) throws IOException {

        CommandLine cmd = new CommandLine(new CommandLineParser())
                .setColorScheme(CommandLine.Help.defaultColorScheme(CommandLine.Help.Ansi.ON));

        int exitCode = cmd.execute(args);
        System.exit(exitCode);

        }

}


