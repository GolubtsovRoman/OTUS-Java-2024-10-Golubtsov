package ru.otus.java.dev.pro;

import ru.otus.java.dev.pro.aop.proxy.Ioc;
import ru.otus.java.dev.pro.service.SmokeService;

public class Main {

    public static void main(String[] args) {
        SmokeService smoker = Ioc.createSmokeService();
        smoker.smoke();
        smoker.smoke(1);
        smoker.smoke(1, 2L);
        smoker.smoke(1, "two");
        smoker.noSmoke(100500);
    }

}