package vuz.elgamal;

public class Main {
    public static void main(String[] args) {
        DigitalSignature digitalSignature = new DigitalSignature();
        digitalSignature.generatePublicAndPrivateKeys();
    }
}
