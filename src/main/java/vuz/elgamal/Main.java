package vuz.elgamal;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class Main {

    private static final String HEADER = "Example: java -jar elgamal-1.0-jar-with-dependencies.jar -s -m file.txt -sk elgamal";

    public static void main(String[] args) {

        ElGamal elGamal = new ElGamal();
        CommandLineParser parser = new DefaultParser();
        CommandLine line = null;
        Options options = new Options();

        options.addOption("g", "generate-keys", false, "generate 512-bit public and private keys in the current directory");
        options.addOption("s", "sign", false, "sign the file with a private key");
        options.addOption("v", "verify", false, "verify digital signature");
        options.addOption(
            Option.builder("pk")
                    .argName("file")
                    .hasArg()
                    .desc("name of the file with public key")
                    .build()
        );
        options.addOption(
            Option.builder("m")
                    .argName("file")
                    .hasArg()
                    .desc("name of the file to sign")
                    .build()
        );
        options.addOption(
            Option.builder("sk")
                    .argName("file")
                    .hasArg()
                    .desc("name of the file with private key")
                    .build()
        );

        try {
            line = parser.parse(options, args);
        } catch (ParseException e) {
            System.err.println("Parsing failed.  Reason: " + e.getMessage());
            System.exit(123);
        }

        if (args.length == 0) {
            HelpFormatter helpFormatter = new HelpFormatter();
            helpFormatter.setWidth(120);
            helpFormatter.printHelp("java -jar elgamal-1.0-jar-with-dependencies.jar [OPTION]... [FILE]...", HEADER, options, null);
        } else {
            elGamal.processArgs(line);
        }
    }
}
