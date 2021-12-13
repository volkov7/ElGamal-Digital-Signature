package vuz.elgamal;

import vuz.elgamal.exceptions.FileCorruptedOrFalsify;

import java.util.Map;

public class Params {

    public static final byte A = (byte) 0xaa;
    public static final byte B = (byte) 0xbb;
    public static final byte M = (byte) 0xcc;
    public static final byte P = (byte) 0xdd;
    public static final byte G = (byte) 0xee;
    public static final byte X = (byte) 0xff;
    public static final byte Y = (byte) 0xab;

    private static final Map<Byte, String> mapping = Map.of(
        A, "A",
        B, "B",
        M, "M",
        P, "P",
        G, "G",
        X, "X",
        Y, "Y"
    );

    public static String mapByteToString(byte b) throws FileCorruptedOrFalsify {
        String param = mapping.get(b);

        if (param == null) {
            throw new FileCorruptedOrFalsify("Digital signature corrupted!");
        }
        return param;
    }
}
