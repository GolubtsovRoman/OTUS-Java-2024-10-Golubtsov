package ru.otus.java.dev.pro;

public enum Denomination {

    EU_5 (5),
    EU_10 (10),
    EU_20 (20),
    EU_50 (50),
    EU_100 (100),
    EU_200 (200),
    EU_500 (500);

    private final Integer num;

    Denomination(Integer num) {
        if (num == null) {
            throw new IllegalArgumentException();
        }
        this.num = num;
    }

    public Integer getNum() {
        return num;
    }

}
