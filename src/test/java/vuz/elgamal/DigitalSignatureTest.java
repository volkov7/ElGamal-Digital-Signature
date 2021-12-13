package vuz.elgamal;

import org.junit.Before;
import org.junit.Test;
import vuz.elgamal.exceptions.FileCorruptedOrFalsify;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class DigitalSignatureTest {

    public static final String FILE_NAME = "src/test/java/vuz/elgamal/data/test.txt";
    public static final String FILE_NAME_CORRUPTED = "src/test/java/vuz/elgamal/data/testCorrupted.txt";
    public static final byte[] MESSAGE = {0x6d,0x65,0x73,0x73,0x61,0x67,0x65}; // message
    private static final String ELGAMAL_PUB = "elgamal.pub";
    private static final String ELGAMAL = "elgamal";
    private static final String ELGAMAL_PUB_CORRUPTED = "src/test/java/vuz/elgamal/data/elgamal.pub";
    private static final String ELGAMAL_CORRUPTED = "src/test/java/vuz/elgamal/data/elgamal";

    private final DigitalSignature digitalSignature = new DigitalSignature();

    @Before
    public void init() throws IOException {
        createTestData();
    }

    @Test
    public void generateSignVerifyNoThrowsExceptionTest() throws FileCorruptedOrFalsify {
        digitalSignature.generatePublicAndPrivateKeys();
        digitalSignature.signFile(FILE_NAME, ELGAMAL);
        digitalSignature.verifySign(FILE_NAME, ELGAMAL_PUB);
    }

    @Test(expected = FileCorruptedOrFalsify.class)
    public void verifyCorruptedAndThrowExceptionTest() throws FileCorruptedOrFalsify {
        digitalSignature.verifySign(FILE_NAME_CORRUPTED, ELGAMAL_PUB_CORRUPTED);
    }

    private void createTestData() throws IOException {
        File file = new File(FILE_NAME);
        file.createNewFile();
        FileOutputStream fileOutputStream = new FileOutputStream(FILE_NAME);
        fileOutputStream.write(MESSAGE);
        fileOutputStream.close();
    }
}