package vuz.elgamal;

import vuz.elgamal.exceptions.FileCorruptedOrFalsify;
import vuz.elgamal.hash.Stribog256;
import vuz.elgamal.utils.BinaryUtils;
import vuz.elgamal.utils.PrimeUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.util.Map;

public class DigitalSignature {

    private static final int BIT_LENGTH = 512;
    private static final int ROUNDS = 512;
    private static final String ELGAMAL_PUB = "elgamal.pub";
    private static final String ELGAMAL = "elgamal";

    /**
     * Create two files in current directory:
     * 1) elgamal.pub - public key
     * 2) elgamal - private key
     */
    public void generatePublicAndPrivateKeys() {
        BigInteger p = generateSafePrimeP();
        BigInteger g = findGeneratorInMultiplicativeGroup(p.subtract(BigInteger.ONE));
        BigInteger x = PrimeUtils.generateBigIntegerInRange(BigInteger.ONE, p.subtract(BigInteger.ONE));
        BigInteger y = g.modPow(x, p);
        writeKeyToFile(p, g, y, ELGAMAL_PUB);
        writeKeyToFile(p, g, x, ELGAMAL);
    }

    /**
     * Adding sign to a file.
     * @param fileName file to sign.
     * @param privateKey file with private key.
     */
    public void signFile(String fileName, String privateKey) throws FileCorruptedOrFalsify {
        try {
            byte[] message = readAllFile(fileName);
            byte[] privateKeyData = readAllFile(privateKey);
            Map<String, BigInteger> params = BinaryUtils.parseParams(privateKeyData);

            byte[] signedMsg = getSignedMessage(message, params.get("P"), params.get("G"), params.get("X"));

            FileOutputStream fileOutputStream = new FileOutputStream(fileName);
            fileOutputStream.write(signedMsg);
            fileOutputStream.close();
        } catch (IOException e) {
            System.err.printf("Something went wrong: %s", e.getMessage());
        }
    }

    /**
     * Verify sign.
     *
     * @param signedFile name of signed file.
     * @param publicKey name of file with public key.
     */
    public void verifySign(String signedFile, String publicKey) throws FileCorruptedOrFalsify {
        byte[] data = readAllFile(signedFile);
        byte[] keyData = readAllFile(publicKey);
        byte[] message = BinaryUtils.parseMessage(data);
        Map<String, BigInteger> params = BinaryUtils.parseParams(data);

        params.putAll(BinaryUtils.parseParams(keyData));

        BigInteger f1 = calculateF1(message, params.get("G"), params.get("P"));
        BigInteger f2 = calculateF2(params.get("Y"), params.get("A"), params.get("B"), params.get("P"));
        if (!f1.equals(f2)) {
            throw new FileCorruptedOrFalsify("File corrupted or falsify!");
        }
    }

    private byte[] readAllFile(String fileName) {
        byte[] data;

        try {
            File file = new File(fileName);
            data = Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            System.err.printf("Something went wrong: %s", e.getMessage());
            data = null;
        }
        return data;
    }

    private byte[] getSignedMessage(byte[] message, BigInteger p, BigInteger g, BigInteger x) throws IOException {
        byte[] sign = createSign(message, p, g, x);
        return BinaryUtils.joinByteArrays(message, sign);
    }

    private byte[] createSign(byte[] message, BigInteger p, BigInteger g, BigInteger x) throws IOException {
        byte[] hash = getMessageHash(message);
        BigInteger hashInt = BinaryUtils.convertBytesToBigInteger(hash);

        BigInteger r = generateRelativePrime(p.subtract(BigInteger.ONE));
        BigInteger a = g.modPow(r, p);
        BigInteger b = calculateB(hashInt, p.subtract(BigInteger.ONE), r, x, a);
        return BinaryUtils.joinByteArrays(
            BinaryUtils.wrapToSignature(BinaryUtils.convertBigIntegerToBytes(a), Params.A),
            BinaryUtils.wrapToSignature(BinaryUtils.convertBigIntegerToBytes(b), Params.B)
        );
    }

    private byte[] getMessageHash(byte[] message) {
        Stribog256 stribog256 = new Stribog256();
        stribog256.fillBuffer(message);
        return stribog256.getHash();
    }

    private BigInteger calculateB(BigInteger hashInt, BigInteger p, BigInteger r, BigInteger x, BigInteger a) {
        BigInteger[] coefficients = gcdExtended(r, p);
        BigInteger reverseR = coefficients[1].mod(p);
        BigInteger diff = hashInt.subtract(x.multiply(a)).mod(p);
        return diff.multiply(reverseR).mod(p);
    }

    /**
     * Safe prime: p = 2q + 1, where q is prime
     */
    private BigInteger generateSafePrimeP() {
        BigInteger p;

        do {
            BigInteger q = PrimeUtils.generatePrimeNum(BIT_LENGTH - 1);
            p = q.multiply(BigInteger.TWO).add(BigInteger.ONE);
        } while (!PrimeUtils.isPrime(p, ROUNDS));
        return p;
    }

    /**
     * 1) generate random alpha in [2, p - 1]
     * 2) g = alpha^{ (p - 1) / q } mod p
     * Since we are using safe prime p = 2q + 1 -> g = alpha^{ ((p - 1) * 2) / (p - 1) } -> g = alpha^2 mod p && g = alpha^q mod p
     * 3) if b1 != 1 && b2 != 1 -> generator founded
     */
    private BigInteger findGeneratorInMultiplicativeGroup(BigInteger p) {
        BigInteger alpha;
        BigInteger b1;
        BigInteger b2;
        BigInteger q = p.divide(BigInteger.TWO);

        do {
            alpha = PrimeUtils.generateBigIntegerInRange(BigInteger.TWO, p.subtract(BigInteger.ONE));
            b1 = alpha.modPow(BigInteger.TWO, p);
            b2 = alpha.modPow(q, p);
        } while (BigInteger.ONE.equals(b1) || BigInteger.ONE.equals(b2));
        return alpha;
    }

    private void writeKeyToFile(BigInteger p, BigInteger g, BigInteger key, String fileName) {
        byte[] byteP = BinaryUtils.wrapToSignature(BinaryUtils.convertBigIntegerToBytes(p), Params.P);
        byte[] byteG = BinaryUtils.wrapToSignature(BinaryUtils.convertBigIntegerToBytes(g), Params.G);
        byte[] byteKey;

        if (ELGAMAL.equals(fileName)) {
            byteKey = BinaryUtils.wrapToSignature(BinaryUtils.convertBigIntegerToBytes(key), Params.X);
        } else {
            byteKey = BinaryUtils.wrapToSignature(BinaryUtils.convertBigIntegerToBytes(key), Params.Y);
        }

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(fileName);
            fileOutputStream.write(BinaryUtils.joinByteArrays(byteP, byteG, byteKey));
            fileOutputStream.close();
        } catch (IOException e) {
            System.err.printf("Something goes wrong while creating file %s: %s%n", fileName, e.getMessage());
        }
    }

    private BigInteger generateRelativePrime(BigInteger n) {
        BigInteger r;
        do {
            r = PrimeUtils.generateBigIntegerInRange(BigInteger.ONE, n.subtract(BigInteger.ONE));
        } while (!r.gcd(n).equals(BigInteger.ONE));
        return r;
    }

    /*
        f1 = g^(hash) mod p
     */
    private BigInteger calculateF1(byte[] message, BigInteger g, BigInteger p) {
        byte[] hash = getMessageHash(message);
        BigInteger hashInt = BinaryUtils.convertBytesToBigInteger(hash);
        return g.modPow(hashInt, p);
    }

    /*
        f2 = (y^a * a^b) mod p
     */
    private BigInteger calculateF2(BigInteger y, BigInteger a, BigInteger b, BigInteger p) {
        BigInteger ya = y.modPow(a, p);
        BigInteger ab = a.modPow(b, p);
        return ya.multiply(ab).mod(p);
    }

    /*
        return array [d, a, b] such that d = gcd(p, q), ap + bq = d
     */
    private BigInteger[] gcdExtended(BigInteger p, BigInteger q) {
        if (q.equals(BigInteger.ZERO)) {
            return new BigInteger[] {p, BigInteger.ONE, BigInteger.ZERO};
        }

        BigInteger[] values = gcdExtended(q, p.mod(q));
        BigInteger d = values[0];
        BigInteger a = values[2];
        BigInteger b = values[1].subtract(p.divide(q).multiply(values[2]));
        return new BigInteger[] {d, a, b};
    }
}
