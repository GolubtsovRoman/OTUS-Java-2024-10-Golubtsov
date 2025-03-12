package ru.otus;

import static ru.otus.Action.REPEAT;

public class NumberPrinter {

    private final int left;
    private final int right;

    private final int maxStep;
    private int counter = 1;

    private int current;
    private boolean needInc = true;
    private boolean needChange = false;

    private Action lastAction = REPEAT;

    public NumberPrinter(int left, int right, int maxStep) {
        if (left >= right) {
            throw new IllegalArgumentException("Left value can't be more or equals right value");
        }
        if (maxStep < 2) {
            throw new IllegalArgumentException("maxStep value can't be less 2");
        }
        this.left = left;
        this.right = right;
        this.current = left;
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
        if (current == left) {
            needInc = true;
        }
        if (current == right) {
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
