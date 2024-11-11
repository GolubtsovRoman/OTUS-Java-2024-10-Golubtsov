package ru.otus.java.dev.pro;

import ru.otus.java.dev.pro.service.HelloOtus;

public class Main {

    public static void main(String[] args) {
        long b = 2;
        int k = 10;

        System.out.println("=".repeat(20));
        System.out.printf("%d pow to %d equal %d%n", b, k, new HelloOtus(b, k).callGuava());
        System.out.println("=".repeat(20));
    }

}
