package ru.otus;

import static ru.otus.Action.REPEAT;
import static ru.otus.Action.STEP;

public class NumbersSequence {

    public static void main(String ...args) {
        var numberPrinter = new NumberPrinter(1, 10, 50);
        new Thread(() -> numberPrinter.makeStepAndPrint(STEP), "thd-1").start();
        new Thread(() -> numberPrinter.makeStepAndPrint(REPEAT), "thd-2").start();
    }

}
