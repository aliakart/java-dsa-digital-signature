package org.krypto;

import java.math.BigInteger;


public class SystemParams {
    public final BigInteger p;
    public final BigInteger q;
    public final BigInteger h;

    public SystemParams(BigInteger p, BigInteger q, BigInteger h) {
        this.p = p;
        this.q = q;
        this.h = h;
    }
}
