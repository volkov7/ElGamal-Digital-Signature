package vuz.elgamal;

import org.junit.Test;
import vuz.elgamal.utils.PrimeUtils;

import java.math.BigInteger;

import static org.junit.Assert.assertEquals;

public class PrimeUtilsTest {

    @Test
    public void isPrimeTestSimplePrime() {
        BigInteger simplePrime = new BigInteger("409");
        assertEquals(Boolean.TRUE, PrimeUtils.isPrime(simplePrime, 10));
    }

    /*
        rounds = 256 because we check 256 bit prime.
     */
    @Test
    public void isPrimeTest256BitPrime() {
        BigInteger prime = new BigInteger("9889948815488436816384279237429048107101076749293300249261929871340230212059029980727015514604209277241674619203119627712871406905497819926494136501920562791237509891649175990984634829780582020406098700551046971371347511006218984196165261746963272966735509");
        assertEquals(Boolean.TRUE, PrimeUtils.isPrime(prime, 256));
    }

    @Test
    public void isPrimeTestSimpleComposite() {
        BigInteger simpleComposite = new BigInteger("1717");
        assertEquals(Boolean.FALSE, PrimeUtils.isPrime(simpleComposite, 11));
    }

    /*
        rounds = 256 because we check 256 bit prime.
     */
    @Test
    public void isPrimeTest256BitComposite() {
        BigInteger prime = new BigInteger("9889948815488436816384279237429048107101076749293300249261929871340230212059029980727015514604209277241674619203119627712871406905497819926494136501920562791237509891649175990984634829780582020406098700551046971371347511006218984196165261746963272966735506");
        assertEquals(Boolean.FALSE, PrimeUtils.isPrime(prime, 256));
    }

    @Test
    public void generateBigIntegerInRangeTest() {
        BigInteger max = new BigInteger("500");
        BigInteger min = new BigInteger("100");
        BigInteger n = PrimeUtils.generateBigIntegerInRange(min, max);

        assertEquals(Boolean.TRUE, n.compareTo(min) > 0 && n.compareTo(max) < 0);
    }
}