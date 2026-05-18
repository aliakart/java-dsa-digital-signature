package org.krypto;

import java.math.BigInteger;

public class KeyPair {
    public final BigInteger a;
    public final BigInteger b;

    public KeyPair(BigInteger a, BigInteger b) {
        this.a = a;
        this.b = b;
    }
}