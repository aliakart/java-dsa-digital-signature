package org.krypto;

import java.math.BigInteger;

public class Signature {
    public final BigInteger s1;
    public final BigInteger s2;

    public Signature(BigInteger s1, BigInteger s2) {
        this.s1 = s1;
        this.s2 = s2;
    }
}
