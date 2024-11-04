package ru.otus.java.dev.pro.service;

import com.google.common.math.LongMath;

public class HelloOtus {

    long b;
    int k;

    public HelloOtus(long b, int k) {
        this.b = b;
        this.k = k;
    }

    public long callGuava() {
        return LongMath.pow(b, k);
    }

}
