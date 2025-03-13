package ru.otus;

import static ru.otus.Action.REPEAT;

public class NumberPrinter {

    private final int begin;
    private final int end;

    private final int maxStep;
    private int counter = 1;

    private int current;
    private boolean needInc = true;
    private boolean needChange = false;

    private Action lastAction = REPEAT;

    public NumberPrinter(int begin, int end, int maxStep) {
        if (begin >= end) {
            throw new IllegalArgumentException("Left value can't be more or equals end value");
        }
        if (maxStep < 2) {
            throw new IllegalArgumentException("maxStep value can't be less 2");
        }
        this.begin = begin;
        this.end = end;
        this.current = begin;
        this.maxStep = maxStep;
    }

    public synchronized void makeStepAndPrint(Action action) {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                while (lastAction == action) {
                    this.wait();
                }

                make();
                lastAction = action;
                notifyAll();
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void make() {
        if (current == begin) {
            needInc = true;
        }
        if (current == end) {
            needInc = false;
        }

        if (needChange) {
            if (needInc) {
                print(current++);
            } else {
                print(current--);
            }
        } else {
            print(current);
        }
        needChange = !needChange;
    }

    private void print(int val) {
        if (counter <= maxStep) {
            System.out.println("Step " + counter + " : " + Thread.currentThread().getName() + " : " + val);
            counter++;
        } else {
            Thread.currentThread().interrupt();
        }
    }

}
