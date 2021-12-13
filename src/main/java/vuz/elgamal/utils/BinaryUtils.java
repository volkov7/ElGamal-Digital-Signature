package vuz.elgamal.utils;

import vuz.elgamal.Params;
import vuz.elgamal.exceptions.FileCorruptedOrFalsify;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class BinaryUtils {

    private static final byte[] SIGNATURE = new byte[]{(byte) 0xde, (byte) 0xad, (byte) 0xbe, (byte) 0xef};

    public static byte[] wrapToSignature(byte[] bytes, byte var) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            outputStream.write(SIGNATURE);
            outputStream.write(var);
            outputStream.write(bytes);
            outputStream.write(SIGNATURE);
        } catch (IOException e) {
            System.out.printf("Something goes wrong while writing to stream %s%n", e.getMessage());
        }
        return outputStream.toByteArray();
    }

    /**
     * Always convert bytes to positive big integer.
     *
     * @param bytes to convert
     * @return positive big integer.
     */
    public static BigInteger convertBytesToBigInteger(byte[] bytes) {
        byte[] positiveBytes = new byte[bytes.length + 1];
        positiveBytes[0] = 0x0;
        System.arraycopy(bytes, 0, positiveBytes, 1, bytes.length);
        return new BigInteger(positiveBytes);
    }

    /**
     * Remove first byte because we always work with positive integers.
     *
     * @param n number to convert.
     * @return byte representation.
     */
    public static byte[] convertBigIntegerToBytes(BigInteger n) {
        byte[] bytes = n.toByteArray();
        return bytes[0] == 0 ? Arrays.copyOfRange(bytes, 1, bytes.length) : bytes;
    }

    public static byte[] joinByteArrays(byte[]... bytes) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );

        for (byte[] byteArray : bytes) {
            if (byteArray != null) {
                outputStream.write(byteArray);
            }
        }
        return outputStream.toByteArray();
    }

    public static String bytesToHexString(byte[] bytes) {
        StringBuilder hexString = new StringBuilder(2 * bytes.length);
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    /**
     * Return message without signature.
     *
     * @param data message with signature.
     * @return bytes message.
     */
    public static byte[] parseMessage(byte[] data) throws FileCorruptedOrFalsify {
        int endOfMessage = indexOfSignature(data, SIGNATURE);

        if (endOfMessage == -1) {
            throw new FileCorruptedOrFalsify("File corrupted or falsify!");
        }
        return Arrays.copyOfRange(data, 0, endOfMessage);
    }

    /**
     * Parsing byte array with signature.
     * Example: deadbeef | p | value | deadbeef | deadbeef | g | value | deadbeef
     *
     * @param data byte array.
     * @return map with parsed params.
     */
    public static Map<String, BigInteger> parseParams(byte[] data) throws FileCorruptedOrFalsify {
        Map<String, byte[]> params = new HashMap<>();
        byte[] copy = Arrays.copyOfRange(data, 0, data.length);
        int start;
        int end = 0;

        while (end + SIGNATURE.length < copy.length) {
            start = indexOfSignature(copy, SIGNATURE);
            if (start == -1) {
                break;
            }
            start += SIGNATURE.length;
            byte param = copy[start++];
            end = indexOfSignature(Arrays.copyOfRange(copy, start, copy.length), SIGNATURE);
            params.put(Params.mapByteToString(param), Arrays.copyOfRange(copy, start, start + end));
            copy = Arrays.copyOfRange(copy, start + end + SIGNATURE.length, copy.length);
        }
        return convert(params);
    }

    /**
     * Knuth-Morris-Pratt Algorithm for Pattern Matching.
     * Finds the first occurrence of the pattern in the text.
     *
     * @param data where searching pattern.
     * @param pattern to search.
     * @return index of first occurrence or -1 if not found.
     */
    public static int indexOfSignature(byte[] data, byte[] pattern) {
        if (data.length == 0) return -1;

        int[] failure = computeFailure(pattern);
        int j = 0;

        for (int i = 0; i < data.length; i++) {
            while (j > 0 && pattern[j] != data[i]) {
                j = failure[j - 1];
            }
            if (pattern[j] == data[i]) { j++; }
            if (j == pattern.length) {
                return i - pattern.length + 1;
            }
        }
        return -1;
    }

    /**
     * Computes the failure function using a boot-strapping process,
     * where the pattern is matched against itself.
     */
    private static int[] computeFailure(byte[] pattern) {
        int[] failure = new int[pattern.length];

        int j = 0;
        for (int i = 1; i < pattern.length; i++) {
            while (j > 0 && pattern[j] != pattern[i]) {
                j = failure[j - 1];
            }
            if (pattern[j] == pattern[i]) {
                j++;
            }
            failure[i] = j;
        }
        return failure;
    }

    private static Map<String, BigInteger> convert(Map<String, byte[]> params) {
        Map<String, BigInteger> fresh = new HashMap<>();

        for (Map.Entry<String, byte[]> entry : params.entrySet()) {
            String key = entry.getKey();
            byte[] value = entry.getValue();
            fresh.put(key, BinaryUtils.convertBytesToBigInteger(value));
        }
        return fresh;
    }
}
