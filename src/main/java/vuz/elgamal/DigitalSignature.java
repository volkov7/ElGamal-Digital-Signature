package vuz.elgamal;

import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;

public class DigitalSignature {

    private static final int BIT_LENGTH = 512;
    private static final int ROUNDS = 512;
    private static final String ELGAMAL_PUB = "elgamal.pub";
    private static final String ELGAMAL = "elgamal";

    private BigInteger p;
    private BigInteger g;
    private BigInteger y;
    private BigInteger x;
    private BigInteger q;

    /**
     * Create two files in current directory:
     * 1) elgamal.pub - public key
     * 2) elgamal - private key
     */
    public void generatePublicAndPrivateKeys() {
        generateSafePrimeP();
        findGeneratorInMultiplicativeGroup();
        x = PrimeUtils.generateBigIntegerInRange(BigInteger.ONE, p.subtract(BigInteger.ONE));
        y = g.modPow(x, p);
        writeKeyToFile(p, g, y, ELGAMAL_PUB);
        writeKeyToFile(p, g, x, ELGAMAL);
    }

    /**
     * Safe prime: p = 2q + 1, where q is prime
     */
    private void generateSafePrimeP() {
        do {
            q = PrimeUtils.generatePrimeNum(BIT_LENGTH - 1);
            p = q.multiply(BigInteger.TWO).add(BigInteger.ONE);
        } while (!PrimeUtils.isPrime(p, ROUNDS));
    }

    /**
     * 1) generate random h in [2, p - 1]
     * 2) g = h^{ (p - 1) / q } mod p
     * Since we using safe prime p = 2q + 1 -> g = h^{ ((p - 1) * 2) / (p - 1) } -> g = h^2 mod p
     * 3) if g != 1 -> generator founded
     */
    private void findGeneratorInMultiplicativeGroup() {
        do {
            BigInteger h = PrimeUtils.generateBigIntegerInRange(BigInteger.TWO, p.subtract(BigInteger.ONE));
            g = h.modPow(BigInteger.TWO, p);
        } while (BigInteger.ONE.equals(g));
    }

    private void writeKeyToFile(BigInteger p, BigInteger g, BigInteger key, String fileName) {
        StringBuilder builder = new StringBuilder();

        builder.append(String.format("p: %d\n", p));
        builder.append(String.format("g: %d\n", g));
        if (ELGAMAL.equals(fileName)) {
            builder.append(String.format("x: %d\n", key));
        } else {
            builder.append(String.format("y: %d\n", key));
        }

        try {
            FileWriter writer = new FileWriter(fileName);
            writer.write(builder.toString());
            writer.close();
        } catch (IOException e) {
            System.out.printf("Something goes wrong while creating file %s: %s%n", fileName, e.getMessage());
        }
    }
}
