package vuz.elgamal;

import org.junit.Test;
import vuz.elgamal.exceptions.FileCorruptedOrFalsify;
import vuz.elgamal.utils.BinaryUtils;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class BinaryUtilsTest {

    @Test
    public void wrapToSignatureTest() {
        byte var = (byte) 0xff;
        byte[] expected = {(byte) 0xde, (byte) 0xad, (byte) 0xbe, (byte) 0xef,
                            var,
                            (byte) 0xaa, (byte) 0xab, (byte) 0xac,
                            (byte) 0xde, (byte) 0xad, (byte) 0xbe, (byte) 0xef};
        byte[] actual = BinaryUtils.wrapToSignature(new byte[]{(byte) 0xaa, (byte) 0xab, (byte) 0xac}, var);
        assertEquals(Boolean.TRUE, Arrays.equals(expected, actual));
    }

    @Test
    public void convertBytesToBigIntegerTest() {
        BigInteger expected = new BigInteger("77492214967865460963471699751820070564084647936440628989923671088450960649501");
        byte[] byteRepresentation = new byte[]{
            -85, 83, 10, 19, -28, 89, 20, -104, 43, 121, -7, -73, -29, -5, -87, -108, -49, -47, -13, -5, 34, -9, 28, -22, 26, -5, -16, 43, 70, 12, 109, 29
        };
        assertEquals(expected, BinaryUtils.convertBytesToBigInteger(byteRepresentation));
    }

    @Test
    public void convertBigIntegerToBytesTest() {
        BigInteger n = new BigInteger("77492214967865460963471699751820070564084647936440628989923671088450960649501");
        byte[] expected = new byte[]{
            -85, 83, 10, 19, -28, 89, 20, -104, 43, 121, -7, -73, -29, -5, -87, -108, -49, -47, -13, -5, 34, -9, 28, -22, 26, -5, -16, 43, 70, 12, 109, 29
        };
        assertEquals(Boolean.TRUE, Arrays.equals(expected, BinaryUtils.convertBigIntegerToBytes(n)));
    }

    @Test
    public void joinByteArraysTest() throws IOException {
        byte[] a = {(byte) 0xaa, (byte) 0xaa, (byte) 0xaa};
        byte[] b = {(byte) 0xbb, (byte) 0xbb, (byte) 0xbb};
        byte[] expected = {(byte) 0xaa, (byte) 0xaa, (byte) 0xaa, (byte) 0xbb, (byte) 0xbb, (byte) 0xbb};
        assertEquals(Boolean.TRUE, Arrays.equals(expected, BinaryUtils.joinByteArrays(a, b)));
    }

    @Test
    public void bytesToHexStringTest() {
        String expected = "ab530a13e45914982b79f9b7e3fba994cfd1f3fb22f71cea1afbf02b460c6d1d";
        byte[] byteRepresentation = new byte[]{
            -85, 83, 10, 19, -28, 89, 20, -104, 43, 121, -7, -73, -29, -5, -87, -108, -49, -47, -13, -5, 34, -9, 28, -22, 26, -5, -16, 43, 70, 12, 109, 29
        };
        assertEquals(expected, BinaryUtils.bytesToHexString(byteRepresentation));
    }

    @Test
    public void indexOfSignatureTest() {
        byte[] data = {(byte) 0xaa, (byte) 0xab, (byte) 0xba, (byte) 0xcc, (byte) 0xff, (byte) 0xaa, (byte) 0xae};
        int expected = 3;

        int actual = BinaryUtils.indexOfSignature(data, new byte[]{(byte) 0xcc, (byte) 0xff});
        assertEquals(expected, actual);
    }

    @Test
    public void parseParamsTest() throws FileCorruptedOrFalsify {
        byte[] data = {
            (byte) 0xaa, (byte) 0xab, (byte) 0xba, (byte) 0xcc, (byte) 0xff, (byte) 0xaa, (byte) 0xae,
            (byte) 0xde, (byte) 0xad, (byte) 0xbe, (byte) 0xef, Params.P, 0x5, 0x39, (byte) 0xde, (byte) 0xad, (byte) 0xbe, (byte) 0xef,
            (byte) 0xde, (byte) 0xad, (byte) 0xbe, (byte) 0xef, Params.G, 0x4, -0x2b, (byte) 0xde, (byte) 0xad, (byte) 0xbe, (byte) 0xef
        };
        Map<String, BigInteger> expected = Map.of("P", new BigInteger("1337"), "G", new BigInteger("1237"));

        Map<String, BigInteger> actual = BinaryUtils.parseParams(data);
        assertEquals(expected, actual);
    }
}