package vuz.elgamal;

import java.math.BigInteger;
import java.util.Random;

public class PrimeUtils {

    public static final BigInteger generatePrimeNum(int bitLength) {
        return BigInteger.probablePrime(bitLength, new Random());
    }

    /**
     * This method using Miller-Rabin probabilistic primality test.
     * n - 1 = 2^s * t
     *
     * @param n prime check number.
     * @param rounds number of rounds.
     * @return true - prime, false - composite.
     */
    public static final Boolean isPrime(BigInteger n, int rounds) {
        int s = findS(n.subtract(BigInteger.ONE));
        BigInteger t = n.divide(BigInteger.TWO.pow(s));
        BigInteger a;
        BigInteger y;

        for (int i = 0; i < rounds; i++) {
            a = generatePrimeInRange(BigInteger.TWO, n.subtract(BigInteger.TWO));
            y = a.modPow(t, n);
            if (!y.equals(BigInteger.ONE) && !y.equals(n.subtract(BigInteger.ONE))) {
                int j = 1;
                while (j < s && !y.equals(n.subtract(BigInteger.ONE))) {
                    y = y.modPow(BigInteger.TWO, n);
                    if (y.equals(BigInteger.ONE)) {
                        return Boolean.FALSE;
                    }
                    j++;
                }
                if (!y.equals(n.subtract(BigInteger.ONE))) {
                    return Boolean.FALSE;
                }
            }
        }
        return Boolean.TRUE;
    }

    /**
     * return exponent of 2 such as n = 2^s * x.
     *
     * @param n input number.
     * @return exponent
     */
    private static int findS(BigInteger n) {
        int s = 0;

        while (BigInteger.ZERO.equals(n.remainder(BigInteger.TWO))) {
            n = n.divide(BigInteger.TWO);
            s++;
        }
        return s;
    }

    private static BigInteger generatePrimeInRange(BigInteger min, BigInteger max) {
        BigInteger diff = max.subtract(min);
        BigInteger randomValue = new BigInteger(max.bitLength(), new Random());

        if (randomValue.compareTo(min) < 0) {
            randomValue = randomValue.add(min);
        } else if (randomValue.compareTo(max) > 0) {
            randomValue = randomValue.mod(diff).add(min);
        }
        return randomValue;
    }
}
