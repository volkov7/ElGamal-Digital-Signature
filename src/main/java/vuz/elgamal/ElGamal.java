package vuz.elgamal;

import org.apache.commons.cli.CommandLine;
import vuz.elgamal.exceptions.FileCorruptedOrFalsify;

public class ElGamal {

    private final DigitalSignature digitalSignature = new DigitalSignature();

    public void processArgs(CommandLine line) {
        if (line.hasOption("g")) {
            digitalSignature.generatePublicAndPrivateKeys();
            System.out.println("Private and public keys are generated!");
        } else if (line.hasOption("s") && line.hasOption("sk") && line.hasOption("m")) {
            try {
                digitalSignature.signFile(line.getOptionValue("m"), line.getOptionValue("sk"));
                System.out.println("File signed!");
            } catch (FileCorruptedOrFalsify fileCorruptedOrFalsify) {
                System.err.println(fileCorruptedOrFalsify.getMessage());
            }
        } else if (line.hasOption("v") && line.hasOption("pk") && line.hasOption("m")) {
            try {
                digitalSignature.verifySign(line.getOptionValue("m"), line.getOptionValue("pk"));
                System.out.println("Sign valid!");
            } catch (FileCorruptedOrFalsify fileCorruptedOrFalsify) {
                System.err.println("File corrupted or falsify!");
            }
        } else {
            System.err.println("Arguments are not enough or they are entered incorrectly");
        }
    }
}
