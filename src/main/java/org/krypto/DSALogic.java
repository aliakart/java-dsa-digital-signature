package org.krypto;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.SecureRandom;




public class DSALogic {

    private static final SecureRandom random = new SecureRandom();

    public static BigInteger f(byte[] messageBytes) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(messageBytes);
        return new BigInteger(1, hashBytes);
    }

    public static SystemParams generateParameters() {
        BigInteger q = new BigInteger(160, 100, random);
        BigInteger p;
        BigInteger k;

        do {
            k = new BigInteger(576 - 160, random);
            p = k.multiply(q).add(BigInteger.ONE);
        } while (!p.isProbablePrime(100));

        BigInteger h;
        do {
            BigInteger g = new BigInteger(p.bitLength() - 1, random).add(BigInteger.TWO);
            h = g.modPow(k, p);
        } while (h.compareTo(BigInteger.ONE) <= 0);

        return new SystemParams(p, q, h);
    }

    public static KeyPair generateKeys(SystemParams params) {
        BigInteger a;
        do {
            a = new BigInteger(160, random);
        } while (a.compareTo(BigInteger.ZERO) <= 0 || a.compareTo(params.q) >= 0);

        BigInteger b = params.h.modPow(a, params.p);
        return new KeyPair(a, b);
    }

    public static Signature signMessage(byte[] M, SystemParams params, BigInteger a) throws Exception {
        BigInteger q = params.q;
        BigInteger p = params.p;
        BigInteger h = params.h;

        BigInteger r;
        do {
            r = new BigInteger(160, random);
        } while (r.compareTo(BigInteger.ZERO) <= 0 || r.compareTo(q) >= 0);

        BigInteger rPrime = r.modInverse(q);
        BigInteger s1 = h.modPow(r, p).mod(q);

        BigInteger fM = f(M);
        BigInteger a_s1 = a.multiply(s1);
        BigInteger sum = fM.add(a_s1);
        BigInteger s2 = rPrime.multiply(sum).mod(q);

        return new Signature(s1, s2);
    }

    public static boolean verifySignature(byte[] M, Signature sig, SystemParams params, BigInteger b) throws Exception {
        BigInteger q = params.q;
        BigInteger p = params.p;
        BigInteger h = params.h;
        BigInteger s1 = sig.s1;
        BigInteger s2 = sig.s2;

        if (s1.compareTo(BigInteger.ZERO) <= 0 || s1.compareTo(q) >= 0 ||
                s2.compareTo(BigInteger.ZERO) <= 0 || s2.compareTo(q) >= 0) {
            return false;
        }

        BigInteger sPrime = s2.modInverse(q);
        BigInteger fM = f(M);
        BigInteger u1 = fM.multiply(sPrime).mod(q);
        BigInteger u2 = sPrime.multiply(s1).mod(q);

        BigInteger h_u1 = h.modPow(u1, p);
        BigInteger b_u2 = b.modPow(u2, p);
        BigInteger t = h_u1.multiply(b_u2).mod(p).mod(q);

        return t.equals(s1);
    }
}